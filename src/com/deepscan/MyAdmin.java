package com.deepscan;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;



public class MyAdmin extends DeviceAdminReceiver {


    SharedPreferences mPref;
   static Activity mActivity;
    public static class Controller extends Activity {



        DevicePolicyManager mDPM;
        ComponentName mDeviceAdminSample;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mActivity=Controller.this;
            mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDeviceAdminSample = new ComponentName(Controller.this,
                    MyAdmin.class);
        }
    }

    @Override
    public void onEnabled(Context ctxt, Intent intent) {
//        ComponentName cn=new ComponentName(ctxt, MyAdmin.class);
//        DevicePolicyManager mgr=
//                (DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
//
//        mgr.setPasswordQuality(cn,
//                DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
//
//        onPasswordChanged(ctxt, intent);
    }

    @Override
    public void onPasswordChanged(Context ctxt, Intent intent) {
//        DevicePolicyManager mgr= (DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        int msgId;
//
//        if (mgr.isActivePasswordSufficient()) {
//            msgId=R.string.compliant;
//        }
//        else {
//            msgId=R.string.not_compliant;
//        }

        // Toast.makeText(ctxt, msgId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {


        mPref = ctxt.getSharedPreferences(ctxt.getResources()
                .getString(R.string.pref_name), Activity.MODE_PRIVATE);


        if(mPref.getBoolean(Constant.isActive,true))
        {
            Intent front_translucent = new Intent(ctxt, PhotoTakingService.class);
            // front_translucent.putExtra("Front_Request", true);
            // front_translucent.putExtra("Quality_Mode",camCapture.getQuality());
            ctxt.startService(front_translucent);
        }



    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
      //  Toast.makeText(ctxt, R.string.password_success, Toast.LENGTH_LONG).show();
    }
}