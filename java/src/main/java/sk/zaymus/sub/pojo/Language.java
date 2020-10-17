package sk.zaymus.sub.pojo;

/**
 * Representation of language supported by OpenSubtitles.org
 *
 * @see <a href="http://www.opensubtitles.org/">OpenSubtitles.org</a>
 *
 * @author Mikulas Zaymus
 */
public class Language {

    private String ISO639;
    private String SubLanguageID;
    private String LanguageName;

    /**
     *
     * @return ISO639-1 2 characters code of language
     */
    public String getISO639() {
        return ISO639;
    }

    /**
     *
     * @param ISO639 ISO639-1 2 characters code of language
     */
    public void setISO639(String ISO639) {
        this.ISO639 = ISO639;
    }

    /**
     *
     * @return ISO639-2 3 characters code of language
     */
    public String getSubLanguageID() {
        return SubLanguageID;
    }

    /**
     *
     * @param SubLanguageID ISO639-2 3 characters code of language
     */
    public void setSubLanguageID(String SubLanguageID) {
        this.SubLanguageID = SubLanguageID;
    }

    /**
     *
     * @return full language name
     */
    public String getLanguageName() {
        return LanguageName;
    }

    /**
     *
     * @param LanguageName full language name
     */
    public void setLanguageName(String LanguageName) {
        this.LanguageName = LanguageName;
    }

}
