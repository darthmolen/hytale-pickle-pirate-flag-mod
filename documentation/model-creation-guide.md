# Model Creation Guide for Hytale

This guide covers creating 3D models for Hytale using Blockbench, including geometry, bone hierarchy, animation, and export workflows.

## Getting Started

### Required Tools

1. **Blockbench** - Download from [blockbench.net](https://blockbench.net)
2. **Hytale Blockbench Plugin** - Install from File > Plugins > Available
3. **Optional: MCP Plugin** - For automation with Claude Code

### Creating a New Project

1. File > New
2. Choose format:
   - **Hytale Prop** - For blocks, furniture, decorations (32px density)
   - **Hytale Character** - For characters, weapons, attachments (64px density)
3. Set texture size (multiple of 32)

## Geometry Constraints

Hytale models use only two primitive types:

### Cubes (Boxes)
- 6-sided rectangular prisms
- Most common element type
- Defined by `from` and `to` corners

### Quads
- 2-sided flat planes
- Used for thin elements (flags, leaves)
- Can be double-sided

### What's NOT Allowed
- Spheres
- Triangles
- Complex topology
- Edge loops
- Weight painting

This keeps models simple to create, animate, and optimize.

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

When `flag_1` rotates 5°, all children rotate with it. Then each child adds its own rotation.

## Building Geometry

### Adding Cubes

1. Select parent bone in Outliner
2. Edit > Add Cube (or Ctrl+N)
3. Adjust position and size in Properties panel

Or use the visual tools:
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
| UV Offset | Texture mapping position |
| Inflate | Expand/shrink without changing UV |

### Size and Scale

- 1 unit in Blockbench = 1 pixel on texture (for UV)
- 16 units = 1 Minecraft-style block
- Player height ≈ 121 units (~1.9 blocks)

### Stretching

You can stretch geometry to fine-tune sizes:
- Allowed range: 0.7x to 1.3x per axis
- Beyond this, pixel distortion becomes visible
- Use sparingly for adjustments, not as primary sizing

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
4. Click keyframe button (or auto-keyframe)

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
- **Increasing amplitude**: Inner segments rotate more
- **Smooth loop**: Start and end at same position

## Export

### Model Export

1. File > Export > Hytale Model (.blockymodel)
2. Choose location (usually in your mod's assets)

Or if already saved:
- File > Export > Export Over

### Animation Export

1. Animation menu > Save Animation (.blockyanim)
2. Place in `Blocks/Animations/` folder

### File Naming

Follow Hytale conventions:
- Models: `model_name.blockymodel`
- Animations: `animation.animation_name.blockyanim`
- Textures: `texture_name.png`

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

### Animation

The wave animation:
- 2 seconds duration, looping
- Each flag segment rotates on Z axis
- Timing offset creates ripple effect
- Amplitude increases toward tip

## Best Practices

### From Official Hytale Guidelines

1. **Keep geometry simple** - Fewer triangles = better performance
2. **Use cubes and quads only** - No spheres or complex shapes
3. **Name bones correctly** - Animation system depends on naming
4. **Optimize triangle count** - Several thousand blocks render simultaneously

### Modeling Tips

1. **Plan hierarchy first** - Harder to change later
2. **Set pivot points early** - Affects all child elements
3. **Test animations often** - Small issues compound
4. **Use references** - Import player model for scale

### Performance Considerations

- Every triangle affects frame rate
- Simpler models = more can be on screen
- Avoid unnecessary subdivision
- Remove hidden faces when possible

## Troubleshooting

### Model doesn't appear in-game
- Check file path in item/block definition
- Verify model exported correctly
- Ensure pack manifest is valid

### Animation doesn't play
- Check animation name matches item definition
- Verify `Looping: true` in item JSON
- Ensure bone names match between model and animation

### Bones move unexpectedly
- Check pivot point positions
- Verify parent-child relationships
- Look for keyframes you didn't intend

### Scale seems wrong
- Confirm using correct format (Prop vs Character)
- Check `CustomModelScale` in item definition
- Compare against player reference model

## File Locations

For a typical Hytale mod:

```
pack/
├── Common/
│   ├── Blocks/
│   │   ├── model_name.blockymodel
│   │   └── Animations/
│   │       └── animation.anim_name.blockyanim
│   └── BlockTextures/
│       └── texture_name.png
└── Server/
    └── Item/
        └── Items/
            └── Item_Name.json  (references model)
```

## Related Documentation

- [Texture Creation Guide](texture-creation-guide.md) - Creating and mapping textures
- [Blockbench MCP Reference](blockbench-mcp-reference.md) - Automation commands
- [Official Hytale Art Guide](external/CREATING_MODELS.md) - Art direction principles
- [Gradle Build Process](gradle-build-process.md) - Deploying to server
