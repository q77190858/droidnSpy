package com.juju.droidSpy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.widget.*;
import junit.framework.*;

public class MONOAdapter
{
	public MONOAdapter(){
		
	}
	
	public String Decompiler(File f,Context ct){
		InputStream is=null;
		String appdir=ct.getFilesDir().getPath();
		appdir=appdir.substring(0,appdir.length()-6);
		ProcessBuilder pb=new ProcessBuilder("sh",appdir+File.separator+"app_libmono/mono.sh",appdir+File.separator+"app_libmono/dnSpy/dnSpy.Console.exe",f.getAbsolutePath(),"-o",f.getParent());
		pb.directory(f.getParentFile());
		Map<String,String> map= pb.environment();
		map.put("LD_LIBRARY_PATH",appdir+"/app_libmono");
		pb.redirectErrorStream(true);
		try{
		Process p=pb.start();
		is=p.getInputStream();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		byte[] buffer=new byte[1024];
		StringBuffer sb=new StringBuffer();
		int len=-1;
		try{
		  while((len=is.read(buffer))!=-1)
		  {
			  sb.append(new String(buffer,0,len));
		  }
		}
		catch(IOException e){
			
		}
		return sb.toString();
	}
	
	public static String runCmd(File cd, String[] cmd){
		InputStream is=null;
		ProcessBuilder pb=new ProcessBuilder(cmd);
		pb.directory(cd);
		pb.redirectErrorStream(true);
		try{
			Process p=pb.start();
			is=p.getInputStream();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		byte[] buffer=new byte[1024];
		StringBuffer sb=new StringBuffer();
		int len=-1;
		try{
			while((len=is.read(buffer))!=-1)
			{
				sb.append(new String(buffer,0,len));
			}
		}
		catch(IOException e){

		}
		return sb.toString();
	}
	
	public static String runCmd(String cmd){
		InputStream is=null;
		try{
			Process p=Runtime.getRuntime().exec(cmd);
			is=p.getInputStream();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		byte[] buffer=new byte[1024];
		StringBuffer sb=new StringBuffer();
		int len=-1;
		try{
			while((len=is.read(buffer))!=-1)
			{
				sb.append(new String(buffer,0,len));
			}
		}
		catch(IOException e){

		}
		return sb.toString();
	}
}
