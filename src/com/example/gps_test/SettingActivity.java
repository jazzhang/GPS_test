package com.example.gps_test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SettingActivity extends Activity {

	private ListView Setting_listView;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	SimpleAdapter adapter;
	private final String PREFERENCES_NAME = "userinfo";

	EditText edittxt;
	String Start_mode = "Cold Start";
	int cnt = 10;
	int interval = 10;
	int time_out = 180;
	double latitude = 0;
	double longitude = 0;
	String if_using_position = "NO";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_menu);

		SharedPreferences userinfo = getSharedPreferences(PREFERENCES_NAME,
				Activity.MODE_PRIVATE);
		Start_mode = userinfo.getString("start_mode", "Cold Start");
		cnt = userinfo.getInt("cnt", 10);
		interval = userinfo.getInt("interval", 10);
		time_out = userinfo.getInt("time_out", 180);
		latitude = userinfo.getFloat("latitude", 0);
		longitude = userinfo.getFloat("longitude", 0);
		if_using_position = userinfo.getString("if_using_position", "NO");
		list.clear();
		Setting_listView = (ListView) findViewById(R.id.listview_setting);
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("title", "Start Mode");
		item.put("info", Start_mode);
		list.add(item);
		Map<String, Object> item1 = new HashMap<String, Object>();
		item1.put("title", "TTFF Test Repeat Count");
		item1.put("info", cnt);
		list.add(item1);
		Map<String, Object> item2 = new HashMap<String, Object>();
		item2.put("title", "TTFF Test interval");
		item2.put("info", interval);
		list.add(item2);
		Map<String, Object> item3 = new HashMap<String, Object>();
		item3.put("title", "Time Out");
		item3.put("info", time_out);
		list.add(item3);
		Map<String, Object> item4 = new HashMap<String, Object>();
		item4.put("title", "Using the true position");
		item4.put("info", if_using_position);
		list.add(item4);
		Map<String, Object> item5 = new HashMap<String, Object>();
		item5.put("title", "Latitude");
		item5.put("info", latitude);
		list.add(item5);
		Map<String, Object> item6 = new HashMap<String, Object>();
		item6.put("title", "Longitude");
		item6.put("info", longitude);
		list.add(item6);
		adapter = new SimpleAdapter(this, list, R.layout.setting_list_item,
				new String[] { "title", "info" }, new int[] { R.id.title,
						R.id.info });
		Setting_listView.setAdapter(adapter);

		Setting_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (list.get(arg2).get("title").equals("Start Mode")) {
					int temp;
					if (Start_mode.equals("Cold Start"))
						temp = 0;
					else if (Start_mode.equals("Warm Start"))
						temp = 1;
					else
						temp = 2;
					new AlertDialog.Builder(SettingActivity.this)
							.setTitle("Start Mode")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setSingleChoiceItems(
									new String[] { "Cold Start", "Warm Start",
											"Hot Start" }, temp,
									new DialogInterface.OnClickListener() {
										SharedPreferences userinfo = getSharedPreferences(
												PREFERENCES_NAME,
												Activity.MODE_PRIVATE);
										SharedPreferences.Editor editor = userinfo
												.edit();

										public void onClick(
												DialogInterface dialog,
												int which) {
											switch (which) {
											case 0:
												editor.putString("start_mode",
														"Cold Start");
												Start_mode = "Cold Start";
												break;
											case 1:
												editor.putString("start_mode",
														"Warm Start");
												Start_mode = "Warm Start";
												break;
											case 2:
												editor.putString("start_mode",
														"Hot Start");
												Start_mode = "Hot Start";
												break;
											}
											Map<String, Object> item = new HashMap<String, Object>();
											item.put("title", "Start Mode");
											item.put("info", Start_mode);
											list.set(0, item);
											adapter.notifyDataSetChanged();
											editor.commit();
											dialog.dismiss();
										}
									}).setNegativeButton("Cancel", null).show();
					// Log.d("GPS_TEST",Integer.toString(Start_mode));
				}
				if (list.get(arg2).get("title")
						.equals("TTFF Test Repeat Count")) {
					edittxt = new EditText(SettingActivity.this);
					edittxt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
					new AlertDialog.Builder(SettingActivity.this)
							.setTitle("TTFF Test Repeat Count")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(edittxt)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// interval =
											// Integer.valueOf(edittxt.getText().toString());
											if (!(edittxt.getText() == null || ""
													.equals(edittxt.getText()
															.toString()))) {
												cnt = Integer.valueOf(edittxt
														.getText().toString());
												SharedPreferences userinfo = getSharedPreferences(
														PREFERENCES_NAME,
														Activity.MODE_PRIVATE);
												SharedPreferences.Editor editor = userinfo
														.edit();
												editor.putInt("cnt", cnt);
												editor.commit();
												Map<String, Object> item = new HashMap<String, Object>();
												item.put("title",
														"TTFF Test Repeat Count");
												item.put("info", cnt);
												list.set(1, item);
												adapter.notifyDataSetChanged();
											}
										}
									}).setNegativeButton("Cancel", null).show();
				}
				if (list.get(arg2).get("title").equals("TTFF Test interval")) {
					edittxt = new EditText(SettingActivity.this);
					edittxt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
					new AlertDialog.Builder(SettingActivity.this)
							.setTitle("TTFF Test interval")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(edittxt)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// interval =
											// Integer.valueOf(edittxt.getText().toString());
											if (!(edittxt.getText() == null || ""
													.equals(edittxt.getText()
															.toString()))) {
												interval = Integer
														.valueOf(edittxt
																.getText()
																.toString());
												SharedPreferences userinfo = getSharedPreferences(
														PREFERENCES_NAME,
														Activity.MODE_PRIVATE);
												SharedPreferences.Editor editor = userinfo
														.edit();
												editor.putInt("interval",
														interval);
												editor.commit();
												Map<String, Object> item = new HashMap<String, Object>();
												item.put("title",
														"TTFF Test interval");
												item.put("info", interval);
												list.set(2, item);
												adapter.notifyDataSetChanged();
											}
										}
									}).setNegativeButton("Cancel", null).show();

				}
				if (list.get(arg2).get("title").equals("Time Out")) {
					edittxt = new EditText(SettingActivity.this);
					edittxt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
					new AlertDialog.Builder(SettingActivity.this)
							.setTitle("Time Out")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(edittxt)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// interval =
											// Integer.valueOf(edittxt.getText().toString());
											if (!(edittxt.getText() == null || ""
													.equals(edittxt.getText()
															.toString()))) {
												time_out = Integer
														.valueOf(edittxt
																.getText()
																.toString());
												SharedPreferences userinfo = getSharedPreferences(
														PREFERENCES_NAME,
														Activity.MODE_PRIVATE);
												SharedPreferences.Editor editor = userinfo
														.edit();
												editor.putInt("time_out",
														time_out);
												editor.commit();
												Map<String, Object> item = new HashMap<String, Object>();
												item.put("title", "Time Out");
												item.put("info", time_out);
												list.set(3, item);
												adapter.notifyDataSetChanged();
											}
										}
									}).setNegativeButton("Cancel", null).show();

				}
				if (list.get(arg2).get("title")
						.equals("Using the true position")) {
					int temp;
					if (if_using_position.equals("NO"))
						temp = 0;
					else
						temp = 1;
					new AlertDialog.Builder(SettingActivity.this)
							.setTitle("Using the true position")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setSingleChoiceItems(new String[] { "NO", "YES" },
									temp,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											SharedPreferences userinfo = getSharedPreferences(
													PREFERENCES_NAME,
													Activity.MODE_PRIVATE);
											SharedPreferences.Editor editor = userinfo
													.edit();
											if (0 == which) {
												editor.putString(
														"if_using_position",
														"NO");
												if_using_position = "NO";
											} else {
												editor.putString(
														"if_using_position",
														"YES");
												if_using_position = "YES";
											}
											Map<String, Object> item = new HashMap<String, Object>();
											item.put("title",
													"Using the true position");
											item.put("info", if_using_position);
											list.set(4, item);
											adapter.notifyDataSetChanged();
											editor.commit();
											dialog.dismiss();
										}
									}).setNegativeButton("Cancel", null).show();
				}
				if (list.get(arg2).get("title").equals("Latitude")) {
					edittxt = new EditText(SettingActivity.this);
					edittxt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
							| android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
					new AlertDialog.Builder(SettingActivity.this)
							.setTitle("Latitude")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(edittxt)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// interval =
											// Integer.valueOf(edittxt.getText().toString());
											if (!(edittxt.getText() == null || ""
													.equals(edittxt.getText()
															.toString()))) {
												latitude = Float
														.valueOf(edittxt
																.getText()
																.toString());
												SharedPreferences userinfo = getSharedPreferences(
														PREFERENCES_NAME,
														Activity.MODE_PRIVATE);
												SharedPreferences.Editor editor = userinfo
														.edit();
												editor.putFloat("latitude",
														(float) latitude);
												editor.commit();
												Map<String, Object> item = new HashMap<String, Object>();
												item.put("title", "Latitude");
												item.put("info", latitude);
												list.set(5, item);
												adapter.notifyDataSetChanged();
											}
										}
									}).setNegativeButton("Cancel", null).show();
				}
				if (list.get(arg2).get("title").equals("Longitude")) {
					edittxt = new EditText(SettingActivity.this);
					edittxt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
							| android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
					new AlertDialog.Builder(SettingActivity.this)
							.setTitle("Longitude")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(edittxt)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// interval =
											// Integer.valueOf(edittxt.getText().toString());
											if (!(edittxt.getText() == null || ""
													.equals(edittxt.getText()
															.toString()))) {
												longitude = Float
														.valueOf(edittxt
																.getText()
																.toString());
												SharedPreferences userinfo = getSharedPreferences(
														PREFERENCES_NAME,
														Activity.MODE_PRIVATE);
												SharedPreferences.Editor editor = userinfo
														.edit();
												editor.putFloat("longitude",
														(float) longitude);
												editor.commit();
												Map<String, Object> item = new HashMap<String, Object>();
												item.put("title", "Longitude");
												item.put("info", longitude);
												list.set(6, item);
												adapter.notifyDataSetChanged();
											}
										}
									}).setNegativeButton("Cancel", null).show();
				}
			}
		});
	}

}
