package com.example.routerCPR;

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
    	
    	public TryPasswordTask(String address, List<Credential> credentials) {
    		this.address = address;
    		this.credentials = credentials;
    	}

		@Override
		protected Credential doInBackground(Void... params) {
			for (int i = 0; i < 5; ++i) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				this.publishProgress((double)i / 5.0);
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
    	
    }
    
}
