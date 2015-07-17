package com.voole.epg.base;

import android.widget.Toast;

import com.voole.tvutils.PropertiesUtil;

public class Config {
	public static final String TAG = "VooleEpg2.0_tcl";
	public static final String AUTH_PORT = "28080";
	public static final String AUTH_CONF = "vooleauth.conf";
	public static final String AUTH_RT_CONF = "voolert.conf";
	public static final String AUTH_FILE = "vooleauthex";
	public static final String PROXY_PORT = "5658";
	public static final String VOOLE_SHARE = "voole";
	public static final String PROXY_FILE = "youpengagent";
	private static Config instance = new Config();

	private Config() {

	}

	public static Config GetInstance() {
		return instance;
	}
	
	public enum KeyCodeType {
		Standard;

		public static KeyCodeType getType(String str) {
			if ("".equals(str)) {
				str = "Standard";
			}
			return valueOf(str);
		}
	}
	
	public enum OemType {
		Standard,
		Hisense,
		TP_Link,
		TCL,
		KONKA_896;
		
		public static OemType getOemType(String str){
			if ("".equals(str)) {
				str = "Standard";
			}
			OemType type = null;
			try{
			type = valueOf(str);
			}catch(Exception e){
				Toast.makeText(LauncherApplication4Tcl.GetInstance(), "OemType 配制出错    未定义", 0).show();
				throw new RuntimeException(e);
			}
			return type;
		}
	}
	
	public void init(){
		PropertiesUtil propertiesUtil = new PropertiesUtil(LauncherApplication4Tcl.GetInstance().getApplicationContext(), "voole.properties");
		setVersionDisplayText(propertiesUtil.getProperty("versionDisplayText", ""));
		setAliPay(propertiesUtil.getProperty("alipay", ""));
		setAppId(propertiesUtil.getProperty("appid", ""));
		setShowtv(propertiesUtil.getProperty("showtv", ""));
		setDownloadUrl(propertiesUtil.getProperty("downloadurl", ""));
		setUpgradeUrl(propertiesUtil.getProperty("upgradeurl", ""));
		setKeyCodeType(propertiesUtil.getProperty("keycodetype", ""));
		setVersionCode(propertiesUtil.getProperty("versionCode", ""));
		setStartUpShowVersion(propertiesUtil.getProperty("startUpShowVersion", ""));
		setOemType(propertiesUtil.getProperty("oemType", ""));
		setInstallServer(propertiesUtil.getProperty("installServer", ""));
		setClearAuthConfig(propertiesUtil.getProperty("clearAuthConfig", ""));
		setBootStartAuth(propertiesUtil.getProperty("bootStartAuth", ""));
	}
	
	private String versionDisplayText = null;
	private String aliPay = null;
	private String appId = null;
	private String showtv = null;
	private String downloadUrl = null;
	private String upgradeUrl = null;
	private String keycodeType = null;
	private String versionCode = null;
	private String startUpShowVersion = null;
	private String oemType = null;
	private String installServer = null;
	private String clearAuthConfig = null;
	private String bootStartAuth = null;
	
	public String getBootStartAuth() {
		if (bootStartAuth == null) {
			init();
		}
		return bootStartAuth;
	}

	public void setBootStartAuth(String bootStartAuth) {
		this.bootStartAuth = bootStartAuth;
	}

	public String getClearAuthConfig() {
		if (clearAuthConfig == null) {
			init();
		}
		return clearAuthConfig;
	}

	public void setClearAuthConfig(String clearCache) {
		this.clearAuthConfig = clearCache;
	}

	public String getInstallServer() {
		if (installServer == null) {
			init();
		}
		return installServer;
	}

	public void setInstallServer(String installServer) {
		this.installServer = installServer;
	}

	public String getOemType() {
		if (oemType == null) {
			init();
		}
		return oemType;
	}

	public void setOemType(String oemType) {
		this.oemType = oemType;
	}

	public String getStartUpShowVersion() {
		if (startUpShowVersion == null) {
			init();
		}
		return startUpShowVersion;
	}

	public void setStartUpShowVersion(String startUpShowVersion) {
		this.startUpShowVersion = startUpShowVersion;
	}

	public String getVersionCode() {
		if (versionCode == null) {
			init();
		}
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getKeyCodeType() {
		if (keycodeType == null) {
			init();
		}
		return keycodeType;
	}

	public void setKeyCodeType(String keycodeType) {
		this.keycodeType = keycodeType;
	}
	
	
	public String getUpgradeUrl() {
		if (upgradeUrl == null) {
			init();
		}
		return upgradeUrl;
	}

	public void setUpgradeUrl(String upgradeUrl) {
		this.upgradeUrl = upgradeUrl;
	}
	
	public String getDownloadUrl() {
		if (downloadUrl == null) {
			init();
		}
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	public String getShowtv() {
		if (showtv == null) {
			init();
		}
		return showtv;
	}

	public void setShowtv(String showtv) {
		this.showtv = showtv;
	}

	public String getAliPay() {
		if(aliPay == null){
			init();
		}
		return aliPay;
	}

	public void setAliPay(String aliPay) {
		this.aliPay = aliPay;
	}

	public String getVersionDisplayText() {
		if(versionDisplayText == null){
			init();
		}
		return versionDisplayText;
	}

	public void setVersionDisplayText(String version) {
		this.versionDisplayText = version;
	}
	
	public String getAppId(){
		if (appId == null) {
			init();
		}
		return appId;
	}
	
	public void setAppId(String appId){
		this.appId = appId;
	}
}
