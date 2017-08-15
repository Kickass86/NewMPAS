package turbotec.newmpas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class AlarmService extends Service {

    private int interval = 60000;
    private PendingIntent pendingIntent;


    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.setAction("Alarm");
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);


        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                long interval = INTERVAL_FIFTEEN_MINUTES;
//                    int interval = 60000;
        manager.cancel(pendingIntent);
//                manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }
}
