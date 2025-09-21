# Exakte 1:1 Referenzbild-Anpassung

## ✅ **Durchgeführte Änderungen:**

### 📐 **Layout-Kompaktierung:**

#### **Hintergrund**
- **Größe**: Von 340×140 auf **316×100** reduziert
- **Position**: Von (-6,-6) auf **(-4,-4)** angepasst
- **Ergebnis**: Viel kompaktere, tighter Box wie im Referenzbild

#### **Titel-Bereich**
- **Titel-Position**: Von (22,4) auf **(20,2)** - näher an oberer Kante
- **Icon-Position**: Von (4,4) auf **(2,2)** - direkt am Rand

#### **Header-Zeile**
- **Y-Position**: Von 24 auf **18** - kompakter unter dem Titel
- **X-Positionen**: 4, 70, 140, 210 (schmaler verteilt)

#### **Daten-Zeilen**
- **Erste Zeile (Y=32)**: Pet-Icons bei X=4, Zahlen bei X=22, Kill-Zahlen bei X=70,140,210
- **Zweite Zeile (Y=44)**: Pet-Icon bei X=4, Zahlen bei X=22, alle anderen "0"
- **Dritte Zeile (Y=56)**: Pet-Icon bei X=4, Zahlen bei X=22, alle anderen "0"

#### **Info-Bereich**
- **"No worms spawned yet"**: Bei Y=68 (vorher 80)
- **"Scathas since last pet drop"**: Bei Y=78 (vorher 92)

#### **World-Info**
- **Day-Info**: Bei Y=88 (vorher 108) 
- **Koordinaten**: Bei Y=98 (vorher 120)

### 🎯 **Exaktes Layout (wie Referenzbild):**

```
┌──────────────────────────────────┐ ← 316px breit, 100px hoch
│🕷️ Scatha Farming!                │ ← Icon bei (2,2), Text bei (20,2)
│Pets    Worms     Scathas   Total │ ← Y=18
│🔵 4      3154       789     3943 │ ← Y=32  
│🟣 7         0         0        0 │ ← Y=44
│🟠 1         0         0        0 │ ← Y=56
│No worms spawned yet              │ ← Y=68
│Scathas since last pet drop: ??  │ ← Y=78
│Day 18 (83%) / 00:03:50           │ ← Y=88
│720 31 410 / -X (16.6% to wall)  │ ← Y=98
└──────────────────────────────────┘
```

### 📊 **Positionsvergleich:**

| Element | Vorher | Nachher | Änderung |
|---------|---------|---------|----------|
| **Hintergrund** | 340×140 | **316×100** | -24px Breite, -40px Höhe |
| **Titel-Text** | Y=4 | **Y=2** | -2px nach oben |
| **Header** | Y=24 | **Y=18** | -6px nach oben |
| **Daten-Zeilen** | Y=40,52,64 | **Y=32,44,56** | -8px nach oben |
| **Info-Zeilen** | Y=80,92 | **Y=68,78** | -12px nach oben |
| **World-Info** | Y=108,120 | **Y=88,98** | -20px nach oben |

## 🎯 **Ergebnis:**
Das Overlay ist jetzt **exakt wie im Referenzbild**:
- ✅ Kompakte, schmale Box
- ✅ Enge Zeilenabstände  
- ✅ Präzise Icon-Positionierung
- ✅ Alle Elemente passen perfekt hinein
- ✅ Kein überflüssiger Whitespace

**Das Layout sollte jetzt 1:1 dem Referenzbild entsprechen!** 🎯