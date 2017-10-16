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
import java.util.Random;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class SyncService extends IntentService {


    static final String PROVIDER_NAME = "turbotec.newmpas.MyProvider";
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent";
    static final String URL3 = "content://" + PROVIDER_NAME + "/tasks/";
    static final String URL4 = "content://" + PROVIDER_NAME + "/tasks/unsent/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    static final Uri CONTENT_URI3 = Uri.parse(URL3);
    static final Uri CONTENT_URI4 = Uri.parse(URL4);
    //    static final Uri CONTENT_URI3 = Uri.parse(URL3);
//    private static final int Timeout = 70000;
    private static final String MESSAGE_ID = "_id";
    private static final String MESSAGE_Title = "MessageTitle";
    private static final String MESSAGE_BODY = "MessageBody";
    private static final String INSERT_DATE = "InsertDate";
    private static final String Critical = "Critical";
    private static final String Seen1 = "Seen";
    private static final String SSeen = "SendSeen";
    private static final String WillDeleted = "WillDeleted";


    private static final String TASK_ID = "_id";
    private static final String TASK_Title = "TaskTitle";
    private static final String Task_Description = "TaskDescription";
    private static final String TASK_DueDate = "DueDate";
    private static final String TASK_Creator = "TaskCreator";
    private static final String TASK_Status = "TaskStatus";
    private static final String TASK_Editable = "isEditable";
    private static final String TASK_ReplyAble = "ReplyAble";
    private static final String TASK_Deletable = "Deletable";
    private static final String SendInsert = "SendInsert";
    private static final String TASK_isCreator = "isCreator";
    private static final String TASK_isResponsible = "isResponsible";
    private static final String TASK_NameResponsible = "NameResponsible";
    private static final String isSeen = "isSeen";
    private static final String Report = "Report";

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
//    private static final String ACTION_FOO = "turbotec.newmpas.action.FOO";
//    private static final String ACTION_BAZ = "turbotec.newmpas.action.BAZ";

    //    private static final String EXTRA_PARAM1 = "turbotec.newmpas.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "turbotec.newmpas.extra.PARAM2";
    private final String ip = "192.168.1.13";
    private final int port = 80;
    private String SendDelivered = "SendDelivered";
    private SharedPreferenceHandler share;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private HttpTransportSE httpTransport;
    private SoapSerializationEnvelope envelopeDel;
    private Object response;
    private boolean isCritical = false;
    private boolean Deleted = false;
    private String Change;
    private boolean isDuplicate = false;
    private boolean FlagChange = false;
    private String IDs = "";
    private String TIDs = "";
    private String TID = "";
    private int MessageID;
    private String MessageTitle;
    private String MessageBody;
    private String Link;
    private String InsertDate;
    //    private boolean Critical;
    private boolean Seen;

    private boolean SendSeen;
    private String TaskID;
    private String[] DeletedTIDs;
    private int ind = 0;
    private String TaskTitle;
    private String TaskDescription;
    private String TaskDueDate;
    private String TaskCreator;
    private int TaskStatus;
    private String TReport = "";
    private boolean TEditable;
    private boolean TReplyAble;
    private boolean TDeletable;
    private boolean TisCreator;
    private boolean TisResponsible;
    private String TNameResponsible;
    private String OPERATION_NAME_CHECK;
    private String OPERATION_NAME_DELIVERED;
    private String OPERATION_NAME_EDITTASK;
    private String SOAP_ACTION_CHECK;
    private String SOAP_ACTION_EditTask;
    private String SOAP_ACTION_DELIVERED;
    private String WSDL_TARGET_NAMESPACE;
    private String SOAP_ADDRESS;
    private Context MyContext;

    private int TAB;

    public SyncService() {
        super("SyncService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
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
            SOAP_ACTION_EditTask = b ? "http://192.168.1.13/EditTask" : "https://mpas.migtco.com:3000/EditTask";
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
//        throw new UnsupportedOperationException("Not yet implemented");


        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);



        String username = share.GetUsername();
        String password = share.GetPassword();
        String DeviceID = share.GetDeviceID();
        String Token = share.GetToken();

        Change = share.GetStatus();
        FlagChange = false;



        try {


            String plaintext = "value1=" + username + ",value2=" + password
                    + ",value3=" + DeviceID
                    + ",value4=";
            if (!Token.equals(MyContext.getString(R.string.defaultValue))) {
                plaintext += Token;
            }


            plaintext = new String(Base64.encode(plaintext.getBytes("UTF-8"), Base64.DEFAULT));

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
            String Nameresp = "";
            Log.e("Response ", response1.toString());

            SoapObject Messagesresp = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_CHECK);

            if (auth.getPropertyCount() > 0)
                Authresp = auth.getProperty(0).toString();
            Log.e("Auth response", Authresp);


            if (tasks.getPropertyCount() > 0)
                Taskresp = tasks.getProperty(0).toString();
            Log.e("Task response", Taskresp);


            if (token.getPropertyCount() > 0) {
                Tokenresp = token.getProperty(0).toString();
                Nameresp = token.getProperty(1).toString();
            }
            Log.e("Token Response", Tokenresp);

//            FLag = (Authresp + Tokenresp);


            if (Authresp.contains("Invalid") | Authresp.contains("Error")) {
//                FLag = Authresp;
            } else if (Authresp.contains("Wait")) {
                Change = "Wait";
//                FLag = "Wait";
            } else { //(Authresp.contains("No Message"))

                Change = "OK";
//                FLag = "OK";
            }
            if (!Tokenresp.isEmpty()) {
                share.SaveToken(Tokenresp);
                share.SaveName(Nameresp);
            }

            InsertMessages(message);

            InsertTasks(tasks);

            isDuplicate = false;

            HandleUnsendInsert();

            HandleUnsendDelivery();


        } catch (Exception e) {
            e.printStackTrace();
        }

        String now = share.GetStatus();
        if (!Change.equals(now)) {
            FlagChange = true;
        }


        share.SaveChange(FlagChange);
        share.SaveStatus(Change);
        Intent in = new Intent("Alarm fire");
        in.putExtra("Type", TAB);

        MyContext.sendBroadcast(in);

//        if (FlagChange)
//        {
//            share.SaveStatus(Change);
//            if(isAppForeground(MyContext))
//            {
//                Intent i = new Intent(MyContext, MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(i);
//                mNotificationManager.cancelAll();
//            }
//            if ((isCritical) & (!isAppForeground(MyContext))) {
//                Intent i = new Intent(MyContext, MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(i);
//                mNotificationManager.cancelAll();
//            }
//        }else{
//            Intent i = new Intent(MyContext, LoginActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(i);
//        }



    }

    private void HandleUnsendInsert() {
        int num = 0;

        Cursor cursor = getContentResolver().query(CONTENT_URI4, null, "SendInsert = ?", new String[]{"1"}, null);
        TIDs = "";
        try {
            if (cursor != null) {


                if (cursor.moveToFirst()) {
                    do {


                        Object response = "";

                        ContentValues values = new ContentValues();


                        if (isLocalReachable()) {
                            SOAP_ACTION_EditTask = "http://192.168.1.13/EditTask";
                            WSDL_TARGET_NAMESPACE = "http://192.168.1.13/";
                            SOAP_ADDRESS = "http://192.168.1.13/Andr/WSLocal.asmx";
                        } else {
                            SOAP_ACTION_EditTask = "https://mpas.migtco.com:3000/EditTask";
                            WSDL_TARGET_NAMESPACE = "https://mpas.migtco.com:3000/";
                            SOAP_ADDRESS = "https://mpas.migtco.com:3000/Andr/WS.asmx";
                        }
                        OPERATION_NAME_EDITTASK = "EditTask";


                        try {

                            // requests!

                            TID = cursor.getString(0);
                            TaskTitle = cursor.getString(1);
                            TaskDescription = cursor.getString(2);
                            TaskDueDate = cursor.getString(3);
                            TaskStatus = Integer.valueOf(cursor.getString(5));
                            TNameResponsible = cursor.getString(15);

                            String plaintext = "value1=" + share.GetDeviceID() + "!!*!!value2=" + share.GetToken()
                                    + "!!*!!value3=" + TID + "!!*!!value4=" + TaskTitle + "!!*!!value5=" + TaskDescription
                                    + "!!*!!value6=" + TaskDueDate + "!!*!!value7=" + "" + "!!*!!value8=" + TaskStatus + "!!*!!value9=";
                            if (TNameResponsible != null)
                                plaintext = plaintext + TNameResponsible;


                            plaintext = new String(Base64.encode(plaintext.getBytes("UTF-8"), Base64.DEFAULT));
                            plaintext = plaintext.replaceAll("\n", "");


                            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_EDITTASK);
                            PropertyInfo pi = new PropertyInfo();
                            pi.setName("Value");
                            pi.setValue(plaintext);
                            pi.setType(String.class);
                            request.addProperty(pi);


                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                    SoapEnvelope.VER11);
                            envelope.dotNet = true;

                            envelope.setOutputSoapObject(request);


                            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);


                            httpTransport.call(SOAP_ACTION_EditTask, envelope);
                            response = envelope.getResponse();


                            if (response.toString().contains(MyContext.getString(R.string.Inserted))) {


                                values.put(SendDelivered, true);
                                values.put(SendInsert, true);


                                MyContext.getContentResolver().insert(CONTENT_URI1, values);

                            } else {


                                values.put(SendDelivered, false);

                                MyContext.getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{TID});
                            }


                        } catch (XmlPullParserException | IOException soapFault) {
                            soapFault.printStackTrace();
                        }
                    } while (cursor.moveToNext());

                }

                cursor.close();
            }

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void HandleUnsendDelivery() {
        int num = 0;

        Cursor cursor = getContentResolver().query(CONTENT_URI2, null, "SendDelivered = ?", new String[]{"0"}, null);
        IDs = "";
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        IDs = IDs + cursor.getInt(0) + ";";
                        num = num + 1;
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        cursor = getContentResolver().query(CONTENT_URI4, null, "SendDelivered = ?", new String[]{"0"}, null);
        TIDs = "";
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        TIDs = TIDs + cursor.getString(0) + ";";
                        num = num + 1;
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

//        for(int i = 0; i < DeletedTIDs.length; i++){
//            TIDs = TIDs + DeletedTIDs[i] + ";";
//        }
//        num = num + DeletedTIDs.length;


        if (num > 0) {
            String Status = "0";
            String plaintxt = "value1=" + IDs + ",value2=" + share.GetToken()
                    + ",value3=" + Status + ",value4=" + TIDs;


            try {
                plaintxt = new String(Base64.encode(plaintxt.getBytes("UTF-8"), Base64.DEFAULT));
                plaintxt = plaintxt.replaceAll("\n", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                values.put(SendDelivered, true);
                String[] MIDs = IDs.split(";");
                for (String MID : MIDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                    getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{MID});
                }
                String[] TDs = TIDs.split(";");

                for (String TID : TDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                    getContentResolver().update(CONTENT_URI3, values, "_id  = ?", new String[]{TID});

                }
                for (String TID : DeletedTIDs) {
                    getContentResolver().delete(CONTENT_URI3, "_id  = ?", new String[]{String.valueOf(TID)});
                }

            } else if (response.toString().contains(MyContext.getString(R.string.Seen))) {

                ContentValues values = new ContentValues();
//            values.put("SendSeen", true);
                values.put(SendDelivered, true);
                String[] TDs = TIDs.split(";");
                for (String TID : TDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                    getContentResolver().update(CONTENT_URI3, values, "_id  = ?", new String[]{TID});
                }

                values.put("SendSeen", true);

                String[] MIDs = IDs.split(";");
                for (String MID : MIDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                    getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{MID});
                }

            }
        }

    }

    private void InsertTasks(SoapObject tasks) {


        int index = 0;
        DeletedTIDs = new String[tasks.getPropertyCount()];
        while (index < tasks.getPropertyCount()) {

//            FLag = "OK";
            Change = "OK";
            FlagChange = true;
            MainActivity.setTab = TabController.Tabs.Task;



            SoapObject Task = (SoapObject) tasks.getProperty(index);


            TaskID = Task.getProperty(0).toString();
            TaskTitle = Task.getProperty(1).toString();
            TaskDescription = Task.getProperty(2).toString();
            TaskDueDate = Task.getProperty(3).toString();
            TaskCreator = Task.getProperty(4).toString();
            TaskStatus = Integer.valueOf(Task.getProperty(5).toString().trim());
            TReplyAble = Boolean.valueOf("1".equals(Task.getProperty(6).toString()));
            TEditable = Boolean.valueOf("1".equals(Task.getProperty(7).toString()));
            TReport = Task.getProperty(8).toString();
            if (!TReport.contains("&#x0D;")) {
                TReport = "";
            } else {
                TReport = TReport.replaceAll("&#x0D;", "");
                TReport = TReport.replaceAll("-@-", "\n");
            }
            TDeletable = Boolean.valueOf("1".equals(Task.getProperty(9).toString()));
            TisCreator = Boolean.valueOf("1".equals(Task.getProperty(10).toString()));
            TisResponsible = Boolean.valueOf("1".equals(Task.getProperty(11).toString()));
            TNameResponsible = Task.getProperty(12).toString();
            if (TNameResponsible.contains("anyType{}")) {
                TNameResponsible = "";
            }

            Cursor c = getContentResolver().query(CONTENT_URI3, new String[]{"*"}, "_id  = ?", new String[]{String.valueOf(TaskID)}, null);
            if (c != null) {
                if ((c.getCount() > 0) & (c.moveToFirst())) {
                    if (TaskStatus == 0) {

//                        getContentResolver().delete(CONTENT_URI3, "_id  = ?", new String[]{String.valueOf(TaskID)});
                        ContentValues v = new ContentValues();
                        v.put(SendDelivered, false);

                        getContentResolver().update(CONTENT_URI3, v, "_id  = ?", new String[]{String.valueOf(TaskID)});
                        DeletedTIDs[ind] = TaskID;
                        ind++;
                        index++;
                        if (index >= tasks.getPropertyCount()) {
                            c.close();
                        }
                        continue;

                    } else {
                        ContentValues values = new ContentValues();
                        values.put(TASK_ID, TaskID);
                        values.put(TASK_Title, TaskTitle);
                        values.put(Task_Description, TaskDescription);
                        values.put(TASK_DueDate, TaskDueDate);
                        values.put(TASK_Creator, TaskCreator);
                        values.put(TASK_Status, TaskStatus);
                        values.put(TASK_Editable, TEditable);
                        values.put(TASK_ReplyAble, TReplyAble);
                        values.put(TASK_Deletable, TDeletable);
                        values.put(TASK_isCreator, TisCreator);
                        values.put(TASK_isResponsible, TisResponsible);
                        values.put(TASK_NameResponsible, TNameResponsible);
                        values.put(SendInsert, false);
                        values.put(SendDelivered, true);
                        values.put(Report, TReport);
                        values.put(isSeen, false);


                        getContentResolver().update(CONTENT_URI3, values, "_id  = ?", new String[]{String.valueOf(TaskID)});

                        index++;
                        if (index >= tasks.getPropertyCount()) {
                            c.close();
                        }
                        continue;
                    }
                } else {
                    TID = TID + Task.getProperty(0).toString() + ";";
                }
            }
            c.close();

//            long[] pattern = {1000, 1000, 1000, 1000, 1000};
//            Vibrator vibrator = (Vibrator) MyContext.getSystemService(Context.VIBRATOR_SERVICE);
//            vibrator.vibrate(pattern, -1);
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(MyContext, notification);
                if (!isDuplicate) {
                    r.play();
                }
                isDuplicate = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!isAppForeground(MyContext)) {

                Log.i("Notify", "is running");
                Intent nid = new Intent(MyContext, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MyContext.getString(R.string.TID), TaskID);
                bundle.putString(MyContext.getString(R.string.Subject), TaskTitle);
                bundle.putString(MyContext.getString(R.string.TDescription), TaskDescription);
                bundle.putString(MyContext.getString(R.string.DueDate), TaskDueDate);
                bundle.putString(MyContext.getString(R.string.TCreator), TaskCreator);
                bundle.putInt(MyContext.getString(R.string.TStatus), TaskStatus);
                bundle.putBoolean(MyContext.getString(R.string.TEditable), TEditable);
                bundle.putBoolean(MyContext.getString(R.string.TReplyAble), TReplyAble);
                bundle.putBoolean(MyContext.getString(R.string.TDeletable), TDeletable);
                bundle.putString(MyContext.getString(R.string.TReport), TReport);
                bundle.putBoolean(MyContext.getString(R.string.TisCreator), TisCreator);
                bundle.putBoolean(MyContext.getString(R.string.TisResponsible), TisResponsible);
                bundle.putString(MyContext.getString(R.string.TNameResponsible), TNameResponsible);
                bundle.putInt("Type", 1);
                nid.putExtras(bundle);
                nid.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Random i = new Random();
                PendingIntent ci = PendingIntent.getActivity(MyContext, i.nextInt(), nid, 0);

                mBuilder =
                        new android.support.v4.app.NotificationCompat.Builder(MyContext)
                                .setSmallIcon(R.mipmap.ic_assignment_black_24dp)
//                                .setLargeIcon(R.mipmap.ic_assignment_black_24dp_new)
                                .setContentTitle(TaskTitle)
                                .setContentIntent(ci)
                                .setAutoCancel(true)
                                .setColor(0x00FFFF)
                                .setContentText(TaskDescription);
                if (!isDuplicate) {
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                }


                mNotificationManager.notify(MessageID, mBuilder.build());
                isDuplicate = true;
            } else {
                TAB = 1;
            }


            ContentValues contentValues = new ContentValues();
            contentValues.put(TASK_ID, TaskID);
            contentValues.put(TASK_Title, TaskTitle);
            contentValues.put(Task_Description, TaskDescription);
            contentValues.put(TASK_DueDate, TaskDueDate);
            contentValues.put(TASK_Creator, TaskCreator);
            contentValues.put(TASK_Status, TaskStatus);
            contentValues.put(Report, TReport);
            contentValues.put(TASK_Editable, TEditable);
            contentValues.put(TASK_ReplyAble, TReplyAble);
            contentValues.put(TASK_Deletable, TDeletable);
            contentValues.put(TASK_isCreator, TisCreator);
            contentValues.put(TASK_isResponsible, TisResponsible);
            contentValues.put(TASK_NameResponsible, TNameResponsible);
            contentValues.put(SendInsert, false);
            contentValues.put(isSeen, false);
            contentValues.put(SendDelivered, false);

            getContentResolver().insert(CONTENT_URI3, contentValues);

            index++;
        }


    }

    private void InsertMessages(SoapObject message) {

        int index = 0;

        while (index < message.getPropertyCount()) {

//            FLag = "OK";
            Change = "OK";
            FlagChange = true;
            MainActivity.setTab = TabController.Tabs.Message;


            SoapObject Message = (SoapObject) message.getProperty(index);


            MessageID = Integer.valueOf(Message.getProperty(0).toString());
            MessageTitle = Message.getProperty(1).toString().trim();
            MessageBody = Message.getProperty(2).toString().trim();
            InsertDate = Message.getProperty(3).toString();
            isCritical = Boolean.valueOf(Message.getProperty(4).toString());
            Deleted = Boolean.valueOf(Message.getProperty(5).toString());
            Link = Message.getProperty(6).toString();
            Seen = false;
//            SendDelivered = false;
            SendSeen = false;

            Cursor c = getContentResolver().query(CONTENT_URI1, new String[]{"*"}, "_id  = ?", new String[]{String.valueOf(MessageID)}, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    index++;
                    if (index >= message.getPropertyCount()) {
                        c.close();
                    }
                    if (Deleted) {
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(SendDelivered, false);
                        contentValues.put(WillDeleted, Deleted);
                        getContentResolver().update(CONTENT_URI1, contentValues, "_id  = ?", new String[]{String.valueOf(MessageID)});
                    }
                    continue;
                }
            }
            c.close();

//            IDs = IDs + Message.getProperty(0).toString() + ";";


            isCritical = Boolean.valueOf(Message.getProperty(4).toString());


            if (isCritical) {


                long[] pattern = {1000, 1000, 1000, 1000, 1000};
                Vibrator vibrator = (Vibrator) MyContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(pattern, -1);
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(MyContext, notification);
                    if (!isDuplicate) {
                        r.play();
                    }
                    isDuplicate = true;
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
                    bundle.putString(getString(R.string.Link), Link);
                    bundle.putBoolean(MyContext.getString(R.string.Critical), isCritical);
                    bundle.putBoolean(MyContext.getString(R.string.SendSeen), SendSeen);
                    bundle.putInt(MyContext.getString(R.string.ID), MessageID);
                    bundle.putBoolean(MyContext.getString(R.string.Seen), Seen);
                    bundle.putInt("Type", 0);
                    nid.putExtras(bundle);
                    nid.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent ci = PendingIntent.getActivity(MyContext, MessageID, nid, 0);


                    mBuilder =
                            new android.support.v4.app.NotificationCompat.Builder(MyContext)
                                    .setSmallIcon(R.mipmap.ic_message_black_24dp)
                                    .setContentTitle(MessageTitle)
                                    .setContentIntent(ci)
                                    .setColor(0x00FFFF)
                                    .setAutoCancel(true)
                                    .setContentText(MessageBody);
                    if (!isDuplicate) {
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                    }


                    mNotificationManager.notify(MessageID, mBuilder.build());
                    isDuplicate = true;
                } else {
                    TAB = 0;
                }
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(MESSAGE_ID, Integer.valueOf(Message.getProperty(0).toString()));
            contentValues.put(MESSAGE_Title, Message.getProperty(1).toString().trim());
            contentValues.put(MESSAGE_BODY, Message.getProperty(2).toString().trim());
            contentValues.put(INSERT_DATE, Message.getProperty(3).toString());
            contentValues.put(Critical, Boolean.valueOf(Message.getProperty(4).toString()));
            contentValues.put(WillDeleted, Boolean.valueOf(Message.getProperty(5).toString()));
            contentValues.put("Link", Message.getProperty(6).toString());
            contentValues.put(Seen1, false);
            contentValues.put(SendDelivered, false);
            contentValues.put(SSeen, false);
            getContentResolver().insert(CONTENT_URI1, contentValues);

            index++;
        }


    }


}
