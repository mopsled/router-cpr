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
	private final String user;
	@DatabaseField
	private final String password;
	@DatabaseField
	private final Double latitude;
	@DatabaseField
	private final Double longitude;
	
	
	public static class Builder {
		private final String bssid;
		private final String user;
		private final String password;
		
		private String ssid = null;
		private Double latitude = null;
		private Double longitude = null;
		
		public Builder(String bssid, String user, String password) {
			this.bssid = bssid;
			this.user = user;
			this.password = password;
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
		user = builder.user;
		password = builder.password;
	}

}
