package namelessju.scathapro.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ScathaProFabricClient implements ClientModInitializer {
    public static FabricConfig CONFIG;

    @Override
    public void onInitializeClient() {
        ScathaProFabric.LOGGER.info("[ScathaProFabricClient] Starting client-side initialization...");
        
        try {
            ScathaProFabric.LOGGER.info("[ScathaProFabricClient] Loading configuration...");
            CONFIG = FabricConfig.load();
            
            ScathaProFabric.LOGGER.info("[ScathaProFabricClient] Initializing FabricScathaPro client...");
            // FabricScathaPro Client-Initialisierung
            FabricScathaPro scathaPro = FabricScathaPro.getInstance();
            if (scathaPro != null) {
                scathaPro.onInitializeClient();
                ScathaProFabric.LOGGER.info("[ScathaProFabricClient] FabricScathaPro client initialized successfully.");
            } else {
                ScathaProFabric.LOGGER.warn("[ScathaProFabricClient] FabricScathaPro instance is null!");
            }

            
            ScathaProFabric.LOGGER.info("[ScathaProFabricClient] Registering client hooks (HUD, Keybinds)...");
            // ClientHooks registrieren - das ist wichtig für das Overlay-Rendering!
            namelessju.scathapro.fabric.client.ClientHooks.register();
            
            ScathaProFabric.LOGGER.info("[ScathaProFabricClient] Registering client commands...");
// Client-Commands (/sp, /scathapro)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            com.mojang.brigadier.builder.LiteralArgumentBuilder<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> sp = ClientCommandManager.literal("sp")
                .executes(ctx -> {
                    var mc = MinecraftClient.getInstance();
                    if (mc != null) {
                        // Versuche direkt zu öffnen; falls ein anderes UI sofort wieder übernimmt, setze Fallback per Tick
                        mc.execute(() -> {
                            mc.setScreen(new namelessju.scathapro.fabric.gui.SettingsScreen(mc.currentScreen));
                            namelessju.scathapro.fabric.client.ClientHooks.requestOpenSettings();
                            if (mc.player != null) {
                                mc.player.sendMessage(Text.literal("Scatha-Pro Einstellungen geöffnet."), false);
                            }
                        });
                    }
                    return 1;
                })
                .then(ClientCommandManager.literal("help")
                    .executes(ctx -> {
                        var player = MinecraftClient.getInstance().player;
                        if (player != null) {
                            player.sendMessage(Text.literal("/sp help [page] - Seiten: 1..2"), false);
                            player.sendMessage(Text.literal("Seite 1: toggleOverlay"), false);
                            player.sendMessage(Text.literal("Seite 2: (weitere Befehle folgen)"), false);
                        }
                        return 1;
                    })
                    .then(ClientCommandManager.argument("page", IntegerArgumentType.integer(1, 2))
                        .executes(ctx -> {
                            int page = IntegerArgumentType.getInteger(ctx, "page");
                            var player = MinecraftClient.getInstance().player;
                            if (player != null) {
                                if (page == 1) {
                                    player.sendMessage(Text.literal("Seite 1: toggleOverlay"), false);
                                } else {
                                    player.sendMessage(Text.literal("Seite 2: (weitere Befehle folgen)"), false);
                                }
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("settings")
                    .executes(ctx -> {
                        var mc = MinecraftClient.getInstance();
                        if (mc != null) {
                            mc.execute(() -> {
                                mc.setScreen(new namelessju.scathapro.fabric.gui.SettingsScreen(mc.currentScreen));
                                namelessju.scathapro.fabric.client.ClientHooks.requestOpenSettings();
                            });
                        }
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("toggleOverlay")
                    .executes(ctx -> {
                        var client = MinecraftClient.getInstance();
                        namelessju.scathapro.fabric.client.ClientHooks.toggleHud(client);
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("debug")
                    .executes(ctx -> {
                        var player = MinecraftClient.getInstance().player;
                        if (player != null && CONFIG != null) {
                            player.sendMessage(Text.literal("Config Debug:"), false);
                            player.sendMessage(Text.literal("overlayStyle: " + (CONFIG.overlayStyle != null ? CONFIG.overlayStyle : "null")), false);
                            player.sendMessage(Text.literal("overlayVisible: " + CONFIG.overlayVisible), false);
                            player.sendMessage(Text.literal("alertsEnabled: " + CONFIG.alertsEnabled), false);
                        }
                        return 1;
                    })
                    .then(ClientCommandManager.literal("hooks")
                        .executes(ctx -> {
                            var p = MinecraftClient.getInstance().player;
                            var spInst = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                            boolean clientHooks = namelessju.scathapro.fabric.client.ClientHooks.isRegistered();
                            boolean evtReg = spInst != null && spInst.getEventManager() != null && spInst.getEventManager().isRegistered();
                            boolean evtStarted = spInst != null && spInst.getEventManager() != null && spInst.getEventManager().isGameStarted();
                            int parserCount = spInst != null && spInst.getChestGuiParsingManager() != null ? spInst.getChestGuiParsingManager().getParserCount() : -1;
                            boolean hudShown = namelessju.scathapro.fabric.client.ClientHooks.isHudShown();
                            if (p != null) {
                                p.sendMessage(Text.literal("Hooks:"), false);
                                p.sendMessage(Text.literal(" - ClientHooks.registered: " + clientHooks), false);
                                p.sendMessage(Text.literal(" - EventManager.registered: " + evtReg + ", gameStarted: " + evtStarted), false);
                                p.sendMessage(Text.literal(" - ChestParsers: " + parserCount), false);
                                p.sendMessage(Text.literal(" - HUD shown: " + hudShown), false);
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("debugLogs")
                    .then(ClientCommandManager.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                        .executes(ctx -> {
                            boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                            if (CONFIG != null) {
                                CONFIG.debugLogs = enabled;
                                CONFIG.save();
                            }
                            var player = MinecraftClient.getInstance().player;
                            if (player != null) player.sendMessage(Text.literal("Debug-Logs " + (enabled ? "ein" : "aus")), false);
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("alerts")
                    .then(ClientCommandManager.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                        .executes(ctx -> {
                            boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                            if (CONFIG != null) { CONFIG.alertsEnabled = enabled; CONFIG.save(); }
                            var player = MinecraftClient.getInstance().player;
                            if (player != null) player.sendMessage(Text.literal("Alerts " + (enabled ? "ein" : "aus")), false);
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("overlay")
                    // Editor entfernt – alle Einstellungen erfolgen im Settings-UI
                    // V2 ist immer aktiv – Unterbefehl v2 wird entfernt
                    .then(ClientCommandManager.literal("toggle")
                        .then(ClientCommandManager.literal("scatha").executes(ctx -> toggleOverlayFlag("overlayShowScatha")))
                        .then(ClientCommandManager.literal("worm").executes(ctx -> toggleOverlayFlag("overlayShowWorm")))
                        .then(ClientCommandManager.literal("total").executes(ctx -> toggleOverlayFlag("overlayShowTotal")))
                        .then(ClientCommandManager.literal("streak").executes(ctx -> toggleOverlayFlag("overlayShowStreak")))
                        .then(ClientCommandManager.literal("bar").executes(ctx -> toggleOverlayFlag("overlayShowBar")))
                    )
                    .then(ClientCommandManager.literal("icons")
                        .then(ClientCommandManager.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                                if (CONFIG != null) { CONFIG.overlayShowIcons = enabled; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay Icons " + (enabled ? "ein" : "aus")), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("background")
                        .then(ClientCommandManager.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                                if (CONFIG != null) { CONFIG.overlayBackgroundEnabled = enabled; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay Background " + (enabled ? "ein" : "aus")), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("columns")
                        .then(ClientCommandManager.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                                if (CONFIG != null) { CONFIG.overlayColumnsEnabled = enabled; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay Columns " + (enabled ? "ein" : "aus")), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("googly")
                        .then(ClientCommandManager.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                                if (CONFIG != null) { CONFIG.overlayGooglyEyesEnabled = enabled; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay GooglyEyes " + (enabled ? "ein" : "aus")), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("spin")
                        .then(ClientCommandManager.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                                if (CONFIG != null) { CONFIG.overlaySpinEnabled = enabled; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay Spin " + (enabled ? "ein" : "aus")), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("iconScale")
                        .then(ClientCommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0.5f, 4.0f))
                            .executes(ctx -> {
                                float v = com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "value");
                                if (CONFIG != null) { CONFIG.overlayIconScale = v; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay Icon-Scale = " + v), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("iconTexSize")
                        .then(ClientCommandManager.argument("value", com.mojang.brigadier.arguments.IntegerArgumentType.integer(16, 1024))
                            .executes(ctx -> {
                                int v = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "value");
                                if (CONFIG != null) { CONFIG.overlayIconTexSize = v; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay Icon-TextureSize = " + v), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("movegui")
                        .executes(ctx -> {
                            var mc = MinecraftClient.getInstance();
                            if (mc != null) mc.execute(() -> mc.setScreen(new namelessju.scathapro.fabric.gui.MoveOverlayScreen(mc.currentScreen)));
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("colors")
                    .then(ClientCommandManager.literal("profile")
                        .then(ClientCommandManager.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
                            .executes(ctx -> {
                                String name = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "name").toLowerCase();
                                if (!(name.equals("default") || name.equals("dark") || name.equals("high") || name.equals("high_contrast"))) {
                                    var p = MinecraftClient.getInstance().player;
                                    if (p != null) p.sendMessage(Text.literal("Unbekanntes Profil. Verfügbar: default, dark, high"), false);
                                    return 0;
                                }
                                if (CONFIG != null) { CONFIG.colorProfile = name; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Farbprofil gesetzt: " + name), false);
                                return 1;
                            })
                        )
                    )
                )
                .then(ClientCommandManager.literal("alertmode")
                    .executes(ctx -> {
                        var scatha = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                        var player = net.minecraft.client.MinecraftClient.getInstance().player;
                        if (scatha != null && player != null && scatha.getAlertModeManager() != null) {
                            var cur = scatha.getAlertModeManager().getCurrent();
                            player.sendMessage(net.minecraft.text.Text.literal("Aktueller Alert-Mode: " + (cur != null ? cur.displayName() : "?")), false);
                        }
                        return 1;
                    })
                    .then(ClientCommandManager.literal("list").executes(ctx -> {
                        var scatha = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                        var player = net.minecraft.client.MinecraftClient.getInstance().player;
                        if (scatha != null && player != null) {
                            player.sendMessage(net.minecraft.text.Text.literal("Verfügbar: vanilla, meme, anime, custom"), false);
                        }
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("set")
                        .then(ClientCommandManager.argument("id", com.mojang.brigadier.arguments.StringArgumentType.word())
                            .executes(ctx -> {
                                String id = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "id");
                                var scatha = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                                var player = net.minecraft.client.MinecraftClient.getInstance().player;
                                if (scatha != null && scatha.getAlertModeManager() != null) {
                                    boolean changed = scatha.getAlertModeManager().setById(id);
                                    if (player != null) player.sendMessage(net.minecraft.text.Text.literal("Alert-Mode: " + id + (changed?" (gesetzt)":" (unverändert)")), false);
                                }
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("editor")
                        .executes(ctx -> {
                            var scatha = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                            var mc = net.minecraft.client.MinecraftClient.getInstance();
                            if (scatha != null && mc != null) {
                                mc.execute(() -> mc.setScreen(new namelessju.scathapro.fabric.gui.CustomAlertModeScreen(
                                    mc.currentScreen, scatha.getCustomAlertModeManager(), scatha.getAlertModeManager())));
                            }
                            return 1;
                        })
                    )
                    .then(ClientCommandManager.literal("next")
                        .executes(ctx -> {
                            var scatha = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                            var player = net.minecraft.client.MinecraftClient.getInstance().player;
                            if (scatha != null && scatha.getAlertModeManager() != null) {
                                var m = scatha.getAlertModeManager().next();
                                if (player != null && m != null) player.sendMessage(net.minecraft.text.Text.literal("Alert-Mode: " + m.displayName()), false);
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("api")
                    .then(ClientCommandManager.literal("key")
                        .then(ClientCommandManager.literal("clear")
                            .executes(ctx -> {
                                var scatha = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                                if (scatha != null && scatha.getWebApiCredentials() != null) {
                                    scatha.getWebApiCredentials().clear();
                                    scatha.getWebApiCredentials().save();
                                }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("API-Key entfernt."), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("player")
                        .executes(ctx -> {
                            var scatha = namelessju.scathapro.fabric.FabricScathaPro.getInstance();
                            var mc = MinecraftClient.getInstance();
                            if (scatha != null && mc != null && mc.player != null) {
                                try {
                                    var res = scatha.getWebApiClient().getPlayer(mc.player.getUuid());
                                    var p = mc.player;
                                    if (p != null) p.sendMessage(Text.literal("Hypixel API: player ok (" + (res.has("success")? res.get("success").getAsString():"?") + ")"), false);
                                } catch (Exception e) {
                                    var p = MinecraftClient.getInstance().player;
                                    if (p != null) p.sendMessage(Text.literal("Hypixel API Fehler: " + e.getMessage()), false);
                                }
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("config")
                    .then(ClientCommandManager.literal("save").executes(ctx -> {
                        if (CONFIG != null) CONFIG.save();
                        var player = MinecraftClient.getInstance().player;
                        if (player != null) player.sendMessage(Text.literal("Konfig gespeichert."), false);
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("reload").executes(ctx -> {
                        CONFIG = FabricConfig.load();
                        var player = MinecraftClient.getInstance().player;
                        if (player != null) player.sendMessage(Text.literal("Konfig neu geladen."), false);
                        return 1;
                    }))
                );

            dispatcher.register(sp);

            // /sp scathaKills set <n>, add <n>
            dispatcher.register(ClientCommandManager.literal("sp")
                .then(ClientCommandManager.literal("scathaKills")
                    .then(ClientCommandManager.literal("set")
                        .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0))
                            .executes(ctx -> {
                                int v = IntegerArgumentType.getInteger(ctx, "value");
                                namelessju.scathapro.fabric.state.ClientState.get().setScathaKills(v);
                                if (CONFIG != null) { CONFIG.scathaKills = v; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Scatha-Kills gesetzt auf " + v), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                            .executes(ctx -> {
                                int v = IntegerArgumentType.getInteger(ctx, "value");
                                namelessju.scathapro.fabric.state.ClientState.get().addScathaKills(v);
                                if (CONFIG != null) { CONFIG.scathaKills = namelessju.scathapro.fabric.state.ClientState.get().getScathaKills(); CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Scatha-Kills +" + v + " = " + namelessju.scathapro.fabric.state.ClientState.get().getScathaKills()), false);
                                return 1;
                            })
                        )
                    )
                )
            );

            // /sp wormKills set <n>, add <n>
            dispatcher.register(ClientCommandManager.literal("sp")
                .then(ClientCommandManager.literal("wormKills")
                    .then(ClientCommandManager.literal("set")
                        .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0))
                            .executes(ctx -> {
                                int v = IntegerArgumentType.getInteger(ctx, "value");
                                namelessju.scathapro.fabric.state.ClientState.get().setWormKills(v);
                                if (CONFIG != null) { CONFIG.wormKills = v; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Worm-Kills gesetzt auf " + v), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                            .executes(ctx -> {
                                int v = IntegerArgumentType.getInteger(ctx, "value");
                                namelessju.scathapro.fabric.state.ClientState.get().addWormKills(v);
                                if (CONFIG != null) { CONFIG.wormKills = namelessju.scathapro.fabric.state.ClientState.get().getWormKills(); CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Worm-Kills +" + v + " = " + namelessju.scathapro.fabric.state.ClientState.get().getWormKills()), false);
                                return 1;
                            })
                        )
                    )
                )
            );

            // /sp streak set <n>, add <n>
            dispatcher.register(ClientCommandManager.literal("sp")
                .then(ClientCommandManager.literal("streak")
                    .then(ClientCommandManager.literal("set")
                        .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0))
                            .executes(ctx -> {
                                int v = IntegerArgumentType.getInteger(ctx, "value");
                                namelessju.scathapro.fabric.state.ClientState.get().setStreak(v);
                                if (CONFIG != null) { CONFIG.streak = v; CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Streak gesetzt auf " + v), false);
                                return 1;
                            })
                        )
                    )
                    .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                            .executes(ctx -> {
                                int v = IntegerArgumentType.getInteger(ctx, "value");
                                namelessju.scathapro.fabric.state.ClientState.get().addStreak(v);
                                if (CONFIG != null) { CONFIG.streak = namelessju.scathapro.fabric.state.ClientState.get().getStreak(); CONFIG.save(); }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Streak +" + v + " = " + namelessju.scathapro.fabric.state.ClientState.get().getStreak()), false);
                                return 1;
                            })
                        )
                    )
                )
            );

            dispatcher.register(ClientCommandManager.literal("scathapro")
                .executes(ctx -> {
                    var player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        player.sendMessage(Text.literal("Verwende /sp help."), false);
                    }
                    return 1;
                })
            );

            // /sp overlay pos <x> <y>, /sp overlay scale <f>
            dispatcher.register(ClientCommandManager.literal("sp")
                .then(ClientCommandManager.literal("overlay")
                    .then(ClientCommandManager.literal("pos")
                        .then(ClientCommandManager.argument("x", IntegerArgumentType.integer(0))
                            .then(ClientCommandManager.argument("y", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    int x = IntegerArgumentType.getInteger(ctx, "x");
                                    int y = IntegerArgumentType.getInteger(ctx, "y");
                                    if (CONFIG != null) {
                                        CONFIG.overlayX = Math.max(0, x);
                                        CONFIG.overlayY = Math.max(0, y);
                                        CONFIG.save();
                                    }
                                    var p = MinecraftClient.getInstance().player;
                                    if (p != null) p.sendMessage(Text.literal("Overlay-Position gesetzt auf (" + x + ", " + y + ")"), false);
                                    return 1;
                                })
                            )
                        )
                    )
                    .then(ClientCommandManager.literal("scale")
                        .then(ClientCommandManager.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0.5f, 3.0f))
                            .executes(ctx -> {
                                float v = com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "value");
                                if (CONFIG != null) {
                                    CONFIG.overlayScale = v;
                                    CONFIG.save();
                                }
                                var p = MinecraftClient.getInstance().player;
                                if (p != null) p.sendMessage(Text.literal("Overlay-Skalierung gesetzt auf " + v), false);
                                return 1;
                            })
                        )
                    )
                )
            );
        });

            // Client-Hooks (HUD, Keybind, Tick) - bereits registriert, hier nicht nochmal
            
            ScathaProFabric.LOGGER.info("[ScathaProFabricClient] Client-side initialization completed successfully!");
        } catch (Exception e) {
            ScathaProFabric.LOGGER.error("[ScathaProFabricClient] Error during client-side initialization:", e);
            throw e;
        }
    }

    private static int toggleOverlayFlag(String field) {
        try {
            if (CONFIG == null) return 0;
            var f = CONFIG.getClass().getField(field);
            boolean val = (boolean) f.get(CONFIG);
            f.set(CONFIG, !val);
            CONFIG.save();
            var p = MinecraftClient.getInstance().player;
            if (p != null) p.sendMessage(Text.literal(field + " = " + (!val)), false);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
