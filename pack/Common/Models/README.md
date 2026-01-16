# 3D Models

Place Blockbench models here.

## Quick Start: Use the Script

We've created a Blockbench script that generates the model automatically:

```bash
# The script is at:
scripts/blockbench_flagpole.js
```

**To use:**
1. Open Blockbench
2. Press `Ctrl+Shift+I` → Console tab
3. Paste the script contents
4. Press Enter → Model created!

See `documentation/BLOCKBENCH_GUIDE.md` for full manual steps.

---

## Required Files

### pickle_flagpole.bbmodel
The flagpole 3D model.

## Model Structure

```
root (origin: 8, 0, 8)
├── pole (origin: 8, 0, 8)
│   └── pole_mesh [2×32×2]
├── crossbar (origin: 8, 30, 8)
│   └── crossbar_mesh [12×2×2]
└── flag (origin: 10, 30, 8)  ← Animation pivot
    └── flag_mesh [16×16×0.5]
```

## Dimensions

| Part | Size (W×H×D) | Purpose |
|------|--------------|---------|
| Pole | 2×32×2 | Vertical wooden pole |
| Crossbar | 12×2×2 | Horizontal bar at top |
| Flag | 16×16×0.5 | The flag (thin plane) |

## Animation

The `flag` bone's pivot is at the attachment point (top-left of flag).
This makes the flag swing naturally when animated by `flag_wave.blockyanim`.

## Save the Model

1. File → Save As (`Ctrl+S`)
2. Save as `pickle_flagpole.bbmodel`
3. Place in this folder

Note: Use **Save**, not Export. Hytale reads `.bbmodel` files directly.
