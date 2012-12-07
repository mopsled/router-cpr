package com.ageatches.routerCPR;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.ageatches.routerCPR.BruteForceTask.Credential;
import com.ageatches.routerCPR.BruteForceTask.Error;
import com.ageatches.routerCPR.domain.Password;
import com.ageatches.routerCPR.domain.User;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements BruteForceTaskListener {
	
	private EditText addressText;
	private WebView status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        addressText = (EditText)findViewById(R.id.address);
        status = (WebView)findViewById(R.id.status);
        
        initializeStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void recoverAction(View v) {
    	String address = addressText.getText().toString();
    	Toast.makeText(this, "Recovering " + address, Toast.LENGTH_SHORT).show();
    	appendToStatus("Starting recovery of " + address);
    	
    	RuntimeExceptionDao<User, Integer> userDao = getHelper().getUserDao();
    	List<User> users = userDao.queryForAll();
    	
    	RuntimeExceptionDao<Password, Integer> passwordDao = getHelper().getPasswordDao();
    	List<Password> passwords = passwordDao.queryForAll();
    		
    	new BruteForceTask(addressText.getText().toString(), users, passwords, this).execute();
    }

    public void processBruteForceTaskSucceeded(Credential credentials) {
		String status = "User: " + credentials.getUser() + ", Password: " + credentials.getPassword();
		Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
	}

    public void processBruteForceTaskFailed(Error error) {
		if (error == Error.COULD_NOT_CONNECT) {
			Toast.makeText(getApplicationContext(), "Could not connect to address", Toast.LENGTH_SHORT).show();
		} else if (error == Error.AUTHENTICATION_UNECESSARY) {
			Toast.makeText(getApplicationContext(), "Address given does not require authentication", Toast.LENGTH_SHORT).show();
		} else if (error == Error.INVALID_URL) {
			Toast.makeText(getApplicationContext(), "Could not understand this address", Toast.LENGTH_SHORT).show();
		} else if (error == Error.UNKNOWN_RESPONSE_CODE) {
			Toast.makeText(getApplicationContext(), "Unknown reponse code returned by server", Toast.LENGTH_SHORT).show();
		} else if (error == Error.COULD_NOT_BRUTE_FORCE) {
			Toast.makeText(getApplicationContext(), "Unable to brute force address", Toast.LENGTH_SHORT).show();
		}
	}

	public void processBruteForceTaskUpdate(String update) {
		appendToStatus(update);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initializeStatus() {
        clearStatus();
        status.getSettings().setJavaScriptEnabled(true);
	}
	
	private void clearStatus() {
		status.loadUrl("file:///android_asset/status.html");
	}

    private void appendToStatus(String update) {
    	status.loadUrl("javascript:appendToStatus('" + update + "')");
    }
    
}
