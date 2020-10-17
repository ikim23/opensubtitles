package sk.ikim23.opensubtitles.client

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class OSUtils {
    static final int HASH_CHUNK_SIZE = 64 * 1024

    static String computeHash(File file) throws IOException {
        long size = file.length()
        long chunkSizeForFile = Math.min(HASH_CHUNK_SIZE, size)
        def fileChannel = new FileInputStream(file).channel
        try {
            long head = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, chunkSizeForFile))
            long tail = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, Math.max(size - HASH_CHUNK_SIZE, 0), chunkSizeForFile))
            return String.format("%016x", size + head + tail)
        } finally {
            fileChannel.close()
        }
    }

    static long computeHashForChunk(ByteBuffer buffer) {
        def longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer()
        long hash = 0
        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get()
        }
        return hash
    }
}
