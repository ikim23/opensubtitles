package sk.zaymus.sub.opensubtitles;

/**
 * Exception raised when client exceeds download limit.
 *
 *
 * @author Mikulas Zaymus
 */
public class DownloadLimitReachedException extends Exception {

    /**
     *
     * @param msg exception message
     */
    public DownloadLimitReachedException(String msg) {
        super(msg);
    }

}
