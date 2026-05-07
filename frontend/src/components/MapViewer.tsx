import React, { useRef, useState, useEffect } from 'react';
import { Stage, Layer, Line, Text, Circle } from 'react-konva';
import type { FestivalArea } from '../types';
import { CROWD_COLORS } from '../utils/colors';
import { useStore } from '../store';
import { api } from '../api';
import { MapBackground } from './MapBackground';

const W = 1200;
const H = 800;

export const MapViewer = () => {
  const { areas, alerts, setSelectedArea, setSelectedReport } = useStore();
  const stageRef = useRef<any>(null);
  const [flashOn, setFlashOn] = useState(true);

  useEffect(() => {
    const interval = setInterval(() => setFlashOn(f => !f), 600);
    return () => clearInterval(interval);
  }, []);

  const handleAreaClick = async (area: FestivalArea) => {
    setSelectedArea(area);
    if (!area.id) return;
    try {
      // Get the festival id from the area object
      const festivalId = (area as any).festival?.id;
      if (!festivalId) return;
      const reports = await api.getReports(festivalId);
      const latest = reports.find((r: any) => r.area.id === area.id);
      if (latest) setSelectedReport(latest);
    } catch (e) {
      console.error('Failed to fetch area report', e);
    }
  };

  const flattenPoints = (pts: any[]) => pts.flatMap(p => [p.x, p.y]);

  const getAreaColor = (area: FestivalArea) => {
    const hasAlert = alerts.some(alert => alert.area.id === area.id);
    if (hasAlert) return flashOn ? CROWD_COLORS.ALERT : 'rgba(255,255,255,0.15)';
    if (area.crowdLevel === 'FULL')   return CROWD_COLORS.FULL;
    if (area.crowdLevel === 'MEDIUM') return CROWD_COLORS.MEDIUM;
    if (area.crowdLevel === 'LOW')    return CROWD_COLORS.LOW;
    return 'rgba(255,255,255,0.25)'; // no report yet — neutral
  };

  const getAreaStroke = (area: FestivalArea, hasAlert: boolean) => {
    if (hasAlert) return flashOn ? '#FF006E' : 'rgba(255,255,255,0.3)';
    if (area.crowdLevel === 'FULL')   return 'rgba(255,84,0,0.8)';
    if (area.crowdLevel === 'MEDIUM') return 'rgba(255,190,11,0.8)';
    if (area.crowdLevel === 'LOW')    return 'rgba(6,255,165,0.8)';
    return 'rgba(255,255,255,0.3)';
  };

  return (
    <div style={{ position: 'relative' }}>
      <Stage
        ref={stageRef}
        width={W}
        height={H}
        style={{
          border: '2px solid rgba(255, 255, 255, 0.1)',
          borderRadius: '20px',
          overflow: 'hidden',
        }}
      >
        {/* ── Background layer ── */}
        <MapBackground />

        {/* ── Areas layer ── */}
        <Layer>
          {areas.map((area, idx) => {
            if (!area.coordinates || area.coordinates.length === 0) return null;

            const hasAlert = alerts.some(alert => alert.area.id === area.id);
            const cx = area.coordinates.reduce((sum, p) => sum + p.x, 0) / area.coordinates.length;
            const cy = area.coordinates.reduce((sum, p) => sum + p.y, 0) / area.coordinates.length;

            return (
              <React.Fragment key={idx}>
                {/* Single fill driven by crowd level */}
                <Line
                  points={flattenPoints(area.coordinates)}
                  closed
                  fill={getAreaColor(area)}
                  opacity={hasAlert ? (flashOn ? 0.92 : 0.2) : 0.88}
                  stroke={getAreaStroke(area, hasAlert)}
                  strokeWidth={hasAlert ? 3 : 1.5}
                  onClick={() => handleAreaClick(area)}
                  onTap={() => handleAreaClick(area)}
                  shadowColor={hasAlert ? '#FF006E' : undefined}
                  shadowBlur={hasAlert ? (flashOn ? 25 : 0) : 0}
                  shadowOpacity={hasAlert ? 1 : 0}
                  style={{ cursor: 'pointer' }}
                />

                {/* Area label */}
                <Text
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

                {/* Alert marker — solid red dot */}
                {hasAlert && (
                  <Circle
                    x={cx} y={cy + 20}
                    radius={7}
                    fill="#FF006E"
                    shadowColor="#FF006E"
                    shadowBlur={12}
                    shadowOpacity={1}
                  />
                )}
              </React.Fragment>
            );
          })}
        </Layer>
      </Stage>

      {/* Legend */}
      <div style={{
        position: 'absolute',
        top: 20, right: 20,
        background: 'rgba(10, 0, 20, 0.85)',
        backdropFilter: 'blur(20px)',
        border: '1px solid rgba(255,255,255,0.15)',
        borderRadius: '14px',
        padding: '16px 20px',
        color: 'white',
        minWidth: '130px',
      }}>
        <h3 style={{ margin: '0 0 12px 0', fontSize: '13px', fontWeight: 700, letterSpacing: '0.1em', textTransform: 'uppercase', color: '#b8b8ff' }}>
          Crowd Level
        </h3>
        {([
          ['LOW',    CROWD_COLORS.LOW],
          ['MEDIUM', CROWD_COLORS.MEDIUM],
          ['FULL',   CROWD_COLORS.FULL],
          ['ALERT',  CROWD_COLORS.ALERT],
        ] as [string, string][]).map(([label, color]) => (
          <div key={label} style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
            <div style={{
              width: '28px', height: '16px',
              background: color,
              borderRadius: '4px',
              border: '1px solid rgba(255,255,255,0.2)',
              boxShadow: label === 'ALERT' ? '0 0 8px rgba(255,0,110,0.6)' : 'none',
            }} />
            <span style={{ fontSize: '13px', fontWeight: 600 }}>{label}</span>
          </div>
        ))}
      </div>
    </div>
  );
};
