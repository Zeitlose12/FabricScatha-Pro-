package namelessju.scathapro.fabric.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import namelessju.scathapro.fabric.alerts.CustomAlertMode;
import namelessju.scathapro.fabric.alerts.CustomAlertModeManager;
import namelessju.scathapro.fabric.alerts.AlertModeManager;

public class CustomAlertModeScreen extends Screen {
    private final Screen parent;
    private final CustomAlertModeManager manager;
    private final AlertModeManager modeManager;

    // Layout/Scroll
    private int scrollY = 0; private int maxScroll = 0;

    public CustomAlertModeScreen(Screen parent, CustomAlertModeManager manager, AlertModeManager modeManager) {
        super(Text.literal("Custom Alert Modes"));
        this.parent = parent; this.manager = manager; this.modeManager = modeManager;
    }

    @Override protected void init() {
        this.clearChildren();
        int y = 40; int x = this.width / 2 - 300;
        // Create new mode
        addDrawableChild(ButtonWidget.builder(Text.literal("New Mode"), b -> openNew())
            .dimensions(x, y, 140, 20).build());
        y += 28;
        // List modes
        for (var m : manager.getAll()) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Set: " + m.displayName()), b -> {
                modeManager.register(m);
                modeManager.setById(m.id());
            }).dimensions(x, y, 180, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Edit"), b -> openEdit(m))
                .dimensions(x + 190, y, 100, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Export"), b -> exportMode(m))
                .dimensions(x + 300, y, 100, 20).build());
            y += 24;
        }
        y += 8;
        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> { manager.save(); close(); })
            .dimensions(x, y, 140, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
            .dimensions(x + 150, y, 140, 20).build());
    }

    private void exportMode(CustomAlertMode m) {
        try {
            var gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            var o = new com.google.gson.JsonObject();
            o.addProperty("id", m.id());
            o.addProperty("name", m.displayName());
            var sm = new com.google.gson.JsonObject(); for (var e : m.getSounds().entrySet()) sm.addProperty(e.getKey(), e.getValue()); o.add("sounds", sm);
            var tm = new com.google.gson.JsonObject(); for (var e : m.getTitles().entrySet()) tm.addProperty(e.getKey(), e.getValue()); o.add("titles", tm);
            var vm = new com.google.gson.JsonObject(); for (var e : m.getVolumes().entrySet()) vm.addProperty(e.getKey(), e.getValue()); o.add("volumes", vm);
            java.nio.file.Path dir = namelessju.scathapro.fabric.FabricConfig.getConfigPath().getParent().resolve("scathapro");
            java.nio.file.Files.createDirectories(dir);
            java.nio.file.Path f = dir.resolve("custom_mode_" + m.id() + ".json");
            java.nio.file.Files.writeString(f, gson.toJson(o));
        } catch (Exception ignored) {}
    }

    private void openNew() {
        this.clearChildren();
        int y = 40; int x = this.width / 2 - 220;
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> init())
            .dimensions(x, y, 120, 20).build());
        y += 28;
        TextFieldWidget idTf = new TextFieldWidget(this.textRenderer, x, y, 200, 20, Text.literal("id")); y += 24;
        TextFieldWidget nameTf = new TextFieldWidget(this.textRenderer, x, y, 200, 20, Text.literal("name")); y += 28;
        addDrawableChild(idTf); addDrawableChild(nameTf);
        addDrawableChild(ButtonWidget.builder(Text.literal("Create"), b -> {
            String id = idTf.getText(); String name = nameTf.getText();
            if (id != null && !id.isEmpty()) {
                var m = new namelessju.scathapro.fabric.alerts.CustomAlertMode(id, (name == null || name.isEmpty()) ? id : name);
                manager.add(m);
                manager.save();
                init();
            }
        }).dimensions(x, y, 120, 20).build());
    }

    private String friendlyName(String key) {
        return switch (key) {
            case "worm_spawn" -> "Worm Spawn";
            case "scatha_spawn" -> "Scatha Spawn";
            case "goblin_spawn" -> "Goblin Spawn";
            case "jerry_spawn" -> "Jerry Spawn";
            case "pet_drop_rare" -> "Pet Drop (Rare)";
            case "pet_drop_epic" -> "Pet Drop (Epic)";
            case "pet_drop_legendary" -> "Pet Drop (Legendary)";
            case "achievement_unlock" -> "Achievement";
            case "milestone_reached" -> "Milestone";
            case "high_heat" -> "High Heat";
            case "cooldown_ready" -> "Cooldown Ready";
            case "notification" -> "Notification";
            case "warning" -> "Warning";
            case "error" -> "Error";
            default -> key;
        };
    }

    private void openEdit(CustomAlertMode cm) {
        this.clearChildren();
        int x = this.width / 2 - 320; int yTop = 40;
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> init())
            .dimensions(x, yTop, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> { manager.save(); openEdit(cm); })
            .dimensions(x + 110, yTop, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Export"), b -> { exportMode(cm); })
            .dimensions(x + 200, yTop, 80, 20).build());
        yTop += 28;

        // Keys
        String[] keys = new String[]{
            "pet_drop_rare","pet_drop_epic","pet_drop_legendary",
            "worm_spawn","scatha_spawn","goblin_spawn","jerry_spawn",
            "achievement_unlock","milestone_reached",
            "high_heat","cooldown_ready",
            "notification","warning","error"
        };

        int y = yTop - scrollY;
        for (String k : keys) {
            String title = cm.getTitle(k) != null ? cm.getTitle(k) : "";
            String mapped = cm.getSounds().get(k) != null ? cm.getSounds().get(k) : k;
            float vol = cm.getVolume(k, 1.0f);

            // Section Header
            addDrawableChild(ButtonWidget.builder(Text.literal("§e" + friendlyName(k)), b->{}).dimensions(x, y, 640, 20).build()).active=false;
            y += 22;

            // Title + Subtitle (readonly)
            TextFieldWidget titleTf = new TextFieldWidget(this.textRenderer, x, y, 320, 20, Text.literal("Title")); titleTf.setText(title); addDrawableChild(titleTf);
            TextFieldWidget subTf = new TextFieldWidget(this.textRenderer, x + 330, y, 310, 20, Text.literal("Subtitle")); subTf.setText("(dynamic)"); subTf.setEditable(false); addDrawableChild(subTf);
            y += 24;

            // Sound mapping field
            TextFieldWidget soundTf = new TextFieldWidget(this.textRenderer, x, y, 320, 20, Text.literal("Sound key")); soundTf.setText(mapped); addDrawableChild(soundTf);
            addDrawableChild(ButtonWidget.builder(Text.literal("Set"), b -> {
                cm.setSound(k, soundTf.getText()); manager.save();
            }).dimensions(x + 330, y, 60, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
                cm.setSound(k, null); manager.save(); openEdit(cm);
            }).dimensions(x + 395, y, 70, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), b -> {
                cm.setSound(k, ""); manager.save(); openEdit(cm);
            }).dimensions(x + 470, y, 70, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Play current"), b -> {
                var sp = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                if (sp != null && sp.getSoundManager() != null) {
                    String key = cm.mapSound(k);
                    sp.getSoundManager().playAlertSound(key, cm.getVolume(k, 1.0f), 1.0f);
                }
            }).dimensions(x + 545, y, 95, 20).build());
            y += 24;

            // Volume slider (0.1..1.0)
            namelessju.scathapro.fabric.gui.widgets.FlatSliderWidget volSlider = new namelessju.scathapro.fabric.gui.widgets.FlatSliderWidget(x, y, 300, 20, "Volume", 0.1, 1.0, vol, v->{ cm.setVolume(k, v.floatValue()); manager.save(); });
            addDrawableChild(volSlider);
            // Apply title button
            addDrawableChild(ButtonWidget.builder(Text.literal("Apply title"), b -> { cm.setTitle(k, titleTf.getText()); manager.save(); })
                .dimensions(x + 310, y, 120, 20).build());
            y += 30;
        }

        // Scrolllimit grob abschätzen
        maxScroll = Math.max(0, (y - (this.height - 60)));
    }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Kein Vanilla-Blur, stattdessen leichte Abdunklung
        ctx.fill(0, 0, this.width, this.height, 0xEE000000);
        int clipLeft = 20, clipTop = 20, clipRight = this.width-20, clipBottom = this.height-20;
        try { ctx.enableScissor(clipLeft, clipTop, clipRight, clipBottom); } catch (Throwable ignored) {}
        super.render(ctx, mouseX, mouseY, delta);
        try { ctx.disableScissor(); } catch (Throwable ignored) {}
    }

    @Override public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        int step = 24; scrollY = Math.max(0, Math.min(maxScroll, scrollY - (int)(vy * step))); init(); return true;
    }

    @Override public void close() { MinecraftClient.getInstance().setScreen(parent); }
}
