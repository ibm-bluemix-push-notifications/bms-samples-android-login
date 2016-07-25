    package com.ibm.mobilefirstplatform.clientsdk.android.app;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.preference.PreferenceManager;
    import android.view.Menu;
    import android.view.MotionEvent;
    import android.view.View;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.Button;
    import android.widget.EditText;
    import android.view.Menu;
    import android.view.ViewGroup;
    import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
    import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
    import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
    import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
    import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
    import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;
    import android.widget.TextView;
    import android.app.AlertDialog.Builder;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import java.net.MalformedURLException;
    import org.json.JSONObject;

    import java.util.Arrays;
    import java.util.List;

    /**
     * Created by mygirl on 23/07/16.
     */
    public class SubscribeActivity  extends Activity implements View.OnClickListener{

        Button getTags;
        Button subscribe;
        Button unSubscribe;
        Button logout;

        String  appRoute = "";
        String  appGuid = "";
        String  pushGuid = "";
        String  clientSecret = "";
        String  appRegion = "";
        String  userId = "";

        private MFPPush push = null;
        private MFPPushNotificationListener notificationListener = null;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.subscribe_activity);
            updateTextView("Starting Register View");

            final Activity activity = this;

            getTags = (Button) findViewById(R.id.getTags);
            subscribe = (Button) findViewById(R.id.subscribe);
            unSubscribe = (Button) findViewById(R.id.unSubscribe);
            logout = (Button) findViewById(R.id.logout);


            getTags.setOnClickListener(SubscribeActivity.this);
            subscribe.setOnClickListener(SubscribeActivity.this);
            unSubscribe.setOnClickListener(SubscribeActivity.this);
            logout.setOnClickListener(SubscribeActivity.this);


           if (validateReg()) {
               try {
                   BMSClient.getInstance().initialize(getApplicationContext(), appRoute, appGuid, appRegion);

               } catch (MalformedURLException e) {
                   e.printStackTrace();
               }

               push = MFPPush.getInstance();
               push.initialize(getApplicationContext(), pushGuid, clientSecret);
               push.registerDeviceWithUserId(userId,new MFPPushResponseListener<String>() {
                   @Override
                   public void onSuccess(String deviceId) {
                       updateTextView("Device is registered with Push Service.");
                       alerts("Device is registered with Push Service.");
                   }

                   @Override
                   public void onFailure(MFPPushException ex) {
                       updateTextView("Error registering with Push Service...\n" + ex.toString()
                               + "Push notifications will not be received.");
                       alerts("error in credentials");
                       finish();
                   }
               });
               notificationListener = new MFPPushNotificationListener() {

                   @Override
                   public void onReceive(final MFPSimplePushNotification message) {
                       showNotification(activity, message);
                   }
               };
           } else {
               alerts("Error in credentials");
               finish();
           }

        }
        private void alerts(final String message){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog alertDialog = new AlertDialog.Builder(SubscribeActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage(message);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });

        }

        public boolean validateReg() {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String user = preferences.getString("userId", "");
            if(!user.equalsIgnoreCase("") && !user.isEmpty() && user != null)
            {
                userId = user;
                String app_Route = preferences.getString("appRoute", "");
                if(!app_Route.equalsIgnoreCase("") && !app_Route.isEmpty() && app_Route != null)
                {
                    appRoute = app_Route;
                    String app_GUID = preferences.getString("appGUID", "");
                    if(!app_GUID.equalsIgnoreCase("") && !app_GUID.isEmpty() && app_GUID != null)
                    {
                        appGuid = app_GUID;
                        String push_GUID = preferences.getString("pushGUID", "");
                        if(!push_GUID.equalsIgnoreCase("") && !push_GUID.isEmpty() && push_GUID != null)
                        {
                            pushGuid = push_GUID;
                            String client_Secret = preferences.getString("clientSecret", "");
                            if(!client_Secret.equalsIgnoreCase("") && !client_Secret.isEmpty() && client_Secret != null)
                            {
                                clientSecret = client_Secret;
                                String app_Region = preferences.getString("appRegion", "");
                                if(!app_Region.equalsIgnoreCase("") && !app_Region.isEmpty() && app_Region != null)
                                {
                                    appRegion = app_Region;

                                } else {
                                    return false;
                                }

                            } else {
                                return false;
                            }

                        } else {
                            return false;
                        }

                    } else {
                        return false;
                    }

                } else {
                    return false;
                }

            } else {
                return false;
            }

            return true;
        }
        @Override
        public void onClick(View v) {

            //Your Logic

            switch(v.getId()) {
                case R.id.getTags:

                    displayTags();
                    break;
                case R.id.subscribe:
                    subscribeToTag();
                    break;
                case R.id.unSubscribe:
                    unsubscribeFromTags();
                    break;
                case R.id.logout:
                    unregisterDevice();
                    break;

            }
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
        public boolean dispatchTouchEvent(MotionEvent ev) {
            View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
            return super.dispatchTouchEvent(ev);
        }


        @Override
        protected void onResume() {
            super.onResume();
            if (push != null) {
                push.listen(notificationListener);
            }
        }

        void showNotification(Activity activity, MFPSimplePushNotification message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Notification Received : " + message.toString());
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        }

        void displayTags() {
            push.getTags(new MFPPushResponseListener<List<String>>() {
                @Override
                public void onSuccess(final List<String> tags) {
                    updateTextView("Retrieved Tags : " + tags.toString());
                    alerts("Retrieved Tags :");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tags.size() > 0){
                                EditText textView = (EditText) findViewById(R.id.get_tags);
                                textView.setText(tags.toString());
                            }

                        }
                    });

                }

                @Override
                public void onFailure(MFPPushException ex) {
                    updateTextView("Error getting tags..." + ex.getMessage());
                }
            });
        }

        void unregisterDevice() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    push.unregister(new MFPPushResponseListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            updateTextView("Device is successfully unregistered. Success response is: " + s);
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            finish();
                        }

                        @Override
                        public void onFailure(MFPPushException e) {
                            updateTextView("Device unregistration failure. Failure response is: " + e);
                            alerts("Device unregistration failure. Failure response is: " + e);
                        }
                    });
                }
            });

        }
        void unsubscribeFromTags() {

          runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = ((EditText)findViewById(R.id.unSubscribe_tags)).getText().toString();
                List<String> allTags = Arrays.asList(str.split("\\s*,\\s*"));
                for (int i = 0 ; i < allTags.size(); i++) {
                    push.unsubscribe(allTags.get(i), new MFPPushResponseListener<String>() {


                        @Override
                        public void onSuccess(String s) {
                            updateTextView("Unsubscribing from tag");
                            updateTextView("Successfully unsubscribed from tag . " );
                            alerts("Successfully unsubscribed from tag .");
                        }

                        @Override
                        public void onFailure(MFPPushException e) {
                            updateTextView("Error while unsubscribing from tags. " + e.getMessage());
                            alerts("Error while unsubscribing from tags. " + e.getMessage());
                        }

                    });
                }
            }
          });

        }

        void subscribeToTag() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String str = ((EditText)findViewById(R.id.subscribe_tags)).getText().toString();
                    List<String> allTags = Arrays.asList(str.split("\\s*,\\s*"));

                    for (int i = 0 ; i < allTags.size(); i++) {
                        final String tag = allTags.get(i);
                        push.subscribe(allTags.get(i),
                                new MFPPushResponseListener<String>() {
                                    @Override
                                    public void onFailure(MFPPushException ex) {
                                        updateTextView("Error subscribing to "+tag
                                                + ex.getMessage());
                                        alerts("Error subscribing to: " + tag);
                                    }

                                    @Override
                                    public void onSuccess(String arg0) {
                                        updateTextView("Succesfully Subscribed to: "+ tag);
                                        alerts("Succesfully Subscribed to: "+ tag);
                                    }
                                });
                    }
                }
            });

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        protected void onPause() {
            super.onPause();

            if (push != null) {
                push.hold();
            }

        }

    }
