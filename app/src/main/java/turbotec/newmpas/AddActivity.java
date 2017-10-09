package turbotec.newmpas;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
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
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {


    static final String PROVIDER_NAME = "turbotec.newmpas.MyProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/users/";
    static final Uri CONTENT_URI = Uri.parse(URL);
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
//                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                overridePendingTransition(0, 0);
                startActivity(in);
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
    String my_var;
    //    private String UserListURL;
    private String IDResponsible;
    private String NameResponsible = "";
    private String[] Responsible;
    //    private URL url;
    private EditText DateAdd;
    private EditText SubjectAdd;
    private EditText DescAdd;
    private AutoCompleteTextView RespAdd;
    private Button Done;
    private SharedPreferenceHandler share;


    private void updateLabel() {

        EditText edittext = (EditText) findViewById(R.id.DueDateAdd);
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_task);
        share = SharedPreferenceHandler.getInstance(getBaseContext());
        registerReceiver(broadcastReceiverEdit, new IntentFilter("Edit Done"));

        DateAdd = (EditText) findViewById(R.id.DueDateAdd);
        SubjectAdd = (EditText) findViewById(R.id.SubjectAdd);
        DescAdd = (EditText) findViewById(R.id.TDescriptionAdd);
        RespAdd = (AutoCompleteTextView) findViewById(R.id.TResponsibleAdd);
        Done = (Button) findViewById(R.id.ButtonAddDone);


        DateAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


//        final String[] finalResponseMessage = responseMessage;
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


        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
//        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.TResponsibleAdd);
        RespAdd.setAdapter(adapter);
        RespAdd.setThreshold(1);
        adapter.notifyDataSetChanged();

        RespAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                IDResponsible = Responsible[position];
//                NameResponsible = (String) parent.getItemAtPosition(position);
                String selection = (String) parent.getItemAtPosition(position);
                NameResponsible = selection;
//                int pos = -1;

                for (int i = 0; i < Names.length; i++) {
                    if (Names[i].equals(selection)) {
                        IDResponsible = Responsible[i];
                        break;
                    }
                }
            }
        });

//        RespAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (!hasFocus) {
//                    String val = RespAdd.getText() + "";
//                    String code = RespAdd.get(val);
//                    Log.v("TruitonAutoCompleteTextViewActivity",
//                            "Selected City Code: " + code);
//                    if (code == null) {
//                        RespAdd.setError("Invalid City");
//                    }
//                }
//            }
//        });


//        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                my_var = adapter.getItem(position).toString();
//            }
//        });
/**
 * Unset the var whenever the user types. Validation will
 * then fail. This is how we enforce selecting from the list.
 */
//        textView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                my_var = null;
//                Cursor c = getContentResolver().query(CONTENT_URI, null, "Name like N'%?%'", new String[]{(String) s}, null);
//                String[] Names = new String[c.getCount()];
//                int index = 0;
//                if (c.moveToFirst()) {
//                    do {
//                        Names[index] = c.getString(1);
//                        Responsible = c.getString(0);
//                        index++;
//                    }while(c.moveToNext());
//                }
//                c.close();
//
//            }
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });


//        RespAdd.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//
//                    String search = RespAdd.getText().toString();
//                    Cursor c = getContentResolver().query(CONTENT_URI, null, "USER_NAME like ?", new String[]{search}, null);
//                    if (c.moveToFirst()) {
//                        search = c.getString(1);
//                        Responsible = c.getString(0);
//                    }
//                    c.close();
//                    RespAdd.setText(search);
//                }
//                return false;
//            }
//        });


        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.waiting_layout);

                String TID = UUID.randomUUID().toString();
                String Subject = SubjectAdd.getText().toString();
                String TDescription = DescAdd.getText().toString();
                String DueDate = DateAdd.getText().toString();


                if ((Subject.isEmpty()) | (TDescription.isEmpty()) | (DueDate.isEmpty()) | (NameResponsible.isEmpty())) {
                    startActivity(new Intent(getBaseContext(), AddActivity.class));
                    finish();
                    return;
                }
                String Report = "";
                int TStatus = 2;

                String[] TaskData = {TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), "1", IDResponsible, NameResponsible};

                SendEdit se = new SendEdit(getBaseContext());

                se.execute(TaskData);

            }
        });


    }
}
