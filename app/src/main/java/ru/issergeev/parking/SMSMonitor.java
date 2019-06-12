package ru.issergeev.parking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

public class SMSMonitor extends BroadcastReceiver {
    private String addressee = "";
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
////---получить входящее SMS сообщение---
//        Bundle bundle = intent.getExtras();
//        SmsMessage[] msgs;
//        String str = "";
//
//        if(bundle != null) {
////---извлечь полученное SMS ---
//            Object[] pdus = (Object[]) bundle.get("pdus");
//            msgs = new SmsMessage[pdus.length];
//            for (int i = 0; i < msgs.length; i++){
//                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
//                str.concat("SMS from "+ msgs[i].getOriginatingAddress());
//                try {
//                    addressee.concat(msgs[i].getOriginatingAddress());
//                }catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//                str.concat(" :");
//                str.concat(msgs[i].getMessageBody());
//                str.concat("\n");
//            }
////---Показать новое SMS сообщение---
//            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
//
//        }

        if (intent != null && intent.getAction() != null &&
                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }


            String sms_from = messages[0].getDisplayOriginatingAddress();
            StringBuilder bodyText = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                bodyText.append(messages[i].getMessageBody());
            }
            String body = bodyText.toString();
            Intent mIntent = new Intent(context, SMSService.class);
            mIntent.putExtra("sms_body", body);
            context.startService(mIntent);
            abortBroadcast();

            Toast.makeText(context, bodyText, Toast.LENGTH_SHORT).show();
        }
    }
}