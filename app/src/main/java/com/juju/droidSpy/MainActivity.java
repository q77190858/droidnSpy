package com.juju.droidSpy;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.io.*;
import android.content.*;
import android.content.res.Resources;
import android.net.Uri;

public class MainActivity extends ListActivity
{
	private MONOAdapter monoAdapter;
    private static final String ROOT_PATH = "/sdcard/";
	private String Current_Path;
	//存储文件名称
	private ArrayList<String> names = null;
	//存储文件路径
	private ArrayList<String> paths = null;
	private View view;
	private EditText editText;
	File assets;
	private ProgressDialog pdialog;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		monoAdapter=new MONOAdapter();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//显示文件列表
		showFileDir(ROOT_PATH);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuinflater=getMenuInflater();
		menuinflater.inflate(R.menu.main_menu,menu);
		// TODO: Implement this method
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		assets= getDir("libmono",MODE_PRIVATE);
		switch(item.getItemId())
		{
			case R.id.main_menuitem1://初始化运行环境
				pdialog=new ProgressDialog(this);
				pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pdialog.setTitle("初始化运行环境...");
				pdialog.setMessage("正在解压文件(约需要2分钟)...");
				pdialog.setCancelable(false);
				pdialog.show();
				new Thread(new Runnable(){
					@Override
					public void run(){
						try{
							FileAdapter.copyFilesFassets(getApplicationContext(),"libmono",assets.getAbsolutePath());
							MONOAdapter.runCmd("chmod 777 "+assets.getAbsolutePath()+File.separator+"init_env.sh");
							String s2=MONOAdapter.runCmd("sh "+assets.getAbsolutePath()+File.separator+"init_env.sh");
							Message msg=new Message();
							msg.obj=s2;
							//msg.what=123;
							myhandler.sendMessage(msg);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}).start();
				break;
			case R.id.main_menuitem2:
				finish();break;
			case R.id.main_menuitem3:	
				showFileDir(Current_Path);break;
		}
		// TODO: Implement this method
		return super.onOptionsItemSelected(item);
	}

	boolean isExit;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO: Implement this method
		if(keyCode==KeyEvent.KEYCODE_BACK)//是否是返回键
		{
			if(!Current_Path.equals("/"))//不是最高目录,返回上级文件夹
			{
				showFileDir(new File(Current_Path).getParent());
			}
			else{
				if(!isExit)//第一次按下返回键,提示退出
				{
					isExit=true;
					displayToast("再按一次返回键退出");
					mhander.sendEmptyMessageDelayed(0,2000);
				}
				else//第二次按下返回键
				{
					finish();
				}
			}
			return false;
		}
		else
			return super.onKeyDown(keyCode, event);
	}
	
	Handler mhander=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method
			super.handleMessage(msg);
			isExit=false;
		}
	};
	
	Handler myhandler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method
			super.handleMessage(msg);
			pdialog.dismiss();
			new AlertDialog.Builder(MainActivity.this).setTitle("运行结果")
				.setMessage((String)msg.obj)
				.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface p1,int p2){}
				}).show();
		}
	};
	
	public static void orderByName(File[] f)
	{
		List<File> files=Arrays.asList(f);
		Collections.sort(files,new Comparator<File>(){
				@Override
				public int compare(File o1,File o2){
					if(o1.isDirectory()&&o2.isFile()){
						return -1;
					}
					if(o1.isFile()&&o2.isDirectory())return 1;
					return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
				}
			});
	}
	
	private void showFileDir(String path){
		Current_Path=path;
		names = new ArrayList<String>();
		paths = new ArrayList<String>();
		File file = new File(path);
		File[] files = file.listFiles();
		orderByName(files);
		//如果当前目录不是根目录
		if (!ROOT_PATH.equals(path)){
			names.add("@1");
			paths.add(ROOT_PATH);
			names.add("@2");
			paths.add(file.getParent());
		}
		//添加所有文件
		for (File f : files){
			names.add(f.getName());
			paths.add(f.getPath());
		}
		this.setListAdapter(new MyAdapter(this,names,paths));
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String path = paths.get(position);
		File file = new File(path);
		// 文件存在并可读
		if (file.exists() && file.canRead()){
			if (file.isDirectory()){
				//显示子目录及文件
				showFileDir(path);
			}
			else{
				//处理文件
				fileHandle(file);
			}
		}
		//没有权限
		else{
			Resources res = getResources();
			new AlertDialog.Builder(this).setTitle("Message")
				.setMessage(res.getString(R.string.no_permission))
				.setPositiveButton("OK",new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
		}
		super.onListItemClick(l, v, position, id);
	}
	//对文件进行增删改
	private void fileHandle(final File file){
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 打开文件
				if (which == 0){
					openFile(file);
				}
				//修改文件名
				else if(which == 1){
					LayoutInflater factory = LayoutInflater.from(MainActivity.this);
					view = factory.inflate(R.layout.rename_dialog, null);
					editText = (EditText)view.findViewById(R.id.editText);
					editText.setText(file.getName());
					DialogInterface.OnClickListener listener2 = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String modifyName = editText.getText().toString();
							final String fpath = file.getParentFile().getPath();
							final File newFile = new File(fpath + "/" + modifyName);
							if (newFile.exists()){
								//排除没有修改情况
								if (!modifyName.equals(file.getName())){
									new AlertDialog.Builder(MainActivity.this)
										.setTitle("注意!")
										.setMessage("文件名已存在，是否覆盖？")
										.setPositiveButton("确定", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												if (file.renameTo(newFile)){
													showFileDir(fpath);
													displayToast("重命名成功！");
												}
												else{
													displayToast("重命名失败！");
												}
											}
										})
										.setNegativeButton("取消", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
											}
										})
										.show();
								}
							}
							else{
								if (file.renameTo(newFile)){
									showFileDir(fpath);
									displayToast("重命名成功！");
								}
								else{
									displayToast("重命名失败！");
								}
							}
						}
					};
					AlertDialog renameDialog = new AlertDialog.Builder(MainActivity.this).create();
					renameDialog.setView(view);
					renameDialog.setButton("确定", listener2);
					renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
						});
					renameDialog.show();
				}
				//删除文件
				else if(which==2){
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("注意!")
						.setMessage("确定要删除此文件吗？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(file.delete()){
									//更新文件列表
									showFileDir(file.getParent());
									displayToast("删除成功！");
								}
								else{
									displayToast("删除失败！");
								}
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				}
				//反编译Assembly
				else
				{
					if(file.getName().endsWith(".exe")||file.getName().endsWith(".dll"))
					{
						pdialog=new ProgressDialog(MainActivity.this);
						pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pdialog.setTitle("正在反编译...");
						pdialog.setMessage("请耐心等待...");
						pdialog.setCancelable(false);
						pdialog.show();
						new Thread(new Runnable(){
							@Override
							public void run(){
								try{
									String s= monoAdapter.Decompiler(file,MainActivity.this);
									Message msg=new Message();
									msg.obj=s;
									//msg.what=123;
									myhandler.sendMessage(msg);
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
						}).start();


					}
					else
					{
						Toast.makeText(getApplicationContext(),"Wrong Format!",Toast.LENGTH_LONG).show();
					}
				}
			}
		};
		//选择文件时，弹出增删该操作选项对话框
		String[] menu = {"打开文件","重命名","删除文件",".NET反编译该Assembly"};
		new AlertDialog.Builder(MainActivity.this)
			.setTitle("请选择要进行的操作!")
			.setItems(menu, listener)
			.setPositiveButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
	}
	//打开文件
	private void openFile(File file){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		startActivity(intent);
	}
	//获取文件mimetype
	private String getMIMEType(File file){
		String type = "";
		String name = file.getName();
		//文件扩展名
		String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")){
			type = "audio";
		}
		else if(end.equals("mp4") || end.equals("3gp")) {
			type = "video";
		}
		else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")){
			type = "image";
		}
		else {
			//如果无法直接打开，跳出列表由用户选择
			type = "*";
		}
		type += "/*";
		return type;
	}
	private void displayToast(String message){
		Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
	}
}
