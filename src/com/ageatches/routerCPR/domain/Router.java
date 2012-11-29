package com.ageatches.routerCPR.domain;

public class Router {
	
	private final String ssid;
	private final String bssid;
	private final double latitude;
	private final double longitude;
	private final Credential credential;
	
	public static class Builder {
		private final String bssid;
		private final Credential credential;
		
		private String ssid = "";
		private double latitude = 0;
		private double longitude = 0;
		
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
