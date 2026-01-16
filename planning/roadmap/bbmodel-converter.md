# Roadmap: bbmodel → blockymodel Converter

## Status: Future Investigation

## Problem

The Hytale Blockbench plugin creates new models in blockymodel format but cannot import/convert existing bbmodel files.

## Opportunity

Many Minecraft modders have existing bbmodel assets. A converter would:

1. Enable migration of existing models to Hytale
2. Allow use of standard Blockbench workflows before exporting
3. Potentially be contributed back to the Hytale plugin

## Investigation Tasks

- [ ] Study blockymodel.ts export logic in hytale-blockbench-plugin
- [ ] Map bbmodel structure to blockymodel structure
- [ ] Handle bone hierarchy translation
- [ ] Handle euler → quaternion conversion for orientations
- [ ] Handle animation format translation (blockyanim)
- [ ] Test with pickle_flagpole.bbmodel as first case

## Key Files

- `research/hytale-blockbench-plugin/src/blockymodel.ts` - export logic
- `research/hytale-blockbench-plugin/src/blockyanim.ts` - animation export
- `src/main/resources/assets/models/pickle_flagpole.bbmodel` - test case

## Notes

Created during pickle pirate flag project planning. Defer until after flag model is complete.
