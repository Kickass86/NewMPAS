package turbotec.newmpas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class StartActivity extends AppCompatActivity {

    private SharedPreferenceHandler share;
    private PendingIntent pendingIntent;
    private int interval = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        share = SharedPreferenceHandler.getInstance(this);

        Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session
        logUser();


        try {




            String fv = share.GetFileVersion();
            int v = 0;
            if (!fv.equals(getString(R.string.defaultValue))) {
                v = Integer.valueOf(fv);
            }

            PackageInfo pInfo;
            int VersionCode = 0;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                VersionCode = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            String MyFileAddress = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/MPAS-V" + v + ".apk";
            File myFile = new File(MyFileAddress);
            try {
                if ((myFile.exists()) & (VersionCode < v)) {
                    Intent InstallIntent = new Intent(this, InstallActivity.class);
                    InstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.MyFile), MyFileAddress);
                    InstallIntent.putExtras(bundle);
                    startActivity(InstallIntent);
                    finish();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            Intent in = new Intent(this, VersionUpdate.class);
            startService(in);


            String state = share.GetStatus();
            State st = State.valueOf(state);


            switch (st) {
                case OK: {

                    Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                    alarmIntent.setAction("Alarm");
                    pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    manager.cancel(pendingIntent);

                    manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);


                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }
                case Wait: {

                    setContentView(R.layout.wait_for_activation_layout);

                    Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                    alarmIntent.setAction("Alarm");
                    pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    manager.cancel(pendingIntent);

                    manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);
                }
                case Invalid:
                case Not_Saved:
                default: {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
//                    finish();
//                    return;
                }

            }
        } catch (Exception e) {
            share.SaveError(e.getMessage());
            Intent SE = new Intent(this, SendError.class);
            startService(SE);
        }
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(share.GetDeviceID());
        Crashlytics.setUserEmail(share.GetUsername());
        Crashlytics.setUserName(share.GetPassword());
    }


    private enum State {
        OK, Wait, Invalid, Not_Saved
    }
}
