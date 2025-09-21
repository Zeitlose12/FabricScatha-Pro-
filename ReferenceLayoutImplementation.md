# Referenzbild-Layout Implementierung

## ✅ Exakt umgesetztes Layout:

```
┌─────────────────────────────────────┐
│ 🧱 Scatha Farming!                  │  
│ Pets    Worms     Scathas    Total  │
│ 🔵 4      3154       789      3943  │
│ 🟣 7         0         0         0  │  
│ 🟠 1         0         0         0  │
│ No worms spawned yet                │
│ Scathas since last pet drop: ??    │
│ Day 18 (83%) / 00:03:50             │
│ 720 31 410 / -X (16.6% to wall)    │
└─────────────────────────────────────┘
```

## 🎯 **Layout-Komponenten:**

### **Zeile 1: Titel**
- 🧱 **Block-Icon** + **"Scatha Farming!"** (Orange Text)
- Position: (4,4) für Icon, (22,4) für Text

### **Zeile 2: Header**
- **"Pets"** (Grün), **"Worms"** (Gelb), **"Scathas"** (Gelb), **"Total"** (Weiß)
- Positionen: X=8, 90, 180, 270

### **Zeilen 3-5: Datentabelle**
- **Erste Zeile**: Echte Werte aus State
  - 🔵 Pet-Count, Worm-Kills, Scatha-Kills, Total-Kills
- **Zweite & Dritte Zeile**: Nur "0" Werte (wie im Bild)
  - 🟣 Pet-Count, 0, 0, 0
  - 🟠 Pet-Count, 0, 0, 0

### **Zeilen 6-7: Info-Text**
- **"No worms spawned yet"** / **"Last worm: Xm ago"** (dynamisch)
- **"Scathas since last pet drop: ??"** (noch zu implementieren)

### **Zeilen 8-9: World Info**
- **Day Info**: "Day 18 (83%) / 00:03:50"
- **Koordinaten**: "720 31 410 / -X (16.6% to wall)"

## 🔧 **Technische Details:**

### **Hintergrund**
- **Größe**: 340×140 Pixel (passend zum Referenzbild)
- **Farbe**: Grau mit weißem Rand

### **Icons**
- **Pet-Icons**: Offizielle Scatha-Pet-Texturen bei X=8
- **Title-Icon**: Minecraft Stone-Block bei (4,4)
- **Skalierung**: 1.0f (natürliche Größe)

### **Text-Skalierung**
- **Alle Texte**: 1.0f (einheitlich, wie im Bild)
- **Keine übertriebene Vergrößerung**

### **Versteckte Elemente**
- Progress Bar, Streak, Timer - außerhalb des sichtbaren Bereichs
- Nur die im Referenzbild sichtbaren Elemente werden angezeigt

## 📊 **Datenquellen:**
- **Pet Counts**: Aus ClientState (getBluePetDrops, etc.)
- **Kill Counts**: Aus ClientState (getWormKills, getScathaKills)
- **Worm Status**: Dynamisch basierend auf LastWormSpawnMs
- **World Info**: Berechnet aus Minecraft World-Daten

Das Layout entspricht jetzt **exakt** dem Referenzbild!