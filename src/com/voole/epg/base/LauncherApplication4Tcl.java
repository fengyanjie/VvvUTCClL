package com.voole.epg.base;

import java.lang.reflect.Field;

import com.voole.epg.LauncherApplication;
import com.voole.epg.corelib.model.auth.AuthManager;
import com.voole.epg.corelib.model.proxy.ProxyManager;
import com.voole.epg.corelib.model.utils.LogUtil;
import com.voole.epg.f4k_download.utils.F4kDownResourceUtils;
import com.voole.epg.f4k_download.utils.StandardAuth;
import com.voole.epg.f4k_download.utils.StandardProxy;
import com.voole.epg.model.play.PlayManager;


public class LauncherApplication4Tcl extends LauncherApplication{
	

	@Override
	public void onCreate() {
		LogUtil.d("LauncherApplication4Tcl-->onCreate");
		initAuthAndProxy();
		initPlayManager();
		super.onCreate();
		
	}
	
	private void initPlayManager() {
		try {
			PlayManager manager = PlayManager.GetInstance();
			Field mMagicClassName = manager.getClass().getDeclaredField("mMagicClassName");
			mMagicClassName.setAccessible(true);
			mMagicClassName.set(manager, MyMagicActivity.class.getName());
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	private void initAuthAndProxy() {
		F4kDownResourceUtils.getInstance().initContext(this);
//		DownloadResourceManager.getInstance().initContext(this);
		AuthManager.GetInstance().init(new StandardAuth(this));
		ProxyManager.GetInstance().init(new StandardProxy(this));
	}
}
