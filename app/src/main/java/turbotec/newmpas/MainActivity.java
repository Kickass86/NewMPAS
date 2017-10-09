package turbotec.newmpas;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.SearchManager;
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
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    static final String URL2 = "content://" + PROVIDER_NAME + "/tasks/";
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    static final int NUM_PAGES = 2;
    static final String[] TABS_Names = {"Messages", "Tasks"};
    static boolean[] MessaCheckedState = new boolean[1];
    static boolean[] TaskCheckedState = new boolean[1];
    static int Scroll_Position = 0;
    static boolean NFlag = true;
    static boolean TFlag = true;
    static boolean Gone = false;
    static MessagesListAdapter AdaptNo;
    static TasksAdapter AdaptTa;
    //    List<Boolean> CList = new ArrayList<>(); //Critical
//    List<Boolean> SSList = new ArrayList<>(); //SendSeen
    //    boolean isSelected = false;
//    private Menu mMenu;
    static TabController.Tabs setTab = TabController.Tabs.Message;
    MenuItem item_search;
    SearchView searchView;
    TabController tabController;
    //    List<String> Mlist = new ArrayList<>(); //Messages List
//    List<String> Tlist = new ArrayList<>(); //Title List
//    List<Boolean> SList = new ArrayList<>(); //is Seen
    List<Integer> IList = new ArrayList<>(); //Message ID
    List<String> IDList = new ArrayList<>();
    private MainActivity Myactivity;
    private SharedPreferenceHandler share = SharedPreferenceHandler.getInstance(this);
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();

            setTitle(getString(R.string.app_name));
            if (action.equals("Alarm fire")) {

                boolean stat = share.GetChange();
                if (stat) {
//                    int i = intent.getIntExtra("Type", 0);

//                    ListView lvno = (ListView) findViewById(R.id.list_notification);
//                    ListView lvta = (ListView) findViewById(R.id.list_task);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    int nt = tabController.GetTabNum(setTab);
                    tabLayout.getTabAt(nt).select();
//                    TabLayout.Tab tab = tabLayout.getTabAt(setTab);
//                    tab.select();


//                    if (i == 0) {

//                        MessagesListAdapter ad = MessagesListAdapter.getInstance(context);
//                        lvno.setAdapter(ad);
                    AdaptNo = MessagesListAdapter.getInstance(context);
                    AdaptNo.notifyDataSetChanged();

//                    } else {

//                        TasksAdapter ad = TasksAdapter.getInstance(context);
//                        lvta.setAdapter(ad);
                    AdaptTa = TasksAdapter.getInstance(context);
                    AdaptTa.notifyDataSetChanged();
//                    }
                    Log.i("is this ", "BroadcastReceiver");
                    share.SaveChange(false);
                }


            }
        }
    };
    private boolean first = true;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private TabLayout tabLayout;

    public static int findNthIndexOf(String str, String needle, int occurence)
            throws IndexOutOfBoundsException {
        int index = -1;
        Pattern p = Pattern.compile(needle, Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        while (m.find()) {
            if (--occurence == 0) {
                index = m.start();
                break;
            }
        }
        if (index < 0) throw new IndexOutOfBoundsException();
        return index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MessagesListAdapter.getInstance(this);
        setContentView(R.layout.activity_main);
//        MessaCheckedState[0] = false;

        Myactivity = this;
        tabController = TabController.getInstance(getApplicationContext());
        try {

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();


            Intent in = getIntent();

            if (savedInstanceState == null) {
                if (in != null) {
                    Bundle b = in.getExtras();
                    if (b != null) {

                        if (b.getBoolean("Type")) {
                            Intent showActivity = new Intent(MainActivity.this, Message_Detail_Activity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.Title), b.getString(getString(R.string.Title)));
                            bundle.putString(getString(R.string.Body), b.getString(getString(R.string.Body)));
                            bundle.putString(getString(R.string.Link), b.getString(getString(R.string.Link)));
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
                                    Gone = true;
                                    setTab = TabController.Tabs.Message;
                                    startActivity(showActivity);
                                    overridePendingTransition(0, 0);
                                }
                        } else {
                            Intent showActivity = new Intent(MainActivity.this, Task_Detail_Activity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.Subject), b.getString(getString(R.string.Subject)));
                            bundle.putString(getString(R.string.TCreator), b.getString(getString(R.string.TCreator)));
                            bundle.putString(getString(R.string.DueDate), b.getString(getString(R.string.DueDate)));
                            bundle.putInt(getString(R.string.TStatus), b.getInt(getString(R.string.TStatus)));
                            bundle.putString(getString(R.string.TDescription), b.getString(getString(R.string.TDescription)));
                            bundle.putBoolean(getString(R.string.TEditable), b.getBoolean(getString(R.string.TEditable)));
                            bundle.putBoolean(getString(R.string.TReplyAble), b.getBoolean(getString(R.string.TReplyAble)));
                            bundle.putBoolean(getString(R.string.TDeletable), b.getBoolean(getString(R.string.TDeletable)));
                            bundle.putBoolean(getString(R.string.TisCreator), b.getBoolean(getString(R.string.TisCreator)));
                            bundle.putBoolean(getString(R.string.TisResponsible), b.getBoolean(getString(R.string.TisResponsible)));
                            bundle.putString(getString(R.string.TNameResponsible), b.getString(getString(R.string.TNameResponsible)));
                            bundle.putString(getString(R.string.TID), b.getString(getString(R.string.TID)));
                            bundle.putString(getString(R.string.TReport), b.getString(getString(R.string.TReport)));
                            showActivity.putExtras(bundle);
                            Gone = true;
                            setTab = TabController.Tabs.Task;
                            showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            Log.i("Task Detail", Tlist.get(position));
//                    finish();
                            startActivity(showActivity);
                            overridePendingTransition(0, 0);
                        }
                    }
                }
            }

            registerReceiver(broadcastReceiver, new IntentFilter("Alarm fire"));

            MessagesListAdapter.set(this);
            TasksAdapter.set(this);


            List<Fragment> fragments = new Vector<>();
            //add Dynamic Tabs here
            fragments.add(Fragment.instantiate(this, NotificationFragment.class.getName()));
            fragments.add(Fragment.instantiate(this, TaskFragment.class.getName()));

            this.mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
            mPager = (ViewPager) findViewById(R.id.pager);

            mPager.setAdapter(mPagerAdapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mPager);

            mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//                @Override
//                public void onTabSelected(TabLayout.Tab tab) {
////                    setTab = tab.getPosition();
//                    //do stuff here
//                    switch (tab.getPosition()) {
//                        case 0: {
//                            ListView lvno = (ListView) findViewById(R.id.list_notification);
//                            if (lvno != null) {
//                                lvno.setSelection(Scroll_Position);
//                                if (Gone) {
////                                    MessagesListAdapter ad = MessagesListAdapter.getInstance(getBaseContext());
////                                    lvno.setAdapter(ad);
//                                    AdaptNo = MessagesListAdapter.getInstance(getBaseContext());
//                                    AdaptNo.notifyDataSetChanged();
//                                }
//                            }
//                        }
//                        case 1: {
//                            ListView lvta = (ListView) findViewById(R.id.list_task);
//                            if (lvta != null) {
//                                lvta.setSelection(Scroll_Position);
//                                if (Gone) {
////                                    TasksAdapter ad = TasksAdapter.getInstance(getBaseContext());
////                                    lvta.setAdapter(ad);
//                                    AdaptTa = TasksAdapter.getInstance(getBaseContext());
//                                    AdaptTa.notifyDataSetChanged();
//                                }
//                            }
//                        }
//                    }
//                }

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    SearchableActivity.TAB = tab.getPosition();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    final TabLayout.Tab tb = tab;
                    int sumN = 0;
                    for (int i = 0; i < MessaCheckedState.length; i++) {
                        sumN += MessaCheckedState[i] ? 1 : 0;
                        MessaCheckedState[i] = false;
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
//                                        MessagesListAdapter NAda = MessagesListAdapter.getInstance(Myactivity);
                                        AdaptNo = MessagesListAdapter.getInstance(Myactivity);
                                        AdaptNo.notifyDataSetChanged();
                                    }
                                }, 300);
                            }
                            break;
                        }
                        case 1: {
                            if (sumT > 0) {
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {

//                                        TasksAdapter TAda = TasksAdapter.getInstance(Myactivity);
                                        AdaptTa = TasksAdapter.getInstance(Myactivity);
                                        AdaptTa.notifyDataSetChanged();
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

        } catch (Exception e) {
            share.SaveError(e.getMessage());
            Intent SE = new Intent(this, SendError.class);
            startService(SE);
        }
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

//        try {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        int nt = tabController.GetTabNum(setTab);
        mPager.setCurrentItem(nt);

        NFlag = true;
        TFlag = true;
        if (Gone) {
            ListView lvno = (ListView) findViewById(R.id.list_messages);
            ListView lvta = (ListView) findViewById(R.id.list_task);
            if ((tabLayout.getSelectedTabPosition() == 0) & (lvno != null)) {
//                    MessagesListAdapter ad = MessagesListAdapter.getInstance(this);
//                    lvno.setAdapter(ad);
                AdaptNo = MessagesListAdapter.getInstance(this);
                AdaptNo.notifyDataSetChanged();
                lvno.setSelection(Scroll_Position);
                Gone = false;

            } else if ((tabLayout.getSelectedTabPosition() == 1) & (lvta != null)) {

//                    TasksAdapter ad = TasksAdapter.getInstance(this);
//                    lvta.setAdapter(ad);
                AdaptTa = TasksAdapter.getInstance(this);
                AdaptTa.notifyDataSetChanged();
                lvta.setSelection(Scroll_Position);
                Gone = false;
            }

            item_search.collapseActionView();
            searchView.setQuery("", false);

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

//        } catch (Exception e) {
//            share.SaveError(e.getMessage());
//            Intent SE = new Intent(this, SendError.class);
//            startService(SE);
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item_read = menu.findItem(R.id.menu_read);
        MenuItem item_delete = menu.findItem(R.id.menu_delete);
        MenuItem item_select = menu.findItem(R.id.menu_select_all);
        item_search = menu.findItem(R.id.action_search);
//        main_menu.findItem(R.id.menu_read).setVisible(false);
//        main_menu.findItem(R.id.menu_delete).setVisible(false);
//        mMenu = menu;
        int sum = 0;
        int total = 0;
        int num = tabLayout.getSelectedTabPosition();
        String Enable = share.GetTabsControl();
        switch (num) {
            case 0:
                if (0 == findNthIndexOf(Enable, "1", 1)) {
                    for (boolean b : MessaCheckedState) {
                        sum += b ? 1 : 0;
                    }
                    total = MessaCheckedState.length;
                } else {
                    for (boolean b : TaskCheckedState) {
                        sum += b ? 1 : 0;
                    }
                    total = TaskCheckedState.length;
                }
                break;
            case 1:
                for (boolean b : TaskCheckedState) {
                    sum += b ? 1 : 0;
                }
                total = TaskCheckedState.length;
                break;

        }
//        if (tabLayout.getSelectedTabPosition() == 0) {
//            for (boolean b : MessaCheckedState) {
//                sum += b ? 1 : 0;
//            }
//            total = MessaCheckedState.length;
//
//        } else if (tabLayout.getSelectedTabPosition() == 1) {
//            for (boolean b : TaskCheckedState) {
//                sum += b ? 1 : 0;
//            }
//            total = TaskCheckedState.length;
//        }

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

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) item_search.getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), SearchableActivity.class)));

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setFocusableInTouchMode(true);
        MessagesListAdapter.valid = false;
        searchView.setFocusable(true);
//        searchView.requestFocus();

//        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        mgr.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
//        mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

//        searchView.setIconified(false);
//        searchView.requestFocusFromTouch();

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
            for (boolean b : MessaCheckedState) {
                sum += b ? 1 : 0;
            }
            total = MessaCheckedState.length;

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
                MessaCheckedState[i] = opposite;
            }
            AdaptNo = MessagesListAdapter.getInstance(this);
            AdaptNo.notifyDataSetChanged();

        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (int i = 0; i < total; i++) {
                TaskCheckedState[i] = opposite;
            }
//            TasksAdapter ad = TasksAdapter.getInstance(this);
            AdaptTa = TasksAdapter.getInstance(this);
            AdaptTa.notifyDataSetChanged();
        }

        invalidateOptionsMenu();

    }

    private void MarkDelete() {

//        new Thread(new Runnable() {
//            public void run() {

        ListView lvno = (ListView) findViewById(R.id.list_messages);
        ListView lvta = (ListView) findViewById(R.id.list_task);

        if (tabLayout.getSelectedTabPosition() == 0) {
            for (int i = 0; i < MessaCheckedState.length; i++) {

                if (MessaCheckedState[i]) {
                    Integer ID;
                    ID = IList.get(i);
                    getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{String.valueOf(ID)});
                    MessaCheckedState[i] = false;
                }
            }


//            MessagesListAdapter ad = MessagesListAdapter.getInstance(this);
//            lvno.setAdapter(ad);
            AdaptNo = MessagesListAdapter.getInstance(this);
            AdaptNo.notifyDataSetChanged();

        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (int i = 0; i < TaskCheckedState.length; i++) {

                if (TaskCheckedState[i]) {
                    String ID;
                    ID = IDList.get(i);
                    getContentResolver().delete(CONTENT_URI2, "_id  = ?", new String[]{String.valueOf(ID)});
                    TaskCheckedState[i] = false;
                }
            }


//            TasksAdapter ad = TasksAdapter.getInstance(this);
//            lvta.setAdapter(ad);
            AdaptTa = TasksAdapter.getInstance(this);
            AdaptTa.notifyDataSetChanged();
        }

        invalidateOptionsMenu();
//            }
//        }).start();


    }

    private void MarkRead() {


//        new Thread(new Runnable() {
//            public void run() {

        ListView lvno = (ListView) findViewById(R.id.list_messages);
        ListView lvta = (ListView) findViewById(R.id.list_task);

        if (tabLayout.getSelectedTabPosition() == 0) {
            for (int i = 0; i < MessaCheckedState.length; i++) {

                if (MessaCheckedState[i]) {
                    Integer ID;
                    ID = IList.get(i);
                    ContentValues values = new ContentValues();
                    values.put("Seen", true);
                    getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{String.valueOf(ID)});
                }
            }
            for (int i = 0; i < MessaCheckedState.length; i++) {
                MessaCheckedState[i] = false;
            }

//            MessagesListAdapter ad = MessagesListAdapter.getInstance(getApplication());
//            lvno.setAdapter(ad);
            AdaptNo = MessagesListAdapter.getInstance(getApplication());
            AdaptNo.notifyDataSetChanged();
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
//            TasksAdapter ad = TasksAdapter.getInstance(getApplication());
//            lvta.setAdapter(ad);
            AdaptTa = TasksAdapter.getInstance(getApplication());
            AdaptTa.notifyDataSetChanged();
        }

        invalidateOptionsMenu();
//            }
//        }).start();


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
//            case R.id.action_search:
//                Intent intent = new Intent(MainActivity.this,SearchableActivity.class);
//////                intent.putExtra("Tab#", tabLayout.getSelectedTabPosition());
//                intent.setAction(Intent.ACTION_SEARCH);
////                SearchableActivity.TAB = tabLayout.getSelectedTabPosition();
//                this.startActivity(intent);
////                finish();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }

    @Override
    public void onBackPressed() {
        boolean fl = false;


        ListView lvno = (ListView) findViewById(R.id.list_messages);
        ListView lvta = (ListView) findViewById(R.id.list_task);

        if (tabLayout.getSelectedTabPosition() == 0) {
            for (boolean i : MessaCheckedState) {
                if (i) {
                    fl = true;
                    break;
                }
            }
            if (fl) {
                for (int i = 0; i < MessaCheckedState.length; i++) {
                    MessaCheckedState[i] = false;
                }

//                MessagesListAdapter ad = MessagesListAdapter.getInstance(this);
//                lvno.setAdapter(ad);
                AdaptNo = MessagesListAdapter.getInstance(this);
                AdaptNo.notifyDataSetChanged();
            }
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (boolean i : TaskCheckedState) {
                if (i) {
                    fl = true;
                    break;
                }
            }
            if (fl) {
                for (int i = 0; i < TaskCheckedState.length; i++) {
                    TaskCheckedState[i] = false;
                }
//                TasksAdapter ad = TasksAdapter.getInstance(this);
//                lvta.setAdapter(ad);
                AdaptTa = TasksAdapter.getInstance(this);
                AdaptTa.notifyDataSetChanged();
            }
        }
        if (!fl) {
            super.onBackPressed();
        }

        invalidateOptionsMenu();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        String Enable;
        private List<Fragment> mfragments;

        //On fournit à l'adapter la liste des mfragments à afficher
        public ScreenSlidePagerAdapter(FragmentManager fm, List fragments) {
            super(fm);
            this.mfragments = fragments;
            Enable = share.GetTabsControl();
        }

        @Override
        public Fragment getItem(int pos) {

            int num = findNthIndexOf(Enable, "1", pos + 1);
            if (mfragments != null) {
                return mfragments.get(num);
            }
            return null;

        }

        @Override
        public int getCount() {
//            return this.mfragments.size();
            int num = 0;
            for (int i = 0; i < NUM_PAGES; i++) {
                if ('1' == (Enable.charAt(i))) {
                    num++;
                }
            }
            return num;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            String title = null;
//            if (position == 0) {
//                title = "Notifications";
//            } else if (position == 1) {
//                title = "Tasks";
//            }
//            return title;
//            String Enable = share.GetTabsControl();
            int num = findNthIndexOf(Enable, "1", position + 1);
            return TABS_Names[num];
//            return super.getPageTitle(position);
        }


    }



}
