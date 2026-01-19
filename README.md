# Pickle Pirate Flag Mod

A Hytale mod featuring a plantable pickle pirate flagpole with wave animation and map marker support. Built as a reference implementation for AI-assisted Hytale mod development.

## Project Goals

This project demonstrates how to teach Claude Code to:

1. **Work with Blockbench** - Automate 3D asset creation via the Blockbench MCP plugin
2. **Write Hytale plugins** - Create functional mods with Java plugins and asset packs
3. **Make the process reusable** - Document workflows, patterns, and gotchas
4. **Write a Claude skill** - Codify knowledge into a skill that persists across sessions
5. **Build something useful** - Not a dummy item, but a functional flagpole with animation, texturing, and game integration

## What's Included

### The Mod Itself

A plantable flagpole that:
- Displays a custom pickle pirate flag with wave animation
- Shows a map marker when placed
- Can be crafted at a workbench
- Supports rotation on placement

### Claude Code Integration

- **[Claude Skill](.claude/skills/blockbench-mcp.md)** - Reusable skill for Blockbench MCP automation
- **[CLAUDE.md](CLAUDE.md)** - Project-specific instructions for Claude Code

### Documentation

| Document | Purpose |
|----------|---------|
| [Model Creation Guide](documentation/model-creation-guide.md) | Creating 3D models for Hytale with Blockbench |
| [Texture Creation Guide](documentation/texture-creation-guide.md) | Textures, UV mapping, and ImageMagick workflows |
| [Blockbench MCP Reference](documentation/blockbench-mcp-reference.md) | Quick reference for MCP automation commands |
| [Gradle Build Process](documentation/gradle-build-process.md) | Building and deploying the mod |
| [Installation Guide](documentation/INSTALLATION.md) | Installing the mod on a server |
| [Publishing Guide](documentation/publishing.md) | Publishing to CurseForge |

### External References

Official Hytale documentation (mirrored in `documentation/external/`):
- [Blockbench Studio Guide](documentation/external/BLOCKBENCH-STUDIO-GUIDE.md) - Official art direction
- [Creating Plugins](documentation/external/CREATING_PLUGINS_FULL.md) - Plugin development guide
- [Modding Resources](documentation/external/MODDING_RESOURCES.md) - API and resource links

## Project Structure

```
pickle_pirate_flag_mod/
├── .claude/skills/          # Claude Code skills
│   └── blockbench-mcp.md    # Blockbench automation skill
├── src/main/java/           # Java plugin code
├── pack/                    # Asset pack (deployed to server)
│   ├── Common/              # Models, textures, animations
│   └── Server/              # Item definitions, translations
├── documentation/           # Guides and references
├── planning/                # Design docs and roadmaps
├── scripts/                 # Utility scripts
└── research/                # Reference repos and examples
```

## Quick Start

### Prerequisites

- Java 17+
- Gradle 8+
- Hytale server with modding support
- Blockbench with [Hytale plugin](https://github.com/JannisX11/hytale-blockbench-plugin)
- (Optional) [Blockbench MCP plugin](https://github.com/jasonjgardner/blockbench-mcp-plugin) for automation

### Build & Deploy

```bash
# Build the JAR
./gradlew jar

# Deploy to server
./gradlew deployAll
```

See [Gradle Build Process](documentation/gradle-build-process.md) for details.

### Using the Claude Skill

The `blockbench-mcp` skill activates automatically when working with Blockbench in this project. It provides:

- Workflow phases for model creation
- UV coordinate handling (direct pixel coordinates)
- Screenshot management (avoids context blowup)
- Animation patterns (nested bones for cascading effects)

To use it in a new project, copy `.claude/skills/blockbench-mcp.md` to your project's `.claude/skills/` folder.

## Modifying the Plugin

### Changing the Model

1. Open `src/main/resources/assets/models/pickle_flagpole_hytale.bbmodel` in Blockbench
2. Make changes using the Hytale plugin
3. Export to `pack/Common/Blocks/pickle_pirate_flagpole.blockymodel`

Or use Claude Code with the Blockbench MCP skill for automated modifications.

### Changing the Texture

1. Edit `pack/Common/BlockTextures/pickle_pirate_flagpole.png`
2. Keep dimensions as multiples of 32px
3. UV offsets in the model are direct pixel coordinates

See [Texture Creation Guide](documentation/texture-creation-guide.md).

### Adding Features

The item definition is at `pack/Server/Item/Items/Pickle_Flag.json`. Key fields:

- `CustomModel` - Path to model file
- `CustomModelAnimation` - Path to animation file
- `Looping` - Enable continuous animation
- `BlockEntity.Components` - Custom data storage

## Publishing

The mod is configured for CurseForge publishing:

```bash
./gradlew curseforge
```

See [Publishing Guide](documentation/publishing.md) for API key setup and metadata.

## Roadmap

Active planning documents in `planning/`:

| Document | Status |
|----------|--------|
| [Phase 3: Full Landmark](planning/phase3_full_landmark.md) | Planned |
| [Blockbench MCP Fork](planning/roadmap/blockbench-mcp-fork.md) | Roadmap |
| [BBModel Converter](planning/roadmap/bbmodel-converter.md) | Roadmap |

Completed phases in `planning/completed/`.

## Key Learnings

### UV Coordinates

UV offsets in `.blockymodel` files are **direct pixel coordinates** (not scaled). 1 model unit = 1 texture pixel.

### Texel Density

- **Props/Blocks**: 32px per world unit
- **Characters/Attachments**: 64px per world unit

### Geometry Constraints

Hytale only supports cubes and quads. No spheres, triangles, or complex topology.

### Screenshot Context Management

Never call `blockbench_capture_screenshot` directly in Claude Code - use `scripts/bb_screenshot.py` to resize before returning to avoid context blowup.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with commits after each phase
4. Submit a pull request

## License

MIT

## Links

- [Hytale Blockbench Plugin](https://github.com/JannisX11/hytale-blockbench-plugin)
- [Blockbench MCP Plugin](https://github.com/jasonjgardner/blockbench-mcp-plugin)
- [Blockbench](https://blockbench.net)
