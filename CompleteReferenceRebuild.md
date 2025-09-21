# Komplette Neugestaltung - Exaktes Referenzbild

## 🔧 **Komplett überarbeitete Änderungen:**

### 📦 **Hintergrundbox**
- **Größe**: 400×130 (viel größer für vollständige Abdeckung)
- **Farbe**: `0xC0303030` (heller grau, nicht fast schwarz)
- **Rahmen**: `0x80FFFFFF` (hellerer Rand)
- **Position**: (-4,-4) für korrekten Offset

### 🎯 **Exaktes Layout nach Referenzbild:**

```
┌────────────────────────────────────────────────────┐
│🕷️ Scatha Farming!                                  │
│Pets       Worms        Scathas       Total         │
│🔵 4       3154         789           3943          │
│🟣 7       0            0             0             │
│🟠 1       No worms spawned yet                     │
│Scathas since last pet drop: ??                    │
│Day 18 (83%) / 00:03:50                             │
│720 31 410 / -X (16.6% to wall)                    │
└────────────────────────────────────────────────────┘
```

### 📐 **Neue Positionierung:**

#### **Titel-Bereich**
- **Title-Icon**: (2,4) mit 1.2f Skalierung
- **Titel-Text**: (20,4) "Scatha Farming!"

#### **Header-Zeile (Y=22)**
- **Pets**: X=4 (grün)
- **Worms**: X=110 (gelb)  
- **Scathas**: X=220 (gelb)
- **Total**: X=310 (weiß)

#### **Erste Datenzeile (Y=40)**
- **🔵 Icon**: (8,40), **"4"**: (28,40)
- **Worms**: "3154" bei (110,40)
- **Scathas**: "789" bei (220,40)  
- **Total**: "3943" bei (310,40)

#### **Zweite Datenzeile (Y=55)**
- **🟣 Icon**: (8,55), **"7"**: (28,55)
- **Alle anderen**: "0" bei (110,220,310)

#### **Dritte Datenzeile (Y=70)**
- **🟠 Icon**: (8,70), **"1"**: (28,70)
- **"No worms spawned yet"**: (110,70)

#### **Info-Zeilen**
- **"Scathas since last pet drop: ??"**: (4,85)
- **Day-Info**: (4,100)
- **Koordinaten**: (4,115)

### ✨ **Icons vergrößert**
- **Alle Icons**: 1.2f Skalierung (20% größer)
- **Bessere Sichtbarkeit** und Übereinstimmung mit Referenzbild

### 🎨 **Farbverbesserungen**
- **Hellerer Hintergrund** statt fast schwarz
- **Sichtbarere Rahmen**
- **Besserer Kontrast** zu den Texten

## 🎯 **Erwartetes Ergebnis:**
Das Overlay sollte jetzt **exakt** wie das Referenzbild aussehen:
- ✅ Richtige Hintergrundfarbe und -größe
- ✅ Korrekte Tabellen-Struktur
- ✅ Sichtbare Icons in richtiger Größe
- ✅ Vollständige Koordinatenzeile
- ✅ "No worms spawned yet" an der richtigen Position

**Build erfolgreich - bereit zum Testen!** 🚀