const API_BASE = '/api';

export const api = {
  // Festivals
  async getFestivals() {
    const res = await fetch(`${API_BASE}/festivals`);
    return res.json();
  },
  
  async createFestival(data: any) {
    const res = await fetch(`${API_BASE}/festivals`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },
  
  // Areas
  async getAreas(festivalId: number) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/areas`);
    return res.json();
  },
  
  async createArea(festivalId: number, data: any) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/areas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },
  
  async updateArea(festivalId: number, areaId: number, data: any) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/areas/${areaId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },
  
  // Reports
  async getReports(festivalId: number) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/reports`);
    return res.json();
  },
  
  async submitReport(festivalId: number, data: any) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/reports`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },
  
  // Alerts
  async getAlerts(festivalId: number) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/alerts`);
    return res.json();
  },
  
  async resolveAlert(festivalId: number, alertId: number) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/alerts/${alertId}/resolve`, {
      method: 'PATCH',
    });
    return res.json();
  },
  
  // Dashboard
  async getDashboard(festivalId: number) {
    const res = await fetch(`${API_BASE}/festivals/${festivalId}/dashboard`);
    return res.json();
  },
};
