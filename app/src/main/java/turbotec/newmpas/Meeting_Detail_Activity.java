package turbotec.newmpas;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ZAMANI on 10/22/2017.
 */

public class Meeting_Detail_Activity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/meetings/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    Button b1;
    String ID, Topic, Creator, Attendance, Secretary, Service, Room, Equipment, StartDate,
            EndDate, PersianDate, StartTime, EndTime;
    GoogleAccountCredential mCredential;
    //    private TextView mOutputText;
//    private Button mCallApiButton;
    Event event;
    ProgressDialog mProgress;
    private Intent starterIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_detail_layout);

        MainActivity.setTab = TabController.Tabs.Meeting;


//        share = SharedPreferenceHandler.getInstance(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle b = getIntent().getExtras();

        starterIntent = getIntent();

        if (b != null) {
            ID = b.getString(getString(R.string.MEETING_ID));
            Topic = b.getString(getString(R.string.MEETING_TOPIC));
            Creator = b.getString(getString(R.string.MEETING_CREATOR));
            Attendance = b.getString(getString(R.string.MEETING_ATTENDANCE));
            Secretary = b.getString(getString(R.string.MEETING_SECRETARY));
            Service = b.getString(getString(R.string.MEETING_SERVICES));
            Room = b.getString(getString(R.string.MEETING_ROOM));
            Equipment = b.getString(getString(R.string.MEETING_EQUIPMENT));
            StartDate = b.getString(getString(R.string.MEETING_START_DATE));
            EndDate = b.getString(getString(R.string.MEETING_END_DATE));
            PersianDate = b.getString(getString(R.string.MEETING_PER_DATE));
            StartTime = b.getString(getString(R.string.MEETING_START_TIME));
            EndTime = b.getString(getString(R.string.MEETING_END_TIME));


            TextView t1 = (TextView) findViewById(R.id.topic);
            TextView t2 = (TextView) findViewById(R.id.date);
            TextView t3 = (TextView) findViewById(R.id.time);
            TextView t4 = (TextView) findViewById(R.id.creator);
            TextView t5 = (TextView) findViewById(R.id.secretary);
            TextView t6 = (TextView) findViewById(R.id.room);
            TextView t7 = (TextView) findViewById(R.id.attendance);
            b1 = (Button) findViewById(R.id.Save);


            Cursor cursor = getContentResolver().query(CONTENT_URI1, null, "_id = ?", new String[]{ID}, null);
            cursor.moveToFirst();
            if ("0".equals(cursor.getString(14))) {
                b1.setEnabled(true);
            }

//            TextView t8 = (TextView) findViewById(R.id.bodydetail2);
//            TextView t9= (TextView) findViewById(R.id.link);
//            TextView t10 = (TextView) findViewById(R.id.titledetail2);
//            TextView t11 = (TextView) findViewById(R.id.bodydetail2);
//            TextView t12 = (TextView) findViewById(R.id.link);

            t1.setText(Topic);
            t2.setText(PersianDate);
            t3.setText(StartTime + " - " + EndTime);
            t4.setText(Creator);
            t5.setText(Secretary);
            t6.setText(Room);
            t7.setText(Attendance);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b1.setEnabled(false);
                    getResultsFromApi();
                }
            });

            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Calling Google Calendar API ...");
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());


        }


    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");

                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
//            mOutputText.setText("No network connection available.");
            Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_LONG).show();
        } else {
            new Meeting_Detail_Activity.MakeRequestTask(mCredential).execute();
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                Meeting_Detail_Activity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.

//            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<>();
//            Events events = mService.events().list("primary")
//                    .setMaxResults(10)
//                    .setTimeMin(now)
//                    .setOrderBy("startTime")
//                    .setSingleEvents(true)
//                    .execute();
//            List<Event> items = events.getItems();
//
//            for (Event event : items) {
//                DateTime start = event.getStart().getDateTime();
//                if (start == null) {
//                    // All-day events don't have start times, so just use
//                    // the start date.
//                    start = event.getStart().getDate();
//                }
//                eventStrings.add(
//                        String.format("%s (%s)", event.getSummary(), start));
//            }


            //change here
            event = new Event()
                    .setSummary("MPAS")
                    .setLocation(Room)
                    .setDescription(Topic);

            DateTime startDateTime = new DateTime(StartDate + "T" + StartTime + ":00+00:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Tehran");
            event.setStart(start);

            DateTime endDateTime = new DateTime(EndDate + "T" + EndTime + ":00+00:00");
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Tehran");
            event.setEnd(end);
            String calendarId = "primary";
            event = mService.events().insert(calendarId, event).execute();

            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
            //change here


            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
//            mOutputText.setText("");
//            mProgress.show();
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable(){
//                @Override
//                public void run(){
//                    startRecord();
//                    mp.stop();
//                    mp.release();
//                }
//            }, 5);


        }

        @Override
        protected void onPostExecute(List<String> output) {

//            Timer t = new Timer();
//            t.schedule(
//                    new TimerTask() {
//                        @Override
//                        public void run() {
//                            mProgress.hide();
//                        }
//                    },
//                    100
//            );


            if (output == null || output.size() == 0) {
//                mOutputText.setText("No results returned.");
                Toast.makeText(getApplicationContext(), "No results returned.", Toast.LENGTH_LONG).show();
            } else {
//                output.add(0, "Data retrieved using the Google Calendar API:");
//                mOutputText.setText(TextUtils.join("\n", output));
//                Toast.makeText(getApplicationContext(), "No results returned.", Toast.LENGTH_LONG).show();
                ContentValues values = new ContentValues();
                values.put("CalendarInsert", true);
                getContentResolver().update(CONTENT_URI1, values, "_id = ?", new String[]{ID});
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Meeting_Detail_Activity.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                    Toast.makeText(getApplicationContext(), "The following error occurred:\n" + mLastError.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
//                mOutputText.setText("Request cancelled.");
                Toast.makeText(getApplicationContext(), "Request cancelled.", Toast.LENGTH_LONG).show();
            }
        }
    }


}
