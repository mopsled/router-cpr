package com.ageatches.routerCPR;

import android.os.Bundle;
import com.google.android.maps.MapActivity;

public class RouterMap extends MapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.router_map);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
