package turbotec.newmpas;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

/**
 * Created by ZAMANI on 9/13/2017.
 */

public class GetUserList extends AsyncTask {


    static final String PROVIDER_NAME = "turbotec.newmpas.MyProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/users/";
    static final Uri CONTENT_URI = Uri.parse(URL);
    private final Context MyContext;
    private final String ip = "192.168.1.13";
    private final int port = 80;
    String[] responseMessage;
    String UserListURL;
    String s = "";
    URL url;
    SharedPreferenceHandler share;

    public GetUserList(Context context) {
        MyContext = context;
        this.share = SharedPreferenceHandler.getInstance(MyContext);
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
    protected Object doInBackground(Object[] params) {

        if (isLocalReachable()) {
            UserListURL = "http://192.168.1.13/Andr/GetUserList.ashx?Value=";
        } else {
            UserListURL = "https://mpas.migtco.com:3000/Andr/GetUserList.ashx?Value=";
        }


        try {


            String value = "Val1=" + share.GetDeviceID() + ",Val2=" + share.GetToken();
            UserListURL = UserListURL + new String(Base64.encode(value.getBytes("UTF-8"), Base64.DEFAULT));
            UserListURL = UserListURL.replaceAll("\n", "");


            url = new URL(UserListURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();


            int code = c.getResponseCode();

            if (code == 200) {


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
//                s = s.replace(";", " ");
//                responseMessage = s.split("-@-");
            } else {
                responseMessage = new String[]{""};
            }
        } catch (Exception e) {
            Log.w("HTTP:", e);
            responseMessage = new String[]{""};
        }

        int num;

        if (!s.isEmpty()) {
            responseMessage = s.split("-@-");
            ContentValues[] values = new ContentValues[responseMessage.length];
//            ContentValues value = new ContentValues();
//            for(int i = 0; i < responseMessage.length; i++){
//                values[i] = new ContentValues();
//
//            }

            int index = 0;
            for (String w : responseMessage) {
                String[] all = w.split(";");
//                value.put("_id", all[0]);
//                value.put("Name", all[1]);
                values[index] = new ContentValues();
                values[index].put("_id", all[0]);
                values[index].put("Name", all[1]);
                index++;

            }

            num = MyContext.getContentResolver().bulkInsert(CONTENT_URI, values);
        } else {
            num = 0;
        }

        return num;
    }
}
