package sk.zaymus.sub.opensubtitles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration stores data needed for initialization of {@link OSXmlRpcClient}
 * and settings values defined by user.
 *
 * @author Mikulas Zaymus
 */
public class OSConfig {

    /**
     * Label for all languages.
     */
    public static final String ALL_LANGUAGES = "all";

    /**
     * Delimiter for ISO639-2 language codes from
     * {@link OSConfig#getSubLanguageIDs()}
     */
    public static final String LANGUAGES_SEPARATOR = ",";

    /**
     * URL link to OpenSubtitles.org XML-RPC server.
     */
    public static final String URL = "http://api.opensubtitles.org:80/xml-rpc";

    /**
     * ISO639-1 code of language used for communication with OpenSubtitles.org
     * server. Value can be omitted.
     */
    public static final String LANGUAGE = "";

    /**
     * Application should have it's own UserAgent. Test UserAgent can be changed
     * in future.
     *
     * @see
     * <a href="http://trac.opensubtitles.org/projects/opensubtitles/wiki/DevReadFirst#Howtorequestanewuseragent">OpenSubtitles.org</a>
     */
    public static final String USER_AGENT = "OSTestUserAgent";
    private static final Logger log = LoggerFactory.getLogger(OSConfig.class);
    private static final String CONFIG_FILE = "app.properties";
    private static OSConfig config;
    private String userName;
    private String password;
    private String subLanguageIDs;

    private OSConfig() {
    }

    /**
     *
     * @return OpenSubtitles.org configuration
     */
    public static synchronized OSConfig getConfig() {
        if (config == null) {
            config = new OSConfig();
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(new File(CONFIG_FILE))) {
                p.load(fis);
            } catch (IOException ex) {
                log.error("Reading Properties", ex);
            }
            config.setUserName(p.getProperty("username", ""));
            String pass = p.getProperty("password", "");
            if (pass.length() > 0) {
                pass = new String(Base64.getDecoder().decode(pass));
            }
            config.setPassword(pass);
            config.setSubLanguageIDs(p.getProperty("sublanguageids", ALL_LANGUAGES));
        }
        return config;
    }

    /**
     * Persists user settings for latter application runs.
     */
    public static void saveChanges() {
        if (config != null) {
            Properties p = new Properties();
            p.setProperty("username", config.userName);
            String pass = config.password;
            if (pass.length() > 0) {
                pass = Base64.getEncoder().encodeToString(pass.getBytes());
            }
            p.setProperty("password", pass);
            p.setProperty("sublanguageids", config.subLanguageIDs);
            try (FileOutputStream fos = new FileOutputStream(new File(CONFIG_FILE))) {
                p.store(fos, null);
            } catch (IOException ex) {
                log.error("Saving Properties", ex);
            }
        }
    }

    /**
     *
     * @return user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return user password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password user password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @see OSConfig#LANGUAGES_SEPARATOR
     * @see OSConfig#ALL_LANGUAGES
     *
     * @return sequence of ISO639-2 language codes separated by comma
     */
    public String getSubLanguageIDs() {
        return subLanguageIDs;
    }

    /**
     * @see OSConfig#LANGUAGES_SEPARATOR
     * @see OSConfig#ALL_LANGUAGES
     *
     * @param subLanguageIDs sequence of ISO639-2 language codes separated by
     * comma
     */
    public void setSubLanguageIDs(String subLanguageIDs) {
        this.subLanguageIDs = subLanguageIDs;
    }

}
