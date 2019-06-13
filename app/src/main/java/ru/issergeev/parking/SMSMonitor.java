package ru.issergeev.parking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

public class SMSMonitor extends BroadcastReceiver {
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null &&
                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }

            String sms_from = messages[0].getDisplayOriginatingAddress();
            StringBuffer bodyText = new StringBuffer();
            for (int i = 0; i < messages.length; i++) {
                bodyText.append(messages[i].getMessageBody());
            }

            Intent service = new Intent(context, SMSService.class);

            if (sms_from.length() == 4) {
                if (sms_from.equals(context.getResources().getString(R.string.MOS_PARKING)) && bodyText.indexOf("авторизована") > -1) {
                    String body = bodyText.toString();
                    service.putExtra("sms_body", body);
                } else if (bodyText.toString().compareToIgnoreCase("Оплата не требуется") == 0) {
                    service.putExtra("sms_body", "No_pay");
                } else if (bodyText.indexOf("Платеж выполнен") < 0){
                    service.putExtra("sms_body", sms_from);
                } else if (bodyText.indexOf("Парковка завершена") > -1) {
                    service.putExtra("sms_body", "End");
                } else if (bodyText.indexOf("сессии отсутствуют") > -1) {
                    service.putExtra("sms_body", "No_sessions");
                }

                context.startService(service);
                abortBroadcast();
            }
        }
    }
}