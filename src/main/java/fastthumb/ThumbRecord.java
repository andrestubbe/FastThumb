package fastthumb;

/**
 * Metadata record describing an extracted thumbnail.
 *
 * @param path Absolute file path of source artifact.
 * @param width Extracted thumbnail width in pixels.
 * @param height Extracted thumbnail height in pixels.
 * @param format Format identifier (e.g. ARGB, RGBA).
 * @param iconOnly True if extracted as standard shell icon, false if content preview.
 */
public record ThumbRecord(
        String path,
        int width,
        int height,
        String format,
        boolean iconOnly
) {}
