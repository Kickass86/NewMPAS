package turbotec.newmpas;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SendEdit extends AsyncTask {

    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/tasks/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    private static final String TASK_ID = "_id";
    private static final String TASK_Title = "TaskTitle";
    private static final String Task_Description = "TaskDescription";
    private static final String TASK_DueDate = "DueDate";
    private static final String TASK_Creator = "TaskCreator";
    private static final String TASK_Status = "TaskStatus";
    private static final String TASK_Editable = "isEditable";
    private static final String TASK_ReplyAble = "ReplyAble";
    private static final String TASK_Deletable = "Deletable";
    private static final String SendDelivered = "SendDelivered";
    private static final String isSeen = "isSeen";
    private static final String TASK_isCreator = "isCreator";
    private static final String TASK_isResponsible = "isResponsible";
    private static final String TASK_NameResponsible = "NameResponsible";
    private final String OPERATION_NAME_DELIVERED = "EditTask";
    private final String ip = "192.168.1.13";
    private final int port = 80;
    String Subject;
    String Creator;
    String DueDate;
    int TStatus;
    String TDescription;
    String TNameResponsible;
    String TIDResponsible;
    String Report = "";
    boolean HasResponsible;
    private boolean flag = false;
    private String SOAP_ACTION_EditTask = "EditTask";
    private String WSDL_TARGET_NAMESPACE;
    private String SOAP_ADDRESS;
    private SharedPreferenceHandler share;
    private Context MyContext;
    private String TID;


    public SendEdit(Context myContext) {
        MyContext = myContext;
        share = SharedPreferenceHandler.getInstance(MyContext);
//        db = DatabaseHandler.getInstance(MyContext);
//        database = db.getWritableDatabase();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if (flag) {
            Toast.makeText(MyContext, "Network Connection Error!", Toast.LENGTH_SHORT).show();
        }

        if (TStatus == 0) {
            Intent intent = new Intent(MyContext.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            MyContext.startActivity(intent);
//            MyContext.overridePendingTransition (0, 0);
        }
    }

    private boolean isLocalReachable() {

        boolean exists = false;

        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 1000;   // 200 milliseconds
            sock.connect(sockaddr, timeoutMs);
            exists = true;

            sock.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return exists;
    }


    @Override
    protected String doInBackground(Object[] params) {


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


        try {

            // requests!

            TID = (String) params[0];
            Subject = (String) params[1];
            TDescription = (String) params[2];
            DueDate = (String) params[3];
            Report = (String) params[4];
            TStatus = Integer.valueOf((String) params[5]);


            String plaintext = "value1=" + share.GetDeviceID() + "!!*!!value2=" + share.GetToken()
                    + "!!*!!value3=" + TID + "!!*!!value4=" + Subject + "!!*!!value5=" + TDescription
                    + "!!*!!value6=" + DueDate + "!!*!!value7=" + Report + "!!*!!value8=" + TStatus + "!!*!!value9=";

            HasResponsible = Boolean.valueOf("1".equals(params[6]));

            if (HasResponsible) {
                TIDResponsible = (String) params[7];
                TNameResponsible = (String) params[8];

                if (TIDResponsible != null) {
                    plaintext = plaintext + TIDResponsible;
                }
            }

            plaintext = new String(Base64.encode(plaintext.getBytes("UTF-8"), Base64.DEFAULT));
            plaintext = plaintext.replaceAll("\n", "");


            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME_DELIVERED);
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

            if (response.toString().contains(MyContext.getString(R.string.Deleted))) {

                MyContext.getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{TID});
////                share.SaveChange(true);
//                Intent in = new Intent("Edit Done");
////                in.putExtra("Type", 1);
//                MyContext.sendBroadcast(in);

            }


            if (response.toString().contains(MyContext.getString(R.string.Seen)) || (response.toString().contains(MyContext.getString(R.string.Delivered)))
                    || (response.toString().contains(MyContext.getString(R.string.True)))) {


                values = new ContentValues();
                values.put(SendDelivered, true);
                if (HasResponsible) {
                    values.put(TASK_NameResponsible, TNameResponsible);
                }

//                values.put(TASK_ID, TID);
                values.put(TASK_Title, Subject);
                values.put(Task_Description, TDescription);
                values.put(TASK_DueDate, DueDate);
//                values.put(TASK_Creator,share.GetName());
                if (TStatus == 1) {
                    TStatus = 2;
                }
                values.put(TASK_Status, TStatus);
                values.put("Report", Report);
//                values.put(isSeen, true);

                MyContext.getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{TID});

            }
            if (response.toString().contains(MyContext.getString(R.string.Inserted))) {

                TNameResponsible = (String) params[8];

                values = new ContentValues();
                values.put(TASK_ID, TID);
                values.put(TASK_Title, Subject);
                values.put(Task_Description, TDescription);
                values.put(TASK_DueDate, DueDate);
                values.put(TASK_NameResponsible, TNameResponsible);
                values.put(TASK_Creator, share.GetName());
                values.put(TASK_Status, TStatus);
                values.put(TASK_isCreator, true);
                values.put(TASK_Deletable, true);
                values.put(TASK_Editable, true);
                values.put(SendDelivered, true);
                values.put(isSeen, true);


                MyContext.getContentResolver().insert(CONTENT_URI1, values);
//
////                share.SaveChange(true);
//                Intent in = new Intent("Edit Done");
////                in.putExtra("Type", 1);
//                MyContext.sendBroadcast(in);


            }


//                values.put(SendDelivered, false);


//            }


        } catch (XmlPullParserException | IOException soapFault) {
            soapFault.printStackTrace();
            flag = true;
        } finally {
//            MyContext.getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{TID});
//                share.SaveChange(true);
            Intent in = new Intent("Edit Done");
//                in.putExtra("Type", 1);
            MyContext.sendBroadcast(in);
        }


        return response.toString();
    }
}
