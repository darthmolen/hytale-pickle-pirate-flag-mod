# Hytale Modding Resources

Quick reference for Hytale modding. For comprehensive tutorials, see [CREATING_PLUGINS_FULL.md](CREATING_PLUGINS_FULL.md).

## Essential Links

| Resource | URL |
|----------|-----|
| **Gitbook Tutorials** | [britakee-studios.gitbook.io](https://britakee-studios.gitbook.io/hytale-modding-documentation) |
| **Plugin Template** | [github.com/realBritakee/hytale-template-plugin](https://github.com/realBritakee/hytale-template-plugin) |
| **Official Modding Strategy** | [hytale.com/news/2025/11](https://hytale.com/news/2025/11/hytale-modding-strategy-and-status) |
| **CurseForge (240+ mods)** | [curseforge.com/hytale](https://www.curseforge.com/hytale) |
| **HytaleModding.dev** | [hytalemodding.dev](https://hytalemodding.dev/en) |

## Quick Start

```bash
# Clone working template
git clone https://github.com/realBritakee/hytale-template-plugin.git
cd hytale-template-plugin
./gradlew shadowJar  # Builds immediately!
```

## File Paths

```
%AppData%/Roaming/Hytale/UserData/
├── Packs/          # Asset packs (blocks, items, textures)
└── Mods/           # Plugin JARs
```

## Four Modding Categories

| Type | Files | Coding | Best For |
|------|-------|--------|----------|
| **Plugins** | .jar | Java | Programmers |
| **Data Assets** | .json | None | Content creators |
| **Art Assets** | .png, .bbmodel | None | Artists |
| **Save Files** | worlds, prefabs | None | Builders |

## Required Tools

| Tool | Purpose |
|------|---------|
| [Java 25](https://adoptium.net/) | Plugin development |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/) | IDE (free Community edition) |
| [Blockbench](https://blockbench.net/) | 3D modeling |

## This Project Structure

Our Pickle Pirate Flag uses **Pack + Plugin** pattern:

```
hytale/
├── pack/                    # Asset Pack (no coding)
│   ├── manifest.json
│   ├── Common/              # Shared assets
│   │   ├── Blocks/          # Block definitions
│   │   ├── BlockTextures/   # Textures
│   │   └── Models/          # 3D models
│   └── Server/              # Server-side data
│       └── Item/            # Item definitions
│
├── src/                     # Java Plugin
│   └── main/java/...        # Map markers, discovery logic
│
└── documentation/           # You are here
```

## Learning Path

1. Read [CREATING_PLUGINS_FULL.md](CREATING_PLUGINS_FULL.md) - Complete gitbook reference
2. Clone the [plugin template](https://github.com/realBritakee/hytale-template-plugin)
3. Follow [Getting Started with Packs](https://britakee-studios.gitbook.io/hytale-modding-documentation/packs-content-creation/02-getting-started-with-packs)
4. Watch [Kaupenjoe's video tutorials](https://www.youtube.com/@Kaupenjoe)

## Community

- [Official Discord](https://discord.gg/hytale)
- [CurseForge Discord](https://discord.gg/S3EaZArYz2)
- [Reddit r/hytale](https://reddit.com/r/hytale)

---

*240+ mods already published. The ecosystem is thriving!*
