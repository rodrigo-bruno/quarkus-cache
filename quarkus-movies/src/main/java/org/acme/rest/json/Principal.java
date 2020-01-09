package org.acme.rest.json;

import java.util.List;

public class Principal {
	public final String tconst;
	public final String nconst;
	public final String category;

	public Principal(String tconst, String nconst, String category) {
		this.tconst = tconst;
		this.nconst = nconst;
		this.category = category;
	}

    public String toString() {
		return String.format("tconst: %s\n"
				+ "nconst: %s\n"
				+ "category: %s", tconst, nconst, category);
    }

}
