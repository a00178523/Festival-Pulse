import { useState, useRef } from 'react';
import { Stage, Layer, Line, Circle, Text } from 'react-konva';
import type { Point, FestivalArea } from '../types';
import { getRandomColor } from '../utils/colors';
import { useStore } from '../store';

interface MapBuilderProps {
  onAreaComplete: (area: FestivalArea) => void;
}

export const MapBuilder = ({ onAreaComplete }: MapBuilderProps) => {
  const [points, setPoints] = useState<Point[]>([]);
  const [isDrawing, setIsDrawing] = useState(false);
  const stageRef = useRef<any>(null);
  const { areas } = useStore();

  const handleStageClick = (e: any) => {
    if (!isDrawing) return;

    const stage = e.target.getStage();
    const point = stage.getPointerPosition();
    
    setPoints([...points, { x: point.x, y: point.y }]);
  };

  const handleStartDrawing = () => {
    setIsDrawing(true);
    setPoints([]);
  };

  const handleCompleteArea = () => {
    if (points.length < 3) {
      alert('Draw at least 3 points to create an area');
      return;
    }

    const areaName = prompt('Enter area name:');
    if (!areaName) return;

    const areaType = prompt('Enter area type (STAGE/FOOD/AMENITY/ENTRANCE):') || 'STAGE';

    const newArea: FestivalArea = {
      name: areaName,
      areaType: areaType.toUpperCase(),
      coordinates: points,
      baseColor: getRandomColor(),
    };

    onAreaComplete(newArea);
    setPoints([]);
    setIsDrawing(false);
  };

  const handleCancelDrawing = () => {
    setPoints([]);
    setIsDrawing(false);
  };

  const flattenPoints = (pts: Point[]) => {
    return pts.flatMap(p => [p.x, p.y]);
  };

  return (
    <div style={{ position: 'relative' }}>
      <div style={{ 
        position: 'absolute', 
        top: 20, 
        left: 20, 
        zIndex: 10,
        display: 'flex',
        gap: '10px'
      }}>
        {!isDrawing ? (
          <button 
            onClick={handleStartDrawing}
            style={{
              padding: '12px 24px',
              background: 'linear-gradient(135deg, #FF006E, #8338EC)',
              color: 'white',
              border: 'none',
              borderRadius: '10px',
              cursor: 'pointer',
              fontWeight: 'bold',
              fontSize: '14px',
            }}
          >
            ✏️ Draw New Area
          </button>
        ) : (
          <>
            <button 
              onClick={handleCompleteArea}
              style={{
                padding: '12px 24px',
                background: 'linear-gradient(135deg, #06FFA5, #00F5FF)',
                color: '#0a0014',
                border: 'none',
                borderRadius: '10px',
                cursor: 'pointer',
                fontWeight: 'bold',
                fontSize: '14px',
              }}
            >
              ✓ Complete Area
            </button>
            <button 
              onClick={handleCancelDrawing}
              style={{
                padding: '12px 24px',
                background: 'rgba(255, 255, 255, 0.2)',
                color: 'white',
                border: '2px solid rgba(255, 255, 255, 0.3)',
                borderRadius: '10px',
                cursor: 'pointer',
                fontWeight: 'bold',
                fontSize: '14px',
              }}
            >
              ✕ Cancel
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
          background: 'linear-gradient(135deg, #0a0014 0%, #1a0033 50%, #2d0052 100%)',
          border: '2px solid rgba(255, 255, 255, 0.1)',
          borderRadius: '20px',
        }}
      >
        <Layer>
          {/* Render existing areas */}
          {areas.map((area, idx) => (
            area.coordinates && area.coordinates.length > 0 && (
              <Line
                key={idx}
                points={flattenPoints(area.coordinates)}
                closed
                fill={area.baseColor}
                opacity={0.6}
                stroke={area.baseColor}
                strokeWidth={3}
              />
            )
          ))}

          {/* Render current drawing */}
          {points.length > 0 && (
            <>
              <Line
                points={flattenPoints(points)}
                stroke="#00F5FF"
                strokeWidth={3}
                lineCap="round"
                lineJoin="round"
              />
              {points.map((point, i) => (
                <Circle
                  key={i}
                  x={point.x}
                  y={point.y}
                  radius={6}
                  fill="#00F5FF"
                  stroke="#FFFFFF"
                  strokeWidth={2}
                />
              ))}
            </>
          )}

          {/* Render area labels */}
          {areas.map((area, idx) => {
            if (!area.coordinates || area.coordinates.length === 0) return null;
            
            const centerX = area.coordinates.reduce((sum, p) => sum + p.x, 0) / area.coordinates.length;
            const centerY = area.coordinates.reduce((sum, p) => sum + p.y, 0) / area.coordinates.length;
            
            return (
              <Text
                key={`label-${idx}`}
                x={centerX - 50}
                y={centerY - 10}
                text={area.name}
                fontSize={16}
                fontStyle="bold"
                fill="#FFFFFF"
                width={100}
                align="center"
              />
            );
          })}
        </Layer>
      </Stage>

      {isDrawing && (
        <div style={{
          position: 'absolute',
          bottom: 20,
          left: '50%',
          transform: 'translateX(-50%)',
          background: 'rgba(0, 245, 255, 0.9)',
          color: '#0a0014',
          padding: '12px 24px',
          borderRadius: '10px',
          fontWeight: 'bold',
        }}>
          Click to add points. Complete when done.
        </div>
      )}
    </div>
  );
};
