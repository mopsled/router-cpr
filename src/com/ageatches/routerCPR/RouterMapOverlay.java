package com.ageatches.routerCPR;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RouterMapOverlay extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	
	public RouterMapOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
	
	public void addOverlay(OverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}

}
