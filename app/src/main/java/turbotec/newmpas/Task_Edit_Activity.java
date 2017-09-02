package turbotec.newmpas;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Task_Edit_Activity extends AppCompatActivity {


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
    private static final String SendDelivered = "SendDelivered";
    private static final String isSeen = "isSeen";
    private static final String Task_Report = "Report";
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
    EditText SE;
    EditText DeE;
    TextView CE;
    EditText DE;
    TextView StE;
    EditText RE;
    TextView ST;
    TextView DeT;
    TextView CT;
    TextView DT;
    TextView StT;
    String Subject;
    String Creator;
    String DueDate;
    String TStatus;
    String TReport;
    String TDescription;
    String Report = "";
    boolean TEditable;
    boolean TReply;
    private SharedPreferenceHandler share;
    private Context MyContext;
    private String TID;

    private void updateLabel() {

        EditText edittext = (EditText) findViewById(R.id.DueDateEdit);
        String myFormat = "MMM dd yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        String W;

        share = SharedPreferenceHandler.getInstance(this);
        MyContext = getBaseContext();


        if (b != null) {
            W = b.getString(getString(R.string.What));

            Subject = b.getString(getString(R.string.Subject));
            Creator = b.getString(getString(R.string.TCreator));
            DueDate = b.getString(getString(R.string.DueDate));
            TStatus = b.getString(getString(R.string.TStatus));
            TDescription = b.getString(getString(R.string.TDescription));
            TEditable = b.getBoolean(getString(R.string.TEditable));
            TReply = b.getBoolean(getString(R.string.TReplyAble));
            TID = b.getString(getString(R.string.TID));
            Report = b.getString(getString(R.string.TReport));

            if (W.equals("Edit")) {
                setContentView(R.layout.task_edit_layout);


                SE = (EditText) findViewById(R.id.SubjectEdit);
                DeE = (EditText) findViewById(R.id.TDescriptionEdit);
                CE = (TextView) findViewById(R.id.TCreatorEdit);
                DE = (EditText) findViewById(R.id.DueDateEdit);
                StE = (TextView) findViewById(R.id.TStatusEdit);
                RE = (EditText) findViewById(R.id.TReply);


                SE.setText(Subject);
                DeE.setText(TDescription);
                CE.setText(Creator);
                DE.setText(DueDate);
                StE.setText(TStatus);
                RE.setText(TReport);


                EditText edittext = (EditText) findViewById(R.id.DueDateEdit);


                edittext.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(Task_Edit_Activity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });


                Button EditBut = (Button) findViewById(R.id.ButtonEditDone);

                EditBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Subject = SE.getText().toString();
                        TDescription = DeE.getText().toString();
                        Creator = CE.getText().toString();
                        DueDate = DE.getText().toString();
                        TStatus = StE.getText().toString();
                        Report = RE.getText().toString();

                        SendEdit se = new SendEdit(getBaseContext());

                        se.execute(MyContext);

                        finish();

                    }
                });
            } else if (W.equals("Reply")) {

                setContentView(R.layout.task_reply_layout);

                RE = (EditText) findViewById(R.id.TReply);


                ST = (TextView) findViewById(R.id.SubjectR);
                DeT = (TextView) findViewById(R.id.TDescriptionR);
                CT = (TextView) findViewById(R.id.TCreatorR);
                DT = (TextView) findViewById(R.id.DueDateR);
                StT = (TextView) findViewById(R.id.TStatusR);
                RE = (EditText) findViewById(R.id.TReply);


                ST.setText(Subject);
                DeT.setText(TDescription);
                CT.setText(Creator);
                DT.setText(DueDate);
                StT.setText(TStatus);
                RE.setText(Report);


                Button ReplyButt = (Button) findViewById(R.id.ButtonReplyDone);

                ReplyButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Report = RE.getText().toString();


                        SendEdit se = new SendEdit(getBaseContext());

                        se.execute(MyContext);

                        finish();

                    }
                });

            }


        }
    }


    public class SendEdit extends AsyncTask {


        private final Context MyContext;
        private final String OPERATION_NAME_DELIVERED = "EditTask";
        private String SOAP_ACTION_EditTask = "EditTask";
        private String WSDL_TARGET_NAMESPACE;
        private String SOAP_ADDRESS;


        public SendEdit(Context myContext) {
            MyContext = myContext;
            share = SharedPreferenceHandler.getInstance(MyContext);
//        db = DatabaseHandler.getInstance(MyContext);
//        database = db.getWritableDatabase();
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
            values.put(TASK_Title, Subject);
            values.put(Task_Description, TDescription);
            values.put(TASK_DueDate, DueDate);
            values.put(TASK_Creator, Creator);
            values.put(TASK_Status, TStatus);
            values.put(SendDelivered, false);
            values.put(isSeen, true);
            values.put(TASK_Editable, TEditable);
            values.put(TASK_ReplyAble, TReply);
            values.put(Task_Report, Report);

            getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{TID});


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

                String plaintext = "value1=" + share.GetDeviceID() + ",value2=" + share.GetToken()
                        + ",value3=" + TID + ",value4=" + Subject + ",value5=" + TDescription
                        + ",value6=" + DueDate + ",value7=" + Report;


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


                if (response.toString().contains(MyContext.getString(R.string.Seen)) || (response.toString().contains(MyContext.getString(R.string.Delivered)))
                        || (response.toString().contains(MyContext.getString(R.string.True)))) {


                    values.put("SendDelivered", true);

                    MyContext.getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{TID});
                }


            } catch (XmlPullParserException | IOException soapFault) {
                soapFault.printStackTrace();
            }


            return response.toString();
        }
    }


}
