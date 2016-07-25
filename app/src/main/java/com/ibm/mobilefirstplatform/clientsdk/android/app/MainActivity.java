package com.ibm.mobilefirstplatform.clientsdk.android.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;

import java.util.List;

import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.login_activity);
        Button btnClickMe;
        updateTextView("Starting Push Android Sample..");

        btnClickMe = (Button) findViewById(R.id.btnLogin);

        btnClickMe.setOnClickListener(MainActivity.this);

        final Activity activity = this;


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

    @Override
    public void onClick(View v) {

        //Your Logic

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        EditText mEdit   = (EditText)findViewById(R.id.userID);

        editor.putString("userId", mEdit.getText().toString());
        editor.putBoolean("logout",false);
        editor.apply();


        ((EditText) findViewById(R.id.userID)).getText().clear();
        ((EditText) findViewById(R.id.password)).getText().clear();

        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
    }
}
