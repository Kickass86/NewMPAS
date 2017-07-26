package turbotec.newmpas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {


    static final String PROVIDER_NAME = "TURBOTEC.NEWMPAS.MESSAGEPROVIDER";
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages";
    static final String URL2 = "content://" + PROVIDER_NAME + "/messages/1";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;
    private int Scroll_Position = 0;
    private boolean[] mCheckedState;
    private Menu mMenu;
    private boolean isSelected = false;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCheckedState = new boolean[0];


        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, NotificationFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, TaskFragment.class.getName()));

        this.mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item_read = menu.findItem(R.id.menu_read);
        MenuItem item_delete = menu.findItem(R.id.menu_delete);
        MenuItem item_select = menu.findItem(R.id.menu_select_all);
//        main_menu.findItem(R.id.menu_read).setVisible(false);
//        main_menu.findItem(R.id.menu_delete).setVisible(false);
//        mMenu = menu;
        int sum = 0;
        for (boolean b : mCheckedState) {
            sum += b ? 1 : 0;
        }

        if (sum > 0) {
            item_read.setVisible(true);
            item_delete.setVisible(true);
            item_select.setVisible(true);
        } else {
            item_read.setVisible(false);
            item_delete.setVisible(false);
            item_select.setVisible(false);
        }
        if (sum == mCheckedState.length) {
            item_select.setTitle(getString(R.string.menu_select_all));
            item_select.setIcon(R.mipmap.ic_check_box_black_24dp);
        } else {
            item_select.setTitle(getString(R.string.menu_deselect_all));
            item_select.setIcon(R.mipmap.ic_check_box_outline_blank_black_24dp);
        }


//        mMenu.getItem(0).setVisible(false);
//        mMenu.getItem(1).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Info about MPAS", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//        public ScreenSlidePagerAdapter(FragmentManager fm) {
//            super(fm);
//        }


        private final List<Fragment> fragments;

        //On fournit à l'adapter la liste des fragments à afficher
        public ScreenSlidePagerAdapter(FragmentManager fm, List fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int pos) {

            switch (pos) {
                case 0:
                    return new NotificationFragment();
                case 1:
                    return new TaskFragment();
//                case 2:
//                    return new FragmentThree();
//
                default:
                    break;
            }
            return null;
//            return this.fragments.get(pos);
//            return new NotificationFragment();
        }

        @Override
        public int getCount() {
//            return this.fragments.size();
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0) {
                title = "Notifications";
            } else if (position == 1) {
                title = "Tasks";
            }
            return title;
//            return super.getPageTitle(position);
        }


    }


    public class CustomAdapter extends BaseAdapter {
//        final Context context;

        int num_selected;
        CheckBox c;
        List<String> Titles;
        List<String> Bodies;
        List<Boolean> isSeen;
        List<Integer> IList;
        List<Boolean> CList;
        List<Boolean> SSList;
        private LayoutInflater inflater = null;

        CustomAdapter() {

            Cursor cursor = getContentResolver().query(CONTENT_URI2, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {

//                        for (int i = 0; i < MESSAGES.size(); i++) {
                        this.IList.add(Integer.valueOf(cursor.getString(0)));
                        this.Titles.add(cursor.getString(1));
                        this.Bodies.add(cursor.getString(2));
                        this.CList.add("1".equals(cursor.getString(4)));
                        this.isSeen.add("1".equals(cursor.getString(5)));
                        this.SSList.add("1".equals(cursor.getString(7)));
//                        }
                        mCheckedState = new boolean[IList.size()];
                        num_selected = 0;
                    } while (cursor.moveToNext());
                }
            }
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            c = (CheckBox) findViewById(R.id.checkbox1);
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


        @Override
        public View getView(final int position, View rowView, ViewGroup parent) {

            Holder holder = new Holder();

            rowView = inflater.inflate(R.layout.list_row_layout, parent, false);

            holder.tv1 = (TextView) rowView.findViewById(R.id.title);
            holder.tv2 = (TextView) rowView.findViewById(R.id.body);
            holder.cb = (CheckBox) rowView.findViewById(R.id.checkbox1);
            holder.iv = (ImageView) rowView.findViewById(R.id.state);
            holder.i2 = (ImageView) rowView.findViewById(R.id.Critical);
            rowView.setTag(holder);

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

                        invalidateOptionsMenu();
                        notifyDataSetChanged();
                    } else {
                        htemp.cb.setVisibility(View.VISIBLE);

                        invalidateOptionsMenu();
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
                invalidateOptionsMenu();

            } else if (isSelected) {
                if (holder.cb != null)
                    holder.cb.setVisibility(View.GONE);
                invalidateOptionsMenu();

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

                        v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.SelectColor1));

                        Intent showActivity = new Intent(MainActivity.this, Message_Detail_Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(getString(R.string.Title), Titles.get(position));
                        bundle.putString(getString(R.string.Body), Bodies.get(position));
                        bundle.putBoolean(getString(R.string.Critical), CList.get(position));
                        bundle.putBoolean(getString(R.string.SendSeen), SSList.get(position));
                        bundle.putInt(getString(R.string.ID), IList.get(position));
                        bundle.putBoolean(getString(R.string.Seen), isSeen.get(position));
                        showActivity.putExtras(bundle);
                        Scroll_Position = position;
                        showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    finish();
                        startActivity(showActivity);
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
                    CheckBox c = (CheckBox) findViewById(R.id.checkbox1);

                    int sum = 0;
                    for (boolean b : mCheckedState) {
                        sum += b ? 1 : 0;
                    }
                    if ((sum == 1) & (htemp.cb.isChecked())) {

                        mCheckedState[position] = false;
                        invalidateOptionsMenu();

                        notifyDataSetChanged();

                    } else if (htemp.cb.isChecked()) {
//                        htemp.cb.setChecked(false);
                        mCheckedState[position] = false;
                        notifyDataSetChanged();

                    } else {

                        c.setVisibility(View.VISIBLE);
//
                        notifyDataSetChanged();
                        mCheckedState[position] = true;
                        htemp.cb.setVisibility(View.VISIBLE);
//                        htemp.cb.setChecked(true);


                        if (!isSelected) {

                            invalidateOptionsMenu();

                            isSelected = true;
                        } else {

                            invalidateOptionsMenu();
                        }


                    }

                    return true;
                }
            });
            holder.cb.setChecked(mCheckedState[position]);

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


//    public static class ArrayListFragment extends ListFragment {
//        int mNum;
//
//
//        static ArrayListFragment newInstance(int num) {
//            ArrayListFragment f = new ArrayListFragment();
//
//            // Supply num input as an argument.
//            Bundle args = new Bundle();
//            args.putInt("num", num);
//            f.setArguments(args);
//
//            return f;
//        }
//
//        /**
//         * When creating, retrieve this instance's number from its arguments.
//         */
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
//        }
//
//        /**
//         * The Fragment's UI is just a simple text view showing its
//         * instance number.
//         */
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.notification_tab, container, false);
//            View tv = v.findViewById(R.id.empty);
//            ((TextView)tv).setText("Fragment #" + mNum);
//            return v;
//        }
//
//        @Override
//        public void onActivityCreated(Bundle savedInstanceState) {
//            super.onActivityCreated(savedInstanceState);
//            setListAdapter(new ArrayAdapter<>(getActivity(),
//                    android.R.layout.simple_list_item_1, new String[]{"1","2","3"}));
//        }
//
//        @Override
//        public void onListItemClick(ListView l, View v, int position, long id) {
//            Log.i("FragmentList", "Item clicked: " + id);
//        }
//    }



}
