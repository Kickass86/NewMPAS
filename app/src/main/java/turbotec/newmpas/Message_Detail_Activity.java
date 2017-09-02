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
import android.widget.ImageView;
import android.widget.TextView;

//import android.app.IntentService;
//import android.content.Intent;
//import android.support.annotation.Nullable;

public class Message_Detail_Activity extends AppCompatActivity {

    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    //    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    private SharedPreferenceHandler share;
    //    static final Uri CONTENT_URI2 = Uri.parse(URL2);
//    private static DatabaseHandler db;
    //    private final Context main_menu;
    //    SQLiteDatabase database;
    private Button DelBut;
    private Integer ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_layout);

        MainActivity.setTab = 0;

        try {
            share = SharedPreferenceHandler.getInstance(this);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            Bundle b = getIntent().getExtras();
            String Title;
            String Body;
            Boolean Critical;
            Boolean isSeen;
            Boolean isSendSeen = false;

            if (b != null) {
                Title = b.getString(getString(R.string.Title));
                Body = b.getString(getString(R.string.Body));
                Critical = b.getBoolean(getString(R.string.Critical));
                isSeen = b.getBoolean(getString(R.string.Seen));
                isSendSeen = b.getBoolean(getString(R.string.SendSeen));
                ID = b.getInt(getString(R.string.ID));


                TextView t1 = (TextView) findViewById(R.id.titledetail2);
                TextView t2 = (TextView) findViewById(R.id.bodydetail2);
                ImageView i1 = (ImageView) findViewById(R.id.statedetail2);
                ImageView i2 = (ImageView) findViewById(R.id.Critical2);
                t1.setText(Title);
                t2.setText(Body);
                i1.setImageResource(R.mipmap.ic_done_all_black_24dp);
                if (Critical) {
                    i2.setImageResource(R.mipmap.ic_priority_high_black_24dp);
                }
                if (!isSeen) {

//                Intent in = new Intent(this, SaveState.class);
//                startService(in);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {

//                        SQLiteDatabase database = db.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put("Seen", true);

                            getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{String.valueOf(ID)});


//                        }


//                    }).start();


                }
            }


            if (!isSendSeen) {
                int z = 3;
                String[] data = new String[]{z + "", "1", String.valueOf(ID)};

                SendStatusAsyncTask taskstate = new SendStatusAsyncTask(this);

                taskstate.execute(data);
            }
            DelBut = (Button) findViewById(R.id.button2);

            DelBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Message_Detail_Activity.this);

                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


//                                SQLiteDatabase database = db.getWritableDatabase();
//                                database.delete("Messages", "MessageID  = ?", new String[]{String.valueOf(ID)});
                                    getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{String.valueOf(ID)});
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


        } catch (Exception e) {
            share.SaveError(e.getMessage());
            Intent SE = new Intent(this, SendError.class);
            startService(SE);
        }


    }

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



//    private class DatabaseDeleteOperation extends AsyncTask<String, String, String> {
//
//
//        @Override
//        protected String doInBackground(String... params) {
//
////            String tablename = params[0];
////            String WhereClause = params[1];
//
////            SQLiteDatabase database = db.getWritableDatabase();
////            arg = new String[]{String.valueOf(ID)};
////            database.delete("Messages", "MessageID  = ?", new String[]{String.valueOf(ID)});
//            getContentResolver().delete(CONTENT_URI1, "MessageID  = ?", new String[]{String.valueOf(ID)});
////            database.close();
//            finish();
//
//            return null;
//        }
//
//
//    }


}
