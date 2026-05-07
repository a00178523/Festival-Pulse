const API_BASE = '/api';

// State
let currentFestivalId = null;
let festivals = [];
let areas = [];
let reports = [];
let alerts = [];

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadFestivals();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('createFestivalForm').addEventListener('submit', handleCreateFestival);
    document.getElementById('createAreaForm').addEventListener('submit', handleCreateArea);
    document.getElementById('submitReportForm').addEventListener('submit', handleSubmitReport);
    document.getElementById('festivalSelect').addEventListener('change', handleFestivalChange);
}

// Festival Management
async function loadFestivals() {
    try {
        const response = await fetch(`${API_BASE}/festivals`);
        festivals = await response.json();
        populateFestivalSelect();
        
        if (festivals.length > 0 && !currentFestivalId) {
            currentFestivalId = festivals[0].id;
            updateFestivalDisplay();
            await initializeFestivalData();
        }
    } catch (error) {
        showToast('Failed to load festivals');
        console.error(error);
    }
}

function populateFestivalSelect() {
    const select = document.getElementById('festivalSelect');
    select.innerHTML = '<option value="">Choose festival...</option>' + 
        festivals.map(festival => `
            <option value="${festival.id}" ${festival.id === currentFestivalId ? 'selected' : ''}>
                ${escapeHtml(festival.name)}
            </option>
        `).join('');
}

function updateFestivalDisplay() {
    const festival = festivals.find(f => f.id === currentFestivalId);
    if (!festival) return;
    
    document.getElementById('currentFestivalName').textContent = festival.name;
    
    if (festival.startDate && festival.endDate) {
        const start = new Date(festival.startDate).toLocaleDateString();
        const end = new Date(festival.endDate).toLocaleDateString();
        document.getElementById('currentFestivalDates').textContent = `${start} - ${end}`;
    } else {
        document.getElementById('currentFestivalDates').textContent = festival.description || '';
    }
    
    document.getElementById('festivalContent').classList.remove('hidden');
}

function handleFestivalChange(e) {
    const festivalId = parseInt(e.target.value);
    if (festivalId) {
        currentFestivalId = festivalId;
        updateFestivalDisplay();
        initializeFestivalData();
    }
}

async function handleCreateFestival(e) {
    e.preventDefault();
    
    const festivalData = {
        name: document.getElementById('festivalName').value,
        description: document.getElementById('festivalDescription').value,
        startDate: document.getElementById('festivalStartDate').value || null,
        endDate: document.getElementById('festivalEndDate').value || null
    };

    try {
        const response = await fetch(`${API_BASE}/festivals`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(festivalData)
        });

        if (response.ok) {
            const newFestival = await response.json();
            showToast('🎉 Festival created successfully!');
            e.target.reset();
            hideCreateFestivalModal();
            await loadFestivals();
            currentFestivalId = newFestival.id;
            updateFestivalDisplay();
            await initializeFestivalData();
        } else {
            showToast('Failed to create festival');
        }
    } catch (error) {
        showToast('Error creating festival');
        console.error(error);
    }
}

function showCreateFestivalModal() {
    document.getElementById('createFestivalModal').classList.add('show');
}

function hideCreateFestivalModal() {
    document.getElementById('createFestivalModal').classList.remove('show');
}

// Festival Data
async function initializeFestivalData() {
    if (!currentFestivalId) return;
    
    await Promise.all([
        loadAreas(),
        loadReports(),
        loadAlerts(),
        loadDashboard()
    ]);
}

// API Calls
async function loadDashboard() {
    if (!currentFestivalId) return;
    
    try {
        const response = await fetch(`${API_BASE}/festivals/${currentFestivalId}/dashboard`);
        const dashboard = await response.json();
        
        document.getElementById('totalAreas').textContent = dashboard.totalAreas;
        document.getElementById('activeAlerts').textContent = dashboard.activeAlerts.length;
        document.getElementById('recentReports').textContent = dashboard.recentReports.length;
    } catch (error) {
        console.error('Failed to load dashboard', error);
    }
}

async function loadAreas() {
    if (!currentFestivalId) return;
    
    try {
        const response = await fetch(`${API_BASE}/festivals/${currentFestivalId}/areas`);
        areas = await response.json();
        renderAreas();
        populateAreaSelect();
    } catch (error) {
        showToast('Failed to load areas');
        console.error(error);
    }
}

async function loadReports() {
    if (!currentFestivalId) return;
    
    try {
        const response = await fetch(`${API_BASE}/festivals/${currentFestivalId}/reports`);
        reports = await response.json();
        renderReports();
    } catch (error) {
        showToast('Failed to load reports');
        console.error(error);
    }
}

async function loadAlerts() {
    if (!currentFestivalId) return;
    
    try {
        const response = await fetch(`${API_BASE}/festivals/${currentFestivalId}/alerts`);
        alerts = await response.json();
        renderAlerts();
    } catch (error) {
        showToast('Failed to load alerts');
        console.error(error);
    }
}

async function handleCreateArea(e) {
    e.preventDefault();
    
    if (!currentFestivalId) {
        showToast('Please select a festival first');
        return;
    }
    
    const areaData = {
        name: document.getElementById('areaName').value,
        description: document.getElementById('areaDescription').value,
        areaType: document.getElementById('areaType').value
    };

    try {
        const response = await fetch(`${API_BASE}/festivals/${currentFestivalId}/areas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(areaData)
        });

        if (response.ok) {
            showToast('✨ Area created successfully!');
            e.target.reset();
            await loadAreas();
            await loadDashboard();
        } else {
            showToast('Failed to create area');
        }
    } catch (error) {
        showToast('Error creating area');
        console.error(error);
    }
}

async function handleSubmitReport(e) {
    e.preventDefault();
    
    if (!currentFestivalId) {
        showToast('Please select a festival first');
        return;
    }
    
    const reportData = {
        areaId: parseInt(document.getElementById('reportArea').value),
        crowdLevel: document.querySelector('input[name="crowdLevel"]:checked').value,
        note: document.getElementById('reportNote').value
    };

    try {
        const response = await fetch(`${API_BASE}/festivals/${currentFestivalId}/reports`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reportData)
        });

        if (response.ok) {
            showToast('📊 Report submitted successfully!');
            e.target.reset();
            await Promise.all([loadReports(), loadAlerts(), loadDashboard()]);
        } else {
            showToast('Failed to submit report');
        }
    } catch (error) {
        showToast('Error submitting report');
        console.error(error);
    }
}

async function resolveAlert(alertId) {
    if (!currentFestivalId) return;
    
    try {
        const response = await fetch(`${API_BASE}/festivals/${currentFestivalId}/alerts/${alertId}/resolve`, {
            method: 'PATCH'
        });

        if (response.ok) {
            showToast('✅ Alert resolved!');
            await loadAlerts();
            await loadDashboard();
        } else {
            showToast('Failed to resolve alert');
        }
    } catch (error) {
        showToast('Error resolving alert');
        console.error(error);
    }
}

// Rendering Functions
function renderAreas() {
    const container = document.getElementById('areasList');
    
    if (areas.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🎪</div>
                <p>No festival areas yet. Create one to get started!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = areas.map(area => `
        <div class="area-card">
            <div class="area-name">${escapeHtml(area.name)}</div>
            <div class="area-type">${escapeHtml(area.areaType)}</div>
            <div class="area-description">${escapeHtml(area.description || 'No description')}</div>
        </div>
    `).join('');
}

function renderReports() {
    const container = document.getElementById('reportsList');
    
    if (reports.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📊</div>
                <p>No reports submitted yet.</p>
            </div>
        `;
        return;
    }

    const sortedReports = [...reports].sort((a, b) => 
        new Date(b.submittedAt) - new Date(a.submittedAt)
    ).slice(0, 10);

    container.innerHTML = sortedReports.map(report => {
        const emoji = {
            'LOW': '🟢',
            'MEDIUM': '🟡',
            'FULL': '🔴'
        }[report.crowdLevel];

        const badgeClass = {
            'LOW': 'badge-low',
            'MEDIUM': 'badge-medium',
            'FULL': 'badge-full'
        }[report.crowdLevel];

        return `
            <div class="report-card">
                <div class="report-level">${emoji}</div>
                <div class="report-content">
                    <div class="report-area">${escapeHtml(report.area?.name || 'Unknown Area')}</div>
                    <div class="report-note">${escapeHtml(report.note || 'No additional notes')}</div>
                    <div class="report-time">${formatTime(report.submittedAt)}</div>
                </div>
                <div class="report-badge ${badgeClass}">${report.crowdLevel}</div>
            </div>
        `;
    }).join('');
}

function renderAlerts() {
    const container = document.getElementById('alertsList');
    
    if (alerts.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">✅</div>
                <p>No active alerts. All clear!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = alerts.map(alert => `
        <div class="alert-card">
            <div class="alert-content">
                <div class="alert-area">🚨 ${escapeHtml(alert.area?.name || 'Unknown Area')}</div>
                <div class="alert-message">${escapeHtml(alert.message)}</div>
                <div class="alert-time">${formatTime(alert.createdAt)}</div>
            </div>
            <button class="btn btn-secondary" onclick="resolveAlert(${alert.id})">
                Resolve
            </button>
        </div>
    `).join('');
}

function populateAreaSelect() {
    const select = document.getElementById('reportArea');
    select.innerHTML = '<option value="">Select an area...</option>' + 
        areas.map(area => `
            <option value="${area.id}">${escapeHtml(area.name)}</option>
        `).join('');
}

// Utility Functions
function formatTime(timestamp) {
    if (!timestamp) return 'Unknown time';
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now - date;
    
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);
    
    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    return `${days}d ago`;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(message) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.add('show');
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Auto-refresh every 30 seconds
setInterval(() => {
    if (currentFestivalId) {
        loadReports();
        loadAlerts();
        loadDashboard();
    }
}, 30000);
