#!/usr/bin/env python3
"""
SHATROX Button Monitor Service
Monitors 8 GPIO buttons and triggers LLM queries with TTS responses
"""

import gpiod
from gpiod.line import Direction, Bias, Edge
import time
import subprocess
import sys
import os

# ---------------------------------------------------------
# Updated GPIO mapping from user
# ---------------------------------------------------------
# Button to GPIO mapping
BUTTON_MAP = {
    5:  "K1",
    6:  "K2",
    13: "K3",
    19: "K4",
    26: "K8"
}

# Button to question mapping (K1, K2, K8 use special actions)
BUTTON_QUESTIONS = {
    "K1": None,
    "K2": None,
    "K3": "What is the Raspberry Pi?",
    "K4": "Explain the Linux Kernel.",
    "K8": None,
}

INPUT_PINS = list(BUTTON_MAP.keys())
CHIP_PATH = "/dev/gpiochip0"
DEBOUNCE_MS = 500  # half a second is enough
DISPLAY_LOG = "/tmp/shatrox-display.log"

last_press_time = {}


def display_print(msg, end='\n'):
    """Print to stdout and to QML display log"""
    print(msg, end=end, flush=True)
    try:
        with open(DISPLAY_LOG, 'a') as f:
            f.write(msg + end)
            f.flush()
    except Exception as e:
        print(f"Warning: Could not write to display log: {e}", file=sys.stderr)


def handle_button_press(button_name):
    """Handle a button press event"""

    # ----------------------------------------------
    # SPECIAL ACTIONS FOR K1, K2, AND K8
    # ----------------------------------------------

    if button_name == "K8":
        display_print("[K8] System shutdown initiated...")
        subprocess.Popen([
            "speak",
            "System is shutting down in 3 2 1"
        ]).wait()  # Wait for TTS to complete
        display_print("[K8] Shutting down now...")
        subprocess.run(["shutdown", "-h", "now"])
        return

    if button_name == "K1":
        display_print("[K1] Speaking fun message...")
        subprocess.Popen([
            "speak",
            "zoozoo haii yaii yaii"
        ])
        return

    if button_name == "K2":
        display_print("[K2] Speaking greeting message...")
        subprocess.Popen([
            "speak",
            "Hi, I'm Ruby — an AI-powered robot here to answer your questions"
        ])
        return

    # ----------------------------------------------
    # DEFAULT = Llama-ask for K3-K4
    # ----------------------------------------------

    question = BUTTON_QUESTIONS.get(button_name, "Unknown question")

    display_print(f"\n>>> [{button_name}] Question: {question}")
    display_print("━" * 60)

    try:
        result = subprocess.Popen(
            ["llama-ask", question],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            bufsize=1
        )

        for line in result.stdout:
            display_print(line.rstrip())

        result.wait(timeout=60)

        if result.returncode != 0:
            display_print(f"Error: llama-ask returned code {result.returncode}")

    except subprocess.TimeoutExpired:
        display_print("Error: Request timeout")
        result.kill()
    except FileNotFoundError:
        display_print("Error: llama-ask command not found")
    except Exception as e:
        display_print(f"Error: {e}")


def run():
    """Main service loop"""

    line_settings = gpiod.LineSettings(
        direction=Direction.INPUT,
        bias=Bias.PULL_UP,
        edge_detection=Edge.FALLING
    )

    try:
        with gpiod.request_lines(
            CHIP_PATH,
            consumer="shatrox-buttons",
            config={tuple(INPUT_PINS): line_settings}
        ) as request:

            display_print("╔═══════════════════════════════════════════════════════════╗")
            display_print("║        SHATROX Button Service Active                      ║")
            display_print(f"║        Monitoring: {', '.join(BUTTON_MAP.values())}                            ║")
            display_print("╚═══════════════════════════════════════════════════════════╝")
            display_print("")

            while True:

                if request.wait_edge_events(timeout=None):
                    events = request.read_edge_events()

                    # Process ONLY the first real event inside this batch
                    for event in events:
                        gpio_pin = event.line_offset
                        button_name = BUTTON_MAP.get(gpio_pin, "Unknown")

                        now = time.time() * 1000

                        # Debounce check
                        if now - last_press_time.get(button_name, 0) < DEBOUNCE_MS:
                            continue

                        last_press_time[button_name] = now
                        handle_button_press(button_name)

                        # Crucial: stop processing this batch (bounce fix)
                        break

    except OSError as e:
        display_print(f"GPIO Error: {e}")
        sys.exit(1)

    except KeyboardInterrupt:
        display_print("\n\n╔═══════════════════════════════════════════════════════════╗")
        display_print("║        Shatrox Button Service Stopped                     ║")
        display_print("╚═══════════════════════════════════════════════════════════╝")
        sys.exit(0)


if __name__ == "__main__":
    run()