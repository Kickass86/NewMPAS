package turbotec.newmpas;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
    AutoCompleteTextView ResE1;
    EditText ResE2;
    TextView CE;
    EditText DE;
    //    TextView StE;
    EditText RE;
    TextView RH;
    TextView ST;
    TextView DeT;
    TextView CT;
    TextView DT;
    //    TextView StT;
    String Subject;
    String Creator;
    String DueDate;
    int TStatus;
    String TReport;
    String TDescription;
    String TResponsible;
    String Report = "";
    boolean TDeletable;
    boolean TEditable;
    boolean TReply;
    private String IDResponsible;
    private String NameResponsible;
    private String[] Responsible;
    //    private String UserListURL;
//    private URL url;
//    private SharedPreferenceHandler share;
//    private Context MyContext;
    private String TID;

//    String[] responseMessage;

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

//        share = SharedPreferenceHandler.getInstance(this);
//        MyContext = getBaseContext();


        if (b != null) {
            W = b.getString(getString(R.string.What));

            Subject = b.getString(getString(R.string.Subject));
            Creator = b.getString(getString(R.string.TCreator));
            DueDate = b.getString(getString(R.string.DueDate));
            TStatus = b.getInt(getString(R.string.TStatus));
            TDescription = b.getString(getString(R.string.TDescription));
            TEditable = b.getBoolean(getString(R.string.TEditable));
            TReply = b.getBoolean(getString(R.string.TReplyAble));
            TDeletable = b.getBoolean(getString(R.string.TDeletable));
            TID = b.getString(getString(R.string.TID));
            Report = b.getString(getString(R.string.TReport));

            if (W.equals("Edit")) {
                setContentView(R.layout.task_edit_layout);

                ResE1 = (AutoCompleteTextView) findViewById(R.id.TResponsibleEdit);


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

                ResE1.setAdapter(adapter);
                ResE1.setThreshold(1);
                adapter.notifyDataSetChanged();

                ResE1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                CE = (TextView) findViewById(R.id.TCreatorEdit);
                DE = (EditText) findViewById(R.id.DueDateEdit);
//                StE = (TextView) findViewById(R.id.TStatusEdit);
                RH = (TextView) findViewById(R.id.THReply);
                RE = (EditText) findViewById(R.id.TReply);


                SE.setText(Subject);
                DeE.setText(TDescription);
                CE.setText(Creator);
                DE.setText(DueDate);
//                StE.setText(TStatus);
                RH.setText(TReport);


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


                        if ((Subject.isEmpty()) | (TDescription.isEmpty()) | (DueDate.isEmpty()) | (NameResponsible.isEmpty())) {
                            startActivity(new Intent(getBaseContext(), Task_Edit_Activity.class));
                            finish();
                        }


//                        TResponsible = ResE2.getText().toString();
                        String[] Taskdata;
                        if (!TResponsible.isEmpty()) {
                            Taskdata = new String[]{TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), "1", IDResponsible, NameResponsible};
                        } else {
                            Taskdata = new String[]{TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), "1", IDResponsible, NameResponsible};
                        }


                        SendEdit se = new SendEdit(getBaseContext());

                        se.execute(Taskdata);

                        finish();

                    }
                });
            } else if (W.equals("Reply")) {

                setContentView(R.layout.task_reply_layout);


                Spinner dropdown = (Spinner) findViewById(R.id.TStatusR);
                String[] items = new String[]{"Not Assigned", "In Progress", "Finished", "Closed"};

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

                dropdown.setAdapter(adapter);

                dropdown.setSelection(TStatus);



                ST = (TextView) findViewById(R.id.SubjectR);
                DeT = (TextView) findViewById(R.id.TDescriptionR);
                CT = (TextView) findViewById(R.id.TCreatorR);
                DT = (TextView) findViewById(R.id.DueDateR);
//                StT = (Spinner) findViewById(R.id.TStatusR);
                RH = (TextView) findViewById(R.id.THReply);
                RE = (EditText) findViewById(R.id.TReply);


                ST.setText(Subject);
                DeT.setText(TDescription);
                CT.setText(Creator);
                DT.setText(DueDate);
//                StT.setText(TStatus);
                RE.setText(Report);


                Button ReplyButt = (Button) findViewById(R.id.ButtonReplyDone);

                ReplyButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Report = RE.getText().toString();

                        Spinner dropdown = (Spinner) findViewById(R.id.TStatusR);
                        TStatus = dropdown.getSelectedItemPosition();


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

                        String[] TaskData = {TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus)};

                        SendEdit se = new SendEdit(getBaseContext());

                        se.execute(TaskData);

                        finish();

                    }
                });

            }


        }
    }





}

