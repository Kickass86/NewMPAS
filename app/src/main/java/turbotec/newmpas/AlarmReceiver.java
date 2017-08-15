package turbotec.newmpas;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

//import java.lang.ref.WeakReference;

//AlarmReceiver

public class AlarmReceiver extends BroadcastReceiver {

    private static final String BOOT_COMPLETED =
            "android.intent.action.BOOT_COMPLETED";
    private static final String QUICKBOOT_POWERON =
            "android.intent.action.QUICKBOOT_POWERON";
    //    private static DatabaseHandler db;
//    private Boolean b = false;
    //    private WeakReference<MainActivity> MyActivity;
    //    private MainActivity MyActivity;
    private SharedPreferenceHandler share;

//    private SharedPreferenceHandler share;
    private Context mContext;
//    private MessageObject MObj;


    public AlarmReceiver() {


//        this.MyActivity = new WeakReference<MainActivity>(activity);
    }

    private Boolean CheckNetworkAvailability() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }



    @Override
    public void onReceive(final Context context, Intent intent) {


        mContext = context;
        share = SharedPreferenceHandler.getInstance(mContext);

        String action = intent.getAction();
        if ((action.equals(BOOT_COMPLETED) || action.equals(QUICKBOOT_POWERON))
                & (share.GetStatus().equals("OK") || share.GetStatus().contains("Wait"))) {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.setAction("Alarm");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = 60000;

            manager.cancel(pendingIntent);

//            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);
        }


        if (action.equals("Alarm") && (CheckNetworkAvailability())) {
//        if (intent.getAction().equals("Alarm")) {


            mContext.sendBroadcast(new Intent("Alarm fire"));

            Log.v("AAA", "Alarm fired now!");

            //TODO service for getting new notifications
            //                b = (Boolean) task.execute(Userdata).get();
//            NetworkAsyncTask task1 = new NetworkAsyncTask(mContext);
//                task1.execute(Userdata).get();
//            task1.execute();
            Intent AL = new Intent(mContext, SyncService.class);
            mContext.startService(AL);

            if (share.GetLastCheckDay() != 0) {
                Calendar c = Calendar.getInstance();
                int day = share.GetLastCheckDay();
                int hour = share.GetLastCheckHour();
                int current_day = c.get(Calendar.DAY_OF_YEAR);
                int current_hour = c.get(Calendar.HOUR_OF_DAY);

                int dif_hour = current_hour - hour;
                int dif_day = current_day - day;

                if ((dif_hour > 20) | (dif_day > 1) | ((dif_day == 1) & (dif_hour > -4))) {
                    Intent in = new Intent(mContext, VersionUpdate.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Update", true);
                    in.putExtras(bundle);
                    mContext.startService(in);
                    share.SaveLastCheckDay(current_day);
                    share.SaveLastCheckHour(current_hour);
                }

            }


        }

    }
}

