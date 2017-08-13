package turbotec.newmpas;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    static final String URL2 = "content://" + PROVIDER_NAME + "/tasks/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    private static final int NUM_PAGES = 2;
    static boolean[] NotiCheckedState;
    static boolean[] TaskCheckedState;
    static int Scroll_Position = 0;
    static boolean NFlag = true;
    static boolean TFlag = true;
    static boolean Gone = false;
    //    List<String> Mlist = new ArrayList<>(); //Messages List
//    List<String> Tlist = new ArrayList<>(); //Title List
//    List<Boolean> SList = new ArrayList<>(); //is Seen
    List<Integer> IList = new ArrayList<>(); //Message ID
    List<String> IDList = new ArrayList<>();
    private SharedPreferenceHandler share = SharedPreferenceHandler.getInstance(this);
    //    List<Boolean> CList = new ArrayList<>(); //Critical
//    List<Boolean> SSList = new ArrayList<>(); //SendSeen
    //    boolean isSelected = false;
//    private Menu mMenu;
    private boolean first = true;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private TabLayout tabLayout;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();

            setTitle(getString(R.string.app_name));
            if (action.equals("Alarm fire")) {

                boolean stat = share.GetChange();
                if (stat) {

                    ListView lvno = (ListView) findViewById(R.id.list_notification);
                    ListView lvta = (ListView) findViewById(R.id.list_task);
                    if (tabLayout.getSelectedTabPosition() == 0) {

                        NotificationsAdapter ad = NotificationsAdapter.getInstance(context);
                        lvno.setAdapter(ad);
                        ad.notifyDataSetChanged();

                    } else if (tabLayout.getSelectedTabPosition() == 1) {

                        TasksAdapter ad = TasksAdapter.getInstance(context);
                        lvta.setAdapter(ad);
                        ad.notifyDataSetChanged();
                    }
                    Log.i("is this ", "BroadcastReceiver");
                }


            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        NotificationsAdapter.getInstance(this);
        setContentView(R.layout.activity_main);
//        NotiCheckedState[0] = false;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();



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

        registerReceiver(broadcastReceiver, new IntentFilter("Alarm fire"));

        NotificationsAdapter.set(this);
        TasksAdapter.set(this);


        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, NotificationFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, TaskFragment.class.getName()));

        this.mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(mPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
//                switch (tab.getPosition()) {
//                    case 0: {
//                        ListView lvno = (ListView) findViewById(R.id.list_notification);
//                        lvno.setSelection(Scroll_Position);
//                    }
//                    case 1: {
//                        ListView lvta = (ListView) findViewById(R.id.list_task);
//                        lvta.setSelection(Scroll_Position);
//                    }
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                final TabLayout.Tab tb = tab;
                int sumN = 0;
                for (int i = 0; i < NotiCheckedState.length; i++) {
                    sumN += NotiCheckedState[i] ? 1 : 0;
                    NotiCheckedState[i] = false;
                }
                int sumT = 0;
                for (int i = 0; i < TaskCheckedState.length; i++) {
                    sumT += TaskCheckedState[i] ? 1 : 0;
                    TaskCheckedState[i] = false;
                }

                switch (tb.getPosition()) {
                    case 0: {
                        if (sumN > 0) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    NotificationsAdapter NAda = NotificationsAdapter.getInstance();
                                    NAda.notifyDataSetChanged();
                                }
                            }, 300);
                        }
                        break;
                    }
                    case 1: {
                        if (sumT > 0) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {

                                    TasksAdapter TAda = TasksAdapter.getInstance();
                                    TAda.notifyDataSetChanged();
                                }
                            }, 300);
                        }
                        break;
                    }
                }

                invalidateOptionsMenu();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("first", first);
        super.onSaveInstanceState(outState);
        }

    @Override
    protected void onResume() {
        super.onResume();
        NFlag = true;
        TFlag = true;
        if (Gone) {
            ListView lvno = (ListView) findViewById(R.id.list_notification);
            ListView lvta = (ListView) findViewById(R.id.list_task);
            if (tabLayout.getSelectedTabPosition() == 0) {
                NotificationsAdapter ad = NotificationsAdapter.getInstance(this);
                lvno.setAdapter(ad);
                ad.notifyDataSetChanged();
                lvno.setSelection(Scroll_Position);

            } else if (tabLayout.getSelectedTabPosition() == 1) {

                TasksAdapter ad = TasksAdapter.getInstance(this);
                lvta.setAdapter(ad);
                ad.notifyDataSetChanged();
            }
            Gone = false;
        }

//        ListView lv = (ListView) findViewById(R.id.list_notification);
//        if ((tabLayout.getSelectedTabPosition() == 0)&(lv != null)) {
//            lv.post(new Runnable() {
//                @Override
//                public void run() {
//                    ListView lv = (ListView) findViewById(R.id.list_notification);
//                    lv.setSelection(Scroll_Position);
//                }
//            });
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item_read = menu.findItem(R.id.menu_read);
        MenuItem item_delete = menu.findItem(R.id.menu_delete);
        MenuItem item_select = menu.findItem(R.id.menu_select_all);
//        main_menu.findItem(R.id.menu_read).setVisible(false);
//        main_menu.findItem(R.id.menu_delete).setVisible(false);
//        mMenu = menu;
        int sum = 0;
        int total = 0;
        if (tabLayout.getSelectedTabPosition() == 0) {
            for (boolean b : NotiCheckedState) {
                sum += b ? 1 : 0;
            }
            total = NotiCheckedState.length;

        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (boolean b : TaskCheckedState) {
                sum += b ? 1 : 0;
            }
            total = TaskCheckedState.length;
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
        if (sum == total) {
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
        int total = 0;
        if (tabLayout.getSelectedTabPosition() == 0) {
            for (boolean b : NotiCheckedState) {
                sum += b ? 1 : 0;
            }
            total = NotiCheckedState.length;

        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (boolean b : TaskCheckedState) {
                sum += b ? 1 : 0;
            }
            total = TaskCheckedState.length;
        }


        if (sum == total) {
            opposite = false;
        }

        if (tabLayout.getSelectedTabPosition() == 0) {
            for (int i = 0; i < total; i++) {
                NotiCheckedState[i] = opposite;
            }
            NotificationsAdapter ad = NotificationsAdapter.getInstance();
            ad.notifyDataSetChanged();

        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (int i = 0; i < total; i++) {
                TaskCheckedState[i] = opposite;
            }
            TasksAdapter ad = TasksAdapter.getInstance();
            ad.notifyDataSetChanged();
        }

        invalidateOptionsMenu();

    }


    private void MarkDelete() {


        if (tabLayout.getSelectedTabPosition() == 0) {
            for (int i = 0; i < NotiCheckedState.length; i++) {

                if (NotiCheckedState[i]) {
                    Integer ID;
                    ID = IList.get(i);
                    getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{String.valueOf(ID)});
                }
            }

            for (int i = 0; i < NotiCheckedState.length; i++) {
                NotiCheckedState[i] = false;
            }
            NotificationsAdapter ad = NotificationsAdapter.getInstance();
            ad.notifyDataSetChanged();

        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (int i = 0; i < TaskCheckedState.length; i++) {

                if (TaskCheckedState[i]) {
                    String ID;
                    ID = IDList.get(i);
                    getContentResolver().delete(CONTENT_URI2, "_id  = ?", new String[]{String.valueOf(ID)});
                }
            }
            for (int i = 0; i < TaskCheckedState.length; i++) {
                TaskCheckedState[i] = false;
            }
            TasksAdapter ad = TasksAdapter.getInstance();
            ad.notifyDataSetChanged();
        }

        invalidateOptionsMenu();


    }

    private void MarkRead() {

        if (tabLayout.getSelectedTabPosition() == 0) {
            for (int i = 0; i < NotiCheckedState.length; i++) {

                if (NotiCheckedState[i]) {
                    Integer ID;
                    ID = IList.get(i);
                    ContentValues values = new ContentValues();
                    values.put("Seen", true);
                    getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{String.valueOf(ID)});
                }
            }
            for (int i = 0; i < NotiCheckedState.length; i++) {
                NotiCheckedState[i] = false;
            }
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (int i = 0; i < TaskCheckedState.length; i++) {

                if (TaskCheckedState[i]) {
                    String ID;
                    ID = IDList.get(i);
                    ContentValues values = new ContentValues();
                    values.put("isSeen", true);
                    getContentResolver().update(CONTENT_URI2, values, "_id  = ?", new String[]{String.valueOf(ID)});
                }
            }
            for (int i = 0; i < TaskCheckedState.length; i++) {
                TaskCheckedState[i] = false;
            }
        }

        invalidateOptionsMenu();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
                        .setTitle(R.string.Delete_Button);
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



    }

    @Override
    public void onBackPressed() {
        boolean fl = false;
//        boolean i;
        for (boolean i : NotiCheckedState) {
            if (i) {
                fl = true;
                break;
            }
        }

        if (fl) {
            for (int i = 0; i < NotiCheckedState.length; i++) {
                NotiCheckedState[i] = false;
            }
            for (int i = 0; i < TaskCheckedState.length; i++) {
                TaskCheckedState[i] = false;
            }
            ListView lvno = (ListView) findViewById(R.id.list_notification);
            ListView lvta = (ListView) findViewById(R.id.list_task);
            if (lvno != null) {
                NotificationsAdapter ad = NotificationsAdapter.getInstance(getBaseContext());
                lvno.setAdapter(ad);
                ad.notifyDataSetChanged();
            } else if (lvta != null) {
                TasksAdapter ad = TasksAdapter.getInstance(getBaseContext());
                lvta.setAdapter(ad);
                ad.notifyDataSetChanged();
            }
        } else {
            super.onBackPressed();
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        private List<Fragment> mfragments;

        //On fournit à l'adapter la liste des mfragments à afficher
        public ScreenSlidePagerAdapter(FragmentManager fm, List fragments) {
            super(fm);
            this.mfragments = fragments;
        }

        @Override
        public Fragment getItem(int pos) {


            if (mfragments != null) {
                return mfragments.get(pos);
            }
            return null;

        }

        @Override
        public int getCount() {
//            return this.mfragments.size();
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







}
