package com.voole.epg.base;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.voole.epg.base.common.LogoView;

public class LogoViewTcl extends LogoView {
	Context context;
	public LogoViewTcl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public LogoViewTcl(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}


	public LogoViewTcl(Context context) {
		super(context);
		this.context = context;
		init();
		
	}
	private void init() {
		getBtnAccount().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMymagic(context, "CONSUMERCENTER", 0);
			}
		});
		getBtnRecharge().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoMymagic(context, "CONSUMERCENTER", 0);
			}
		});
		getBtnHistory().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMymagic(context, "MYHISTORY");
			}
		});
		getBtnFavorite().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMymagic(context, "MYFAVORITE");
			}
		});
		getBtnSearch().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSearch(context);
			}
		});
		
		
	}
	
	private void gotoMymagic(Context context, String where, int index) {
		Intent intent = new Intent();
		intent.setAction("com.voole.epg.action.Mymagic_tcl");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("toWhere", where);
		intent.putExtra("index", index);
		context.startActivity(intent);
	}
	
	private void gotoSearch(Context context) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction("com.voole.epg.action.Search_tcl");
		context.startActivity(intent);
	}
	
	private void gotoMymagic(Context context, String where) {
		Intent intent = new Intent();
		intent.setAction("com.voole.epg.action.Mymagic_tcl");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("toWhere", where);
		context.startActivity(intent);
	}

}
