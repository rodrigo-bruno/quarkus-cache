package org.acme.rest.json;

public class RatingRequest {

    public String name;
    public String description;

    public RatingRequest() {
    }

    public RatingRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
	
}
