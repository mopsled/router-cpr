package com.ageatches.routerCPR.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private final String user;
	
	public User(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
}
