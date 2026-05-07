# 🖼️ FastThumb - High-Performance Shell Image Engine (v0.1.0)

FastThumb is a lightning-fast native Windows JNI module designed for high-performance extraction of **system thumbnails and icons**. Leveraging the modern `IShellItemImageFactory` COM API, it provides pixel-perfect visual previews for files, folders, videos, and documents directly into Java `BufferedImage` objects.

---

## ⚡ Core Features

- **High-Speed Extraction**: Direct native C++ implementation for minimum latency.
- **Unified API**: Extract both thumbnails (previews) and high-resolution system icons (up to 256x256+).
- **Format Support**: Automatically handles images, videos (frames), folders, and document previews.
- **Memory Efficient**: Optimized ARGB pixel transfer via JNI `int[]` arrays.
- **FastCore Integrated**: Built-in native library management via the FastJava ecosystem.

---

## 🚀 Quick Start

### Basic Thumbnail Extraction
```java
import fastthumb.FastThumb;
import java.awt.image.BufferedImage;

// Extract a 256x256 thumbnail
BufferedImage thumb = FastThumb.extract("C:\\Videos\\demo.mp4", 256);
```

### High-Quality Icon Extraction
```java
// Extract the system icon (no preview)
BufferedImage icon = FastThumb.extractIcon("C:\\Windows\\explorer.exe", 128);
```

---

## 🛠️ Architecture Blueprint

### Java Layer (`fastthumb.FastThumb`)
The Java layer provides a clean, high-level API. It automatically handles the conversion from native raw ARGB pixels into standard `java.awt.image.BufferedImage` objects, making it compatible with any Swing, JavaFX, or AWT application.

### Native Layer (`FastThumb.cpp`)
- **API**: Windows Shell COM (`IShellItemImageFactory`).
- **Pipeline**: `SHCreateItemFromParsingName` -> `GetImage` -> `GetDIBits` -> JNI Transfer.
- **Threading**: COM initialized in `ApartmentThreaded` mode for Shell compatibility.

---

## 📦 Build & Requirements

- **OS**: Windows 10/11 (x64)
- **JDK**: Java 8 or higher
- **Build**: Maven (Java) + MSVC (Native)

```bash
# Build Java module
mvn install

# Build Native DLL
compile.bat
```

---

## 🗺️ Ecosystem Integration
FastThumb is part of the **FastJava** monorepo:
- **FastCore**: Required for native loading.
- **FastFileSearch**: Use FastThumb to show previews in search results.
- **FastTheme**: Styled UI for displaying visual assets.

---
*Created by [Antigravity](https://github.com/andrestubbe/FastUI)*
