package com.um.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.um.controller.AppAdapter;
import com.um.controller.AppList;
import com.um.dvb.R;
import com.um.dvbstack.ProgList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

public class AndoroidApp extends Activity
{
	AppAdapter myAdapter;
	public static final int FILTER_ALL_APP = 0; // ����Ӧ�ó���
	public static final int FILTER_SYSTEM_APP = 1; // ϵͳ����
	public static final int FILTER_THIRD_APP = 2; // ����Ӧ�ó���
	public static final int FILTER_SDCARD_APP = 3; // ��װ��SDCard��Ӧ�ó���
	public static final int FILTER_SYSTEM_AND_THIRD_APP = 4; // ����ϵͳ���� �͵���Ӧ�ó���
	
	private int filter = FILTER_SDCARD_APP; 
	
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
	super.onCreate(savedInstanceState); 
	setContentView(R.layout.androidapp); 
	ActionBar actionBar = getActionBar(); 
    //actionBar.hide();
    actionBar.setTitle(R.string.android_app);
	
	if(getIntent()!=null)
	{
		filter = getIntent().getIntExtra("filter", FILTER_SYSTEM_AND_THIRD_APP) ;
	}

	//�õ�PackageManager����
	PackageManager pm = getPackageManager();
	//�õ�ϵͳ��װ�����г�����PackageInfo����          
	List<PackageInfo> packs = pm.getInstalledPackages(0); 
	
	
	GridView gridview =(GridView)findViewById(R.id.jmz_gridView1);
	AppList alist = AppList.GetInstance();
	ArrayList<HashMap<String,Object>> menulist = new ArrayList<HashMap<String,Object>>();
	
	//������
//	Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);  
//	mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	// ͨ���ѯ���������ResolveInfo����
//	List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);  
	// ����ϵͳ���� �� ���name����   
	// ���������Ҫ������ֻ����ʾϵͳӦ�ã������г����Ӧ�ó���
//	Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));  
//	for (ResolveInfo pi : resolveInfos)
//	{ 	
//		HashMap<String,Object> map =new HashMap<String,Object>();
//		map.put("name", (String) pi.loadLabel(pm));//Ӧ�ó������   
//		map.put("image", pi.loadIcon(pm));//ͼ��   
//		map.put("app_package",pi.activityInfo.packageName);
//		menulist.add(map);	
//	}
	
	//����һ
//	for(PackageInfo pi:packs)
//	{
		//�⽫����ʾ���а�װ��Ӧ�ó��򣬰�(ϵͳӦ�ó���      
//		HashMap<String,Object> map =new HashMap<String,Object>();
//		map.put("name", pi.applicationInfo.loadLabel(pm));//Ӧ�ó������   
//		map.put("image", pi.applicationInfo.loadIcon(pm));//ͼ��   
//		map.put("app_package",pi.packageName);
//		menulist.add(map);	
//	}
	
	//������
	menulist = queryFilterAppInfo(filter); // ��ѯ����Ӧ�ó�����Ϣ
	
	
	alist.list = menulist;
	myAdapter = new AppAdapter(this, menulist,R.layout.androidapp_sub,
			new String[]{"name","image"},new int[]{R.id.androidapp_name1,R.id.androidapp_imageView1});
//	SimpleAdapter sadapte= new SimpleAdapter(this, menulist,R.layout.jmz_test_sub,
//			new String[]{"name","image"},new int[]{R.id.jmz_nametext1,R.id.jmz_imageView1});

	gridview.setAdapter(myAdapter);
	
	
	gridview.setOnItemClickListener(new OnItemClickListener(){

		//@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
		{
			openApp(arg2);
		}
				
	});
	 	
	gridview.setOnKeyListener(new OnKeyListener()
	{

		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			switch (keyCode)
			{
			case KeyEvent.KEYCODE_DPAD_CENTER:
				if (event.getAction() == KeyEvent.ACTION_UP)
				{
					int pos;
					pos = ((GridView) v).getSelectedItemPosition();
					openApp(pos);
					return true;
				}	
			default:
				
				break;
			}
			
			return false;
			// return super.onKeyDown(keyCode, event);

		}

	});

	
	}
	
	// ��ݲ�ѯ�������ѯ�ض���ApplicationInfo
	private ArrayList<HashMap<String,Object>> queryFilterAppInfo(int filter) {
			PackageManager pm = getPackageManager();
			
			// ��ѯ�����Ѿ���װ��Ӧ�ó���
			List<ApplicationInfo> listAppcations = pm
					.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			Collections.sort(listAppcations,
					new ApplicationInfo.DisplayNameComparator(pm));// ����
			ArrayList<HashMap<String,Object>> appInfos = new ArrayList<HashMap<String,Object>>(); // ������˲鵽��AppInfo
			// ������4����
			switch (filter) {
			case FILTER_ALL_APP: // ����Ӧ�ó���
				
				for (ApplicationInfo app : listAppcations) {
					appInfos.add(getAppInfo(app));
					//System.out.println("jmz111111,packagename="+app.packageName);
				}
				return appInfos;
			case FILTER_SYSTEM_APP: // ϵͳ����
				
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
						appInfos.add(getAppInfo(app));
					}
				}
				return appInfos;
			case FILTER_THIRD_APP: // ����Ӧ�ó���
				
				for (ApplicationInfo app : listAppcations) {
					//��ϵͳ����
					if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
						appInfos.add(getAppInfo(app));
					} 
					//��4��ϵͳ���򣬱��û��ֶ����º󣬸�ϵͳ����Ҳ��Ϊ����Ӧ�ó�����
					else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
						appInfos.add(getAppInfo(app));
					}
				}
				break;
			case FILTER_SYSTEM_AND_THIRD_APP: // ����ϵͳ���� �͵���Ӧ�ó���
				
				for (ApplicationInfo app : listAppcations) {
					//��ϵͳ����
					if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
						if(!delPartApp(app))
							appInfos.add(getAppInfo(app));
					} 
					//��4��ϵͳ���򣬱��û��ֶ����º󣬸�ϵͳ����Ҳ��Ϊ����Ӧ�ó�����
					else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
						appInfos.add(getAppInfo(app));
					}
					else if(addPartSystemApp(app))
					{
						appInfos.add(getAppInfo(app));						
					}
				}
				break;
								
			case FILTER_SDCARD_APP: // ��װ��SDCard��Ӧ�ó���
				
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
						appInfos.add(getAppInfo(app));
					}
					
				}
				return appInfos;
			default:
				return null;
			}
			return appInfos;
		}
		
	private boolean delPartApp(ApplicationInfo app )
	{
		
		String[] s={"com.sample.livewallpaper.bokehrainbow",
				    "com.expertiseandroid.wallpaper.heart",
				    "org.clangen.gfx.plasma",				   				    
		};
		for(int i=0;i<s.length;i++)
		{
			if(app.packageName.equals(s[i]))
				return true;
		}
		return false;
	}
		private boolean addPartSystemApp(ApplicationInfo app )
		{
			
			String[] s={"com.android.settings",
					    "com.android.browser",
					    "com.android.email",
					    "com.android.calculator2",
					    "com.android.calendar",
					    "com.android.deskclock",
					    "com.android.quicksearchbox",
					    "com.networkupgrad.activity",
					    "com.android.speechrecorder",
					    "com.explorer",
					    "com.android.camera",
					    "com.android.music",
					    "com.android.vending",
					    "com.android.providers.downloads.ui",
					    "com.huawei.activity",
					    
			};
			for(int i=0;i<s.length;i++)
			{
				if(app.packageName.equals(s[i]))
					return true;
			
			}
			return false;
		}
	
	
	// ����һ��AppInfo���� ������ֵ
		private HashMap<String,Object> getAppInfo(ApplicationInfo app) {
			HashMap<String,Object> map =new HashMap<String,Object>();
			
			map.put("name", (String) app.loadLabel(getPackageManager()));//Ӧ�ó������   
			//System.out.println("jmz222,name ="+app.loadLabel(getPackageManager()));
			map.put("image", app.loadIcon(getPackageManager()));//ͼ��   
			map.put("app_package",app.packageName);
			//System.out.println("jmz333,packagename="+app.packageName);
			return map;
		}
	
	public void openApp(int pos)
	{
		PackageManager pm1 = getPackageManager();
		AppList alist1 = AppList.GetInstance();
		Intent intent;
		intent = pm1.getLaunchIntentForPackage(alist1.list.get(pos).get("app_package").toString());
		AndoroidApp.this.startActivity(intent);  
	}


}
