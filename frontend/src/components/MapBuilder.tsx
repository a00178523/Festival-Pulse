import { useState, useRef } from 'react';
import { Stage, Layer, Line, Circle, Text } from 'react-konva';
import { PenLine, Check, X } from 'lucide-react';
import type { Point, FestivalArea } from '../types';
import { getRandomColor } from '../utils/colors';
import { useStore } from '../store';
import { MapBackground } from './MapBackground';

interface MapBuilderProps {
  onAreaComplete: (area: FestivalArea) => void;
}

const AREA_TYPES = ['Stage', 'Food & Drink', 'Wellness', 'Medical', 'Campsite', 'Entrance', 'Other'];

export const MapBuilder = ({ onAreaComplete }: MapBuilderProps) => {
  const [points, setPoints]         = useState<Point[]>([]);
  const [isDrawing, setIsDrawing]   = useState(false);
  const [showModal, setShowModal]   = useState(false);
  const [areaName, setAreaName]     = useState('');
  const [areaType, setAreaType]     = useState('Stage');
  const stageRef = useRef<any>(null);
  const { areas } = useStore();

  const handleStageClick = (e: any) => {
    if (!isDrawing) return;
    const stage = e.target.getStage();
    const point = stage.getPointerPosition();
    setPoints(prev => [...prev, { x: point.x, y: point.y }]);
  };

  const handleCompleteArea = () => {
    if (points.length < 3) return;
    setShowModal(true);
  };

  const handleModalConfirm = () => {
    if (!areaName.trim()) return;
    const newArea: FestivalArea = {
      name: areaName.trim(),
      areaType,
      coordinates: points,
      baseColor: getRandomColor(),
    };
    onAreaComplete(newArea);
    setPoints([]);
    setIsDrawing(false);
    setShowModal(false);
    setAreaName('');
    setAreaType('Stage');
  };

  const handleModalCancel = () => {
    setShowModal(false);
    setAreaName('');
    setAreaType('Stage');
  };

  const flattenPoints = (pts: Point[]) => pts.flatMap(p => [p.x, p.y]);

  return (
    <div style={{ position: 'relative' }}>

      {/* Toolbar */}
      <div style={{ position: 'absolute', top: 20, left: 20, zIndex: 10, display: 'flex', gap: '10px' }}>
        {!isDrawing ? (
          <button
            onClick={() => { setIsDrawing(true); setPoints([]); }}
            className="btn btn-primary"
            style={{ fontSize: '14px', padding: '10px 20px', display: 'flex', alignItems: 'center', gap: '8px' }}
          >
            <PenLine size={16} /> Draw New Area
          </button>
        ) : (
          <>
            <button
              onClick={handleCompleteArea}
              disabled={points.length < 3}
              style={{
                padding: '10px 20px',
                background: points.length >= 3 ? 'linear-gradient(135deg, #06FFA5, #00F5FF)' : 'rgba(255,255,255,0.1)',
                color: points.length >= 3 ? '#0a0014' : 'rgba(255,255,255,0.4)',
                border: 'none',
                borderRadius: '10px',
                cursor: points.length >= 3 ? 'pointer' : 'not-allowed',
                fontWeight: 'bold',
                fontSize: '14px',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                fontFamily: 'Outfit, sans-serif',
              }}
            >
              <Check size={16} /> Complete Area
            </button>
            <button
              onClick={() => { setPoints([]); setIsDrawing(false); }}
              className="btn btn-secondary"
              style={{ fontSize: '14px', padding: '10px 20px', display: 'flex', alignItems: 'center', gap: '8px' }}
            >
              <X size={16} /> Cancel
            </button>
          </>
        )}
      </div>

      <Stage
        ref={stageRef}
        width={1200}
        height={800}
        onClick={handleStageClick}
        style={{
          border: '2px solid rgba(255, 255, 255, 0.1)',
          borderRadius: '20px',
          overflow: 'hidden',
          cursor: isDrawing ? 'crosshair' : 'default',
        }}
      >
        <MapBackground />

        <Layer>
          {/* Existing areas */}
          {areas.map((area, idx) => {
            if (!area.coordinates || area.coordinates.length === 0) return null;
            const cx = area.coordinates.reduce((s, p) => s + p.x, 0) / area.coordinates.length;
            const cy = area.coordinates.reduce((s, p) => s + p.y, 0) / area.coordinates.length;
            return (
              <>
                <Line
                  key={idx}
                  points={flattenPoints(area.coordinates)}
                  closed
                  fill="rgba(255,255,255,0.25)"
                  stroke="rgba(255,255,255,0.5)"
                  strokeWidth={2}
                />
                <Text
                  key={`lbl-${idx}`}
                  x={cx - 55} y={cy - 10}
                  text={area.name}
                  fontSize={13}
                  fontStyle="bold"
                  fill="#ffffff"
                  width={110}
                  align="center"
                  shadowColor="#000000"
                  shadowBlur={6}
                  shadowOpacity={0.9}
                />
              </>
            );
          })}

          {/* Current drawing */}
          {points.length > 0 && (
            <>
              <Line
                points={flattenPoints(points)}
                stroke="#00F5FF"
                strokeWidth={2.5}
                lineCap="round"
                lineJoin="round"
              />
              {points.map((pt, i) => (
                <Circle
                  key={i} x={pt.x} y={pt.y}
                  radius={5}
                  fill="#00F5FF"
                  stroke="#ffffff"
                  strokeWidth={2}
                />
              ))}
            </>
          )}
        </Layer>
      </Stage>

      {/* Drawing hint */}
      {isDrawing && (
        <div style={{
          position: 'absolute', bottom: 20, left: '50%', transform: 'translateX(-50%)',
          background: 'rgba(0,245,255,0.9)', color: '#0a0014',
          padding: '10px 22px', borderRadius: '10px',
          fontWeight: 700, fontSize: '14px', fontFamily: 'Outfit, sans-serif',
          pointerEvents: 'none',
        }}>
          Click to place points · {points.length} placed{points.length >= 3 ? ' · Click Complete when done' : ' · Need at least 3'}
        </div>
      )}

      {/* Area details modal */}
      {showModal && (
        <div className="modal show" onClick={handleModalCancel}>
          <div className="modal-content" style={{ maxWidth: '440px' }} onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontFamily: 'Bebas Neue', fontSize: '1.8rem', letterSpacing: '0.05em' }}>
                Name This Area
              </h2>
              <button className="modal-close" onClick={handleModalCancel}>×</button>
            </div>

            <div className="form" style={{ gap: '1.25rem' }}>
              <div className="form-group">
                <label>Area Name</label>
                <input
                  type="text"
                  value={areaName}
                  onChange={e => setAreaName(e.target.value)}
                  placeholder="e.g. Main Stage"
                  autoFocus
                  onKeyDown={e => e.key === 'Enter' && handleModalConfirm()}
                />
              </div>

              <div className="form-group">
                <label>Area Type</label>
                <select
                  className="report-select"
                  value={areaType}
                  onChange={e => setAreaType(e.target.value)}
                >
                  {AREA_TYPES.map(t => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', gap: '1rem' }}>
                <button className="btn btn-secondary" onClick={handleModalCancel} style={{ flex: 1 }}>
                  Cancel
                </button>
                <button
                  className="btn btn-primary"
                  onClick={handleModalConfirm}
                  disabled={!areaName.trim()}
                  style={{ flex: 1, opacity: !areaName.trim() ? 0.5 : 1 }}
                >
                  <Check size={16} style={{ display: 'inline', marginRight: '6px', verticalAlign: 'middle' }} />
                  Save Area
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
