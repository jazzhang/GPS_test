package com.example.gps_test;


import  java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Build;
import android.util.Log;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;



/**
 * This class is the base class of a Test
 * @author 
 */

public class Excel_operation {	
	private WritableWorkbook workbook;
	WritableSheet sheet; 
	String logpath = "/sdcard/gpstest";
	FileOutputStream fw = null;	
	int current_index = 1;
	Map<String,String> TTFFData = new HashMap<String,String>();
	boolean if_use_true_position = false;
	float latitude = 0;
	float longtitude = 0;
	ArrayList<Float> TTFF= new ArrayList<Float>();
	ArrayList<Float> error= new ArrayList<Float>();
	
	public void create_workbook(){						
		create_xls_file();
		try {
			workbook = Workbook.createWorkbook(fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sheet  =  workbook.createSheet( "statistics" ,  0 );
		write_first_raw();
		current_index = 1;
	}
	
	public void create_xls_file(){
		File excellogfile;
        File excelfilepath;
 	    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
 	    String nowTime=format.format(new Date());
 	    Build bd = new Build();
 	    String model = bd.MODEL;
 	    excelfilepath = new File(logpath);
  	    if(!excelfilepath.exists()){
  	    	excelfilepath.mkdirs();
 		    }   	
  	    excellogfile =new File(logpath,model+"_TTFF_EXCEL_"+nowTime+".xls");  ///create log file
 	        if(!excellogfile.exists()){
 		        try {
 		        	excellogfile.createNewFile();
 		        } catch (IOException e) {
 			    e.printStackTrace();
 		        }
 		    }	
 	        try {
 	    	    fw = new FileOutputStream(excellogfile);
 	        } catch (FileNotFoundException e1) {
 		        e1.printStackTrace();
 	        }		
	}
	
	public void get_true_position(String if_use, float lat, float lon){
		if(if_use.equals("NO")) 
		    if_use_true_position = false;
		else if(if_use.equals("YES")){
			if_use_true_position = true;
		    latitude = lat;
		    longtitude = lon;
		}
	}
	
	public float get_distance(float x1,float y1,float x2,float y2){
        float x = (float) (Math.toRadians(x1) -  Math.toRadians(x2));
        float y = (float) (Math.toRadians(y1) -  Math.toRadians(y2));
        float dis = (float) (2 * Math.asin(Math.sqrt(Math.pow(Math.sin(x/2),2) +
        		Math.cos(Math.toRadians(x1)) * Math.cos(Math.toRadians(x2)) * Math.pow(Math.sin(y/2),2))));
		dis = (float) (dis * 6378.137 * 1000);
        return dis;
	}
	
	
	public void process_svdata(ArrayList<Map<String,String>> svdata){
		int sv_in_use = 0, sv_in_view = 0;
		float top4_avg_gps_cno = 0,top4_avg_cno =0;
		ArrayList<Float> GPS_CN0Data= new ArrayList<Float>();
		ArrayList<Float> CN0Data= new ArrayList<Float>();
		for(int i=1;i<svdata.size();i++){
			Map<String,String> item = svdata.get(i);
			sv_in_view++;
			if(Boolean.parseBoolean(item.get("used_in_fix")))
				sv_in_use++;
			CN0Data.add(Float.valueOf(item.get("snr")));
			if(Integer.valueOf(item.get("prn")) < 40)
				GPS_CN0Data.add(Float.valueOf(item.get("snr")));
		}
		Collections.sort(CN0Data);
		Collections.sort(GPS_CN0Data);
		float sum = 0;
		int i;
		for(i=0; i<4 && i<CN0Data.size();i++){
			sum += CN0Data.get(CN0Data.size()-1-i);
		}
		top4_avg_cno = sum/i;
		sum = 0;
		for(i=0; i<4 && i<GPS_CN0Data.size();i++){
			sum += GPS_CN0Data.get(GPS_CN0Data.size()-1-i);
		}
		top4_avg_gps_cno = sum/i;		
		TTFFData.put("sv_in_use", Integer.toString(sv_in_use));
		TTFFData.put("sv_in_view", Integer.toString(sv_in_view));
		TTFFData.put("top4_avg_cno", String.format("%.2f", top4_avg_cno));
		TTFFData.put("top4_avg_gps_cno", String.format("%.2f", top4_avg_gps_cno));
	}
	
	public void read_location(ArrayList<String> location){
		 TTFFData.put("latitude", location.get(0));
		 TTFFData.put("longtitude", location.get(1));
		 TTFFData.put("altitude", location.get(2));
		 TTFFData.put("accuracy", location.get(3));
		 TTFFData.put("TTFF", location.get(4));
		 TTFF.add(Float.valueOf(location.get(4)));
		 if(if_use_true_position){
			 float err = get_distance(Float.valueOf(location.get(0)),Float.valueOf(location.get(1)),
					 latitude,longtitude);
			 TTFFData.put("error", String.format("%.2f", err));
			 Log.d("GPSTEST_error=",String.valueOf(err));
			 error.add(err);
		 }
		
	}
	
	public void write_first_raw(){		
		try {
			WritableCell temp; 
			temp = new Label(0, 0, "NO.");
            sheet.addCell(temp);
			temp = new Label(1, 0, "TTFF");
            sheet.addCell(temp);
			temp = new Label(2, 0, "LONGTITUDE");
            sheet.addCell(temp);
			temp = new Label(3, 0, "LATITUDE");
            sheet.addCell(temp);
			temp = new Label(4, 0, "ALTITUDE");
            sheet.addCell(temp);
			temp = new Label(5, 0, "ACCURACY");
            sheet.addCell(temp);
			temp = new Label(6, 0, "IN_USE");
            sheet.addCell(temp);
			temp = new Label(7, 0, "IN_VIEW");
            sheet.addCell(temp);
			temp = new Label(8, 0, "TOP4_GPS_CN0");
            sheet.addCell(temp);
		    temp = new Label(9, 0, "TOP4_ALL_CN0");
            sheet.addCell(temp);
			temp = new Label(10, 0, "TIME_OUT");
            sheet.addCell(temp);
			temp = new Label(11, 0, "ERROR");
            sheet.addCell(temp);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	public void write_info(ArrayList<String> location,ArrayList<Map<String,String>> svdata){
		 process_svdata(svdata);
		 read_location(location);
		 //Log.d("GPSTEST_EXCEL INDEX =",Integer.toString(current_index));
		 //Log.d("GPSTEST_EXCEL TTFF =",TTFFData.get("TTFF"));
		 //Log.d("GPSTEST_EXCEL TTFF =",TTFFData.get("top4_avg_gps_cno"));
			try {
				WritableCell temp; 
				temp = new Label(0, current_index, Integer.toString(current_index));
	            sheet.addCell(temp);
	            temp = new Label(1, current_index, TTFFData.get("TTFF"));
	            sheet.addCell(temp);
				temp = new Label(2, current_index, TTFFData.get("longtitude"));
	            sheet.addCell(temp);
				temp = new Label(3, current_index, TTFFData.get("latitude"));
	            sheet.addCell(temp);
				temp = new Label(4, current_index, TTFFData.get("altitude"));
	            sheet.addCell(temp);
				temp = new Label(5, current_index, TTFFData.get("accuracy"));
	            sheet.addCell(temp);
				temp = new Label(6, current_index, TTFFData.get("sv_in_use"));
	            sheet.addCell(temp);
				temp = new Label(7, current_index, TTFFData.get("sv_in_view"));
	            sheet.addCell(temp);
				temp = new Label(8, current_index, TTFFData.get("top4_avg_gps_cno"));
	            sheet.addCell(temp);
				temp = new Label(9, current_index, TTFFData.get("top4_avg_cno"));
	            sheet.addCell(temp);
			    temp = new Label(10, current_index, " ");
	            sheet.addCell(temp);
	            if(if_use_true_position){
	            	temp = new Label(11, current_index, TTFFData.get("error"));
	                sheet.addCell(temp);		
	            }
            
	            current_index++;
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
	}
	
	public void write_statistic(){
		if(TTFF.size() == 0)
			return;
		current_index = current_index + 2;
		Collections.sort(TTFF);
		Collections.sort(error);
        try {
    		WritableCell temp; 
    		temp = new Label(1, current_index, "MIN");
			sheet.addCell(temp);
    		temp = new Label(2, current_index, "MAX");
			sheet.addCell(temp);
    		temp = new Label(3, current_index, "AVG");
			sheet.addCell(temp);
    		temp = new Label(4, current_index, "CEP50");
			sheet.addCell(temp);
    		temp = new Label(5, current_index, "CEP68");
			sheet.addCell(temp);
    		temp = new Label(6, current_index, "CEP95");
			sheet.addCell(temp);
			current_index++;
    		temp = new Label(0, current_index, "TTFF(s)");
			sheet.addCell(temp);
    		temp = new Label(1, current_index, Float.toString(TTFF.get(0)));
			sheet.addCell(temp);
    		temp = new Label(2, current_index, String.format("%.2f",TTFF.get(TTFF.size()-1)));
			sheet.addCell(temp);
    		temp = new Label(3, current_index, String.format("%.2f",average(TTFF)));
			sheet.addCell(temp);
    		temp = new Label(4, current_index, String.format("%.2f",TTFF.get((int) Math.floor(TTFF.size()/2))));
			sheet.addCell(temp);
    		temp = new Label(5, current_index, String.format("%.2f",TTFF.get((int) Math.floor(TTFF.size()*0.68))));
			sheet.addCell(temp);
    		temp = new Label(6, current_index, String.format("%.2f",TTFF.get((int) Math.floor(TTFF.size()*0.95))));
    		sheet.addCell(temp);
    		if(if_use_true_position){
    			current_index++;
        		temp = new Label(0, current_index, "Error(m)");
    			sheet.addCell(temp);
        		temp = new Label(1, current_index,String.format("%.2f",error.get(0)));
    			sheet.addCell(temp);
        		temp = new Label(2, current_index, String.format("%.2f",error.get(error.size()-1)));
    			sheet.addCell(temp);
        		temp = new Label(3, current_index, String.format("%.2f",average(error)));
    			sheet.addCell(temp);
        		temp = new Label(4, current_index, String.format("%.2f",error.get((int) Math.floor(error.size()/2))));
    			sheet.addCell(temp);
        		temp = new Label(5, current_index, String.format("%.2f",error.get((int) Math.floor(error.size()*0.68))));
    			sheet.addCell(temp);
        		temp = new Label(6, current_index, String.format("%.2f",error.get((int) Math.floor(error.size()*0.95))));
        		sheet.addCell(temp);
    		}
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}		

        
	}
	
	
	public Float average(ArrayList<Float> array){
		double sum = 0;
		int cnt = 0;
		for(int i=0;i<array.size();i++){
				sum+=array.get(i);
				cnt++;
		}
		Float ave = (float) (sum / cnt);
		return ave;
	}
	
	
	public void write_info_time_out(int time_out){
		try 
		{
		WritableCell temp;  
		temp = new Label(0, current_index, Integer.toString(current_index));
        sheet.addCell(temp);
        temp = new Label(1, current_index, Integer.toString(time_out));
        sheet.addCell(temp);
	    temp = new Label(10, current_index, "Y");
        sheet.addCell(temp);
	    } catch (RowsExceededException e) {
		    e.printStackTrace();
	    } catch (WriteException e) {
		    e.printStackTrace();
	    }
        current_index++;
	}
	
	public void close_xls_file(){
		try {
			write_statistic();
			workbook.write();
			workbook.close();
			fw.close();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
}
