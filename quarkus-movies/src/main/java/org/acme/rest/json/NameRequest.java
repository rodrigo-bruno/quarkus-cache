package org.acme.rest.json;

public class NameRequest {

    public String name;
    public String description;

    public NameRequest() {
    }

    public NameRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
