# Blockbench MCP Reference

Quick reference for Blockbench MCP tools available for automation with Claude Code.

## Prerequisites

- Blockbench running with MCP plugin active
- MCP server at `http://localhost:3000/bb-mcp`
- Project file open in Blockbench

**Plugin Repository**: https://github.com/jasonjgardner/blockbench-mcp-plugin

## Essential Tools

### Inspection

#### `blockbench_list_outline`
Get the current bone/element hierarchy.

```json
{}
```

Returns tree structure of all groups and elements.

#### `blockbench_capture_screenshot`
**WARNING**: Use `scripts/bb_screenshot.py` instead to avoid context blowup.

```json
{
  "project": "optional_project_name"
}
```

#### `blockbench_list_textures`
List all textures in the project.

```json
{}
```

#### `blockbench_get_texture`
Get texture image data.

```json
{
  "texture": "texture_name_or_id"
}
```

### Geometry Creation

#### `blockbench_add_group`
Create a bone (group).

```json
{
  "name": "bone_name",
  "origin": [0, 0, 0],
  "rotation": [0, 0, 0],
  "parent": "parent_name",
  "visibility": true,
  "autouv": "0"
}
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| name | Yes | Bone name |
| origin | Yes | Pivot point [x, y, z] |
| rotation | Yes | Initial rotation [x, y, z] |
| parent | No | Parent bone name (default: "root") |

#### `blockbench_place_cube`
Add cube elements.

```json
{
  "elements": [
    {
      "name": "cube_name",
      "from": [0, 0, 0],
      "to": [16, 16, 16],
      "origin": [8, 8, 8],
      "rotation": [0, 0, 0]
    }
  ],
  "group": "parent_bone",
  "texture": "texture_name",
  "faces": true
}
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| elements | Yes | Array of cube definitions |
| group | No | Parent bone name |
| texture | No | Texture to apply |
| faces | No | Auto UV mapping (true/false or face array) |

#### `blockbench_modify_cube`
Modify existing cube properties.

```json
{
  "id": "cube_name_or_id",
  "from": [0, 0, 0],
  "to": [16, 16, 16],
  "origin": [8, 8, 8],
  "rotation": [0, 0, 0],
  "uv_offset": [0, 0],
  "visibility": true
}
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| id | No | Element ID (defaults to selected) |
| uv_offset | No | UV position in **pixels** |
| autouv | No | "0"=off, "1"=on, "2"=relative |

#### `blockbench_remove_element`
Delete an element.

```json
{
  "id": "element_name_or_id"
}
```

#### `blockbench_duplicate_element`
Duplicate element with optional offset.

```json
{
  "id": "element_name",
  "newName": "copy_name",
  "offset": [0, 16, 0]
}
```

#### `blockbench_rename_element`
Rename an element.

```json
{
  "id": "old_name",
  "new_name": "new_name"
}
```

### Textures

#### `blockbench_create_texture`
Create or load a texture.

```json
{
  "name": "texture_name",
  "width": 256,
  "height": 288,
  "data": "path/to/file.png",
  "fill_color": "#FFFFFF"
}
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| name | Yes | Texture name |
| width | No | Width in pixels (default: 16) |
| height | No | Height in pixels (default: 16) |
| data | No | File path or data URL |
| fill_color | No | Fill color if no data |

#### `blockbench_apply_texture`
Apply texture to elements.

```json
{
  "id": "element_name",
  "texture": "texture_name",
  "applyTo": "blank"
}
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| id | Yes | Element to apply to |
| texture | Yes | Texture name |
| applyTo | No | "all", "blank", or "none" |

### Animation

#### `blockbench_create_animation`
Create animation with keyframes.

```json
{
  "name": "wave",
  "animation_length": 2.0,
  "loop": true,
  "bones": {
    "flag_1": [
      {"time": 0, "rotation": [0, 0, 0]},
      {"time": 0.5, "rotation": [0, 0, 10]},
      {"time": 1.0, "rotation": [0, 0, 0]},
      {"time": 1.5, "rotation": [0, 0, -10]},
      {"time": 2.0, "rotation": [0, 0, 0]}
    ]
  }
}
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| name | Yes | Animation name |
| animation_length | No | Duration in seconds |
| loop | No | Whether to loop |
| bones | Yes | Keyframes per bone |

#### `blockbench_manage_keyframes`
Add, edit, or delete keyframes.

```json
{
  "action": "create",
  "bone_name": "flag_1",
  "channel": "rotation",
  "keyframes": [
    {"time": 0.5, "values": [0, 0, 15], "interpolation": "linear"}
  ],
  "animation_id": "wave"
}
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| action | Yes | "create", "delete", "edit", "select" |
| bone_name | Yes | Target bone |
| channel | Yes | "rotation", "position", or "scale" |
| keyframes | Yes | Keyframe data array |

#### `blockbench_animation_timeline`
Control animation playback.

```json
{
  "action": "play"
}
```

Actions: "play", "pause", "stop", "set_time", "set_length", "set_fps", "loop"

### Actions

#### `blockbench_trigger_action`
Trigger any Blockbench action.

```json
{
  "action": "export_over",
  "confirmDialog": true
}
```

Common actions:

| Action | Description |
|--------|-------------|
| `export_over` | Export to current file |
| `export_blockmodel` | Export Java block model |
| `export_bedrock` | Export Bedrock model |
| `export_gltf` | Export glTF |
| `uv_auto` | Auto UV selected elements |
| `undo` | Undo last action |
| `redo` | Redo |
| `select_all` | Select all elements |
| `delete` | Delete selected |

## Mesh Tools (Advanced)

#### `blockbench_place_mesh`
Create mesh elements (more complex than cubes).

```json
{
  "elements": [
    {
      "name": "mesh_name",
      "position": [0, 0, 0],
      "vertices": [
        [0, 0, 0],
        [16, 0, 0],
        [16, 16, 0],
        [0, 16, 0]
      ]
    }
  ],
  "group": "parent_bone"
}
```

#### `blockbench_create_sphere`
Create sphere mesh.

```json
{
  "elements": [
    {
      "name": "sphere",
      "position": [8, 8, 8],
      "diameter": 16,
      "sides": 12
    }
  ]
}
```

**Note**: Spheres are NOT allowed in Hytale models - use only for Blockbench testing.

#### `blockbench_create_cylinder`
Create cylinder mesh.

```json
{
  "elements": [
    {
      "name": "cylinder",
      "position": [8, 0, 8],
      "diameter": 16,
      "height": 32,
      "sides": 12,
      "capped": true
    }
  ]
}
```

## Paint Tools

#### `blockbench_paint_with_brush`
Paint on textures.

```json
{
  "texture_id": "texture_name",
  "coordinates": [
    {"x": 10, "y": 10},
    {"x": 11, "y": 10}
  ],
  "brush_settings": {
    "color": "#FF0000",
    "size": 1,
    "opacity": 255
  }
}
```

#### `blockbench_paint_fill_tool`
Fill area with color.

```json
{
  "texture_id": "texture_name",
  "x": 10,
  "y": 10,
  "color": "#00FF00",
  "fill_mode": "color_connected"
}
```

#### `blockbench_eraser_tool`
Erase pixels.

```json
{
  "texture_id": "texture_name",
  "coordinates": [{"x": 10, "y": 10}],
  "brush_size": 3
}
```

## Camera Control

#### `blockbench_set_camera_angle`
Set viewport camera.

```json
{
  "angle": {
    "position": [50, 50, 50],
    "target": [0, 50, 0],
    "projection": "perspective"
  }
}
```

## Rigging

#### `blockbench_bone_rigging`
Manipulate bone structure.

```json
{
  "action": "create",
  "bone_data": {
    "name": "new_bone",
    "origin": [0, 0, 0],
    "parent": "root"
  }
}
```

Actions: "create", "parent", "unparent", "delete", "rename", "set_pivot"

## Utility

#### `blockbench_risky_eval`
Execute JavaScript in Blockbench. Use with caution.

```json
{
  "code": "Project.name"
}
```

#### `blockbench_emulate_clicks`
Simulate mouse clicks (for UI interaction).

```json
{
  "position": {"x": 100, "y": 200, "button": "left"},
  "drag": {"to": {"x": 150, "y": 250}, "duration": 100}
}
```

## Common Patterns

### Create Bone with Cube

```json
// Step 1: Create bone
blockbench_add_group({
  "name": "arm",
  "origin": [4, 12, 0],
  "rotation": [0, 0, 0],
  "parent": "body"
})

// Step 2: Add cube to bone
blockbench_place_cube({
  "elements": [{
    "name": "arm_cube",
    "from": [2, 0, -2],
    "to": [6, 12, 2],
    "origin": [4, 12, 0]
  }],
  "group": "arm",
  "faces": true
})
```

### Apply Texture and Fix UV

```json
// Step 1: Create texture
blockbench_create_texture({
  "name": "main",
  "width": 64,
  "height": 64,
  "data": "textures/model_texture.png"
})

// Step 2: Apply to element
blockbench_apply_texture({
  "id": "arm_cube",
  "texture": "main",
  "applyTo": "blank"
})

// Step 3: Adjust UV if needed
blockbench_modify_cube({
  "id": "arm_cube",
  "uv_offset": [32, 16]
})
```

### Create Looping Animation

```json
blockbench_create_animation({
  "name": "idle",
  "animation_length": 4.0,
  "loop": true,
  "bones": {
    "head": [
      {"time": 0, "rotation": [0, 0, 0]},
      {"time": 2, "rotation": [5, 0, 0]},
      {"time": 4, "rotation": [0, 0, 0]}
    ],
    "body": [
      {"time": 0, "position": [0, 0, 0]},
      {"time": 2, "position": [0, 0.5, 0]},
      {"time": 4, "position": [0, 0, 0]}
    ]
  }
})
```

## Error Handling

### "Server not initialized"
MCP connection failed. Ask user to:
1. Check Blockbench is running
2. Verify MCP plugin is enabled
3. Restart Blockbench

### "Element not found"
The specified element ID/name doesn't exist. Use `blockbench_list_outline` to verify names.

### "Invalid texture"
Texture doesn't exist or path is wrong. Use `blockbench_list_textures` to check.

## Related Documentation

- [Blockbench MCP Skill](../.claude/skills/blockbench-mcp.md) - Workflow guide
- [Model Creation Guide](model-creation-guide.md) - Manual Blockbench usage
- [Texture Creation Guide](texture-creation-guide.md) - Texture workflows
