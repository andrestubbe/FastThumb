# FastThumbnail (v0.1.0-Blueprint)

High-performance native Windows 11 thumbnail extraction for Java. This module enables lightning-fast, OS-native folder previews and high-DPI assets using zero-copy JNI integration.

## Architectural Overview

✅ **1. Native Windows 11 Thumbnails via JNI**
Windows provides advanced thumbnail generation through Shell COM APIs rather than legacy Win32 functions. The implementation targets the following interfaces:
- `IShellItem`
- `IShellItemImageFactory` (Primary interface for high-quality scaling)
- `IThumbnailCache` (Optional: Integration with system-wide caching)
- `IExtractImage` (Legacy fallback for older Windows versions)

**Implementation Strategy:**
By utilizing `SHCreateItemFromParsingName()`, an `IShellItem` is instantiated, followed by:

```cpp
IShellItemImageFactory* factory;
item->QueryInterface(IID_PPV_ARGS(&factory));
factory->GetImage(size, SIIGBF_RESIZETOFIT, &hBitmap);
```

This approach ensures parity with Windows Explorer, including:
- **Dynamic Folder Previews** (Win11 style with content snippets)
- **Multimedia Thumbnails** (Video, Images, Audio covers)
- **Document Previews** (PDF, Office, Text)
- **High-DPI Support** (Native scaling for 256px, 512px, and beyond)

✅ **2. Directory Icons vs. Thumbnails**
While `SHGetFileInfoW` or `SHGetStockIconInfo` provide standard folder icons, FastThumbnail prioritizes `IShellItemImageFactory` to deliver the rich, content-aware previews used in modern Windows environments.

✅ **3. Windows 11 Folder Preview Logic**
FastThumbnail leverages the dynamic Win11 preview generation logic. The Shell API automatically:
1. Samples the primary files within a directory.
2. Generates micro-thumbnails for those items.
3. Composites them into the final folder preview asset.

✅ **4. JNI Integration Pipeline**
The native bridge is designed to be lightweight and efficient:
- Initialization via `CoInitializeEx`.
- Object creation via `SHCreateItemFromParsingName`.
- Image extraction via `IShellItemImageFactory::GetImage`.
- Pixel data transfer to Java via `jbyteArray` or `DirectByteBuffer` using GDI+ or `CreateDIBSection`.

✅ **5. Evaluation of Java-Side Alternatives**
Traditional approaches like `sun.awt.shell.ShellFolder` are deemed unsuitable for the FastJava ecosystem due to:
- Performance bottlenecks.
- Resolution limits (often capped at 32x32 or 48x48).
- Lack of support for native Win11 folder previews.

## Integration with FastJava Ecosystem

FastThumbnail completes the high-performance visualization pipeline by bridging existing modules:
- **FastFileIndex**: Provides the source file list.
- **FastIO**: Delivers high-speed metadata for intelligent lazy loading.
- **FastImage**: Handles the processed bitmap assets for UI rendering.

**Key Features:**
- **Asynchronous Loading**: Native thumbnail extraction without blocking the UI thread.
- **Native Caching**: Leveraging the Windows system cache for near-instant retrieval.
- **60 FPS Performance**: Optimized for smooth scrolling in high-density file lists.
