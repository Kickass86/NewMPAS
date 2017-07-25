package turbotec.newmpas;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by ZAMANI on 7/22/2017.
 */

public class MessageProvider extends ContentProvider {


    static final String PROVIDER_NAME = "turbotec.newmpas.MessageProvider.messages";
    static final String URL = "content://" + PROVIDER_NAME + "/Messages";
    static final Uri CONTENT_URI = Uri.parse(URL);


    private static HashMap<String, String> MESSAGES_PROJECTION_MAP;
    static final int Message_ID  = 1;
    static final int Unsent = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "Messages", Message_ID);
        uriMatcher.addURI(PROVIDER_NAME, "Messages/#", Unsent);
    }

    private DatabaseHandler dbHelper;
    // Table Name
    static final String TABLE_NAME = "MESSAGES";

    private static final String MESSAGE_ID = "_id";
    private static final String MESSAGE_Title = "MessageTitle";
    private static final String MESSAGE_BODY = "MessageBody";
    private static final String INSERT_DATE = "InsertDate";
    private static final String Critical = "Critical";
    private static final String Seen = "Seen";
    private static final String SendSeen = "SendSeen";
    private static final String SendDelivered = "SendDelivered";
    // Database Name
    private static final String DATABASE_NAME = "MPAS";
    static final String CREATE_DB_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    MESSAGE_ID + " INTEGER PRIMARY KEY," +
                    MESSAGE_Title + " TEXT," + MESSAGE_BODY + " TEXT," + INSERT_DATE +
                    " TEXT," + Critical + " Boolean," + Seen + " Boolean,"
                    + SendDelivered + " Boolean," + SendSeen + " Boolean)";



    @Override
    public boolean onCreate() {

        Context context = getContext();
        dbHelper = new DatabaseHandler(context);
//        db = dbHelper.getWritableDatabase();
        return true;

    }


//    public Cursor Unsend(@Nullable String[] projection, @Nullable String sortOrder) {
//
//        return dbHelper.getUnsend(projection, sortOrder);
//
//    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {


        String id = null;
        Cursor cursor;
        if(uriMatcher.match(uri) == Message_ID) {
            //Query is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
            cursor = dbHelper.getMessages(id, projection, selection, selectionArgs, sortOrder);
        }
//        if(uriMatcher.match(uri) == Unsent){
        else{
            cursor = dbHelper.getUnsend(projection, sortOrder);
        }
        return cursor;

//        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//        qb.setTables(TABLE_NAME);
//
//        switch (uriMatcher.match(uri)) {
//            case Message_ID:
//                qb.setProjectionMap(MESSAGES_PROJECTION_MAP);
//                break;
//
//            default:
//        }
//
//        if (sortOrder == null || sortOrder == ""){
//            /**
//             * By default sort on student names
//             */
//            sortOrder = INSERT_DATE;
//        }
//
//        Cursor c = qb.query(db,	projection,	selection,
//                selectionArgs,null, null, sortOrder);
//
//        c.setNotificationUri(getContext().getContentResolver(), uri);
//        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case Message_ID:
                return "vnd.android.cursor.dir/vnd.TURBOTEC.NEWMPAS.MESSAGEPROVIDER";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long rowID = dbHelper.addNewMessage(values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {


        String id = null;
        if(uriMatcher.match(uri) == Message_ID) {
            //Update is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
        }

        int count = dbHelper.deleteMessage(id);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {


        String id = null;
        if(uriMatcher.match(uri) == Message_ID) {
            //Update is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
        }

        int count = dbHelper.updateMessage(id, values);


        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    class DatabaseHandler extends SQLiteOpenHelper {


        private static final int DATABASE_VERSION = 1;


        private DatabaseHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            // Create tables again
            onCreate(db);

        }

        public Cursor getMessages(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
            sqliteQueryBuilder.setTables(TABLE_NAME);

//            if(id != null) {
//                sqliteQueryBuilder.appendWhere("_id" + " = " + id);
//            }

            if(sortOrder == null || sortOrder == "") {
                sortOrder = "INSERT_DATE";
            }
            Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            return cursor;
        }


        public Cursor getUnsend(String[] projection, String sortOrder) {
            SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
            sqliteQueryBuilder.setTables(TABLE_NAME);


            sqliteQueryBuilder.appendWhere("SendDelivered  = 0 OR (Seen = 1 AND SendSeen = 0)");


            if(sortOrder == null || sortOrder == "") {
                sortOrder = "INSERT_DATE";
            }
            Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder);
            return cursor;
        }


        public long addNewMessage(ContentValues values) throws SQLException {
            long id = getWritableDatabase().insert(TABLE_NAME, "", values);
            if(id <=0 ) {
                throw new SQLException("Failed to add an image");
            }

            return id;
        }

        public int deleteMessage(String id) {
            if(id == null) {
                return getWritableDatabase().delete(TABLE_NAME, null , null);
            } else {
                return getWritableDatabase().delete(TABLE_NAME, "_id=?", new String[]{id});
            }
        }

        public int updateMessage(String id, ContentValues values) {
            if(id == null) {
                return getWritableDatabase().update(TABLE_NAME, values, null, null);
            } else {
                return getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
            }
        }



    }



}
