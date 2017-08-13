package turbotec.newmpas;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;

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

//import android.database.Cursor;

/**
 * Created by ZAMANI on 5/21/2017.
 */

public class SendStatusAsyncTask extends AsyncTask {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    //    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    //    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    //    private final DatabaseHandler db;
//    private final SQLiteDatabase database;
    private static final int Timeout = 70000;
    private final Context MyContext;
    private final String ip = "192.168.1.13";
    private final int port = 80;
    private final SharedPreferenceHandler share;
    private final String OPERATION_NAME_DELIVERED = "Delivered";
    private String SOAP_ACTION_DELIVERED = "Delivered";
    private String WSDL_TARGET_NAMESPACE;
    private String SOAP_ADDRESS;



    public SendStatusAsyncTask(Context myContext) {
        MyContext = myContext;
        share = SharedPreferenceHandler.getInstance(MyContext);
//        db = DatabaseHandler.getInstance(MyContext);
//        database = db.getWritableDatabase();
    }

    protected void onPreExecute() {

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

        int num = Integer.parseInt(params[0].toString());
        Object Status = params[1];
        Object response = "";
        String IDs = "";
        for (int i = 2; i < num; i++) {
            IDs = IDs + (params[i] + ";");
        }


        if (isLocalReachable()) {
            SOAP_ACTION_DELIVERED = "http://192.168.1.13/Delivered";
            WSDL_TARGET_NAMESPACE = "http://192.168.1.13/";
            SOAP_ADDRESS = "http://192.168.1.13/Andr/WSLocal.asmx";
        } else {
            SOAP_ACTION_DELIVERED = "https://mpas.migtco.com:3000/Delivered";
            WSDL_TARGET_NAMESPACE = "https://mpas.migtco.com:3000/";
            SOAP_ADDRESS = "https://mpas.migtco.com:3000/Andr/WS.asmx";
        }


        try {

            // requests!

            String plaintext = "value1=" + IDs + ",value2=" + share.GetToken()
                    + ",value3=" + Status;


            plaintext = new String(Base64.encode(plaintext.getBytes(), Base64.DEFAULT));
            plaintext = plaintext.replaceAll("\n", "");

//            SoapObject request = new SoapObject("http://192.168.1.13/", "Delivered");
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

//            HttpTransportSE httpTransport = new HttpTransportSE("http://mpas.migtco.com/Andr/WS.asmx");
//            HttpTransportSE httpTransport = new HttpTransportSE("http://192.168.1.13/Andr/WS.asmx");
            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);

//            httpTransport.call("http://mpas.migtco.com/CheckUser", envelope);
//            httpTransport.call("http://192.168.1.13/Delivered", envelope);
            httpTransport.call(SOAP_ACTION_DELIVERED, envelope);
            response = envelope.getResponse();




                if (response.toString().contains(MyContext.getString(R.string.Seen))) {

                    ContentValues values = new ContentValues();
                    values.put("SendSeen", true);
                    values.put("SendDelivered", true);
                    String[] MIDs = IDs.split(";");
                    for (String MID : MIDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                        MyContext.getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{MID});
                    }
//                database.close();
                } else if (response.toString().contains(MyContext.getString(R.string.Delivered))) {

                    ContentValues values = new ContentValues();
                    values.put("SendDelivered", true);
                    String[] MIDs = IDs.split(";");
                    for (String MID : MIDs) {
//                        database.update("Messages", values, "MessageID  = ?", new String[]{MID});
                        MyContext.getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{MID});
                    }
//                database.close();
                }





        } catch (XmlPullParserException | IOException soapFault) {
            soapFault.printStackTrace();
        }

//        if (database != null && database.isOpen()) {
//            database.close();
//        }


        return response.toString();
    }


}
