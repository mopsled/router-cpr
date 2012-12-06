package com.ageatches.routerCPR;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ageatches.routerCPR.domain.Password;
import com.ageatches.routerCPR.domain.Router;
import com.ageatches.routerCPR.domain.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	
	private static final String DATABASE_NAME = "routercpr.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String[] DEFAULT_USERS = {"", "admin", "root"};
	private static final String[] DEFAULT_PASSWORDS = {"", "password", "admin", "logmein", "root"};
	
	private RuntimeExceptionDao<User, Integer> userRuntimeDao;
	private RuntimeExceptionDao<Password, Integer> passwordRuntimeDao;
	private RuntimeExceptionDao<Router, Integer> routerRuntimeDao;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Password.class);
			TableUtils.createTable(connectionSource, Router.class);
		} catch (SQLException e) {
			Log.d(DatabaseHelper.class.getName(), "Could not create database", e);
			throw new RuntimeException(e);
		}
		
		insertDefaultUsers();
		insertDefaultPasswords();
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		
	}
	
	@Override
	public void close() {
		super.close();
		userRuntimeDao = null;
		passwordRuntimeDao = null;
		routerRuntimeDao = null;
	}
	
	private void insertDefaultUsers() {
		for (String username : DEFAULT_USERS) {
			User user = new User(username);
			getUserDao().create(user);
		}
	}
	
	private void insertDefaultPasswords() {
		for (String passphrase : DEFAULT_PASSWORDS) {
			Password password = new Password(passphrase);
			getPasswordDao().create(password);
		}
	}
	
	public RuntimeExceptionDao<User, Integer> getUserDao() {
		if (userRuntimeDao == null) {
			userRuntimeDao = getRuntimeExceptionDao(User.class);
		}
		
		return userRuntimeDao;
	}
	
	public RuntimeExceptionDao<Password, Integer> getPasswordDao() {
		if (passwordRuntimeDao == null) {
			passwordRuntimeDao = getRuntimeExceptionDao(Password.class);
		}
		
		return passwordRuntimeDao;
	}
	
	public RuntimeExceptionDao<Router, Integer> getRouterDao() {
		if (routerRuntimeDao == null) {
			routerRuntimeDao = getRuntimeExceptionDao(Router.class);
		}
		
		return routerRuntimeDao;
	}

}
