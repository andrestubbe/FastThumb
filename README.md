# FastThumb 0.1.1 [ALPHA] — Native Windows Shell Thumbnail & Icon Extraction Engine

[![Status](https://img.shields.io/badge/status-0.1.1-brightgreen.svg)](https://github.com/andrestubbe/FastThumb/releases/tag/0.1.1)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows--x64-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastThumb)

---

**⚡ High-speed native Windows 11 thumbnail extraction, shell icon retrieval, zero-copy ARGB buffers, and `.thumbbin` metadata caching for Java.**

**FastThumb** delivers Windows Explorer parity for Java applications. Utilizing the native Windows `IShellItemImageFactory` and Windows Shell Thumbnail Cache over dedicated Single-Threaded Apartment (STA) execution, it extracts full-fidelity previews for PDFs, Images, Videos, Office documents, and executables with zero Swing/AWT overhead.

---

## Quick Start

```java
import fastthumb.FastThumb;
import fastthumb.ThumbCodec;
import fastthumb.ThumbRecord;
import fastimage.FastImage;
import java.nio.file.Path;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // 1. Extract high-resolution shell thumbnail (e.g. 256x256)
        Path file = Path.of("C:\\Users\\user\\report.pdf");
        FastImage thumbnail = FastThumb.get(file, 256);

        if (thumbnail != null) {
            System.out.printf("Thumbnail extracted: %dx%d ARGB pixels\n",
                    thumbnail.getWidth(), thumbnail.getHeight());
        }

        // 2. Compact FastFileFormat Binary Metadata Cache (.thumbbin)
        ThumbRecord record = new ThumbRecord(file.toString(), 256, 256, "ARGB", false);
        byte[] binary = ThumbCodec.encode(List.of(record));
        List<ThumbRecord> restored = ThumbCodec.decode(binary);
    }
}
```

---

## Key Features

- **🖼️ Explorer-Parity Shell Extraction** — Direct access to `IShellItemImageFactory` for OS-native previews across all registered file extensions.
- **⚡ Zero-Copy FastImage Integration** — Direct native bitmap handles converted to `FastImage` ARGB buffers without intermediate serialization.
- **🧵 Dedicated STA Apartment Pool** — Safe and non-blocking COM thread apartment execution preventing UI lockups.
- **📦 FastFileFormat `.thumbbin` Cache** — Compact VarInt binary metadata format for local thumbnail index persistence (Payload ID `0x0008`).

---

## Real-World Scenarios

- **📁 High-Performance File Managers** — Instant smooth scrolling thumbnail grids for thousands of files.
- **📑 Document Archiving & Search** — High-speed visual preview caching for PDF, Word, and Excel repositories.
- **🎮 Media Asset Browsers** — Ultra-fast video and image thumbnail previews using the Windows Shell cache.

---

## Performance Benchmarks

FastThumb is profiled using **JMH** to guarantee zero bottleneck during heavy directory scanning.

| Benchmark Operation | Score (ops/ms) | Throughput | Memory Overhead |
|---|---|---|---|
| **Binary Metadata Cache Decoding (`.thumbbin`)** | **~85,000 ops/ms** | **> 85 Million records/sec** | **Zero-Copy Streaming** |
| **Binary Metadata Cache Encoding (`.thumbbin`)** | **~25,000 ops/ms** | **> 25 Million records/sec** | **Compact VarInt Delta Buffer** |

*Run the benchmarks locally:* `.\run-benchmark.bat`

---

## API Quick Reference

| Method / Class | Description |
|---|---|
| `FastThumb.get(path, size)` | Extracts native thumbnail or shell preview for file at specified resolution. |
| `FastThumb.getIcon(path, size)` | Extracts filetype icon instead of content thumbnail. |
| `FastThumb.getFolder(folder, size)` | Extracts native folder thumbnail preview with content fan-out. |
| `ThumbCodec.encode(records)` | Serializes thumbnail metadata records into compressed FastFileFormat bytes. |
| `ThumbCodec.decode(bytes)` | Deserializes `.thumbbin` bytes back into `List<ThumbRecord>`. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Live Shell Thumbnail Demo** | [Demo.java](examples/Demo/src/main/java/fastthumb/demo/Demo.java) | `run-demo.bat` | Native thumbnail extraction from OS files and `.thumbbin` metadata caching. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastthumb/benchmark/Benchmark.java) | `run-benchmark.bat` | High-throughput binary codec serialization and streaming benchmarks. |

---

## Installation

### Option 1: Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastThumb</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastImage</artifactId>
        <version>0.1.1</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastFileFormat</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastThumb:0.1.0'
    implementation 'com.github.andrestubbe:FastImage:0.1.1'
    implementation 'com.github.andrestubbe:FastFileFormat:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 🖼️ **[FastThumb-0.1.0.jar](https://github.com/andrestubbe/FastThumb/releases/download/0.1.0/FastThumb-0.1.0.jar)** (Native Shell Thumbnail Engine)
2. ⚡ **[FastImage-0.1.1.jar](https://github.com/andrestubbe/FastImage/releases/download/0.1.1/FastImage-0.1.1.jar)** (Zero-Copy Image Manipulation)
3. 📄 **[FastFileFormat-0.1.0.jar](https://github.com/andrestubbe/FastFileFormat/releases/download/0.1.0/FastFileFormat-0.1.0.jar)** (Dual Binary & Text File Format)
4. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (Foundation Library)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API reference and method signatures.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Architectural design principles and COM STA apartment model.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.
* **[COMPILE.md](docs/COMPILE.md)**: Instructions for compiling from source.

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastImage](https://github.com/andrestubbe/FastImage) — SIMD-accelerated image scaling and pixel buffers
- [FastPreview](https://github.com/andrestubbe/FastPreview) — High-performance document & PDF rendering
- [FastIO](https://github.com/andrestubbe/FastIO) — Native high-speed memory-mapped file access

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
