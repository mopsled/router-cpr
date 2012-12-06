package com.ageatches.routerCPR.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "credentials")
public class Credential {
	@DatabaseField(id = true)
	private int id;
	@DatabaseField(foreign = true)
	private final User user;
	@DatabaseField(foreign = true)
	private final Password password;
	
	public Credential(User user, Password password) {
		this.user = user;
		this.password = password;
	}
	
	public String getUser() {
		return user.getUser();
	}
	
	public String getPassword() {
		return password.getPassword();
	}
	
}
