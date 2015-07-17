package com.voole.epg.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.voole.epg.R;
import com.voole.epg.base.common.AgreementDialog;
import com.voole.epg.base.common.CheckUtil;
import com.voole.epg.base.common.DisplayManager;
import com.voole.epg.base.common.ID;
import com.voole.epg.base.common.OrderAgreementDialog;
import com.voole.epg.base.common.OrderAgreementDialog.OnOrderButtonClickListener;
import com.voole.epg.base.common.TVAlertDialog;
import com.voole.epg.cooperation.ali.AliPayManager;
import com.voole.epg.cooperation.ali.AliPayResult;
import com.voole.epg.corelib.model.account.AccountManager;
import com.voole.epg.corelib.model.account.AccountUser;
import com.voole.epg.corelib.model.account.AccountUserResult;
import com.voole.epg.corelib.model.account.ConsumeListInfo;
import com.voole.epg.corelib.model.account.LoginResult;
import com.voole.epg.corelib.model.account.MessageInfoResult;
import com.voole.epg.corelib.model.account.OrderListInfo;
import com.voole.epg.corelib.model.account.OrderResult;
import com.voole.epg.corelib.model.account.Product;
import com.voole.epg.corelib.model.account.RechargeListInfo;
import com.voole.epg.corelib.model.account.RechargeResult;
import com.voole.epg.corelib.model.auth.AuthManager;
import com.voole.epg.corelib.model.auth.User;
import com.voole.epg.corelib.model.cooperation.CooperationManager;
import com.voole.epg.corelib.model.movies.Film;
import com.voole.epg.corelib.model.movies.FilmAndPageInfo;
import com.voole.epg.corelib.model.movies.MovieManager;
import com.voole.epg.corelib.model.navigation.FilmClass;
import com.voole.epg.corelib.model.transscreen.DataResult;
import com.voole.epg.corelib.model.transscreen.ResumeAndPageInfo;
import com.voole.epg.corelib.model.transscreen.ResumeFilm;
import com.voole.epg.corelib.model.transscreen.TransScreenManager;
import com.voole.epg.corelib.model.utils.LogUtil;
import com.voole.epg.model.play.PlayManager;
import com.voole.epg.view.movies.detail.MovieDetailActivity;
import com.voole.epg.view.movies.movie.MovieViewListener;
import com.voole.epg.view.mymagic.ConsumerCenterFragment;
import com.voole.epg.view.mymagic.ConsumerCenterView.ConsumerCenterUpdataListner;
import com.voole.epg.view.mymagic.MessageCenterFragment;
import com.voole.epg.view.mymagic.MovieEditedListener;
import com.voole.epg.view.mymagic.MyMagicMovieFragment;
import com.voole.epg.view.mymagic.OrderListener;
import com.voole.epg.view.mymagic.OrderView.ProductItemView.OrderStatus;
import com.voole.epg.view.mymagic.RechargeListener;
import com.voole.epg.view.mymagic.RegisterView.RegisterViewListener;
import com.voole.epg.view.mymagic.ResetPwdView.ResetPwdViewListener;
import com.voole.epg.view.mymagic.UserCenterHintViewFragment;
import com.voole.epg.view.mymagic.UserInfoDialog;
import com.voole.epg.view.mymagic.UserInfoDialog.OnCancelClickListener;
import com.voole.epg.view.mymagic.UserInfoDialog.OnCodeClickListener;
import com.voole.epg.view.mymagic.UserInfoDialog.OnOkClickListener;
import com.voole.epg.view.mymagic.UserInfoViewListener;
import com.voole.epg.view.mymagic.UserManagementFragment;
import com.voole.epg.view.navigation.NavigationItemSelectedListener;
import com.voole.epg.view.navigation.NavigationProgramView;

public class MyMagicActivity extends BaseActivity {

	private static final int DOWNLOAD_UI = 10000;

	private static final int PRODUCT_SUCCESS = 10001;
	private static final int PRODUCT_FAIL = 10002;
	private static final int CONSUMELISTINFO_SUCCESS = 10003;
	private static final int CONSUMELISTINFO_FAIL = 10004;
	private static final int ORDERLISTINFO_SUCCESS = 10005;
	private static final int ORDERLISTINFO_FAIL = 10006;
	private static final int RECHARGELISTINFO_SUCCESS = 10007;
	private static final int RECHARGELISTINFO_FAIL = 10008;
	private static final int RECHARGERESULT_SUCCESS = 10009;
	private static final int RECHARGERESULT_FAIL = 10010;
	private static final int ORDERRESULT_SUCCESS = 10011;
	private static final int ORDERRESULT_FAIL = 10012;

	private static final int FAVORITE_SUCCESS = 10013;
	private static final int FAVORITE_FAIL = 10014;
	private static final int HISTORY_SUCCESS = 10015;
	private static final int HISTORY_FAIL = 10016;
	private static final int PURSUEVIDEO_SUCCESS = 10017;
	private static final int PURSUEVIDEO_FAIL = 10018;

	private static final int FAVORITE_DELETE_SUCCESS = 10019;
	private static final int HISTORY_DELETE_SUCCESS = 10020;
	private static final int PURSUEVIDEO_DELETE_SUCCESS = 10021;
	private static final int NET_FAIL = 10030;

	private static final int GET_ACCOUNTUSER_SUCCESS = 10031;
	private static final int GET_ACCOUNTUSER_FAIL = 10032;

	private static final int GET_MESSAGEINFO_SUCCESS = 10033;
	private static final int GET_MESSAGEINFO_FAIL = 10034;

	private static final int FAVORITE_EMPTY = 10035;
	private static final int HISTORY_EMPTY = 10036;
	private static final int PURSUEVIDEO_EMPTY = 10037;

	private static final int GET_AUTHCODE_SUCCESS = 10038;
	private static final int GET_AUTHCODE_FAIL = 10039;

	private static final int GET_REGISTER_SUCCESS = 10039;
	private static final int GET_REGISTER_FAIL = 10041;

	private static final int GET_MODIFY_SUCCESS = 10042;

	private static final int GET_USER_SUCCESS = 10043;
	private static final int GET_USER_FAIL = 10044;
	private static final int SUCCESS_RELEATED_FILM = 10054;

	private static final int ALI_PAY_SUCCESS = 10055;
	private static final int ALI_PAY_FAILURE = 10056;

	private static final String CONSUMERCENTER = "CONSUMERCENTER";
	private static final String MYFAVORITE = "MYFAVORITE";
	private static final String MYHISTORY = "MYHISTORY";
	private static final String MYPURSUEVIDEO = "MYPURSUEVIDEO";
	private static final String USERMANAGEMENT = "USERMANAGEMENT";
	private static final String MESSAGECENTER = "MESSAGECENTER";
	
	
	private static final String ACTION_CONSUMERCENTER_RECHARGE="com.voole.epg.action.myvoole.comsumer.Recharge_tcl";
	private static final String ACTION_CONSUMERCENTER_ORDER="com.voole.epg.action.myvoole.comsumer.Order_tcl";
	private static final String ACTION_MYFAVORITE="com.voole.epg.action.myvoole.Favorite_tcl";
	private static final String ACTION_MYHISTORY="com.voole.epg.action.myvoole.History_tcl";
	private static final String ACTION_MYPURSUEVIDEO="com.voole.epg.action.myvoole.Pursue_tcl";
	private static final String ACTION_USERMANAGEMENT="com.voole.epg.action.myvoole.Usermanage_tcl";
	private static final String ACTION_MESSAGECENTER="com.voole.epg.action.myvoole.Message_tcl";
	
	private boolean isAction=false;

	private AliPayResult aliResult = null;
	private AccountUser accountUser = null;

	private NavigationProgramView navigationProgramView = null;
	// private NavigationMovieView navigationMovieView = null;

	private FragmentManager fragmentManager = null;
	/** 消费中心 */
	private ConsumerCenterFragment consumerCenterFragment = null;
	/** 我的收藏 */
	private MyMagicMovieFragment myFavoriteFragment = null;
	/** 观看历史 */
	private MyMagicMovieFragment myHistoryFragment = null;
	/** 我的追剧 */
	private MyMagicMovieFragment myPursueVideoFragment = null;
	/** 用户管理 */
	private UserManagementFragment userManagementFragment = null;
	/** 消息中心 */
	private MessageCenterFragment messageCenterFragment = null;
	/** 没有记录提示 */
	private UserCenterHintViewFragment hintViewFragment = null;

	private List<FilmClass> navigation_list = null;

	private OrderListInfo orderListInfo = null;
	private List<Product> products = null;
	// private List<Film> favoriteFilms = null;
	private FilmAndPageInfo favoriteInfo = null;
	private ResumeAndPageInfo historyInfo = null;
	private FilmAndPageInfo pursueVideoInfo = null;
	private List<Film> releatedFilms = null;

	private DataResult favoriteResult = null;
	private DataResult historyResult = null;
	private DataResult pursueResult = null;

	private ConsumeListInfo consumeListInfo = null;
	private RechargeListInfo rechargeListInfo = null;
	private RechargeResult rechargeResult = null;
	private MessageInfoResult messageResult = null;
	private OrderResult orderResult = null;
	private LoginResult codeResult = null;
	private LoginResult registerResult = null;
	private LoginResult updateResult = null;
	private AccountUserResult userResult = null;
	private Product orderProduct = null;

	private int UPDATE_CODE = -1;
	private String UPDATE_TEXT = "";

	private boolean isBind = false;

	private String showHintString = "";

	private Timer countDownTimer = null;
	private TimerTask countDownTask = null;
	private int countDownTime = 60;
	private boolean isProduct = false;
	
	private int currentPage=-1;

	private void startCountDownTimer() {
		countDownTimer = new Timer();
		countDownTask = new TimerTask() {

			@Override
			public void run() {
				Message msg = codeHanlder.obtainMessage();
				msg.what = countDownTime;
				codeHanlder.sendMessage(msg);
				countDownTime--;
			}
		};
	}

	private void stopCountDownTimer() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
			countDownTimer = null;
		}
		if (countDownTask != null) {
			countDownTask.cancel();
			countDownTask = null;
		}
	}

	@Override
	protected void doHandleMessage(int what, Object obj) {
		if (MyMagicActivity.this.isFinishing()) {
			return;
		}
		switch (what) {
		case DOWNLOAD_UI:
				init();
				Intent intent = getIntent();
				getIntentData(intent);
			break;
		case ORDERLISTINFO_SUCCESS:
			cancelDialog();
			if(consumerCenterFragment != null ){
				consumerCenterFragment.getConsumerCenterView()
				.setOrderViewUserInfo(orderListInfo);
				// consumerCenterFragment.getConsumerCenterView().gotoRechargeView();
				if (isProduct) {
					getUserProductList();
				}
			}
			break;
		case ORDERLISTINFO_FAIL:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(R.string.mymagic_net_fail)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MyMagicActivity.this.finish();
								}
							}).create().show();
			break;
		case PRODUCT_SUCCESS:
			cancelDialog();
			if(consumerCenterFragment != null){
				consumerCenterFragment.getConsumerCenterView()
				.setOrderViewProductData(products);
			}
			break;
		case PRODUCT_FAIL:
			break;
		case CONSUMELISTINFO_SUCCESS:
			cancelDialog();
			if(consumerCenterFragment != null){
				consumerCenterFragment.getConsumerCenterView()
				.setOrderHistoryViewData(consumeListInfo);
			}
			break;
		case CONSUMELISTINFO_FAIL:
			cancelDialog();
			break;
		case RECHARGELISTINFO_SUCCESS:
			cancelDialog();
			if(consumerCenterFragment != null){
				consumerCenterFragment.getConsumerCenterView()
				.setRechargeHistoryViewData(rechargeListInfo);
			}
			break;
		case RECHARGELISTINFO_FAIL:
			cancelDialog();
			break;
		case GET_AUTHCODE_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(codeResult.getResultdesc())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();
			break;
		case ORDERRESULT_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(orderResult.getResultDesc())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// consumerCenterFragment.getConsumerCenterView().setLeftViewPosition(0);
									getUserOrderList(true);
								}
							}).create().show();
			break;
		case ORDERRESULT_FAIL:
			cancelDialog();
			if(consumerCenterFragment != null){
				new TVAlertDialog.Builder(MyMagicActivity.this)
				.setTitle(orderResult.getResultDesc())
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						consumerCenterFragment
						.getConsumerCenterView()
						.gotoRechargeView();
					}
				}).create().show();
			}
			break;
		case RECHARGERESULT_SUCCESS:
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(rechargeResult.getResultdesc())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 刷新用户余额UI
									if ("000".equals(rechargeResult.getStatus())) {
										consumerCenterFragment
												.getConsumerCenterView()
												.gotoOrderView();
										getUserOrderList(true);
									}
								}
							}).create().show();
			break;
		case RECHARGERESULT_FAIL:
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(R.string.mymagic_net_fail)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			break;
		case FAVORITE_EMPTY:
			cancelDialog();
			showUserCenterHintView();
			hideMyFavoriteView();
			getReleatedFilmList(MYFAVORITE);
			break;
		case FAVORITE_SUCCESS:
			//myFavoriteFragment.setShowPursueVideoBar(true);
			myFavoriteFragment.setMovieData(favoriteInfo);
			break;
		case FAVORITE_FAIL:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(R.string.mymagic_mymovie_favorite_get_fail)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			break;
		case FAVORITE_DELETE_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setCancelable(false)
					.setTitle(favoriteResult.getResultText())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									getFavoriteList(1);
								}
							}).create().show();
			break;
		case HISTORY_EMPTY:
			cancelDialog();
			hideMyHistoryView();
			showUserCenterHintView();
			getReleatedFilmList(MYHISTORY);
			break;
		case HISTORY_SUCCESS:
			if(myHistoryFragment != null){
				myHistoryFragment.setMovieData(historyInfo);
			}
			break;
		case HISTORY_FAIL:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(R.string.mymagic_mymovie_history_get_fail)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			break;
		case HISTORY_DELETE_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(historyResult.getResultText())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									getHistoryList(1);
								}
							}).create().show();
			break;
		case PURSUEVIDEO_EMPTY:
			cancelDialog();
			showUserCenterHintView();
			hideMyPursueVideoView();
			getReleatedFilmList(MYPURSUEVIDEO);
			break;
		case PURSUEVIDEO_SUCCESS:
			cancelDialog();
			myPursueVideoFragment.setShowPursueVideoBar(true);
			myPursueVideoFragment.setMovieData(pursueVideoInfo);
			break;
		case PURSUEVIDEO_FAIL:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(R.string.mymagic_mymovie_pursue_get_fail)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			break;
		case PURSUEVIDEO_DELETE_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(pursueResult.getResultText())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if ("0".equals(pursueResult.getResultCode())) {
										getPursueVideoList(1);
									}
								}
							}).create().show();
			break;
		case GET_ACCOUNTUSER_SUCCESS:
			cancelDialog();
			if (isBind) {
				getUserInfo();
			}
			showUserManagementView(isBind, true);
			break;
		case GET_ACCOUNTUSER_FAIL:
			showUserManagementView(false, false);
			break;
		case GET_USER_SUCCESS:
			cancelDialog();
			accountUser = userResult.getAccountUser();
			userManagementFragment.setUserInfo(accountUser.getAccount(),
					accountUser.getEmail(), accountUser.getMobile());
			break;
		case GET_USER_FAIL:
			cancelDialog();
			break;
		case GET_MODIFY_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(updateResult.getResultdesc())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if ("0".equals(updateResult.getStatus())) {
										switch (UPDATE_CODE) {
										case UserInfoDialog.NAME:
											userManagementFragment
													.updateName(UPDATE_TEXT);
											break;
										case UserInfoDialog.EMAIL:
											userManagementFragment
													.updateEmail(UPDATE_TEXT);
											break;
										case UserInfoDialog.PHONE:
											userManagementFragment
													.updateMobile(UPDATE_TEXT);
											break;
										case UserInfoDialog.PASSWORD:
											userManagementFragment
													.getUserManagementView()
													.clearPwd();
											break;
										default:
											break;
										}
									}
								}
							}).create().show();
			break;
		case GET_REGISTER_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(registerResult.getResultdesc())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if ("0".equals(registerResult.getStatus())) {
										getAccountUserList();
									}
								}
							}).create().show();
			break;
		case GET_MESSAGEINFO_SUCCESS:
			messageCenterFragment.getMessageCenterView()
					.setNotificationViewData(messageResult);
			if (messageResult.getList() != null
					&& messageResult.getList().size() > 0) {
				saveLastMessageInfo(messageResult.getList().get(0).getId());
			}
			cancelDialog();
			break;
		case GET_MESSAGEINFO_FAIL:
			cancelDialog();
			messageCenterFragment.getMessageCenterView()
					.setNotificationViewData(messageResult);
			break;
		case NET_FAIL:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(R.string.mymagic_net_fail)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create().show();
			break;
		case SUCCESS_RELEATED_FILM:
			if (showHintString.equals(MYFAVORITE)) {
				hintViewFragment
						.setHint(getString(R.string.mymagic_mymovie_favorite_empty));
			} else if (showHintString.equals(MYHISTORY)) {
				hintViewFragment
						.setHint(getString(R.string.mymagic_mymovie_history_empty));
			} else if (showHintString.equals(MYPURSUEVIDEO)) {
				if(hintViewFragment != null){
					hintViewFragment
					 .setHint(getString(R.string.mymagic_mymovie_pursue_empty));
				}
			}
			hintViewFragment.setData(releatedFilms);
			break;
		case ALI_PAY_SUCCESS:
			cancelDialog();
			new TVAlertDialog.Builder(MyMagicActivity.this)
					.setTitle(aliResult.getResultDis())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if ("0".equals(aliResult.getResult())) {
										if( consumerCenterFragment != null){
											consumerCenterFragment
											.getConsumerCenterView()
											.gotoOrderView();
											getUserOrderList(true);
										}
									}
								}
							}).create().show();
			break;
		case ALI_PAY_FAILURE:
			cancelDialog();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("onCreate--->");
		downloadUI();
	}

	private void init() {
		setContentView(R.layout.cs_mymagic);
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		mainLayout.setBackgroundResource(R.drawable.cs_recommend_bg);
		navigationProgramView = (NavigationProgramView) findViewById(R.id.movie_program_navigator);
		navigationProgramView
				.setNextFocusDownId(ID.MyMagicActivity.CC_LEFT_VIEW_NAVIGATION_ID);
		TextView logoText = (TextView) findViewById(R.id.logo_text);
		logoText.setTextSize(TypedValue.COMPLEX_UNIT_SP,DisplayManager.GetInstance().changeTextSize(34));
		fragmentManager = getFragmentManager();

		getNavigationDate();
		addViewListener();
	}

	private void downloadUI() {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendMessage(DOWNLOAD_UI);
			}
		}).start();
	}

	private void addViewListener() {
		navigationProgramView
				.setOnItemSelectedListener(new NavigationItemSelectedListener() {
					@Override
					public void onItemSelected(int index, FilmClass item) {
						if (CONSUMERCENTER.equals(item.getChannelId())) {
							showConsumerView(0);
							hideUserCenterHintView();
							hideMyFavoriteView();
							hideMyHistoryView();
							hideUserManagementView();
							hideMyPursueVideoView();
							hideMessageCenterView();
							getUserOrderList(false);
							navigationProgramView
									.setNextFocusDownId(ID.MyMagicActivity.CC_LEFT_VIEW_NAVIGATION_ID);
						} else if (MYFAVORITE.equals(item.getChannelId())) {
							showMyFavoriteView();
							hideUserCenterHintView();
							hideConsumerView();
							hideMyHistoryView();
							hideUserManagementView();
							hideMyPursueVideoView();
							hideMessageCenterView();
							currentPage=1;
							getFavoriteList(1);
						} else if (MYHISTORY.equals(item.getChannelId())) {
							showMyHistroyView();
							hideUserCenterHintView();
							hideConsumerView();
							hideMyFavoriteView();
							hideUserManagementView();
							hideMyPursueVideoView();
							hideMessageCenterView();
							currentPage=1;
							getHistoryList(1);
						} else if (MYPURSUEVIDEO.equals(item.getChannelId())) {
							showMyPursueViedeo();
							hideUserCenterHintView();
							hideUserManagementView();
							hideConsumerView();
							hideMyHistoryView();
							hideMyFavoriteView();
							hideMessageCenterView();
							currentPage=1;
							getPursueVideoList(1);
						} else if (USERMANAGEMENT.equals(item.getChannelId())) {
							hideConsumerView();
							hideUserCenterHintView();
							hideMyHistoryView();
							hideMyFavoriteView();
							hideMyPursueVideoView();
							hideMessageCenterView();
							getAccountUserList();
							navigationProgramView
									.setNextFocusDownId(ID.MyMagicActivity.UM_LEFT_VIEW_NAVIGATION_ID);
						} else if (MESSAGECENTER.equals(item.getChannelId())) {
							showMessageCenterView();
							hideUserCenterHintView();
							hideConsumerView();
							hideMyHistoryView();
							hideMyFavoriteView();
							hideMyPursueVideoView();
							hideUserManagementView();
							getMessageInfoList();
							navigationProgramView
									.setNextFocusDownId(ID.MyMagicActivity.MM_LEFT_VIEW_NAVIGATION_ID);
						}
					}

				});

	}

	private void getIntentData(Intent intent) {
		 
		String toWhere = intent.getStringExtra("toWhere");
		String action=null;
		if (toWhere == null || "".equals(toWhere)) {
			action=intent.getAction();
			if(ACTION_CONSUMERCENTER_ORDER.equals(action)||ACTION_CONSUMERCENTER_RECHARGE.equals(action)){
				isAction=true;
				toWhere=CONSUMERCENTER;
			}else if(ACTION_MYFAVORITE.equals(action)){
				toWhere=MYFAVORITE;
			}else if(ACTION_MYHISTORY.equals(action)){
				toWhere=MYHISTORY;
			}else if(ACTION_MYPURSUEVIDEO.equals(action)){
				toWhere=MYPURSUEVIDEO;
			}else if(ACTION_USERMANAGEMENT.equals(action)){
				toWhere=USERMANAGEMENT;
			}else if(ACTION_MESSAGECENTER.equals(action)){
				toWhere=MESSAGECENTER;
			}
		}
		if (CONSUMERCENTER.equals(toWhere)) {
			navigationProgramView.setLoseFocusSelectedItemIndex(0);
			navigationProgramView.requestFocus();
			int index =-1;
			if(isAction){
				if(action==null||ACTION_CONSUMERCENTER_RECHARGE.equals(action)){
					index=0;
				}else if(ACTION_CONSUMERCENTER_ORDER.equals(action)){
					index=1;
				}
			}else{
			   index = intent.getIntExtra("index", 0);
			}
			showConsumerView(index);
			hideUserCenterHintView();
			hideMyFavoriteView();
			hideMyHistoryView();
			hideUserManagementView();
			hideMyPursueVideoView();
			hideMessageCenterView();
			if (index == 0) {
				getUserOrderList(false);
			} else if (index == 1) {
				getUserOrderList(true);
			}
		} else if (MYFAVORITE.equals(toWhere)) {
			navigationProgramView.setLoseFocusSelectedItemIndex(1);
			navigationProgramView.requestFocus();
			showMyFavoriteView();
			getFavoriteList(1);
		} else if (MYHISTORY.equals(toWhere)) {
			navigationProgramView.setLoseFocusSelectedItemIndex(2);
			navigationProgramView.requestFocus();
			showMyHistroyView();
			getHistoryList(1);
		} else if (MYPURSUEVIDEO.equals(toWhere)) {
			navigationProgramView.setLoseFocusSelectedItemIndex(3);
			navigationProgramView.requestFocus();
			showMyPursueViedeo();
			getPursueVideoList(1);
		} 
//		else if (USERMANAGEMENT.equals(toWhere)) {
//			navigationProgramView.setLoseFocusSelectedItemIndex(4);
//			navigationProgramView.requestFocus();
//			getAccountUserList();
//		} 
		else if (MESSAGECENTER.equals(toWhere)) {
			navigationProgramView.setLoseFocusSelectedItemIndex(5);
			navigationProgramView.requestFocus();
			showMessageCenterView();
			getMessageInfoList();
		} else {
			showConsumerView(0);
			getUserOrderList(false);
		}
	}

	/**
	 * 获得消息数据
	 */
	private void getMessageInfoList() {
		showDialog();
		new Thread() {
			public void run() {
				messageResult = AccountManager.GetInstance().getMessageInfo();
				if (messageResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					sendMessage(GET_MESSAGEINFO_SUCCESS);
				}

			};
		}.start();

	}

	private void saveLastMessageInfo(final String msgId) {
		new Thread() {
			public void run() {
				SharedPreferences sp = getSharedPreferences(
						AccountManager.SP_NAME, MODE_PRIVATE);
				sp.edit().putString("msgId", msgId).commit();
			};
		}.start();
	}

	/**
	 * 修改密码
	 * 
	 * @param password
	 */
	private void updatePwd(final String password) {
		showDialog();
		new Thread() {
			public void run() {
				updateResult = AccountManager.GetInstance().modifyPwd(password);
				if (updateResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					UPDATE_TEXT = password;
					sendMessage(GET_MODIFY_SUCCESS);
				}
			};
		}.start();
	}

	/**
	 * 修改邮箱
	 * 
	 * @param email
	 */
	private void updateEmail(final String email) {
		showDialog();
		new Thread() {
			public void run() {
				updateResult = AccountManager.GetInstance().modifyEmail(email);
				if (updateResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					UPDATE_TEXT = email;
					sendMessage(GET_MODIFY_SUCCESS);
				}
			};
		}.start();
	}

	/**
	 * 修改用户 账号
	 * 
	 * @param account
	 */
	private void updateAccount(final String account) {
		showDialog();
		new Thread() {
			public void run() {
				updateResult = AccountManager.GetInstance().modifyAccount(
						account);
				if (updateResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					UPDATE_TEXT = account;
					sendMessage(GET_MODIFY_SUCCESS);
				}
			};
		}.start();
	}

	/**
	 * 修改 手机号
	 * 
	 * @param mobile
	 */
	private void updateMobile(final String mobile,final String code) {
		showDialog();
		new Thread() {
			public void run() {
				updateResult = AccountManager.GetInstance().modifyMobile(
						mobile,code);
				if (updateResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					UPDATE_TEXT = mobile;
					sendMessage(GET_MODIFY_SUCCESS);
				}
			};
		}.start();
	}

	/**
	 * 获得用户信息
	 */
	private void getUserInfo() {
		showDialog();
		new Thread() {
			public void run() {
				userResult = AccountManager.GetInstance().getUserInfo();
				if (userResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					if (userResult.getAccountUser() != null) {
						sendMessage(GET_USER_SUCCESS);
					} else {
						sendMessage(GET_USER_FAIL);
					}
				}
			};
		}.start();
	}

	private void getAccountUserList() {
		showDialog();
		new Thread() {
			public void run() {
				LoginResult result = AccountManager.GetInstance()
						.getIsBindAccount();
				if (result == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					if ("1".equals(result.getStatus())) {
						isBind = true;
					} else {
						isBind = false;
					}
					sendMessage(GET_ACCOUNTUSER_SUCCESS);
				}
			};
		}.start();
	}

	/**
	 * 
	 */
	private void getNavigationDate() {
		navigation_list = new ArrayList<FilmClass>();
		FilmClass filmClass1 = new FilmClass();
		filmClass1.setFilmClassName("消费中心");
		filmClass1.setChannelId(CONSUMERCENTER);
		navigation_list.add(filmClass1);

		FilmClass filmClass2 = new FilmClass();
		filmClass2.setFilmClassName("我的收藏");
		filmClass2.setChannelId(MYFAVORITE);
		navigation_list.add(filmClass2);
		
		FilmClass filmClass3 = new FilmClass();
		filmClass3.setFilmClassName("观看记录");
		filmClass3.setChannelId(MYHISTORY);
		navigation_list.add(filmClass3);

		FilmClass filmClass4 = new FilmClass();
		filmClass4.setFilmClassName("我的追剧");
		filmClass4.setChannelId(MYPURSUEVIDEO);
		navigation_list.add(filmClass4);

//		FilmClass filmClass5 = new FilmClass();
//		filmClass5.setFilmClassName("用户管理");
//		filmClass5.setChannelId(USERMANAGEMENT);
//		navigation_list.add(filmClass5);

		FilmClass filmClass6 = new FilmClass();
		filmClass6.setFilmClassName("消息中心");
		filmClass6.setChannelId(MESSAGECENTER);
		navigation_list.add(filmClass6);
		navigationProgramView.setData(navigation_list);
	}

	private void showConsumerView(int index) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (consumerCenterFragment == null) {
			consumerCenterFragment = new ConsumerCenterFragment(index);
			consumerCenterFragment.setOrderListener(new OrderListener() {
				@Override
				public void order(final Product product,
						final OrderStatus orderStatus) {
					LogUtil.d("order----->");
					String msg = "确定订购";
					if (orderStatus == OrderStatus.ORDER_CANCEL) {
						msg = "确定取消自动续订服务";
					} else if (orderStatus == OrderStatus.ORDER_CONTINUE) {
						msg = "确定恢复续订";
					}
					final OrderAgreementDialog orderDialog = new OrderAgreementDialog(
							MyMagicActivity.this, R.style.agreement_dialog);
					orderDialog.show();
					if (orderStatus == OrderStatus.ORDER_CANCEL) {
						orderDialog.showResume();
					}
					orderDialog.setTitle(msg);
					orderDialog.setUserInfo(orderListInfo, product);
					orderDialog.setListener(new OnOrderButtonClickListener() {
						@Override
						public void onClickOk() {
							if (orderStatus == OrderStatus.ORDER_NEW
									|| orderStatus == OrderStatus.ORDER_CONTINUE) {
								getOrderResult(product);
							} else if (orderStatus == OrderStatus.ORDER_CANCEL) {
								cancelOrder(product);
							}
						}

						@Override
						public void onClickCancel() {

						}
					});
				}
			});
			consumerCenterFragment
					.setConsumerCenterUpdataListner(new ConsumerCenterUpdataListner() {
						@Override
						public void getData(int currentPosition) {
							switch (currentPosition) {
							case 0:
								// 我要充值
								User user = AuthManager.GetInstance().getUser();
								if (user != null) {
									consumerCenterFragment
											.getConsumerCenterView()
											.setRechargeViewData(user.getUid());
									getUserOrderList(false);
								}
								break;
							case 1:
								// 我要订购
								getUserOrderList(true);
								break;
							case 2:
								// 订购记录
								getConsumeListInfo();
								break;
							case 3:
								// 充值记录
								getRecharList();
								break;
							default:
								break;
							}
						}
					});
			consumerCenterFragment.setRechargeListener(new RechargeListener() {
				@Override
				public void recharge(String uid, String cardNo) {
					if (TextUtils.isEmpty(cardNo.trim())) {
						showCheckAlert("密码不能为空");
						return;
					}
					getRecharResult(uid, cardNo);
				}

				@Override
				public void aliPay(String sum) {
					if (TextUtils.isEmpty(sum)) {
						showCheckAlert("充值金额不能为空，请输入充值金额");
						return;
					}
					Pattern pattern = Pattern.compile("[0-9]*");
					if (pattern.matcher(sum).matches()) {
						if (Integer.parseInt(sum) < 5) {
							showCheckAlert("最低充值金额为5元，请重新输入充值金额");
							return;
						}
					} else {
						showCheckAlert("充值金额必须是整数，请重新输入充值金额");
						return;
					}
					aliPayThread(sum);
				}
			});
			ft.replace(R.id.middle, consumerCenterFragment);
		} else {
			ft.show(consumerCenterFragment);
		}
		ft.commit();
	}

	private void aliPayThread(final String sum) {
		showDialog();
		new Thread() {
			public void run() {
				aliResult = AliPayManager.GetInstance().pay(
						MyMagicActivity.this, sum);
				sendMessage(ALI_PAY_SUCCESS);
			};
		}.start();

	}

	private void hideConsumerView() {
		if (consumerCenterFragment != null) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			// ft.hide(consumerCenterFragment);
			ft.remove(consumerCenterFragment);
			ft.commit();
			consumerCenterFragment = null;
		}
	}

	private void showMyFavoriteView() {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (myFavoriteFragment == null) {
			myFavoriteFragment = new MyMagicMovieFragment();
			myFavoriteFragment
					.setMovieEditedListener(new MovieEditedListener() {

						@Override
						public void onEdited() {
							myFavoriteFragment.setEdited(true);
						}

						@Override
						public void onDelete(final List<? extends Film> list) {
							new TVAlertDialog.Builder(MyMagicActivity.this)
									.setTitle(R.string.mymagic_mymovie_delete)
									.setPositiveButton(
											R.string.ok,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													deletedFavorite(list);
													myFavoriteFragment
															.setEdited(false);
												}
											})
									.setNegativeButton(
											R.string.cancel,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

												}
											}).create().show();
						}

						@Override
						public void onClear(List<? extends Film> list) {
							new TVAlertDialog.Builder(MyMagicActivity.this)
									.setTitle(
											R.string.mymagic_mymovie_favorite_clear)
									.setPositiveButton(
											R.string.ok,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													deleteAllFavorite();
												}
											})
									.setNegativeButton(
											R.string.cancel,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											}).create().show();
						}

						@Override
						public void onCancel() {
							myFavoriteFragment.setEdited(false);
						}

					});
			myFavoriteFragment.setMovieViewListener(new MovieViewListener() {

				@Override
				public void onPlay(Film item) {

				}

				@Override
				public void onItemSelected(Film item,int index) {
					gotoDetail(item);
				}

				@Override
				public void onGotoPage(int pageNo) {
					getFavoriteList(pageNo);
				}
			});
			ft.replace(R.id.middle, myFavoriteFragment);
			// ft.add(R.id.middle, myFavoriteFragment);
		} else {
			ft.show(myFavoriteFragment);
		}
		ft.commit();
	}

	private void hideMyFavoriteView() {
		if (myFavoriteFragment != null) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			// ft.hide(myFavoriteFragment);
			ft.remove(myFavoriteFragment);
			ft.commit();
			myFavoriteFragment = null;
			currentPage=-1;
		}
	}

	private void showUserCenterHintView() {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (hintViewFragment == null) {
			hintViewFragment = new UserCenterHintViewFragment();
			hintViewFragment.setMovieViewListener(new MovieViewListener() {

				@Override
				public void onPlay(Film item) {

				}

				@Override
				public void onItemSelected(Film item,int index) {
					Intent intent = new Intent();
					intent.putExtra("film", item);
					intent.setClass(MyMagicActivity.this,
							MovieDetailActivity.class);
					startActivity(intent);
				}

				@Override
				public void onGotoPage(int pageNo) {

				}
			});
			LogUtil.d("showUserCenterHintView---------new");
			ft.replace(R.id.middle, hintViewFragment);
		} else {
			ft.show(hintViewFragment);
		}
		ft.commit();
		LogUtil.d("showUserCenterHintView---------commit");
	}

	private void hideUserCenterHintView() {
		if (hintViewFragment != null) {
			LogUtil.d("hideUserCenterHintView---------");
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.remove(hintViewFragment);
			ft.commit();
			hintViewFragment = null;
		}
	}

	Handler codeHanlder = new Handler() {
		public void handleMessage(Message msg) {
			int time = msg.what;
			if (time == 0) {
				countDownTime = 60;
				stopCountDownTimer();
				userManagementFragment.getCodeEnable(time);
			} else {
				userManagementFragment.getCodeEnable(time);
			}
		};
	};
	

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtil.d("onNewIntent--->");
		getIntentData(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("onDestroy--->");
	}

	private void showUserManagementView(boolean isRegister, boolean isFinish) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (userManagementFragment == null || isFinish == true) {
			userManagementFragment = new UserManagementFragment(isRegister);
			userManagementFragment
					.setRegisterViewListener(new RegisterViewListener() {

						@Override
						public void getAuthCode(String mobile) {

							if (TextUtils.isEmpty(mobile)) {
								showCheckAlert("请输入手机号");
								return;
							}

							if (!CheckUtil.isMobile(mobile)) {
								showCheckAlert("手机号格式不正确");
								return;
							}

							startCountDownTimer();
							userManagementFragment.getCodeDisable();
							countDownTimer.schedule(countDownTask, 1000, 1000);
							getCode(mobile);
						}

						@Override
						public void onAgreementClick() {
							AgreementDialog dialog = new AgreementDialog(
									MyMagicActivity.this,
									R.style.agreement_dialog);
							dialog.setPosition(userManagementFragment
									.getLeftWidth());
							dialog.show();
							dialog.setTitle("服务协议");
							dialog.setType(AgreementDialog.TYPE_REGISTER);
							dialog.setShowButton(false);
						}

						@Override
						public void onFinishClick(String account, String email,
								String password, String compwd, String mobile,
								String authCode) {
							if (TextUtils.isEmpty(account)
									|| TextUtils.isEmpty(email)
									|| TextUtils.isEmpty(password)
									|| TextUtils.isEmpty(compwd)
									|| TextUtils.isEmpty(mobile)
									|| TextUtils.isEmpty(authCode)) {
								showCheckAlert("输入信息不完整");
								return;
							}
						 
							if(CheckUtil.isAccountName(account)){
								showCheckAlert("用户名格式不正确");
								return;
							}

							if (!CheckUtil.isEmail(email)) {
								showCheckAlert("邮箱格式不正确，请重新输入");
								return;
							}

							if (password.trim().length() > 16 || password.trim().length() < 6) {
								showCheckAlert("密码长度不正确，请重新输入");
								return;
							}
							

							if(password.equals(account)){
								showCheckAlert("用户名和密码不能相同，请重新输入");
								return;
							}
							
							if(!CheckUtil.isChineseBlank(password)){
								showCheckAlert("密码不能包含中文和空格，请重新输入");
								return;
							}
							
							if(!CheckUtil.isSpecial(password)){
								showCheckAlert("密码不能包含特殊字符，请重新输入");
								return;
							}
							
							
							if (!compwd.equals(password)) {
								showCheckAlert("两次密码输入不一致");
								return;
							}

							if (!CheckUtil.isMobile(mobile)) {
								showCheckAlert("手机号格式不正确，请重新输入");
								return;
							}
							register(account, email, password, mobile, authCode);

						}
					});

			userManagementFragment
					.setUserInfoViewListener(new UserInfoViewListener() {

						@Override
						public void phoneChangeOnClick() {
							UPDATE_CODE = UserInfoDialog.PHONE;
							final UserInfoDialog userInfoDialog = new UserInfoDialog(
									MyMagicActivity.this,
									R.style.user_info_dialog);
							userInfoDialog.setCode(UserInfoDialog.PHONE);
							userInfoDialog.setPosition(userManagementFragment
									.getLeftWidth());
							userInfoDialog.show();
							userInfoDialog
									.setOnOkClickListener(new OnOkClickListener() {

										@Override
										public void onClick(String content) {
											
										}

										@Override
										public void onClick(String content,
												String code) {
											userInfoDialog.cancel();
											if (TextUtils.isEmpty(content)) {
												showCheckAlert("内容不能为空");
												return;
											}
											
											if (TextUtils.isEmpty(code)) {
												showCheckAlert("验证码不能为空");
												return;
											}
											
											if (!CheckUtil.isMobile(content)) {
												showCheckAlert("手机号格式不正确，请重新输入");
												return;
											}
											updateMobile(content,code);
										}
									});
							userInfoDialog
									.setOnCancelClickListener(new OnCancelClickListener() {
										@Override
										public void onClick() {
											userInfoDialog.cancel();
										}
									});
							userInfoDialog.setOnCodeClickListener(new OnCodeClickListener() {
								
								@Override
								public void onClick(String mobile) {
									if (TextUtils.isEmpty(mobile)) {
										showCheckAlert("手机不能为空");
										return;
									}
									
									if (!CheckUtil.isMobile(mobile)) {
										showCheckAlert("手机格式不正确");
										return;
									}
									userInfoDialog.startCountDown();
									getCode(mobile);
								}
							});
						}

						@Override
						public void nameChangeOnClick() {
							UPDATE_CODE = UserInfoDialog.NAME;
							final UserInfoDialog userInfoDialog = new UserInfoDialog(
									MyMagicActivity.this,
									R.style.user_info_dialog);
							userInfoDialog.setCode(UserInfoDialog.NAME);
							userInfoDialog.setPosition(userManagementFragment
									.getLeftWidth());
							userInfoDialog.show();
							userInfoDialog
									.setOnOkClickListener(new OnOkClickListener() {

										@Override
										public void onClick(String content) {
											userInfoDialog.cancel();
											if (TextUtils.isEmpty(content)) {
												showCheckAlert("内容不能为空");
												return;
											}
											updateAccount(content);
										}

										@Override
										public void onClick(String content,
												String code) {
											
										}
									});
							userInfoDialog
									.setOnCancelClickListener(new OnCancelClickListener() {
										@Override
										public void onClick() {
											userInfoDialog.cancel();
										}
									});
						}

						@Override
						public void emailChangeOnClick() {
							UPDATE_CODE = UserInfoDialog.EMAIL;
							final UserInfoDialog userInfoDialog = new UserInfoDialog(
									MyMagicActivity.this,
									R.style.user_info_dialog);
							userInfoDialog.setCode(UserInfoDialog.EMAIL);
							userInfoDialog.setPosition(userManagementFragment
									.getLeftWidth());
							userInfoDialog.show();
							userInfoDialog
									.setOnOkClickListener(new OnOkClickListener() {
										@Override
										public void onClick(String content) {
											userInfoDialog.cancel();
											if (TextUtils.isEmpty(content)) {
												showCheckAlert("内容不能为空");
												return;
											}
											if (!CheckUtil.isEmail(content)) {
												showCheckAlert("邮箱格式不正确，请重新输入");
												return;
											}

											updateEmail(content);
										}

										@Override
										public void onClick(String content,
												String code) {
											 
											
										}
									});
							userInfoDialog
									.setOnCancelClickListener(new OnCancelClickListener() {
										@Override
										public void onClick() {
											userInfoDialog.cancel();
										}
									});
						}
					});

			userManagementFragment
					.setResetPwdViewListener(new ResetPwdViewListener() {
						@Override
						public void onFinsih(String password, String compassword) {
							UPDATE_CODE = UserInfoDialog.PASSWORD;
							if (TextUtils.isEmpty(password)
									|| TextUtils.isEmpty(compassword)) {
								showCheckAlert("输入信息不完整");
								return;
							}

							if (password.length() < 6 || password.length() > 16) {
								showCheckAlert("输入长度不正确");
								return;
							}

							if(!CheckUtil.isChineseBlank(password)){
								showCheckAlert("密码不能包含中文和空格，请重新输入");
								return;
							}
							
							if(!CheckUtil.isSpecial(password)){
								showCheckAlert("密码不能包含特殊字符，请重新输入");
								return;
							}
							
							if (!compassword.equals(password)) {
								showCheckAlert("两次密码输入不一致");
								return;
							}
							
							
						
							
							updatePwd(password);
						}
					});
			ft.replace(R.id.middle, userManagementFragment);
			// ft.add(R.id.middle, userManagementFragment);
		} else {
			ft.show(userManagementFragment);
		}
		ft.commit();
	}

	private void hideUserManagementView() {
		if (userManagementFragment != null) {
			stopCountDownTimer();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			// ft.hide(userManagementFragment);
			ft.remove(userManagementFragment);
			ft.commit();
			userManagementFragment = null;
		}
	}

	private void showMyHistroyView() {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (myHistoryFragment == null) {
			myHistoryFragment = new MyMagicMovieFragment();
			myHistoryFragment.setMovieEditedListener(new MovieEditedListener() {

				@Override
				public void onEdited() {
					myHistoryFragment.setEdited(true);
				}

				@Override
				public void onDelete(final List<? extends Film> list) {
					new TVAlertDialog.Builder(MyMagicActivity.this)
							.setTitle(R.string.mymagic_mymovie_delete)
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											deletedHistory(list);
											myHistoryFragment.setEdited(false);
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).create().show();
				}

				@Override
				public void onCancel() {
					myHistoryFragment.setEdited(false);
				}

				@Override
				public void onClear(List<? extends Film> list) {
					new TVAlertDialog.Builder(MyMagicActivity.this)
							.setTitle(R.string.mymagic_mymovie_history_clear)
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											deleteAllHistory();
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create().show();

				}

			});
			myHistoryFragment.setMovieViewListener(new MovieViewListener() {

				@Override
				public void onPlay(Film item) {

				}

				@Override
				public void onItemSelected(Film item,int index) {
					ResumeFilm film = (ResumeFilm) item;
					PlayManager.GetInstance().play(MyMagicActivity.this,
							item.getMid(), film.getSid());
				}

				@Override
				public void onGotoPage(int pageNo) {
					currentPage=pageNo;
					getHistoryList(pageNo);
				}
			});
			ft.replace(R.id.middle, myHistoryFragment);
			// ft.add(R.id.middle, myHistoryFragment);
		} else {
			ft.show(myHistoryFragment);
		}
		ft.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(currentPage!=-1){
			if(myFavoriteFragment!=null){
				getFavoriteList(currentPage);
			}else if(myHistoryFragment!=null){
				getHistoryList(currentPage);
			}else if(myPursueVideoFragment!=null){
				getPursueVideoList(currentPage);
			}
		}
		
	}
	
	private void hideMyHistoryView() {
		if (myHistoryFragment != null) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			// ft.hide(myHistoryFragment);
			ft.remove(myHistoryFragment);
			ft.commit();
			myHistoryFragment = null;
			currentPage=-1;
		}
	}

	private void showMyPursueViedeo() {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (myPursueVideoFragment == null) {
			myPursueVideoFragment = new MyMagicMovieFragment();
			myPursueVideoFragment
					.setMovieEditedListener(new MovieEditedListener() {

						@Override
						public void onEdited() {
							myPursueVideoFragment.setEdited(true);
						}

						@Override
						public void onDelete(final List<? extends Film> list) {
							new TVAlertDialog.Builder(MyMagicActivity.this)
									.setTitle(R.string.mymagic_mymovie_delete)
									.setPositiveButton(
											R.string.ok,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													deletePursueVideo(list);
													myPursueVideoFragment
															.setEdited(false);
												}
											})
									.setNegativeButton(
											R.string.cancel,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

												}
											}).create().show();
						}

						@Override
						public void onClear(List<? extends Film> list) {
							new TVAlertDialog.Builder(MyMagicActivity.this)
									.setTitle(
											R.string.mymagic_mymovie_pursue_clear)
									.setPositiveButton(
											R.string.ok,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													deleteAllPursueVideo();
												}
											})
									.setNegativeButton(
											R.string.cancel,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											}).create().show();

						}

						@Override
						public void onCancel() {
							myPursueVideoFragment.setEdited(false);
						}
					});
			myPursueVideoFragment.setMovieViewListener(new MovieViewListener() {

				@Override
				public void onPlay(Film item) {

				}

				@Override
				public void onItemSelected(Film item,int index) {
					gotoDetail(item);
				}

				@Override
				public void onGotoPage(int pageNo) {
					currentPage=pageNo;
					getPursueVideoList(pageNo);
				}
			});
			ft.replace(R.id.middle, myPursueVideoFragment);
			// ft.add(R.id.middle, myPursueVideoFragment);
		} else {
			ft.show(myPursueVideoFragment);
		}
		ft.commit();
	}

	private void hideMyPursueVideoView() {
		if (myPursueVideoFragment != null) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			// ft.hide(myPursueVideoFragment);
			ft.remove(myPursueVideoFragment);
			ft.commit();
			myPursueVideoFragment = null;
			currentPage=-1;
		}
	}

	private void showMessageCenterView() {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (messageCenterFragment == null) {
			messageCenterFragment = new MessageCenterFragment();
			// ft.add(R.id.middle, messageCenterFragment);
			ft.replace(R.id.middle, messageCenterFragment);
		} else {
			ft.show(messageCenterFragment);
		}
		ft.commit();
	}

	private void hideMessageCenterView() {
		if (messageCenterFragment != null) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			// ft.hide(messageCenterFragment);
			ft.remove(messageCenterFragment);
			ft.commit();
			messageCenterFragment = null;
		}
	}

	/**
	 * 用户注册
	 * 
	 * @param account
	 * @param email
	 * @param password
	 * @param mobile
	 * @param authCode
	 */
	private void register(final String account, final String email,
			final String password, final String mobile, final String authCode) {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				registerResult = AccountManager.GetInstance().registerAccount(
						account, email, mobile, password, authCode);
				if (registerResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					sendMessage(GET_REGISTER_SUCCESS);
				}
			}
		}).start();
	}

	private void getUserOrderList(boolean isProduct) {
		this.isProduct = isProduct;
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				orderListInfo = AccountManager.GetInstance().getOrderInfo();
				if (orderListInfo != null) {
					sendMessage(ORDERLISTINFO_SUCCESS);
				} else {
					sendMessage(ORDERLISTINFO_FAIL);
				}
			}
		}).start();
	}

	private void getUserProductList() {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				products = AccountManager.GetInstance().getUserProduct();
				if (products != null) {
					if (products.size() > 0) {
						sendMessage(PRODUCT_SUCCESS);
					} else {
						sendMessage(PRODUCT_FAIL);
					}
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			}
		}).start();
	}

	private void getOrderResult(final Product product) {
		this.orderProduct = product;
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				orderResult = AccountManager.GetInstance().order(product);
				if (orderResult != null) {
					LogUtil.d("getOrderResult---->status"
							+ orderResult.getStatus());
					if ("004".equals(orderResult.getStatus())) {
						sendMessage(ORDERRESULT_FAIL);
					} else {
						sendMessage(ORDERRESULT_SUCCESS);
					}
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			}
		}).start();
	}

	private void cancelOrder(final Product product) {
		this.orderProduct = product;
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				orderResult = AccountManager.GetInstance().cancelOrder(product);
				if (orderResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					sendMessage(ORDERRESULT_SUCCESS);
				}
			}
		}).start();
	}

	private void getConsumeListInfo() {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				consumeListInfo = AccountManager.GetInstance().getConsumeInfo();
				if (consumeListInfo != null) {
					if (consumeListInfo.getConsumeList() != null
							&& consumeListInfo.getConsumeList().size() > 0) {
						sendMessage(CONSUMELISTINFO_SUCCESS);
					} else {
						sendMessage(CONSUMELISTINFO_FAIL);
					}
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			}
		}).start();
	}

	private void getRecharList() {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				rechargeListInfo = AccountManager.GetInstance()
						.getRechargeInfo();
				if (rechargeListInfo != null) {
					sendMessage(RECHARGELISTINFO_SUCCESS);
				} else {
					sendMessage(RECHARGELISTINFO_FAIL);
				}
			}
		}).start();
	}

	/**
	 * 
	 * @param uid
	 * @param cardNo
	 */
	private void getRecharResult(final String uid, final String cardNo) {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				rechargeResult = AccountManager.GetInstance().recharge(uid,
						cardNo);
				if (rechargeResult != null) {
					sendMessage(RECHARGERESULT_SUCCESS);
				} else {
					sendMessage(RECHARGERESULT_FAIL);
				}
			}
		}).start();
	}

	/**
	 * 获得我的收藏
	 * 
	 * @param currentPageNo
	 */
	private void getFavoriteList(final int currentPageNo) {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				favoriteInfo = TransScreenManager.GetInstance()
						.getFavoriteList(currentPageNo, 12);
				if (favoriteInfo == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					if (favoriteInfo.getFilmList() == null) {
						sendMessage(FAVORITE_FAIL);
					} else {
						if (favoriteInfo.getFilmList().size() == 0) {
							sendMessage(FAVORITE_EMPTY);
						} else {
							sendMessage(FAVORITE_SUCCESS);
						}
					}
				}
			}
		}).start();
	}

	/**
	 * 删除观看记录
	 * @param films
	 */
	private void deletedHistory(final List<? extends Film> films) {
		showDialog();
		new Thread() {
			public void run() {
				historyResult = TransScreenManager.GetInstance().deleteResume(
						(List<ResumeFilm>) films);
				if (historyResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					sendMessage(HISTORY_DELETE_SUCCESS);
					for (Film film : films) {
						CooperationManager.GetInstance().getCooperation().deletePlayHistory(film.getMid());
					}
				}
			};
		}.start();
	}

	/**
	 * 删除我的收藏
	 * 
	 * @param films
	 */
	private void deletedFavorite(final List<? extends Film> films) {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				favoriteResult = TransScreenManager.GetInstance()
						.deleteFavorite((List<Film>) films);
				if (favoriteResult != null) {
					sendMessage(FAVORITE_DELETE_SUCCESS);
					for (Film film : films) {
						CooperationManager.GetInstance().getCooperation().deleteFavorite(film.getMid());
					}
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			}
		}).start();
	}

	/**
	 * 获得验证码
	 * 
	 * @param mobile
	 */
	private void getCode(final String mobile) {
		showDialog();
		new Thread() {
			@Override
			public void run() {
				codeResult = AccountManager.GetInstance().getAuthCode(mobile);
				if (codeResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					sendMessage(GET_AUTHCODE_SUCCESS);
				}
			}
		}.start();
	}

	/**
	 * 删除所有我的收藏
	 */
	private void deleteAllFavorite() {
		showDialog();
		new Thread() {
			public void run() {
				favoriteResult = TransScreenManager.GetInstance()
						.deleteAllFavorite();
				if (favoriteResult != null) {
					sendMessage(FAVORITE_DELETE_SUCCESS);
					CooperationManager.GetInstance().getCooperation().deleteAllFavorite();
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}

			};
		}.start();

	}

	/**
	 * 获得观看历史
	 * 
	 * @param currentPageNo
	 */
	private void getHistoryList(final int currentPageNo) {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				historyInfo = TransScreenManager.GetInstance().getResumeList(
						currentPageNo, 12);
				if (historyInfo == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					if (historyInfo.getFilmList() == null) {
						sendMessage(HISTORY_FAIL);
					} else {
						if (historyInfo.getFilmList().size() == 0) {
							sendMessage(HISTORY_EMPTY);
						} else {
							sendMessage(HISTORY_SUCCESS);
						}
					}
				}
			}
		}).start();
	}

	/**
	 * 删除所有观看历史
	 */
	private void deleteAllHistory() {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				historyResult = TransScreenManager.GetInstance()
						.deleteAllResume();
				if (historyResult != null) {
					sendMessage(HISTORY_DELETE_SUCCESS);
					CooperationManager.GetInstance().getCooperation().deleteAllPlayHistory();
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			}
		}).start();
	}

	/**
	 * 删除所有追剧
	 */
	private void deleteAllPursueVideo() {
		showDialog();
		new Thread() {
			public void run() {
				pursueResult = TransScreenManager.GetInstance().deleteAllTele();
				if (pursueResult != null) {
					sendMessage(PURSUEVIDEO_DELETE_SUCCESS);
					CooperationManager.GetInstance().getCooperation().deleteAllZhuiju();
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			};
		}.start();
	}

	/**
	 * 删除追剧
	 * 
	 * @param films
	 */
	private void deletePursueVideo(final List<? extends Film> films) {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				pursueResult = TransScreenManager.GetInstance().deleteTele(
						(List<Film>) films);
				if (pursueResult == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					sendMessage(PURSUEVIDEO_DELETE_SUCCESS);
					for (Film film : films) {
						CooperationManager.GetInstance().getCooperation().deleteZhuiJu(film.getMid());
					}
				}
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (messageCenterFragment != null
					&& messageCenterFragment.getMessageEmailView()
							.getVisibility() == View.VISIBLE) {
				if (messageCenterFragment.getMessageCenterView() != null) {
					messageCenterFragment.getMessageCenterView()
							.hideEmailView();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 获得追剧列表
	 * 
	 * @param currentPageNo
	 */
	private void getPursueVideoList(final int currentPageNo) {
		showDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				pursueVideoInfo = TransScreenManager.GetInstance().getTeleList(
						currentPageNo, 12);
				if (pursueVideoInfo == null) {
					sendMessage(ACCESS_NET_FAIL);
				} else {
					if (pursueVideoInfo.getFilmList() == null) {
						sendMessage(PURSUEVIDEO_FAIL);
					} else {
						if (pursueVideoInfo.getFilmList().size() == 0) {
							sendMessage(PURSUEVIDEO_EMPTY);
						} else {
							sendMessage(PURSUEVIDEO_SUCCESS);
						}
					}
				}
			}
		}).start();
	}

	private void gotoDetail(Film film) {
		if (film != null) {
			Intent intent = new Intent();
			intent.putExtra("intentMid", film.getMid());
			intent.setClass(MyMagicActivity.this, MovieDetailActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 获得观看排行
	 * 
	 * @param string
	 */
	private void getReleatedFilmList(String string) {
		showHintString = string;
		showDialog();
		new Thread() {
			public void run() {
				releatedFilms = MovieManager.GetInstance().getTopViewMovies();
				if (releatedFilms != null) {
					sendMessage(SUCCESS_RELEATED_FILM);
				} else {
					sendMessage(ACCESS_NET_FAIL);
				}
			};
		}.start();
	}

	private void showCheckAlert(String text) {
		new TVAlertDialog.Builder(MyMagicActivity.this)
				.setTitle(text)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();
	}

}
