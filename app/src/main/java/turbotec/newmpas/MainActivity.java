package turbotec.newmpas;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL = "content://" + PROVIDER_NAME + "/messages/";
    static final Uri CONTENT_URI = Uri.parse(URL);
    private static final int NUM_PAGES = 2;
    static boolean[] mCheckedState;
    static int Scroll_Position = 0;
    static boolean Flag = true;
    List<String> Mlist = new ArrayList<>(); //Messages List
    List<String> Tlist = new ArrayList<>(); //Title List
    List<Boolean> SList = new ArrayList<>(); //is Seen
    List<Integer> IList = new ArrayList<>(); //Message ID
    List<Boolean> CList = new ArrayList<>(); //Critical
    List<Boolean> SSList = new ArrayList<>(); //SendSeen
    //    boolean isSelected = false;
//    private Menu mMenu;
    private boolean first = true;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CustomAdapter.getInstance(this);
        setContentView(R.layout.activity_main);
//        mCheckedState[0] = false;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();


//        Intent VC = new Intent(this,VersionUpdate.class);
//        startService(VC);

        Intent in = getIntent();

        if (savedInstanceState == null) {
            if (in != null) {
                Bundle b = in.getExtras();
                if (b != null) {

                    Intent showActivity = new Intent(MainActivity.this, Message_Detail_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.Title), b.getString(getString(R.string.Title)));
                    bundle.putString(getString(R.string.Body), b.getString(getString(R.string.Body)));
                    bundle.putInt(getString(R.string.ID), b.getInt(getString(R.string.ID)));
                    bundle.putBoolean(getString(R.string.Seen), b.getBoolean(getString(R.string.Seen)));
                    bundle.putBoolean(getString(R.string.Critical), b.getBoolean(getString(R.string.Critical)));
                    bundle.putBoolean(getString(R.string.SendSeen), b.getBoolean(getString(R.string.SendSeen)));

                    String s = b.getString(getString(R.string.Title));
                    if (s != null)
                        if (!s.isEmpty()) {
                            showActivity.putExtras(bundle);
//            showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(showActivity);
                        }
                }
            }
        }


        CustomAdapter.set(this);



        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, NotificationFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, TaskFragment.class.getName()));

        this.mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                for (int i = 0; i < mCheckedState.length; i++) {
                    mCheckedState[i] = false;
                }
//                CustomAdapter.set(MainActivity.this);
                ListView lvno = (ListView) findViewById(R.id.list_notification);
                ListView lvta = (ListView) findViewById(R.id.list_task);
                if (lvno != null) {
//                    CustomAdapter.set(this);
                    CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
                    lvno.setAdapter(ad);
                    ad.notifyDataSetChanged();
                } else if (lvta != null) {
//                    CustomAdapter.set(this);
                    CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
                    lvta.setAdapter(ad);
                    ad.notifyDataSetChanged();
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("first", first);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Flag = true;
        ListView lvno = (ListView) findViewById(R.id.list_notification);
        ListView lvta = (ListView) findViewById(R.id.list_task);
        if (lvno != null) {
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvno.setAdapter(ad);
            ad.notifyDataSetChanged();

            lvno.post(new Runnable() {
                @Override
                public void run() {
                    ListView lvno = (ListView) findViewById(R.id.list_notification);
                    lvno.setSelection(Scroll_Position);
                }
            });

        } else if (lvta != null) {
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvta.setAdapter(ad);
            ad.notifyDataSetChanged();
            lvta.post(new Runnable() {
                @Override
                public void run() {
                    ListView lvta = (ListView) findViewById(R.id.list_task);
                    lvta.setSelection(Scroll_Position);
                }
            });
        }

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
        } else if (sum > 0) {
            item_select.setTitle(getString(R.string.menu_deselect_all));
            item_select.setIcon(R.mipmap.ic_check_box_outline_blank_black_24dp);
        }


//        mMenu.getItem(0).setVisible(false);
//        mMenu.getItem(1).setVisible(false);
        return true;
    }



    private void MarkAll() {
        boolean opposite = true;
//        MenuItem item_select = mMenu.findItem(R.id.menu_select_all);
//        item_select.setTitle("Deselect All");
//        item_select.setIcon(R.mipmap.ic_check_box_outline_blank_black_24dp);
        int sum = 0;
        for (boolean b : mCheckedState) {
            sum += b ? 1 : 0;
        }
        if (sum == mCheckedState.length) {
            opposite = false;
//            item_select.setTitle("Deselect All");
//            item_select.setIcon(R.mipmap.ic_check_box_outline_blank_black_24dp);
        }

        for (int i = 0; i < mCheckedState.length; i++) {
            mCheckedState[i] = opposite;
        }
        invalidateOptionsMenu();
//        getContentResolver().notifyChange(CONTENT_URI, null, false);
        ListView lvno = (ListView) findViewById(R.id.list_notification);
        ListView lvta = (ListView) findViewById(R.id.list_task);
        if (lvno != null) {
//            CustomAdapter.set(this);
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvno.setAdapter(ad);
            ad.notifyDataSetChanged();
        } else if (lvta != null) {
//            CustomAdapter.set(this);
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvta.setAdapter(ad);
            ad.notifyDataSetChanged();
        }
    }


    private void MarkDelete() {

//        ListView lv = (ListView) findViewById(R.id.list1);
//        CheckBox cb;

//        SQLiteDatabase database = db.getWritableDatabase();
        for (int i = 0; i < mCheckedState.length; i++) {
//            cb = (CheckBox) lv.getChildAt(i).findViewById(R.id.checkbox1);

            if (mCheckedState[i]) {
                Integer ID;
                ID = IList.get(i);
//                database.delete("Messages", "MessageID  = ?", new String[]{String.valueOf(ID)});
                getContentResolver().delete(Uri.parse(URL + ID), "_id  = ?", new String[]{String.valueOf(ID)});
            }
        }
//        database.close();
//        isSelected = false;
//        mMenu.clear();
//        adapt.notifyDataSetChanged();
        for (int i = 0; i < mCheckedState.length; i++) {
            mCheckedState[i] = false;
        }
//        adapt.notifyDataSetChanged();
        invalidateOptionsMenu();
        ListView lvno = (ListView) findViewById(R.id.list_notification);
        ListView lvta = (ListView) findViewById(R.id.list_task);
        if (lvno != null) {
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvno.setAdapter(ad);
            ad.notifyDataSetChanged();
        } else if (lvta != null) {
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvta.setAdapter(ad);
            ad.notifyDataSetChanged();
        }

//        UpdateUI(share.GetStatus());

    }

    private void MarkRead() {


//        SQLiteDatabase database = db.getWritableDatabase();
        for (int i = 0; i < mCheckedState.length; i++) {
//            cb = (CheckBox) lv.getChildAt(i).findViewById(R.id.checkbox1);

            if (mCheckedState[i]) {
                Integer ID;
                ID = IList.get(i);
                ContentValues values = new ContentValues();
                values.put("Seen", true);
//                database.update("Messages", values, "MessageID  = ?", new String[]{String.valueOf(ID)});
                getContentResolver().update(Uri.parse(URL + ID), values, "_id  = ?", new String[]{String.valueOf(ID)});
            }
        }
//        database.close();
//        isSelected = false;
//        mMenu.clear();
//        adapt.notifyDataSetChanged();
        for (int i = 0; i < mCheckedState.length; i++) {
            mCheckedState[i] = false;
        }
//        adapt.notifyDataSetChanged();
        invalidateOptionsMenu();
        ListView lvno = (ListView) findViewById(R.id.list_notification);
        ListView lvta = (ListView) findViewById(R.id.list_task);
        if (lvno != null) {
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvno.setAdapter(ad);
            ad.notifyDataSetChanged();
        } else if (lvta != null) {
            CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
            lvta.setAdapter(ad);
            ad.notifyDataSetChanged();
        }
//        UpdateUI(share.GetStatus());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_delete:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                MarkDelete();

                            }
                        })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();

                                    }
                                })
                        .setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title);
                AlertDialog adialog = alertDialogBuilder.create();
                adialog.show();
                return true;

            case R.id.menu_read:
                MarkRead();
                return true;
            case R.id.menu_select_all:
                MarkAll();
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Info about MPAS", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(this, "Info about MPAS", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        boolean fl = false;
//        boolean i;
        for (boolean i : mCheckedState) {
            if (i) {
                fl = true;
                break;
            }
        }

        if (fl) {
            for (int i = 0; i < mCheckedState.length; i++) {
                mCheckedState[i] = false;
            }
            ListView lvno = (ListView) findViewById(R.id.list_notification);
            ListView lvta = (ListView) findViewById(R.id.list_task);
            if (lvno != null) {
                CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
                lvno.setAdapter(ad);
                ad.notifyDataSetChanged();
            } else if (lvta != null) {
                CustomAdapter ad = CustomAdapter.getInstance(getBaseContext());
                lvta.setAdapter(ad);
                ad.notifyDataSetChanged();
            }
        } else {
            super.onBackPressed();
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
//                    NotificationFragment.set(MainActivity.this);
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
