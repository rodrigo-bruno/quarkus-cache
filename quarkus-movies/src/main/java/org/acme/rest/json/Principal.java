package org.acme.rest.json;

import java.util.List;

public class Principal {
	public final String tconst;
	public final String ordering;
	public final String nconst;
	public final String category;
	public final String job;
	public final List<String> characters;
	
	public Principal(String tconst, String ordering, String nconst, String category, String job, List<String> characters) {
		this.tconst = tconst;
		this.ordering = ordering;
		this.nconst = nconst;
		this.category = category;
		this.job = job;
		this.characters = characters;
	}
	
    public String toString() {
		return String.format("tconst: %s\n"
				+ "ordering: %s\n"
				+ "nconst: %s\n"
				+ "category: %s\n"
				+ "job: %s\n"
				+ "characters: %s", tconst, ordering, nconst, category, job, characters);
    }

}
