---
name: blockbench-mcp
description: Use when building or modifying 3D models for Hytale via Blockbench MCP. Covers geometry creation, texturing, UV mapping, animation, and export workflows.
---

# Blockbench MCP Automation Skill

Use this skill when working with 3D models in Blockbench through the MCP plugin.

## Prerequisites

Before using Blockbench MCP tools:

1. **Blockbench must be running** with a project open
2. **MCP plugin must be active** (installed from plugin registry)
3. **MCP server responding** at `http://localhost:3000/bb-mcp`j
4. **ImageMagick is installed**

If you get "Server not initialized" errors, ask the user to:

- Check Blockbench is running
- Verify the MCP plugin is enabled (File > Plugins)
- Reload the plugin
- Restart Blockbench if needed

## Critical Rules

### Screenshots

**NEVER call `blockbench_capture_screenshot` directly.**

Screenshots blow up context and can kill the conversation. Instead:

```bash
python3 scripts/bb_screenshot.py [resize_percent] [quality]
# Default: 25% size, 50% quality
```

This script:

1. Calls the MCP internally
2. Resizes with ImageMagick
3. Returns small base64 that won't destroy context

### UV Coordinates

**UV offsets are DIRECT PIXEL COORDINATES** (not scaled).

- 1 model unit = 1 texture pixel
- No 4x scale factor
- Texture sizes must be multiples of 32px

Example: A cube with size `{x: 64, y: 16, z: 1}` needs 64x16 pixels on the texture.

### Texel Density

| Model Type | Density | Use For |
|------------|---------|---------|
| Prop/Block | 32px per world block | Furniture, decorations |
| Character/Attachment | 64px per world block | Characters, weapons, cosmetics |

This affects in-game rendering, not internal UV coordinates.

## Workflow Phases

### Phase 1: Understand Current State

```
1. blockbench_list_outline
   → See existing bone/element hierarchy

2. scripts/bb_screenshot.py (only if visual check needed)
   → Get low-res preview
```

### Phase 2: Create Bone Hierarchy

Create bones (groups) FIRST, then add geometry to them.

```
blockbench_add_group
  name: "bone_name"
  origin: [x, y, z]       # Pivot point
  rotation: [rx, ry, rz]  # Initial rotation
  parent: "parent_name"   # Or "root" for top level
```

For animated models, use nested hierarchy:
```
root
├── pole
├── crossbar
└── flag_1
    └── flag_2        # Child of flag_1
        └── flag_3    # Child of flag_2
            └── flag_4
```

### Phase 3: Build Geometry

Add cubes to bones:

```
blockbench_place_cube
  elements: [{
    name: "cube_name",
    from: [x1, y1, z1],   # Start corner
    to: [x2, y2, z2],     # End corner
    origin: [px, py, pz], # Pivot point
    rotation: [rx, ry, rz]
  }]
  group: "parent_bone"    # Which bone owns this cube
  texture: "texture_name" # Optional
  faces: true             # Auto UV mapping
```

Size calculation: `to - from = size`

### Phase 4: Create/Load Textures

```
blockbench_create_texture
  name: "texture_name"
  width: 256              # Multiple of 32
  height: 288
  data: "path/to/file.png" # Or data URL
```

Apply texture to elements:
```
blockbench_apply_texture
  id: "element_name"
  texture: "texture_name"
  applyTo: "blank"        # Only faces without texture
```

### Phase 5: Fix UV Mapping

If auto UV doesn't work correctly:

```
blockbench_modify_cube
  id: "cube_name"
  uv_offset: [x, y]       # Pixel coordinates!
```

For per-face control, use the Blockbench UI or:
```
blockbench_trigger_action
  action: "uv_auto"
```

### Phase 6: Create Animation

```
blockbench_create_animation
  name: "animation_name"
  animation_length: 2.0   # Seconds
  loop: true
  bones: {
    "bone_name": [
      { time: 0, rotation: [0, 0, 0] },
      { time: 0.5, rotation: [0, 0, 10] },
      { time: 1.0, rotation: [0, 0, 0] },
      { time: 1.5, rotation: [0, 0, -10] },
      { time: 2.0, rotation: [0, 0, 0] }
    ]
  }
```

For cascading effects (like wave animation), offset keyframe timing between nested bones.

### Phase 7: Export

```
blockbench_trigger_action
  action: "export_over"   # Export to current file path
```

Or use `export_blockyanim` for animations.

## Common Patterns

### Wave Animation (Flag/Banner)

1. Create nested bone chain: flag_1 → flag_2 → flag_3 → flag_4
2. Each segment is a child of the previous
3. Animate Z rotation on each bone
4. Offset timing: flag_1 at t=0, flag_2 at t=0.1, etc.
5. Inner segments (children) get larger rotation amplitude

### Multi-Part Props

1. Create flat hierarchy under root
2. Each part is independent bone
3. Position relative to root origin
4. Apply different texture regions to each part

### Character Attachments

1. Use 64px density
2. Create attachment point bones (R-Attachment, L-Attachment)
3. Position geometry relative to grip point
4. Test with player model reference (~121 units tall)

## MCP Tool Reference

Quick reference for common tools. See `documentation/blockbench-mcp-reference.md` for full details.

| Tool | Purpose |
|------|---------|
| `blockbench_list_outline` | Get current bone/element hierarchy |
| `blockbench_add_group` | Create bone (group) |
| `blockbench_place_cube` | Add cube geometry |
| `blockbench_modify_cube` | Change cube properties/UV |
| `blockbench_create_texture` | Create or load texture |
| `blockbench_apply_texture` | Apply texture to element |
| `blockbench_create_animation` | Create animation with keyframes |
| `blockbench_manage_keyframes` | Add/edit/delete keyframes |
| `blockbench_trigger_action` | Trigger Blockbench action (export, undo, etc.) |

## Troubleshooting

### "Server not initialized"
- Blockbench not running or MCP plugin not active
- Ask user to restart Blockbench

### UV appears wrong in-game
- Check UV offsets are within texture bounds
- Remember: offset is in pixels, not scaled
- Verify texture size is multiple of 32

### Animation not playing
- Check animation is set to loop
- Verify bone names match exactly
- Export animation separately if needed

### Context getting too large
- You called screenshot directly instead of using bb_screenshot.py
- Stop and inform user to start new conversation
- In future, ALWAYS use the script
