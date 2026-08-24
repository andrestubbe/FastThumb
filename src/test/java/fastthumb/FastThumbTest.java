package fastthumb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FastThumbTest {

    @Test
    public void testThumbCodecSerialization(@TempDir Path tempDir) throws IOException {
        List<ThumbRecord> records = List.of(
                new ThumbRecord("C:\\Windows\\explorer.exe", 256, 256, "ARGB", false),
                new ThumbRecord("C:\\Users\\user\\report.pdf", 64, 64, "ARGB", true)
        );

        byte[] encoded = ThumbCodec.encode(records);
        assertNotNull(encoded);
        assertTrue(encoded.length >= 12);

        List<ThumbRecord> decoded = ThumbCodec.decode(encoded);
        assertEquals(2, decoded.size());
        assertEquals("C:\\Windows\\explorer.exe", decoded.get(0).path());
        assertEquals(256, decoded.get(0).width());
        assertFalse(decoded.get(0).iconOnly());
        assertTrue(decoded.get(1).iconOnly());

        Path file = tempDir.resolve("thumbs.thumbbin");
        ThumbCodec.writeToFile(file, records);
        assertTrue(file.toFile().exists());

        List<ThumbRecord> fromDisk = ThumbCodec.readFromFile(file);
        assertEquals(2, fromDisk.size());
    }
}
