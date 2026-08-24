# FastThumb Reference Guide

## API Overview

`fastthumb.FastThumb` provides direct Java bindings to the native Windows Shell Image Factory (`IShellItemImageFactory`) and Windows Shell Thumbnail Cache.

### Core Methods

| Method | Return | Description |
| :--- | :--- | :--- |
| `FastThumb.get(Path path, int size)` | `FastImage` | Extracts a high-resolution thumbnail or icon for the specified file path. |
| `FastThumb.getIcon(Path path, int size)` | `FastImage` | Extracts only the filetype shell icon without reading internal file content. |
| `FastThumb.getFolder(Path folder, int size)` | `FastImage` | Extracts the OS-native folder thumbnail with content fan-out. |
| `FastThumb.extract(String path, int size)` | `FastImage` | Direct string overload for file paths. |

---

## Codec & Binary Serialization

`fastthumb.ThumbCodec` provides serialization of thumbnail metadata records into FastFileFormat `.thumbbin` binaries (Payload ID `0x0008`).

| Method | Return | Description |
| :--- | :--- | :--- |
| `ThumbCodec.encode(List<ThumbRecord> records)` | `byte[]` | Encodes records to compressed `.thumbbin` byte array. |
| `ThumbCodec.decode(byte[] bytes)` | `List<ThumbRecord>` | Deserializes `.thumbbin` payload back to `ThumbRecord` list. |
| `ThumbCodec.writeToFile(Path path, List<ThumbRecord> records)` | `void` | Writes binary metadata cache directly to disk. |
| `ThumbCodec.readFromFile(Path path)` | `List<ThumbRecord>` | Reads binary metadata cache from disk. |