package com.ageatches.routerCPR;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.ageatches.routerCPR.BruteForceTask.Credential;
import com.ageatches.routerCPR.BruteForceTask.Error;
import com.ageatches.routerCPR.MyLocation.LocationResult;
import com.ageatches.routerCPR.domain.Password;
import com.ageatches.routerCPR.domain.Router;
import com.ageatches.routerCPR.domain.User;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements BruteForceTaskListener {
	
	private EditText addressText;
	private WebView status;
	private Credential discoveredCredentials;
	private Menu menu;
	
	private enum StatusType {
		INFO,
		GOOD,
		BAD,
		IMPORTANT
	};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        addressText = (EditText)findViewById(R.id.address);
        status = (WebView)findViewById(R.id.status);
        
        initializeStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	this.menu = menu;
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	
    	if (itemId == R.id.menu_guess_gateway) {
    		String gatewayAddress = getGatewayAddress();
    		if (gatewayAddress == null) {
    			Toast.makeText(this, getString(R.string.main_cannot_get_gateway), Toast.LENGTH_SHORT).show();
    		} else {
    			setAddress(gatewayAddress);
    		}
    		
    		return true;
    	} else if (itemId == R.id.menu_debug_known) {
    		setAddress("mopsled.com/r");
    	} else if (itemId == R.id.menu_store_credentials) {
    		storeCredentials();
    	} else if (itemId == R.id.menu_view_router_locations) {
    		Intent routerMapIntent = new Intent(this, RouterMap.class);
    		startActivity(routerMapIntent);
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    public void recoverAction(View v) {
    	resetState();
    	
    	String address = addressText.getText().toString();
    	appendToStatus(getString(R.string.status_starting_recovery) + " " + address + "...");
    	
    	RuntimeExceptionDao<User, Integer> userDao = getHelper().getUserDao();
    	List<User> users = userDao.queryForAll();
    	
    	RuntimeExceptionDao<Password, Integer> passwordDao = getHelper().getPasswordDao();
    	List<Password> passwords = passwordDao.queryForAll();
    		
    	new BruteForceTask(addressText.getText().toString(), users, passwords, this).execute();
    }

    public void processBruteForceTaskSucceeded(Credential credentials) {
    	clearSubStatus();
    	appendToStatus(getString(R.string.status_successful_brute_force), StatusType.GOOD);
		String status = credentials.getUser().getUser() + "/" + credentials.getPassword().getPassword();
		appendToStatus(getString(R.string.status_credentials) + " " + status, StatusType.GOOD);
		appendToStatus(getString(R.string.status_store_credentials), StatusType.IMPORTANT);
		
		addStoreCredentialsMenuItem();
		discoveredCredentials = credentials;
	}

    public void processBruteForceTaskFailed(Error error) {
		if (error == Error.COULD_NOT_CONNECT) {
			appendToStatus(getString(R.string.error_could_not_connect), StatusType.BAD);
		} else if (error == Error.AUTHENTICATION_UNECESSARY) {
			appendToStatus(getString(R.string.error_no_authentication_needed), StatusType.BAD);
		} else if (error == Error.INVALID_URL) {
			appendToStatus(getString(R.string.error_bad_url), StatusType.BAD);
		} else if (error == Error.UNKNOWN_RESPONSE_CODE) {
			appendToStatus(getString(R.string.error_uknown_response_code), StatusType.BAD);
		} else if (error == Error.COULD_NOT_BRUTE_FORCE) {
			appendToStatus(getString(R.string.status_brute_force_failed), StatusType.BAD);
		}
		
		clearSubStatus();
	}

	public void processBruteForceTaskUpdate(String progress, String user, String password) {
		StringBuilder statusBuilder = new StringBuilder(progress).append(" ")
				.append(getString(R.string.progress_trying)).append(" ")
				.append(user).append("/").append(password);
		setSubStatus(statusBuilder.toString());
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initializeStatus() {
        clearStatus();
        status.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        status.getSettings().setJavaScriptEnabled(true);
	}
	
	private void storeCredentials() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifiConnectivityInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (!wifiConnectivityInfo.isConnected()) {
			appendToStatus(getString(R.string.error_not_connected_to_wifi), StatusType.BAD);
			removeStoreCredentialsMenuItem();
			return;
		}
		
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		
		final String ssid = wifiInfo.getSSID();
		final String bssid = wifiInfo.getBSSID();
		final String user = discoveredCredentials.getUser().getUser();
		final String password = discoveredCredentials.getPassword().getPassword();
		
		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				Router.Builder routerBuilder = new Router.Builder(bssid, user, password).ssid(ssid);
				
				if (location == null) {
					appendToStatus(getString(R.string.status_no_gps));
				} else {
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					appendToStatus(getString(R.string.status_located_gps) + " " + Double.toString(latitude) + "," + Double.toString(longitude));
					routerBuilder = routerBuilder.coordinates(latitude, longitude);
				}
				
				Router router = routerBuilder.build();
				deleteRoutersWithSameBssid(router);
				getHelper().getRouterDao().create(router);
				
				appendToStatus(getString(R.string.status_credentials_stored), StatusType.IMPORTANT);
			}
		};
		
		appendToStatus(getString(R.string.status_locating_gps));
		MyLocation myLocation = new MyLocation();
		boolean canGetLocation = myLocation.getLocation(this, locationResult);
		
		if (!canGetLocation) {
			locationResult.gotLocation(null);
		}
		
		removeStoreCredentialsMenuItem();
	}
	
	private void setAddress(String address) {
		addressText.setText(address);
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(addressText.getWindowToken(), 0);
		status.requestFocus();
	}
	
	private void deleteRoutersWithSameBssid(Router router) {
		Router oldRouter = new Router.Builder(router.getBssid(), null, null).build();
		RuntimeExceptionDao<Router, Integer> routerDao = getHelper().getRouterDao();
		List<Router> oldRouters = routerDao.queryForMatching(oldRouter);
		for (int i = 0; i < oldRouters.size(); i++) {
			routerDao.delete(oldRouters.get(i));
		}
	}
	
	private void resetState() {
		clearStatus();
    	clearSubStatus();
    	discoveredCredentials = null;
    	removeStoreCredentialsMenuItem();
	}
	
	private void clearStatus() {
		status.loadUrl("file:///android_asset/status.html");
	}

    private void appendToStatus(String update) {
    	appendToStatus(update, StatusType.INFO);
    }
    
    private void appendToStatus(String update, StatusType type) {
    	update = makeJavascriptStringSafe(update);
    	status.loadUrl("javascript:appendToStatus('" + update + "', '" + type.toString() + "')");
    }
    
    private void setSubStatus(String message) {
    	status.loadUrl("javascript:setSubStatus('" + message + "')");
    }
    
    private void clearSubStatus() {
    	status.loadUrl("javascript:setSubStatus('')");
    }
    
    private String makeJavascriptStringSafe(String message) {
    	message = message.replace("\\", "\\\\");
    	message = message.replace("\"", "\\\"");
    	message = message.replace("'", "\\'");
    	return message;
    }
    
    private void addStoreCredentialsMenuItem() {
    	MenuItem item = menu.findItem(R.id.menu_store_credentials);
    	item.setVisible(true);
    }
    
    private void removeStoreCredentialsMenuItem() {
    	MenuItem item = menu.findItem(R.id.menu_store_credentials);
    	item.setVisible(false);
    }
    
    private String getGatewayAddress() {
    	WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	DhcpInfo info = wifiManager.getDhcpInfo();
    	
    	byte[] infoBytesAndroidOrder = ByteBuffer.allocate(4).putInt(info.gateway).array();
    	byte[] infoBytesNetworkOrder = new byte[4];
    	for (int i = 0; i < infoBytesAndroidOrder.length; i++) {
    		infoBytesNetworkOrder[infoBytesAndroidOrder.length - i - 1] = infoBytesAndroidOrder[i];
    	}
    	
    	String gateway = null;
    	
    	try {
			gateway = InetAddress.getByAddress(infoBytesNetworkOrder).getHostAddress();
		} catch (UnknownHostException e) {
			Log.d(this.getClass().getName(), "Unable to turn gateway " + Integer.toString(info.gateway) + " into IP address", e);
		}
    	
    	return gateway;
    }
    
}
