package turbotec.newmpas;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.Calendar;

public class InstallActivity extends AppCompatActivity {


    static final String PROVIDER_NAME = "TURBOTEC.NEWMPAS.MESSAGEPROVIDER";
    static final String URL = "content://" + PROVIDER_NAME + "/messages";
    static final Uri CONTENT_URI = Uri.parse(URL);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);


        Bundle b = getIntent().getExtras();
        try {
            if (b != null) {
                String MyFileAddress = b.getString(getString(R.string.MyFile));
                File MyFile = new File(MyFileAddress);
                if ((MyFileAddress != null) & (MyFile.exists())) {
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(Uri.fromFile(MyFile),
                            "application/vnd.android.package-archive");
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(installIntent);
                    finish();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            Calendar c = Calendar.getInstance();



            ContentValues contentValues = new ContentValues();
            contentValues.put("MessageID", 100000);
            contentValues.put("MessageTitle" , "Internal Error");
            contentValues.put("MessageBody", e.getMessage());
            contentValues.put("InsertDate", c.getTime().toString());
            contentValues.put("Critical", true);
            getContentResolver().insert(CONTENT_URI, contentValues);
//            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
//            refreshValuesFromContentProvider();

        }

    }

//    private void refreshValuesFromContentProvider() {
//        CursorLoader cursorLoader = new CursorLoader(getBaseContext(), CONTENT_URI,
//                null, null, null, null);
//        Cursor c = cursorLoader.loadInBackground();
//        adapter.swapCursor(c);
//    }


}
