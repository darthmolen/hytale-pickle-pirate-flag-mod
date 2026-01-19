# Model Creation Guide for Hytale

This guide covers creating 3D models for Hytale using Blockbench, including geometry, bone hierarchy, animation, and export workflows.

## Getting Started

### Required Tools

1. **Blockbench** - Download from [blockbench.net](https://blockbench.net)
2. **Hytale Blockbench Plugin** - Install from File > Plugins > Available, or from [GitHub](https://github.com/JannisX11/hytale-blockbench-plugin)
3. **Optional: MCP Plugin** - For automation with Claude Code ([GitHub](https://github.com/jasonjgardner/blockbench-mcp-plugin))

### Installing the Hytale Plugin

1. Open Blockbench
2. Go to File > Plugins
3. Search for "Hytale" in the Available tab
4. Click Install

The plugin provides:
- Correct export formats (`.blockymodel`, `.blockyanim`)
- Consistent pixel ratio across textures
- Quality-of-life improvements for Hytale workflows
- Proper texel density handling

### Creating a New Project

1. File > New
2. Choose format:
   - **Hytale Prop** - For blocks, furniture, decorations (32px density)
   - **Hytale Character** - For characters, weapons, attachments (64px density)
3. Set texture size (must be multiple of 32)

## Geometry Constraints

Hytale models use only two primitive types. This is a hard requirement.

### Cubes (Boxes)
- 6-sided rectangular prisms
- Most common element type
- Defined by `from` and `to` corners

### Quads
- 2-sided flat planes
- Used for thin elements (flags, leaves)
- Can be double-sided

### What's NOT Allowed
- ❌ Spheres
- ❌ Triangles
- ❌ Complex topology
- ❌ Edge loops
- ❌ Weight painting
- ❌ Pyramids

This constraint keeps models easy to create, animate, and optimized for rendering thousands simultaneously.

## Bone Hierarchy

### What Are Bones?

In Blockbench, "bones" are called "groups." They define:
- **Pivot points** for rotation
- **Parent-child relationships** for animation
- **Organization** of model elements

### Creating Bones

1. Right-click in Outliner > Add Group
2. Name it descriptively (e.g., `arm_right`, `flag_1`)
3. Set origin (pivot point)
4. Set parent if needed

### Bone Naming Convention

Bones must be properly named for Hytale's animation system. Once correctly named, animations work automatically in-game.

Standard character bones:
- `body`, `head`, `arm_left`, `arm_right`
- `leg_left`, `leg_right`, `hand_left`, `hand_right`

### Hierarchy Design

Good hierarchy enables animation. Plan before building:

```
root
├── body
│   ├── head
│   │   └── jaw
│   ├── arm_left
│   │   └── hand_left
│   └── arm_right
│       └── hand_right
└── legs
    ├── leg_left
    │   └── foot_left
    └── leg_right
        └── foot_right
```

### Nested Bones for Cascading Animation

For effects like waving flags, use nested hierarchy:

```
flag_1 (rotates first)
└── flag_2 (inherits flag_1 rotation, adds own)
    └── flag_3 (inherits flag_1+2, adds own)
        └── flag_4 (inherits all, adds own)
```

When `flag_1` rotates 5°, all children rotate with it. Then each child adds its own rotation, creating a cascading wave effect.

## Building Geometry

### Adding Cubes

1. Select parent bone in Outliner
2. Edit > Add Cube (or Ctrl+N)
3. Adjust position and size in Properties panel

Visual tools:
- Move tool (V)
- Resize tool (S)
- Rotate tool (R)

### Cube Properties

| Property | Description |
|----------|-------------|
| Position | Cube location relative to bone origin |
| Size | Dimensions (width, height, depth) |
| Origin | Pivot point for rotation |
| Rotation | Initial rotation (usually 0,0,0) |
| UV Offset | Texture mapping position (in pixels) |
| Inflate | Expand/shrink without changing UV |

### Size and Scale

- 1 unit in Blockbench = 1 pixel on texture (for UV mapping)
- 16 units = 1 Minecraft-style block
- Player height ≈ 121 units (~1.9 blocks)

### Stretching

You can stretch geometry for fine adjustments:
- **Allowed range**: 0.7x to 1.3x per axis
- Beyond this, pixel distortion becomes obvious
- Use sparingly - for avoiding Z-fighting or minor tweaks

## Animation

### Creating an Animation

1. Animation menu > Add Animation
2. Set properties:
   - Name (e.g., `wave`, `idle`, `walk`)
   - Length in seconds
   - Loop mode: Once, Loop, or Hold

### Keyframes

Keyframes define bone positions at specific times. Blockbench interpolates between them.

1. Select bone in Outliner
2. Move timeline to desired time
3. Adjust bone rotation/position
4. Click keyframe button (or enable auto-keyframe)

### Keyframe Types

| Channel | What it animates |
|---------|------------------|
| Rotation | Bone orientation (most common) |
| Position | Bone location |
| Scale | Bone size |

### Interpolation Modes

| Mode | Behavior |
|------|----------|
| Linear | Constant speed between keyframes |
| Smooth (Catmull-Rom) | Eased transitions |
| Bezier | Custom curve control |
| Step | Instant jump, no interpolation |

### Wave Animation Example

For a 4-segment flag with cascading wave:

```
Timeline (2 seconds, loop):

flag_1:
  t=0.0: rotation Z=0
  t=0.5: rotation Z=8
  t=1.0: rotation Z=0
  t=1.5: rotation Z=-8
  t=2.0: rotation Z=0

flag_2:
  t=0.1: rotation Z=0
  t=0.6: rotation Z=12
  t=1.1: rotation Z=0
  t=1.6: rotation Z=-12

flag_3:
  t=0.2: rotation Z=0
  t=0.7: rotation Z=16
  t=1.2: rotation Z=0
  t=1.7: rotation Z=-16

flag_4:
  t=0.3: rotation Z=0
  t=0.8: rotation Z=20
  t=1.3: rotation Z=0
  t=1.8: rotation Z=-20
```

Key principles:
- **Offset timing**: Each segment starts slightly later
- **Increasing amplitude**: Outer segments rotate more
- **Smooth loop**: Start and end at same position

## Export

### Model Export

1. File > Export > Hytale Model (.blockymodel)
2. Save to your pack's `Common/Blocks/` folder

Or if already saved:
- File > Export > Export Over (Ctrl+Shift+S)

### Animation Export

1. Animation menu > Save Animation
2. File format: `.blockyanim`
3. Save to `Common/Blocks/Animations/`
4. Naming convention: `animation.<name>.blockyanim`

### File Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Models | `model_name.blockymodel` | `pickle_pirate_flagpole.blockymodel` |
| Animations | `animation.<name>.blockyanim` | `animation.flag_wave.blockyanim` |
| Textures | `texture_name.png` | `pickle_pirate_flagpole.png` |

## Pack Structure

Hytale mods use a specific folder structure:

```
pack/
├── manifest.json                           # Pack metadata
├── Common/                                 # Shared assets (client + server)
│   ├── Blocks/                            # Block models
│   │   ├── model_name.blockymodel
│   │   └── Animations/
│   │       └── animation.anim_name.blockyanim
│   ├── BlockTextures/                     # Textures for block models
│   │   └── texture_name.png
│   ├── Icons/
│   │   └── ItemsGenerated/                # Item icons (inventory)
│   │       └── item_icon.png
│   └── UI/
│       └── WorldMap/
│           └── MapMarkers/                # Custom map markers
│               └── marker.png
└── Server/                                # Server-side definitions
    ├── Item/
    │   └── Items/
    │       └── Item_Name.json             # Item definition
    └── Languages/
        └── en-US/
            └── server.lang                # Translations
```

## Item Definition

To make your model appear in-game, create an item definition:

**`pack/Server/Item/Items/Item_Name.json`**:

```json
{
  "TranslationProperties": {
    "Name": "server.items.item_id.name"
  },
  "MaxStack": 16,
  "Icon": "Icons/ItemsGenerated/item_icon.png",
  "Categories": ["Blocks.Deco"],
  "BlockType": {
    "DrawType": "Model",
    "CustomModel": "Blocks/model_name.blockymodel",
    "CustomModelTexture": [
      {
        "Texture": "BlockTextures/texture_name.png",
        "Weight": 1
      }
    ],
    "CustomModelAnimation": "Blocks/Animations/animation.anim_name.blockyanim",
    "Looping": true,
    "CustomModelScale": 1
  }
}
```

Key fields:
- `CustomModel`: Path to `.blockymodel` (relative to `Common/`)
- `CustomModelTexture`: Path to texture (relative to `Common/`)
- `CustomModelAnimation`: Path to animation file
- `Looping`: Set `true` for continuous animations
- `CustomModelScale`: Adjust model size (1 = normal)

## Practical Example: Pickle Pirate Flagpole

### Structure

```
pole (bone at origin)
├── pole_cube (4x124x4)
crossbar (bone at Y=120)
├── crossbar_cube (82x4x4)
flag_1 (bone at Y=120)
├── flag_1_cube (64x16x1)
└── flag_2 (child bone)
    ├── flag_2_cube (64x16x1)
    └── flag_3 (child bone)
        ├── flag_3_cube (64x16x1)
        └── flag_4 (child bone)
            └── flag_4_cube (64x16x1)
```

### Dimensions

| Element | Size (units) | Notes |
|---------|--------------|-------|
| Pole | 4x124x4 | Tall vertical beam |
| Crossbar | 82x4x4 | Horizontal, extends past flag |
| Flag segments | 64x16x1 | Each 1/4 of flag height |

### Files Created

| File | Location |
|------|----------|
| Model | `Common/Blocks/pickle_pirate_flagpole.blockymodel` |
| Texture | `Common/BlockTextures/pickle_pirate_flagpole.png` |
| Animation | `Common/Blocks/Animations/animation.flag_wave.blockyanim` |
| Item definition | `Server/Item/Items/Pickle_Flag.json` |

## Best Practices

### From Official Hytale Art Guidelines

1. **Keep geometry simple** - Work as simply as possible, increase geometry only to improve silhouette
2. **Use cubes and quads only** - No spheres or complex shapes
3. **Name bones correctly** - Animation system depends on naming
4. **Optimize triangle count** - Several thousand blocks render simultaneously, resulting in millions of triangles per frame

### Texturing Tips

1. **Paint shadows into textures** - Don't rely only on in-game lighting
2. **Avoid pure white (#FFFFFF)** and **pure black (#000000)** - They break in-game lighting
3. **Add color to shadows** - Hint of purple makes models vibrant
4. **Avoid noise and grain** - Keep textures clean
5. **Treat each texture as an illustration** - Simulate lighting in the texture itself

### Modeling Tips

1. **Plan hierarchy first** - Harder to change later
2. **Set pivot points early** - Affects all child elements
3. **Test animations often** - Small issues compound
4. **Use references** - Import player model for scale comparison

### Performance Considerations

- Every triangle affects frame rate
- Simpler models = more can be on screen simultaneously
- Avoid unnecessary subdivision
- Remove hidden faces when possible

## Troubleshooting

### Model doesn't appear in-game
- Verify `CustomModel` path in item JSON is correct
- Check pack `manifest.json` is valid
- Ensure model exported as `.blockymodel` (not `.bbmodel`)
- Verify pack is deployed to server's `mods/` folder

### Animation doesn't play
- Check `CustomModelAnimation` path in item JSON
- Verify `Looping: true` is set in item definition
- Ensure bone names match between model and animation
- Check animation file is `.blockyanim` format

### Bones move unexpectedly
- Check pivot point positions
- Verify parent-child relationships
- Look for unintended keyframes

### Scale seems wrong
- Confirm using correct format (Prop=32px vs Character=64px)
- Adjust `CustomModelScale` in item definition
- Compare against player reference model (~121 units tall)

### Texture appears wrong
- Verify texture dimensions are multiples of 32px
- Check UV offsets are within texture bounds
- Ensure texture path in item JSON is correct

## Related Documentation

- [Texture Creation Guide](texture-creation-guide.md) - Creating and mapping textures
- [Blockbench MCP Reference](blockbench-mcp-reference.md) - Automation commands
- [Official Hytale Art Guide](external/BLOCKBENCH-STUDIO-GUIDE.md) - Art direction principles
