# EasyStego

A drag-and-drop image steganography desktop application built in Java that lets you hide and reveal secret text messages inside PNG image files — no technical knowledge required.

---

## Table of Contents

- [Features](#features)
- [How It Works](#how-it-works)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Key Components](#key-components)
- [Core Algorithm](#core-algorithm)
- [Tech Stack](#tech-stack)
- [Results](#results)
- [Limitations](#limitations)
- [Future Work](#future-work)
- [Academic Context](#academic-context)

---

## Features

- **Drag-and-drop interface** — load images by simply dropping them into the app
- **LSB steganography** — hides messages in the least significant bits of pixel color channels, making changes visually imperceptible
- **Two-tab GUI** — a dedicated **Hide Message** tab and a **Reveal Message** tab
- **Real-time capacity indicator** — shows the maximum message length a loaded image can hold
- **Lossless PNG output** — stego-images are saved as PNG to preserve embedded bits exactly
- **One-click clipboard copy** — decoded messages can be copied instantly
- **Error handling** — alerts for oversized messages and unsupported file formats
- **Cross-platform** — runs on Windows, macOS, and Linux with Java installed

---

## How It Works

EasyStego uses the **Least Significant Bit (LSB)** technique, one of the most widely studied steganographic methods due to its simplicity and minimal perceptual distortion.

### Embedding Process

1. The secret message is converted to a byte array and a terminator (`###END###`) is appended.
2. Each bit of the message overwrites the LSB of a pixel's color channel.
3. Since only the last bit of each pixel changes, the output image is visually identical to the original.
4. The stego-image is saved as a lossless PNG file.

### Extraction Process

1. The stego-image is loaded and pixel data is read into a buffer.
2. The LSB of each pixel is read sequentially, reconstructing bits into bytes.
3. Bytes are assembled until the `###END###` terminator is detected.
4. The recovered bytes are decoded back into a human-readable string.

### Capacity Formula

```
Capacity (characters) = (Image Width × Image Height) / 8
```

This value is displayed to the user in real time after an image is loaded.

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK) **11 or higher**

### Run the application

```bash
java -jar EasyStego.jar
```

### Hide a message

1. Open the **Hide Message** tab.
2. Drag and drop a PNG or JPEG image into the drop zone.
3. Type your secret message in the text area.
4. Click **Embed** — the stego-image is saved as a lossless PNG file.

### Reveal a message

1. Open the **Reveal Message** tab.
2. Drag and drop a stego-image (PNG) into the drop zone.
3. The hidden message is displayed automatically.
4. Click **Copy to Clipboard** to reuse the decoded text.

---

## Project Structure

```
EasyStego/
├── src/
│   ├── Main.java                  # Entry point
│   ├── StegoEncoder.java          # LSB embedding logic
│   ├── StegoDecoder.java          # LSB extraction logic
│   ├── DragDropHandler.java       # Custom TransferHandler for drag-and-drop
│   ├── CapacityCalculator.java    # Computes max embeddable message length
│   └── ImagePreviewPanel.java     # Scales and renders image previews
├── resources/
│   └── icon.png
├── EasyStego.jar
└── README.md
```

---

## Key Components

| Module | Responsibility |
|---|---|
| `StegoEncoder` | LSB bit manipulation and message embedding into pixel data |
| `StegoDecoder` | Sequential LSB reading and hidden text reconstruction |
| `DragDropHandler` | Custom `TransferHandler` subclass; validates and accepts file drops |
| `CapacityCalculator` | Computes and displays max text length for the loaded image |
| `ImagePreviewPanel` | Scales images with `Graphics2D` while maintaining aspect ratio |

---

## Core Algorithm

The LSB embedding logic in pseudocode:

```java
for each bit in (message + "###END###"):
    pixel = image.getRGB(x, y)
    modified_pixel = (pixel & 0xFFFFFFFE) | bit
    image.setRGB(x, y, modified_pixel)
```

Only the last bit of each pixel's color value is modified. The visual difference is imperceptible to the human eye.

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java (JDK 11+) |
| GUI Framework | Java Swing (`javax.swing`) |
| Image I/O | `javax.imageio.ImageIO` |
| IDE | Visual Studio Code |
| Output Format | PNG (lossless) |
| Version Control | Git / GitHub |

### Why PNG?

PNG uses lossless compression, meaning embedded LSB data is preserved exactly on save. JPEG uses lossy compression that destroys hidden bits during re-encoding, making it unsuitable for steganography. JPEG inputs are accepted but are automatically converted to PNG on save.

---

## Results

Tested on PNG images ranging from **500×500** to **3000×2000** pixels:

- ✅ Messages up to `(width × height / 8)` characters embedded and recovered without data loss
- ✅ Output images visually identical to originals in all test cases
- ✅ Hidden messages accurately recovered via the Reveal tab
- ✅ Edge cases (oversized messages, unsupported formats) handled with appropriate error dialogs

---

## Limitations

- Supports **text-only** secret data; binary file embedding is not yet implemented.
- No encryption is applied before embedding — anyone with a steganography tool could potentially extract the hidden message.
- Very large images may cause slight performance delays on older hardware.
- JPEG input is supported but output is always PNG to preserve embedded bits.

---

## Future Work

- [ ] Add **AES encryption** to secure messages before embedding
- [ ] Support embedding **binary files** (images, PDFs) in addition to plain text
- [ ] Implement **password protection** to restrict message extraction to authorized users
- [ ] Explore **2-bit or 3-bit LSB** per channel for higher capacity with an optional quality trade-off setting

---

## Academic Context

Developed as a course project for **CSE 2110 — Object-Oriented Programming II Lab**
Department of Computer Science and Engineering
Northern University of Business and Technology Khulna

**Author:** Md. Salauddin (ID: 11240321728)
**Submitted to:** Shovon Mandal, Lecturer, Dept. of CSE
**Date:** 31 March 2026

---

## References

- Google. (2024). *Gemini* [Large language model]. Google DeepMind. https://gemini.google.com
- Moonshot AI. (2024). *Kimi* [Large language model]. Moonshot AI. https://kimi.moonshot.cn

---

## License

This project is intended for academic and educational use.
