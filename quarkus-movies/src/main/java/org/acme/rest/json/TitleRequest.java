package org.acme.rest.json;

public class TitleRequest {

    public String name;
    public String description;

    public TitleRequest() {
    }

    public TitleRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
