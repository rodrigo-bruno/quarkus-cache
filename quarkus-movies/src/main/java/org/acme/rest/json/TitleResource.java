package org.acme.rest.json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.rest.json.utils.HashMapStringName;
import org.acme.rest.json.utils.HashMapStringRating;
import org.acme.rest.json.utils.HashMapStringSession;
import org.acme.rest.json.utils.HashMapStringTitle;
import org.acme.rest.json.utils.HashMapStringUser;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TitleResource {
	
	// TODO - move PathParams into QueryParams
    private Map<String,Title> titles = Collections.synchronizedMap(new HashMapStringTitle());
    private Map<String, Name> names = Collections.synchronizedMap(new HashMapStringName());
    private Map<String, Rating> ratings = Collections.synchronizedMap(new HashMapStringRating());
    private Map<String, User> users = Collections.synchronizedMap(new HashMapStringUser());
    private Map<String, Session> sessions = Collections.synchronizedMap(new HashMapStringSession());
    // principals.get(tconst).get(ordering) -> Principal
    private Map<String, List<Principal>> principals = Collections.synchronizedMap(new HashMap<>()); // TODO - inline list?
    
    private static final String localDB = "/home/rbruno/Downloads/imdb-lite";

    public TitleResource() { }

    // Query 1 - given a title id, get title description.
    @GET
    @Path("/titles")
    public Set<EntryDescription> getTitle(@QueryParam("tconst") String tconst) {    	
		Set<EntryDescription> set = new HashSet<>();
		if (titles.containsKey(tconst)) {
			set.add(new EntryDescription(titles.get(tconst).tconst, titles.get(tconst).toString()));	
		}
		return set;
    }

    // Query 2 - given a title id, get rating.
    @GET
    @Path("/ratings")
    public Set<EntryDescription> getRating(@QueryParam("tconst") String tconst) {
		Set<EntryDescription> set = new HashSet<>();
		if (ratings.containsKey(tconst)) {
			set.add(new EntryDescription(ratings.get(tconst).tconst, ratings.get(tconst).toString()));	
		}
		return set;    	
    }
    
    // Query 3 - given a user id and pass, check login
    @GET
    @Path("/login")
    public Set<EntryDescription> login(@QueryParam("user") String user, @QueryParam("pass") String pass) {
		Set<EntryDescription> set = new HashSet<>();
		User obj = users.get(user);
		if (obj != null) {
			if (!sessions.containsKey(user)) {
				if (obj.password.equals(pass)) {
					Long timestamp = System.currentTimeMillis();
					sessions.put(user, new Session(user, "ch", timestamp));
					set.add(new EntryDescription(user, timestamp.toString()));
				} else {
					set.add(new EntryDescription(user, "wrong password"));	
				}
			} else {
				set.add(new EntryDescription(user, "already logged in"));
			}
		} else {
			set.add(new EntryDescription(user, "user not found"));	
		}
		return set;    	
    }
    
    // Query 4 - given a user id, logout
    @GET
    @Path("/logout")
    public Set<EntryDescription> logout(@QueryParam("user") String user) {
		Set<EntryDescription> set = new HashSet<>();
		User obj = users.get(user);
		if (obj != null) {
			if (sessions.containsKey(user)) {
				sessions.remove(user);
				set.add(new EntryDescription(user, "logged out"));
			} else {
				set.add(new EntryDescription(user, "not logged in"));
			}
		} else {
			set.add(new EntryDescription(user, "user not found"));	
		}
		return set;    	
    }    
    // Query N - given a name id, get name description.
    @GET
    @Path("/names")
    public Set<EntryDescription> getName(@QueryParam("nconst") String nconst) {
		Set<EntryDescription> set = new HashSet<>();
		if (names.containsKey(nconst)) {
			set.add(new EntryDescription(names.get(nconst).nconst, names.get(nconst).toString()));	
		}
		return set;	
    }
    
    // Query 4 - given a title id, get principals
    @GET
    @Path("/principals")
    public Set<EntryDescription> getPrincipal(@QueryParam("tconst") String tconst) {
		Set<EntryDescription> set = new HashSet<>();
		
		if (!principals.containsKey(tconst)) {
			return set;
		}
		
		for (Principal p : principals.get(tconst)) {
			set.add(new EntryDescription(p.tconst, p.toString()));
		}
		
		return set;
    }
    
    
    // Query 5 - given a year, get title ids sorted by rating
    @GET
    @Path("/titlesyear/{year}")
    public List<TitleDescriptionRating> getTitlesYear(@PathParam("year") String year) {
    	List<TitleDescriptionRating> set = new ArrayList<>();
    	for (Title t : titles.values()) {
    		if (t.startYear.equals(year)) {
    			set.add(new TitleDescriptionRating(t.tconst, ratings.get(t.tconst).averageRating, t.toString()));
    		}
    	}
		Collections.sort(set, new Comparator<TitleDescriptionRating>() {

			@Override
			public int compare(TitleDescriptionRating o1, TitleDescriptionRating o2) {
				Float f1 = Float.parseFloat(o1.rating);
				Float f2 = Float.parseFloat(o2.rating);
				return Float.compare(f1, f2);
			}
		});
		
		System.out.println(set.size());
		
		return set;
    }
    
    // Query 6 - given a genre, get title ids sorted by rating
    @GET
    @Path("/titlesgenre/{genre}")
    public List<TitleDescriptionRating> getTitlesGenre(@PathParam("genre") String genre) {
    	List<TitleDescriptionRating> set = new ArrayList<>();
    	for (Title t : titles.values()) {
    		if (t.genres.contains(genre)) {
    			set.add(new TitleDescriptionRating(t.tconst, ratings.containsKey(t.tconst) ? ratings.get(t.tconst).averageRating : "0", t.toString()));
    		}
    	}
		Collections.sort(set, new Comparator<TitleDescriptionRating>() {

			@Override
			public int compare(TitleDescriptionRating o1, TitleDescriptionRating o2) {
				Float f1 = Float.parseFloat(o1.rating);
				Float f2 = Float.parseFloat(o2.rating);
				return Float.compare(f1, f2);
			}
		});
		
		System.out.println(set.size());
		
		return set;
    }
    
    private long usedMemory() {
    	System.gc();
    	long total = Runtime.getRuntime().totalMemory();
    	long free = Runtime.getRuntime().freeMemory();
    	return (total - free) / (1024 * 1024);
    }

    @GET
    @Path("/info")
    public String info() {
    	
    	return String.format(
    				"memory: %d\n"
    			+ 	"titles: %d\n"
    			+	"names: %d\n"
    			+	"rankings: %d",
    			usedMemory(), titles.size(), names.size(), ratings.size());
    	
    }

    private long loadTitleBasics() {
    	long mem_before = usedMemory();
    	
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
    	
    	return usedMemory() - mem_before;
    }
    
    private long loadNameBasics() {
    	long mem_before = usedMemory();
    	
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
    	
    	return usedMemory() - mem_before;
    }
    
    public long loadTitleRatings() {
    	long mem_before = usedMemory();
    	
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
    	
    	return usedMemory() - mem_before;
    }
    
    public long loadTitlePrincipals() {
    	long mem_before = usedMemory();
    	
    	try(BufferedReader br = new BufferedReader(new FileReader(localDB + "/title.principals.tsv"))) {
    	    for(String line; (line = br.readLine()) != null; ) {
    	        String[] splits = line.split("\t");
    	        
    	        if (!principals.containsKey(splits[0])) {
    	        	principals.put(splits[0], new LinkedList<>());
    	        }
    	        
    	        principals.get(splits[0]).add(new Principal(
    	        		splits[0],
    	        		splits[1],
    	        		splits[2],
    	        		splits[3],
    	        		splits[4],
    	        		Arrays.asList(splits[5].replace("[", "").replace("]", "").split(","))));
    	    }
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return usedMemory() - mem_before;
    }
    
    public long loadUsers() {
    	long mem_before = usedMemory();
    	
    	try(BufferedReader br = new BufferedReader(new FileReader(localDB + "/users.csv"))) {
    	    for(String line; (line = br.readLine()) != null; ) {
    	        String[] splits = line.split(",");
    	        users.put(splits[0], new User(
    	        		splits[0],
    	        		splits[1]));
    	    }
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return usedMemory() - mem_before;
    }
    
    @GET
    @Path("/init")
    public String init() {
    	long memoryTitles = loadTitleBasics();
    	long memoryNames = loadNameBasics();
    	long memoryRatings = loadTitleRatings();
    	long memoryPrincipals = loadTitlePrincipals();
    	long memoryUsers = loadUsers(); 
    	
    	int principalsCounter = 0;
    	for (List<Principal> list : principals.values()) {
    		principalsCounter += list.size();
    	}
    	
    	return String.format("Loaded %d titles (%d MBs), %d names (%d MBs), %d rankings (%d MBs), %s principals (%d MBs) %s users (%d MBs).", 
    			titles.size(), memoryTitles, 
    			names.size(), memoryNames, 
    			ratings.size(), memoryRatings, 
    			principalsCounter, memoryPrincipals, 
    			users.size(), memoryUsers);
    }
    
    public static void main(String[] args) throws Exception {
    	System.out.println((new TitleResource()).init());
    }
}
