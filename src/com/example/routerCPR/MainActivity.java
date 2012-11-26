package com.example.routerCPR;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.routerCPR.domain.Credential;

public class MainActivity extends Activity {
	
	private EditText addressText;
	
	private enum Error {
		NONE,
		INVALID_URL,
		COULD_NOT_CONNECT
	};

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
    	new TryPasswordTask("10.0.3.14", new ArrayList<Credential>()).execute();
    }
    
    private class TryPasswordTask extends AsyncTask<Void, Double, Credential> {
    	
    	private final String address;
    	private final List<Credential> credentials;
    	private Error error = Error.NONE;
    	
    	public TryPasswordTask(String address, List<Credential> credentials) {
    		this.address = cleanup(address);
    		this.credentials = credentials;
    	}

		@Override
		protected Credential doInBackground(Void... params) {
			
			URL url;
			try {
				url = new URL(address);
			} catch (MalformedURLException e) {
				error = Error.INVALID_URL;
				return null;
			}
			
			for (final Credential credential : credentials) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(credential.getUser(), credential.getPassword().toCharArray());
					}
				});
				
				HttpURLConnection connection;
				try {
					connection = (HttpURLConnection)url.openConnection();
				} catch (IOException e) {
					error = Error.COULD_NOT_CONNECT;
					return null;
				}
				
				int responseCode;
				try {
					responseCode = connection.getResponseCode();
				} catch (IOException e) {
					error = Error.COULD_NOT_CONNECT;
				}
			}
			
			return new Credential("hatfullofhallow", "hunter7");
		}
		
		@Override
		protected void onPostExecute(Credential result) {
			String status = "User: " + result.getUser() + ", Password: " + result.getPassword();
			Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected void onProgressUpdate(Double... values) {
			Log.d(MainActivity.this.getClass().getName(), Double.toString(values[0]));
		}
		
		public String cleanup(String url) {
			
			String cleanUrl = url.trim();
			
			if (!cleanUrl.contains("://")) {
				cleanUrl = "http://" + cleanUrl;
			}
			
			return cleanUrl;
		}
    	
    }
    
}
