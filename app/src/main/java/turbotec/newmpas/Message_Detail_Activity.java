package turbotec.newmpas;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

//import android.app.IntentService;
//import android.content.Intent;
//import android.support.annotation.Nullable;

public class Message_Detail_Activity extends AppCompatActivity {

    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    //    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    //    static final Uri CONTENT_URI2 = Uri.parse(URL2);
//    private static DatabaseHandler db;
    //    private final Context main_menu;
    //    SQLiteDatabase database;
    private static final String ip = "192.168.1.13";
    private static final int port = 80;
    //            String Link;
    static GetLink n;
    private static SharedPreferenceHandler share;
    private Intent starterIntent;
    private String Link;
    private Button DelBut;
    private Integer ID = 1;
    private String Title;
    private String Body;
    private Boolean Critical;
    private Boolean isSeen;
    private Boolean isSendSeen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_layout);

        MainActivity.setTab = TabController.Tabs.Message;


        share = SharedPreferenceHandler.getInstance(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle b = getIntent().getExtras();

        starterIntent = getIntent();

        if (b != null) {
            Title = b.getString(getString(R.string.Title));
            Body = b.getString(getString(R.string.Body));
            Critical = b.getBoolean(getString(R.string.Critical));
            isSeen = b.getBoolean(getString(R.string.Seen));
            isSendSeen = b.getBoolean(getString(R.string.SendSeen));
            Link = b.getString(getString(R.string.Link));
            ID = b.getInt(getString(R.string.ID));


            TextView t1 = (TextView) findViewById(R.id.titledetail2);
            TextView t2 = (TextView) findViewById(R.id.bodydetail2);
            TextView t3 = (TextView) findViewById(R.id.link);
            ImageView i1 = (ImageView) findViewById(R.id.statedetail2);
            ImageView i2 = (ImageView) findViewById(R.id.Critical2);

            t1.setText(Title);
            t2.setText(Body);
//                t3.setText(Link);
            t3.setText(Html.fromHtml("<u> Click Here </u>"));
            t3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.SelectColor1));
                    setContentView(R.layout.waiting_layout);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                    n = new GetLink();
                    n.execute();

                }
            });

//                t3.setText(Html.fromHtml(" <a href=" + Link +">Click Here</a> "));
//                t3.setMovementMethod(LinkMovementMethod.getInstance());
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


                                getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{String.valueOf(ID)});

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


    public class GetLink extends AsyncTask {

        String responseMessage;
        boolean exists = false;


        public GetLink() {

        }

        @Override
        protected Object doInBackground(Object[] params) {

            String requestString;
            URL url;

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
            // GetLinkRequest

//                                return exists;


            if (exists) {
                requestString = "http://192.168.1.13/Andr/GetLinkRequest.ashx?Value=";
            } else {
                requestString = "https://mpas.migtco.com:3000/Andr/GetLinkRequest.ashx?Value=";
            }

//        String requestString = intent.getStringExtra(REQUEST_STRING);
            Log.v("Intent Service", "Check Request");


            try {


                String value = "Val1=" + share.GetDeviceID() + ",Val2=" + share.GetToken() + ",Val3=1";
                requestString = requestString + new String(Base64.encode(value.getBytes("UTF-8"), Base64.DEFAULT));
                requestString = requestString.replaceAll("\n", "");


                url = new URL(requestString);
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

                    responseMessage = s;
                } else {
                    responseMessage = "";
                }


            } catch (Exception e) {
                Log.w("HTTP:", e);
                responseMessage = "";
            }


            return responseMessage;

        }

        @Override
        protected void onPostExecute(Object o) {

            finish();
            startActivity(starterIntent);

            if (exists) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.13/" + responseMessage));
                startActivity(browserIntent);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mpas.migtco.com:3000/" + responseMessage));
                startActivity(browserIntent);
            }

            super.onPostExecute(o);
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
