package turbotec.newmpas;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZAMANI on 10/21/2017.
 */


public class MeetingAdapter extends BaseAdapter {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/meetings/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static Context context;
    static boolean valid;
    static Cursor cursor;
    static MainActivity activity;
    //    static List<String> IDList = new ArrayList<>();
    static List<String> TopicList = new ArrayList<>();
    static List<String> RoomList = new ArrayList<>();
    static List<String> STList = new ArrayList<>();
    static List<String> ETList = new ArrayList<>();
    static List<String> DateList = new ArrayList<>();
    static List<String> PDateList = new ArrayList<>();
    static List<String> AttendanceList = new ArrayList<>();
    static List<String> SecretaryList = new ArrayList<>();
    static List<String> CreatorList = new ArrayList<>();
    static List<String> ServiceList = new ArrayList<>();
    static List<String> EquipmentList = new ArrayList<>();
    private static MeetingAdapter instance;
    private static LayoutInflater inflater = null;


    private MeetingAdapter(Context act) {
        context = act;
    }

    public static void set(MainActivity mainActivity) {

        activity = mainActivity;

    }

    public static MeetingAdapter getInstance() {
        if (instance == null) {
            instance = new MeetingAdapter(activity.getApplicationContext());

        }
        Initialize();
        return instance;
    }

//    public static MeetingAdapter getInstance() {
//        return instance;
//    }


    public static MeetingAdapter getSearchInstance(String search) {
        if (instance == null) {
            instance = new MeetingAdapter(activity.getApplicationContext());
        }
        SearchQuery(search);
        return instance;
    }

    static void Initialize() {
        valid = true;

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, null, null, null);
        Populate();
    }


    private static void SearchQuery(String search) {
        search = "%" + search + "%";
        valid = false;

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, "MEETING_TOPIC like ? OR MEETING_CREATOR like ? OR MEETING_ATTENDANCE like ?"
                + " OR MEETING_SECRETARY like ? ", new String[]{search, search, search, search}, null);
        Populate();

    }


    public static MeetingAdapter Filter(String topic, String date) {

        instance = new MeetingAdapter(context);


        String Select;
        String[] SelectArgtemp = new String[3];
        String[] SelectArg;
        SelectArgtemp[0] = "0";

        Select = null;
        String[] SelectArgTemp = new String[4];


        if (!topic.isEmpty()) {
            topic = "%" + topic + "%";
            Select = " MEETING_TOPIC like ? ";
            if (SelectArgTemp[0] == null) {
                SelectArgTemp[0] = topic;
            }
        }

        if (!date.isEmpty()) {
//            date = "%" + date + "%";

            if (SelectArgTemp[0] == null) {
                Select = " MEETING_START_DATE >= ";
                SelectArgTemp[0] = date;
            } else {
                Select = Select + " AND MEETING_START_DATE >= ";
                SelectArgTemp[1] = date;
            }
        }

        List<String> list = new ArrayList<>();

        for (String s : SelectArgtemp) {
            if (s != null && s.length() > 0) {
                list.add(s);
            }
        }

        SelectArg = list.toArray(new String[list.size()]);


        valid = false;

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, Select, SelectArg, null);

        Populate();

        return instance;

    }

    private static void Populate() {

        activity.MTIDList = new ArrayList<>();
        TopicList = new ArrayList<>();
        RoomList = new ArrayList<>();
        STList = new ArrayList<>();
        ETList = new ArrayList<>();
        DateList = new ArrayList<>();
        PDateList = new ArrayList<>();
        AttendanceList = new ArrayList<>();
        SecretaryList = new ArrayList<>();
        CreatorList = new ArrayList<>();
        ServiceList = new ArrayList<>();
        EquipmentList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {


                    activity.MTIDList.add(cursor.getString(0));
                    TopicList.add(cursor.getString(1));
                    CreatorList.add(cursor.getString(2));
                    AttendanceList.add(cursor.getString(3));
                    SecretaryList.add(cursor.getString(4));
                    ServiceList.add(cursor.getString(5));
                    RoomList.add(cursor.getString(6));
                    EquipmentList.add(cursor.getString(7));
                    DateList.add(cursor.getString(8));
                    STList.add(cursor.getString(10));
                    ETList.add(cursor.getString(11));
                    PDateList.add(cursor.getString(12));


                } while (cursor.moveToNext());
            }
        }
        int count = cursor.getCount();
        cursor.close();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


}
