package fastthumb;

import fastimage.FastImage;
import java.nio.file.Path;
import java.util.concurrent.*;

public final class FastThumb {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "FastThumb-STA");
        t.setDaemon(true);
        return t;
    });

    static {
        try {
            fastcore.FastCore.loadLibrary("fastthumb");
        } catch (Throwable e) {
        }
    }

    private static native long getNative(String path, int size, boolean iconOnly);

    public static FastImage get(Path path, int size) {
        return get(path.toAbsolutePath().toString(), size, false);
    }

    public static FastImage getIcon(Path path, int size) {
        return get(path.toAbsolutePath().toString(), size, true);
    }

    public static FastImage getFolder(Path folder, int size) {
        return get(folder.toAbsolutePath().toString(), size, false);
    }

    public static FastImage extract(String path, int size) {
        return get(path, size, false);
    }

    private static FastImage get(String path, int size, boolean iconOnly) {
        Future<Long> future = executor.submit(() -> getNative(path, size, iconOnly));
        try {
            long handle = future.get(5, TimeUnit.SECONDS);
            if (handle != 0) {
                return FastImage.fromNativeHandle(handle, size, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
