package com.example.gps_test;


import java.io.PrintStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class GPSTest implements android.location.GpsStatus.Listener,LocationListener,android.location.GpsStatus.NmeaListener{
	
	public LocationManager locationManager=null;
//	public MainActivity MainActivity;
	public Location location=null;
	public GpsStatus gpsStatus=null;
	public LocationProvider locationProvider=null;
	public int iNbSatellites=0;
	public float averageSNR=0;
	public boolean isrunning=false;
	public boolean isfixed=false;
	public boolean isSV_fixed=false;
	IGPSreportListener listener;
	public boolean isTTFF=false;
	Timer timer;
	private int recLen=0;
	public boolean getlocationflag=false;
	public int location_inc=0;
	private static final int GPS_COLD_START = 0;
    private static final int GPS_WARM_START = 1;
    private static final int GPS_HOT_START = 2;
	public int start_mode=GPS_COLD_START;

	PrintStream printStream;
	
	
	
	
/////////timer 	
    final Handler handler = new Handler(){  
	       public void handleMessage(Message msg) {  
	          switch (msg.what) {      
	              case 1:      
	                  recLen++;
	                  String time_value = convertTime(recLen);
	                  listener.ontimechanged(time_value);
	                  break;      
	              }      
	              super.handleMessage(msg);  
	         }    
	     };
	     
    static String convertTime(long seconds){
	    long hour,min,sec;		
	    hour=seconds/3600;
	    min=seconds/60-hour*60;
	    sec=seconds-hour*3600-min*60;
	    String strDate=String.format("%02d:%02d:%02d", hour,min,sec);
	    return strDate;
	 	}
	     
    class task extends TimerTask{  
	    public void run() {  
	    Message message = new Message();      
	    message.what = 1;      
	    handler.sendMessage(message);    
	    }  
	 };
//////////////
	public GPSTest(LocationManager locationManager1, IGPSreportListener Gpslistener){
		listener = Gpslistener;
		locationManager = locationManager1;
		locationProvider=locationManager.getProvider("gps");
		locationManager.isProviderEnabled("gps");
//		locationManager.requestLocationUpdates("gps", 0, 0, this);
//		locationManager.addGpsStatusListener(this);

	}
	
	public void startNavigation(){
		
		////create the NMEA record file
		if(!isrunning){
		   isrunning = true;
		   isfixed = false;
		   isSV_fixed = false;
		   location_inc=0;
		   Bundle bundle = new Bundle();
		   switch(start_mode){
		   case GPS_COLD_START:	bundle.putBoolean("ephemeris", true);
		                        bundle.putBoolean("almanac", true);
		                        bundle.putBoolean("position", true);
		                        bundle.putBoolean("time", true);
		                        break;
		   case GPS_WARM_START:	bundle.putBoolean("ephemeris", true);
                                bundle.putBoolean("almanac", false);
                                bundle.putBoolean("position", false);
                                bundle.putBoolean("time", false);
                                break;		   
		   case GPS_HOT_START: bundle.putBoolean("ephemeris", false);
                               bundle.putBoolean("almanac", false);
                               bundle.putBoolean("position", false);
                               bundle.putBoolean("time", false);
                               break;
		   }
		   locationManager.sendExtraCommand("gps","delete_aiding_data",bundle);
    	   
	       locationManager.requestLocationUpdates("gps", 0, 0, this);
		   locationManager.addGpsStatusListener(this);
		   locationManager.addNmeaListener(this);
		   timer = new Timer(true);
		   timer.schedule(new task(),1000, 1000);
		   listener.ongpsstart();
		   Log.d("GPStest","start!");
		   getlocationflag=false;
		}

	}
	
	public void stopNavigation(){
		if(isrunning){
			if(locationManager!=null)
			{
				locationManager.removeUpdates(this);
				locationManager.removeGpsStatusListener(this);
				locationManager.removeNmeaListener(this);
				timer.cancel();
				recLen=0;
			}

			isrunning = false;
		}
		
	}
	

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		switch(event)
		{
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			//Log.d("GPSTEST_onGpsStatusChanged", "GPS_EVENT_FIRST_FIX");
			isfixed = true;
			break;
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			if(locationManager!=null)
			    gpsStatus=locationManager.getGpsStatus(null);
			listener.ongpsstatuschanged(gpsStatus);
			if(isfixed)
				isSV_fixed = true;
			//Log.d("GPSTEST_onGpsStatusChanged", "GPS_EVENT_SATELLITE_STATUS");
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			//listener.ongpsstart();
			//Log.d("GPSTEST_onGpsStatusChanged", "GPS_EVENT_STARTED");
			return;
		case GpsStatus.GPS_EVENT_STOPPED:
			//Log.d("GPSTEST_onGpsStatusChanged", "GPS_EVENT_STOPPED");
			return;
		}
				
	}
	
	@Override
	public void onLocationChanged(Location location) {
		//Log.d("GPSTEST_onGpsStatusChanged", "GPS_EVENT_LocationChanged");
		if(isfixed && isSV_fixed){
			this.location=location;
			Double TTF = (double) this.gpsStatus.getTimeToFirstFix();
			String TTFF = Double.toString(TTF/1000);
			Log.d("GPSSTATUS in GPStest class = ",TTFF);
			listener.onlocationchanged(location, TTF,gpsStatus);
//			String TTFF = Double.toString(TTF/1000);
			getlocationflag=true;
			long timestamp = location.getTime();
			Date date = new Date(timestamp);
			String date_string = date.toGMTString();
			//Log.d("GPSSTATUS_current_time",date_string);
		}
		location_inc++;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		//mainActivity.sessionDuration.setText("disabled");
		
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		//mainActivity.sessionDuration.setText("enabled");				
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
					
	}

	@Override
	public void onNmeaReceived(long timestamp, String nmea) {
		// TODO Auto-generated method stub
		//Log.d("GPSTEST_nmea", nmea+"GPSTESTNMEA");
		listener.onNmeadataReceived(nmea);
	}

}
