import { useState } from 'react';
import { AlertTriangle, CheckCircle } from 'lucide-react';
import { useStore } from '../store';
import { api } from '../api';
import type { CrowdAlert } from '../types';

interface ResolveModalProps {
  alert: CrowdAlert;
  festivalId: number;
  onClose: () => void;
  onResolved: () => void;
}

const LEVEL_STYLES: Record<string, { bg: string; color: string }> = {
  LOW:    { bg: 'rgba(6,255,165,0.15)',  color: '#06ffa5' },
  MEDIUM: { bg: 'rgba(255,190,11,0.15)', color: '#ffbe0b' },
};

export const ResolveModal = ({ alert, festivalId, onClose, onResolved }: ResolveModalProps) => {
  const { areas, setAlerts, alerts } = useStore();
  const [newLevel, setNewLevel] = useState<'LOW' | 'MEDIUM' | ''>('');
  const [submitting, setSubmitting] = useState(false);

  const handleConfirm = async () => {
    if (!newLevel) return;
    setSubmitting(true);
    try {
      // 1. Resolve the alert
      await api.resolveAlert(festivalId, alert.id);

      // 2. Submit a follow-up report at the new level
      await api.submitReport(festivalId, {
        areaId: alert.area.id,
        crowdLevel: newLevel,
        note: `Crowd level updated to ${newLevel} after alert resolved`,
      });

      // 3. Update store — remove resolved alert, refresh areas crowd level
      setAlerts(alerts.filter(a => a.id !== alert.id));
      onResolved();
      onClose();
    } catch (e) {
      console.error('Failed to resolve alert', e);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal show" onClick={onClose}>
      <div className="modal-content" style={{ maxWidth: '480px' }} onClick={e => e.stopPropagation()}>

        <div className="modal-header">
          <h2 style={{ fontFamily: 'Bebas Neue', fontSize: '1.8rem', letterSpacing: '0.05em', display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
            <AlertTriangle size={24} color="var(--neon-pink)" />
            Resolve Alert
          </h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        {/* Alert summary */}
        <div style={{
          background: 'rgba(255,0,110,0.1)',
          border: '1px solid rgba(255,0,110,0.3)',
          borderRadius: '12px',
          padding: '1rem 1.25rem',
          marginBottom: '1.5rem',
        }}>
          <div style={{ fontWeight: 700, fontSize: '1.1rem', marginBottom: '0.25rem' }}>
            {alert.area.name}
          </div>
          <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            {alert.message}
          </div>
        </div>

        {/* New level selection */}
        <div className="form-group" style={{ marginBottom: '1.5rem' }}>
          <label>What is the crowd level now?</label>
          <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem' }}>
            {(['LOW', 'MEDIUM'] as const).map(l => (
              <button
                key={l}
                type="button"
                onClick={() => setNewLevel(l)}
                style={{
                  flex: 1,
                  padding: '1rem',
                  borderRadius: '12px',
                  border: `2px solid ${newLevel === l ? LEVEL_STYLES[l].color : 'rgba(255,255,255,0.15)'}`,
                  background: newLevel === l ? LEVEL_STYLES[l].bg : 'rgba(255,255,255,0.04)',
                  color: newLevel === l ? LEVEL_STYLES[l].color : 'var(--text-secondary)',
                  fontFamily: 'Outfit, sans-serif',
                  fontSize: '1rem',
                  fontWeight: 700,
                  letterSpacing: '0.05em',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                }}
              >
                {l}
              </button>
            ))}
          </div>
        </div>

        {/* Actions */}
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button
            className="btn btn-secondary"
            onClick={onClose}
            style={{ flex: 1 }}
          >
            Cancel
          </button>
          <button
            className="btn btn-primary"
            onClick={handleConfirm}
            disabled={!newLevel || submitting}
            style={{ flex: 1, opacity: !newLevel ? 0.5 : 1, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}
          >
            <CheckCircle size={18} />
            {submitting ? 'Resolving...' : 'Confirm Resolve'}
          </button>
        </div>
      </div>
    </div>
  );
};
