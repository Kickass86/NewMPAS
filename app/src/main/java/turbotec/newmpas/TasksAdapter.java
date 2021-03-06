package turbotec.newmpas;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TasksAdapter extends BaseAdapter {
    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/tasks/";
    //    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    //    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    static MainActivity activity;
    //    int num_selected;
    static CheckBox c;
    static List<String> Tlist = new ArrayList<>();
    static List<String> DesList = new ArrayList<>();
    static List<Boolean> isSeen = new ArrayList<>();
    static List<Boolean> EList = new ArrayList<>();
    static List<Boolean> RList = new ArrayList<>();
    static List<Boolean> DelList = new ArrayList<>();
    static List<String> ReList = new ArrayList<>();
    static List<Boolean> iCList = new ArrayList<>();
    static List<Boolean> iRList = new ArrayList<>();
    static List<String> NRList = new ArrayList<>();
    static List<String> CrList = new ArrayList<>();
    static List<Integer> StList = new ArrayList<>();
    static List<String> DateList = new ArrayList<>();
    static Context context;
    private static Cursor cursor;
    private static TasksAdapter instance;
    private static LayoutInflater inflater = null;

    private TasksAdapter(Context act) {
        context = act;
    }

    public static void set(MainActivity mainActivity) {
//        if(activity == null) {
        activity = mainActivity;
//        }

    }

    public static TasksAdapter getInstance() {

        if (instance == null) {
            instance = new TasksAdapter(activity.getApplicationContext());

        }
        Initialize();
        return instance;
    }


    public static TasksAdapter getSearchInstance(String search) {
        if (instance == null) {
            instance = new TasksAdapter(activity.getApplicationContext());
        }
        SearchQuery(search);
        return instance;
    }

    private static void SearchQuery(String search) {
        search = "%" + search + "%";
//        valid = false;

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, "TaskTitle like ? OR TaskDescription like ?", new String[]{search, search}, null);

        Populate();


    }


    public static TasksAdapter Filter(String title, String description, String Creator, String Responsible) {

        instance = new TasksAdapter(context);


        String Select = null;
        String[] SelectArgTemp = new String[4];
        String[] SelectArg;

        if (!title.isEmpty()) {
            title = "%" + title + "%";
            Select = " TaskTitle like ? ";
            if (SelectArgTemp[0] == null) {
                SelectArgTemp[0] = title;
            }
        }

        if (!description.isEmpty()) {
            description = "%" + description + "%";

            if (SelectArgTemp[0] == null) {
                Select = " TaskDescription like ? ";
                SelectArgTemp[0] = description;
            } else {
                Select = Select + " AND TaskDescription like ? ";
                SelectArgTemp[1] = description;
            }
        }


        if (!Responsible.isEmpty()) {


            if (SelectArgTemp[0] == null) {
                Select = " NameResponsible = ? ";
                SelectArgTemp[0] = Responsible;
            } else if (SelectArgTemp[1] == null) {
                Select = Select + " AND NameResponsible = ? ";
                SelectArgTemp[1] = Responsible;
            } else if (SelectArgTemp[2] == null) {
                Select = Select + " AND NameResponsible = ? ";
                SelectArgTemp[2] = Responsible;
            } else if (SelectArgTemp[3] == null) {
                Select = Select + " AND NameResponsible = ? ";
                SelectArgTemp[3] = Responsible;
            }
        }

        if (!Creator.isEmpty()) {


            if (SelectArgTemp[0] == null) {
                Select = " TaskCreator = ? ";
                SelectArgTemp[0] = Creator;
            } else if (SelectArgTemp[1] == null) {
                Select = Select + " AND TaskCreator = ? ";
                SelectArgTemp[1] = Creator;
            } else if (SelectArgTemp[2] == null) {
                Select = Select + " AND TaskCreator = ? ";
                SelectArgTemp[2] = Creator;
            } else if (SelectArgTemp[3] == null) {
                Select = Select + " AND TaskCreator = ? ";
                SelectArgTemp[3] = Creator;
            }
        }


        List<String> list = new ArrayList<>();

        for (String s : SelectArgTemp) {
            if (s != null && s.length() > 0) {
                list.add(s);
            }
        }

        SelectArg = list.toArray(new String[list.size()]);


        cursor = activity.getContentResolver().query(CONTENT_URI1, null, Select, SelectArg, null);

        Populate();

        return instance;
    }


//    public static TasksAdapter getInstance(Context context) {
//        if (instance == null) {
//            instance = new TasksAdapter(context);
//
//        }
//        Initialize();
//        return instance;
//    }

//    public static TasksAdapter getInstance() {
////        Initialize();
//        return instance;
//    }

    static void Initialize() {

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, null, null, null);
        Populate();
    }

    private static void Populate() {

        Tlist = new ArrayList<>();
        DesList = new ArrayList<>();
        isSeen = new ArrayList<>();
        EList = new ArrayList<>();
        RList = new ArrayList<>();
        DelList = new ArrayList<>();
        ReList = new ArrayList<>();
        iCList = new ArrayList<>();
        iRList = new ArrayList<>();
        NRList = new ArrayList<>();
        activity.TIDList = new ArrayList<>();
        CrList = new ArrayList<>();
        StList = new ArrayList<>();
        DateList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

//                        for (int i = 0; i < MESSAGES.size(); i++) {
                    activity.TIDList.add(cursor.getString(0));
                    Tlist.add(cursor.getString(1));
                    DesList.add(cursor.getString(2));
                    DateList.add(cursor.getString(3));
                    CrList.add(cursor.getString(4));
                    StList.add(cursor.getInt(5));
                    ReList.add(cursor.getString(6));
                    isSeen.add("1".equals(cursor.getString(7)));
                    EList.add("1".equals(cursor.getString(9)));
                    RList.add("1".equals(cursor.getString(10)));
                    DelList.add("1".equals(cursor.getString(11)));
                    iCList.add("1".equals(cursor.getString(12)));
                    iRList.add("1".equals(cursor.getString(13)));
                    NRList.add(cursor.getString(15));


//                        }
//                    activity.TaskCheckedState = new boolean[activity.MIDList.size()];
//                    activity.num_selected = 0;
                } while (cursor.moveToNext());
            }
        }
        int count = cursor.getCount();
        cursor.close();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c = (CheckBox) activity.findViewById(R.id.tcheckbox);
        if (MainActivity.TFlag) {
            MainActivity.TaskCheckedState = new boolean[Tlist.size()];
            for (int i = 0; i < activity.TIDList.size(); i++) {
                MainActivity.TaskCheckedState[i] = false;
            }
            MainActivity.TFlag = false;
        }
        if (count != MainActivity.TaskCheckedState.length) {
            MainActivity.TaskCheckedState = new boolean[Tlist.size()];
            for (int i = 0; i < activity.TIDList.size(); i++) {
                MainActivity.TaskCheckedState[i] = false;
            }
            MainActivity.TFlag = false;
        }
    }

    @Override
    public int getCount() {

        return Tlist.size();
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
    public View getView(final int position, View rowView, ViewGroup parent) {

//        ListView list = (ListView) parent.findViewById(R.id.list_task);
//
//        TextView tv = (TextView) parent.findViewById(R.id.empty2);
//        tv.setText("No Tasks");
//        list.setEmptyView(tv);


        Holder holder = new Holder();

//        rowView = inflater.inflate(R.layout.list_row_layout_message, parent, false);
        rowView = inflater.inflate(R.layout.list_row_layout_task, parent, false);

        holder.tv1 = (TextView) rowView.findViewById(R.id.ttitle);
//        holder.tv2 = (TextView) rowView.findViewById(R.id.tdes);
        holder.tv3 = (TextView) rowView.findViewById(R.id.tdate);
        holder.cb = (CheckBox) rowView.findViewById(R.id.tcheckbox);
        holder.i1 = (ImageView) rowView.findViewById(R.id.tstate);
//        holder.tv1 = (TextView) rowView.findViewById(R.id.title);
//        holder.tv2 = (TextView) rowView.findViewById(R.id.body);
//        holder.i1 = (ImageView) rowView.findViewById(R.id.state);
//        holder.cb = (CheckBox) rowView.findViewById(R.id.checkbox1);
//        holder.tv3 = (TextView) rowView.findViewById(R.id.title);


        final Holder htemp = holder;


        holder.tv1.setText(Tlist.get(position));
//        holder.tv2.setText(DesList.get(position));
        holder.tv3.setText(DateList.get(position));

        if (isSeen.get(position))
            holder.i1.setImageResource(R.mipmap.ic_done_all_black_24dp);
        else
            holder.i1.setImageResource(R.mipmap.ic_done_black_24dp);

//        if (CrList.get(position)) {
//            holder.i2.setImageResource(R.mipmap.ic_priority_high_black_24dp);
//        }


        CheckBox.OnCheckedChangeListener checkListener = new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    countCheck(isChecked);
                MainActivity.TaskCheckedState[position] = isChecked;
                int sum = 0;
                for (boolean b : MainActivity.TaskCheckedState) {
                    sum += b ? 1 : 0;
                }

                if ((sum == 0) & (!isChecked)) {
                    htemp.cb.setVisibility(View.GONE);

                    activity.invalidateOptionsMenu();
                    notifyDataSetChanged();
                } else {
                    htemp.cb.setVisibility(View.VISIBLE);

                    activity.invalidateOptionsMenu();
//                    notifyDataSetChanged();
                }


            }
        };
        if (c != null)
            c.setOnCheckedChangeListener(checkListener);
        if (holder.cb != null)
            holder.cb.setOnCheckedChangeListener(checkListener);

        int sum = 0;
        for (boolean b : MainActivity.TaskCheckedState) {
            sum += b ? 1 : 0;
        }
        if (sum > 0) {
            if (holder.cb != null)
                holder.cb.setVisibility(View.VISIBLE);
            activity.invalidateOptionsMenu();

        }
//        else if (activity.isSelected) {
//            if (holder.cb != null)
//                holder.cb.setVisibility(View.GONE);
//            activity.invalidateOptionsMenu();
//
//        }


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean fl = false;
                for (boolean i : MainActivity.TaskCheckedState) {
                    if (i) {
                        fl = true;
                        break;
                    }
                }

                if (fl) {
                    MainActivity.TaskCheckedState[position] = !MainActivity.TaskCheckedState[position];
                    notifyDataSetChanged();
                } else {

                    v.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.SelectColor1));

                    Intent showActivity = new Intent(activity, Task_Detail_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(activity.getString(R.string.Subject), Tlist.get(position));
                    bundle.putString(activity.getString(R.string.TCreator), CrList.get(position));
                    bundle.putString(activity.getString(R.string.DueDate), DateList.get(position));
                    bundle.putInt(activity.getString(R.string.TStatus), StList.get(position));
                    bundle.putString(activity.getString(R.string.TDescription), DesList.get(position));
                    bundle.putBoolean(activity.getString(R.string.TEditable), EList.get(position));
                    bundle.putBoolean(activity.getString(R.string.TReplyAble), RList.get(position));
                    bundle.putBoolean(activity.getString(R.string.TDeletable), DelList.get(position));
                    bundle.putString(activity.getString(R.string.TID), activity.TIDList.get(position));
                    bundle.putString(activity.getString(R.string.TReport), ReList.get(position));
                    bundle.putString(activity.getString(R.string.TNameResponsible), NRList.get(position));
                    bundle.putBoolean(activity.getString(R.string.TisCreator), iCList.get(position));
                    bundle.putBoolean(activity.getString(R.string.TisResponsible), iRList.get(position));
                    showActivity.putExtras(bundle);
                    MainActivity.Gone = true;
                    MainActivity.Scroll_Position = position;
                    showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.i("Task Detail", Tlist.get(position));
//                    finish();
                    activity.startActivity(showActivity);
                    activity.overridePendingTransition(0, 0);
                }


            }
        });


        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            //                LinearLayout l = (LinearLayout) findViewById(R.id.linearLayout1);


//                CheckBox[] cb = new CheckBox[Titles.size()];
//                Holder holder = new Holder();

            //                CheckBox[] cbs = new CheckBox[5];
            @Override
            public boolean onLongClick(View v) {
                CheckBox c = (CheckBox) v.findViewById(R.id.tcheckbox);

                int sum = 0;
                for (boolean b : MainActivity.TaskCheckedState) {
                    sum += b ? 1 : 0;
                }
                if ((sum == 1) & (htemp.cb.isChecked())) {

                    MainActivity.TaskCheckedState[position] = false;
                    activity.invalidateOptionsMenu();

                    notifyDataSetChanged();

                } else if (htemp.cb.isChecked()) {
//                        htemp.cb.setChecked(false);
                    MainActivity.TaskCheckedState[position] = false;
                    notifyDataSetChanged();

                } else {

                    c.setVisibility(View.VISIBLE);
//
                    notifyDataSetChanged();
                    MainActivity.TaskCheckedState[position] = true;
                    htemp.cb.setVisibility(View.VISIBLE);
//                        htemp.cb.setChecked(true);


//                    if (!activity.isSelected) {

                    activity.invalidateOptionsMenu();

//                        activity.isSelected = true;
//                    } else {
//
//                        activity.invalidateOptionsMenu();
//                    }


                }

                return true;
            }
        });
        holder.cb.setChecked(MainActivity.TaskCheckedState[position]);

        return rowView;
    }


    private class Holder {
        TextView tv1;
        //        TextView tv2;
        TextView tv3;
        ImageView i1;
        CheckBox cb;

    }

}