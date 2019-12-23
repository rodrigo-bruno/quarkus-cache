package org.acme.rest.json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.rest.json.utils.HashMapStringName;
import org.acme.rest.json.utils.HashMapStringRating;
import org.acme.rest.json.utils.HashMapStringTitle;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TitleResource {

	// TODO - queries:
	// 1 - get trendy movies, sort by year, sort by rating
	// 2 - get movies by genre, sort by rating
	// 3 - get movies from name, sort by rating
	// 4 - get title from title (done)
	// 5 - get get name from name (done)
	// 6 - get names from title
	// 7 - get titles from name
	
    private Map<String,Title> titles = Collections.synchronizedMap(new HashMapStringTitle());
    private Map<String, Name> names = Collections.synchronizedMap(new HashMapStringName());
    private Map<String, Rating> ratings = Collections.synchronizedMap(new HashMapStringRating());
    
    private static final String localDB = "/home/rbruno/Downloads/imdb-lite";

    public TitleResource() { }

    @GET
    @Path("/titles/{tconst}")
    public Set<TitleRequest> getTitle(@PathParam("tconst") String tconst) {
    	if (tconst.equals("all")) {
    		return getTitles();	
    	} else {
    		Set<TitleRequest> set = new HashSet<>();
    		if (titles.containsKey(tconst)) {
    			set.add(new TitleRequest(titles.get(tconst).tconst, titles.get(tconst).toString()));	
    		}
    		return set;
    	}
    	
    }
    
    @GET
    @Path("/names/{nconst}")
    public Set<NameRequest> getName(@PathParam("nconst") String nconst) {
    	if (nconst.equals("all")) {
    		return getNames();	
    	} else {
    		Set<NameRequest> set = new HashSet<>();
    		if (names.containsKey(nconst)) {
    			set.add(new NameRequest(names.get(nconst).nconst, names.get(nconst).toString()));	
    		}
    		return set;
    	}
    	
    }

    @GET
    @Path("/ratings/{tconst}")
    public Set<RatingRequest> getRating(@PathParam("tconst") String tconst) {
    	if (tconst.equals("all")) {
    		return getRatings();	
    	} else {
    		Set<RatingRequest> set = new HashSet<>();
    		if (ratings.containsKey(tconst)) {
    			set.add(new RatingRequest(ratings.get(tconst).tconst, ratings.get(tconst).toString()));	
    		}
    		return set;
    	}
    	
    }
    
    private long usedMemory() {
    	System.gc();
    	long total = Runtime.getRuntime().totalMemory();
    	long free = Runtime.getRuntime().freeMemory();
    	return (total - free) / (1024 * 1024);
    }

    @GET
    @Path("/info")
    public String getInformation() {
    	
    	return String.format(
    				"memory: %d\n"
    			+ 	"titles: %d\n"
    			+	"names: %d\n"
    			+	"rankings: %d",
    			usedMemory(), titles.size(), names.size(), ratings.size());
    	
    }

    @GET
    @Path("/initialize")
    public String initialize() {
    	long memoryTitles, memoryNames, memoryRatings;
    	
    	try(BufferedReader br = new BufferedReader(new FileReader(localDB + "/title.basics.tsv"))) {
    	    for(String line; (line = br.readLine()) != null; ) {
    	        String[] splits = line.split("\t");
    	        titles.put(splits[0], new Title(
    	        		splits[0],
    	        		splits[1],
    	        		splits[2],
    	        		splits[3],
		    	        splits[4],
		        		splits[5],
						splits[6],
						splits[7],
						Arrays.asList(splits[8].split(","))));
    	    }
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	memoryTitles = usedMemory();
    	
    	try(BufferedReader br = new BufferedReader(new FileReader(localDB + "/name.basics.tsv"))) {
    	    for(String line; (line = br.readLine()) != null; ) {
    	        String[] splits = line.split("\t");
    	        names.put(splits[0], new Name(
    	        		splits[0],
    	        		splits[1],
    	        		splits[2],
    	        		splits[3],
		    	        splits[4],
						Arrays.asList(splits[5].split(","))));
    	    }
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	memoryNames = usedMemory() - memoryTitles;
    	
    	try(BufferedReader br = new BufferedReader(new FileReader(localDB + "/title.ratings.tsv"))) {
    	    for(String line; (line = br.readLine()) != null; ) {
    	        String[] splits = line.split("\t");
    	        ratings.put(splits[0], new Rating(
    	        		splits[0],
    	        		splits[1],
    	        		splits[2]));
    	    }
    	} catch (Exception e) {
			e.printStackTrace();
		}
		
    	memoryRatings = usedMemory() - memoryNames - memoryTitles;
    	
    	return String.format("Loaded %d titles (%d MBs), %d names (%d MBs), and %d rankings (%d MBs).", titles.size(), memoryTitles, names.size(), memoryNames, ratings.size(), memoryRatings);
    	
    }
    
    @GET
    @Path("/titles")
    public Set<TitleRequest> getTitles() {
    	Set<TitleRequest> set = new HashSet<>();
    	for (Title f : titles.values()) {
    		set.add(new TitleRequest(f.tconst, f.toString()));
    	}
    	return set;
    }
    
    @GET
    @Path("/names")
    public Set<NameRequest> getNames() {
    	Set<NameRequest> set = new HashSet<>();
    	for (Name f : names.values()) {
    		set.add(new NameRequest(f.nconst, f.toString()));
    	}
    	return set;
    }

    @GET
    @Path("/ratings")
    public Set<RatingRequest> getRatings() {
    	Set<RatingRequest> set = new HashSet<>();
    	for (Rating f : ratings.values()) {
    		set.add(new RatingRequest(f.tconst, f.toString()));
    	}
    	return set;
    }
    
    public static void main(String[] args) throws Exception {
    	System.out.println((new TitleResource()).initialize());
    }
}
