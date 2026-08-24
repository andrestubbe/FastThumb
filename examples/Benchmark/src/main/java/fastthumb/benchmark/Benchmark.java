package fastthumb.benchmark;

import fastthumb.ThumbCodec;
import fastthumb.ThumbRecord;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class Benchmark {

    private List<ThumbRecord> sampleRecords;
    private byte[] sampleBinary;

    @Setup
    public void setup() {
        sampleRecords = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            sampleRecords.add(new ThumbRecord(
                    "C:\\Windows\\System32\\driver-" + i + ".dll",
                    256,
                    256,
                    "ARGB",
                    i % 2 == 0
            ));
        }
        sampleBinary = ThumbCodec.encode(sampleRecords);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkEncode100ThumbRecords() {
        return ThumbCodec.encode(sampleRecords);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public List<ThumbRecord> benchmarkDecode100ThumbRecords() {
        return ThumbCodec.decode(sampleBinary);
    }
}
