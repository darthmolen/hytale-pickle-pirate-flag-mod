# Pickle Pirate Flag Mod

A Hytale mod that adds a plantable pickle pirate flagpole with wave animation and map marker support.

## Project Structure

```
pickle_pirate_flag_mod/
├── src/main/java/        # Java plugin code
├── src/main/resources/   # Plugin manifest + assets (bundled in JAR)
├── pack/                 # Asset pack (deployed separately to server)
│   ├── Common/          # Shared assets (models, textures)
│   ├── Server/          # Server-side assets (items, blocks)
│   └── manifest.json    # Pack manifest
├── scripts/             # Utility scripts (ComfyUI, screenshots)
├── libs/                # HytaleServer.jar (compile dependency)
└── build.gradle         # Gradle build config
```

## Server Locations

| Path | Description |
|------|-------------|
| `/mnt/c/hytale-server/` | Hytale server root |
| `/mnt/c/hytale-server/mods/` | Plugin JARs and asset packs |
| `/mnt/c/hytale-server/logs/` | Server logs (timestamped .log files) |
| `/mnt/c/hytale-server/config.json` | Server configuration |
| `/mnt/c/hytale-server/start-server.bat` | Server start script |

### Checking Server Logs
```bash
# Latest log
ls -t /mnt/c/hytale-server/logs/*.log | head -1 | xargs cat

# Tail recent logs
tail -50 /mnt/c/hytale-server/logs/$(ls -t /mnt/c/hytale-server/logs/*.log | head -1 | xargs basename)
```

## Deployment

### Build and Deploy JAR
```bash
cd /mnt/c/dev/Hytale/pickle_pirate_flag_mod
./gradlew jar
cp build/libs/PicklePirateFlag-1.0.0.jar /mnt/c/hytale-server/mods/
```

### Deploy Asset Pack
The pack must be deployed separately to the mods folder:
```bash
cp -r pack /mnt/c/hytale-server/mods/PicklePirateFlag
```

Or use gradle:
```bash
./gradlew deployAssets
```

## Blockbench MCP Integration

The project uses Blockbench MCP for programmatic 3D model editing. The MCP server runs inside Blockbench.

### Setup
1. Open Blockbench
2. Install the MCP plugin (if not already)
3. The MCP server starts on `http://localhost:3000/bb-mcp`
4. Config is in `.mcp.json`

### Common MCP Operations

**Screenshot current view:**
```
mcp__blockbench__blockbench_capture_screenshot
```

**List model outline (groups/elements):**
```
mcp__blockbench__blockbench_list_outline
```

**Place a cube:**
```
mcp__blockbench__blockbench_place_cube
  elements: [{ name: "cube1", from: [0,0,0], to: [16,16,16] }]
```

**Create animation:**
```
mcp__blockbench__blockbench_create_animation
  name: "wave"
  animation_length: 2.0
  loop: true
  bones: { "flag_1": [{ time: 0, rotation: [0,0,0] }, { time: 1, rotation: [0,0,10] }] }
```

**Export to blockymodel:**
```
mcp__blockbench__blockbench_trigger_action
  action: "export_over"
```

### UV Mapping Notes
- Hytale uses 4x UV scale: UV coordinate 1 = 4 pixels
- Texture 256x288 pixels = 64x72 UV units max
- Watch for UV offsets pointing outside texture bounds

## ComfyUI Texture Generation

Generate pixel art textures using ComfyUI (Stable Diffusion).

### Prerequisites
- ComfyUI running on `127.0.0.1:8188`
- SD 1.5 model loaded: `v1-5-pruned-emaonly.safetensors`
- Image Pixelate node installed

### Usage
```bash
python scripts/comfyui_generate.py \
  --workflow scripts/pixel_art_workflow_api.json \
  --prompt "pixel art, wooden flagpole texture, game asset" \
  --output generated/flagpole.png \
  --seed 12345
```

### Workflow Settings (pixel_art_workflow_api.json)
- Output: 512x512 downsampled to 32x32 pixel art
- 16 colors, k-means++ palette
- No dithering (clean edges)

### Example Prompts
```
# Wood texture
"pixel art, 16-bit game texture, wooden planks, oak wood grain, warm brown tones, painted shadows, tileable seamless texture, game asset"

# Flag texture
"pixel art, pirate flag, skull and crossbones, pickle character, green, cartoon style, game sprite"
```

## Hytale Asset Format

### Plugin Manifest (src/main/resources/manifest.json)
```json
{
    "Group": "dev.smolen",
    "Name": "PluginName",
    "Version": "1.0.0",
    "Authors": [{ "Name": "smolen" }],
    "Dependencies": {},
    "Main": "dev.smolen.plugin.PluginClass"
}
```

### Pack Manifest (pack/manifest.json)
```json
{
    "Group": "PicklePirate",
    "Name": "PicklePirateFlag"
}
```

### Item Definition (pack/Server/Item/Items/item.json)
```json
{
    "TranslationProperties": { "Name": "server.items.item_id.name" },
    "MaxStack": 16,
    "BlockType": {
        "DrawType": "Model",
        "CustomModel": "Models/model.blockymodel",
        "CustomModelTexture": [{ "Texture": "BlockTextures/texture.png", "Weight": 1 }]
    }
}
```

## Model Files

| File | Purpose |
|------|---------|
| `pickle_pirate_flagpole_hytale.bbmodel` | Blockbench project (edit this) |
| `pickle_pirate_flagpole_hytale.blockymodel` | Hytale export (deploy this) |
| `pickle_pirate_flagpole.blockymodel` | Final deployed model name |
| `pickle_pirate_flagpole.png` | 256x288 texture |

## Troubleshooting

### "Failed to load any asset packs"
- Check pack manifest.json exists and is valid JSON
- Ensure pack folder is in `/mnt/c/hytale-server/mods/` not just `/packs/`
- Re-extract Assets.zip if HytaleAssets manifest is invalid

### Texture appears white/wrong
- Check UV offsets are within texture bounds
- UV * 4 = pixel coordinate (must be < texture size)
- Use Blockbench MCP to fix UVs: `blockbench_modify_cube` with `uv_offset`

### Plugin fails to load
- Check manifest.json format matches current server version
- Authors must be `[{"Name": "..."}]` not `["..."]`
- Dependencies must be `{}` not `[]`
