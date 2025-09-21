# Fabric Scatha-Pro (Unofficial Fabric Port for 1.21.5)

This is an unofficial Fabric port of the Scatha-Pro mod for Minecraft 1.21.5 by Zeitlose (Jan).
It brings the classic Scatha-Pro experience to modern Minecraft versions and adds several quality-of-life improvements.

Highlights in this port
- Fabric 1.21.5 support (Java 21)
- Reworked Settings UI with clear tabs (Overlay, Achievements, News, Alerts, Sound)
- Overlay settings with 2-column layout, sections, and scrolling
- Achievements view: compact rows, 2-column categories, progress bars, proper clipping/scrolling
- Alerts tab: alert mode switching (Vanilla/Meme/Anime/Custom), Bedrock-wall trigger distance, test buttons
- Custom Mode Editor: per-alert title, sound mapping, per-alert volume, quick test play, JSON export
- No blurred backgrounds in GUIs for crisp titles

Credits
All original credits and rights belong to the original Scatha‑Pro developers. Please support and credit their work:
- Modrinth (original): https://modrinth.com/mod/scatha-pro
- GitHub (original): https://github.com/NamelessJu/Scatha-Pro
- Discord (Scatha Farmers): https://discord.gg/scatha-farmers-898827889145942056

Disclaimer
- This repository is not affiliated with the original authors. It is a community-maintained, unofficial Fabric port.
- If any of the original authors request changes or takedowns, they will be respected.

Build locally
Requirements: Java 21, Gradle wrapper included.

```
./gradlew build
```
The distributable jar will be in `build/libs/` after the build.

Releases
- GitHub Actions will build and attach the jar automatically whenever a tag like `v1.0.0` is pushed.
- See the workflow in `.github/workflows/release.yml`.

License (original project terms)
- You may not publish the mod or parts of it on it's own without my permission (regardless of modifications)!
- You may bundle this mod in modpacks with credit
- Modifying this mod for private use is allowed
- And: you can of course share pictures/videos of the mod
