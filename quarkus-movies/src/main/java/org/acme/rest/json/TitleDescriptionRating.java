package org.acme.rest.json;

public class TitleDescriptionRating extends EntryDescription  {
    public String rating;

    public TitleDescriptionRating() {
    }

    public TitleDescriptionRating(String name, String rating, String description) {
    	super(name, description);
        this.rating = rating;
    }
}
