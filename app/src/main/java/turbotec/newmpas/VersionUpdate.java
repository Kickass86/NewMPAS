package turbotec.newmpas;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Calendar;


public class VersionUpdate extends Service {

    public static final String RESPONSE_MESSAGE = "myResponseMessage";
    public static final String REQUEST_DOWNLOAD = "myDownloadRequest";
    private final String ip = "192.168.1.13";
    private final int port = 80;
    private final SharedPreferenceHandler share;
    private URL url = null;
    private String requestString;
    private String downloadString;

    private boolean isStarted;

    private File myFile;
    private String MyFileAddress;
    private int latestVersion;
    private Long downloadReference;
    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d("Downloaded!", "1");
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //check if the broadcast message is for our Enqueued download
                share.SaveFileVersion(String.valueOf(latestVersion));

                DownloadManager dMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Cursor c = dMgr.query(new DownloadManager.Query().setFilterById(referenceId));

                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        //Download completed, celebrate

                        Log.d("Downloaded!", "2");
                        if (downloadReference == referenceId) {

                            Intent InstallIntent = new Intent(context, InstallActivity.class);
                            InstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.MyFile), MyFileAddress);
                            InstallIntent.putExtras(bundle);
                            startActivity(InstallIntent);


                        }



                    } else {
                        int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                        Log.d("Failed!", "Download not correct, status [" + status + "] reason [" + reason + "]");

                    }
                }
                c.close();


            }
        }
    };
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;


    public VersionUpdate() {
        super();
        share = SharedPreferenceHandler.getInstance(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        isStarted = false;
        Log.i("onCreate", "1");
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        boolean up = intent.getBooleanExtra("Update", false);

        if (!isStarted) {
            isStarted = true;
            // do something

            Log.i("onStartCommand", "1");
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        }
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private boolean isLocalReachable() {

        boolean exists = false;

        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 5000;   // 800 milliseconds
            sock.connect(sockaddr, timeoutMs);
            exists = true;

            sock.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return exists;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(downloadReceiver);
        Log.i("onDestroy", "1");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);


                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(downloadReceiver, filter);
                if (isLocalReachable()) {
                    requestString = "http://192.168.1.13/Andr/CheckVersion.ashx?Value=";
                    downloadString = "http://192.168.1.13/Andr/Download.ashx";

                } else {
                    requestString = "https://mpas.migtco.com:3000/Andr/CheckVersion.ashx?Value=";
                    downloadString = "https://mpas.migtco.com:3000/Andr/Download.ashx";

                }

//        String requestString = intent.getStringExtra(REQUEST_STRING);
                Log.v("Intent Service", "Check Request");
                int responseMessage;
                String value = "Val1=" + share.GetDeviceID() + ",Val2=" + share.GetToken();
                requestString = requestString + new String(Base64.encode(value.getBytes(), Base64.DEFAULT));
//        downloadString = downloadString + new String(Base64.encode(value.getBytes(), Base64.DEFAULT));
                requestString = requestString.replaceAll("\n", "");
//        downloadString = downloadString.replaceAll("\n", "");

                try {


                    url = new URL(requestString);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
//            c.setDoOutput(true);
                    c.connect();

//
//            URL1 url = new URL1(requestString);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.connect();
                    int code = c.getResponseCode();
                    String s = "";
                    if (code == 200) {

//                String s = urlConnection.getResponseMessage();
                        final InputStream is = c.getInputStream();

                        if (is != null) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                            String line;

                            while ((line = bufferedReader.readLine()) != null)
                                s += line;
                            is.close();
                        }

                    }
                    c.disconnect();
                    if (!(s.contains("Invalid") | s.contains("Error") | s.contains("Unable") | (s.contains("unexpected")))) {
                        responseMessage = Integer.valueOf(s);
                    } else {
                        responseMessage = 0;
                    }


                } catch (Exception e) {
                    Log.w("HTTP:", e);
                    responseMessage = 0;
                }


//        if(up) {
                latestVersion = responseMessage;
                String appURI = downloadString;

                String fv = share.GetFileVersion();

                PackageInfo pInfo;
                int VersionCode = 0;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                    VersionCode = pInfo.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                //get the app version Code for checking

                int v = 0;
                if (!fv.equals(getString(R.string.defaultValue))) {
                    v = Integer.valueOf(fv);
                }

                MyFileAddress = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/MPAS-V" + v + ".apk";
//                MyFileAddress = Environment.getExternalFilesDir() + "/MPAS-V" + v + ".apk";
                myFile = new File(MyFileAddress);

                if (latestVersion != 0) {
                    Calendar c = Calendar.getInstance();
                    int current_day = c.get(Calendar.DAY_OF_YEAR);
                    int current_hour = c.get(Calendar.HOUR_OF_DAY);
                    share.SaveLastCheckDay(current_day);
                    share.SaveLastCheckHour(current_hour);
                }

                if (((latestVersion == 0) & (v > VersionCode)) & (myFile.exists()))// file downloaded before
                {

                    Intent InstallIntent = new Intent(getApplicationContext(), InstallActivity.class);
                    InstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.MyFile), MyFileAddress);
                    InstallIntent.putExtras(bundle);
                    startActivity(InstallIntent);

                } else {

                    try {
//

                        if ((latestVersion != 0) & (latestVersion > (VersionCode))) {


                            //check if we need to upgrade?
                            MyFileAddress = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/MPAS-V" + latestVersion + ".apk";
//                            MyFileAddress = Environment.getExternalStorageState() + "/MPAS-V" + latestVersion + ".apk";
                            myFile = new File(MyFileAddress);
                            if (myFile.exists()) // file downloaded before
                            {

                                Intent InstallIntent = new Intent(getApplicationContext(), InstallActivity.class);
                                InstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                Bundle bundle = new Bundle();
                                bundle.putString(getString(R.string.MyFile), MyFileAddress);
                                InstallIntent.putExtras(bundle);
                                startActivity(InstallIntent);

                            } else {

                                //oh yeah we do need an upgrade, let the user know send an alert message

                                //start downloading the file using the download manager
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                Uri Download_Uri = Uri.parse(appURI);
                                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

                                DownloadManager.Query q = new DownloadManager.Query();
                                q.setFilterByStatus(DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PENDING | DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_FAILED);

                                Cursor cursor = downloadManager.query(q);
                                if (cursor.moveToFirst()) {
                                    boolean flag = false;
                                    long did = 0;
                                    do {
                                        String targetURL = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                                        if (targetURL.equals(Download_Uri.toString())) {
                                            did = Integer.valueOf(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
                                            flag = true;
                                            break;
                                        }
                                    } while (cursor.moveToNext());
                                    if (flag) {
                                        downloadManager.remove(did);
                                    }
                                }


                                request.setDestinationUri(Uri.fromFile(myFile));
                                request.setMimeType("application/vnd.android.package-archive");
                                downloadReference = downloadManager.enqueue(request);
                                Log.d("Downloaded!", "3");
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job

        }
    }

}