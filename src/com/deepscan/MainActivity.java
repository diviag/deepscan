package com.deepscan;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

	LinearLayout txt_location;
	Switch switch1;
	SharedPreferences mPref;
	GPSTracker gps;
	TextView txt_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPref = getSharedPreferences(
				getResources().getString(R.string.pref_name),
				Activity.MODE_PRIVATE);
		txt_email = (TextView) findViewById(R.id.txt_email);
		switch1 = (Switch) findViewById(R.id.switch1);
		txt_email.setText(Constant.TO_EMAIL_PATTERN);
		txt_location = (LinearLayout) findViewById(R.id.txt_location);

		gps = new GPSTracker(MainActivity.this);

		if (gps.canGetLocation()) {
			txt_location.setVisibility(View.GONE);
		} else {
			txt_location.setVisibility(View.VISIBLE);

		}

		txt_location.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});

		if (mPref.getBoolean(Constant.isActive, true)) {
			switch1.setChecked(true);
		} else {
			switch1.setChecked(false);
		}

		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,
					boolean b) {

				if (b) {
					mPref.edit().putBoolean(Constant.isActive, true).commit();
				} else {
					mPref.edit().putBoolean(Constant.isActive, false).commit();
				}
			}
		});

		
		//device administration on kiya h 
		ComponentName cn = new ComponentName(this, MyAdmin.class);
		DevicePolicyManager mgr = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		if (mgr.isAdminActive(cn)) {
			int msgId;

			// if (mgr.isActivePasswordSufficient()) {
			// msgId=R.string.compliant;
			// }
			// else {
			// msgId=R.string.not_compliant;
			// }
			// Toast.makeText(this, msgId, Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					getString(R.string.device_admin_explanation));
			startActivity(intent);
		}

	}

}
