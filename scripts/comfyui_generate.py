#!/usr/bin/env python3
"""
ComfyUI Pixel Art Generator
Submits prompts to ComfyUI and retrieves generated images.
"""
import json
import uuid
import urllib.request
import urllib.parse
import time
import sys
from pathlib import Path

SERVER = "127.0.0.1:8188"

def queue_prompt(prompt: dict, client_id: str) -> str:
    """Submit a workflow to ComfyUI, return prompt_id."""
    data = json.dumps({"prompt": prompt, "client_id": client_id}).encode('utf-8')
    req = urllib.request.Request(
        f"http://{SERVER}/prompt",
        data=data,
        headers={'Content-Type': 'application/json'}
    )
    response = json.loads(urllib.request.urlopen(req).read())
    return response['prompt_id']

def get_history(prompt_id: str) -> dict:
    """Get execution history for a prompt."""
    with urllib.request.urlopen(f"http://{SERVER}/history/{prompt_id}") as response:
        return json.loads(response.read()).get(prompt_id, {})

def get_image(filename: str, subfolder: str, folder_type: str) -> bytes:
    """Retrieve generated image data."""
    params = urllib.parse.urlencode({
        "filename": filename,
        "subfolder": subfolder,
        "type": folder_type
    })
    with urllib.request.urlopen(f"http://{SERVER}/view?{params}") as response:
        return response.read()

def generate_pixel_art(
    workflow_path: str,
    positive_prompt: str,
    negative_prompt: str,
    output_path: str,
    seed: int = None
):
    """
    Generate pixel art using ComfyUI.
    """
    # Load workflow template
    with open(workflow_path) as f:
        workflow = json.load(f)

    # Find and update prompt nodes
    for node_id, node in workflow.items():
        class_type = node.get("class_type", "")

        if class_type == "CLIPTextEncode":
            # Node 2 is positive, Node 3 is negative (based on our workflow)
            if node_id == "2":
                node["inputs"]["text"] = positive_prompt
            elif node_id == "3":
                node["inputs"]["text"] = negative_prompt

        elif class_type == "KSampler":
            if seed is not None:
                node["inputs"]["seed"] = seed
            else:
                # Use random seed
                node["inputs"]["seed"] = int(time.time() * 1000) % (2**32)

    # Submit and wait
    client_id = str(uuid.uuid4())
    prompt_id = queue_prompt(workflow, client_id)
    print(f"Submitted prompt: {prompt_id}")

    # Poll for completion
    print("Generating...", end="", flush=True)
    for _ in range(120):  # Max 2 minutes
        try:
            history = get_history(prompt_id)
            if history.get('outputs'):
                print(" done!")
                break
        except Exception:
            pass
        print(".", end="", flush=True)
        time.sleep(1)
    else:
        print(" timeout!")
        raise RuntimeError("Generation timed out")

    # Get the output image
    outputs = history['outputs']
    for node_id, output in outputs.items():
        if 'images' in output:
            for img in output['images']:
                image_data = get_image(
                    img['filename'],
                    img.get('subfolder', ''),
                    img['type']
                )
                Path(output_path).parent.mkdir(parents=True, exist_ok=True)
                Path(output_path).write_bytes(image_data)
                print(f"Saved: {output_path}")
                return output_path

    raise RuntimeError("No images in output")

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description="Generate pixel art via ComfyUI")
    parser.add_argument("--workflow", required=True, help="Path to workflow API JSON")
    parser.add_argument("--prompt", required=True, help="Positive prompt")
    parser.add_argument("--negative", default="photorealistic, 3d render, noise, grain, blurry, text, watermark", help="Negative prompt")
    parser.add_argument("--output", required=True, help="Output path")
    parser.add_argument("--seed", type=int, help="Random seed (optional)")
    args = parser.parse_args()

    generate_pixel_art(
        args.workflow,
        args.prompt,
        args.negative,
        args.output,
        args.seed
    )
