import { useState, useEffect } from 'react';
import { MapBuilder } from './components/MapBuilder';
import { MapViewer } from './components/MapViewer';
import { useStore } from './store';
import { api } from './api';
import type { FestivalArea } from './types';
import './App.css';

function App() {
  console.log('App component rendering...');
  
  const {
    festivals,
    currentFestival,
    areas,
    alerts,
    viewMode,
    setFestivals,
    setCurrentFestival,
    setAreas,
    setAlerts,
    setViewMode,
    addArea,
  } = useStore();

  const [showCreateFestival, setShowCreateFestival] = useState(false);
  const [festivalName, setFestivalName] = useState('');
  const [festivalDescription, setFestivalDescription] = useState('');

  useEffect(() => {
    loadFestivals();
  }, []);

  useEffect(() => {
    if (currentFestival) {
      loadFestivalData();
    }
  }, [currentFestival]);

  const loadFestivals = async () => {
    try {
      console.log('Loading festivals...');
      const data = await api.getFestivals();
      console.log('Festivals loaded:', data);
      setFestivals(data);
      if (data.length > 0 && !currentFestival) {
        setCurrentFestival(data[0]);
      }
    } catch (error) {
      console.error('Failed to load festivals', error);
      alert('Failed to connect to backend. Make sure Spring Boot is running on port 8080.');
    }
  };

  const loadFestivalData = async () => {
    if (!currentFestival) return;
    
    try {
      const [areasData, alertsData] = await Promise.all([
        api.getAreas(currentFestival.id),
        api.getAlerts(currentFestival.id),
      ]);
      
      // Parse coordinates from JSON string
      const parsedAreas = areasData.map((area: any) => ({
        ...area,
        coordinates: area.coordinates ? JSON.parse(area.coordinates) : [],
      }));
      
      setAreas(parsedAreas);
      setAlerts(alertsData);
    } catch (error) {
      console.error('Failed to load festival data', error);
    }
  };

  const handleCreateFestival = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const newFestival = await api.createFestival({
        name: festivalName,
        description: festivalDescription,
      });
      
      await loadFestivals();
      setCurrentFestival(newFestival);
      setShowCreateFestival(false);
      setFestivalName('');
      setFestivalDescription('');
    } catch (error) {
      console.error('Failed to create festival', error);
    }
  };

  const handleAreaComplete = async (area: FestivalArea) => {
    if (!currentFestival) return;
    
    try {
      const areaData = {
        name: area.name,
        areaType: area.areaType,
        coordinates: JSON.stringify(area.coordinates),
        baseColor: area.baseColor,
      };
      
      const savedArea = await api.createArea(currentFestival.id, areaData);
      addArea({
        ...savedArea,
        coordinates: area.coordinates,
      });
    } catch (error) {
      console.error('Failed to save area', error);
    }
  };

  return (
    <div className="app">
      <header className="header">
        <div className="header-content">
          <h1 className="logo">
            <span className="logo-pulse">⚡</span>
            <span className="logo-text">Festival Pulse</span>
          </h1>
          <div className="header-status">
            <div className="live-indicator"></div>
            <span>LIVE</span>
          </div>
        </div>
      </header>

      <main className="container">
        {/* Festival Selector */}
        <section className="festival-selector">
          <div className="festival-header">
            <div className="festival-info">
              <h2 className="festival-name">
                {currentFestival ? currentFestival.name : 'Select a Festival'}
              </h2>
              <p className="festival-dates">
                {currentFestival?.description || ''}
              </p>
            </div>
            <div className="festival-actions">
              <select
                className="festival-dropdown"
                value={currentFestival?.id || ''}
                onChange={(e) => {
                  const festival = festivals.find(f => f.id === Number(e.target.value));
                  setCurrentFestival(festival || null);
                }}
              >
                <option value="">Choose festival...</option>
                {festivals.map(f => (
                  <option key={f.id} value={f.id}>{f.name}</option>
                ))}
              </select>
              <button 
                className="btn btn-secondary"
                onClick={() => setShowCreateFestival(true)}
              >
                + New Festival
              </button>
            </div>
          </div>
        </section>

        {currentFestival && (
          <>
            {/* Mode Toggle */}
            <div className="mode-toggle">
              <button
                className={`mode-btn ${viewMode === 'monitor' ? 'active' : ''}`}
                onClick={() => setViewMode('monitor')}
              >
                👁️ Monitor Mode
              </button>
              <button
                className={`mode-btn ${viewMode === 'builder' ? 'active' : ''}`}
                onClick={() => setViewMode('builder')}
              >
                ✏️ Builder Mode
              </button>
            </div>

            {/* Map Display */}
            <section className="map-section">
              {viewMode === 'builder' ? (
                <MapBuilder onAreaComplete={handleAreaComplete} />
              ) : (
                <MapViewer />
              )}
            </section>

            {/* Stats */}
            <section className="dashboard-hero">
              <div className="stat-card">
                <div className="stat-icon">🎪</div>
                <div className="stat-content">
                  <div className="stat-value">{areas.length}</div>
                  <div className="stat-label">Areas</div>
                </div>
              </div>
              <div className="stat-card">
                <div className="stat-icon">🚨</div>
                <div className="stat-content">
                  <div className="stat-value">{alerts.length}</div>
                  <div className="stat-label">Active Alerts</div>
                </div>
              </div>
            </section>
          </>
        )}

        {!currentFestival && festivals.length === 0 && (
          <div style={{
            textAlign: 'center',
            padding: '4rem',
            background: 'var(--card-bg)',
            borderRadius: '20px',
            border: '2px solid rgba(255, 255, 255, 0.1)',
            backdropFilter: 'blur(20px)',
          }}>
            <div style={{ fontSize: '5rem', marginBottom: '1rem' }}>🎉</div>
            <h2 style={{ 
              fontFamily: 'Bebas Neue', 
              fontSize: '2.5rem',
              marginBottom: '1rem',
              background: 'linear-gradient(135deg, var(--neon-pink), var(--neon-cyan))',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}>
              Welcome to Festival Pulse!
            </h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '1.2rem', marginBottom: '2rem' }}>
              Create your first festival to get started with interactive crowd monitoring.
            </p>
            <button 
              className="btn btn-primary"
              onClick={() => setShowCreateFestival(true)}
              style={{ fontSize: '1.1rem', padding: '1.2rem 2.5rem' }}
            >
              🎪 Create Your First Festival
            </button>
          </div>
        )}
      </main>

      {/* Create Festival Modal */}
      {showCreateFestival && (
        <div className="modal show">
          <div className="modal-content">
            <div className="modal-header">
              <h2>🎉 Create New Festival</h2>
              <button 
                className="modal-close"
                onClick={() => setShowCreateFestival(false)}
              >
                ×
              </button>
            </div>
            <form onSubmit={handleCreateFestival} className="form">
              <div className="form-group">
                <label>Festival Name</label>
                <input
                  type="text"
                  value={festivalName}
                  onChange={(e) => setFestivalName(e.target.value)}
                  required
                  placeholder="Summer Music Fest 2024"
                />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea
                  value={festivalDescription}
                  onChange={(e) => setFestivalDescription(e.target.value)}
                  rows={3}
                  placeholder="A celebration of music, food, and culture..."
                />
              </div>
              <button type="submit" className="btn btn-primary">
                Create Festival
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
