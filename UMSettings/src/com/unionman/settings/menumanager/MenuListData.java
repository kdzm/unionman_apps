package com.unionman.settings.menumanager;

import android.content.Context;

import com.unionman.settings.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuListData {
	Context ctx;

	public MenuListData(Context paramContext) {
		this.ctx = paramContext;
	}

	public List<Map<String, Object>> getMenuData() {
		ArrayList localArrayList = new ArrayList();
		
		//设备信息
		HashMap localHashMap1 = new HashMap();
		localHashMap1.put("title",this.ctx.getResources().getString(R.string.menu_about));
		localHashMap1.put("pic", R.drawable.devinfor);
		localHashMap1.put("pic2", R.drawable.devinfor2);
		localArrayList.add(localHashMap1);

		//网络设置
		HashMap localHashMap2 = new HashMap();
		localHashMap2.put("title",this.ctx.getResources().getString(R.string.menu_network));
		localHashMap2.put("pic",R.drawable.netset);
		localHashMap2.put("pic2",R.drawable.netset2);
		localArrayList.add(localHashMap2);

		//网络信息
		HashMap localHashMap3 = new HashMap();
		localHashMap3.put("title",this.ctx.getResources().getString(R.string.menu_networkinfo));
		localHashMap3.put("pic",R.drawable.netinfo);
		localHashMap3.put("pic2",R.drawable.netinfo2);
		localArrayList.add(localHashMap3);

		//日期和时间
		HashMap localHashMap4 = new HashMap();
		localHashMap4.put("title",this.ctx.getResources().getString(R.string.menu_date));
		localHashMap4.put("pic",R.drawable.dataset);
		localHashMap4.put("pic2",R.drawable.dataset2);
		localArrayList.add(localHashMap4);

		//显示器设置 改为  节能设置
		HashMap localHashMap5 = new HashMap();
		localHashMap5.put("title",this.ctx.getResources().getString(R.string.menu_power_save));
		localHashMap5.put("pic",R.drawable.showset);
		localHashMap5.put("pic2",R.drawable.showset2);
		localArrayList.add(localHashMap5);

		//播放器设置 改为  系统设置
		HashMap localHashMap6 = new HashMap();
	    localHashMap6.put("title",this.ctx.getResources().getString(R.string.menu_sys_setting));
		localHashMap6.put("pic",R.drawable.dont);
		localHashMap6.put("pic2",R.drawable.dont2);
		localArrayList.add(localHashMap6);		
		
		//网络检测
		HashMap localHashMap7 = new HashMap();
		localHashMap7.put("title",this.ctx.getResources().getString(R.string.menu_net_check));
		localHashMap7.put("pic",R.drawable.netinfo);
		localHashMap7.put("pic2",R.drawable.netinfo2);
		localArrayList.add(localHashMap7);

		//密码保护
		HashMap localHashMap8 = new HashMap();
		localHashMap8.put("title",this.ctx.getResources().getString(R.string.menu_pwdprotected));
		localHashMap8.put("pic",R.drawable.secretsave);
		localHashMap8.put("pic2",R.drawable.secretsave2);
		localArrayList.add(localHashMap8);

		//系统升级
		HashMap localHashMap9 = new HashMap();
		localHashMap9.put("title",this.ctx.getResources().getString(R.string.menu_upgrade));
		localHashMap9.put("pic",R.drawable.systemupgrade);
		localHashMap9.put("pic2",R.drawable.systemupgrade2);
		localArrayList.add(localHashMap9);

		//管理应用程序
		HashMap localHashMap10 = new HashMap();
		localHashMap10.put("title",this.ctx.getResources().getString(R.string.menu_app));
		localHashMap10.put("pic",R.drawable.appmanage);
		localHashMap10.put("pic2",R.drawable.appmanage2);
		localArrayList.add(localHashMap10);

		//恢复出厂设置
		HashMap localHashMap11 = new HashMap();
		localHashMap11.put("title",this.ctx.getResources().getString(R.string.menu_reset));
		localHashMap11.put("pic",R.drawable.backfactery);
		localHashMap11.put("pic2",R.drawable.backfactery2);
		localArrayList.add(localHashMap11);

		//背景设置
//		HashMap localHashMap12 = new HashMap();
//		localHashMap12.put("title",this.ctx.getResources().getString(R.string.wallpaper));
//		localHashMap12.put("pic",R.drawable.backset);
//		localHashMap12.put("pic2",R.drawable.backset2);
//		localArrayList.add(localHashMap12);

		//定时关机
		HashMap localHashMap13 = new HashMap();
		localHashMap13.put("title",this.ctx.getResources().getString(R.string.shutdown));
		localHashMap13.put("pic",R.drawable.timeset);
		localHashMap13.put("pic2",R.drawable.dataset2);
		localArrayList.add(localHashMap13);

		//语言设置
//		HashMap localHashMap14 = new HashMap();
//		localHashMap14.put("title", this.ctx.getResources().getString(R.string.menu_language));
//		localHashMap14.put("pic", R.drawable.languageset2);
//		localHashMap14.put("pic2", R.drawable.languageset);
//		localArrayList.add(localHashMap14);

		//业务信息
		HashMap localHashMap15 = new HashMap();
		localHashMap15.put("title", this.ctx.getResources().getString(R.string.service_info));
		localHashMap15.put("pic", R.drawable.service_info);
		localHashMap15.put("pic2", R.drawable.service_info2);
		localArrayList.add(localHashMap15);

		return localArrayList;
		
		/*
		 * HashMap localHashMap11 = new HashMap(); localHashMap11.put("title",
		 * this.ctx.getResources().getString(R.string.menu_language));
		 * localArrayList.add(localHashMap11);
		 */


	}
}