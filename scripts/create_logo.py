#!/usr/bin/env python3
"""
Create CurseForge logo from pickle pirate source image.

Resizes the 578x578 pickle_pirate1.jpg to 256x256 PNG for CurseForge.
"""

import os
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    print("Error: Pillow is required. Install with: pip install Pillow")
    exit(1)


def create_logo():
    # Paths relative to project root
    project_root = Path(__file__).parent.parent
    source_path = project_root / "src/main/resources/assets/textures/wip/pickle_pirate1.jpg"
    output_dir = project_root / "curseforge"
    output_path = output_dir / "logo.png"

    # Ensure output directory exists
    output_dir.mkdir(exist_ok=True)

    # Check source exists
    if not source_path.exists():
        print(f"Error: Source image not found: {source_path}")
        exit(1)

    # Load and resize
    print(f"Loading: {source_path}")
    img = Image.open(source_path)
    print(f"Original size: {img.size}")

    # Resize to 400x400 with high-quality Lanczos resampling (CurseForge minimum)
    img = img.resize((400, 400), Image.Resampling.LANCZOS)
    print(f"Resized to: {img.size}")

    # Convert to RGB if needed (in case of RGBA or other modes)
    if img.mode != "RGB":
        img = img.convert("RGB")

    # Save as PNG
    img.save(output_path, "PNG", optimize=True)
    print(f"Saved: {output_path}")


if __name__ == "__main__":
    create_logo()
