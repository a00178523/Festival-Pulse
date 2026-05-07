import { create } from 'zustand';
import type { Festival, FestivalArea, CrowdAlert, ViewMode } from './types';

interface AppState {
  // Festival state
  festivals: Festival[];
  currentFestival: Festival | null;
  
  // Area state
  areas: FestivalArea[];
  selectedArea: FestivalArea | null;
  
  // Alert state
  alerts: CrowdAlert[];
  
  // UI state
  viewMode: ViewMode;
  isDrawing: boolean;
  
  // Actions
  setFestivals: (festivals: Festival[]) => void;
  setCurrentFestival: (festival: Festival | null) => void;
  setAreas: (areas: FestivalArea[]) => void;
  setSelectedArea: (area: FestivalArea | null) => void;
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
  alerts: [],
  viewMode: 'monitor',
  isDrawing: false,
  
  setFestivals: (festivals) => set({ festivals }),
  setCurrentFestival: (festival) => set({ currentFestival: festival }),
  setAreas: (areas) => set({ areas }),
  setSelectedArea: (area) => set({ selectedArea: area }),
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
