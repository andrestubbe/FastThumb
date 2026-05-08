package fastthumb;

import fastimage.FastImage;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FastThumb - High-performance Windows Shell Thumbnail Extraction.
 * Uses IShellItem + IShellItemImageFactory for OS-native previews.
 */
public final class FastThumb {
    
    // Dedicated STA thread for COM operations (IShellItemImageFactory requires STA)
    private static final ExecutorService STA_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "FastThumb-STA");
        t.setDaemon(true);
        return t;
    });

    static {
        try {
            fastcore.FastCore.loadLibrary("fastthumb");
        } catch (Throwable e) {
            System.err.println("[FastThumb] CRITICAL: Failed to load native DLL: " + e.getMessage());
        }
    }

    /**
     * Extracts a thumbnail for a single file or folder.
     */
    public static FastImage get(Path path, int size) {
        return get(path.toAbsolutePath().toString(), size, false);
    }

    /**
     * Extracts a folder preview (composite thumbnail showing contents).
     */
    public static FastImage getFolder(Path folder, int size) {
        return get(folder.toAbsolutePath().toString(), size, false);
    }

    /**
     * Checks if FastThumb is supported on the current platform (Windows only).
     */
    public static boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Legacy support: Extracts a thumbnail/icon as a FastImage.
     */
    public static FastImage extract(String path, int size) {
        return get(path, size, false);
    }

    private static FastImage get(String path, int size, boolean iconOnly) {
        try {
            return STA_EXECUTOR.submit(() -> {
                System.out.println("[FastThumb] Requesting thumbnail for: " + path + " (size: " + size + ")");
                long handle = getNative(path, size, iconOnly);
                
                if (handle == 0) {
                    System.err.println("[FastThumb] Native call returned NULL handle for: " + path);
                    return null;
                }
                
                System.out.println("[FastThumb] Successfully created native handle: 0x" + Long.toHexString(handle));
                return FastImage.fromNativeHandle(handle, size, size);
            }).get();
        } catch (Exception e) {
            System.err.println("[FastThumb] Java Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static native long getNative(String path, int size, boolean iconOnly);
}
