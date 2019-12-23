package org.acme.rest.json;

public class NameRequest {

    public String nconst;
    public String description;

    public NameRequest() {
    }

    public NameRequest(String nconst, String description) {
        this.nconst = nconst;
        this.description = description;
    }
}
