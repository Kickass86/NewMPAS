package turbotec.newmpas;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
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
    //    private String UserListURL;
    private String Responsibe;
    //    private URL url;
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_task);
        share = SharedPreferenceHandler.getInstance(getBaseContext());

        DateAdd = (EditText) findViewById(R.id.DueDateAdd);
        SubjectAdd = (EditText) findViewById(R.id.SubjectAdd);
        DescAdd = (EditText) findViewById(R.id.TDescriptionAdd);
        RespAdd = (EditText) findViewById(R.id.TResponsibleAdd);
        Done = (Button) findViewById(R.id.ButtonAddDone);


        DateAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


//        final String[] finalResponseMessage = responseMessage;
        RespAdd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    String search = RespAdd.getText().toString();
                    Cursor c = getContentResolver().query(CONTENT_URI, null, "USER_NAME like ?", new String[]{search}, null);
                    if (c.moveToFirst()) {
                        search = c.getString(1);
                        Responsibe = c.getString(0);
                    }
                    c.close();
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
//                String Responsibe = RespAdd.getText().toString();
                String Report = "";
                int TStatus = 1;

                String[] TaskData = {TID, Subject, TDescription, DueDate, Report, String.valueOf(TStatus), Responsibe};

                SendEdit se = new SendEdit(getBaseContext());

                se.execute(TaskData);

                finish();
            }
        });


    }
}
