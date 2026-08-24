package fastthumb;

import fastfileformat.BinaryHeader;
import fastfileformat.BinaryReader;
import fastfileformat.BinaryWriter;
import fastfileformat.FastFileFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * High-speed binary serializer and stream decoder for cached thumbnail metadata logs (.thumbbin).
 * Built on top of FastFileFormat and FastBinary VarInt compression.
 */
public final class ThumbCodec {
    /**
     * Payload type identifier for FastJava Thumbnail Metadata Logs (0x0008).
     */
    public static final short PAYLOAD_TYPE_THUMBBIN = 0x0008;

    private ThumbCodec() {}

    /**
     * Encodes a list of thumbnail records into a compressed FastFileFormat binary byte array.
     */
    public static byte[] encode(List<ThumbRecord> records) {
        if (records == null || records.isEmpty()) {
            BinaryWriter finalWriter = FastFileFormat.binaryWriter(12);
            finalWriter.writeHeader(FastFileFormat.DEFAULT_MAGIC, FastFileFormat.DEFAULT_VERSION, PAYLOAD_TYPE_THUMBBIN, 0);
            return finalWriter.toByteArray();
        }

        BinaryWriter payloadWriter = FastFileFormat.binaryWriter(records.size() * 32);
        payloadWriter.writeVarInt(records.size());

        for (ThumbRecord r : records) {
            payloadWriter.writeString(r.path() != null ? r.path() : "");
            payloadWriter.writeVarInt(r.width());
            payloadWriter.writeVarInt(r.height());
            payloadWriter.writeString(r.format() != null ? r.format() : "");
            payloadWriter.writeByte((byte) (r.iconOnly() ? 1 : 0));
        }

        byte[] payload = payloadWriter.toByteArray();

        BinaryWriter finalWriter = FastFileFormat.binaryWriter(12 + payload.length);
        finalWriter.writeHeader(
                FastFileFormat.DEFAULT_MAGIC,
                FastFileFormat.DEFAULT_VERSION,
                PAYLOAD_TYPE_THUMBBIN,
                payload.length
        );
        finalWriter.writeBytes(payload);
        return finalWriter.toByteArray();
    }

    /**
     * Decodes a .thumbbin binary payload into a list of ThumbRecord instances.
     */
    public static List<ThumbRecord> decode(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return Collections.emptyList();
        }

        BinaryReader reader = FastFileFormat.binaryReader(bytes);
        BinaryHeader header = reader.readHeader();

        if (header.getMagic() != FastFileFormat.DEFAULT_MAGIC) {
            throw new IllegalArgumentException("Invalid FastFileFormat magic header: " + Integer.toHexString(header.getMagic()));
        }
        if (header.getPayloadType() != PAYLOAD_TYPE_THUMBBIN) {
            throw new IllegalArgumentException("Unexpected payload type for Thumbbin: " + header.getPayloadType());
        }
        if (header.getPayloadLength() == 0) {
            return Collections.emptyList();
        }

        int count = reader.readVarInt();
        List<ThumbRecord> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            String path = reader.readString();
            int w = reader.readVarInt();
            int h = reader.readVarInt();
            String format = reader.readString();
            boolean iconOnly = (reader.readByte() & 0xFF) == 1;

            list.add(new ThumbRecord(path, w, h, format, iconOnly));
        }
        return Collections.unmodifiableList(list);
    }

    public static void writeToFile(Path path, List<ThumbRecord> records) throws IOException {
        byte[] bytes = encode(records);
        Files.write(path, bytes);
    }

    public static List<ThumbRecord> readFromFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return decode(bytes);
    }
}
