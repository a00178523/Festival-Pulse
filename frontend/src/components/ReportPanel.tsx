import { useState, useEffect } from 'react';
import { ClipboardList, Send, Clock } from 'lucide-react';
import { useStore } from '../store';
import { api } from '../api';
import type { CrowdReport } from '../types';

interface ReportPanelProps {
  festivalId: number;
}

const LEVEL_STYLES: Record<string, { bg: string; color: string; label: string }> = {
  LOW:    { bg: 'rgba(6,255,165,0.15)',   color: '#06ffa5', label: 'LOW'    },
  MEDIUM: { bg: 'rgba(255,190,11,0.15)',  color: '#ffbe0b', label: 'MEDIUM' },
  FULL:   { bg: 'rgba(255,84,0,0.15)',    color: '#ff5400', label: 'FULL'   },
};

export const ReportPanel = ({ festivalId }: ReportPanelProps) => {
  const { areas, setAlerts, setSelectedReport } = useStore();

  const [areaId, setAreaId]         = useState('');
  const [level, setLevel]           = useState<'LOW' | 'MEDIUM' | 'FULL' | ''>('');
  const [note, setNote]             = useState('');
  const [reports, setReports]       = useState<CrowdReport[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError]           = useState('');

  useEffect(() => {
    setReports([]);
    setAreaId('');
    setLevel('');
    setNote('');
    setError('');
    api.getReports(festivalId)
      .then(setReports)
      .catch(e => console.error('Failed to load reports', e));
  }, [festivalId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!areaId || !level) return;
    setError('');
    setSubmitting(true);
    try {
      const res = await fetch(`/api/festivals/${festivalId}/reports`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ areaId: Number(areaId), crowdLevel: level, note }),
      });

      if (!res.ok) {
        const body = await res.json();
        setError(body.message || 'Failed to submit report');
        return;
      }

      const newReport = await res.json();
      setReports(prev => [newReport, ...prev].slice(0, 20));

      if (level === 'FULL') {
        const updatedAlerts = await api.getAlerts(festivalId);
        setAlerts(updatedAlerts);
      }

      setAreaId('');
      setLevel('');
      setNote('');
    } catch (e) {
      console.error('Failed to submit report', e);
      setError('Unexpected error, please try again');
    } finally {
      setSubmitting(false);
    }
  };

  const formatTime = (iso: string) =>
    new Date(iso).toLocaleTimeString('en-IE', { hour: '2-digit', minute: '2-digit' });

  return (
    <section className="report-panel">
      {/* Submit form */}
      <div className="report-form-card">
        <h2 className="report-title">
          <Send size={22} color="var(--neon-cyan)" />
          Submit Crowd Report
        </h2>

        <form onSubmit={handleSubmit} className="report-form">
          <div className="report-form-row">
            <div className="form-group">
              <label>Area</label>
              <select
                className="report-select"
                value={areaId}
                onChange={e => setAreaId(e.target.value)}
                required
              >
                <option value="">Select area...</option>
                {areas.map(a => (
                  <option key={a.id} value={a.id}>{a.name}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Crowd Level</label>
              <div className="level-buttons">
                {(['LOW', 'MEDIUM', 'FULL'] as const).map(l => (
                  <button
                    key={l}
                    type="button"
                    className={`level-btn ${level === l ? 'selected' : ''}`}
                    style={level === l ? {
                      background: LEVEL_STYLES[l].bg,
                      borderColor: LEVEL_STYLES[l].color,
                      color: LEVEL_STYLES[l].color,
                    } : {}}
                    onClick={() => setLevel(l)}
                  >
                    {l}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div className="form-group">
            <label>Note <span style={{ color: 'var(--text-secondary)', fontWeight: 400 }}>(optional)</span></label>
            <input
              type="text"
              value={note}
              onChange={e => setNote(e.target.value)}
              placeholder="e.g. Queue backing up near entrance..."
              maxLength={200}
            />
          </div>

          {error && (
            <div style={{
              background: 'rgba(255,0,110,0.15)',
              border: '1px solid rgba(255,0,110,0.5)',
              borderRadius: '10px',
              padding: '0.75rem 1rem',
              color: '#ff006e',
              fontSize: '0.9rem',
            }}>
              {error}
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary"
            disabled={!areaId || !level || submitting}
            style={{ opacity: (!areaId || !level) ? 0.5 : 1 }}
          >
            <Send size={16} style={{ display: 'inline', marginRight: '8px', verticalAlign: 'middle' }} />
            {submitting ? 'Submitting...' : 'Submit Report'}
          </button>
        </form>
      </div>

      {/* Recent reports feed */}
      <div className="report-feed-card">
        <h2 className="report-title">
          <ClipboardList size={22} color="var(--neon-yellow)" />
          Recent Reports
        </h2>

        {reports.length === 0 ? (
          <p style={{ color: 'var(--text-secondary)', textAlign: 'center', padding: '2rem 0' }}>
            No reports yet
          </p>
        ) : (
          <div className="report-feed">
            {reports.map(r => {
              const style = LEVEL_STYLES[r.crowdLevel];
              return (
                <div key={r.id} className="report-item" style={{ cursor: 'pointer' }} onClick={() => setSelectedReport(r)}>
                  <div className="report-item-left">
                    <span
                      className="report-level-badge"
                      style={{ background: style.bg, color: style.color, borderColor: style.color }}
                    >
                      {style.label}
                    </span>
                    <div>
                      <div className="report-area-name">{r.area.name}</div>
                      {r.note && <div className="report-note">{r.note}</div>}
                    </div>
                  </div>
                  <div className="report-time">
                    <Clock size={12} style={{ display: 'inline', marginRight: '4px' }} />
                    {formatTime(r.submittedAt)}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
};
