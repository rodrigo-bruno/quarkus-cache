package org.acme.rest.json;

public class Session {
	public final String id;
	public final String location;
	public final Long timestamp;
	
	public Session(String id, String location, Long timestamp) {
		this.id = id;
		this.location = location;
		this.timestamp = timestamp;
	}

}
