import { useState } from 'react';
import { AlertTriangle, Clock, ShieldCheck } from 'lucide-react';
import { useStore } from '../store';
import { api } from '../api';
import type { CrowdAlert } from '../types';
import { ResolveModal } from './ResolveModal';

interface AlertsPanelProps {
  festivalId: number;
}

export const AlertsPanel = ({ festivalId }: AlertsPanelProps) => {
  const { alerts, setAreas } = useStore();
  const [resolvingAlert, setResolvingAlert] = useState<CrowdAlert | null>(null);

  const formatTime = (isoString: string) => {
    const date = new Date(isoString);
    return date.toLocaleTimeString('en-IE', { hour: '2-digit', minute: '2-digit' });
  };

  const handleResolved = async () => {
    // Refresh areas so map crowd levels update immediately
    try {
      const [areasData, reportsData] = await Promise.all([
        api.getAreas(festivalId),
        api.getReports(festivalId),
      ]);
      const parsedAreas = areasData.map((area: any) => {
        const latest = reportsData
          .filter((r: any) => r.area.id === area.id)
          .sort((a: any, b: any) => new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime())[0];
        return {
          ...area,
          coordinates: area.coordinates ? JSON.parse(area.coordinates) : [],
          crowdLevel: latest?.crowdLevel ?? undefined,
        };
      });
      setAreas(parsedAreas);
    } catch (e) {
      console.error('Failed to refresh areas', e);
    }
  };

  const sortedAlerts = [...alerts].sort(
    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  );

  return (
    <>
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
                    <div className="alert-time">
                      <Clock size={12} style={{ display: 'inline', marginRight: '4px' }} />
                      {formatTime(alert.createdAt)}
                    </div>
                  </div>
                </div>
                <button
                  className="btn-resolve"
                  onClick={() => setResolvingAlert(alert)}
                >
                  Resolve
                </button>
              </div>
            ))}
          </div>
        )}
      </section>

      {resolvingAlert && (
        <ResolveModal
          alert={resolvingAlert}
          festivalId={festivalId}
          onClose={() => setResolvingAlert(null)}
          onResolved={handleResolved}
        />
      )}
    </>
  );
};
