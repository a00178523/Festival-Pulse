export interface Point {
  x: number;
  y: number;
}

export interface Festival {
  id: number;
  name: string;
  description?: string;
  startDate?: string;
  endDate?: string;
}

export interface FestivalArea {
  id?: number;
  festivalId?: number;
  name: string;
  description?: string;
  areaType: string;
  coordinates?: Point[];
  baseColor: string;
  crowdLevel?: 'LOW' | 'MEDIUM' | 'FULL';
  hasAlert?: boolean;
}

export interface CrowdReport {
  id: number;
  area: FestivalArea;
  crowdLevel: 'LOW' | 'MEDIUM' | 'FULL';
  note?: string;
  submittedAt: string;
}

export interface CrowdAlert {
  id: number;
  area: FestivalArea;
  message: string;
  status: 'ACTIVE' | 'RESOLVED';
  createdAt: string;
}

export interface DashboardSummary {
  totalAreas: number;
  recentReports: CrowdReport[];
  activeAlerts: CrowdAlert[];
}

export type ViewMode = 'builder' | 'monitor';
