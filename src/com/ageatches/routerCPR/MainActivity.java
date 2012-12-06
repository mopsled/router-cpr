package com.ageatches.routerCPR;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ageatches.routerCPR.domain.Password;
import com.ageatches.routerCPR.domain.User;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
	private EditText addressText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        addressText = (EditText)findViewById(R.id.address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void recoverAction(View v) {
    	String address = addressText.getText().toString();
    	Toast.makeText(this, "Recovering " + address, Toast.LENGTH_SHORT).show();
    	
    	RuntimeExceptionDao<User, Integer> userDao;
    	try {
    		userDao = getHelper().getUserRuntimeDao();
    	} catch (SQLException e) {
    		Log.d(MainActivity.class.getName(), "Could not get User DAO", e);
    		throw new RuntimeException(e);
    	}
    	List<User> users = userDao.queryForAll();
    	
    	RuntimeExceptionDao<Password, Integer> passwordDao;
    	try {
    		passwordDao = getHelper().getPasswordRuntimeDao();
    	} catch (SQLException e) {
    		Log.d(MainActivity.class.getName(), "Could not get Password DAO", e);
    		throw new RuntimeException(e);
    	}
    	List<Password> passwords = passwordDao.queryForAll();
    		
    	new BruteForceTask("10.0.3.14", users, passwords, this).execute();
    }
    
}
