package sk.zaymus.sub.opensubtitles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import sk.zaymus.sub.pojo.Movie;
import sk.zaymus.sub.pojo.Subtitles;
import sk.zaymus.sub.pojo.Mapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import sk.zaymus.sub.pojo.Language;

/**
 * Client communicating with OpenSubtitles.org wia XML-RPC standard.
 * Implementation uses for communication library {@link org.apache.xmlrpc}.
 * {@link OSXmlRpcClient} is thread safe.
 *
 * @see <a href="https://tools.ietf.org/html/rfc3529/">XML-RPC RFC 3529</a>
 * @see org.apache.xmlrpc.client.XmlRpcClient
 * @see
 * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#XMLRPCmethods">OpenSubtitles.org
 * XML-RPC Methods</a>
 *
 * @author Mikulas Zaymus
 */
public class OSXmlRpcClient implements AutoCloseable {

    /**
     * Listener responding on all asynchronous method calls. Each asynchronous
     * method name starts with prefix "async".
     */
    public static interface AsyncListener {

        /**
         * Method is called after successful search request.
         *
         * @see
         * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#SearchSubtitles">SearchSubtitles</a>
         *
         * @param subs list of subtitles, max list size: 500
         */
        void onSearchResponse(List<Subtitles> subs);

        /**
         * Method is called after unsuccessful search request.
         *
         * @param ex client exception
         */
        void onSearchException(Exception ex);

        /**
         * Method is called after successful download request.
         *
         * @param dest destination file
         */
        void onDownloadResponse(File dest);

        /**
         * Method is called after unsuccessful download request.
         *
         * @param ex client exception or download limit reached
         */
        void onDownloadException(Exception ex);

        /**
         * Method is called after successful request.
         *
         * @see
         * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#GetSubLanguages">GetSubLanguages</a>
         *
         * @param langs list of supported languages
         */
        void onGetSubLanguagesResponse(List<Language> langs);

        /**
         * Method is called after unsuccessful request.
         *
         * @param ex client exception
         */
        void onGetSubLanguagesException(Exception ex);

    }

    private static interface AsyncTask {

        void execute();

    }

    private static class AsyncWorker implements Runnable {

        private final AtomicBoolean closed = new AtomicBoolean();
        private final Object lock = new Object();
        private final Queue<AsyncTask> queue = new LinkedList<>();

        @Override
        public void run() {
            AsyncTask task;
            while (!closed.get()) {
                synchronized (lock) {
                    task = queue.poll();
                }
                if (task == null) {
                    try {
                        synchronized (lock) {
                            lock.wait();
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    task.execute();
                }

            }
        }

        private void addTask(AsyncTask task) {
            synchronized (lock) {
                queue.add(task);
                lock.notifyAll();
            }
        }

        private void close() {
            if (!closed.get()) {
                closed.set(true);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }

    }

    /**
     * HTTP OK
     */
    public static final int OK = 200;

    /**
     * HTTP Error: Unauthorized
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * HTTP Error: Download limit reached
     */
    public static final int DOWNLOAD_LIMIT_REACHED = 407;

    /**
     * HTTP Error: Other or unknown error
     */
    public static final int UNKNOWN_ERROR = 410;

    private final XmlRpcClient client;
    private final OSConfig conf;
    private final AtomicReference<String> token = new AtomicReference<>();
    private AsyncListener listener;
    private AsyncWorker worker;

    /**
     * Initializes OpenSubtitles.org XML-RPC client. Client requires:
     * <br>- server URL
     * <br>- user name
     * <br>- password
     * <br>- language
     * <br>- user agent
     *
     * @param config configuration for OpenSubtitles.org XML-RPC client
     * @throws MalformedURLException wrong URL format at configuration
     */
    public OSXmlRpcClient(OSConfig config) throws MalformedURLException {
        XmlRpcClientConfigImpl c = new XmlRpcClientConfigImpl();
        c.setServerURL(new URL(OSConfig.URL));
        client = new XmlRpcClient();
        client.setConfig(c);
        conf = config;
    }

    /**
     * Listener process responses on asynchronous methods of
     * {@link OSXmlRpcClient}. Listener can be set only once and for all life
     * time of {@link OSXmlRpcClient}.
     *
     * @param listener listener handling asynchronous responses
     */
    public void setListener(AsyncListener listener) {
        if (this.listener == null) {
            this.listener = listener;
            worker = new AsyncWorker();
            new Thread(worker).start();
        }
    }

    /**
     * Searches for available subtitles by specified criteria. Max result size
     * is 500 subtitles. Searching by movie hash is superior over search by
     * movie name.
     *
     * @see
     * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#SearchSubtitles">SearchSubtitles</a>
     *
     * @param movie movie information
     * @return list of subtitles, max list size: 500
     * @throws XmlRpcException client exception
     */
    public List<Subtitles> searchSubtitles(Movie movie) throws XmlRpcException {
        if (token.get() == null) {
            logIn();
        }
        try {
            Map map = (Map) client.execute("SearchSubtitles", new Object[]{token.get(), new Object[]{Mapper.toMap(movie)}});
            int status = statusCode(map);
            if (status == UNAUTHORIZED) { // token expired
                logIn();
                map = (Map) client.execute("SearchSubtitles", new Object[]{token.get(), new Object[]{Mapper.toMap(movie)}});
                status = statusCode(map);
            }
            if (status != OK) {
                throw new XmlRpcException("Unknown status code: " + status);
            }
            List<Subtitles> subs = new ArrayList<>();
            Object[] objs = (Object[]) map.get("data");
            for (Object o : objs) {
                subs.add(Mapper.fromMap((Map) o, Subtitles.class));
            }
            return subs;
        } catch (IOException ex) {
            throw new XmlRpcException(ex.getMessage());
        }
    }

    /**
     * Searches for available subtitles by specified criteria. Max result size
     * is 500 subtitles. Searching by movie hash is superior over search by
     * movie name.
     *
     * @see
     * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#SearchSubtitles">SearchSubtitles</a>
     *
     * @param movie movie information
     */
    public void asyncSearchSubtitles(Movie movie) {
        if (worker != null) {
            worker.addTask(() -> {
                try {
                    listener.onSearchResponse(searchSubtitles(movie));
                } catch (XmlRpcException ex) {
                    listener.onSearchException(ex);
                }
            });
        }
    }

    /**
     * Downloads and uncompress Gzipped subtitles. If user doesn't have premium
     * account, only 200 subtitles can by downloaded per day.
     *
     * @param subDownloadLink URL link to subtitles
     * @param dest destination file
     * @throws IOException exception while downloading
     * @throws DownloadLimitReachedException download limit reached
     */
    public void downloadSubtitles(String subDownloadLink, File dest) throws IOException, DownloadLimitReachedException {
        HttpURLConnection conn = (HttpURLConnection) new URL(subDownloadLink).openConnection();
        conn.connect();
        int status = conn.getResponseCode();
        if (status == OK) {
            try (BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()), 8192);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest), 8192)) {
                byte[] buffer = new byte[8192];
                int readed;
                while ((readed = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, readed);
                }
            }
            conn.disconnect();
        } else if (status == DOWNLOAD_LIMIT_REACHED || status == UNKNOWN_ERROR) {
            throw new DownloadLimitReachedException("Daily download limit was exceeded.");
        } else {
            throw new IOException("Another status code: " + status);
        }
    }

    /**
     * Downloads and uncompress Gzipped subtitles. If user doesn't have premium
     * account, only 200 subtitles can by downloaded per day.
     *
     * @param subDownloadLink URL link to subtitles
     * @param dest destination file
     */
    public void asyncDownloadSubtitles(String subDownloadLink, File dest) {
        if (worker != null) {
            worker.addTask(() -> {
                try {
                    downloadSubtitles(subDownloadLink, dest);
                    listener.onDownloadResponse(dest);
                } catch (IOException | DownloadLimitReachedException ex) {
                    listener.onDownloadException(ex);
                }
            });
        }
    }

    /**
     * Get list of all supported languages.
     *
     * @see
     * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#GetSubLanguages">GetSubLanguages</a>
     *
     * @return list of supported languages
     * @throws XmlRpcException client exception
     */
    public List<Language> getSubLanguages() throws XmlRpcException {
        try {
            Map map = (Map) client.execute("GetSubLanguages", new Object[]{});
            List<Language> langs = new LinkedList<>();
            Object[] objs = (Object[]) map.get("data");
            for (Object o : objs) {
                langs.add(Mapper.fromMap((Map) o, Language.class));
            }
            return langs;
        } catch (IOException ex) {
            throw new XmlRpcException(ex.getMessage());
        }
    }

    /**
     * Get list of all supported languages.
     *
     * @see
     * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#GetSubLanguages">GetSubLanguages</a>
     */
    public void asyncGetSubLanguages() {
        if (worker != null) {
            worker.addTask(() -> {
                try {
                    listener.onGetSubLanguagesResponse(getSubLanguages());
                } catch (XmlRpcException ex) {
                    listener.onGetSubLanguagesException(ex);
                }
            });
        }
    }

    /**
     * Notifies server about new client. Receives unique token that is used for
     * next communication.
     *
     * @see
     * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#LogIn">LogIn</a>
     *
     * @throws XmlRpcException client exception
     */
    public void logIn() throws XmlRpcException {
        Map map = (Map) client.execute("LogIn", new Object[]{conf.getUserName(), conf.getPassword(), OSConfig.LANGUAGE, OSConfig.USER_AGENT});
        token.set((String) map.get("token"));
    }

    /**
     * Notifies server about closing. Releases communication token.
     *
     * @see
     * <a href="https://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC#LogOut">LogOut</a>
     *
     * @throws Exception client exception
     */
    @Override
    public void close() throws Exception {
        if (token.get() != null) {
            client.execute("LogOut", new Object[]{token.get()});
        }
        if (worker != null) {
            worker.close();
        }
    }

    private int statusCode(Map map) {
        return Integer.parseInt(((String) map.get("status")).substring(0, 3));
    }

}
