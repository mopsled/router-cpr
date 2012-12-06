package com.ageatches.routerCPR.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "routers")
public class Router {
	@DatabaseField(id = true)
	private final String ssid;
	@DatabaseField
	private final String bssid;
	@DatabaseField
	private final Double latitude;
	@DatabaseField
	private final Double longitude;
	@DatabaseField(foreign = true)
	private final Credential credential;
	
	public static class Builder {
		private final String bssid;
		private final Credential credential;
		
		private String ssid = null;
		private Double latitude = null;
		private Double longitude = null;
		
		public Builder(String bssid, Credential credential) {
			this.bssid = bssid;
			this.credential = credential;
		}
		
		public Builder ssid(String ssid) {
			this.ssid = ssid;
			return this;
		}
		
		public Builder coordinates(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
			return this;
		}
		
		public Router build() {
			return new Router(this);
		}
	}
	
	public Router(Builder builder) {
		ssid = builder.ssid;
		bssid = builder.bssid;
		latitude = builder.latitude;
		longitude = builder.longitude;
		credential = builder.credential;
	}

}
