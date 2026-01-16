/**
 * Blockbench Script: Pickle Pirate Flagpole Generator
 *
 * HOW TO USE:
 * 1. Open Blockbench
 * 2. Go to File > Plugins > Load Plugin from File
 *    OR press Ctrl+Shift+P and paste this script
 * 3. The model will be generated automatically
 *
 * Alternatively, use the Console:
 * 1. Press Ctrl+Shift+I to open DevTools
 * 2. Go to Console tab
 * 3. Paste this entire script and press Enter
 */

(function() {
    // Create new project
    if (Project && Project.geometry_name) {
        // Project exists, ask to overwrite
        if (!confirm('This will create a new model. Continue?')) return;
    }

    // Start new Generic Model (works for Hytale)
    newProject(Formats['generic_model'] || Formats['free']);
    Project.name = 'pickle_flagpole';

    // ============================================
    // CONFIGURATION - Adjust these values as needed
    // ============================================

    const CONFIG = {
        // Pole dimensions
        pole: {
            width: 2,
            height: 32,  // 2 blocks tall
            depth: 2,
            offsetY: 0
        },
        // Crossbar at top
        crossbar: {
            width: 12,
            height: 2,
            depth: 2,
            offsetY: 30  // Near top of pole
        },
        // Flag hanging from crossbar
        flag: {
            width: 16,
            height: 16,
            depth: 0.5,
            offsetX: 2,   // Offset from center
            offsetY: 14   // Hangs below crossbar
        },
        // Texture sizes (adjust to match your actual textures)
        textures: {
            pole: { width: 32, height: 32 },
            flag: { width: 64, height: 64 }
        }
    };

    // ============================================
    // CREATE BONES (Groups)
    // ============================================

    // Root bone
    const root = new Group({
        name: 'root',
        origin: [8, 0, 8]  // Center of block
    }).init().addTo();

    // Pole bone
    const poleBone = new Group({
        name: 'pole',
        origin: [8, 0, 8]
    }).init().addTo(root);

    // Crossbar bone
    const crossbarBone = new Group({
        name: 'crossbar',
        origin: [8, CONFIG.crossbar.offsetY, 8]
    }).init().addTo(root);

    // Flag bone (this will be animated)
    const flagBone = new Group({
        name: 'flag',
        origin: [8 + CONFIG.flag.offsetX, CONFIG.crossbar.offsetY, 8]  // Pivot at attachment point
    }).init().addTo(root);

    // ============================================
    // CREATE CUBES
    // ============================================

    // Pole cube
    const poleCube = new Cube({
        name: 'pole_mesh',
        from: [
            8 - CONFIG.pole.width/2,
            CONFIG.pole.offsetY,
            8 - CONFIG.pole.depth/2
        ],
        to: [
            8 + CONFIG.pole.width/2,
            CONFIG.pole.offsetY + CONFIG.pole.height,
            8 + CONFIG.pole.depth/2
        ],
        faces: {
            north: { texture: null, uv: [0, 0, 2, 32] },
            south: { texture: null, uv: [0, 0, 2, 32] },
            east:  { texture: null, uv: [0, 0, 2, 32] },
            west:  { texture: null, uv: [0, 0, 2, 32] },
            up:    { texture: null, uv: [0, 0, 2, 2] },
            down:  { texture: null, uv: [0, 0, 2, 2] }
        }
    }).init().addTo(poleBone);

    // Crossbar cube
    const crossbarCube = new Cube({
        name: 'crossbar_mesh',
        from: [
            8 - CONFIG.crossbar.width/2,
            CONFIG.crossbar.offsetY,
            8 - CONFIG.crossbar.depth/2
        ],
        to: [
            8 + CONFIG.crossbar.width/2,
            CONFIG.crossbar.offsetY + CONFIG.crossbar.height,
            8 + CONFIG.crossbar.depth/2
        ],
        faces: {
            north: { texture: null, uv: [0, 0, 12, 2] },
            south: { texture: null, uv: [0, 0, 12, 2] },
            east:  { texture: null, uv: [0, 0, 2, 2] },
            west:  { texture: null, uv: [0, 0, 2, 2] },
            up:    { texture: null, uv: [0, 0, 12, 2] },
            down:  { texture: null, uv: [0, 0, 12, 2] }
        }
    }).init().addTo(crossbarBone);

    // Flag cube (thin plane)
    const flagCube = new Cube({
        name: 'flag_mesh',
        from: [
            8 + CONFIG.flag.offsetX,
            CONFIG.flag.offsetY,
            8 - CONFIG.flag.depth/2
        ],
        to: [
            8 + CONFIG.flag.offsetX + CONFIG.flag.width,
            CONFIG.flag.offsetY + CONFIG.flag.height,
            8 + CONFIG.flag.depth/2
        ],
        faces: {
            north: { texture: null, uv: [0, 0, CONFIG.textures.flag.width, CONFIG.textures.flag.height] },  // Main flag face
            south: { texture: null, uv: [0, 0, CONFIG.textures.flag.width, CONFIG.textures.flag.height] },  // Back of flag
            east:  { texture: null, uv: [0, 0, 1, CONFIG.textures.flag.height] },
            west:  { texture: null, uv: [0, 0, 1, CONFIG.textures.flag.height] },
            up:    { texture: null, uv: [0, 0, CONFIG.textures.flag.width, 1] },
            down:  { texture: null, uv: [0, 0, CONFIG.textures.flag.width, 1] }
        }
    }).init().addTo(flagBone);

    // ============================================
    // SETUP COMPLETE
    // ============================================

    // Select root for easy viewing
    root.select();

    // Update view
    Canvas.updateAll();

    // Show success message
    Blockbench.showQuickMessage('Pickle Flagpole model created!', 2000);

    console.log(`
========================================
Pickle Pirate Flagpole Model Created!
========================================

Structure:
  root
  ├── pole (bone)
  │   └── pole_mesh (cube)
  ├── crossbar (bone)
  │   └── crossbar_mesh (cube)
  └── flag (bone) ← This bone will be animated
      └── flag_mesh (cube)

Next Steps:
1. Import your texture files:
   - Textures panel → Click folder icon (Import Texture)
   - Import flagpole.png (32x32 wood texture)
   - Import pickle_flag.png (32x32 or 64x64 flag design)

2. Apply textures to meshes:
   - Select pole_mesh and crossbar_mesh in Outliner
   - Right-click flagpole.png → "Apply to Untextured Faces"
   - Select flag_mesh in Outliner
   - Right-click pickle_flag.png → "Apply to Untextured Faces"

3. File > Save As (Ctrl+S)
   Save as: pickle_flagpole.bbmodel

4. Copy files to your pack:
   - Model: pack/Common/Models/pickle_flagpole.bbmodel
   - Textures: pack/Common/BlockTextures/

The 'flag' bone pivot is set at the attachment point
so it will wave naturally when animated.
========================================
`);

})();
