package org.acme.rest.json;

import java.util.Map;

import org.acme.rest.json.utils.HashMapStringInteger;

public class User {
	public final String id;
	public final String password;
	public final Map<String,Integer> userRatings;
	
	public User(String id, String password) {
		this.id = id;
		this.password = password;
		this.userRatings = new HashMapStringInteger();
	}
	
}
