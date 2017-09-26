package turbotec.newmpas;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import android.app.IntentService;
//import android.content.Intent;
//import android.support.annotation.Nullable;

public class Task_Detail_Activity extends AppCompatActivity {

    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/tasks/";
    //    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static boolean BackFlag = true;
    String Subject;
    String Creator;
    String DueDate;
    int TStatus;
    String TDescription;
    String TNameResponsible;
    String Report = "";
    boolean TEditable;
    boolean TReplyable;
    boolean TisCreator;
    boolean TisResposible;
    boolean TDeletable;
    private SharedPreferenceHandler share;
    //    static final Uri CONTENT_URI2 = Uri.parse(URL2);
//    private static DatabaseHandler db;
    //    private final Context main_menu;
    //    SQLiteDatabase database;
    private Button DelBut;
    private Button EditBut;
    private Button ReplyBut;
    private String TID;
    private Task_Detail_Activity activity;


//    private enum Status {
//        Not_Assigned, In_Progress, Finished, Closed
//    }

    public Task_Detail_Activity() {
        activity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_layout);

        MainActivity.setTab = 1;

        try {
            share = SharedPreferenceHandler.getInstance(this);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            Bundle b = getIntent().getExtras();


            if (b != null) {
                Subject = b.getString(getString(R.string.Subject));
                Creator = b.getString(getString(R.string.TCreator));
                DueDate = b.getString(getString(R.string.DueDate));
                TStatus = b.getInt(getString(R.string.TStatus));
                TDescription = b.getString(getString(R.string.TDescription));
                TEditable = b.getBoolean(getString(R.string.TEditable));
                TReplyable = b.getBoolean(getString(R.string.TReplyAble));
                TDeletable = b.getBoolean(getString(R.string.TDeletable));
                TisCreator = b.getBoolean(getString(R.string.TisCreator));
                TisResposible = b.getBoolean(getString(R.string.TisResponsible));
                TNameResponsible = b.getString(getString(R.string.TNameResponsible));
                TID = b.getString(getString(R.string.TID));
                Report = b.getString(getString(R.string.TReport));


                TextView t1 = (TextView) findViewById(R.id.Subject);
                TextView t2 = (TextView) findViewById(R.id.TCreator);
                TextView t3 = (TextView) findViewById(R.id.DueDate);
                TextView t4 = (TextView) findViewById(R.id.TStatus);
                TextView t5 = (TextView) findViewById(R.id.TDescription);
                TextView t6 = (TextView) findViewById(R.id.THReply);
                TextView t7 = (TextView) findViewById(R.id.TResponsible);

                Button b1 = (Button) findViewById(R.id.ButtonEdit);
                Button b2 = (Button) findViewById(R.id.ButtonReply);
                Button b3 = (Button) findViewById(R.id.ButtonDelete);

                t1.setText(Subject);
                t2.setText(Creator);
                t3.setText(DueDate);

                t5.setText(TDescription);
                t6.setText(Report);
                t7.setText(TNameResponsible);


                switch (TStatus) {
                    case 1:
                        t4.setText("Not Assigned");
                        break;
                    case 2:
                        t4.setText("In Progress");
                        break;
                    case 3:
                        t4.setText("Finished");
                        break;
                    case 1002:
                    case 0:
                    default:
                        t4.setText("Closed");
                        break;

                }

                if (TEditable) {
                    b1.setEnabled(true);
                }
                if (TReplyable) {
                    b2.setEnabled(true);
                }

                if (TDeletable) {
                    b3.setEnabled(true);
                }


                ContentValues values = new ContentValues();
                values.put("isSeen", true);

                getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{String.valueOf(TID)});





            }


//        if (!TDescription) {
            int z = 3;
            String[] data = new String[]{z + "", "1", String.valueOf(TID)};

            SendStatusAsyncTask taskstate = new SendStatusAsyncTask(this);

            taskstate.execute(data);
//        }
            DelBut = (Button) findViewById(R.id.ButtonDelete);

            DelBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Task_Detail_Activity.this);

                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    setContentView(R.layout.waiting_layout);

                                    String[] Taskdata = {TID, Subject, TDescription, DueDate, Report, "0", "0"};

                                    SendEdit se = new SendEdit(getBaseContext());

//                                    getActionBar().setHomeButtonEnabled(false);
//                                    getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
//                                    getSupportActionBar().setHomeButtonEnabled(false);
                                    getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
                                    getSupportActionBar().setDisplayShowHomeEnabled(false);
//                                    BackFlag = false;
                                    invalidateOptionsMenu();

                                    se.execute(Taskdata);

//                                    getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{String.valueOf(TID)});

//                                    finish();

                                }
                            })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.cancel();

                                        }
                                    })
                            .setMessage(R.string.dialog_message)
                            .setTitle(R.string.Delete_Button);
                    AlertDialog adialog = alertDialogBuilder.create();
                    adialog.show();


                }
            });


            EditBut = (Button) findViewById(R.id.ButtonEdit);


            EditBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent EditActivity = new Intent(activity, Task_Edit_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(activity.getString(R.string.What), "Edit");
                    bundle.putString(activity.getString(R.string.TID), TID);
                    bundle.putString(activity.getString(R.string.Subject), Subject);
                    bundle.putString(activity.getString(R.string.TCreator), Creator);
                    bundle.putString(activity.getString(R.string.TNameResponsible), TNameResponsible);
                    bundle.putString(activity.getString(R.string.DueDate), DueDate);
                    bundle.putInt(activity.getString(R.string.TStatus), TStatus);
                    bundle.putString(activity.getString(R.string.TDescription), TDescription);
                    bundle.putString(activity.getString(R.string.TReport), Report);
                    bundle.putBoolean(activity.getString(R.string.TEditable), TEditable);
                    bundle.putBoolean(activity.getString(R.string.TReplyAble), TReplyable);
                    bundle.putBoolean(activity.getString(R.string.TDeletable), TDeletable);


                    EditActivity.putExtras(bundle);


                    EditActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    activity.startActivity(EditActivity);
                    overridePendingTransition(0, 0);
                    finish();


                }
            });


            ReplyBut = (Button) findViewById(R.id.ButtonReply);


            ReplyBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent EditActivity = new Intent(activity, Task_Edit_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(activity.getString(R.string.What), "Reply");
                    bundle.putString(activity.getString(R.string.TID), TID);
                    bundle.putString(activity.getString(R.string.Subject), Subject);
                    bundle.putString(activity.getString(R.string.TCreator), Creator);
                    bundle.putString(activity.getString(R.string.DueDate), DueDate);
                    bundle.putInt(activity.getString(R.string.TStatus), TStatus);
                    bundle.putString(activity.getString(R.string.TDescription), TDescription);
                    bundle.putString(activity.getString(R.string.TReport), Report);
                    bundle.putBoolean(activity.getString(R.string.TEditable), TEditable);
                    bundle.putBoolean(activity.getString(R.string.TReplyAble), TReplyable);
                    bundle.putBoolean(activity.getString(R.string.TDeletable), TDeletable);


                    EditActivity.putExtras(bundle);

                    EditActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    activity.startActivity(EditActivity);
                    overridePendingTransition(0, 0);
                    finish();



                }
            });




        } catch (Exception e) {
            share.SaveError(e.getMessage());
            Intent SE = new Intent(this, SendError.class);
            startService(SE);
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (BackFlag) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
//        }else
//        {
//
//        }
//        return true;
    }


}
