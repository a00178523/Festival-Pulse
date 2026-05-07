import { create } from 'zustand';
import type { Festival, FestivalArea, CrowdAlert, CrowdReport, ViewMode } from './types';

interface AppState {
  festivals: Festival[];
  currentFestival: Festival | null;
  areas: FestivalArea[];
  selectedArea: FestivalArea | null;
  selectedReport: CrowdReport | null;
  alerts: CrowdAlert[];
  viewMode: ViewMode;
  isDrawing: boolean;
  
  setFestivals: (festivals: Festival[]) => void;
  setCurrentFestival: (festival: Festival | null) => void;
  setAreas: (areas: FestivalArea[]) => void;
  setSelectedArea: (area: FestivalArea | null) => void;
  setSelectedReport: (report: CrowdReport | null) => void;
  setAlerts: (alerts: CrowdAlert[]) => void;
  setViewMode: (mode: ViewMode) => void;
  setIsDrawing: (drawing: boolean) => void;
  addArea: (area: FestivalArea) => void;
  updateArea: (area: FestivalArea) => void;
  deleteArea: (areaId: number) => void;
}

export const useStore = create<AppState>((set) => ({
  festivals: [],
  currentFestival: null,
  areas: [],
  selectedArea: null,
  selectedReport: null,
  alerts: [],
  viewMode: 'monitor',
  isDrawing: false,
  
  setFestivals: (festivals) => set({ festivals }),
  setCurrentFestival: (festival) => set({ currentFestival: festival }),
  setAreas: (areas) => set({ areas }),
  setSelectedArea: (area) => set({ selectedArea: area }),
  setSelectedReport: (report) => set({ selectedReport: report }),
  setAlerts: (alerts) => set({ alerts }),
  setViewMode: (mode) => set({ viewMode: mode }),
  setIsDrawing: (drawing) => set({ isDrawing: drawing }),
  
  addArea: (area) => set((state) => ({ 
    areas: [...state.areas, area] 
  })),
  
  updateArea: (area) => set((state) => ({
    areas: state.areas.map(a => a.id === area.id ? area : a)
  })),
  
  deleteArea: (areaId) => set((state) => ({
    areas: state.areas.filter(a => a.id !== areaId)
  })),
}));
