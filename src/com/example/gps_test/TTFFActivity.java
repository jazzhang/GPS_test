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

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TTFFActivity extends Activity implements IGPSreportListener {

	public TextView latitude;
	public TextView longtitude;
	public TextView altitude;
	public TextView start_mode_text;
	// public TextView speed;
	// public TextView heading;
	public TextView accuracy;
	public TextView TTFF;
	public TextView time;
	public TextView ttff_count;
	public TextView ttff_interval;
	public TextView time_out;
	public TextView pre_ttff;
	public TextView avg_ttff;
	public TextView max_ttff;
	public boolean isrunning = false;
	public int time_out_trail = 0;
	String Start_mode = "Cold Start";
	private ListView SVinfo_listView;
	ArrayList<Map<String, String>> svData = new ArrayList<Map<String, String>>();
	ArrayList<Double> TTFFData = new ArrayList<Double>();
	SimpleAdapter adapter;
	// public GPSTest GPStest;
	public GPSTTFFtest GPSTTFFtest;
	private final String PREFERENCES_NAME = "userinfo";
	String logpath = "/sdcard/gpstest";
	public Excel_operation excel1;
	FileOutputStream fw = null;

	private static final int MSG_SVSTATUS = 0;
	private static final int MSG_LOCATION = 1;
	private static final int MSG_START = 2;
	private static final int MSG_TO = 3;

	public Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TO:
				time_out_trail++;
				excel1.write_info_time_out(GPSTTFFtest.time_out);
				Log.d("GPSTEST_time_out_trail =",
						Integer.toString(time_out_trail));
				if (GPSTTFFtest.current_count == GPSTTFFtest.count) {
					isrunning = false;
					stop_record_log();
					excel1.close_xls_file();
				}
				break;
			case MSG_SVSTATUS:
				adapter.notifyDataSetChanged();
				break;
			case MSG_LOCATION:
				ArrayList<String> transfer = (ArrayList<String>) msg.obj;
				Log.d("GPSTEST_Location =", "MSG_LOCATION");
				String current_count1 = Integer
						.toString(GPSTTFFtest.current_count);
				Log.d("GPSSTATUS_GPSTTFFtest.count_end", current_count1);
				latitude.setText(transfer.get(0));
				longtitude.setText(transfer.get(1));
				altitude.setText(transfer.get(2));
				accuracy.setText(transfer.get(3));
				// this.speed.setText(speed);
				// this.heading.setText(heading);
				TTFF.setText(transfer.get(4) + "s");
				double avgttff = average(TTFFData);
				double maxttff = max(TTFFData);
				avg_ttff.setText(Double.toString(avgttff));
				max_ttff.setText(Double.toString(maxttff));
				excel1.write_info(transfer, svData);
				if (GPSTTFFtest.current_count == GPSTTFFtest.count) {
					isrunning = false;
					stop_record_log();
					excel1.close_xls_file();
				}
				break;
			case MSG_START:
				latitude.setText("0");
				longtitude.setText("0");
				altitude.setText("0");
				accuracy.setText("0");
				TTFF.setText("0");
				time.setText("00:00:00");
				String current_count = Integer
						.toString(GPSTTFFtest.current_count);
				ttff_count.setText(current_count + "/"
						+ String.valueOf(GPSTTFFtest.count));
				Log.d("GPSSTATUS_GPSTTFFtest.count", current_count);
				if (GPSTTFFtest.current_count == 1) {
					avg_ttff.setText("0");
					max_ttff.setText("0");
					pre_ttff.setText("0");
					break;
				}
				Log.d("GPSTEST", String.valueOf(GPSTTFFtest.time_out_flag));
				if (GPSTTFFtest.time_out_flag)
					TTFFData.add((double) GPSTTFFtest.time_out);
				double prettff = TTFFData.get(GPSTTFFtest.current_count - 2);
				pre_ttff.setText(Double.toString(prettff));
				break;
			}
		}
	};

	public double average(ArrayList<Double> array) {
		double sum = 0;
		int cnt = 0;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) < (double) GPSTTFFtest.time_out) {
				sum += array.get(i);
				cnt++;
			}
		}
		double ave = sum / cnt;
		return ave;
	}

	public double max(ArrayList<Double> array) {
		double max = 0;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) < (double) GPSTTFFtest.time_out) {
				if (array.get(i) > max)
					max = array.get(i);
			}
		}
		return max;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ttff_activity_main);
		latitude = (TextView) findViewById(R.id.latitude_value);
		longtitude = (TextView) findViewById(R.id.longtitude_value);
		altitude = (TextView) findViewById(R.id.altitude_value);
		start_mode_text = (TextView) findViewById(R.id.start_mode_value);
		// speed=(TextView)findViewById(R.id.speed_value);
		// heading=(TextView)findViewById(R.id.heading_value);
		accuracy = (TextView) findViewById(R.id.accuracy_value);
		TTFF = (TextView) findViewById(R.id.TTFF_value);
		time = (TextView) findViewById(R.id.time_value);
		ttff_count = (TextView) findViewById(R.id.ttff_count);
		ttff_interval = (TextView) findViewById(R.id.ttff_interval);
		time_out = (TextView) findViewById(R.id.time_out);
		pre_ttff = (TextView) findViewById(R.id.pre_ttff);
		avg_ttff = (TextView) findViewById(R.id.avg_ttff);
		max_ttff = (TextView) findViewById(R.id.max_ttff);

		this.latitude.setText("0");
		this.longtitude.setText("0");
		this.altitude.setText("0");
		this.accuracy.setText("0");
		// this.speed.setText("0");
		// this.heading.setText("0");
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
		// Log.d("GPSTEST_svdata.prn =",svData.get(0).get("prn"));
		// Log.d("GPSTEST_svdata.flag =",svData.get(0).get("flag").toString());
		adapter = new SimpleAdapter(
				this,
				svData,
				R.layout.list_item,
				new String[] { "prn", "snr", "el", "az", "flag", "used_in_fix" },
				new int[] { R.id.prn, R.id.snr, R.id.el, R.id.az, R.id.flag,
						R.id.used_in_fix });
		SVinfo_listView.setAdapter(adapter);
		LocationManager locationManager = this.GetLocationManager();
		GPSTTFFtest = new GPSTTFFtest(locationManager, this);
		excel1 = new Excel_operation();
		GPSTTFFtest.GPStest.listener = this;
		loadconfig(GPSTTFFtest);
		String interval = Integer.toString(GPSTTFFtest.interval);
		String timeout = Integer.toString(GPSTTFFtest.time_out);
		ttff_interval.setText(interval + "s");
		time_out.setText(timeout + "s");
		start_mode_text.setText(Start_mode);
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
		// String speed = Double.toString(location.getSpeed());
		// String heading = Double.toString(location.getBearing());
		String TTFF = Double.toString(TTF / 1000);
		TTFFData.add(TTF / 1000);

		ArrayList<String> transfer = new ArrayList<String>();
		transfer.add(latitude);
		transfer.add(longtitude);
		transfer.add(altitude);
		transfer.add(accuracy);
		transfer.add(TTFF);
		mhandler.obtainMessage(MSG_LOCATION, transfer).sendToTarget();

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
			mhandler.obtainMessage(MSG_SVSTATUS, svData).sendToTarget();

		}

	}

	public void ongpsstart() {
		mhandler.obtainMessage(MSG_START).sendToTarget();
	}

	public void ongpstimeout() {
		mhandler.obtainMessage(MSG_TO).sendToTarget();
	}

	@Override
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

	public void loadconfig(GPSTTFFtest gpsttfftest) {
		SharedPreferences userinfo = getSharedPreferences(PREFERENCES_NAME,
				Activity.MODE_PRIVATE);
		Start_mode = userinfo.getString("start_mode", "Cold Start");
		if (Start_mode.equals("Cold Start"))
			gpsttfftest.GPStest.start_mode = 0;
		else if (Start_mode.equals("Warm Start"))
			gpsttfftest.GPStest.start_mode = 1;
		else
			gpsttfftest.GPStest.start_mode = 2;
		gpsttfftest.count = userinfo.getInt("cnt", 10);
		gpsttfftest.interval = userinfo.getInt("interval", 10);
		gpsttfftest.time_out = userinfo.getInt("time_out", 180);
		excel1.get_true_position(userinfo.getString("if_using_position", "NO"),
				userinfo.getFloat("latitude", 0),
				userinfo.getFloat("longitude", 0));
	}

	public void start_record_log() {
		File nmealogfile;
		File logfilepath;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String nowTime = format.format(new Date());
		Build bd = new Build();
		String model = bd.MODEL;
		logfilepath = new File(logpath);
		if (!logfilepath.exists()) {
			logfilepath.mkdirs();
		}
		nmealogfile = new File(logpath, model + "_TTFF_NMEA_" + nowTime
				+ ".txt"); // /create log file
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

				GPSTTFFtest.startTTFFTest();
				svData.clear();
				adapter.notifyDataSetChanged();
				TTFFData.clear();
				isrunning = true;
				excel1.create_workbook();
				start_record_log();
			}
			return true;
		case R.id.stop:
			if (isrunning) {
				GPSTTFFtest.stopTTFFTest();
				isrunning = false;
				stop_record_log();
				excel1.close_xls_file();
			}
			return true;
		case R.id.clear_aiding_data:
			// GPStest.locationManager.sendExtraCommand("gps","delete_aiding_data",null);
			Log.d("GPSTEST_delete_aiding_data", "delete_aiding_data");
			return true;
		}
		return false;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
					GPSTTFFtest.stopTTFFTest();
					isrunning = false;
					stop_record_log();
					excel1.close_xls_file();
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
