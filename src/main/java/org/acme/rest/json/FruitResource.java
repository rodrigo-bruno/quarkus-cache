package org.acme.rest.json;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.rest.json.utils.HashMapStringFruit;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    private Map<String,Fruit> fruits = Collections.synchronizedMap(new HashMapStringFruit());

    public FruitResource() {
        fruits.put("Apple", new Fruit("Apple", "Winter fruit"));
        fruits.put("Pineapple", new Fruit("Pineapple", "Tropical fruit"));
    }

    @GET
    @Path("/{fruit}")
    public Set<FruitPutRequest> get(@PathParam("fruit") FruitGetRequest fruit) {
    	if (fruit.name.equals("all")) {
    		return get();	
    	} else {
    		Set<FruitPutRequest> set = new HashSet<>();
    		if (fruits.containsKey(fruit.name)) {
    			set.add(new FruitPutRequest(fruits.get(fruit.name).name, fruits.get(fruit.name).description));	
    		}
    		return set;
    	}
    	
    }
    
    @GET
    public Set<FruitPutRequest> get() {
    	Set<FruitPutRequest> set = new HashSet<>();
    	for (Fruit f : fruits.values()) {
    		set.add(new FruitPutRequest(f.name, f.description));
    	}
    	return set;
    }

    @POST
    public Set<FruitPutRequest> add(FruitPutRequest fruit) {
        fruits.put(fruit.name, new Fruit(fruit.name, fruit.description));
        Set<FruitPutRequest> set = new HashSet<>();
        set.add(fruit);	
        return set;
    }

    @DELETE
    public Set<FruitPutRequest> delete(FruitPutRequest fruit) {
    	fruits.remove(fruit.name);
        Set<FruitPutRequest> set = new HashSet<>();
        set.add(fruit);	
        return set;
    }
}
