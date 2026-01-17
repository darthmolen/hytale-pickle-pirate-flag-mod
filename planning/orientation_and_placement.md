# Orientation and Placement Findings

## The Problem

When holding the Pickle Pirate Flag, the flag appears to point LEFT.
When placed in the world, the flag points RIGHT.

This is confusing for players who expect the held preview to match placement.

---

## Investigation Results

### Model Origin
- Model origin: `[0, 0, 0]` (base of pole)
- Crossbar extends in +X direction (to the right)
- Flag hangs from crossbar in +X direction

### PlayerAnimationsId
Current setting: `"PlayerAnimationsId": "Block"`

This uses Hytale's standard block-holding animation, which:
- Positions the block in front of the player
- May apply rotation/mirroring for hand position
- Doesn't account for asymmetric models like our flagpole

### Placement Behavior
- `VariantRotation: "NESW"` allows 4-way rotation
- Player faces determine placement rotation
- Model +X aligns with placement direction

---

## Why They Differ

```
HELD ITEM (PlayerAnimationsId: "Block")
┌─────────────────────────────────────┐
│  Player holds block in right hand   │
│  Block may be mirrored for display  │
│  +X appears to point LEFT           │
└─────────────────────────────────────┘

PLACED BLOCK (VariantRotation: "NESW")
┌─────────────────────────────────────┐
│  Block placed facing player         │
│  +X aligns with facing direction    │
│  Flag appears to point RIGHT        │
└─────────────────────────────────────┘
```

---

## Potential Solutions

### Option 1: Accept the Difference (Recommended for MVP)
- Held item is temporary (only while placing)
- Placement is correct
- No code changes needed
- Most games have this quirk with asymmetric items

### Option 2: Custom PlayerAnimationsId
- Requires Java plugin
- Create custom animation that doesn't mirror
- Complex, may not be worth the effort

### Option 3: Mirror Model + Adjust Placement
- Mirror model in Blockbench (flip on X)
- Adjust placement rotation to compensate
- Risk: May break UV mapping or animation

### Option 4: Icon-Only Held Display
- Use a 2D icon when held instead of 3D model
- Set `"Scale": 0` in item JSON to hide 3D model
- Use `IconProperties` for held display
- Hides the problem entirely

---

## Decision

For MVP: **Option 1 - Accept the difference**

The placement behavior is correct, and players will quickly learn that the flag places to the right. This is consistent with how many games handle asymmetric items.

If players complain, revisit with Option 4 (icon-only held display).

---

## Related Files

- Item JSON: `Server/Item/Items/Pickle_Flag.json`
- Model: `Common/Blocks/pickle_pirate_flagpole.blockymodel`
- Blockbench project: `src/main/resources/assets/models/pickle_pirate_flagpole_hytale.bbmodel`
