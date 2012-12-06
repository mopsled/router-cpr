package com.ageatches.routerCPR;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ageatches.routerCPR.domain.Password;
import com.ageatches.routerCPR.domain.Router;
import com.ageatches.routerCPR.domain.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	
	private static final String DATABASE_NAME = "routercpr.db";
	private static final int DATABASE_VERSION = 1;
	
	private Dao<User, Integer> userDao;
	private RuntimeExceptionDao<User, Integer> userRuntimeDao;
	private Dao<Password, Integer> passwordDao;
	private RuntimeExceptionDao<Password, Integer> passwordRuntimeDao;
	private Dao<Router, Integer> routerDao;
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
	
	public Dao<User, Integer> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(User.class);
		}
		return userDao;
	}
	
	public RuntimeExceptionDao<User, Integer> getUserRuntimeDao() throws SQLException {
		if (userRuntimeDao == null) {
			userRuntimeDao = getRuntimeExceptionDao(User.class);
		}
		return userRuntimeDao;
	}
	
	public Dao<Password, Integer> getPasswordDao() throws SQLException {
		if (passwordDao == null) {
			passwordDao = getDao(Password.class);
		}
		return passwordDao;
	}
	
	public RuntimeExceptionDao<Password, Integer> getPasswordRuntimeDao() throws SQLException {
		if (passwordRuntimeDao == null) {
			passwordRuntimeDao = getRuntimeExceptionDao(Password.class);
		}
		return passwordRuntimeDao;
	}
	
	public Dao<Router, Integer> getRouterDao() throws SQLException {
		if (routerDao == null) {
			routerDao = getDao(Router.class);
		}
		return routerDao;
	}
	
	public RuntimeExceptionDao<Router, Integer> getRouterRuntimeDao() throws SQLException {
		if (routerRuntimeDao == null) {
			routerRuntimeDao = getRuntimeExceptionDao(Router.class);
		}
		return routerRuntimeDao;
	}

}
