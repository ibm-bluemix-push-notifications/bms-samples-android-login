package com.ibm.mobilefirstplatform.clientsdk.android.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by mygirl on 23/07/16.
 */
public class RegisterActivity extends Activity implements View.OnClickListener{

    Button btnClickMe;
    Button autoLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        updateTextView("Starting Register View");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("logout",false ) == true){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("logout", false);
            editor.apply();
            finish();
        }


        final Activity activity = this;

         btnClickMe = (Button) findViewById(R.id.btnRegister);
         autoLoad = (Button) findViewById(R.id.autoLoad);

         btnClickMe.setOnClickListener(RegisterActivity.this);
        autoLoad.setOnClickListener(RegisterActivity.this);


    }

    public void updateTextView(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(str);
                System.out.println("\n");
            }
        });
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("myConfig.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onClick(View v) {

        //Your Logic

        switch(v.getId()) {
            case R.id.autoLoad:

                try {
                    JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
                    ((EditText)findViewById(R.id.reg_appRoute)).setText(jsonObject.getString("appRoute"));
                    ((EditText)findViewById(R.id.reg_appGuid)).setText(jsonObject.getString("appGUID"));
                    ((EditText)findViewById(R.id.reg_pushGuid)).setText(jsonObject.getString("pushGUID"));
                    ((EditText)findViewById(R.id.reg_clientSecret)).setText(jsonObject.getString("clientSecret"));
                    ((EditText)findViewById(R.id.reg_region)).setText(jsonObject.getString("appRegion"));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btnRegister:

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                EditText appRoute   = (EditText)findViewById(R.id.reg_appRoute);
                EditText appGUID   = (EditText)findViewById(R.id.reg_appGuid);
                EditText pushGUID   = (EditText)findViewById(R.id.reg_pushGuid);
                EditText clientSecret   = (EditText)findViewById(R.id.reg_clientSecret);
                EditText appRegion   = (EditText)findViewById(R.id.reg_region);

                editor.putString("appRoute", appRoute.getText().toString());
                editor.putString("appGUID", appGUID.getText().toString());
                editor.putString("pushGUID", pushGUID.getText().toString());
                editor.putString("clientSecret", clientSecret.getText().toString());
                editor.putString("appRegion", appRegion.getText().toString());

                editor.apply();

                ((EditText) findViewById(R.id.reg_appRoute)).getText().clear();
                ((EditText) findViewById(R.id.reg_appGuid)).getText().clear();
                ((EditText) findViewById(R.id.reg_pushGuid)).getText().clear();
                ((EditText) findViewById(R.id.reg_clientSecret)).getText().clear();
                ((EditText) findViewById(R.id.reg_region)).getText().clear();

                Intent i = new Intent(getApplicationContext(), SubscribeActivity.class);
                startActivity(i);
                //finish();
                break;

        }
    }
}
