package com.example.gps_test;

import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

public class GPSTTFFtest {

	public GPSTest GPStest;
	int interval = 10;
	int count = 10;
	int current_count = 0;
	int time_out = 180;
	boolean time_out_flag = false;
	// double avg_ttff;
	// double pre_ttff;
	// double max_ttff;
	Thread thread_1;
	Thread thread_2;

	boolean isGPSrunning = false;
	boolean isGPSstop = false;
	boolean istestrunning = false;

	public GPSTTFFtest(LocationManager locationManager,
			IGPSreportListener Gpslistener) {
		GPStest = new GPSTest(locationManager, Gpslistener);
		GPStest.isTTFF = true;
	}

	public void startTTFFTest() {
		thread_1 = new testthread();
		thread_1.start();
		istestrunning = true;
		current_count = 1;
	}

	public void stopTTFFTest() {
		if (isGPSrunning && istestrunning) {
			isGPSstop = true;

			isGPSrunning = false;
			istestrunning = false;
		} else if (!isGPSrunning && istestrunning) {
			isGPSstop = true;
			istestrunning = false;
		}
	}

	class testthread extends Thread {
		public void run() {

			int i;
			for (i = 0; i < count; i++) {
				thread_2 = new testthread_2();
				thread_2.start();
				isGPSrunning = true;
				isGPSstop = false;

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int temp = 0;
				time_out_flag = false;
				while (!GPStest.getlocationflag && !isGPSstop) {
					if (temp / 10 >= time_out) {
						time_out_flag = true;
						Log.d("GPSTEST_class_TIME_OUT", String.valueOf(time_out_flag));
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					temp++;
					// Log.d("GPSSTATUS_isGPSstop",Boolean.toString(isGPSstop));
					// Log.d("GPSSTATUS_getlocationflag",Boolean.toString(GPStest.getlocationflag));
				}
				// Log.d("GPSSTATUS_11",Boolean.toString(isGPSstop));
				// Log.d("GPSSTATUS_11",Boolean.toString(GPStest.getlocationflag));

				GPStest.stopNavigation();
				if (time_out_flag) {
					GPStest.listener.ongpstimeout();
					Log.d("GPSTEST_class_TIME_OUT_CALL",
							String.valueOf(time_out_flag));
				}
				thread_2.interrupt();
				if (isGPSstop)
					break;
				isGPSrunning = false;
				int k = 0;
				while (!isGPSstop && k < 10 * interval) {
					// if(isInterrupted())
					// break;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					k++;
					// Log.d("GPSSTATUS_22",Boolean.toString(isGPSstop));
				}
				// Log.d("GPSSTATUS_22",Boolean.toString(isGPSstop));
				current_count++;
				if (isGPSstop)
					break;

			}
			isGPSrunning = false;
		}

	}

	class testthread_2 extends Thread {
		public void run() {
			Looper.prepare();
			GPStest.startNavigation();
			Looper.loop();
		}
	}

}
