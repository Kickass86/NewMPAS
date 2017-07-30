package turbotec.newmpas;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {
    static final String PROVIDER_NAME = "turbotec.newmpas.MessageProvider.messages";
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/1";
    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    static MainActivity activity;
    //    int num_selected;
    static CheckBox c;
    //    static List<String> Tlist = new ArrayList<>();
//    static List<String> Bodies = new ArrayList<>();
//    static List<Boolean> isSeen = new ArrayList<>();
//    static List<Integer> IList = new ArrayList<>();
//    static List<Boolean> CList = new ArrayList<>();
//    static List<Boolean> SSList = new ArrayList<>();
    private static CustomAdapter instance;
    private static LayoutInflater inflater = null;
    Context context;

    private CustomAdapter(Context act) {
        context = act;
    }

    public static void set(MainActivity mainActivity) {
        activity = mainActivity;
    }

    public static CustomAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new CustomAdapter(context);

        }
        Initialize();
        return instance;
    }

    public static CustomAdapter getInstance() {
//        Initialize();
        return instance;
    }

    static void Initialize() {

        Cursor cursor = activity.getContentResolver().query(CONTENT_URI1, null, null, null, null);

        activity.Tlist = new ArrayList<>();
        activity.Mlist = new ArrayList<>();
        activity.SList = new ArrayList<>();
        activity.IList = new ArrayList<>();
        activity.CList = new ArrayList<>();
        activity.SSList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

//                        for (int i = 0; i < MESSAGES.size(); i++) {
                    activity.IList.add(Integer.valueOf(cursor.getString(0)));
                    activity.Tlist.add(cursor.getString(1));
                    activity.Mlist.add(cursor.getString(2));
                    activity.CList.add("1".equals(cursor.getString(4)));
                    activity.SList.add("1".equals(cursor.getString(5)));
                    activity.SSList.add("1".equals(cursor.getString(7)));
//                        }
//                    activity.mCheckedState = new boolean[activity.IList.size()];
//                    activity.num_selected = 0;
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c = (CheckBox) activity.findViewById(R.id.checkbox1);
        if (MainActivity.Flag) {
            MainActivity.mCheckedState = new boolean[activity.IList.size()];
            for (int i = 0; i < activity.IList.size(); i++) {
                MainActivity.mCheckedState[i] = false;
            }
            MainActivity.Flag = false;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return activity.Tlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {

//        ListView list = (ListView) parent.findViewById(R.id.list_task);
//
//        TextView tv = (TextView) parent.findViewById(R.id.empty2);
//        tv.setText("No Tasks");
//        list.setEmptyView(tv);


        final Holder holder = new Holder();

        rowView = inflater.inflate(R.layout.list_row_layout, parent, false);

        holder.tv1 = (TextView) rowView.findViewById(R.id.title);
        holder.tv2 = (TextView) rowView.findViewById(R.id.body);
        holder.cb = (CheckBox) rowView.findViewById(R.id.checkbox1);
        holder.iv = (ImageView) rowView.findViewById(R.id.state);
        holder.i2 = (ImageView) rowView.findViewById(R.id.Critical);
//        rowView.setTag(holder);

//        final Holder htemp = holder;

        holder.tv1.setText(activity.Tlist.get(position));
        holder.tv2.setText(activity.Mlist.get(position));

        if (activity.SList.get(position))
            holder.iv.setImageResource(R.mipmap.ic_done_all_black_24dp);
        else
            holder.iv.setImageResource(R.mipmap.ic_done_black_24dp);

        if (activity.CList.get(position)) {
            holder.i2.setImageResource(R.mipmap.ic_priority_high_black_24dp);
        }


        CheckBox.OnCheckedChangeListener checkListener = new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    countCheck(isChecked);
                MainActivity.mCheckedState[position] = isChecked;
                int sum = 0;
                for (boolean b : MainActivity.mCheckedState) {
                    sum += b ? 1 : 0;
                }

                if ((sum == 0) & (!isChecked)) {
                    holder.cb.setVisibility(View.GONE);

                    activity.invalidateOptionsMenu();
                    notifyDataSetChanged();
                } else {
                    holder.cb.setVisibility(View.VISIBLE);

                    activity.invalidateOptionsMenu();
                    notifyDataSetChanged();
                }


            }
        };
        if (c != null)
            c.setOnCheckedChangeListener(checkListener);
        if (holder.cb != null)
            holder.cb.setOnCheckedChangeListener(checkListener);

        int sum = 0;
        for (boolean b : MainActivity.mCheckedState) {
            sum += b ? 1 : 0;
        }
        if (sum > 0) {
            if (holder.cb != null)
                holder.cb.setVisibility(View.VISIBLE);
            activity.invalidateOptionsMenu();

        } else if (activity.isSelected) {
            if (holder.cb != null)
                holder.cb.setVisibility(View.GONE);
            activity.invalidateOptionsMenu();

        }


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean fl = false;
                for (boolean i : MainActivity.mCheckedState) {
                    if (i) {
                        fl = true;
                        break;
                    }
                }

                if (fl) {
                    MainActivity.mCheckedState[position] = !MainActivity.mCheckedState[position];
                    notifyDataSetChanged();
                } else {

                    v.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.SelectColor1));

                    Intent showActivity = new Intent(activity, Message_Detail_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(activity.getString(R.string.Title), activity.Tlist.get(position));
                    bundle.putString(activity.getString(R.string.Body), activity.Mlist.get(position));
                    bundle.putBoolean(activity.getString(R.string.Critical), activity.CList.get(position));
                    bundle.putBoolean(activity.getString(R.string.SendSeen), activity.SSList.get(position));
                    bundle.putInt(activity.getString(R.string.ID), activity.IList.get(position));
                    bundle.putBoolean(activity.getString(R.string.Seen), activity.SList.get(position));
                    showActivity.putExtras(bundle);
                    MainActivity.Scroll_Position = position;
                    showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    finish();
                    activity.startActivity(showActivity);
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
                CheckBox c = (CheckBox) v.findViewById(R.id.checkbox1);

                int sum = 0;
                for (boolean b : MainActivity.mCheckedState) {
                    sum += b ? 1 : 0;
                }
                if ((sum == 1) & (holder.cb.isChecked())) {

                    MainActivity.mCheckedState[position] = false;
                    activity.invalidateOptionsMenu();

                    notifyDataSetChanged();

                } else if (holder.cb.isChecked()) {
//                        htemp.cb.setChecked(false);
                    MainActivity.mCheckedState[position] = false;
                    notifyDataSetChanged();

                } else {

                    c.setVisibility(View.VISIBLE);
//
                    notifyDataSetChanged();
                    MainActivity.mCheckedState[position] = true;
                    holder.cb.setVisibility(View.VISIBLE);
//                        htemp.cb.setChecked(true);


                    if (!activity.isSelected) {

                        activity.invalidateOptionsMenu();

                        activity.isSelected = true;
                    } else {

                        activity.invalidateOptionsMenu();
                    }


                }

                return true;
            }
        });
        holder.cb.setChecked(MainActivity.mCheckedState[position]);

        return rowView;
    }


    private class Holder {
        TextView tv1;
        TextView tv2;
        ImageView iv;
        ImageView i2;
        CheckBox cb;

    }

}