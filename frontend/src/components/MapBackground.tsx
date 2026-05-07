import React from 'react';
import { Layer, Rect, Ellipse, Circle } from 'react-konva';

export const TREES: [number, number, number, number, string, string][] = [
  [60,  40,  38, 22, '#2d6e1a', '#4a9e2a'],
  [160, 25,  32, 18, '#3a7a20', '#5aae35'],
  [280, 45,  40, 24, '#256015', '#3d8e22'],
  [420, 20,  34, 20, '#2d6e1a', '#4a9e2a'],
  [560, 38,  36, 21, '#3a7a20', '#52a030'],
  [700, 22,  38, 23, '#256015', '#3d8e22'],
  [840, 42,  33, 19, '#2d6e1a', '#4a9e2a'],
  [980, 28,  37, 22, '#3a7a20', '#5aae35'],
  [1100,44,  35, 20, '#256015', '#3d8e22'],
  [50,  760, 40, 24, '#2d6e1a', '#4a9e2a'],
  [180, 775, 34, 20, '#3a7a20', '#5aae35'],
  [320, 758, 38, 22, '#256015', '#3d8e22'],
  [460, 772, 32, 18, '#2d6e1a', '#4a9e2a'],
  [600, 760, 36, 21, '#3a7a20', '#52a030'],
  [740, 778, 40, 24, '#256015', '#3d8e22'],
  [880, 762, 33, 19, '#2d6e1a', '#4a9e2a'],
  [1020,775, 37, 22, '#3a7a20', '#5aae35'],
  [1140,758, 35, 20, '#256015', '#3d8e22'],
  [35,  140, 36, 21, '#2d6e1a', '#4a9e2a'],
  [28,  280, 40, 24, '#3a7a20', '#5aae35'],
  [40,  420, 34, 20, '#256015', '#3d8e22'],
  [30,  560, 38, 22, '#2d6e1a', '#4a9e2a'],
  [42,  680, 32, 18, '#3a7a20', '#52a030'],
  [1165,140, 36, 21, '#256015', '#3d8e22'],
  [1172,280, 40, 24, '#2d6e1a', '#4a9e2a'],
  [1160,420, 34, 20, '#3a7a20', '#5aae35'],
  [1168,560, 38, 22, '#256015', '#3d8e22'],
  [1158,680, 33, 19, '#2d6e1a', '#4a9e2a'],
];

export const STONES: [number, number, number, number, number][] = [
  [110, 55,  9, 6,  20], [240, 35,  7, 5,  -10], [370, 50,  8, 5,  15],
  [500, 30,  6, 4,   5], [630, 48,  9, 6,  -20], [760, 32,  7, 5,  10],
  [900, 46,  8, 5,  -15],[1050,38,  6, 4,   25], [1130,55,  9, 6,  -5],
  [55,  200, 7, 5,  10], [45,  350, 8, 6,  -20], [38,  490, 6, 4,  15],
  [50,  630, 9, 5,  -10],[1155,200, 7, 5,  20],  [1162,350, 8, 6,  -5],
  [1150,490, 6, 4,  10], [1160,630, 9, 5, -15],
  [120, 770, 8, 5,  15], [260, 762, 7, 4,  -5],  [400, 775, 9, 6,  20],
  [540, 765, 6, 4, -10], [680, 772, 8, 5,  10],  [820, 760, 7, 5, -20],
  [960, 775, 9, 6,   5], [1080,762, 6, 4,  15],
];

export const GRASS_BLOBS: [number, number, number, number, string][] = [
  [200, 180, 180, 120, '#7ec850'],
  [700, 150, 220, 100, '#6db840'],
  [950, 400, 160, 130, '#72c248'],
  [300, 580, 200, 110, '#6db840'],
  [600, 620, 180, 100, '#7ec850'],
  [100, 400, 130,  90, '#72c248'],
];

export const MapBackground = () => (
  <Layer listening={false}>
    <Rect x={0} y={0} width={1200} height={800} fill="#8fce5a" />
    <Ellipse x={600} y={400} radiusX={480} radiusY={320} fill="#a8dc6e" />
    {GRASS_BLOBS.map(([x, y, rx, ry, color], i) => (
      <Ellipse key={i} x={x} y={y} radiusX={rx} radiusY={ry} fill={color} opacity={0.35} />
    ))}
    {STONES.map(([x, y, rx, ry, rot], i) => (
      <Ellipse key={i} x={x} y={y} radiusX={rx} radiusY={ry} rotation={rot}
        fill="#b8bfaa" stroke="#9aa090" strokeWidth={1} opacity={0.8} />
    ))}
    {TREES.map(([x, y, outerR, innerR, dark, light], i) => (
      <React.Fragment key={i}>
        <Circle x={x} y={y} radius={outerR + 4} fill="#1a4a0a" opacity={0.25} />
        <Circle x={x} y={y} radius={outerR} fill={dark} />
        <Circle x={x} y={y} radius={innerR} fill={light} />
        <Circle x={x - outerR * 0.25} y={y - outerR * 0.25} radius={innerR * 0.45} fill="#c8f080" opacity={0.4} />
      </React.Fragment>
    ))}
  </Layer>
);
