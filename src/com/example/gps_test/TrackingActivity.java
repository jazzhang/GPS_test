package com.example.gps_test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TrackingActivity extends Activity implements IGPSreportListener {

	public TextView latitude;
	public TextView longtitude;
	public TextView altitude;
	public TextView start_mode_text;
	public TextView speed;
	public TextView heading;
	public TextView accuracy;
	public TextView TTFF;
	public TextView time;
	public boolean isrunning = false;
	String Start_mode = "Cold Start";
	private ListView SVinfo_listView;
	ArrayList<Map<String, String>> svData = new ArrayList<Map<String, String>>();
	SimpleAdapter adapter;
	public GPSTest GPStest;
	private final String PREFERENCES_NAME = "userinfo";

	FileOutputStream fw = null;
	String logpath = "/sdcard/gpstest";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		latitude = (TextView) findViewById(R.id.latitude_value);
		longtitude = (TextView) findViewById(R.id.longtitude_value);
		altitude = (TextView) findViewById(R.id.altitude_value);
		start_mode_text = (TextView) findViewById(R.id.start_mode_value);
		speed = (TextView) findViewById(R.id.speed_value);
		heading = (TextView) findViewById(R.id.heading_value);
		accuracy = (TextView) findViewById(R.id.accuracy_value);
		TTFF = (TextView) findViewById(R.id.TTFF_value);
		time = (TextView) findViewById(R.id.time_value);

		this.latitude.setText("0");
		this.longtitude.setText("0");
		this.altitude.setText("0");
		this.accuracy.setText("0");
		this.speed.setText("0");
		this.heading.setText("0");
		this.TTFF.setText("0");
		this.time.setText("00:00:00");

		// //set the list view
		SVinfo_listView = (ListView) findViewById(R.id.listview);
		Map<String, String> item = new HashMap<String, String>();
		item.put("prn", "PRN");
		item.put("snr", "SNR");
		item.put("el", "EL");
		item.put("az", "AZ");
		item.put("flag", "FLAG");
		item.put("used_in_fix", "USED");
		svData.add(item);
		Log.d("GPSTEST_svdata.prn =", svData.get(0).get("prn"));
		Log.d("GPSTEST_svdata.flag =", svData.get(0).get("flag").toString());
		adapter = new SimpleAdapter(
				this,
				svData,
				R.layout.list_item,
				new String[] { "prn", "snr", "el", "az", "flag", "used_in_fix" },
				new int[] { R.id.prn, R.id.snr, R.id.el, R.id.az, R.id.flag,
						R.id.used_in_fix });
		SVinfo_listView.setAdapter(adapter);
		LocationManager locationManager = this.GetLocationManager();
		GPStest = new GPSTest(locationManager, this);
		loadconfig(GPStest);
		start_mode_text.setText(Start_mode);
		GPStest.listener = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public LocationManager GetLocationManager() {
		return (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	public void ontimechanged(String timer) {
		time.setText(timer + "s");
	}

	public void onlocationchanged(Location location, Double TTF,
			GpsStatus gpsStatus) {
		// TODO Auto-generated method stub
		String latitude = Double.toString(location.getLatitude());
		String longtitude = Double.toString(location.getLongitude());
		String altitude = Double.toString(location.getAltitude());
		String accuracy = Double.toString(location.getAccuracy());
		String speed = Double.toString(location.getSpeed());
		String heading = Double.toString(location.getBearing());
		String TTFF = Double.toString(TTF / 1000);
		Log.d("GPSTEST_latitude =", latitude);
		Log.d("GPSTEST_longtitude =", longtitude);

		this.latitude.setText(latitude);
		this.longtitude.setText(longtitude);
		this.altitude.setText(altitude);
		this.accuracy.setText(accuracy);
		this.speed.setText(speed);
		this.heading.setText(heading);
		this.TTFF.setText(TTFF + "s");

	}

	public void onNmeadataReceived(String nmea) {
		// TODO Auto-generated method stub
		if (nmea.length() != 0) {
			try {
				if (fw != null) {
					fw.write(nmea.getBytes());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void ongpsstatuschanged(GpsStatus gpsStatus) {
		if (gpsStatus != null) {
			Iterator<GpsSatellite> iterator = gpsStatus.getSatellites()
					.iterator();
			svData.clear();
			Map<String, String> item1 = new HashMap<String, String>();
			item1.put("prn", "PRN");
			item1.put("snr", "SNR");
			item1.put("el", "EL");
			item1.put("az", "AZ");
			item1.put("flag", "FLAG");
			item1.put("used_in_fix", "USED");
			svData.add(item1);
			while (iterator.hasNext()) {
				GpsSatellite gpsSatellite = iterator.next();
				if (gpsSatellite.getSnr() != 0) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("prn", Integer.toString(gpsSatellite.getPrn()));
					item.put("snr", Float.toString(gpsSatellite.getSnr()));
					item.put("el",
							String.format("%.2f", gpsSatellite.getElevation()));
					item.put("az",
							String.format("%.2f", gpsSatellite.getAzimuth()));

					if (gpsSatellite.hasAlmanac()
							&& gpsSatellite.hasEphemeris())
						item.put("flag", "EA");
					else if (gpsSatellite.hasAlmanac())
						item.put("flag", "A");
					else if (gpsSatellite.hasEphemeris())
						item.put("flag", "E");
					else
						item.put("flag", "");

					item.put("used_in_fix",
							Boolean.toString(gpsSatellite.usedInFix()));
					svData.add(item);
				}
			}
			adapter.notifyDataSetChanged();
		}

	}

	public void ongpsstart() {

	}

	public void ongpstimeout() {

	}

	public void loadconfig(GPSTest gpstest) {
		SharedPreferences userinfo = getSharedPreferences(PREFERENCES_NAME,
				Activity.MODE_PRIVATE);
		Start_mode = userinfo.getString("start_mode", "Cold Start");
		if (Start_mode.equals("Cold Start"))
			gpstest.start_mode = 0;
		else if (Start_mode.equals("Warm Start"))
			gpstest.start_mode = 1;
		else
			gpstest.start_mode = 2;
	}

	public void start_record_log() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String nowTime = format.format(new Date());
		Build bd = new Build();
		String model = bd.MODEL;
		File logfilepath;
		logfilepath = new File(logpath);
		if (!logfilepath.exists()) {
			logfilepath.mkdirs();
		}
		File nmealogfile = new File(logpath, model + "_TRACKING_NMEA_"
				+ nowTime + ".txt"); // /create log file
		if (!nmealogfile.exists()) {
			try {
				nmealogfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			fw = new FileOutputStream(nmealogfile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void stop_record_log() {
		if (fw != null)
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.start:
			if (!isrunning) {
				GPStest.startNavigation();
				isrunning = true;
				svData.clear();
				adapter.notifyDataSetChanged();
				this.latitude.setText("0");
				this.longtitude.setText("0");
				this.altitude.setText("0");
				this.accuracy.setText("0");
				this.speed.setText("0");
				this.TTFF.setText("0");
				this.time.setText("00:00:00");
				this.heading.setText("00:00:00");
				start_record_log();
			}
			// recLen=0;
			// timer.cancel();

			return true;
		case R.id.stop:
			if (isrunning) {
				GPStest.stopNavigation();
				isrunning = false;
				stop_record_log();
			}
			return true;
			/*
			 * case R.id.sv_map: Intent intent = new Intent();
			 * intent.setClass(TrackingActivity.this, CanvasActivity.class);
			 * startActivity(intent); return true;
			 */
		case R.id.clear_aiding_data:
			GPStest.locationManager.sendExtraCommand("gps",
					"delete_aiding_data", null);
			Log.d("GPSTEST_delete_aiding_data", "delete_aiding_data");
			return true;
		}
		return false;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d("111", "111111111111111111111");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isrunning) {
				AlertDialog isExit = new AlertDialog.Builder(this).create();
				isExit.setTitle("Remind");
				isExit.setMessage("Stop Test?");
				isExit.setButton("Yes", listener);
				isExit.setButton2("No", listener);
				isExit.show();
			} else {
				finish();
			}

		}

		return false;

	}

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:
				if (isrunning) {
					GPStest.stopNavigation();
					isrunning = false;
					if (fw != null)
						try {
							fw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:
				break;
			default:
				break;
			}
		}
	};

}
