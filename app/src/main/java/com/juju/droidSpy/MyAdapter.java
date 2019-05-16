package com.juju.droidSpy;

import android.view.*;
import android.content.*;
import android.widget.*;
import android.graphics.*;
import java.util.*;
import java.io.*;
import android.widget.Toolbar.*;
import android.text.*;

public class MyAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private Bitmap directory,file;
	//存储文件名称
	private ArrayList<String> names = null;
	//存储文件路径
	private ArrayList<String> paths = null;
	//参数初始化
	public MyAdapter(Context context,ArrayList<String> na,ArrayList<String> pa){
		names = na;
		paths = pa;
		directory = BitmapFactory.decodeResource(context.getResources(),R.drawable.d);
		file = BitmapFactory.decodeResource(context.getResources(),R.drawable.f);
		//缩小图片
		//directory = small(directory,0.16f);
		//file = small(file,0.1f);
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return names.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (null == convertView){
			convertView = inflater.inflate(R.layout.file,parent,false);
			holder = new ViewHolder();
			holder.text = (TextView)convertView.findViewById(R.id.textView);
			holder.image = (ImageView)convertView.findViewById(R.id.imageView);
			holder.size=(TextView)convertView.findViewById(R.id.TextView_size);
			holder.modified=(TextView)convertView.findViewById(R.id.TextView_modified);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		File f = new File(paths.get(position).toString());
		if (names.get(position).equals("@1")){
			holder.text.setText("/");
			holder.image.setImageBitmap(directory);
		}
		else if (names.get(position).equals("@2")){
			holder.text.setText("..");
			holder.image.setImageBitmap(directory);
		}
		else{
			holder.text.setText(f.getName());
			Calendar calendar=Calendar.getInstance();
			calendar.setTimeInMillis(f.lastModified());
			holder.modified.setText(calendar.getTime().toLocaleString());
			if (f.isDirectory()){
				holder.image.setImageBitmap(directory);
				holder.size.setText("文件夹");
			}
			else if (f.isFile()){
				holder.image.setImageBitmap(file);
				holder.size.setText(FileAdapter.getDataSize(f.length()));
			}
			else{
				System.out.println(f.getName());
			}
		}
		return convertView;
	}
	private class ViewHolder{
		private TextView text;
		private TextView size;
		private TextView modified;
		private ImageView image;
	}
	private Bitmap small(Bitmap map,float num){
		Matrix matrix = new Matrix();
		matrix.postScale(num, num);
		return Bitmap.createBitmap(map,0,0,map.getWidth(),map.getHeight(),matrix,true);
	}
}
