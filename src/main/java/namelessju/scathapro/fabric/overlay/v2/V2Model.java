package namelessju.scathapro.fabric.overlay.v2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class V2Model {
    public final V2Container root;
    public final V2BackgroundBox background;
    public final V2Text title;

    // Hauptblöcke wie im Screenshot
    public final V2Text headerPets;
    public final V2Text headerWorms;
    public final V2Text headerScathas;
    public final V2Text headerTotal;
    public final V2Text petsBlue;
    public final V2Text petsPurple;
    public final V2Text petsOrange;
    public final V2Text wormsCount;
    public final V2Text scathasCount;
    public final V2Text totalCount;
    public final V2Text totalPercent;

    public final V2Text scathaText; // weiterhin klassische Zeilen
    public final V2Text wormText;
    public final V2Text totalText;
    public final V2Text streakText;
    public final V2ProgressBar bar;
    public final V2Text wormTimerText;
    public final V2Text scathaTimerText;
    public final V2Text dayTimeText;
    public final V2Text coordsText;
    
    // Offizielle Icons
    public V2IconTexture bluePetIcon;
    public V2IconTexture purplePetIcon;
    public V2IconTexture orangePetIcon;
    public V2IconTexture scathaIcon;
    public V2IconTexture wormIcon;
    public V2IconTexture titleIcon;
    
    // Alte Item-Icons (für Fallback)
    public V2IconItem scathaItemIcon;
    public V2IconItem wormItemIcon;

    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public V2Model() {
        root = new V2Container(0,0);
        // Hintergrundbox kompakt aber hoch genug für alle Texte
        background = new V2BackgroundBox(-5, -5, 320, 190, 0x50000000).setBorder(0x60FFFFFF);
        
        // Titel noch größer für maximale Sichtbarkeit
        title = new V2Text(Text.literal("Scatha Farming!"), 0xFFFFAA00, 30, 8, 2.0f); // Noch größer

        // Header noch größer, Spalten sehr kompakt für schmales Overlay
        headerPets = new V2Text(Text.literal("Pets"), 0xFF55FF55, 8, 38, 1.5f);      // Noch größer
        headerWorms = new V2Text(Text.literal("Worms"), 0xFFFFFFAA, 75, 38, 1.5f);   // Kompakter
        headerScathas = new V2Text(Text.literal("Scathas"), 0xFFFFFF55, 135, 38, 1.5f); // Kompakter
        headerTotal = new V2Text(Text.literal("Total"), 0xFFFFFFFF, 200, 38, 1.5f);    // Kompakter
        
        // Pet-Drop-Zahlen noch größer
        petsBlue = new V2Text(Text.literal("4"), 0xFF55AAFF, 28, 55, 1.6f);   // Noch größer
        petsPurple = new V2Text(Text.literal("7"), 0xFFAA55FF, 28, 72, 1.6f); // Noch größer
        petsOrange = new V2Text(Text.literal("1"), 0xFFFFAA00, 28, 89, 1.6f); // Noch größer
        
        // Nur EINE Zahl pro Spalte - kompakt positioniert
        wormsCount = new V2Text(Text.literal("2"), 0xFFFFFFFF, 88, 55, 1.6f);      // Kompakter
        scathasCount = new V2Text(Text.literal("2"), 0xFFFFFFFF, 152, 55, 1.6f);    // Kompakter
        totalCount = new V2Text(Text.literal("4"), 0xFFFFFFFF, 210, 55, 1.6f);      // Kompakter
        
        // Keine überflüssigen Nullen mehr - alles versteckt
        var wormsCount2 = new V2Text(Text.literal(""), 0xFFFFFFFF, -100, -100, 1.0f); // Versteckt
        var scathasCount2 = new V2Text(Text.literal(""), 0xFFFFFFFF, -100, -100, 1.0f); // Versteckt
        var totalCount2 = new V2Text(Text.literal(""), 0xFFFFFFFF, -100, -100, 1.0f); // Versteckt
        
        var wormsCount3 = new V2Text(Text.literal(""), 0xFFFFFFFF, -100, -100, 1.0f); // Versteckt
        var scathasCount3 = new V2Text(Text.literal(""), 0xFFFFFFFF, -100, -100, 1.0f); // Versteckt
        var totalCount3 = new V2Text(Text.literal(""), 0xFFFFFFFF, -100, -100, 1.0f); // Versteckt
        
        // "No worms spawned yet" Text versteckt - wird dynamisch angezeigt
        wormText = new V2Text(Text.literal(""), 0xFFFFAA00, -100, -100, 1.1f); // Versteckt
        
        // Info-Zeilen nach unten verschoben für Worm-Text Platz
        scathaText = new V2Text(Text.literal("Scathas since last pet drop: ??"), 0xFFFFFFFF, 8, 125, 1.2f);
        
        // World info weiter unten positioniert
        dayTimeText = new V2Text(Text.literal("Day 18 (83%) / 00:03:50"), 0xFFE0E0E0, 8, 145, 1.2f);
        coordsText = new V2Text(Text.literal("720 31 410 / -X (16.6%)"), 0xFFB0B0B0, 8, 160, 1.2f);
        
        // Versteckte/unbenutzte Elemente  
        totalPercent = new V2Text(Text.literal(""), 0xFFB0B0B0, -100, -100, 1.0f);
        totalText = new V2Text(Text.literal(""), 0xFFAAAAAA, -100, -100, 1.0f);
        streakText = new V2Text(Text.literal(""), 0xFF55FFFF, -100, -100, 1.0f);
        bar = new V2ProgressBar(-100, -100, 160, 8, 1.0f, 0xFF55FF55, 0x80555555);
        wormTimerText = new V2Text(Text.literal(""), 0xFFBBBBBB, -100, -100, 1.0f);
        scathaTimerText = new V2Text(Text.literal(""), 0xFFBBBBBB, -100, -100, 1.0f);
        
        // Zusätzliche Zeilen zu Root hinzufügen
        root.add(wormsCount2).add(scathasCount2).add(totalCount2);
        root.add(wormsCount3).add(scathasCount3).add(totalCount3);
        
        // Icons für Referenzbild-Layout
        try {
            // Pet-Icons massiv vergrößert - jetzt deutlich sichtbar!
            bluePetIcon = new V2IconTexture(Identifier.of("scathapro", "textures/overlay/scatha_pet_rare.png"), 
                                          3, 48, 1.2f, 64, 64);    // Massiv vergrößert auf 1.2f
            purplePetIcon = new V2IconTexture(Identifier.of("scathapro", "textures/overlay/scatha_pet_epic.png"), 
                                            3, 67, 1.2f, 64, 64);   // Massiv vergrößert, Y=67
            orangePetIcon = new V2IconTexture(Identifier.of("scathapro", "textures/overlay/scatha_pet_legendary.png"), 
                                            3, 86, 1.2f, 64, 64);   // Massiv vergrößert, Y=86
            
            // Title-Icon massiv vergrößert für maximale Sichtbarkeit
            titleIcon = new V2IconTexture(Identifier.of("scathapro", "textures/overlay/scatha_icons/default.png"), 
                                        2, 3, 2.2f, 512, 512);     // Massiv vergrößert auf 2.2f
            
            // Worm/Scatha Icons sind in diesem Layout nicht sichtbar (versteckt)
            wormIcon = null;
            scathaIcon = null;
            
            // Fallback Item-Icons (versteckt für dieses Layout)
            scathaItemIcon = null;
            wormItemIcon = null;
            
        } catch (Exception e) {
            // Fallback: nur Item-Icons verwenden
            bluePetIcon = null;
            purplePetIcon = null;
            orangePetIcon = null;
            scathaIcon = null;
            wormIcon = null;
            scathaItemIcon = null;
            wormItemIcon = null;
        }

        root.add(background)
            .add(title)
            .add(headerPets).add(headerWorms).add(headerScathas).add(headerTotal)
            .add(petsBlue).add(petsPurple).add(petsOrange)
            .add(wormsCount).add(scathasCount).add(totalCount).add(totalPercent)
            .add(scathaText).add(wormText).add(totalText).add(streakText)
            .add(bar).add(wormTimerText).add(scathaTimerText)
            .add(dayTimeText).add(coordsText);
            
        // Offizielle Icons hinzufügen (falls verfügbar)
        if (titleIcon != null) root.add(titleIcon);
        if (bluePetIcon != null) root.add(bluePetIcon);
        if (purplePetIcon != null) root.add(purplePetIcon);
        if (orangePetIcon != null) root.add(orangePetIcon);
        if (scathaIcon != null) root.add(scathaIcon);
        if (wormIcon != null) root.add(wormIcon);
        
        // Fallback Item-Icons hinzufügen (falls verfügbar)
        if (scathaItemIcon != null) root.add(scathaItemIcon);
        if (wormItemIcon != null) root.add(wormItemIcon);
    }

    public String toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("version", 1);
        // Background
        JsonObject bg = new JsonObject();
        bg.addProperty("visible", background.isVisible());
        bg.addProperty("x", background.getX());
        bg.addProperty("y", background.getY());
        bg.addProperty("color", background.getColor());
        if (background.getBorderColor() != null) bg.addProperty("borderColor", background.getBorderColor()); else bg.add("borderColor", null);
        o.add("background", bg);
        // Title
        JsonObject titleO = new JsonObject();
        titleO.addProperty("x", title.getX()); titleO.addProperty("y", title.getY()); titleO.addProperty("scale", title.getScale()); titleO.addProperty("color", title.getColor());
        o.add("title", titleO);
        // Text elements (subset)
        JsonObject texts = new JsonObject();
        addTextObj(texts, "headerPets", headerPets);
        addTextObj(texts, "headerWorms", headerWorms);
        addTextObj(texts, "headerScathas", headerScathas);
        addTextObj(texts, "headerTotal", headerTotal);
        addTextObj(texts, "petsBlue", petsBlue);
        addTextObj(texts, "petsPurple", petsPurple);
        addTextObj(texts, "petsOrange", petsOrange);
        addTextObj(texts, "wormsCount", wormsCount);
        addTextObj(texts, "scathasCount", scathasCount);
        addTextObj(texts, "totalCount", totalCount);
        addTextObj(texts, "totalPercent", totalPercent);
        addTextObj(texts, "scathaText", scathaText);
        addTextObj(texts, "wormText", wormText);
        addTextObj(texts, "totalText", totalText);
        addTextObj(texts, "streakText", streakText);
        addTextObj(texts, "wormTimerText", wormTimerText);
        addTextObj(texts, "scathaTimerText", scathaTimerText);
        addTextObj(texts, "dayTimeText", dayTimeText);
        addTextObj(texts, "coordsText", coordsText);
        o.add("texts", texts);
        // Bar
        JsonObject barO = new JsonObject();
        barO.addProperty("x", bar.getX()); barO.addProperty("y", bar.getY()); barO.addProperty("scale", bar.getScale()); barO.addProperty("visible", bar.isVisible());
        barO.addProperty("fg", bar.getFgColor());
        barO.addProperty("bg", bar.getBgColor());
        o.add("bar", barO);
        return G.toJson(o);
    }

    private static void addTextObj(JsonObject parent, String key, V2Text t) {
        JsonObject o = new JsonObject();
        o.addProperty("x", t.getX()); o.addProperty("y", t.getY());
        o.addProperty("scale", t.getScale()); o.addProperty("visible", t.isVisible());
        o.addProperty("color", t.getColor());
        parent.add(key, o);
    }

    public static V2Model fromJson(String json) {
        try {
            JsonObject o = G.fromJson(json, JsonObject.class);
            V2Model m = new V2Model();
            if (o.has("title")) {
                var to = o.getAsJsonObject("title");
                m.title.setPosition(to.get("x").getAsInt(), to.get("y").getAsInt()); m.title.setScale(to.get("scale").getAsFloat()); m.title.setColor(to.get("color").getAsInt());
            }
            if (o.has("background")) {
                var bo = o.getAsJsonObject("background");
                m.background.setVisible(bo.get("visible").getAsBoolean());
                m.background.setPosition(bo.get("x").getAsInt(), bo.get("y").getAsInt());
                if (bo.has("color")) m.background.setColor(bo.get("color").getAsInt());
                if (bo.has("borderColor") && !bo.get("borderColor").isJsonNull()) m.background.setBorder(bo.get("borderColor").getAsInt()); else m.background.setBorder(null);
            }
            if (o.has("texts")) {
                var ts = o.getAsJsonObject("texts");
                applyText(ts, "headerPets", m.headerPets);
                applyText(ts, "headerWorms", m.headerWorms);
                applyText(ts, "headerScathas", m.headerScathas);
                applyText(ts, "headerTotal", m.headerTotal);
                applyText(ts, "petsBlue", m.petsBlue);
                applyText(ts, "petsPurple", m.petsPurple);
                applyText(ts, "petsOrange", m.petsOrange);
                applyText(ts, "wormsCount", m.wormsCount);
                applyText(ts, "scathasCount", m.scathasCount);
                applyText(ts, "totalCount", m.totalCount);
                applyText(ts, "totalPercent", m.totalPercent);
                applyText(ts, "scathaText", m.scathaText);
                applyText(ts, "wormText", m.wormText);
                applyText(ts, "totalText", m.totalText);
                applyText(ts, "streakText", m.streakText);
                applyText(ts, "wormTimerText", m.wormTimerText);
                applyText(ts, "scathaTimerText", m.scathaTimerText);
                applyText(ts, "dayTimeText", m.dayTimeText);
                applyText(ts, "coordsText", m.coordsText);
            }
            if (o.has("bar")) {
                var bo = o.getAsJsonObject("bar");
                m.bar.setPosition(bo.get("x").getAsInt(), bo.get("y").getAsInt());
                m.bar.setScale(bo.get("scale").getAsFloat());
                m.bar.setVisible(bo.get("visible").getAsBoolean());
                if (bo.has("fg") || bo.has("bg")) {
                    int fg = bo.has("fg")? bo.get("fg").getAsInt() : m.bar.getFgColor();
                    int bg = bo.has("bg")? bo.get("bg").getAsInt() : m.bar.getBgColor();
                    m.bar.setColors(fg, bg);
                }
            }
            return m;
        } catch (Exception e) {
            return new V2Model();
        }
    }

    private static void applyText(JsonObject ts, String key, V2Text t) {
        if (!ts.has(key)) return;
        var o = ts.getAsJsonObject(key);
        t.setPosition(o.get("x").getAsInt(), o.get("y").getAsInt());
        t.setScale(o.get("scale").getAsFloat());
        t.setVisible(o.get("visible").getAsBoolean());
        t.setColor(o.get("color").getAsInt());
    }

    public static V2Model defaultModel(){ return new V2Model(); }
}