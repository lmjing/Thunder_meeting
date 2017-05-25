package com.sopt.android.thunder.GCM;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sopt.android.thunder.R;


/**
 * Created by Hong on 2016-01-12.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public RegistrationIntentService() {
        super(TAG);
    }

    //GCM을 위한 Instance ID의 토큰을 생성하여 가져온다.
    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"일단함수실행 0");
        setting = getSharedPreferences("setting", 0);
        Log.i(TAG,"일단함수실행 1");
        editor = setting.edit();
        Log.i(TAG,"일단함수실행 2");
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(QuickstartPreferences.REGISTRATION_GENERATING));
        Log.i(TAG, "일단함수실행 3");
        // GCM을 위한 Instance ID를 가져온다.
        InstanceID instanceID = InstanceID.getInstance(this);

        Log.i(TAG,"일단함수실행 4");


        String token = null;
        try {
            synchronized (TAG) {
                token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                /*
                String default_senderId = getString(R.string.gcm_defaultSenderId);
                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
                token = instanceID.getToken(default_senderId, scope, null);
                */
                Log.i(TAG, "GCM Registration Token: " + token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to complete token refresh", e);
            setting.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        editor.putString("Token", token);
        editor.commit();

        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}