package turbotec.newmpas;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

public class SendEdit extends AsyncTask {

    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/tasks/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);

    private final String OPERATION_NAME_DELIVERED = "EditTask";
    private final String ip = "192.168.1.13";
    private final int port = 80;
    String Subject;
    String Creator;
    String DueDate;
    int TStatus;
    String TDescription;
    String Report = "";
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

        if (TStatus == 0) {
            MyContext.startActivity(new Intent(MyContext.getApplicationContext(), MainActivity.class));
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
            Subject = (String) params[0];
            TDescription = (String) params[0];
            DueDate = (String) params[0];
            Report = (String) params[0];
            TStatus = (int) params[0];

            String plaintext = "value1=" + share.GetDeviceID() + ",value2=" + share.GetToken()
                    + ",value3=" + TID + ",value4=" + Subject + ",value5=" + TDescription
                    + ",value6=" + DueDate + ",value7=" + Report + ",value8=" + TStatus;


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
            }


            if (response.toString().contains(MyContext.getString(R.string.Seen)) || (response.toString().contains(MyContext.getString(R.string.Delivered)))
                    || (response.toString().contains(MyContext.getString(R.string.True)))) {


                values.put("SendDelivered", true);

                MyContext.getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{TID});
            } else {


                values.put("SendDelivered", false);

                MyContext.getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{TID});
            }


        } catch (XmlPullParserException | IOException soapFault) {
            soapFault.printStackTrace();
        }


        return response.toString();
    }
}
