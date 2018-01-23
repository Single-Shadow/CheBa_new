package com.aou.cheba.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.aou.cheba.R;
import com.aou.cheba.view.SwipeBackLayout;

/**
 * 
 * @author xiaanming
 *
 */
public class SwipeBackActivity extends Activity {
	protected SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
	}
	
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}




	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}


}
