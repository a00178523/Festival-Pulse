export const CROWD_COLORS = {
  LOW: 'rgba(6, 255, 165, 0.3)',
  MEDIUM: 'rgba(255, 190, 11, 0.5)',
  FULL: 'rgba(255, 140, 0, 0.6)',
  ALERT: 'rgba(255, 0, 110, 0.8)',
};

export const DEFAULT_AREA_COLORS = [
  '#FF006E', // Neon Pink
  '#00F5FF', // Neon Cyan
  '#FFBE0B', // Neon Yellow
  '#8338EC', // Neon Purple
  '#06FFA5', // Neon Green
  '#FF5400', // Neon Orange
  '#FF006E', // Hot Pink
  '#00D9FF', // Electric Blue
  '#FFD60A', // Electric Yellow
  '#B5179E', // Magenta
];

export const getRandomColor = () => {
  return DEFAULT_AREA_COLORS[Math.floor(Math.random() * DEFAULT_AREA_COLORS.length)];
};

export const hexToRgba = (hex: string, alpha: number = 1) => {
  const r = parseInt(hex.slice(1, 3), 16);
  const g = parseInt(hex.slice(3, 5), 16);
  const b = parseInt(hex.slice(5, 7), 16);
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};
