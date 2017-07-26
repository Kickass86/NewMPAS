package turbotec.newmpas;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

/**
 * Created by ZAMANI on 7/26/2017.
 */
class customadapter extends BaseAdapter {
    final Context context;

    boolean[] mCheckedState;
    MainActivity activity;
    int num_selected;
    CheckBox c;
    List<String> Titles;
    List<String> Bodies;
    List<Boolean> isSeen;
    List<Integer> IList;
    List<Boolean> CList;
    List<Boolean> SSList;
    private boolean isSelected = false;
    private LayoutInflater inflater = null;

    customadapter(MainActivity mainActivity, List<String> MessagesTitle, List<String> MessagesBody, List<Boolean> isSeen, List<Integer> IList, List<Boolean> CList, List<Boolean> SSList) {

        activity = mainActivity;
        this.mCheckedState = new boolean[IList.size()];
        num_selected = 0;
        context = mainActivity;
        Titles = MessagesTitle;
        Bodies = MessagesBody;
        this.isSeen = isSeen;
        this.IList = IList;
        this.CList = CList;
        this.SSList = SSList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c = (CheckBox) activity.findViewById(R.id.checkbox1);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Titles.size();
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

//        private void countCheck(boolean isChecked) {
//
//            num_selected += isChecked ? 1 : -1;
//
//        }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {

        Holder holder = new Holder();
//            final View rowView;
//            if (rowView == null) {
        rowView = inflater.inflate(R.layout.list_row_layout, parent, false);

        holder.tv1 = (TextView) rowView.findViewById(R.id.title);
        holder.tv2 = (TextView) rowView.findViewById(R.id.body);
        holder.cb = (CheckBox) rowView.findViewById(R.id.checkbox1);
        holder.iv = (ImageView) rowView.findViewById(R.id.state);
        holder.i2 = (ImageView) rowView.findViewById(R.id.Critical);
        rowView.setTag(holder);
//            } else {
//                holder = (Holder) rowView.getTag();
//            }
        final Holder htemp = holder;

        holder.tv1.setText(Titles.get(position));
        holder.tv2.setText(Bodies.get(position));

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
                mCheckedState[position] = isChecked;
                int sum = 0;
                for (boolean b : mCheckedState) {
                    sum += b ? 1 : 0;
                }

                if ((sum == 0) & (!isChecked)) {
                    htemp.cb.setVisibility(View.GONE);
//                        mMenu.findItem(0).setVisible(false);
//                        mMenu.findItem(1).setVisible(false);
                    activity.invalidateOptionsMenu();
                    notifyDataSetChanged();
                } else {
                    htemp.cb.setVisibility(View.VISIBLE);
//                        mMenu.findItem(0).setVisible(true);
//                        mMenu.findItem(1).setVisible(true);
                    activity.invalidateOptionsMenu();
                    notifyDataSetChanged();
                }

//                                num_selected
                Log.i("MAIN", num_selected + "");
            }
        };
        if (c != null)
            c.setOnCheckedChangeListener(checkListener);
        if (holder.cb != null)
            holder.cb.setOnCheckedChangeListener(checkListener);

        int sum = 0;
        for (boolean b : mCheckedState) {
            sum += b ? 1 : 0;
        }
        if (sum > 0) {
            if (holder.cb != null)
                holder.cb.setVisibility(View.VISIBLE);
            activity.invalidateOptionsMenu();
//                mMenu.findItem(0).setVisible(true);
//                mMenu.findItem(1).setVisible(true);
        } else if (isSelected) {
            if (holder.cb != null)
                holder.cb.setVisibility(View.GONE);
            activity.invalidateOptionsMenu();
//                mMenu.findItem(0).setVisible(false);
//                mMenu.findItem(1).setVisible(false);
        }


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean fl = false;
                for (boolean i : mCheckedState) {
                    if (i) {
                        fl = true;
                        break;
                    }
                }

                if (fl) {
                    mCheckedState[position] = !mCheckedState[position];
                    notifyDataSetChanged();
                } else {


//                    Toast.makeText(main_menu, "You Clicked " + Titles.get(position), Toast.LENGTH_LONG).show();
//                        rowView.setBackgroundColor(context.getResources().getColor(R.color.SelectColor1));
//                        rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.SelectColor1));
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.SelectColor1));

                    Intent showActivity = new Intent(activity, Message_Detail_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(activity.getString(R.string.Title), Titles.get(position));
                    bundle.putString(activity.getString(R.string.Body), Bodies.get(position));
                    bundle.putBoolean(activity.getString(R.string.Critical), CList.get(position));
                    bundle.putBoolean(activity.getString(R.string.SendSeen), SSList.get(position));
                    bundle.putInt(activity.getString(R.string.ID), IList.get(position));
                    bundle.putBoolean(activity.getString(R.string.Seen), isSeen.get(position));
                    showActivity.putExtras(bundle);
//                    activity.Scroll_Position = position;
                    showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    finish();
                    activity.startActivity(showActivity);
                }


            }
        });
//            CheckBox c = (CheckBox) findViewById(R.id.checkbox1);


        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            //                LinearLayout l = (LinearLayout) findViewById(R.id.linearLayout1);


//                CheckBox[] cb = new CheckBox[Titles.size()];
//                Holder holder = new Holder();

            //                CheckBox[] cbs = new CheckBox[5];
            @Override
            public boolean onLongClick(View v) {
                CheckBox c = (CheckBox) activity.findViewById(R.id.checkbox1);

                int sum = 0;
                for (boolean b : mCheckedState) {
                    sum += b ? 1 : 0;
                }
                if ((sum == 1) & (htemp.cb.isChecked())) {
//                        for (int i = 0; i < Titles.size(); i++) {
//                            l.removeViewInLayout(cb[i]);
//                        }
//                        l.removeView(c);
//                        htemp.cb.setChecked(false);
//                        c.setVisibility(View.GONE);
//
//                        c.setOnCheckedChangeListener(checkListener);
//                        htemp.cb.setOnCheckedChangeListener(checkListener);
                    htemp.cb.setVisibility(View.GONE);


                    mCheckedState[position] = false;
                    activity.invalidateOptionsMenu();
//                        mMenu.findItem(0).setVisible(false);
//                        mMenu.findItem(1).setVisible(false);
//                        num_selected--;
//                        htemp.cb.invalidate();
//                        htemp.cb.requestLayout();
//                        htemp.cb.invalidate();
//                        htemp.cb.requestLayout();
                    notifyDataSetChanged();
//                        isSelected = false;
//                        notifyDataSetChanged();
                } else if (htemp.cb.isChecked()) {
//                        htemp.cb.setChecked(false);
                    mCheckedState[position] = false;
                    notifyDataSetChanged();
//                        CheckBox.OnCheckedChangeListener checkListener = new CheckBox.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                countCheck(isChecked);
//                                mCheckedState[position] = isChecked;
//                                if ((num_selected == 0) & (!isChecked)) {
//                                    htemp.cb.setVisibility(View.GONE);
//                                    mMenu.findItem(Menu.FIRST).setVisible(false);
//                                    mMenu.findItem(Menu.FIRST + 1).setVisible(false);
//                                    htemp.cb.invalidate();
//                                    htemp.cb.requestLayout();
//                                    notifyDataSetChanged();
//                                }
//
////                                num_selected
//                                Log.i("MAIN", num_selected + "");
//                            }
//                        };
//                        c.setOnCheckedChangeListener(checkListener);
//                        htemp.cb.setOnCheckedChangeListener(checkListener);
//                        htemp.cb.setVisibility(View.GONE);
//                        num_selected--;
                } else {

                    c.setVisibility(View.VISIBLE);
//                        CheckBox.OnCheckedChangeListener checkListener = new CheckBox.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                countCheck(isChecked);
//                                mCheckedState[position] = isChecked;
//                                if ((num_selected == 0) & (!isChecked)) {
//                                    htemp.cb.setVisibility(View.GONE);
//                                    mMenu.findItem(Menu.FIRST).setVisible(false);
//                                    mMenu.findItem(Menu.FIRST + 1).setVisible(false);
//                                    htemp.cb.invalidate();
//                                    htemp.cb.requestLayout();
//                                    notifyDataSetChanged();
//                                }
//
////                                num_selected
//                                Log.i("MAIN", num_selected + "");
//                            }
//                        };
//                        c.setOnCheckedChangeListener(checkListener);
//                        htemp.cb.setOnCheckedChangeListener(checkListener);

//                        htemp.cb.invalidate();
//                        htemp.cb.requestLayout();
                    notifyDataSetChanged();
                    mCheckedState[position] = true;
                    htemp.cb.setVisibility(View.VISIBLE);
//                        htemp.cb.setChecked(true);


                    if (!isSelected) {
//                            mMenu.clear();
//                            MenuItem item1 = mMenu.add(Menu.NONE, Menu.FIRST, 10, R.string.menu_delete);
//                            item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//                            item1.setIcon(R.mipmap.ic_delete_black_24dp);
//
//                            MenuItem item2 = mMenu.add(Menu.NONE, Menu.FIRST + 1, 10, R.string.menu_read);
//                            item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//                            item2.setIcon(R.mipmap.ic_done_all_black_24dp);

//                            mMenu.findItem(0).setVisible(false);
//                            mMenu.findItem(1).setVisible(false);
                        activity.invalidateOptionsMenu();

                        isSelected = true;
                    } else {
//                            mMenu.findItem(0).setVisible(true);
//                            mMenu.findItem(1).setVisible(true);
                        activity.invalidateOptionsMenu();
                    }


//                            l.invalidate();
//                            l.requestLayout();


                }

                return true;
            }
        });
        holder.cb.setChecked(mCheckedState[position]);

        return rowView;
    }

//        @Override
//        public void notifyDataSetChanged() {
//            super.notifyDataSetChanged();
//        }

    private class Holder {
        TextView tv1;
        TextView tv2;
        ImageView iv;
        ImageView i2;
        CheckBox cb;

    }

}
