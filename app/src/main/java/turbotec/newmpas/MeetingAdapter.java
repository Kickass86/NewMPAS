package turbotec.newmpas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
    Holder holder;


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

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, "TOPIC like ? OR CREATOR like ? " +
                        "OR ATTENDANCE like ? or SECRETARY like ?"
                , new String[]{search, search, search, search}, null);
        Populate();

    }


    public static MeetingAdapter Filter(String creator, String secretary, String date) {

        instance = new MeetingAdapter(context);


        String Select;
        String[] SelectArg;

        Select = null;
        String[] SelectArgTemp = new String[3];


        if (!creator.isEmpty()) {
//            creator = "%" + creator + "%";
            Select = " CREATOR = ? ";
            if (SelectArgTemp[0] == null) {
                SelectArgTemp[0] = creator;
            }
        }

        if (!secretary.isEmpty()) {
//            date = "%" + date + "%";
            if (SelectArgTemp[0] == null) {
                SelectArgTemp[0] = secretary;
                Select = " SECRETARY = ? ";
            } else if (SelectArgTemp[1] == null) {
                SelectArgTemp[1] = secretary;
                Select = "(" + Select;
                Select = Select + " OR SECRETARY = ? )";
            }
        }

        if (!date.isEmpty()) {
            if (SelectArgTemp[0] == null) {
                Select = " START_DATE >= ?";
                SelectArgTemp[0] = date;
            } else {
                Select = Select + " AND START_DATE >= ?";
                SelectArgTemp[1] = date;
            }
        }

        List<String> list = new ArrayList<>();

        for (String s : SelectArgTemp) {
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
        return TopicList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new Holder();

            convertView = inflater.inflate(R.layout.list_row_layout_meeting, parent, false);

            holder.tv1 = (TextView) convertView.findViewById(R.id.topic);
            holder.tv2 = (TextView) convertView.findViewById(R.id.datetime);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final Holder htemp = holder;

        holder.tv1.setText(TopicList.get(position));
        holder.tv2.setText(PDateList.get(position));


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                v.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.SelectColor1));
                final View vi = v;
                final Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        vi.setBackgroundResource(0);
                    }
                };

                handler.postDelayed(r, 1000);

                Intent showActivity = new Intent(activity, Meeting_Detail_Activity.class);
                Bundle bundle = new Bundle();

                bundle.putString(activity.getString(R.string.MEETING_ID), activity.MTIDList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_TOPIC), TopicList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_CREATOR), CreatorList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_ATTENDANCE), AttendanceList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_SECRETARY), SecretaryList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_SERVICES), ServiceList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_ROOM), RoomList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_EQUIPMENT), EquipmentList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_START_DATE), DateList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_END_DATE), DateList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_PER_DATE), PDateList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_START_TIME), STList.get(position));
                bundle.putString(activity.getString(R.string.MEETING_END_TIME), ETList.get(position));

                showActivity.putExtras(bundle);
                MainActivity.Scroll_Position = position;
                MainActivity.Gone = true;
                showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    finish();
                activity.startActivity(showActivity);
                activity.overridePendingTransition(0, 0);


            }
        });


        return convertView;
    }


    private class Holder {
        TextView tv1;
        TextView tv2;

    }

}
