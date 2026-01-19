# Roadmap: Fork Blockbench MCP Plugin

## Problem

The current Blockbench MCP plugin's `blockbench_capture_screenshot` returns full-resolution base64 images that can blow up Claude Code's context, killing conversations.

Current workaround: `scripts/bb_screenshot.py` makes HTTP calls to the MCP, then resizes with ImageMagick. This works but adds complexity and requires Python + requests + ImageMagick dependencies.

## Proposed Solution

Fork the Blockbench MCP plugin to add native low-resolution screenshot support.

### Upstream Repository

**Source**: https://github.com/jasonjgardner/blockbench-mcp-plugin

### Changes Required

#### 1. Add `resize` parameter to `blockbench_capture_screenshot`

```typescript
// Current signature
blockbench_capture_screenshot(project?: string)

// Proposed signature
blockbench_capture_screenshot(project?: string, options?: {
  resize?: number,    // Percentage (1-100), default 100
  quality?: number,   // JPEG quality (1-100), default 100
  format?: 'png' | 'jpeg'  // Output format, default 'png'
})
```

#### 2. Implementation

Use Blockbench's built-in canvas manipulation to resize before encoding:

```typescript
// Pseudo-code
const screenshot = Screencam.screenshot()
if (options.resize && options.resize < 100) {
  const canvas = document.createElement('canvas')
  canvas.width = screenshot.width * (options.resize / 100)
  canvas.height = screenshot.height * (options.resize / 100)
  const ctx = canvas.getContext('2d')
  ctx.drawImage(screenshot, 0, 0, canvas.width, canvas.height)
  return canvas.toDataURL(`image/${options.format}`, options.quality / 100)
}
return screenshot.toDataURL()
```

#### 3. Backward Compatibility

Default behavior should match current plugin - full resolution PNG.

### Development Steps

1. Fork repository to personal GitHub
2. Clone fork locally
3. Set up development environment (Blockbench dev mode)
4. Implement resize option
5. Test with Claude Code
6. Submit PR upstream
7. If PR not accepted, maintain fork

### Files to Modify

Based on plugin structure (to be verified):
- `src/tools/screenshot.ts` - Add resize logic
- `src/types.ts` - Update parameter types
- `README.md` - Document new option

### Testing Plan

1. Test with default parameters (should be unchanged)
2. Test with resize=25 (quarter size)
3. Test with resize=50 (half size)
4. Verify context usage reduction in Claude Code
5. Test JPEG format option
6. Test edge cases (resize=0, resize=200)

### Success Criteria

- Screenshot with resize=25 produces ~6% of original file size
- No visible artifacts at resize=25 for model preview purposes
- Backward compatible with existing usage
- PR merged upstream OR maintainable fork

## Alternative Approaches Considered

### 1. Wrapper MCP Server

Create a proxy MCP that intercepts screenshot calls.

**Pros**: No fork needed
**Cons**: More complex setup, another process to run

### 2. Keep Python Script

Current approach with `bb_screenshot.py`.

**Pros**: Works now
**Cons**: Requires Python + requests + ImageMagick, more moving parts

### 3. Client-Side Resize in Claude Code

Have Claude Code's MCP client resize images.

**Pros**: No plugin changes
**Cons**: Image already in context by the time resize happens, defeats purpose

## Priority

**Medium** - Current workaround (`bb_screenshot.py`) is functional. Fork is a quality-of-life improvement.

## Dependencies

- Familiarity with Blockbench plugin development
- Understanding of MCP protocol
- GitHub account for forking

## Timeline

Not scheduled. Implement when:
- Current workaround becomes problematic
- Upstream plugin has breaking changes
- Contributing to open source is prioritized

## References

- [Blockbench MCP Plugin](https://github.com/jasonjgardner/blockbench-mcp-plugin)
- [Blockbench Plugin Development](https://www.blockbench.net/wiki/api/plugin)
- [MCP Protocol Specification](https://spec.modelcontextprotocol.io/)
