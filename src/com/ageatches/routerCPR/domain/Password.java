package com.ageatches.routerCPR.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "passwords")
public class Password {
	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private final String password;
	
	public Password(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
}
