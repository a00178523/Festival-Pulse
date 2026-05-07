import { X, MapPin, Users, Clock, FileText, AlertTriangle } from 'lucide-react';
import { useStore } from '../store';

const LEVEL_STYLES: Record<string, { bg: string; color: string }> = {
  LOW:    { bg: 'rgba(6,255,165,0.15)',  color: '#06ffa5' },
  MEDIUM: { bg: 'rgba(255,190,11,0.15)', color: '#ffbe0b' },
  FULL:   { bg: 'rgba(255,84,0,0.15)',   color: '#ff5400' },
};

export const ReportDetailModal = () => {
  const { selectedReport, setSelectedReport, alerts } = useStore();

  if (!selectedReport) return null;

  const r = selectedReport;
  const style = LEVEL_STYLES[r.crowdLevel];
  const hasAlert = alerts.some(a => a.area.id === r.area.id);

  const formatDateTime = (iso: string) =>
    new Date(iso).toLocaleString('en-IE', {
      dateStyle: 'medium',
      timeStyle: 'short',
    });

  return (
    <div className="modal show" onClick={() => setSelectedReport(null)}>
      <div className="modal-content report-detail-modal" onClick={e => e.stopPropagation()}>

        {/* Header */}
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <span
              style={{
                background: style.bg,
                color: style.color,
                border: `1px solid ${style.color}`,
                borderRadius: '8px',
                padding: '0.3rem 0.8rem',
                fontSize: '0.8rem',
                fontWeight: 800,
                letterSpacing: '0.08em',
              }}
            >
              {r.crowdLevel}
            </span>
            <h2 style={{ fontFamily: 'Bebas Neue', fontSize: '1.8rem', letterSpacing: '0.05em' }}>
              Crowd Report
            </h2>
          </div>
          <button className="modal-close" onClick={() => setSelectedReport(null)}>×</button>
        </div>

        {/* Alert banner */}
        {hasAlert && (
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.6rem',
            background: 'rgba(255,0,110,0.15)',
            border: '1px solid rgba(255,0,110,0.5)',
            borderRadius: '10px',
            padding: '0.75rem 1rem',
            marginBottom: '1.5rem',
            color: '#ff006e',
            fontSize: '0.9rem',
            fontWeight: 600,
          }}>
            <AlertTriangle size={18} />
            This area currently has an active alert
          </div>
        )}

        {/* Details */}
        <div className="report-detail-grid">
          <div className="report-detail-item">
            <div className="report-detail-label">
              <MapPin size={14} /> Area
            </div>
            <div className="report-detail-value">{r.area.name}</div>
          </div>

          {r.area.areaType && (
            <div className="report-detail-item">
              <div className="report-detail-label">
                <MapPin size={14} /> Type
              </div>
              <div className="report-detail-value">{r.area.areaType}</div>
            </div>
          )}

          <div className="report-detail-item">
            <div className="report-detail-label">
              <Users size={14} /> Crowd Level
            </div>
            <div className="report-detail-value" style={{ color: style.color }}>
              {r.crowdLevel}
            </div>
          </div>

          <div className="report-detail-item">
            <div className="report-detail-label">
              <Clock size={14} /> Submitted
            </div>
            <div className="report-detail-value">{formatDateTime(r.submittedAt)}</div>
          </div>

          {r.note && (
            <div className="report-detail-item report-detail-full">
              <div className="report-detail-label">
                <FileText size={14} /> Note
              </div>
              <div className="report-detail-value">{r.note}</div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
