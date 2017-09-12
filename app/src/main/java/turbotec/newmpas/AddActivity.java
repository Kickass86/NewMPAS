package turbotec.newmpas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    private final String ip = "192.168.1.13";
    private final int port = 80;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    private String UserListURL;
    private URL url;
    private EditText DateAdd;
    private EditText SubjectAdd;
    private EditText DescAdd;
    private EditText RespAdd;
    private Button Done;

    private SharedPreferenceHandler share;


    private void updateLabel() {

        EditText edittext = (EditText) findViewById(R.id.DueDateAdd);
        String myFormat = "MMM dd yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_task);

        DateAdd = (EditText) findViewById(R.id.DueDateAdd);
        SubjectAdd = (EditText) findViewById(R.id.SubjectAdd);
        DescAdd = (EditText) findViewById(R.id.TDescriptionAdd);
        RespAdd = (EditText) findViewById(R.id.TResponsibleAdd);


        DateAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        if (isLocalReachable()) {
            UserListURL = "http://192.168.1.13/Andr/Download.ashx";
        } else {
            UserListURL = "https://mpas.migtco.com:3000/Andr/Download.ashx";
        }


        String[] responseMessage;

        try {


            String value = "Val1=" + share.GetDeviceID() + ",Val2=" + share.GetToken();
            UserListURL = UserListURL + new String(Base64.encode(value.getBytes("UTF-8"), Base64.DEFAULT));
            UserListURL = UserListURL.replaceAll("\n", "");


            url = new URL(UserListURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();


            int code = c.getResponseCode();
            String s = "";
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
                s = s.replace(";", " ");
                responseMessage = s.split("-@-");
            } else {
                responseMessage = new String[]{""};
            }
        } catch (Exception e) {
            Log.w("HTTP:", e);
            responseMessage = new String[]{""};
        }


        final String[] finalResponseMessage = responseMessage;
        RespAdd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    String search = RespAdd.getText().toString();
                    for (String st : finalResponseMessage) {
                        if (st.contains(search)) {
                            search = st;
                        }
                    }
                    RespAdd.setText(search);
                }
                return false;
            }
        });


        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String TID = UUID.randomUUID().toString();
                String Subject = SubjectAdd.getText().toString();
                String TDescription = DescAdd.getText().toString();
                String DueDate = DateAdd.getText().toString();
                String Responsibe = RespAdd.getText().toString();
                String Report = "";
                int TStatus = 1;

                String[] Taskdata = {TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), Responsibe};

                SendEdit se = new SendEdit(getBaseContext());

                se.execute(Taskdata);

                finish();
            }
        });


    }
}
