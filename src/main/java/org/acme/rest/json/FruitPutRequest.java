package org.acme.rest.json;

public class FruitPutRequest {

    public String name;
    public String description;

    public FruitPutRequest() {
    }

    public FruitPutRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
