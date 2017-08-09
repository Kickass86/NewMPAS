package turbotec.newmpas;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SyncService extends IntentService {
    static final String PROVIDER_NAME = "turbotec.newmpas.MyProvider";
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent";
    static final String URL3 = "content://" + PROVIDER_NAME + "/tasks/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    //    static final Uri CONTENT_URI3 = Uri.parse(URL3);
    private static final int Timeout = 70000;
    private static final String TASK_ID = "_id";
    private static final String TASK_Title = "TaskTitle";
    private static final String Task_Description = "TaskDescription";
    private static final String TASK_DueDate = "DueDate";
    private static final String TASK_Creator = "TaskCreator";
    private static final String TASK_Status = "TaskStatus";
    private static final String isSeen = "isSeen";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
//    private static final String ACTION_FOO = "turbotec.newmpas.action.FOO";
//    private static final String ACTION_BAZ = "turbotec.newmpas.action.BAZ";
//    // TODO: Rename parameters
//    private static final String EXTRA_PARAM1 = "turbotec.newmpas.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "turbotec.newmpas.extra.PARAM2";
    private final String ip = "192.168.1.13";
    private final int port = 80;
    private SharedPreferenceHandler share;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private HttpTransportSE httpTransport;
    private SoapSerializationEnvelope envelopeDel;
    private Object response;
    private boolean isCritical = false;
    private boolean New = false;
    private boolean isDuplicate = false;
    private String FLag = "Invalid";
    private String IDs = "";
    private String TIDs = "";
    private String TID = "";
    private int MessageID;
    private String MessageTitle;
    private String MessageBody;
    private String InsertDate;
    private boolean Critical;
    private boolean Seen;
    private boolean SendDelivered;
    private boolean SendSeen;
    private String TaskID;
    private String TaskTitle;
    private String TaskDescription;
    private String TaskDueDate;
    private String TaskCreator;
    private String TaskStatus;
    private String OPERATION_NAME_CHECK;
    private String OPERATION_NAME_DELIVERED;
    private String SOAP_ACTION_CHECK;
    private String SOAP_ACTION_DELIVERED;
    private String WSDL_TARGET_NAMESPACE;
    private String SOAP_ADDRESS;
    private Context MyContext;

    public SyncService() {
        super("SyncService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
//    public static void startActionFoo(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, SyncService.class);
//        intent.setAction(ACTION_FOO);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, SyncService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyContext = getApplicationContext();
        share = SharedPreferenceHandler.getInstance(MyContext);
        if (intent != null) {
            boolean b = isLocalReachable();
            SOAP_ACTION_CHECK     = b?"http://192.168.1.13/CheckUser":"https://mpas.migtco.com:3000/CheckUser";
            SOAP_ACTION_DELIVERED = b?"http://192.168.1.13/Delivered":"https://mpas.migtco.com:3000/Delivered";
            WSDL_TARGET_NAMESPACE = b?"http://192.168.1.13/":"https://mpas.migtco.com:3000/";
            SOAP_ADDRESS          = b?"http://192.168.1.13/Andr/WSLocal.asmx":"https://mpas.migtco.com:3000/Andr/WS.asmx";
            OPERATION_NAME_CHECK = "CheckUser";
            OPERATION_NAME_DELIVERED = "Delivered";

            GetNotificationFromServer();
        }
    }


    private boolean isAppForeground(Context mContext) {

        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = mContext.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;

    }

    private boolean isLocalReachable() {

        boolean exists = false;

        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 5000;   // 200 milliseconds
            sock.connect(sockaddr, timeoutMs);
//            sock.connect(sockaddr);
            exists = true;

            sock.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void GetNotificationFromServer() {
        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");


        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);



        String username = share.GetUsername();
        String password = share.GetPassword();
        String DeviceID = share.GetDeviceID();
        String Token = share.GetToken();



        try {


            String plaintext = "value1=" + username + ",value2=" + password
                    + ",value3=" + DeviceID
                    + ",value4=";
            if (!Token.equals(MyContext.getString(R.string.defaultValue))) {
                plaintext += Token;
            }


            plaintext = new String(Base64.encode(plaintext.getBytes(), Base64.DEFAULT));

            plaintext = plaintext.replaceAll("\n", "");
            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_CHECK);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Value");
            pi.setValue(plaintext);
            pi.setType(String.class);
            request.addProperty(pi);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            httpTransport = new HttpTransportSE(SOAP_ADDRESS);


            httpTransport.call(SOAP_ACTION_CHECK, envelope);

            response = envelope.getResponse();


            SoapObject response1 = (SoapObject) response;
            SoapObject message = (SoapObject) response1.getProperty(0);
            SoapObject token = (SoapObject) response1.getProperty(1);
            SoapObject auth = (SoapObject) response1.getProperty(2);
            SoapObject tasks = (SoapObject) response1.getProperty(3);


            String Taskresp = "";
            String Authresp = "";
            String Tokenresp = "";
            Log.e("Response ", response1.toString());

            SoapObject Messagesresp = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_CHECK);

            if (auth.getPropertyCount() > 0)
                Authresp = auth.getProperty(0).toString();
            Log.e("Auth response", Authresp);


            if (tasks.getPropertyCount() > 0)
                Taskresp = tasks.getProperty(0).toString();
            Log.e("Task response", Taskresp);


            if (token.getPropertyCount() > 0)
                Tokenresp = token.getProperty(0).toString();
            Log.e("Token Response", Tokenresp);

            FLag = (Authresp + Tokenresp);


            if (Authresp.contains("Invalid") | Authresp.contains("Error")) {
                FLag = Authresp;
            } else if (Authresp.contains("Wait")) {

                FLag = "Wait";
            } else { //(Authresp.contains("No Message"))
                if (share.GetStatus().equals("OK")) {
                }
                FLag = "OK";
            }
            if (!Tokenresp.isEmpty()) {
                share.SaveToken(Tokenresp);
            }

            InsertMessages(message);

            InsertTasks(tasks);

            isDuplicate = false;

            HandleUnsendDelivery();

        } catch (Exception e) {
            e.printStackTrace();
        }





        if ((FLag.equals(MyContext.getString(R.string.OK))) | (FLag.equals(MyContext.getString(R.string.Wait))))
            share.SaveStatus(FLag);

        if ((isCritical) & (!isAppForeground(MyContext))) {
            Intent i = new Intent(MyContext, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            mNotificationManager.cancelAll();
            if ((FLag.equals(MyContext.getString(R.string.OK))) | (FLag.equals(MyContext.getString(R.string.Wait))))
                share.SaveStatus(FLag);


        }


    }

    private void HandleUnsendDelivery() {

        Cursor cursor = getContentResolver().query(CONTENT_URI2, null, "SendDelivered = ?", new String[]{"0"}, null);
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        IDs = IDs + cursor.getInt(0) + ";";

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

        String Status = "0";
        String plaintxt = "value1=" + IDs + ",value2=" + share.GetToken()
                + ",value3=" + Status;


        plaintxt = new String(Base64.encode(plaintxt.getBytes(), Base64.DEFAULT));
        plaintxt = plaintxt.replaceAll("\n", "");

        SoapObject requestDel = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_DELIVERED);
        PropertyInfo Pinf = new PropertyInfo();
        Pinf.setName("Value");
        Pinf.setValue(plaintxt);
        Pinf.setType(String.class);
        requestDel.addProperty(Pinf);


        envelopeDel = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelopeDel.dotNet = true;

        envelopeDel.setOutputSoapObject(requestDel);


        try {
            httpTransport.call(SOAP_ACTION_DELIVERED, envelopeDel);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            response = envelopeDel.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }


        if (response.toString().contains(MyContext.getString(R.string.Delivered))) {

            ContentValues values = new ContentValues();
            values.put("SendDelivered", true);
            String[] MIDs = IDs.split(";");
            for (String MID : MIDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                getContentResolver().update(Uri.parse(URL1 + MID), values, "_id  = ?", new String[]{MID});
            }

        } else if (response.toString().contains(MyContext.getString(R.string.Seen))) {

            ContentValues values = new ContentValues();
            values.put("SendSeen", true);
            values.put("SendDelivered", true);
            String[] MIDs = IDs.split(";");
            for (String MID : MIDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                getContentResolver().update(Uri.parse(URL1 + MID), values, "_id  = ?", new String[]{MID});
            }
        }

    }

    private void InsertTasks(SoapObject tasks) {


        int index = 0;
        while (index < tasks.getPropertyCount()) {

            FLag = "OK";

            SoapObject Task = (SoapObject) tasks.getProperty(index);


            TaskID = Task.getProperty(0).toString();
            TaskTitle = Task.getProperty(1).toString();
            TaskDescription = Task.getProperty(2).toString();
            TaskDueDate = Task.getProperty(3).toString();
            TaskCreator = Task.getProperty(4).toString();
            TaskStatus = Task.getProperty(5).toString();


            Cursor c = getContentResolver().query(Uri.parse(URL3 + TaskID), new String[]{"*"}, "_id  = ?", new String[]{String.valueOf(TaskID)}, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    index++;
                    continue;
                } else {
                    TID = TID + Task.getProperty(0).toString() + ";";
                }
            }
            c.close();

            long[] pattern = {1000, 1000, 1000, 1000, 1000};
            Vibrator vibrator = (Vibrator) MyContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, -1);
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(MyContext, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!isAppForeground(MyContext)) {

                Log.i("Notify", "is running");
//                Intent nid = new Intent(MyContext, .class);
                Bundle bundle = new Bundle();
//                bundle.putInt(MyContext.getString(R.string.ID), TaskID);
                bundle.putString(MyContext.getString(R.string.Title), TaskTitle);
                bundle.putString(MyContext.getString(R.string.Body), TaskDescription);
//                bundle.putBoolean(MyContext.getString(R.string.Critical), TaskDueDate);
//                bundle.putBoolean(MyContext.getString(R.string.SendSeen), TaskCreator);
//                bundle.putBoolean(MyContext.getString(R.string.Seen), TaskStatus);
//                nid.putExtras(bundle);
//                nid.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                PendingIntent ci = PendingIntent.getActivity(MyContext, MessageID, nid, 0);

                mBuilder =
                        new android.support.v4.app.NotificationCompat.Builder(MyContext)
                                .setSmallIcon(R.mipmap.ic_assignment_black_24dp)
                                .setContentTitle(MessageTitle)
//                                .setContentIntent(ci)
                                .setAutoCancel(true)
                                .setContentText(MessageBody);
                if (!isDuplicate) {
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                }


                mNotificationManager.notify(MessageID, mBuilder.build());
                isDuplicate = true;
            }


            ContentValues contentValues = new ContentValues();
            contentValues.put(TASK_ID, Task.getProperty(0).toString());
            contentValues.put(TASK_Title, Task.getProperty(1).toString().trim());
            contentValues.put(Task_Description, Task.getProperty(2).toString().trim());
            contentValues.put(TASK_DueDate, Task.getProperty(3).toString());
            contentValues.put(TASK_Creator, Task.getProperty(4).toString());
            contentValues.put(TASK_Status, Task.getProperty(5).toString());
            contentValues.put(isSeen, false);

            getContentResolver().insert(Uri.parse(URL3 + TaskID), contentValues);

            index++;
        }


    }

    private void InsertMessages(SoapObject message) {

        int index = 0;

        while (index < message.getPropertyCount()) {

            FLag = "OK";

            SoapObject Message = (SoapObject) message.getProperty(index);


            MessageID = Integer.valueOf(Message.getProperty(0).toString());
            MessageTitle = Message.getProperty(1).toString().trim();
            MessageBody = Message.getProperty(2).toString().trim();
            InsertDate = Message.getProperty(3).toString();
            Critical = Boolean.valueOf(Message.getProperty(4).toString());
            Seen = false;
            SendDelivered = false;
            SendSeen = false;

            Cursor c = getContentResolver().query(Uri.parse(URL1 + MessageID), new String[]{"*"}, "_id  = ?", new String[]{String.valueOf(MessageID)}, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    index++;
                    continue;
                }
            }
            c.close();

            IDs = IDs + Message.getProperty(0).toString() + ";";


            isCritical = Boolean.valueOf(Message.getProperty(4).toString());


            if (isCritical) {


                long[] pattern = {1000, 1000, 1000, 1000, 1000};
                Vibrator vibrator = (Vibrator) MyContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(pattern, -1);
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(MyContext, notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                if (!isAppForeground(MyContext)) {

                    Log.i("Notify", "is running");
                    Intent nid = new Intent(MyContext, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(MyContext.getString(R.string.Title), MessageTitle);
                    bundle.putString(MyContext.getString(R.string.Body), MessageBody);
                    bundle.putBoolean(MyContext.getString(R.string.Critical), Critical);
                    bundle.putBoolean(MyContext.getString(R.string.SendSeen), SendSeen);
                    bundle.putInt(MyContext.getString(R.string.ID), MessageID);
                    bundle.putBoolean(MyContext.getString(R.string.Seen), Seen);
                    nid.putExtras(bundle);
                    nid.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent ci = PendingIntent.getActivity(MyContext, MessageID, nid, 0);


                    mBuilder =
                            new android.support.v4.app.NotificationCompat.Builder(MyContext)
                                    .setSmallIcon(R.mipmap.ic_message_black_24dp)
                                    .setContentTitle(MessageTitle)
                                    .setContentIntent(ci)
                                    .setAutoCancel(true)
                                    .setContentText(MessageBody);
                    if (!isDuplicate) {
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                    }


                    mNotificationManager.notify(MessageID, mBuilder.build());
                    isDuplicate = true;
                }
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", Integer.valueOf(Message.getProperty(0).toString()));
            contentValues.put("MessageTitle", Message.getProperty(1).toString().trim());
            contentValues.put("MessageBody", Message.getProperty(2).toString().trim());
            contentValues.put("InsertDate", Message.getProperty(3).toString());
            contentValues.put("Critical", Boolean.valueOf(Message.getProperty(4).toString()));
            contentValues.put("Seen", false);
            contentValues.put("SendDelivered", false);
            contentValues.put("SendSeen", false);
            getContentResolver().insert(Uri.parse(URL1 + MessageID), contentValues);

            index++;
        }


    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
