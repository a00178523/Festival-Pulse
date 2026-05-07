import React, { useRef } from 'react';
import { Stage, Layer, Line, Text } from 'react-konva';
import type { FestivalArea } from '../types';
import { CROWD_COLORS, hexToRgba } from '../utils/colors';
import { useStore } from '../store';

export const MapViewer = () => {
  const { areas, alerts, setSelectedArea } = useStore();
  const stageRef = useRef<any>(null);

  const flattenPoints = (pts: any[]) => {
    return pts.flatMap(p => [p.x, p.y]);
  };

  const getAreaColor = (area: FestivalArea) => {
    // Check if area has an active alert
    const hasAlert = alerts.some(alert => alert.area.id === area.id);
    
    if (hasAlert) {
      return CROWD_COLORS.ALERT;
    }
    
    // Apply crowd level overlay
    if (area.crowdLevel === 'FULL') {
      return CROWD_COLORS.FULL;
    } else if (area.crowdLevel === 'MEDIUM') {
      return CROWD_COLORS.MEDIUM;
    } else if (area.crowdLevel === 'LOW') {
      return CROWD_COLORS.LOW;
    }
    
    // Default to base color with transparency
    return hexToRgba(area.baseColor, 0.6);
  };

  const handleAreaClick = (area: FestivalArea) => {
    setSelectedArea(area);
  };

  return (
    <div style={{ position: 'relative' }}>
      <Stage
        ref={stageRef}
        width={1200}
        height={800}
        style={{ 
          background: 'linear-gradient(135deg, #0a0014 0%, #1a0033 50%, #2d0052 100%)',
          border: '2px solid rgba(255, 255, 255, 0.1)',
          borderRadius: '20px',
        }}
      >
        <Layer>
          {areas.map((area, idx) => {
            if (!area.coordinates || area.coordinates.length === 0) return null;

            const hasAlert = alerts.some(alert => alert.area.id === area.id);
            
            return (
              <React.Fragment key={idx}>
                {/* Base area shape */}
                <Line
                  points={flattenPoints(area.coordinates)}
                  closed
                  fill={area.baseColor}
                  opacity={0.4}
                  stroke={area.baseColor}
                  strokeWidth={2}
                />
                
                {/* Crowd level overlay */}
                <Line
                  points={flattenPoints(area.coordinates)}
                  closed
                  fill={getAreaColor(area)}
                  opacity={0.8}
                  stroke={hasAlert ? '#FF006E' : area.baseColor}
                  strokeWidth={hasAlert ? 4 : 2}
                  onClick={() => handleAreaClick(area)}
                  onTap={() => handleAreaClick(area)}
                  shadowColor={hasAlert ? '#FF006E' : undefined}
                  shadowBlur={hasAlert ? 20 : 0}
                  shadowOpacity={hasAlert ? 0.8 : 0}
                />
                
                {/* Area label */}
                <Text
                  x={area.coordinates.reduce((sum, p) => sum + p.x, 0) / area.coordinates.length - 50}
                  y={area.coordinates.reduce((sum, p) => sum + p.y, 0) / area.coordinates.length - 10}
                  text={area.name}
                  fontSize={16}
                  fontStyle="bold"
                  fill="#FFFFFF"
                  width={100}
                  align="center"
                  shadowColor="#000000"
                  shadowBlur={10}
                  shadowOpacity={0.8}
                />
                
                {/* Alert indicator */}
                {hasAlert && (
                  <Text
                    x={area.coordinates.reduce((sum, p) => sum + p.x, 0) / area.coordinates.length - 15}
                    y={area.coordinates.reduce((sum, p) => sum + p.y, 0) / area.coordinates.length + 15}
                    text="🚨"
                    fontSize={24}
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
        top: 20,
        right: 20,
        background: 'rgba(26, 0, 51, 0.9)',
        backdropFilter: 'blur(20px)',
        border: '2px solid rgba(255, 255, 255, 0.1)',
        borderRadius: '15px',
        padding: '20px',
        color: 'white',
      }}>
        <h3 style={{ margin: '0 0 15px 0', fontSize: '18px', fontWeight: 'bold' }}>
          Crowd Levels
        </h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ 
              width: '30px', 
              height: '20px', 
              background: CROWD_COLORS.LOW,
              borderRadius: '5px',
              border: '1px solid rgba(255, 255, 255, 0.3)'
            }} />
            <span>🟢 LOW</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ 
              width: '30px', 
              height: '20px', 
              background: CROWD_COLORS.MEDIUM,
              borderRadius: '5px',
              border: '1px solid rgba(255, 255, 255, 0.3)'
            }} />
            <span>🟡 MEDIUM</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ 
              width: '30px', 
              height: '20px', 
              background: CROWD_COLORS.FULL,
              borderRadius: '5px',
              border: '1px solid rgba(255, 255, 255, 0.3)'
            }} />
            <span>🟠 FULL</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ 
              width: '30px', 
              height: '20px', 
              background: CROWD_COLORS.ALERT,
              borderRadius: '5px',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 0 15px rgba(255, 0, 110, 0.6)'
            }} />
            <span>🚨 ALERT</span>
          </div>
        </div>
      </div>
    </div>
  );
};
