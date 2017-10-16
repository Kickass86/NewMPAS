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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MessagesListAdapter extends BaseAdapter {
    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    //    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/unsent/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    public static boolean valid;
    static Cursor cursor;
    //    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    static MainActivity activity;
    static CheckBox c;
    static boolean isSearch;
    static List<String> Tlist = new ArrayList<>();
    static List<String> Mlist = new ArrayList<>();
    static List<String> Llist = new ArrayList<>();
    static List<Boolean> isSeen = new ArrayList<>();
//    static List<Integer> IList = new ArrayList<>();
static List<Boolean> CList = new ArrayList<>();
    static List<Boolean> SSList = new ArrayList<>();
    static Context context;
    private static MessagesListAdapter instance;
    private static LayoutInflater inflater = null;
    //    int num_selected;
    Holder holder;

    private MessagesListAdapter(Context act) {
        context = act;
    }

    public static void set(MainActivity mainActivity) {
//        if(activity == null) {
        activity = mainActivity;
//        }

    }

    public static MessagesListAdapter getSearchInstance(Context context, String search) {
        if (instance == null) {
            instance = new MessagesListAdapter(context);
        }
        SearchQuery(search);
        return instance;
    }

    private static void SearchQuery(String search) {
        search = "%" + search + "%";
        valid = false;

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, "WillDeleted = ? AND (MessageTitle like ? OR MessageBody like ?)", new String[]{"0", search, search}, null);

        Populate();

    }


    public static MessagesListAdapter Filter(String title, String body) {

        instance = new MessagesListAdapter(context);


        String Select = " WillDeleted = ? ";
        String[] SelectArgtemp = new String[3];
        String[] SelectArg;
        SelectArgtemp[0] = "0";

        if (!title.isEmpty()) {
            title = "%" + title + "%";
            Select = Select + " AND MessageTitle like ? ";
            if (SelectArgtemp[1] == null) {
                SelectArgtemp[1] = title;
            }
        }

        if (!body.isEmpty()) {
            body = "%" + body + "%";

            if (SelectArgtemp[1] == null) {
                Select = Select + " AND MessageBody like ? ";
                SelectArgtemp[1] = body;
            } else {
                Select = Select + " AND MessageBody like ? ";
                SelectArgtemp[2] = body;
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


    public static MessagesListAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new MessagesListAdapter(context);

        }
        Initialize();
        return instance;
    }

    public static MessagesListAdapter getInstance() {
//        Initialize();
        return instance;
    }

    static void Initialize() {
        valid = true;

        cursor = activity.getContentResolver().query(CONTENT_URI1, null, "WillDeleted = ?", new String[]{"0"}, null);
        Populate();
    }

    private static void Populate() {

        Tlist = new ArrayList<>();
        Mlist = new ArrayList<>();
        Llist = new ArrayList<>();
        isSeen = new ArrayList<>();
        activity.IList = new ArrayList<>();
        CList = new ArrayList<>();
        SSList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

//                        for (int i = 0; i < MESSAGES.size(); i++) {
                    activity.IList.add(Integer.valueOf(cursor.getString(0)));
                    Tlist.add(cursor.getString(1));
                    Mlist.add(cursor.getString(2));
                    CList.add("1".equals(cursor.getString(4)));
                    isSeen.add("1".equals(cursor.getString(5)));
                    SSList.add("1".equals(cursor.getString(7)));
                    Llist.add(cursor.getString(9));
//                        }
//                    activity.MessaCheckedState = new boolean[activity.IList.size()];
//                    activity.num_selected = 0;
                } while (cursor.moveToNext());
            }
        }
        int count = cursor.getCount();
        cursor.close();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c = (CheckBox) activity.findViewById(R.id.checkbox1);
        if (MainActivity.NFlag) {
            MainActivity.MessaCheckedState = new boolean[activity.IList.size()];
            for (int i = 0; i < activity.IList.size(); i++) {
                MainActivity.MessaCheckedState[i] = false;
            }
            MainActivity.NFlag = false;
        }
        if (count != MainActivity.MessaCheckedState.length) {
            MainActivity.MessaCheckedState = new boolean[activity.IList.size()];
            for (int i = 0; i < activity.IList.size(); i++) {
                MainActivity.MessaCheckedState[i] = false;
            }
            MainActivity.NFlag = false;
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

        if (rowView == null) {
            holder = new Holder();

            rowView = inflater.inflate(R.layout.list_row_layout_message, parent, false);

            holder.tv1 = (TextView) rowView.findViewById(R.id.title);
            holder.tv2 = (TextView) rowView.findViewById(R.id.body);
            holder.cb = (CheckBox) rowView.findViewById(R.id.checkbox1);
            holder.iv = (ImageView) rowView.findViewById(R.id.state);
            holder.i2 = (ImageView) rowView.findViewById(R.id.Critical);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        final Holder htemp = holder;

        holder.tv1.setText(Tlist.get(position));
        holder.tv2.setText(Mlist.get(position));


        if (isSeen.get(position))
            holder.iv.setImageResource(R.mipmap.ic_done_all_black_24dp);
        else
            holder.iv.setImageResource(R.mipmap.ic_done_black_24dp);

        if (CList.get(position)) {
            holder.i2.setImageResource(R.mipmap.ic_priority_high_black_24dp);
        }


        CheckBox.OnCheckedChangeListener checkListener = new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    countCheck(isChecked);
                MainActivity.MessaCheckedState[position] = isChecked;
                int sum = 0;
                for (boolean b : MainActivity.MessaCheckedState) {
                    sum += b ? 1 : 0;
                }

                if (sum == 0) {
                    htemp.cb.setVisibility(View.GONE);

                    activity.invalidateOptionsMenu();
                    notifyDataSetChanged();
                } else {
                    htemp.cb.setVisibility(View.VISIBLE);
                }
                    activity.invalidateOptionsMenu();
//                    notifyDataSetChanged();
//                }


            }
        };
//        if (c != null)
//            c.setOnCheckedChangeListener(checkListener);
//        if (holder.cb != null)
            holder.cb.setOnCheckedChangeListener(checkListener);

        int sum = 0;
        for (boolean b : MainActivity.MessaCheckedState) {
            sum += b ? 1 : 0;
        }
        if (sum > 0) {
            if (holder.cb != null)
                holder.cb.setVisibility(View.VISIBLE);

        } else if (sum == 0) {
            if (holder.cb != null)
                holder.cb.setVisibility(View.GONE);
            notifyDataSetChanged();
        }
        if (valid) {
            activity.invalidateOptionsMenu();
        }


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean fl = false;
                for (boolean i : MainActivity.MessaCheckedState) {
                    if (i) {
                        fl = true;
                        break;
                    }
                }

                if (fl) {
                    MainActivity.MessaCheckedState[position] = !MainActivity.MessaCheckedState[position];

                    notifyDataSetChanged();
                    activity.invalidateOptionsMenu();


                } else {

                    v.setBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.SelectColor1));
                    final View vi = v;
                    final Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            vi.setBackgroundResource(0);
                        }
                    };

                    handler.postDelayed(r, 1000);

                    Intent showActivity = new Intent(activity, Message_Detail_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(activity.getString(R.string.Title), Tlist.get(position));
                    bundle.putString(activity.getString(R.string.Body), Mlist.get(position));
                    bundle.putString(activity.getString(R.string.Link), Llist.get(position));
                    bundle.putBoolean(activity.getString(R.string.Critical), CList.get(position));
                    bundle.putBoolean(activity.getString(R.string.SendSeen), SSList.get(position));
                    bundle.putInt(activity.getString(R.string.ID), activity.IList.get(position));
                    bundle.putBoolean(activity.getString(R.string.Seen), isSeen.get(position));
                    showActivity.putExtras(bundle);
                    MainActivity.Scroll_Position = position;
                    MainActivity.Gone = true;
                    showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                CheckBox c = (CheckBox) v.findViewById(R.id.checkbox1);

                int sum = 0;
                for (boolean b : MainActivity.MessaCheckedState) {
                    sum += b ? 1 : 0;
                }
                if ((sum == 1) & (htemp.cb.isChecked())) {

                    MainActivity.MessaCheckedState[position] = false;
                    activity.invalidateOptionsMenu();

                    notifyDataSetChanged();

                } else if (htemp.cb.isChecked()) {
//                        htemp.cb.setChecked(false);
                    MainActivity.MessaCheckedState[position] = false;
//                    notifyDataSetChanged();

                } else {

                    c.setVisibility(View.VISIBLE);
//
                    notifyDataSetChanged();
                    MainActivity.MessaCheckedState[position] = true;
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
        holder.cb.setChecked(MainActivity.MessaCheckedState[position]);

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