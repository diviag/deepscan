package com.deepscan;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class SendMailTask extends AsyncTask {

    //  private ProgressDialog statusDialog;
    private Context mContext;

    public SendMailTask(Context activity) {
        mContext = activity;

    }

    protected void onPreExecute() {
//        statusDialog = new ProgressDialog(sendMailActivity);
//        statusDialog.setMessage("Getting ready...");
//        statusDialog.setIndeterminate(false);
//        statusDialog.setCancelable(false);
//        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            GMail androidEmail = new GMail(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString(), mContext);
            publishProgress("Preparing mail message....");

            if ((Boolean) args[5]) {
                //for attachment task
                androidEmail.createEmailWithAttachment(args[4].toString(),args[6].toString());
                publishProgress("Sending email....");
                androidEmail.sendEmail();
                publishProgress("Email Sent.");
                Log.i("SendMailTask", "Mail Sent.");

            } else {

                //for non attachment task
                androidEmail.createEmailMessage();
                publishProgress("Sending email....");
                androidEmail.sendEmail();
                publishProgress("Email Sent.");
                Log.i("SendMailTask", "Mail Sent.");
            }

        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        // statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
        //statusDialog.dismiss();

    }

}