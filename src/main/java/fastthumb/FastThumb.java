package fastthumb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

/**
 * FastThumb - High-performance Windows Shell Thumbnail Extraction
 */
public class FastThumb {
    static {
        try {
            fastcore.FastCore.loadLibrary("fastthumb");
        } catch (Throwable e) {
            System.err.println("CRITICAL: FastThumb failed to load native DLL: " + e.getMessage());
        }
    }

    /**
     * Extracts a thumbnail or icon from the given file path.
     * 
     * @param path Full path to the file or directory
     * @param size Requested square size (e.g., 256)
     * @return BufferedImage containing the thumbnail/icon, or null if failed
     */
    public static BufferedImage extract(String path, int size) {
        return extract(path, size, false);
    }

    /**
     * Extracts ONLY the icon (no thumbnail/preview) from the given file path.
     */
    public static BufferedImage extractIcon(String path, int size) {
        return extract(path, size, true);
    }

    /**
     * Core extraction method with icon-only flag.
     */
    public static BufferedImage extract(String path, int size, boolean iconOnly) {
        int[] pixels = extractNative(path, size, iconOnly);
        if (pixels == null) return null;

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        int[] imgData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, imgData, 0, pixels.length);
        return img;
    }

    /**
     * Native call to Windows Shell API to get thumbnail/icon pixels.
     */
    private static native int[] extractNative(String path, int size, boolean iconOnly);
}
