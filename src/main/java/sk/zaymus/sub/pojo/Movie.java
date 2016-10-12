package sk.zaymus.sub.pojo;

/**
 * Basic movie information.
 * 
 * @author Mikulas Zaymus
 */
public class Movie {

    private String sublanguageid;
    private String moviehash;
    private Integer moviebytesize;
    private String query;
    private Integer season;
    private Integer episode;

    /**
     *
     * @return ISO639-2 formatted language codes separated by comas
     */
    public String getSublanguageid() {
        return sublanguageid;
    }

    /**
     *
     * @param sublanguageid ISO639-2 formatted language codes separated by comas
     */
    public void setSublanguageid(String sublanguageid) {
        this.sublanguageid = sublanguageid;
    }

    /**
     * Movie hash is calculated from movie file. Hash function is defined as:
     * movie size in bytes + 64bit checksum of the first and last 64 kb of movie file
     * 
     * @return hash calculated from movie file
     */
    public String getMoviehash() {
        return moviehash;
    }

    /**
     * Movie hash is calculated from movie file. Hash function is defined as:
     * movie size in bytes + 64bit checksum of the first and last 64 kb of movie file
     * 
     * @param moviehash hash calculated from movie file
     */
    public void setMoviehash(String moviehash) {
        this.moviehash = moviehash;
    }

    /**
     *
     * @return movie size in bytes
     */
    public Integer getMoviebytesize() {
        return moviebytesize;
    }

    /**
     *
     * @param moviebytesize movie size in bytes
     */
    public void setMoviebytesize(Integer moviebytesize) {
        this.moviebytesize = moviebytesize;
    }

    /**
     *
     * @return movie name
     */
    public String getQuery() {
        return query;
    }

    /**
     *
     * @param query movie name
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     *
     * @return season number
     */
    public Integer getSeason() {
        return season;
    }

    /**
     *
     * @param season season number
     */
    public void setSeason(Integer season) {
        this.season = season;
    }

    /**
     *
     * @return episode number
     */
    public Integer getEpisode() {
        return episode;
    }

    /**
     *
     * @param episode episode number
     */
    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

}
