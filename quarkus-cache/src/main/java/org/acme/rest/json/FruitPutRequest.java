package org.acme.rest.json;

public class FruitPutRequest {

    public String name;
    public Integer x;
    public Integer y;

    public FruitPutRequest() {
    }

    public FruitPutRequest(String name, Integer x, Integer y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}
