package com.ageatches.routerCPR.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "passwords")
public class Password {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String password;
	
	public Password() {
			
	}
	
	public Password(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
}
