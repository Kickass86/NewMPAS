package turbotec.newmpas;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;




public class LoginActivity extends AppCompatActivity {

    List<String> Mlist = new ArrayList<>(); //Messages List
    List<String> Tlist = new ArrayList<>(); //Title List
    //        List<String> Dlist = new ArrayList<>(); //Date List
    List<Boolean> SList = new ArrayList<>(); //is Seen
    List<Integer> IList = new ArrayList<>(); //Message ID
    List<Boolean> CList = new ArrayList<>(); //Critical
    List<Boolean> SSList = new ArrayList<>(); //SendSeen

    private SharedPreferenceHandler share;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            Log.i("is this ", "BroadcastReceiver");
            String action = intent.getAction();

            setTitle(getString(R.string.app_name));
            if (action.equals("Alarm fire")) {

                String stat = share.GetStatus();
                if (stat.contains("OK")) {
//                    UpdateUI(share.GetStatus());
                    intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

//                    Log.i("is this ", "BroadcastReceiver");
                }
                else{
                    startActivity(new Intent(context, LoginActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                }

            }
        }
    };
    private int Scroll_Position = 0;
    private boolean[] mCheckedState;
    private Menu mMenu;
    private boolean isSelected = false;
    private int interval = 60000;
    private String Username;
    private String Password;
    private String DeviceID;
    private boolean first = false;
    private boolean flag = false;
    //    private static String DeviceID;
//    private static String username;
//    private static String password;
    private PendingIntent pendingIntent;
    private EditText UsernameView;
    private EditText PasswordView;


    @SuppressWarnings("deprecation")
    private static String getUniquePsuedoID() {

        String m_szDevIDShort;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            m_szDevIDShort = "46cd" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.SUPPORTED_ABIS[0].length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        } else {
            m_szDevIDShort = "46cd" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        }


        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


    @Override
    protected void onDestroy() {

        if (flag) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
//        unregisterReceiver(NotifyReceiver);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {


//        NotificationsAdapter.getInstance(this);
            share = SharedPreferenceHandler.getInstance(this);
            first = true;
            mCheckedState = new boolean[0];

            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.setAction("Alarm");
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);


            if (share.GetStatus().equals(getString(R.string.defaultValue))) {
                setTitle(getString(R.string.app_name));
            } else {
                setTitle(getString(R.string.Waiting_for_Network));
            }


            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();


            String state = share.GetStatus();

            State st = State.valueOf(state);

            switch (st) {
                case OK: {

                    setContentView(R.layout.notification_tab);

                    alarmIntent = new Intent(this, AlarmReceiver.class);
                    alarmIntent.setAction("Alarm");
                    pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    manager.cancel(pendingIntent);

                    manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);
                    Log.i("Alarm", "Set");
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);


                    break;
                }
                case Wait: {
                    setContentView(R.layout.wait_for_activation_layout);
                    alarmIntent = new Intent(this, AlarmReceiver.class);
                    alarmIntent.setAction("Alarm");
                    pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    manager.cancel(pendingIntent);

                    manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);

                    Log.i("Alarm", "Set");

                    break;
                }
                case Invalid:
                case Not_Saved:
                default: {
//                if (!first) {
//                    setContentView(R.layout.waiting_layout);
//                } else {
                    setContentView(R.layout.login_layout);
                    registerReceiver(broadcastReceiver, new IntentFilter("Alarm fire"));
                    flag = true;


//            new Thread(new Runnable() {
//                @Override
//                public void run() {

                    UsernameView = (EditText) findViewById(R.id.editText2);
                    PasswordView = (EditText) findViewById(R.id.editText);
                    Button registerButton = (Button) findViewById(R.id.button);

                    UsernameView.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                    PasswordView.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                                String Username = UsernameView.getText().toString();
                                String Password = PasswordView.getText().toString();
                                String DeviceID = getUniquePsuedoID();
                                share.SaveLoginDetails(Username, Password);
                                share.SaveDeviceID(DeviceID);

                                attemptLogin(Username, Password, DeviceID);
                            }
                            return false;
                        }
                    });


                    registerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                            String Username = UsernameView.getText().toString();
                            String Password = PasswordView.getText().toString();
                            String DeviceID = getUniquePsuedoID();
                            share.SaveLoginDetails(Username, Password);
                            share.SaveDeviceID(DeviceID);

                            attemptLogin(Username, Password, DeviceID);
//                Log.i("Successful Login ", "Welcome " + Name);
                        }
                    });
//                }

                }
            }

        } catch (Exception e) {
            share.SaveError(e.getMessage());
            Intent SE = new Intent(this, SendError.class);
            startService(SE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Intent in = new Intent(this, VersionUpdate.class);
//        startService(in);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        first = true;
    }

    private void attemptLogin(String username, String password, String DeviceID) {

//        String result = getString(R.string.Invalid);

        UsernameView.setError(null);
        PasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            PasswordView.setError(getString(R.string.error_invalid_password));
            focusView = PasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            UsernameView.setError(getString(R.string.error_field_required));
            focusView = UsernameView;
            cancel = true;
        }

//        String s = "";
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // save data in local shared preferences
            Username = username;
            Password = password;
            this.DeviceID = DeviceID;
            String[] Userdata = {username, password, DeviceID};
//            first = true;

            share.SaveDeviceID(DeviceID);
            share.SaveLoginDetails(username, password);

//            NetworkAsyncTask task = new NetworkAsyncTask(this);
//            task.execute(Userdata);
            Intent AL = new Intent(this, SyncService.class);
            startService(AL);
            setTitle(R.string.Connecting);
            setContentView(R.layout.waiting_layout);

//            UpdateUI(username, password, DeviceID, null);
        }


    }








    private enum State {
        OK, Wait, Invalid, Not_Saved
    }






}
