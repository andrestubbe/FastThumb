# Changelog: FastThumb

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-08-24
### Added
- **Native Windows Shell Thumbnail Extractor (`FastThumb`)**: Direct `IShellItemImageFactory` extraction across all Windows file types.
- **FastImage Zero-Copy Output**: Instant ARGB pixel buffers directly from native COM bitmap handles.
- **FastFileFormat Metadata Streamer (`ThumbCodec`)**: Compact `.thumbbin` binary cache format (Payload ID `0x0008`).
- **JMH Microbenchmark Suite**: Profiling >85M metadata decodes/sec and >25M encodes/sec.
- **Two-Stage OS UI Pipeline Integration**: First-stage instant Shell cache feeder for `FastPreview`.