package ru.issergeev.parking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//---получить входящее SMS сообщение---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";

        if(bundle != null) {
//---извлечь полученное SMS ---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str.concat("SMS from "+ msgs[i].getOriginatingAddress());
                str.concat(" :");
                str.concat(msgs[i].getMessageBody());
                str.concat("\n");
            }
//---Показать новое SMS сообщение---
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }
}