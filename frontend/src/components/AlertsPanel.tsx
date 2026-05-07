import { AlertTriangle, CheckCircle, Clock, ShieldCheck } from 'lucide-react';
import { useStore } from '../store';
import { api } from '../api';
import type { CrowdAlert } from '../types';

interface AlertsPanelProps {
  festivalId: number;
}

export const AlertsPanel = ({ festivalId }: AlertsPanelProps) => {
  const { alerts, setAlerts } = useStore();

  const handleResolve = async (alert: CrowdAlert) => {
    try {
      await api.resolveAlert(festivalId, alert.id);
      setAlerts(alerts.filter(a => a.id !== alert.id));
    } catch (error) {
      console.error('Failed to resolve alert', error);
    }
  };

  const formatTime = (isoString: string) => {
    const date = new Date(isoString);
    return date.toLocaleTimeString('en-IE', { hour: '2-digit', minute: '2-digit' });
  };

  const sortedAlerts = [...alerts].sort(
    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  );

  return (
    <section className="alerts-panel">
      <div className="alerts-header">
        <h2 className="alerts-title">
          <AlertTriangle size={28} color="var(--neon-pink)" />
          Active Alerts
          {alerts.length > 0 && (
            <span className="alerts-badge">{alerts.length}</span>
          )}
        </h2>
      </div>

      {sortedAlerts.length === 0 ? (
        <div className="alerts-empty">
          <ShieldCheck size={48} color="#06ffa5" style={{ marginBottom: '1rem' }} />
          <p>All clear — no active alerts</p>
        </div>
      ) : (
        <div className="alerts-list">
          {sortedAlerts.map(alert => (
            <div key={alert.id} className="alert-card">
              <div className="alert-card-left">
                <div className="alert-pulse" />
                <div className="alert-info">
                  <div className="alert-area">{alert.area.name}</div>
                  <div className="alert-message">{alert.message}</div>
                  <div className="alert-time"><Clock size={12} style={{ display: 'inline', marginRight: '4px' }} />{formatTime(alert.createdAt)}</div>
                </div>
              </div>
              <button
                className="btn-resolve"
                onClick={() => handleResolve(alert)}
              >
                Resolve
              </button>
            </div>
          ))}
        </div>
      )}
    </section>
  );
};
