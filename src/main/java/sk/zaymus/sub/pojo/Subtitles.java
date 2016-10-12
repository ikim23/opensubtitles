package sk.zaymus.sub.pojo;

import java.util.Date;

/**
 * Information about found subtitles from OpenSubtitles.org
 *
 * @see <a href="http://www.opensubtitles.org/">OpenSubtitles.org</a>
 *
 * @author Mikulas Zaymus
 */
public class Subtitles {

    private String ISO639;
    private Integer SubDownloadsCnt;
    private String SubDownloadLink;
    private Date SubAddDate;
    private Double SubRating;
    private String MovieReleaseName;
    private String SubLanguageID;
    private String SubFormat;
    private String SubFileName;

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
     * @return number of downloads
     */
    public Integer getSubDownloadsCnt() {
        return SubDownloadsCnt;
    }

    /**
     *
     * @param SubDownloadsCnt number of downloads
     */
    public void setSubDownloadsCnt(Integer SubDownloadsCnt) {
        this.SubDownloadsCnt = SubDownloadsCnt;
    }

    /**
     *
     * @return subtitles download link
     */
    public String getSubDownloadLink() {
        return SubDownloadLink;
    }

    /**
     *
     * @param SubDownloadLink subtitles download link
     */
    public void setSubDownloadLink(String SubDownloadLink) {
        this.SubDownloadLink = SubDownloadLink;
    }

    /**
     *
     * @return date of subtitles creation
     */
    public Date getSubAddDate() {
        return SubAddDate;
    }

    /**
     *
     * @param SubAddDate date of subtitles creation
     */
    public void setSubAddDate(Date SubAddDate) {
        this.SubAddDate = SubAddDate;
    }

    /**
     * User rating of subtitles. Gains value from 0 to 10.
     *
     * @return subtitles rating
     */
    public Double getSubRating() {
        return SubRating;
    }

    /**
     *
     * @param SubRating subtitles rating, value from 0 to 10
     */
    public void setSubRating(Double SubRating) {
        this.SubRating = SubRating;
    }

    /**
     *
     * @return release name of movie
     */
    public String getMovieReleaseName() {
        return MovieReleaseName;
    }

    /**
     *
     * @param MovieReleaseName release name of movie
     */
    public void setMovieReleaseName(String MovieReleaseName) {
        this.MovieReleaseName = MovieReleaseName;
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
     * @return subtitles file format
     */
    public String getSubFormat() {
        return SubFormat;
    }

    /**
     *
     * @param SubFormat subtitles file format
     */
    public void setSubFormat(String SubFormat) {
        this.SubFormat = SubFormat;
    }

    /**
     *
     * @return subtitles file name
     */
    public String getSubFileName() {
        return SubFileName;
    }

    /**
     *
     * @param SubFileName subtitles file name
     */
    public void setSubFileName(String SubFileName) {
        this.SubFileName = SubFileName;
    }

}
