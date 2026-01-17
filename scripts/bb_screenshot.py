#!/usr/bin/env python3
"""Low-res Blockbench screenshot utility to save context."""
import subprocess
import sys
import base64
import tempfile
import os


def capture_low_res_screenshot(resize_percent=25, quality=50):
    """
    Process base64 PNG data through imagemagick to reduce size.

    Usage: pipe base64 data in, get reduced base64 out
    Example: echo "$BASE64_DATA" | python3 bb_screenshot.py
    """
    base64_data = sys.stdin.read().strip()

    if not base64_data:
        print("Error: No base64 data provided on stdin", file=sys.stderr)
        sys.exit(1)

    with tempfile.NamedTemporaryFile(suffix='.png', delete=False) as tmp_in:
        tmp_in.write(base64.b64decode(base64_data))
        tmp_in_path = tmp_in.name

    tmp_out_path = tmp_in_path + '_small.png'

    try:
        subprocess.run([
            'convert', tmp_in_path,
            '-resize', f'{resize_percent}%',
            '-quality', str(quality),
            tmp_out_path
        ], check=True, capture_output=True)

        with open(tmp_out_path, 'rb') as f:
            result = base64.b64encode(f.read()).decode()

        print(result)
    finally:
        if os.path.exists(tmp_in_path):
            os.unlink(tmp_in_path)
        if os.path.exists(tmp_out_path):
            os.unlink(tmp_out_path)


if __name__ == '__main__':
    # Allow optional resize percentage as argument
    resize = int(sys.argv[1]) if len(sys.argv) > 1 else 25
    quality = int(sys.argv[2]) if len(sys.argv) > 2 else 50
    capture_low_res_screenshot(resize, quality)
