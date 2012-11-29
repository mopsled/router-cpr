package com.example.routerCPR;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.routerCPR.domain.Credential;

public class CredentialHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "routercpr.db";
	private static final int SCHEMA_VERSION = 1;
	
	private static final String CREATE_TABLE = "CREATE TABLE credentials (id INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT, password TEXT)";
	
	public CredentialHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Nothing to upgrade yet
	}
	
	public long insertCredential(Credential credential) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("user", credential.getUser());
		contentValues.put("password", credential.getPassword());
		
		return getWritableDatabase().insert("credentials", "user", contentValues);
	}

}
