package org.acme.rest.json;

public class Rating {
	public final String tconst;
	public final String averageRating;
	public final String numVotes;
	
	public Rating(String tconst, String averageRating, String numVotes) {
		this.tconst = tconst;
		this.averageRating = averageRating;
		this.numVotes = numVotes;
		
	}
}
