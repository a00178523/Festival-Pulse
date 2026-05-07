# Festival Pulse - Quick Start Guide

## 🚀 Running the Application

### 1. Start Spring Boot Backend
```bash
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`

### 2. Start React Frontend
```bash
cd frontend
npm run dev
```
Frontend runs on `http://localhost:3000`

### 3. Open Browser
Navigate to `http://localhost:3000`

## 📝 Usage Flow

### Step 1: Create a Festival
1. Click **"+ New Festival"** button
2. Enter festival name (e.g., "Electric Picnic 2024")
3. Add description (optional)
4. Click **"Create Festival"**

### Step 2: Build Your Map
1. Switch to **"✏️ Builder Mode"**
2. Click **"✏️ Draw New Area"**
3. Click on canvas to add points (vertices of polygon)
4. Click **"✓ Complete Area"** when done
5. Enter area name (e.g., "Main Stage")
6. Enter area type (STAGE/FOOD/AMENITY/ENTRANCE)
7. Area is saved with random color

Repeat to create multiple areas!

### Step 3: Monitor Crowds
1. Switch to **"👁️ Monitor Mode"**
2. View your festival map with all areas
3. Areas show crowd levels with color overlays:
   - 🟢 **GREEN** = LOW crowd
   - 🟡 **YELLOW** = MEDIUM crowd
   - 🟠 **ORANGE** = FULL crowd
   - 🚨 **RED PULSING** = ACTIVE ALERT

### Step 4: Submit Crowd Reports (via API or old UI)
Use the Spring Boot API to submit crowd reports:
```bash
curl -X POST http://localhost:8080/api/festivals/1/reports \
  -H "Content-Type: application/json" \
  -d '{
    "areaId": 1,
    "crowdLevel": "FULL",
    "note": "Very crowded near stage"
  }'
```

When a FULL report is submitted, an alert is automatically created and the area will pulse RED on the map!

## 🎨 Features

- **Interactive Drawing** - Draw custom polygon shapes for festival areas
- **Real-time Visualization** - See crowd levels update live
- **Color-Coded System** - Instant visual feedback on crowd density
- **Alert System** - Automatic alerts for FULL areas with pulsing effect
- **Multiple Festivals** - Switch between different festivals
- **Persistent Storage** - All data saved to H2 database

## 🔧 Troubleshooting

**Frontend not loading?**
- Check Spring Boot is running on port 8080
- Check console for errors (F12 in browser)
- Verify proxy is working in vite.config.ts

**Can't draw areas?**
- Make sure you're in Builder Mode
- Click "Draw New Area" first
- Need at least 3 points to complete an area

**Areas not showing?**
- Check browser console for errors
- Verify coordinates are being saved (check backend logs)
- Try refreshing the page

## 🎯 Next Steps

1. Add WebSocket support for real-time updates
2. Add area editing (move/resize polygons)
3. Add background image upload for festival grounds
4. Add crowd report submission UI in React
5. Add historical crowd data charts
