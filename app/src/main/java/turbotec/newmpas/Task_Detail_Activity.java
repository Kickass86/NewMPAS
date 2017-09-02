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
    String Subject;
    String Creator;
    String DueDate;
    String TStatus;
    String TDescription;
    String Report = "";
    boolean TEditable;
    boolean TReplyable;
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
                TStatus = b.getString(getString(R.string.TStatus));
                TDescription = b.getString(getString(R.string.TDescription));
                TEditable = b.getBoolean(getString(R.string.TEditable));
                TReplyable = b.getBoolean(getString(R.string.TReplyAble));
                TID = b.getString(getString(R.string.TID));
                Report = b.getString(getString(R.string.TReport));


                TextView t1 = (TextView) findViewById(R.id.Subject);
                TextView t2 = (TextView) findViewById(R.id.TCreator);
                TextView t3 = (TextView) findViewById(R.id.DueDate);
                TextView t4 = (TextView) findViewById(R.id.TStatus);
                TextView t5 = (TextView) findViewById(R.id.TDescription);
                TextView t6 = (TextView) findViewById(R.id.TReply);

                Button b1 = (Button) findViewById(R.id.ButtonEdit);
                Button b2 = (Button) findViewById(R.id.ButtonReply);

                t1.setText(Subject);
                t2.setText(Creator);
                t3.setText(DueDate);
                t4.setText(TStatus);
                t5.setText(TDescription);
                t6.setText(Report);

                if (TEditable) {
                    b1.setEnabled(true);
                }
                if (TReplyable) {
                    b2.setEnabled(true);
                }

//                Intent in = new Intent(this, SaveState.class);
//                startService(in);

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {

//                        SQLiteDatabase database = db.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put("isSeen", true);

                        getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{String.valueOf(TID)});


//                    }


//                }).start();


//            }
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


//                                SQLiteDatabase database = db.getWritableDatabase();
//                                database.delete("Messages", "MessageID  = ?", new String[]{String.valueOf(TID)});
                                    getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{String.valueOf(TID)});
//                                database.close();
                                    finish();

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
                    bundle.putString(activity.getString(R.string.DueDate), DueDate);
                    bundle.putString(activity.getString(R.string.TStatus), TStatus);
                    bundle.putString(activity.getString(R.string.TDescription), TDescription);
                    bundle.putString(activity.getString(R.string.TReport), Report);
                    bundle.putBoolean(activity.getString(R.string.TEditable), TEditable);
                    bundle.putBoolean(activity.getString(R.string.TReplyAble), TReplyable);


                    EditActivity.putExtras(bundle);


                    EditActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    activity.startActivity(EditActivity);


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
                    bundle.putString(activity.getString(R.string.TStatus), TStatus);
                    bundle.putString(activity.getString(R.string.TDescription), TDescription);
                    bundle.putString(activity.getString(R.string.TReport), Report);
                    bundle.putBoolean(activity.getString(R.string.TEditable), TEditable);
                    bundle.putBoolean(activity.getString(R.string.TReplyAble), TReplyable);


                    EditActivity.putExtras(bundle);

                    EditActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    activity.startActivity(EditActivity);


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
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
