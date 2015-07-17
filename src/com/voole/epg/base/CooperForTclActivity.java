package com.voole.epg.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.voole.epg.Config;
import com.voole.epg.LauncherApplication;
import com.voole.epg.base.common.CheckUtil;
import com.voole.epg.base.common.ID;
import com.voole.epg.base.common.MessageManager;
import com.voole.epg.base.common.NumberDialog;
import com.voole.epg.base.common.NumberDialog.NumberListener;
import com.voole.epg.base.common.TVAlertDialog;
import com.voole.epg.cooperation.MagicTVManager;
import com.voole.epg.corelib.model.account.AccountManager;
import com.voole.epg.corelib.model.account.MessageInfoResult;
import com.voole.epg.corelib.model.auth.AuthManager;
import com.voole.epg.corelib.model.movies.Film;
import com.voole.epg.corelib.model.movies.MovieManager;
import com.voole.epg.corelib.model.movies.Union;
import com.voole.epg.corelib.model.navigation.FilmClass;
import com.voole.epg.corelib.model.navigation.NavigationManager;
import com.voole.epg.corelib.model.proxy.ProxyManager;
import com.voole.epg.corelib.model.utils.LogUtil;
import com.voole.epg.f4k_download.utils.StandardAuth;
import com.voole.epg.f4k_download.utils.StandardProxy;
import com.voole.epg.upgrade.Upgrade;
import com.voole.epg.view.movies.RecommendItemBaseView.ItemSelectedListener;
import com.voole.epg.view.movies.RecommendTVView;
import com.voole.epg.view.movies.RecommendTopRankView;
import com.voole.epg.view.movies.RecommendTopicView;
import com.voole.epg.view.movies.RecommendWatchFocusView;
import com.voole.epg.view.movies.detail.MovieDetailActivity;
import com.voole.epg.view.movies.movie.MovieActivity;
import com.voole.epg.view.movies.movie.MovieViewListener;
import com.voole.epg.view.movies.rank.RankActivity;
import com.voole.epg.view.movies.topic.TopicFilmListHorActivity;
import com.voole.epg.view.movies.topic.TopicFilmListVerActivity;
import com.voole.epg.view.movies.topic.TopicListActivity;
import com.voole.epg.view.movies.zy.ZYActivity;
import com.voole.epg.view.navigation.NavigationHomeMultiLineView;
import com.voole.epg.view.navigation.NavigationItemSelectedListener;
import com.voole.epgfor4k.R;
import com.voole.tvutils.DeviceUtil;

public class CooperForTclActivity extends BaseActivity {
	private static final int AUTH_SUCCESS = 2;
	private static final int AUTH_FAIL = 3;
	private static final int NAVIGATION_SUCCESS = 4;
	private static final int WATCH_FOCUS_SUCCESS = 5;
	private static final int UNION_SUCCESS = 6;

	private static final String VOOLE_DETAIL = "VOOLE_DETAIL";

	private List<FilmClass> navigationItems = null;
	private List<Film> watchFocusItems = null;
	private Union union = null;
	private RecommendWatchFocusView recommendView = null;
	// private NavigationHomeSingleLineView navigationSingleLineView = null;
	private NavigationHomeMultiLineView navigationMultiLineView = null;
	private RecommendTopicView recommendTopicView = null;
	private RecommendTVView recommendTVView = null;
	private RecommendTopRankView recommendTopRankView = null;

	private NumberDialog numberDialog = null;
	private boolean isUpgrade = false;
	private View splash;

	private void getMessage() {
		new Thread() {
			public void run() {
				MessageManager.GetInstance().getMessageInfo();
			};
		}.start();

	}

	@Override
	protected void doHandleMessage(int what, Object obj) {
		if (isUpgrade) {
			return;
		}
		switch (what) {
		case Upgrade.HAS_UPGRADE:
			isUpgrade = true;
			break;
		case AUTH_SUCCESS:
			String hid = AuthManager.GetInstance().getUser().getHid();
			LogUtil.d("hid------>" + hid);
			if (TextUtils.isEmpty(hid) || !CheckUtil.isNotAllZero(hid)) {
				new TVAlertDialog.Builder(CooperForTclActivity.this)
						.setCancelable(false)
						.setTitle("无法验证用户的合法性，请与产品供应商联系")
						.setPositiveButton(R.string.common_ok_button,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										CooperForTclActivity.this.finish();
									}
								}).create().show();
				return;
			}
		
			
			
			
			checkMobileServer();
			getMessage();
			getNavigation();
			getWatchFocus();
			getUnion();
			break;
		case AUTH_FAIL:
			new TVAlertDialog.Builder(CooperForTclActivity.this)
					.setCancelable(false)
					.setTitle(R.string.common_access_net_fail)
					.setPositiveButton(R.string.common_ok_button,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									CooperForTclActivity.this.finish();
								}
							}).create().show();
			break;
		case NAVIGATION_SUCCESS:
			// navigationSingleLineView.setItems(navigationItems);
			navigationMultiLineView.setItems(navigationItems);
			numberDialog.setTotalPageSize(navigationItems.size());
			numberDialog.setInputLimit(2);
			getNewMessage();
			
			if(getIntent().getIntExtra("is4K", -1)==1&&splash.isShown()){
				FilmClass item = null;
				for (FilmClass film : navigationItems) {
					if(NavigationManager.FK.equals(film.getTemplate())){
						item = film;
						break;
					}
				}
				if(item!=null){
					SystemClock.sleep(1000);//停留在欢迎页面一会
					Bundle bundle = new Bundle();
					bundle.putSerializable("navigation", item);
					bundle.putAll(getIntent().getExtras());
					Intent intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(CooperForTclActivity.this,com.voole.epg.f4k_download.F4KListActivity.class);
//  					intent.setClass(CooperForTclActivity.this,com.voole.epg.download.FilmListActivity.class);
					startActivityForResult(intent, 800);
				}else{
					Toast.makeText(getApplicationContext(), "4K频道没有配制", 0).show();
					CooperForTclActivity.this.onBackPressed();
				}
			}else
				Toast.makeText(CooperForTclActivity.this, "按频道左侧对应的数字键切换到对应频道",Toast.LENGTH_LONG).show();
			
			break;
		case WATCH_FOCUS_SUCCESS:
			recommendView.setData(watchFocusItems);
			// recommendView.requestFocusedItem();
			break;
		case UNION_SUCCESS:
			String topicName = "";
			if (union.getSubjectList() != null
					&& union.getSubjectList().size() > 0) {
				topicName = union.getSubjectList().get(0).getName();
			}
			recommendTopicView.setItem(union.getTopicCount(), topicName,
					union.getImgTopic());
			String channelName = "";
			if (union.getChannelList() != null
					&& union.getChannelList().size() > 0) {
				channelName = union.getChannelList().get(0).getName();
			}
			recommendTVView.setItem(channelName, union.getImgTV());
			if (union.getRankList() != null && union.getRankList().size() >= 3) {
				recommendTopRankView.setItem(union.getRankList().get(0)
						.getName(), union.getRankList().get(1).getName(), union
						.getRankList().get(2).getName(), union.getImgRank());
			}
			break;
		default:
			break;
		}
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Log.e("onCreate");
		if (!isTaskRoot()
				&& !"KONKA_896".equalsIgnoreCase(Config.GetInstance()
						.getOemType())) {
			LogUtil.d("isTaskRoot------------------------------------------------------------------->");
			finish();
		}
		
		
		
		setContentView(R.layout.cs_movies_recommend);
		splash = findViewById(R.id.splash);
		if ("android.intent.action.MAIN".equalsIgnoreCase(getIntent().getAction())) {
			getIntent().putExtra("is4K", 1);
			splash.setVisibility(View.VISIBLE);
		}else{
			splash.setVisibility(View.GONE);
		}
		
		// Toast.makeText(RecommendActivity.this, "该版本为升级测试版本",
		// Toast.LENGTH_LONG).show();
		init();
		String param = getIntent().getStringExtra("TARGET");
		if (param != null && !"".equals(param)) {
			startAuthAndProxy();
			gotoVoole(param);
		} else {
			showVersion();
			startAuthAndProxy();
		}
	}

	private void showVersion() {
		new Thread() {
			public void run() {
				if ("1".equals(Config.GetInstance().getStartUpShowVersion())) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(
									CooperForTclActivity.this,
									"您正在使用的版本号为-->"
											+ Config.GetInstance()
													.getVersionDisplayText(),
									Toast.LENGTH_LONG).show();
						}
					});

				}
			};
		}.start();
	}

	private void getNewMessage() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String msgId = getSharedPreferences(AccountManager.SP_NAME,
						MODE_PRIVATE).getString("msgId", "");
				LogUtil.d("msgId=============" + msgId);
				if (!"".equals(msgId)) {
					final MessageInfoResult result = AccountManager
							.GetInstance().getUnReadMessage(msgId);
					if (result != null) {
						if (!"0".equals(result.getCount())) {
							final MessageInfoResult messageResult = AccountManager
									.GetInstance().getMessageInfo();
							if (messageResult != null) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (messageResult.getList() != null
												&& messageResult.getList()
														.size() > 0) {
											showMessage(messageResult.getList()
													.get(0).getSubject());
										}
									}
								});

							}

						}
					}
				}

			};

		}.start();

	}

	private void init() {
		recommendView = (RecommendWatchFocusView) findViewById(R.id.recommend_view);
		recommendView.requestFocusedItem();
		recommendView.setMovieViewListener(new MovieViewListener() {
			@Override
			public void onPlay(Film item) {

			}

			@Override
			public void onItemSelected(Film item, int index) {
				if (item == null) {
					return;
				}
				if (MovieManager.TOPIC.equals(item.getContentType())) {
					if (MovieManager.TOPIC_VER.equals(item.getTemplate())) {
						Intent intent = new Intent();
						intent.setClass(CooperForTclActivity.this,
								TopicFilmListVerActivity.class);
						intent.putExtra("topicUrl", item.getInterfaceUrl());
						intent.putExtra("topicBigUrl",
								item.getRelateColumnImgS());
						intent.putExtra("topicName", item.getRelateName());
						startActivity(intent);
					} else {
						Intent intent = new Intent();
						intent.setClass(CooperForTclActivity.this,
								TopicFilmListHorActivity.class);
						intent.putExtra("topicUrl", item.getInterfaceUrl());
						intent.putExtra("topicBigUrl",
								item.getRelateColumnImgS());
						intent.putExtra("topicName", item.getRelateName());
						startActivity(intent);
					}
				} else {
					Bundle bundle = new Bundle();
					bundle.putSerializable("film", item);
					Intent intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(CooperForTclActivity.this,
							MovieDetailActivity.class);
					startActivity(intent);
				}
			}

			@Override
			public void onGotoPage(int pageNo) {
			}
		});
		recommendTopicView = (RecommendTopicView) findViewById(R.id.recommend_topic);
		recommendTopicView.setNextFocusRightId(R.id.recommend_topic);
		recommendTopicView
				.setOnItemSelectedListener(new ItemSelectedListener() {
					@Override
					public void onItemSelected() {
						Intent intent = new Intent();
						intent.setClass(CooperForTclActivity.this,
								TopicListActivity.class);
						startActivity(intent);
					}
				});
		recommendTVView = (RecommendTVView) findViewById(R.id.recommend_tv);
		recommendTVView.setNextFocusRightId(R.id.recommend_tv);
		recommendTVView.setOnItemSelectedListener(new ItemSelectedListener() {
			@Override
			public void onItemSelected() {
				if ("1".equals(Config.GetInstance().getShowtv())) {
					if (DeviceUtil.checkPackageExist(CooperForTclActivity.this,
							"com.voole.magictv")) {
						MagicTVManager.startMagicTV(CooperForTclActivity.this);
					} else {
						MagicTVManager.downloadMagicTV(CooperForTclActivity.this);
					}
				} else {
					gotoMymagic("CONSUMERCENTER", 0);
				}
			}
		});
		recommendTopRankView = (RecommendTopRankView) findViewById(R.id.recommend_toprank);
		recommendTopRankView.setId(ID.RecommendActivity.TOP_RANK_ID);
		recommendTopRankView
				.setNextFocusRightId(ID.RecommendActivity.TOP_RANK_ID);
		recommendTopRankView
				.setOnItemSelectedListener(new ItemSelectedListener() {
					@Override
					public void onItemSelected() {
						Intent intent = new Intent();
						intent.setClass(CooperForTclActivity.this,
								RankActivity.class);
						startActivity(intent);
					}
				});
//		navigationSingleLineView = (NavigationHomeSingleLineView) findViewById(R.id.recommend_navigation_s);
//		navigationSingleLineView
//				.setOnItemSelectedListener(new NavigationItemSelectedListener() {
//					@Override
//					public void onItemSelected(int index, FilmClass item) {
//						if (item == null) {
//							return;
//						}
//						if ("1".equals(Config.GetInstance().getShowtv())) {
//							if (index == 0) {
//								if (DeviceUtil.checkPackageExist(
//										RecommendActivity.this,
//										"com.voole.magictv")) {
//									MagicTVManager
//											.startMagicTV(RecommendActivity.this);
//								} else {
//									MagicTVManager
//											.downloadMagicTV(RecommendActivity.this);
//								}
//								return;
//							}
//						}
//						if (NavigationManager.ZY.equals(item.getTemplate())) {
//							Bundle bundle = new Bundle();
//							bundle.putSerializable("navigation", item);
//							Intent intent = new Intent();
//							intent.putExtras(bundle);
//							intent.setClass(RecommendActivity.this,
//									ZYActivity.class);
//							startActivity(intent);
//						} else if (NavigationManager.LIFE.equals(item
//								.getTemplate())) {
//							Bundle bundle = new Bundle();
//							bundle.putSerializable("navigation", item);
//							Intent intent = new Intent();
//							intent.putExtras(bundle);
//							intent.setClass(RecommendActivity.this,
//									ZYActivity.class);
//							startActivity(intent);
//						} else if (NavigationManager.FK.equals(item
//								.getTemplate())) {
//							Bundle bundle = new Bundle();
//							bundle.putSerializable("navigation", item);
//							Intent intent = new Intent();
//							intent.putExtras(bundle);
//							intent.setClass(
//									RecommendActivity.this,
//									com.voole.epg.f4k_download.F4KListActivity.class);
//							startActivity(intent);
//						} else {
//							Bundle bundle = new Bundle();
//							bundle.putSerializable("navigation", item);
//							Intent intent = new Intent();
//							intent.putExtras(bundle);
//							intent.setClass(RecommendActivity.this,
//									MovieActivity.class);
//							startActivity(intent);
//						}
//					}
//				});
		navigationMultiLineView = (NavigationHomeMultiLineView) findViewById(R.id.recommend_navigation_m);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navigationMultiLineView
				.getLayoutParams();
		params.bottomMargin = -80;
		navigationMultiLineView.setLayoutParams(params);
		navigationMultiLineView
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navigationMultiLineView
								.getLayoutParams();
						if (hasFocus) {
							Animation animationUp = new TranslateAnimation(0,
									0, 80, 0);
							animationUp.setDuration(500);
							animationUp.setFillAfter(false);
							navigationMultiLineView.startAnimation(animationUp);
							params.bottomMargin = 0;
							navigationMultiLineView
									.setBackgroundResource(R.drawable.cs_navigation_multi_line_bg);
							// navigationMultiLineView.requestFocus();
						} else {
							Animation animationDown = new TranslateAnimation(0,
									0, -80, 0);
							animationDown.setDuration(500);
							animationDown.setFillAfter(false);
							navigationMultiLineView
									.startAnimation(animationDown);
							params.bottomMargin = -80;
							navigationMultiLineView.setBackgroundResource(0);
						}
						navigationMultiLineView.setLayoutParams(params);
					}
				});
		navigationMultiLineView
				.setOnItemSelectedListener(new NavigationItemSelectedListener() {
						@Override
						public void onItemSelected(int index, FilmClass item) {
							if (item == null) {
								return;
							}
							if ("1".equals(Config.GetInstance().getShowtv())) {
								if (index == 0) {
									if (DeviceUtil.checkPackageExist(CooperForTclActivity.this, "com.voole.magictv")) {
										MagicTVManager.startMagicTV(CooperForTclActivity.this);
									}else {
										MagicTVManager.downloadMagicTV(CooperForTclActivity.this);
									}
									return;
								}
							}
							if (NavigationManager.ZY.equals(item.getTemplate())) {
								Bundle bundle = new Bundle();
								bundle.putSerializable("navigation", item);
								Intent intent = new Intent();
								intent.putExtras(bundle);
								intent.setClass(CooperForTclActivity.this,
										ZYActivity.class);
								startActivity(intent);
							} else if (NavigationManager.LIFE.equals(item
									.getTemplate())) {
								Bundle bundle = new Bundle();
								bundle.putSerializable("navigation", item);
								Intent intent = new Intent();
								intent.putExtras(bundle);
								intent.setClass(CooperForTclActivity.this,
										ZYActivity.class);
								startActivity(intent);
							} else if(NavigationManager.FK.equals(item
									.getTemplate())){
								Bundle bundle = new Bundle();
								bundle.putSerializable("navigation", item);
								Intent intent = new Intent();
								intent.putExtras(bundle);
								intent.setClass(CooperForTclActivity.this,com.voole.epg.f4k_download.F4KListActivity.class);
//								intent.setClass(CooperForTclActivity.this,com.voole.epg.download.FilmListActivity.class);
								startActivityForResult(intent, 800);
							}else {
								Bundle bundle = new Bundle();
								bundle.putSerializable("navigation", item);
								Intent intent = new Intent();
								intent.putExtras(bundle);
								intent.setClass(CooperForTclActivity.this,
										MovieActivity.class);
								startActivity(intent);
							}
					}
				});
		navigationMultiLineView.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.bottom_in));
		numberDialog = new NumberDialog(this, R.style.alertDialog);
		numberDialog.setNumberListener(new NumberListener() {
			@Override
			public void onGotoPage(int pageNo) {
				// navigationMultiLineView.setGoTo(pageNo);
			}

			@Override
			public void onInput(String str) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void startAuthAndProxy() {
		showDialog();
		new Thread() {
			public void run() {
//				initAuthAndProxy();
				checkAppFirstStart();
				if (!AuthManager.GetInstance().isAuthRunning()) {
					boolean b = AuthManager.GetInstance().startAuth();
					if (!b) {
						sendMessage(AUTH_FAIL);
						return;
					}
				} else {
					if (AuthManager.GetInstance().getUser() == null
							|| AuthManager.GetInstance().getUrlList() == null) {
						sendMessage(AUTH_FAIL);
						return;
					}
				}
				ProxyManager.GetInstance().startProxy();
				sendMessage(AUTH_SUCCESS);
				handler.post(new Runnable() {
					@Override
					public void run() {
						checkVersion();
					}
				});
			};
		}.start();
	}

	private void initAuthAndProxy() {
		AuthManager.GetInstance().init(new StandardAuth(this));
		ProxyManager.GetInstance().init(new StandardProxy(this));
	}

	private void getNavigation() {
		showDialog();
		new Thread() {
			public void run() {
				List<FilmClass> items = NavigationManager.GetInstance()
						.getMainCategoryList();
				if (items != null && items.size() > 0) {
					navigationItems = new ArrayList<FilmClass>();
					if ("1".equals(Config.GetInstance().getShowtv())) {
						navigationItems.addAll(items);
						FilmClass live = new FilmClass();
						live.setFilmClassName("优朋TV");
						navigationItems.add(0, live);
					} else {
						navigationItems = items;
					}
					sendMessage(NAVIGATION_SUCCESS);
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			};
		}.start();
	}

	private void getWatchFocus() {
		showDialog();
		new Thread() {
			public void run() {
				watchFocusItems = MovieManager.GetInstance()
						.getRecommendMovies();
				if (watchFocusItems != null && watchFocusItems.size() > 0) {
					sendMessage(WATCH_FOCUS_SUCCESS);
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			};
		}.start();
	}

	private void getUnion() {
		showDialog();
		new Thread() {
			public void run() {
				union = MovieManager.GetInstance().getUnion();
				if (union != null) {
					sendMessage(UNION_SUCCESS);
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			};
		}.start();
	}

	private void doExit() {
		new Thread() {
			public void run() {
				AuthManager.GetInstance().exitAuth();
				ProxyManager.GetInstance().exitProxy();
				android.os.Process.killProcess(android.os.Process.myPid());
			};
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			new TVAlertDialog.Builder(this)
					.setTitle("您确定要退出应用吗？")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
//									F4kExitDialog.getInstance(CooperForTclActivity.this).showDialogWithMessage(R.string.download_exit_msg);
									
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			return true;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_1:
		case KeyEvent.KEYCODE_2:
		case KeyEvent.KEYCODE_3:
		case KeyEvent.KEYCODE_4:
		case KeyEvent.KEYCODE_5:
		case KeyEvent.KEYCODE_6:
		case KeyEvent.KEYCODE_7:
		case KeyEvent.KEYCODE_8:
		case KeyEvent.KEYCODE_9:
			//navigationSingleLineView.onKeyDown(keyCode, event);
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	private void checkVersion() {
		showDialog();
		/*
		 * String checkVersionUrl = AuthManager.GetInstance().getUrlList()
		 * .getUpgradeCheck();
		 */
		String checkVersionUrl = Config.GetInstance().getUpgradeUrl();
		LogUtil.d("checkVersion---->" + checkVersionUrl);
		Upgrade upgrade = new Upgrade(CooperForTclActivity.this, handler);
		upgrade.checkVersion(checkVersionUrl);
	}

	private void gotoMymagic(String where, int index) {
		Intent intent = new Intent();
		intent.setAction("com.voole.epg.action.Mymagic_tcl");
		intent.putExtra("toWhere", where);
		intent.putExtra("index", index);
		CooperForTclActivity.this.startActivity(intent);
	}

	private void gotoVoole(String value) {
		if (value.equals(VOOLE_DETAIL)) {
			final String mid = getIntent().getStringExtra("VOOLE_DETAIL_PARAM");
			if (mid != null && !mid.equals("")) {
				Intent intent = new Intent();
				intent.setClass(CooperForTclActivity.this,
						MovieDetailActivity.class);
				intent.putExtra("intentMid", mid);
				CooperForTclActivity.this.startActivity(intent);
			}
		}
	}

	private void checkMobileServer() {
		if ("1".equals(Config.GetInstance().getInstallServer())) {
			final SharedPreferences voolesharedPreferences = this
					.getSharedPreferences(Config.VOOLE_SHARE,
							Context.MODE_PRIVATE);
			boolean isInstallController = voolesharedPreferences.getBoolean(
					"installController", false);
			if (!isInstallController) {
				if (!DeviceUtil.checkPackageExist(CooperForTclActivity.this,
						"com.voole.epg.vurcserver")) {
					new TVAlertDialog.Builder(this)
							.setTitle("安装优朋影视智能扩展")
							.setCancelable(false)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// File myFile = new
											// File("file:///android_asset/server.apk");
											String fileName = "server.apk";
											String filePath = LauncherApplication
													.GetInstance()
													.getFilePath();
											if (DeviceUtil.copyFileFromAssets(
													CooperForTclActivity.this,
													fileName, filePath)) {
												File apkFile = new File(
														filePath + "/"
																+ fileName);
												apkFile.setReadable(true, false);
												DeviceUtil.installApk(
														CooperForTclActivity.this,
														apkFile);

												Editor edit = voolesharedPreferences
														.edit();
												edit.putBoolean(
														"installController",
														true);
												edit.commit();
											}
										}
									}).create().show();
				} else {
					Editor edit = voolesharedPreferences.edit();
					edit.putBoolean("installController", true);
					edit.commit();
				}
			}
		}
	}

	private void checkAppFirstStart() {
		SharedPreferences voolesharedPreferences = this.getSharedPreferences(
				Config.VOOLE_SHARE, Context.MODE_PRIVATE);
		String versionCodeInShare = voolesharedPreferences.getString(
				"versionCode", "");
		// String versionCodeInProp =
		// PropertiesUtil.getProperty(getApplicationContext(), "versionCode");
		String versionCodeInProp = Config.GetInstance().getVersionCode();
		LogUtil.d("versionCodeInShare--->" + versionCodeInShare
				+ "----versionCodeInProp--->" + versionCodeInProp);
		if (!versionCodeInShare.equalsIgnoreCase(versionCodeInProp)) {
			AuthManager.GetInstance().exitAuth();
			AuthManager.GetInstance().deleteAuthFiles();
			ProxyManager.GetInstance().exitProxy();
			ProxyManager.GetInstance().deleteProxyFiles();

			Editor edit = voolesharedPreferences.edit();
			edit.putString("versionCode", versionCodeInProp);
			edit.commit();
		}
	} 
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==800) {//退出
			this.onBackPressed();
		}
		if (resultCode==600) {//继续观看
			splash.setVisibility(View.GONE);
		}
//		if(resultCode==400) {
//			moveTaskToBack(true);
//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_MAIN);
//			intent.addCategory(Intent.CATEGORY_HOME);           
//			startActivity(intent);
//		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onResume() {
	
		super.onResume();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		doExit();
	}
	
}
