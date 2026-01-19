# Texture Creation Guide for Hytale Models

This guide covers creating and applying textures to Hytale models, including UV mapping fundamentals and practical workflows using ImageMagick.

## Hytale Texture Fundamentals

### Texture Size Requirements

All textures must be **multiples of 32 pixels** in both dimensions:
- Valid: 32x32, 64x64, 128x96, 256x288
- Invalid: 50x50, 100x100, 48x48

Non-square textures are allowed (e.g., 256x288 for our flagpole).

### Texel Density

Hytale uses two texel density levels that affect how textures are rendered in-game:

| Model Type | Texel Density | Example |
|------------|---------------|---------|
| Prop/Block | 32 pixels per world block | Furniture, flags, decorations |
| Character/Attachment | 64 pixels per world block | Player, weapons, cosmetics |

**What this means**: A 32-pixel wide texture region covers 1 world block on a prop. On a character, that same 32-pixel region would only cover half a world block (appear twice as detailed).

Choose your format in Blockbench when creating the project:
- File > New > Hytale Prop (32px density)
- File > New > Hytale Character (64px density)

### UV Coordinate System

**Key insight**: In the exported `.blockymodel` format, UV offsets are **direct pixel coordinates**.

```
UV offset (x, y) = Pixel position on texture

Example:
- Texture: 256x288 pixels
- Cube size: 64x16 pixels
- UV offset: (0, 32)
- This cube uses pixels from (0,32) to (64,48)
```

There is no scaling factor. 1 model unit = 1 texture pixel.

## Creating Textures with ImageMagick

ImageMagick is a powerful command-line tool for texture manipulation. Here are common operations:

### Removing Backgrounds (Transparency)

Convert a white background to transparent:

```bash
# Remove white background with 10% fuzz tolerance
convert input.png -fuzz 10% -transparent white output.png

# Ensure output is RGBA (has alpha channel)
convert output.png -type TrueColorAlpha PNG32:output.png
```

The `-fuzz` percentage allows for near-white pixels to also become transparent.

### Resizing Textures

```bash
# Resize to specific dimensions
convert input.png -resize 64x64 output.png

# Resize with high-quality filter (for downscaling)
convert input.png -resize 64x64 -filter Lanczos output.png

# Resize to percentage
convert input.png -resize 50% output.png
```

### Rotating Textures

Useful for changing wood grain direction:

```bash
# Rotate 90 degrees clockwise
convert input.png -rotate 90 output.png

# Rotate 90 degrees counter-clockwise
convert input.png -rotate -90 output.png
```

### Compositing (Combining Images)

Build a texture atlas by compositing multiple sources:

```bash
# Create base canvas
convert -size 256x288 xc:white canvas.png

# Overlay image at specific position
convert canvas.png overlay.png -geometry +0+32 -composite output.png

# Multiple overlays
convert canvas.png \
  flag.png -geometry +0+32 -composite \
  pole.png -geometry +64+32 -composite \
  crossbar.png -geometry +0+0 -composite \
  output.png
```

### Tiling Textures

Create repeating patterns:

```bash
# Tile a 32x32 texture to fill 256x256
convert -size 256x256 tile:input_32x32.png output.png

# Tile in one direction only (horizontal)
convert input.png -resize 32x256! -write mpr:tile +delete \
  -size 256x256 tile:mpr:tile output.png
```

### Extracting Regions

```bash
# Extract a 64x64 region starting at (128, 32)
convert input.png -crop 64x64+128+32 +repage output.png
```

## UV Mapping in Blockbench

### Box UV vs Per-Face UV

Blockbench offers two UV mapping modes:

**Box UV** (simple):
- Single UV offset for entire cube
- Automatically wraps faces
- Good for simple textured boxes

**Per-Face UV** (custom):
- Individual UV offset per face
- More control, more work
- Required for complex texture atlases

Toggle in Blockbench: Edit > Cube UV Mode

### Auto UV

Blockbench can automatically calculate UV positions:

1. Select element(s)
2. Right-click > Auto UV

Or use the action: `uv_auto`

The Hytale plugin handles most UV mapping automatically when you:
1. Create a texture
2. Apply it to elements
3. Use "Auto UV" on faces

### Manual UV Adjustment

When auto UV doesn't work:

1. Select the element
2. Open the UV panel (View > UV Editor)
3. Drag UV regions to correct positions
4. Or modify `uv_offset` in element properties

Remember: Offsets are in **pixels**, not scaled coordinates.

## Practical Example: Flagpole Texture

Our pickle pirate flagpole uses a 256x288 texture with this layout:

```
     0        64       128      192      256
   0 +--------+--------+--------+--------+
     |     CROSSBAR TEXTURE (grain →)    | 8px
   8 +--------+--------+--------+--------+
     |     CROSSBAR TEXTURE 2            | 8px
  16 +--------+--------+--------+--------+
     |        |        |        |        |
  32 |  FLAG  | POLE   | POLE   | (avail)|
     | 64x64  | texture| texture|        |
     |        | 64x256 | alt    |        |
  96 +--------+        |        |        |
     | (avail)|        |        |        |
     |        |        |        |        |
 288 +--------+--------+--------+--------+
```

### Creating the Texture

```bash
# Start with base canvas
convert -size 256x288 xc:transparent flagpole_texture.png

# Add flag image (64x64) at position (0, 32)
convert flagpole_texture.png \
  pickle_flag_64.png -geometry +0+32 -composite \
  flagpole_texture.png

# Add pole texture (tiled wood) at position (64, 32)
convert wood_32.png -rotate 90 -resize 64x256! pole_strip.png
convert flagpole_texture.png \
  pole_strip.png -geometry +64+32 -composite \
  flagpole_texture.png

# Add crossbar texture (horizontal grain) at top
convert wood_32.png -resize 256x16! crossbar_strip.png
convert flagpole_texture.png \
  crossbar_strip.png -geometry +0+0 -composite \
  flagpole_texture.png
```

### Mapping Flag Segments

The flag is split into 4 segments for wave animation, each 64x16 pixels:

| Segment | Model Size | UV Offset | Texture Region |
|---------|------------|-----------|----------------|
| flag_1 | 64x16x1 | (0, 32) | Y=32-48 |
| flag_2 | 64x16x1 | (0, 48) | Y=48-64 |
| flag_3 | 64x16x1 | (0, 64) | Y=64-80 |
| flag_4 | 64x16x1 | (0, 80) | Y=80-96 |

Each segment maps to 1/4 of the 64x64 flag image.

## Texture Best Practices

### From Official Hytale Art Direction

1. **Paint shadows into textures** - Don't rely only on in-game lighting
2. **Avoid pure white (#FFFFFF)** and **pure black (#000000)** - They break lighting
3. **Add color to shadows** - Purple hints make models more vibrant
4. **Avoid noise and grain** - Keep textures clean
5. **Avoid perfectly flat surfaces** - Add subtle variation

### For UV Mapping

1. **Plan your texture atlas** before modeling
2. **Leave padding** between texture regions (1-2px) to avoid bleeding
3. **Test in-game** - Blockbench preview may differ
4. **Use power-of-2 sizes** when possible (32, 64, 128, 256)

### Grain Direction

For wood textures, ensure grain runs in the correct direction:

| Element | Grain Direction | Rotation Needed |
|---------|-----------------|-----------------|
| Vertical pole | Vertical (↕) | None or 90° depending on source |
| Horizontal crossbar | Horizontal (↔) | None or 90° depending on source |

## Troubleshooting

### Texture appears white/missing
- Check file path in model references
- Verify texture is deployed to server
- Ensure pack manifest is valid

### UV appears offset in-game
- Recalculate UV offsets (remember: pixels, not scaled)
- Check texture dimensions match expectations
- Try "Auto UV" in Blockbench

### Transparency not working
- Ensure PNG has alpha channel (RGBA, not RGB)
- Use `PNG32:` prefix in ImageMagick
- Check Hytale doesn't expect opaque textures for that element type

### Pixel stretching visible
- Stay within 0.7x to 1.3x stretch limits
- Consider higher resolution texture
- Adjust UV mapping to use more texture space

## Related Documentation

- [Model Creation Guide](model-creation-guide.md) - Building 3D models
- [Blockbench MCP Reference](blockbench-mcp-reference.md) - Automation commands
- [Official Hytale Art Guide](external/CREATING_MODELS.md) - Art direction principles
