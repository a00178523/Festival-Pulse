# Festival Pulse - Interactive Map Frontend

Modern React application with interactive festival map builder and real-time crowd monitoring.

## Features

### 🎨 Map Builder Mode
- Draw custom festival areas using polygon tool
- Click to add vertices, complete to save
- Assign names, types, and colors to areas
- Visual feedback with neon styling

### 👁️ Monitor Mode
- Real-time crowd level visualization
- Color-coded overlays:
  - 🟢 **GREEN** - LOW crowd level
  - 🟡 **YELLOW** - MEDIUM crowd level
  - 🟠 **ORANGE** - FULL crowd level
  - 🚨 **RED PULSING** - ACTIVE ALERT
- Click areas for details
- Live legend display

## Tech Stack

- **React 18** + **TypeScript**
- **Vite** - Fast build tool
- **React-Konva** - Canvas rendering
- **Zustand** - State management
- **Konva.js** - 2D canvas library

## Getting Started

### Install Dependencies
\`\`\`bash
npm install
\`\`\`

### Run Development Server
\`\`\`bash
npm run dev
\`\`\`

Frontend runs on `http://localhost:3000`

Backend API proxy configured to `http://localhost:8080`

### Build for Production
\`\`\`bash
npm run build
\`\`\`

## Usage

1. **Create Festival** - Click "+ New Festival" to create a festival
2. **Switch to Builder Mode** - Click "✏️ Builder Mode"
3. **Draw Areas** - Click "Draw New Area", click to add points, complete when done
4. **Monitor Crowds** - Switch to "👁️ Monitor Mode" to see live crowd levels
5. **View Alerts** - Areas with alerts pulse red

## API Integration

All API calls proxy to Spring Boot backend at `/api`:
- `GET /api/festivals` - List festivals
- `POST /api/festivals` - Create festival
- `GET /api/festivals/{id}/areas` - Get areas
- `POST /api/festivals/{id}/areas` - Create area
- `GET /api/festivals/{id}/alerts` - Get alerts

## Color System

Areas have:
- **Base Color** - Identity color (assigned on creation)
- **Crowd Overlay** - Dynamic color based on crowd level
- **Alert Glow** - Red pulsing effect for active alerts

## Project Structure

\`\`\`
src/
├── components/
│   ├── MapBuilder.tsx    # Interactive drawing tool
│   └── MapViewer.tsx     # Live monitoring display
├── utils/
│   └── colors.ts         # Color constants
├── types.ts              # TypeScript interfaces
├── store.ts              # Zustand state management
├── api.ts                # API client
├── App.tsx               # Main application
└── main.tsx              # Entry point
\`\`\`
