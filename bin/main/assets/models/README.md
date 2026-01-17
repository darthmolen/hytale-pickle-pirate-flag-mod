# Pickle Pirate Flag Models

This folder should contain the 3D model files for the flagpole.

## Required Models

### pickle_flagpole.blockymodel
The 3D model for the flagpole block, created in Blockbench.

## Model Structure

The flagpole should consist of:

1. **Pole (vertical)**
   - Dimensions: 2x16x2 (width x height x depth) in Blockbench units
   - Material: Wood texture (flagpole.png)
   - Position: Centered on block

2. **Crossbar (horizontal)**
   - Dimensions: 8x1x1
   - Material: Same wood texture
   - Position: Near top of pole

3. **Flag (animated)**
   - Dimensions: 8x8x0.5
   - Material: pickle_flag.png texture
   - Position: Hanging from crossbar
   - Note: Animation is handled by the server, the model is static

## Creating the Model

1. Download [Blockbench](https://blockbench.net/)
2. Install the Hytale plugin (if available)
3. Create a new model
4. Build the pole, crossbar, and flag elements
5. Apply textures from the textures folder
6. Export as .blockymodel format

## Tips

- Keep polygon count low for performance
- Use box elements (cubes) for simple shapes
- Test the model at different distances
- The flag can be a simple plane - animation is server-side
