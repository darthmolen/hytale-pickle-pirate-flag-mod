# Blockbench Flagpole Model - Step by Step

## Option 1: Use the Script (Fastest)

1. Open Blockbench
2. Press `Ctrl+Shift+I` to open Developer Tools
3. Go to **Console** tab
4. Copy contents of `scripts/blockbench_flagpole.js`
5. Paste into console and press Enter
6. Model is created automatically!

---

## Option 2: Manual Step-by-Step

### Step 1: Create New Project

1. Open Blockbench
2. **File → New → Generic Model**
3. Name it `pickle_flagpole`

### Step 2: Create the Bone Structure

Bones (Groups) let you animate parts separately.

**Create Root Bone:**
1. Right-click in **Outliner** panel (right side)
2. Click **Add Group**
3. Name it `root`
4. Set Origin: `8, 0, 8` (center of block)

**Create Pole Bone:**
1. Right-click on `root`
2. **Add Group** → name it `pole`
3. Origin: `8, 0, 8`

**Create Crossbar Bone:**
1. Right-click on `root`
2. **Add Group** → name it `crossbar`
3. Origin: `8, 30, 8`

**Create Flag Bone (important for animation):**
1. Right-click on `root`
2. **Add Group** → name it `flag`
3. Origin: `10, 30, 8` ← Pivot point where flag attaches

### Step 3: Create the Pole Cube

1. Select the `pole` bone in Outliner
2. Press `Ctrl+N` or click **Add Cube**
3. Name it `pole_mesh`
4. In the **Element** panel (left side), set:
   - **Position (From):** `7, 0, 7`
   - **Position (To):** `9, 32, 9`
   - This creates a 2×32×2 pole

### Step 4: Create the Crossbar Cube

1. Select the `crossbar` bone
2. **Add Cube** → name it `crossbar_mesh`
3. Set dimensions:
   - **From:** `2, 30, 7`
   - **To:** `14, 32, 9`
   - This creates a 12×2×2 horizontal bar

### Step 5: Create the Flag Cube

1. Select the `flag` bone
2. **Add Cube** → name it `flag_mesh`
3. Set dimensions:
   - **From:** `10, 14, 7.75`
   - **To:** `26, 30, 8.25`
   - This creates a 16×16×0.5 thin flag

### Step 6: Add Textures

**Create texture slots:**
1. Go to **Textures** panel (bottom left)
2. Click **+** to add texture
3. Add two textures:
   - `flagpole` (for pole and crossbar)
   - `pickle_flag` (for the flag)

**Apply textures:**
1. Select `pole_mesh` cube
2. Go to **Paint** mode (top toolbar)
3. Select `flagpole` texture
4. Click **Apply to Selected**
5. Repeat for `crossbar_mesh`
6. Select `flag_mesh`, apply `pickle_flag` texture

### Step 7: UV Mapping

1. Select `flag_mesh`
2. Go to **UV** panel
3. For **north** and **south** faces:
   - Set UV to cover the full texture: `0,0` to `64,64`
4. Side faces can be minimal

### Step 8: Save the Model

**Important:** `.bbmodel` is Blockbench's native format - use **Save**, not Export!

1. **File → Save As** (or `Ctrl+S`)
2. Save as `pickle_flagpole.bbmodel`
3. Copy to `pack/Common/Models/`

Note: The Export menu (gltf, obj, fbx, etc.) is for other 3D software. Hytale uses `.bbmodel` files directly.

---

## Final Bone Hierarchy

```
root (origin: 8, 0, 8)
├── pole (origin: 8, 0, 8)
│   └── pole_mesh [cube 2×32×2]
├── crossbar (origin: 8, 30, 8)
│   └── crossbar_mesh [cube 12×2×2]
└── flag (origin: 10, 30, 8)  ← ANIMATION PIVOT
    └── flag_mesh [cube 16×16×0.5]
```

The `flag` bone's origin is at the **attachment point** (top-left of flag). This means when animated, the flag will swing from where it connects to the pole - like a real flag!

---

## Animation Note

Our `flag_wave.blockyanim` file references the `flag` bone and rotates it on the Z-axis. The pivot point setup ensures it waves naturally.

---

## Blockbench Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `Ctrl+N` | Add Cube |
| `G` | Add Group/Bone |
| `Ctrl+D` | Duplicate |
| `Delete` | Delete selected |
| `R` | Rotate mode |
| `T` | Move mode |
| `S` | Scale mode |
| `Ctrl+Shift+I` | Developer Console |

---

## Resources

- [Blockbench Wiki](https://blockbench.net/wiki/)
- [Hytale Blockbench Guide](https://britakee-studios.gitbook.io/hytale-modding-documentation/resources-and-tools/17-blockbench-modeling-guide)
- [Blockbench Plugins](https://blockbench.net/plugins/)
