package org.acme.rest.json;

import java.util.List;

public class Name {
	public final String nconst;
	public final String primaryName;
	public final String birthYear;
	public final String deathYear;
	public final String primaryProfession;
	public final List<String> knownForTitles;
	
	public Name(String nconst, String primaryName, String birthYear, String deadthYear, String primaryProfession, List<String> knownForTitles) {
		this.nconst = nconst;
		this.primaryName = primaryName;
		this.birthYear = birthYear;
		this.deathYear = deadthYear;
		this.primaryProfession = primaryProfession;
		this.knownForTitles = knownForTitles;
	}
	
	public String toString() {
		return String.format("nconst: %s\n"
				+ "primaryName: %s\n"
				+ "birthYear: %s\n"
				+ "deathYear: %s\n"
				+ "primaryProfession: %s\n"
				+ "knownForTitles: %s", nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles);
	}
}
