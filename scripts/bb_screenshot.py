#!/usr/bin/env python3
"""
Low-res Blockbench screenshot utility for Claude Code.

This script calls the Blockbench MCP server directly, captures a screenshot,
resizes it with ImageMagick, and outputs the smaller base64 image.

Usage:
    python3 bb_screenshot.py [resize_percent] [quality]
    python3 bb_screenshot.py 25 50    # 25% size, 50% quality (default)
    python3 bb_screenshot.py 50       # 50% size, 50% quality

The output is a base64-encoded PNG suitable for viewing in Claude Code.

Requirements:
    - Blockbench running with MCP plugin active
    - ImageMagick installed (convert command)
    - requests library (pip install requests)
"""
import requests
import base64
import subprocess
import tempfile
import sys
import os
import json

MCP_URL = "http://localhost:3000/bb-mcp"
SESSION_ID = None


def mcp_request(method: str, params: dict = None) -> dict:
    """Send a JSON-RPC request to the MCP server."""
    global SESSION_ID

    payload = {
        "jsonrpc": "2.0",
        "method": method,
        "id": 1
    }
    if params:
        payload["params"] = params

    headers = {"Content-Type": "application/json"}
    if SESSION_ID:
        headers["Mcp-Session-Id"] = SESSION_ID

    try:
        response = requests.post(MCP_URL, json=payload, headers=headers, timeout=30)

        # Check for session ID in response headers
        if "Mcp-Session-Id" in response.headers:
            SESSION_ID = response.headers["Mcp-Session-Id"]

        response.raise_for_status()
        return response.json()
    except requests.exceptions.ConnectionError:
        print("Error: Cannot connect to Blockbench MCP server.", file=sys.stderr)
        print("Make sure Blockbench is running with the MCP plugin active.", file=sys.stderr)
        sys.exit(1)
    except requests.exceptions.Timeout:
        print("Error: MCP request timed out.", file=sys.stderr)
        sys.exit(1)


def initialize_mcp() -> bool:
    """Initialize the MCP session."""
    result = mcp_request("initialize", {
        "protocolVersion": "2024-11-05",
        "capabilities": {},
        "clientInfo": {
            "name": "bb_screenshot",
            "version": "1.0.0"
        }
    })

    if "error" in result:
        print(f"MCP initialization error: {result['error']}", file=sys.stderr)
        return False

    # Send initialized notification
    mcp_request("notifications/initialized")
    return True


def capture_screenshot() -> str:
    """Capture screenshot from Blockbench and return base64 data."""
    result = mcp_request("tools/call", {
        "name": "blockbench_capture_screenshot",
        "arguments": {}
    })

    if "error" in result:
        print(f"Screenshot error: {result['error']}", file=sys.stderr)
        sys.exit(1)

    # Extract base64 from result
    # MCP tool results are in result.result.content[].data or similar
    try:
        content = result.get("result", {}).get("content", [])
        for item in content:
            if item.get("type") == "image":
                return item.get("data", "")
            elif item.get("type") == "text":
                # Sometimes returned as text with base64
                text = item.get("text", "")
                if text.startswith("data:image"):
                    # Data URL format
                    return text.split(",", 1)[1]
                elif len(text) > 100 and text.replace("+", "").replace("/", "").replace("=", "").isalnum():
                    # Likely raw base64
                    return text

        # Fallback: check for direct data field
        if "data" in result.get("result", {}):
            return result["result"]["data"]

        print(f"Unexpected response format: {json.dumps(result, indent=2)}", file=sys.stderr)
        sys.exit(1)

    except Exception as e:
        print(f"Error parsing response: {e}", file=sys.stderr)
        print(f"Response: {json.dumps(result, indent=2)}", file=sys.stderr)
        sys.exit(1)


def resize_image(base64_data: str, resize_percent: int = 25, quality: int = 50) -> str:
    """Resize the image using ImageMagick and return smaller base64."""
    # Decode base64 to temp file
    with tempfile.NamedTemporaryFile(suffix='.png', delete=False) as tmp_in:
        tmp_in.write(base64.b64decode(base64_data))
        tmp_in_path = tmp_in.name

    tmp_out_path = tmp_in_path + '_small.png'

    try:
        # Resize with ImageMagick
        result = subprocess.run([
            'convert', tmp_in_path,
            '-resize', f'{resize_percent}%',
            '-quality', str(quality),
            tmp_out_path
        ], capture_output=True, text=True)

        if result.returncode != 0:
            print(f"ImageMagick error: {result.stderr}", file=sys.stderr)
            # Fall back to original if resize fails
            with open(tmp_in_path, 'rb') as f:
                return base64.b64encode(f.read()).decode()

        # Read resized image
        with open(tmp_out_path, 'rb') as f:
            return base64.b64encode(f.read()).decode()

    finally:
        # Cleanup temp files
        if os.path.exists(tmp_in_path):
            os.unlink(tmp_in_path)
        if os.path.exists(tmp_out_path):
            os.unlink(tmp_out_path)


def main():
    # Parse arguments
    resize_percent = int(sys.argv[1]) if len(sys.argv) > 1 else 25
    quality = int(sys.argv[2]) if len(sys.argv) > 2 else 50

    # Initialize MCP session
    if not initialize_mcp():
        sys.exit(1)

    # Capture screenshot
    base64_data = capture_screenshot()

    if not base64_data:
        print("Error: No image data received", file=sys.stderr)
        sys.exit(1)

    # Resize and output
    small_base64 = resize_image(base64_data, resize_percent, quality)
    print(small_base64)


if __name__ == '__main__':
    main()
