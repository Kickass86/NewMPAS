package turbotec.newmpas;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Task_Edit_Activity extends AppCompatActivity {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/tasks/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final String URL = "content://" + PROVIDER_NAME + "/users/";
    static final Uri CONTENT_URI = Uri.parse(URL);
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
    private final BroadcastReceiver broadcastReceiverEdit = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();

            setTitle(getString(R.string.app_name));
            Log.e("Edit", "Done!");
            if (action.equals("Edit Done")) {


                MainActivity.setTab = TabController.Tabs.Task;
                MainActivity.Gone = true;
                Intent in = new Intent(context, MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                overridePendingTransition(0, 0);
                finish();


            }
        }
    };
    private final String ip = "192.168.1.13";
    private final int port = 80;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    EditText SE;
    EditText DeE;
    EditText RE;
    EditText DE;
    AutoCompleteTextView ResE;
    //    EditText ResE2;
    TextView CE;

    //    TextView StE;

    TextView RH;
    TextView ST;
    TextView DeT;
    TextView CT;
    TextView DT;
    TextView StT;
    CheckBox NFCB;
    CheckBox CCB;
    CheckBox CBF;
    String Subject;
    String Creator;
    String TNameResponsible;
    String DueDate;
    int TStatus;
    String TReport;
    String TDescription;
    String TResponsible;
    String Report = "";
    boolean TDeletable;
    boolean TEditable;
    boolean TReplyable;
    private String IDResponsible;
    private String NameResponsible = "";
    private String[] Responsible;
    //    private String UserListURL;
//    private URL url;
//    private SharedPreferenceHandler share;
//    private Context MyContext;
    private String TID;

//    String[] responseMessage;

    private void updateLabel() {

        EditText edittext = (EditText) findViewById(R.id.DueDateEdit);
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(broadcastReceiverEdit);
        super.onDestroy();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        String W;

        registerReceiver(broadcastReceiverEdit, new IntentFilter("Edit Done"));
//        share = SharedPreferenceHandler.getInstance(this);
//        MyContext = getBaseContext();


        if (b != null) {
            W = b.getString(getString(R.string.What));

            Subject = b.getString(getString(R.string.Subject));
            Creator = b.getString(getString(R.string.TCreator));
            TNameResponsible = b.getString(getString(R.string.TNameResponsible));
            DueDate = b.getString(getString(R.string.DueDate));
            TStatus = b.getInt(getString(R.string.TStatus));
            TDescription = b.getString(getString(R.string.TDescription));
            TEditable = b.getBoolean(getString(R.string.TEditable));
            TReplyable = b.getBoolean(getString(R.string.TReplyAble));
            TDeletable = b.getBoolean(getString(R.string.TDeletable));
            TID = b.getString(getString(R.string.TID));
            Report = b.getString(getString(R.string.TReport));

            if (W.equals("Edit")) {
                setContentView(R.layout.task_edit_layout);

                ResE = (AutoCompleteTextView) findViewById(R.id.TResponsibleEdit);


                Cursor c = getContentResolver().query(CONTENT_URI, null, null, null, null);
                final String[] Names = new String[c.getCount()];
                Responsible = new String[c.getCount()];
                int index = 0;
                if (c.moveToFirst()) {
                    do {
                        Names[index] = c.getString(1);
                        Responsible[index] = c.getString(0);
                        index++;
                    } while (c.moveToNext());
                }
                c.close();


                final ArrayAdapter<String> adapter = new ArrayAdapter<>(Task_Edit_Activity.this, android.R.layout.simple_list_item_1, Names);

                ResE.setAdapter(adapter);
                ResE.setThreshold(1);
                adapter.notifyDataSetChanged();

                ResE.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String selection = (String) parent.getItemAtPosition(position);
                        NameResponsible = selection;

                        for (int i = 0; i < Names.length; i++) {
                            if (Names[i].equals(selection)) {
                                IDResponsible = Responsible[i];
                                break;
                            }
                        }
                    }
                });

                SE = (EditText) findViewById(R.id.SubjectEdit);
                DeE = (EditText) findViewById(R.id.TDescriptionEdit);
//                CE = (TextView) findViewById(R.id.TCreatorEdit);
                DE = (EditText) findViewById(R.id.DueDateEdit);
                StT = (TextView) findViewById(R.id.TStatusEdit);
                RH = (TextView) findViewById(R.id.THReply);
                RE = (EditText) findViewById(R.id.TReply);
                NFCB = (CheckBox) findViewById(R.id.checkboxNotFinished);
                CCB = (CheckBox) findViewById(R.id.checkboxClosed);


                if (TStatus == 3) {
                    NFCB.setVisibility(View.VISIBLE);
                } else if (TStatus == 2) {
                    CCB.setVisibility(View.VISIBLE);
                }


                SE.setText(Subject);
                DeE.setText(TDescription);
                ResE.setText(TNameResponsible);
//                CE.setText(Creator);
                DE.setText(DueDate);
                String s;
                switch (TStatus) {

                    case 1:
                        s = "Not Assigned";
                        break;
                    case 2:
                        s = "In Progress";
                        break;
                    case 3:
                        s = "Finished";
                        break;
                    case 1002:
                    case 0:
                    default:
                        s = "Closed";
                        break;
                }
                StT.setText(s);
                RH.setText(TReport);


                EditText edittext = (EditText) findViewById(R.id.DueDateEdit);


                edittext.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(Task_Edit_Activity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });


                Button EditBut = (Button) findViewById(R.id.ButtonEditDone);

                EditBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        setContentView(R.layout.waiting_layout);
                        Subject = SE.getText().toString();
                        TDescription = DeE.getText().toString();
                        DueDate = DE.getText().toString();
                        NameResponsible = ResE.getText().toString();

                        if (((Subject.isEmpty()) | (TDescription.isEmpty()) | (DueDate.isEmpty()) | (NameResponsible.isEmpty())) || (NFCB.isChecked()) || (CCB.isChecked())) {

                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.What), "Edit");
                            bundle.putString(getString(R.string.TID), TID);
                            bundle.putString(getString(R.string.Subject), Subject);
                            bundle.putString(getString(R.string.TCreator), Creator);
                            bundle.putString(getString(R.string.TNameResponsible), TNameResponsible);
                            bundle.putString(getString(R.string.DueDate), DueDate);
                            bundle.putInt(getString(R.string.TStatus), TStatus);
                            bundle.putString(getString(R.string.TDescription), TDescription);
                            bundle.putString(getString(R.string.TReport), Report);
                            bundle.putBoolean(getString(R.string.TEditable), TEditable);
                            bundle.putBoolean(getString(R.string.TReplyAble), TReplyable);
                            bundle.putBoolean(getString(R.string.TDeletable), TDeletable);
                            Intent EditActivity = new Intent(Task_Edit_Activity.this, Task_Edit_Activity.class);

                            EditActivity.putExtras(bundle);
                            startActivity(new Intent(getBaseContext(), Task_Edit_Activity.class));
                            overridePendingTransition(0, 0);
                            finish();
                            return;
                        }

                        if (NFCB.isChecked()) {
                            TStatus = 2;
                        }
                        if (CCB.isChecked()) {
                            TStatus = 1002;
                        }

//                        Subject = SE.getText().toString();
//                        TDescription = DeE.getText().toString();
//                        TResponsible = ResE2.getText().toString();
                        String[] Taskdata;
                        if (!NameResponsible.isEmpty()) {
                            Taskdata = new String[]{TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), "1", IDResponsible, NameResponsible};
                        } else {
                            Taskdata = new String[]{TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), "1", IDResponsible, NameResponsible};
                        }


                        SendEdit se = new SendEdit(getBaseContext());
                        Task_Detail_Activity.BackFlag = false;
                        invalidateOptionsMenu();

                        se.execute(Taskdata);

                    }
                });
            } else if (W.equals("Reply")) {

                setContentView(R.layout.task_reply_layout);


//                Spinner dropdown = (Spinner) findViewById(R.id.TStatusR);
//                String[] items = new String[]{"Not Assigned", "In Progress", "Finished", "Closed"};
//                String[] items = new String[]{"Not Assigned", "In Progress", "Finished", "Closed"};

//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

//                dropdown.setAdapter(adapter);

//                dropdown.setSelection(TStatus);



                ST = (TextView) findViewById(R.id.SubjectR);
                DeT = (TextView) findViewById(R.id.TDescriptionR);
                CT = (TextView) findViewById(R.id.TCreatorR);
                DT = (TextView) findViewById(R.id.DueDateR);
                StT = (TextView) findViewById(R.id.TStatusR);
                RH = (TextView) findViewById(R.id.THReply);
                RE = (EditText) findViewById(R.id.TReply);
                CBF = (CheckBox) findViewById(R.id.checkboxFinished);


                ST.setText(Subject);
                DeT.setText(TDescription);
                CT.setText(Creator);
                DT.setText(DueDate);

                String s;
                switch (TStatus) {

                    case 1:
                        s = "Not Assigned";
                        break;
                    case 2:
                        s = "In Progress";
                        break;
                    case 3:
                        s = "Finished";
                        break;
                    case 1002:
                    case 0:
                    default:
                        s = "Closed";
                        break;
                }

                StT.setText(s);
                RE.setText(Report);

                if ((TStatus == 3) | (TStatus == 1002) | (TStatus == 0)) {
                    CBF.setVisibility(View.GONE);
                }



                Button ReplyButt = (Button) findViewById(R.id.ButtonReplyDone);

                ReplyButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        setContentView(R.layout.waiting_layout);

                        Report = RE.getText().toString();
                        boolean b = CBF.isChecked();
                        if (b) {
                            TStatus = 3;
                        }

//                        Spinner dropdown = (Spinner) findViewById(R.id.TStatusR);
//                        TStatus = dropdown.getSelectedItemPosition();


                        ContentValues values = new ContentValues();
                        values.put(TASK_Title, Subject);
                        values.put(Task_Description, TDescription);
                        values.put(TASK_DueDate, DueDate);
                        values.put(TASK_Creator, Creator);
                        values.put(TASK_Status, TStatus);
                        values.put(SendDelivered, false);
                        values.put(isSeen, true);
                        values.put(TASK_Editable, TEditable);
                        values.put(TASK_ReplyAble, TReplyable);
                        values.put(Task_Report, Report);

                        getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{TID});

                        String[] TaskData = {TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), "0"};

                        SendEdit se = new SendEdit(getBaseContext());
                        Task_Detail_Activity.BackFlag = false;
                        invalidateOptionsMenu();

                        se.execute(TaskData);


                    }
                });

            }


        }
    }





}

