package sk.ikim23.subtitlesdownloader.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import groovy.transform.CompileStatic
import sk.ikim23.subtitlesdownloader.R

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.LongBuffer
import java.nio.channels.FileChannel

@CompileStatic
class U {

    interface OnPrefsChangeListener {
        void onPrefsChange()
    }

    static final String PREFERENCES = 'prefs'
    static final String PREF_LANGUAGE_IDS = 'pref_lang_ids'
    static final int HASH_CHUNK_SIZE = 64 * 1024
    static OnPrefsChangeListener listener

    static String computeHash(File file) throws IOException {
        long size = file.length()
        long chunkSizeForFile = Math.min(HASH_CHUNK_SIZE, size)
        FileChannel fileChannel = new FileInputStream(file).channel
        try {
            long head = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, chunkSizeForFile))
            long tail = computeHashForChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, Math.max(size - HASH_CHUNK_SIZE, 0), chunkSizeForFile))
            return String.format("%016x", size + head + tail)
        } finally {
            fileChannel.close()
        }
    }

    static long computeHashForChunk(ByteBuffer buffer) {
        LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer()
        long hash = 0
        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get()
        }
        return hash
    }

    static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return cm?.activeNetworkInfo?.isConnectedOrConnecting()
    }

    static String formatLongToTime(long ms) {
        long s = (ms / 1000).longValue() % 60
        long m = ((ms / 1000) / 60).longValue() % 60
        long h = ((ms / 1000) / (60 * 60)).longValue() % 24
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    /***************
     * PREFERENCES *
     ***************/
    static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    static String getLanguageIds(Context context) {
        return getPrefs(context).getString(PREF_LANGUAGE_IDS, context.getString(R.string.pref_language_id_default))
    }

    static Set<String> getLanguageIdsAsSet(Context context) {
        return getLanguageIds(context)?.split(',').toList().toSet()
    }

    static void setLanguageIds(Context context, String languageIds) {
        SharedPreferences.Editor editor = getPrefs(context).edit()
        editor.putString(PREF_LANGUAGE_IDS, languageIds)
        if (editor.commit()) {
            listener?.onPrefsChange()
        }
    }

}
