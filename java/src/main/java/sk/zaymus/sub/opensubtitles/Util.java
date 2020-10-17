package sk.zaymus.sub.opensubtitles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import sk.zaymus.sub.pojo.Movie;

/**
 * Computes movie hash.
 * <br>Methods downloaded from OpenSubtitles.org:
 * <br>- {@link #computeHash(java.io.File)}
 * <br>- {@link #computeHashForChunk(java.nio.ByteBuffer)}
 *
 * @see
 * <a href="https://trac.opensubtitles.org/projects%3Cscript%20type=/opensubtitles/wiki/HashSourceCodes#Java">Official
 * Source Code</a>
 *
 * @author OpenSubtitles.org and Mikulas Zaymus
 */
public class Util {

    private static final int HASH_CHUNK_SIZE = 64 * 1024;

    /**
     * Method creates {@link sk.zaymus.sub.pojo.Movie} with predefined fields:
     * <br>- movie hash,
     * <br>- movie file size in bytes
     *
     * @param filePath path to movie file
     * @return Movie with predefined fields: movie hash, movie size in bytes
     * @throws IOException exception while accessing file
     */
    public static Movie getMovie(String filePath) throws IOException {
        Movie m = new Movie();
        File file = new File(filePath);
        m.setMoviehash(computeHash(file));
        m.setMoviebytesize(Math.toIntExact(file.length()));
        return m;
    }

    /**
     * Movie hash is calculated from movie file. Hash function is defined as:
     * movie size in bytes + 64bit checksum of the first and last 64 kb of movie
     * file
     *
     * @see
     * <a href="https://trac.opensubtitles.org/projects%3Cscript%20type=/opensubtitles/wiki/HashSourceCodes">OpenSubtitles.org</a>
     *
     * @param file movie file
     * @return movie hash calculated from file
     * @throws IOException exception while accessing file
     */
    public static String computeHash(File file) throws IOException {
        long size = file.length(), head, tail;
        long chunkSizeForFile = Math.min(HASH_CHUNK_SIZE, size);
        try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
            head = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, chunkSizeForFile));
            tail = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, Math.max(size - HASH_CHUNK_SIZE, 0), chunkSizeForFile));
        }
        return String.format("%016x", size + head + tail);
    }

    private static long computeHashForChunk(ByteBuffer buffer) {
        LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
        long hash = 0;
        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get();
        }
        return hash;
    }

}
