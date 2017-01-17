package com.example.gps_test;

import android.location.GpsStatus;
import android.location.Location;

/**
 * Interface to implements to receive chronometer events
 * @author Alan Sowamber
 */
public interface IGPSreportListener {
	
	abstract void ontimechanged(String time);
	

	abstract void ongpsstatuschanged(GpsStatus gpsStatus);
	
	abstract void onlocationchanged(Location location,Double TTFF,GpsStatus gpsStatus);
	
	abstract void ongpsstart();
	
	abstract void ongpstimeout();
	
	abstract void onNmeadataReceived(String nmea);
	

}
