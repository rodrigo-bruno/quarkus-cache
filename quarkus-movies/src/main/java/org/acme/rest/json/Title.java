package org.acme.rest.json;

import java.util.List;

public class Title {

    public final String tconst;
    public final String titleType;
    public final String primaryTitle;
    public final String originalTitle;
    public final String isAdult;
    public final String startYear;
    public final String endYear;
    public final String runtimeMinutes;
    public final List<String> genres;

    public Title(String tconst, String titleType, String primaryTitle, String originalTitle, String isAdult, String startYear, String endYear, String runtimeMinutes, List<String> genres) {
        this.tconst = tconst;
        this.titleType = titleType;
        this.primaryTitle = primaryTitle;
        this.originalTitle = originalTitle;
        this.isAdult = isAdult;
        this.startYear = startYear;
        this.endYear = endYear;
        this.runtimeMinutes = runtimeMinutes;
        this.genres = genres;
    }
}