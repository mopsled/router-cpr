package com.ageatches.routerCPR.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "routers")
public class Router {
	@DatabaseField
	private String ssid;
	@DatabaseField(id = true)
	private String bssid;
	@DatabaseField
	private String user;
	@DatabaseField
	private String password;
	@DatabaseField
	private Double latitude;
	@DatabaseField
	private Double longitude;
	
	
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
	
	public Router() {
		
	}
}
