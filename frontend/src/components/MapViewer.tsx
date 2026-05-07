import React, { useRef, useState, useEffect } from 'react';
import { Stage, Layer, Line, Text, Circle, Ellipse, Rect } from 'react-konva';
import type { FestivalArea } from '../types';
import { CROWD_COLORS, hexToRgba } from '../utils/colors';
import { useStore } from '../store';

const W = 1200;
const H = 800;

// Perimeter trees: [x, y, outerR, innerR, color1, color2]
const TREES: [number, number, number, number, string, string][] = [
  // Top edge
  [60,  40,  38, 22, '#2d6e1a', '#4a9e2a'],
  [160, 25,  32, 18, '#3a7a20', '#5aae35'],
  [280, 45,  40, 24, '#256015', '#3d8e22'],
  [420, 20,  34, 20, '#2d6e1a', '#4a9e2a'],
  [560, 38,  36, 21, '#3a7a20', '#52a030'],
  [700, 22,  38, 23, '#256015', '#3d8e22'],
  [840, 42,  33, 19, '#2d6e1a', '#4a9e2a'],
  [980, 28,  37, 22, '#3a7a20', '#5aae35'],
  [1100,44,  35, 20, '#256015', '#3d8e22'],
  // Bottom edge
  [50,  760, 40, 24, '#2d6e1a', '#4a9e2a'],
  [180, 775, 34, 20, '#3a7a20', '#5aae35'],
  [320, 758, 38, 22, '#256015', '#3d8e22'],
  [460, 772, 32, 18, '#2d6e1a', '#4a9e2a'],
  [600, 760, 36, 21, '#3a7a20', '#52a030'],
  [740, 778, 40, 24, '#256015', '#3d8e22'],
  [880, 762, 33, 19, '#2d6e1a', '#4a9e2a'],
  [1020,775, 37, 22, '#3a7a20', '#5aae35'],
  [1140,758, 35, 20, '#256015', '#3d8e22'],
  // Left edge
  [35,  140, 36, 21, '#2d6e1a', '#4a9e2a'],
  [28,  280, 40, 24, '#3a7a20', '#5aae35'],
  [40,  420, 34, 20, '#256015', '#3d8e22'],
  [30,  560, 38, 22, '#2d6e1a', '#4a9e2a'],
  [42,  680, 32, 18, '#3a7a20', '#52a030'],
  // Right edge
  [1165,140, 36, 21, '#256015', '#3d8e22'],
  [1172,280, 40, 24, '#2d6e1a', '#4a9e2a'],
  [1160,420, 34, 20, '#3a7a20', '#5aae35'],
  [1168,560, 38, 22, '#256015', '#3d8e22'],
  [1158,680, 33, 19, '#2d6e1a', '#4a9e2a'],
];

// Stones scattered around perimeter area: [x, y, rx, ry, rotation]
const STONES: [number, number, number, number, number][] = [
  [110, 55,  9, 6,  20], [240, 35,  7, 5,  -10], [370, 50,  8, 5,  15],
  [500, 30,  6, 4,   5], [630, 48,  9, 6,  -20], [760, 32,  7, 5,  10],
  [900, 46,  8, 5,  -15],[1050,38,  6, 4,   25], [1130,55,  9, 6,  -5],
  [55,  200, 7, 5,  10], [45,  350, 8, 6,  -20],[38,  490, 6, 4,  15],
  [50,  630, 9, 5,  -10],[1155,200, 7, 5,  20], [1162,350, 8, 6,  -5],
  [1150,490, 6, 4,  10], [1160,630, 9, 5, -15],
  [120, 770, 8, 5,  15], [260, 762, 7, 4,  -5],[400, 775, 9, 6,  20],
  [540, 765, 6, 4, -10], [680, 772, 8, 5,  10],[820, 760, 7, 5, -20],
  [960, 775, 9, 6,   5],[1080,762, 6, 4,  15],
];

// Grass contour blobs: [x, y, rx, ry, color]
const GRASS_BLOBS: [number, number, number, number, string][] = [
  [200, 180, 180, 120, '#7ec850'],
  [700, 150, 220, 100, '#6db840'],
  [950, 400, 160, 130, '#72c248'],
  [300, 580, 200, 110, '#6db840'],
  [600, 620, 180, 100, '#7ec850'],
  [100, 400, 130,  90, '#72c248'],
];

export const MapViewer = () => {
  const { areas, alerts, setSelectedArea } = useStore();
  const stageRef = useRef<any>(null);
  const [flashOn, setFlashOn] = useState(true);

  useEffect(() => {
    const interval = setInterval(() => setFlashOn(f => !f), 600);
    return () => clearInterval(interval);
  }, []);

  const flattenPoints = (pts: any[]) => pts.flatMap(p => [p.x, p.y]);

  const getAreaColor = (area: FestivalArea) => {
    const hasAlert = alerts.some(alert => alert.area.id === area.id);
    if (hasAlert) return flashOn ? CROWD_COLORS.ALERT : 'rgba(255,255,255,0.15)';
    if (area.crowdLevel === 'FULL')   return CROWD_COLORS.FULL;
    if (area.crowdLevel === 'MEDIUM') return CROWD_COLORS.MEDIUM;
    if (area.crowdLevel === 'LOW')    return CROWD_COLORS.LOW;
    return hexToRgba(area.baseColor, 0.85);
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
        <Layer listening={false}>
          {/* Base grass fill */}
          <Rect x={0} y={0} width={W} height={H} fill="#8fce5a" />

          {/* Lighter inner field */}
          <Ellipse
            x={W / 2} y={H / 2}
            radiusX={480} radiusY={320}
            fill="#a8dc6e"
          />

          {/* Subtle contour blobs */}
          {GRASS_BLOBS.map(([x, y, rx, ry, color], i) => (
            <Ellipse key={i} x={x} y={y} radiusX={rx} radiusY={ry} fill={color} opacity={0.35} />
          ))}

          {/* Stones */}
          {STONES.map(([x, y, rx, ry, rot], i) => (
            <Ellipse
              key={i} x={x} y={y}
              radiusX={rx} radiusY={ry}
              rotation={rot}
              fill="#b8bfaa"
              stroke="#9aa090"
              strokeWidth={1}
              opacity={0.8}
            />
          ))}

          {/* Trees — outer shadow blob, main canopy, highlight */}
          {TREES.map(([x, y, outerR, innerR, dark, light], i) => (
            <React.Fragment key={i}>
              <Circle x={x} y={y} radius={outerR + 4} fill="#1a4a0a" opacity={0.25} />
              <Circle x={x} y={y} radius={outerR}     fill={dark} />
              <Circle x={x} y={y} radius={innerR}     fill={light} />
              <Circle x={x - outerR * 0.25} y={y - outerR * 0.25} radius={innerR * 0.45} fill="#c8f080" opacity={0.4} />
            </React.Fragment>
          ))}
        </Layer>

        {/* ── Areas layer ── */}
        <Layer>
          {areas.map((area, idx) => {
            if (!area.coordinates || area.coordinates.length === 0) return null;

            const hasAlert = alerts.some(alert => alert.area.id === area.id);
            const cx = area.coordinates.reduce((sum, p) => sum + p.x, 0) / area.coordinates.length;
            const cy = area.coordinates.reduce((sum, p) => sum + p.y, 0) / area.coordinates.length;

            return (
              <React.Fragment key={idx}>
                {/* Base fill — solid and opaque */}
                <Line
                  points={flattenPoints(area.coordinates)}
                  closed
                  fill={area.baseColor}
                  opacity={0.9}
                  stroke={area.baseColor}
                  strokeWidth={2}
                />

                {/* Crowd level colour overlay */}
                <Line
                  points={flattenPoints(area.coordinates)}
                  closed
                  fill={getAreaColor(area)}
                  opacity={0.85}
                  stroke={hasAlert ? (flashOn ? '#FF006E' : '#ffffff') : '#ffffff'}
                  strokeWidth={hasAlert ? 3 : 1}
                  onClick={() => setSelectedArea(area)}
                  onTap={() => setSelectedArea(area)}
                  shadowColor={hasAlert ? '#FF006E' : undefined}
                  shadowBlur={hasAlert ? (flashOn ? 25 : 0) : 0}
                  shadowOpacity={hasAlert ? 1 : 0}
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
