package com.example.gps_test;



import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity{

	

	ArrayList<String> Data= new ArrayList<String>();
	SimpleAdapter adapter;
	private ListView listView;

	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		listView = (ListView)findViewById(R.id.listview_main);
		Data.add("Tracking Test");
		Data.add("TTFF Test");
		//Data.add("H/W CN0 Test");
		Data.add("Settings");
		Data.add("About");
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,Data));
//        setContentView(listView);
        
        listView.setOnItemClickListener(new OnItemClickListener(){
        	 
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                if(Data.get(arg2).equals("Tracking Test"))
                {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, TrackingActivity.class);
                    startActivity(intent);
                }
                if(Data.get(arg2).equals("TTFF Test"))
                {
                	Intent intent = new Intent();
                    intent.setClass(MainActivity.this, TTFFActivity.class);
                    startActivity(intent);

                }
                if(Data.get(arg2).equals("Settings"))
                {
                	Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SettingActivity.class);
                    startActivity(intent);

                }
                if(Data.get(arg2).equals("About"))
                {

                }

	        }
        });
	}
}


	