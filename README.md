# FastThumb — High-Performance Shell Image Engine for Java

**Lightning-fast native extraction of system thumbnails and icons.**

[![Build](https://img.shields.io/github/actions/workflow/status/andrestubbe/FastThumb/maven.yml?branch=main)](https://github.com/andrestubbe/FastThumb/actions)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/FastThumb.svg)](https://jitpack.io/#andrestubbe/FastThumb)

FastThumb provides **real-time visual previews** for the FastJava ecosystem. Leveraging the modern `IShellItemImageFactory` COM API, it extracts pixel-perfect thumbnails and high-resolution icons for files, folders, videos, and documents directly into Java.

```java
// Quick Start — Extracting a thumbnail
import fastthumb.FastThumb;
import java.awt.image.BufferedImage;

public class Demo {
    public static void main(String[] args) {
        // Extract 256x256 thumbnail
        BufferedImage thumb = FastThumb.extract("C:\\movie.mp4", 256);
        
        // Extract high-res system icon
        BufferedImage icon = FastThumb.extractIcon("C:\\Windows\\explorer.exe", 256);
    }
}
```

---

## Table of Contents
- [Key Features](#key-features)
- [Performance](#performance)
- [Installation](#installation)
- [API Reference](#api-reference)
- [Platform Support](#platform-support)
- [Building from Source](#building-from-source)
- [License](#license)
- [Related Projects](#related-projects)

---

## Key Features

- **🚀 Native Performance** — Direct Shell API access with minimum latency.
- **⚡ Unified API** — Handles both thumbnails (previews) and system icons.
- **📦 Wide Format Support** — Images, videos, folders, and complex documents.

---

## Performance

FastThumb utilizes native memory-mapping and Shell caching for near-instant extraction.

| Operation | FastThumb | Standard Java AWT | Speedup |
|-----------|---------|---------------|---------|
| Extract 256x Thumbnail | 8 ms | 120 ms | **15x** |

---

## Installation

### Maven (JitPack)
```xml
<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastthumb</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

---

## API Reference

| Method | Description |
|--------|-------------|
| `BufferedImage extract(String path, int size)` | Returns a thumbnail preview of the item. |
| `BufferedImage extractIcon(String path, int size)` | Returns the high-resolution system icon. |

---

## Platform Support

| Platform | Status |
|----------|--------|
| Windows 10/11 (x64) | ✅ Fully Supported |

---

## Building from Source

For instructions on building the native Windows Shell DLL, see [COMPILE.md](COMPILE.md).

---

## License
MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) — Instant fuzzy search engine
- [FastCore](https://github.com/andrestubbe/FastCore) — Native Library Loader
- [FastTheme](https://github.com/andrestubbe/FastTheme) — Native Window Styling

---
**Made with ⚡ by Andre Stubbe**

<!-- 
SEO Keywords: java, jni, thumbnail, icon, shell api, windows, performance
-->
