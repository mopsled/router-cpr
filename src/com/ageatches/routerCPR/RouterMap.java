package com.ageatches.routerCPR;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.ageatches.routerCPR.domain.Router;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class RouterMap extends MapActivity {

	private MapView mapView;
	RouterMapOverlay routerMapOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.router_map);
		
		mapView = (MapView)findViewById(R.id.map);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.router);
		RouterMapOverlay routerMapOverlay = new RouterMapOverlay(drawable, this);
		
		List<OverlayItem> overlayItems = getRouterOverlays();
		for (OverlayItem overlayItem : overlayItems) {
			routerMapOverlay.addOverlay(overlayItem);
		}
		
		
		mapOverlays.add(routerMapOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	List<OverlayItem> getRouterOverlays() {
		List<Router> routers = getRouters();
		List<OverlayItem> routerOverlays = new ArrayList<OverlayItem>();
		
		for (Router router : routers) {
			GeoPoint geoPoint = new GeoPoint((int)(router.getLatitude() * 1E6), (int)(router.getLongitude() * 1E6));
			String title = router.getSsid();
			StringBuilder message = new StringBuilder();
			message.append("BSSID - ").append(router.getBssid()).append("\n\n");
			message.append("User - ").append(router.getUser()).append("\n");
			message.append("Password - ").append(router.getPassword());
			
			OverlayItem overlayItem = new OverlayItem(geoPoint, title, message.toString());
			
			routerOverlays.add(overlayItem);
		}
		
		return routerOverlays;
	}
	
	List<Router> getRouters() {
		DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		RuntimeExceptionDao<Router, Integer> routerDao = databaseHelper.getRouterDao();
		return routerDao.queryForAll();
	}

}
