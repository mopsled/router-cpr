package com.ageatches.routerCPR;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.ageatches.routerCPR.domain.Password;
import com.ageatches.routerCPR.domain.User;

public class BruteForceTask extends AsyncTask<Void, String, BruteForceTask.Credential> {
	
	public enum Error {
		NONE,
		INVALID_URL,
		COULD_NOT_CONNECT,
		AUTHENTICATION_UNECESSARY,
		UNKNOWN_RESPONSE_CODE,
		COULD_NOT_BRUTE_FORCE
	};
	
	private final String address;
	private final List<User> users;
	private final List<Password> passwords;
	private BruteForceTaskListener delegate;
	private Error error;
	
	public BruteForceTask(String address, List<User> users,  List<Password> passwords, BruteForceTaskListener delegate) {
		this.address = cleanup(address);
		this.users = users;
		this.passwords = passwords;
		this.delegate = delegate;
	}

	@Override
	protected Credential doInBackground(Void... params) {
		error = Error.NONE;
		
		URL url;
		try {
			url = new URL(address);
		} catch (MalformedURLException e) {
			error = Error.INVALID_URL;
			return null;
		}
		
		Error errorWithoutCredentials = tryConnectingWithoutCredentials(url);
		
		if (errorWithoutCredentials == Error.AUTHENTICATION_UNECESSARY) {
			error = Error.AUTHENTICATION_UNECESSARY;
			return null;
		} else if (errorWithoutCredentials == Error.COULD_NOT_CONNECT) {
			error = Error.COULD_NOT_CONNECT;
			return null;
		} else if (errorWithoutCredentials == Error.UNKNOWN_RESPONSE_CODE) {
			error = Error.UNKNOWN_RESPONSE_CODE;
			return null;
		}
		
		for (final User user : users) {
			for (final Password password : passwords) {
				HttpURLConnection connection;
				try {
					connection = (HttpURLConnection) url.openConnection();
				} catch (IOException e) {
					error = Error.COULD_NOT_CONNECT;
					return null;
				}
				
				connection.setUseCaches(false);
				setAuthentication(connection, user, password);
				
				String userString = (user.getUser().equals("")) ? "(blank)" : user.getUser();
				String passwordString = (password.getPassword().equals("")) ? "(blank)" : password.getPassword();
				publishProgress("Trying authentication " + userString + "/" + passwordString + "");
				
				int responseCode;
				try {
					responseCode = connection.getResponseCode();
				} catch (IOException e) {
					error = Error.COULD_NOT_CONNECT;
					return null;
				}
				
				if (responseCode == 200) {
					return new Credential(user, password);
				} else if (responseCode != 401) {
					Log.d(BruteForceTask.class.getName(), "Received unknown response code " + responseCode);
					error = Error.UNKNOWN_RESPONSE_CODE;
					return null;
				}
			}
		}
		
		error = Error.COULD_NOT_BRUTE_FORCE;
		return null;
	}
	
	@Override
	protected void onPostExecute(Credential result) {
		if (error == Error.NONE) {
			delegate.processBruteForceTaskSucceeded(result);
		} else {
			delegate.processBruteForceTaskFailed(error);
		}
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		delegate.processBruteForceTaskUpdate(values[0]);
	}
	
	private Error tryConnectingWithoutCredentials(URL url) {
		Authenticator.setDefault(null);
		
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			return Error.COULD_NOT_CONNECT;
		}
		
		int responseCode;
		try {
			responseCode = connection.getResponseCode();
		} catch (IOException e) {
			return Error.COULD_NOT_CONNECT;
		}
		
		if (responseCode == 200) {
			return Error.AUTHENTICATION_UNECESSARY;
		} else if (responseCode == 401) {
			return Error.NONE;
		} else {
			Log.d(BruteForceTask.class.getName(), "Received unknown response code " + responseCode);
			return Error.UNKNOWN_RESPONSE_CODE;
		}
	}
	
	private void setAuthentication(HttpURLConnection connection, User user, Password password) {
		String credentials = user.getUser() + ":" + password.getPassword();
		connection.setRequestProperty("Authorization", "basic " + Base64.encode(credentials.getBytes(), Base64.DEFAULT));
	}
	
	private String cleanup(String url) {
		
		String cleanUrl = url.trim();
		
		if (!cleanUrl.contains("://")) {
			cleanUrl = "http://" + cleanUrl;
		}
		
		return cleanUrl;
	}
	
	public class Credential {
		private final User user;
		private final Password password;
		
		public Credential(User user, Password password) {
			this.user = user;
			this.password = password;
		}
		
		public User getUser() {
			return user;
		}
		
		public Password getPassword() {
			return password;
		}
	}
}
