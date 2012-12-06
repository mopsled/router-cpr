package com.ageatches.routerCPR;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ageatches.routerCPR.domain.Password;
import com.ageatches.routerCPR.domain.User;

public class BruteForceTask extends AsyncTask<Void, Double, BruteForceTask.Credential> {
	
	public enum Error {
		NONE,
		INVALID_URL,
		COULD_NOT_CONNECT
	};
	
	private final String address;
	private final List<User> users;
	private final List<Password> passwords;
	private Context context;
	private Error error;
	
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
	
	public BruteForceTask(String address, List<User> users,  List<Password> passwords, Context context) {
		this.address = cleanup(address);
		this.users = users;
		this.passwords = passwords;
		this.context = context;
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
		
		for (final User user : users) {
			for (final Password password : passwords) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(user.getUser(), password.getPassword().toCharArray());
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
		}
		
		User user = new User("hatfullofhallow");
		Password password = new Password("hunter7");
		return new Credential(user, password);
	}
	
	@Override
	protected void onPostExecute(Credential result) {
		String status = "User: " + result.getUser() + ", Password: " + result.getPassword();
		Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onProgressUpdate(Double... values) {
		// Update UI with progress
	}
	
	public String cleanup(String url) {
		
		String cleanUrl = url.trim();
		
		if (!cleanUrl.contains("://")) {
			cleanUrl = "http://" + cleanUrl;
		}
		
		return cleanUrl;
	}
}
