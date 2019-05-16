package com.juju.droidSpy;
import java.io.*;
import org.apache.http.message.*;
import android.content.*;
import java.text.*;
public class FileAdapter
{
	public FileAdapter()
	{
		
	}
	public void copy(String from,String to)
	{
		File fromFile=new File(from);
		if(fromFile.isFile()){
			copyFile(from,to);
		}
		else
			copyFolder(from,to);
	}
	public static void copyFile(String fromFile, String toFile)
	{
		int byteread=0;
		try{
		File fFile=new File(fromFile);
		if(fFile.exists())
		{
			InputStream inS=new FileInputStream(fFile);
			FileOutputStream outS=new FileOutputStream(toFile);
			byte[] buffer=new byte[2048];
			while((byteread=inS.read(buffer))!=-1)
			{
				outS.write(buffer,0,byteread);
			}
			inS.close();
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return;
	}
	
	public static void copyFile(InputStream fromStream, String toFile)
	{
		int byteread=0;
		try{
			if(!fromStream.equals(null))
			{
				FileOutputStream outS=new FileOutputStream(toFile);
				byte[] buffer=new byte[2048];
				while((byteread=fromStream.read(buffer))!=-1)
				{
					outS.write(buffer,0,byteread);
				}
				fromStream.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return;
	}
	
	public static void copyFolder(String fromFolder,String toFolder)
	{
		try{
		(new File(toFolder)).mkdir();
		File from=new File(fromFolder);
		String[] childrens=from.list();
		File temp=null;
		for(String each:childrens){
			if(fromFolder.endsWith(File.separator)){
				temp=new File(fromFolder+each);
			}
			else{
				temp=new File(fromFolder+File.separator+each);
			}
			if(temp.isFile()){
				copyFile(temp.getAbsolutePath(),toFolder+File.separator+each);
			}
			else{
				copyFolder(fromFolder+File.separator+each,toFolder+File.separator+each);
			}
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void copyFilesFassets(Context context,String oldPath,String newPath) {                    
		try {
			String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
			if (fileNames.length > 0) {//如果是目录
				File file = new File(newPath);
				file.mkdirs();//如果文件夹不存在，则递归
				for (String fileName : fileNames) {
					copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
				}
			} else {//如果是文件
				InputStream is = context.getAssets().open(oldPath);
				FileOutputStream fos = new FileOutputStream(new File(newPath));
				byte[] buffer = new byte[1024];
				int byteCount=0;               
				while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节        
					fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
				}
				fos.flush();//刷新缓冲区
				is.close();
				fos.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//如果捕捉到错误则通知UI线程
			//MainActivity.handler.sendEmptyMessage(COPY_FALSE);
		}                           
	}
	public static String getDataSize(long size){
		DecimalFormat formater = new DecimalFormat("####.00");
		if(size<1024){
			return size+"bytes";
		}else if(size<1024*1024){
			float kbsize = size/1024f;  
			return formater.format(kbsize)+"KB";
		}else if(size<1024*1024*1024){
			float mbsize = size/1024f/1024f;  
			return formater.format(mbsize)+"MB";
		}else if(size<1024*1024*1024*1024){
			float gbsize = size/1024f/1024f/1024f;  
			return formater.format(gbsize)+"GB";
		}else{
			return "size: error";
		}
	}
}
