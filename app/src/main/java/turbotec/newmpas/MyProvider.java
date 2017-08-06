package turbotec.newmpas;

import android.content.ContentProvider;
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

/**
 * Created by ZAMANI on 7/22/2017.
 */

public class MyProvider extends ContentProvider {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    static final String URL2 = "content://" + PROVIDER_NAME + "/tasks/";
    static final Uri CONTENT_URI = Uri.parse(URL1);
    static final int Message_ID  = 1;
    static final int Unsent = 2;
    static final int Tasks = 3;
    static final UriMatcher uriMatcher;
    // Table Name
    static final String TABLE_MESSAGES = "MESSAGES";
    static final String TABLE_TASKS = "MESSAGES";
    private static final String MESSAGE_ID = "_id";
    private static final String MESSAGE_Title = "MessageTitle";
    private static final String MESSAGE_BODY = "MessageBody";
    private static final String INSERT_DATE = "InsertDate";
    private static final String Critical = "Critical";
    private static final String Seen = "Seen";
    private static final String SendSeen = "SendSeen";
    private static final String SendDelivered = "SendDelivered";
    static final String CREATE_MESSAGES_TABLE =
            "CREATE TABLE " + TABLE_MESSAGES + "(" +
                    MESSAGE_ID + " INTEGER PRIMARY KEY," +
                    MESSAGE_Title + " TEXT," + MESSAGE_BODY + " TEXT," + INSERT_DATE +
                    " TEXT," + Critical + " Boolean," + Seen + " Boolean,"
                    + SendDelivered + " Boolean," + SendSeen + " Boolean)";
    private static final String TASK_ID = "_id";
    private static final String TASK_Title = "MessageTitle";
    private static final String Task_Description = "MessageBody";
    private static final String TASK_DueDate = "InsertDate";
    private static final String TASK_Creator = "Critical";
    private static final String TASK_Status = "Seen";
    static final String CREATE_TASKS_TABLE =
            "CREATE TABLE " + TABLE_TASKS + "(" +
                    TASK_ID + " TEXT PRIMARY KEY," +
                    TASK_Title + " TEXT," + Task_Description + " TEXT," + TASK_DueDate +
                    " TEXT," + TASK_Creator + " TEXT," + TASK_Status + " TEXT)";

    private static final String DATABASE_NAME = "MPAS";
//    private static HashMap<String, String> MESSAGES_PROJECTION_MAP;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "/messages/#", Message_ID);
        uriMatcher.addURI(PROVIDER_NAME, "/messages/unsent/", Unsent);
        uriMatcher.addURI(PROVIDER_NAME, "/tasks/", Tasks);
    }

    private DatabaseHandler dbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        dbHelper = new DatabaseHandler(context);

        return true;

    }




    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {


        String id;
        Cursor cursor;
        if(uriMatcher.match(uri) == Message_ID) {
            //Query is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
            cursor = dbHelper.getMessages(id, projection, selection, selectionArgs, sortOrder);
        } else if (uriMatcher.match(uri) == Unsent) {
            cursor = dbHelper.getUnsend(projection, selection, selectionArgs, sortOrder);
        } else if (uriMatcher.match(uri) == Tasks) {
            id = uri.getPathSegments().get(1);
            cursor = dbHelper.getTasks(id, projection, selection, selectionArgs, sortOrder);
        } else {
            cursor = dbHelper.getUnsend(projection, selection, selectionArgs, sortOrder);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
//        getContext().getContentResolver().notifyChange(uri, null);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

//    @Nullable
//    @Override
//    public String getType(@NonNull Uri uri) {
//        switch (uriMatcher.match(uri)){
//            /**
//             * Get all student records
//             */
//            case Message_ID:
//                return "vnd.android.cursor.dir/vnd.TURBOTEC."
//
//            default:
//                throw new IllegalArgumentException("Unsupported URI: " + uri);
//        }
//    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {


        if (uriMatcher.match(uri) == Message_ID) {

            long rowID = dbHelper.addNewMessage(values);
            if (rowID > 0) {
//            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
//            getContext().getContentResolver().notifyChange(_uri, null);
                return uri;
            }
        }
        if (uriMatcher.match(uri) == Tasks) {
            long rowID = dbHelper.addNewTask(values);
            if (rowID > 0) {
//            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
//            getContext().getContentResolver().notifyChange(_uri, null);
                return uri;
            }
        }
        throw new SQLException("Failed to add a record into " + uri);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {


        String id = null;
//        if(uriMatcher.match(uri) == Message_ID) {

            id = uri.getPathSegments().get(1);
//        }
//        if(uriMatcher.match(uri) == Tasks) {
//
//            id = uri.getPathSegments().get(1);
//        }

        int count = 0;
        if (uriMatcher.match(uri) == Message_ID) {
            count = dbHelper.deleteMessage(id, selection, selectionArgs);
        } else if (uriMatcher.match(uri) == Message_ID) {
            count = dbHelper.deleteTask(id, selection, selectionArgs);
        }


//        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {


        String id = null;
        if(uriMatcher.match(uri) == Message_ID) {
            //Update is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
        }
        //TODO Update Multiple messages

        int count = dbHelper.updateMessage(id, values);


//        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    class DatabaseHandler extends SQLiteOpenHelper {


        private static final int DATABASE_VERSION = 1;


        private DatabaseHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public synchronized void close() {
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TASKS_TABLE);
            db.execSQL(CREATE_MESSAGES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

            // Create tables again
            onCreate(db);

        }


        public Cursor getMessages(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
            sqliteQueryBuilder.setTables(TABLE_MESSAGES);

            if(sortOrder == null || sortOrder == "") {
                sortOrder = INSERT_DATE + " DESC";
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


        public Cursor getTasks(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
            sqliteQueryBuilder.setTables(TABLE_TASKS);

            if (sortOrder == null || sortOrder == "") {
                sortOrder = TASK_DueDate + " DESC";
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



        public Cursor getUnsend(String[] projection, String selection, String[] selectionArg, String sortOrder) {
            SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
            sqliteQueryBuilder.setTables(TABLE_MESSAGES);

            if(sortOrder == null || sortOrder == "") {
                sortOrder = INSERT_DATE + " DESC";
            }
            Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                    projection,
                    selection,
                    selectionArg,
                    null,
                    null,
                    sortOrder);
            return cursor;
        }


        public long addNewMessage(ContentValues values) throws SQLException {
            long id = getWritableDatabase().insert(TABLE_MESSAGES, "", values);
            if(id <=0 ) {
                throw new SQLException("Failed to add an image");
            }

            return id;
        }


        public long addNewTask(ContentValues values) throws SQLException {
            long id = getWritableDatabase().insert(TABLE_TASKS, "", values);
            if (id <= 0) {
                throw new SQLException("Failed to add an image");
            }

            return id;
        }

        public int deleteMessage(String id, String selection, String[] selectionArg) {
            if(id == null) {
                return getWritableDatabase().delete(TABLE_MESSAGES, selection, selectionArg);
            } else {
                return getWritableDatabase().delete(TABLE_MESSAGES, "_id=?", new String[]{id});
            }
        }


        public int deleteTask(String id, String selection, String[] selectionArg) {
            if (id == null) {
                return getWritableDatabase().delete(TABLE_TASKS, selection, selectionArg);
            } else {
                return getWritableDatabase().delete(TABLE_TASKS, "_id=?", new String[]{id});
            }
        }


        public int updateMessage(String id, ContentValues values) {
            if(id == null) {
                return getWritableDatabase().update(TABLE_MESSAGES, values, null, null);
            } else {
                return getWritableDatabase().update(TABLE_MESSAGES, values, "_id=?", new String[]{id});
            }
        }



    }



}