package turbotec.newmpas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    static final String PROVIDER_NAME = SyncService.PROVIDER_NAME;
    static final String URL1 = "content://" + PROVIDER_NAME + "/messages/";
    static final String URL2 = "content://" + PROVIDER_NAME + "/tasks/";
    static final String URL = "content://" + PROVIDER_NAME + "/users/";
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final Uri CONTENT_URI1 = Uri.parse(URL1);
    static final Uri CONTENT_URI2 = Uri.parse(URL2);
    static final int NUM_PAGES = 3;
    static final String[] TABS_Names = {"Messages", "Tasks", "Meeting"};
    static String[] Responsible;
    static String IDResponsible;
    static String NameResponsible = "";
    static String NameCreator = "";
    static String NameSecretary = "";
    static boolean[] MessaCheckedState = new boolean[1];
    static boolean[] TaskCheckedState = new boolean[1];
    static int Scroll_Position = 0;
    static boolean NFlag = true;
    static boolean TFlag = true;
    static boolean Gone = false, Filter = true;
    static MessagesListAdapter AdaptNo;
    static TasksAdapter AdaptTa;
    static MeetingAdapter AdaptMt;
    //    List<Boolean> CList = new ArrayList<>(); //Critical
//    List<Boolean> SSList = new ArrayList<>(); //SendSeen
    //    boolean isSelected = false;
//    private Menu mMenu;
    static TabController.Tabs setTab;
    String Enable;
    boolean First = true;
    //    boolean search = false;
    EditText subject;
    EditText description;
    EditText message;
    EditText body;
    EditText Date;
    //    EditText Creator;
    MenuItem item_search, item_filter, item_setting;
    SearchView searchView;
    TabController tabController;
    //    List<String> Mlist = new ArrayList<>(); //Messages List
//    List<String> Tlist = new ArrayList<>(); //Topic List
//    List<Boolean> SList = new ArrayList<>(); //is Seen
    List<Integer> MIDList = new ArrayList<>(); //Message ID
    List<String> TIDList = new ArrayList<>();
    List<String> MTIDList = new ArrayList<>();
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    private MainActivity MyActivity;
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
                    AdaptNo = MessagesListAdapter.getInstance();
                    AdaptNo.notifyDataSetChanged();

//                    } else {

//                        TasksAdapter ad = TasksAdapter.getInstance(context);
//                        lvta.setAdapter(ad);
                    AdaptTa = TasksAdapter.getInstance();
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

    private void updateLabel() {

        EditText edittext = (EditText) findViewById(R.id.DateFilter);
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTab = TabController.Tabs.Message;
        Enable = share.GetTabsControl();
        Handle(savedInstanceState);
    }

    void Handle(Bundle savedInstanceState) {

//        MessagesListAdapter.getInstance(this);
        setContentView(R.layout.activity_main);
//        MessaCheckedState[0] = false;

        MyActivity = this;
        tabController = TabController.getInstance(getApplicationContext());
//        try {

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        MessagesListAdapter.set(this);
        TasksAdapter.set(this);
        MeetingAdapter.set(this);

        Intent in = getIntent();
        if (in != null) {
            if (Intent.ACTION_SEARCH.equals(in.getAction())) {
                String query = getIntent().getStringExtra(SearchManager.QUERY);

//                MessagesListAdapter ma = MessagesListAdapter.getSearchInstance(getApplicationContext(), query.trim());
//                TasksAdapter ta = TasksAdapter.getSearchInstance(getApplicationContext(), query.trim());

                MessageFragment.isSearch = true;
                MessageFragment.query = query.trim();

                TaskFragment.isSearch = true;
                TaskFragment.query = query.trim();

                MeetingFragment.isSearch = true;
                MeetingFragment.query = query.trim();

            } else {


                if (savedInstanceState == null) {
                    if (in != null) {
                        Bundle b = in.getExtras();
                        if (b != null) {

                            if (b.getInt("Type") == 0) {
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
                                        Scroll_Position = 0;
                                        startActivity(showActivity);
                                        overridePendingTransition(0, 0);
                                    }
                            } else if (b.getInt("Type") == 1) {
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
                                Scroll_Position = 0;
//                            Log.i("Task Detail", Tlist.get(position));
//                    finish();
                                startActivity(showActivity);
                                overridePendingTransition(0, 0);
                            } else if (b.getInt("Type") == 2) {
                                Intent showActivity = new Intent(MainActivity.this, Meeting_Detail_Activity.class);
                                Bundle bundle = new Bundle();

                                bundle.putString(getString(R.string.MEETING_ID), b.getString(getString(R.string.MEETING_ID)));
                                bundle.putString(getString(R.string.MEETING_TOPIC), b.getString(getString(R.string.MEETING_TOPIC)));
                                bundle.putString(getString(R.string.MEETING_CREATOR), b.getString(getString(R.string.MEETING_CREATOR)));
                                bundle.putString(getString(R.string.MEETING_ATTENDANCE), b.getString(getString(R.string.MEETING_ATTENDANCE)));
                                bundle.putString(getString(R.string.MEETING_SECRETARY), b.getString(getString(R.string.MEETING_SECRETARY)));
                                bundle.putString(getString(R.string.MEETING_SERVICES), b.getString(getString(R.string.MEETING_SERVICES)));
                                bundle.putString(getString(R.string.MEETING_ROOM), b.getString(getString(R.string.MEETING_ROOM)));
                                bundle.putString(getString(R.string.MEETING_EQUIPMENT), b.getString(getString(R.string.MEETING_EQUIPMENT)));
                                bundle.putString(getString(R.string.MEETING_START_DATE), b.getString(getString(R.string.MEETING_START_DATE)));
                                bundle.putString(getString(R.string.MEETING_END_DATE), b.getString(getString(R.string.MEETING_END_DATE)));
                                bundle.putString(getString(R.string.MEETING_PER_DATE), b.getString(getString(R.string.MEETING_PER_DATE)));
                                bundle.putString(getString(R.string.MEETING_START_TIME), b.getString(getString(R.string.MEETING_START_TIME)));
                                bundle.putString(getString(R.string.MEETING_END_TIME), b.getString(getString(R.string.MEETING_END_TIME)));
                                showActivity.putExtras(bundle);
                                Gone = true;
                                setTab = TabController.Tabs.Task;
                                Scroll_Position = 0;
                                showActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            Log.i("Task Detail", Tlist.get(position));
//                    finish();
                                startActivity(showActivity);
                                overridePendingTransition(0, 0);
                            }
                        }
                    }
                }
            }
        }
        registerReceiver(broadcastReceiver, new IntentFilter("Alarm fire"));


        List<Fragment> fragments = new Vector<>();
        //add Dynamic Tabs here
        fragments.add(Fragment.instantiate(this, MessageFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, TaskFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, MeetingFragment.class.getName()));

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
                switch (tab.getPosition()) {

                    case 2:
                        setTab = TabController.Tabs.Meeting;
                        break;
                    case 1:
                        if (0 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Task;
                        } else if (1 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Meeting;
                        }
                        break;
                    case 0:

                        if (0 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Message;
                        } else if (1 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Task;
                        } else if (2 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Meeting;
                        }
                        break;
                }
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
                    //TODO: check if there is a problem here switching between tabs
                    case 0: {
                        if (sumN > 0) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
//                                        MessagesListAdapter NAda = MessagesListAdapter.getInstance(MyActivity);
                                    AdaptNo = MessagesListAdapter.getInstance();
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

//                                        TasksAdapter TAda = TasksAdapter.getInstance(MyActivity);
                                    AdaptTa = TasksAdapter.getInstance();
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
                switch (tab.getPosition()) {
                    case 2:
                        setTab = TabController.Tabs.Meeting;
                        break;
                    case 1:
                        if (0 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Task;
                        } else if (1 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Meeting;
                        }
                        break;
                    case 0:

                        if (0 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Message;
                        } else if (1 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Task;
                        } else if (2 == findNthIndexOf(Enable, "1", 1)) {
                            setTab = TabController.Tabs.Meeting;
                        }
                        break;
                }
            }
        });

//        } catch (Exception e) {
//            share.SaveError(e.getMessage());
//            Intent SE = new Intent(this, SendError.class);
//            startService(SE);
//        }
        int nt = tabController.GetTabNum(setTab);
        tabLayout.getTabAt(nt).select();
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();

    }


    @Override
    protected void onNewIntent(Intent intent) {

//        int nt = tabController.GetTabNum(setTab);
//        tabLayout.getTabAt(nt).select();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

//            MessagesListAdapter ma = MessagesListAdapter.getSearchInstance(getApplicationContext(), query);
//            TasksAdapter ta = TasksAdapter.getSearchInstance(getApplicationContext(), query);

            MessageFragment.isSearch = true;
            MessageFragment.isFilter = false;
            MessageFragment.query = query.trim();

            TaskFragment.isSearch = true;
            TaskFragment.isFilter = false;
            TaskFragment.query = query.trim();

            MeetingFragment.isSearch = true;
            MeetingFragment.isFilter = false;
            MeetingFragment.query = query.trim();
//            ListView lvm = (ListView) findViewById(R.id.list_messages);
            MessagesListAdapter ma = MessagesListAdapter.getSearchInstance(getApplicationContext(), query.trim());
            TextView tv = (TextView) findViewById(R.id.empty);
            tv.setText("No Search Result");
//            lvm.setEmptyView(tv);
//            lvm.setAdapter(ma);
            ma.notifyDataSetChanged();

//            ListView lvt = (ListView) findViewById(R.id.list_task);
            TasksAdapter ta = TasksAdapter.getSearchInstance(query.trim());
            TextView tv1 = (TextView) findViewById(R.id.empty);
            tv1.setText("No Search Result");
//            lvt.setEmptyView(tv1);
//            lvt.setAdapter(ta);
            ta.notifyDataSetChanged();


            MeetingAdapter mt = MeetingAdapter.getSearchInstance(query.trim());
            TextView tv2 = (TextView) findViewById(R.id.empty);
            tv2.setText("No Search Result");

            mt.notifyDataSetChanged();

        } else {


            Bundle b = intent.getExtras();
            if (b != null) {
//            setContentView(R.layout.activity_main);

                int n = b.getInt("Type");
                switch (n) {
                    case 0:


                        String Title = b.getString(getString(R.string.Title));
                        String Body = b.getString(getString(R.string.Body));


                        MessageFragment.isFilter = true;
                        MessageFragment.isSearch = false;
                        MessageFragment.Title = Title;
                        MessageFragment.Body = Body;

                        setTab = TabController.Tabs.Message;
                        setIntent(null);

                        Handle(null);

//                    ListView lvm = (ListView) findViewById(R.id.list_messages);
//                    MessagesListAdapter ma = MessagesListAdapter.Filter(Topic, Body);
//                MessagesListAdapter ma = MessagesListAdapter.getInstance(getApplicationContext());
//                    TextView tv = (TextView) findViewById(R.id.empty1);
//                    tv.setText("No Result");
//                    lvm.setEmptyView(tv);
//                    lvm.setAdapter(ma);
//                    ma.notifyDataSetChanged();

                        break;
                    case 1:

                        Handle(null);

                        String Subject = b.getString(getString(R.string.Subject));
                        String Description = b.getString(getString(R.string.Description));
                        String Responsible = b.getString(getString(R.string.Responsible));
                        String Creator = b.getString(getString(R.string.Creator));

                        TaskFragment.isFilter = true;
                        TaskFragment.isSearch = false;
                        TaskFragment.Subject = Subject;
                        TaskFragment.Description = Description;
                        TaskFragment.Creator = Creator;
                        TaskFragment.Responsible = Responsible;

                        setTab = TabController.Tabs.Task;
                        setIntent(null);

                        Handle(null);

                        break;

                    case 2:

                        String Date = b.getString(getString(R.string.Date));
                        NameCreator = b.getString(getString(R.string.Creator));
                        String NameSecretary = b.getString(getString(R.string.Secretary));
//                        String Creator = b.getString(getString(R.string.Creator));

                        MeetingFragment.isFilter = true;
                        MeetingFragment.isSearch = false;

                        MeetingFragment.Date = Date;
                        MeetingFragment.NameCreator = NameCreator;
                        MeetingFragment.NameSecretary = NameSecretary;

                        setTab = TabController.Tabs.Meeting;
                        setIntent(null);

                        Handle(null);

                        break;

                }
            }
        }


        super.onNewIntent(intent);
//        setIntent(intent);
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


        if (Gone) {
            NFlag = true;
            TFlag = true;
            ListView lvno = (ListView) findViewById(R.id.list_messages);
            ListView lvta = (ListView) findViewById(R.id.list_task);
            if ((tabLayout.getSelectedTabPosition() == 0) & (lvno != null)) {
//                MessagesListAdapter ad = MessagesListAdapter.getInstance(this);

                if (MessageFragment.isSearch)
                    AdaptNo = MessagesListAdapter.getSearchInstance(getBaseContext(), MessageFragment.query.trim());
                else if (MessageFragment.isFilter)
                    AdaptNo = MessagesListAdapter.Filter(MessageFragment.Title, MessageFragment.Body);
                else
                    AdaptNo = MessagesListAdapter.getInstance();
//                AdaptNo = MessagesListAdapter.getInstance();
                lvno.setAdapter(AdaptNo);
                AdaptNo.notifyDataSetChanged();

                lvno.setSelection(Scroll_Position);
                Gone = false;

            } else if ((tabLayout.getSelectedTabPosition() == 1) & (lvta != null)) {

//                TasksAdapter ad = TasksAdapter.getInstance(this);
                if (TaskFragment.isSearch)
                    AdaptTa = TasksAdapter.getSearchInstance(TaskFragment.query.trim());
                else if (TaskFragment.isFilter)
                    AdaptTa = TasksAdapter.Filter(TaskFragment.Subject, TaskFragment.Description, TaskFragment.Creator, TaskFragment.Responsible);
                else
                    AdaptTa = TasksAdapter.getInstance();
//                AdaptTa = TasksAdapter.getInstance();
                lvta.setAdapter(AdaptTa);

                AdaptTa.notifyDataSetChanged();
                lvta.setSelection(Scroll_Position);
                Gone = false;
            }

            if (item_search != null) {
                item_search.collapseActionView();
                searchView.setQuery("", false);
            }

        } else {
            NFlag = false;
            TFlag = false;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item_read = menu.findItem(R.id.menu_read);
        MenuItem item_delete = menu.findItem(R.id.menu_delete);
        MenuItem item_select = menu.findItem(R.id.menu_select_all);
        item_search = menu.findItem(R.id.action_search);
        item_filter = menu.findItem(R.id.action_filter);
        item_setting = menu.findItem(R.id.action_settings);

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


        if (sum > 0) {
            item_read.setVisible(true);
            item_delete.setVisible(true);
            item_select.setVisible(true);
            item_search.collapseActionView();
            searchView.setIconified(true);
            searchView.clearFocus();
        } else {
            item_read.setVisible(false);
            item_delete.setVisible(false);
            item_select.setVisible(false);
        }
        if (sum == total) {
            item_select.setTitle(getString(R.string.menu_deselect_all));
            item_select.setIcon(R.mipmap.ic_check_box_black_24dp);
        } else if (sum > 0) {
            item_select.setTitle(getString(R.string.menu_select_all));
            item_select.setIcon(R.mipmap.ic_check_box_outline_blank_black_24dp);
        }


        if ((MessageFragment.isFilter | TaskFragment.isFilter | MessageFragment.isFilter) & Filter) {
            item_setting.setVisible(false);
            menu.add(Menu.NONE, 10000, Menu.NONE, "Done")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            Filter = false;
        }

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) item_search.getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), SearchableActivity.class)));

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        if(searchView == null) {
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(true);
//        searchView.setFocusableInTouchMode(true);
        MessagesListAdapter.valid = false;
//        searchView.setFocusable(true);
        searchView.setSubmitButtonEnabled(true);
        MenuItemCompat.setOnActionExpandListener(item_search,
                new MenuItemCompat.OnActionExpandListener() {

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Return true to allow the action view to expand

                        if (First) {
                            searchView.requestFocus();
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            First = false;
                        }
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        // When the action view is collapsed, reset the query
//                        searchView.clearFocus();
//                        searchView.setIconified(true);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchView.getRootView().getWindowToken(), 0);
                        First = true;
//                        TasksAdapter ta = TasksAdapter.getInstance(getApplicationContext());
//                        MessagesListAdapter ma = MessagesListAdapter.getInstance(getApplicationContext());
//                        lvm.setAdapter(ma);
//                        lvt.setAdapter(ta);
//                        ta.notifyDataSetChanged();
//                        ma.notifyDataSetChanged();
//                        onBackPressed();
                        // Return true to allow the action view to collapse
                        return true;
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchView.setIconified(true);
                searchView.clearFocus();
                item_search.collapseActionView();
//                search = true;

                // call the request here
                MessageFragment.isSearch = true;
                MessageFragment.query = query.trim();

                TaskFragment.isSearch = true;
                TaskFragment.query = query.trim();

                MeetingFragment.isSearch = true;
                MeetingFragment.query = query.trim();

//                ListView lvm = (ListView) findViewById(R.id.list_messages);
                MessagesListAdapter ma = MessagesListAdapter.getSearchInstance(getApplicationContext(), query.trim());
//            TextView tv = (TextView) findViewById(R.id.empty1);
//            tv.setText("No Message");
//            lvm.setEmptyView(tv);
//                lvm.setAdapter(ma);
                ma.notifyDataSetChanged();

//                ListView lvt = (ListView) findViewById(R.id.list_task);
                TasksAdapter ta = TasksAdapter.getSearchInstance(query.trim());
//            TextView tv1 = (TextView) findViewById(R.id.empty1);
//            tv1.setText("No Task");
//            lvt.setEmptyView(tv1);
//                lvt.setAdapter(ta);
                ta.notifyDataSetChanged();

//                ListView lmt = (ListView) findViewById(R.id.list_meeting);
                MeetingAdapter mt = MeetingAdapter.getSearchInstance(query.trim());
//                lmt.setAdapter(mt);
                mt.notifyDataSetChanged();


                // call collapse action view on 'MenuItem'
//                item_search.collapseActionView();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        if (MessageFragment.isFilter | TaskFragment.isFilter) {
            item_search.setVisible(false);
            item_filter.setVisible(false);
            searchView.setVisibility(View.GONE);
        }

        return true;
    }

    private void MarkAll() {
        boolean opposite = true;
        searchView.clearFocus();
        item_search.collapseActionView();
        ListView lvno = (ListView) findViewById(R.id.list_messages);
        ListView lvta = (ListView) findViewById(R.id.list_task);
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

            if (MessageFragment.isSearch)
                AdaptNo = MessagesListAdapter.getSearchInstance(getBaseContext(), MessageFragment.query.trim());
            else if (MessageFragment.isFilter)
                AdaptNo = MessagesListAdapter.Filter(MessageFragment.Title.trim(), MessageFragment.Body.trim());
            else
                AdaptNo = MessagesListAdapter.getInstance();

            lvno.setAdapter(AdaptNo);
            AdaptNo.notifyDataSetChanged();

        } else if (tabLayout.getSelectedTabPosition() == 1) {

            for (int i = 0; i < total; i++) {
                TaskCheckedState[i] = opposite;
            }
//            TasksAdapter ad = TasksAdapter.getInstance(this);
            if (TaskFragment.isSearch)
                AdaptTa = TasksAdapter.getSearchInstance(TaskFragment.query.trim());
            else if (TaskFragment.isFilter)
                AdaptTa = TasksAdapter.Filter(TaskFragment.Subject, TaskFragment.Description, TaskFragment.Creator, TaskFragment.Responsible);
            else
                AdaptTa = TasksAdapter.getInstance();

            lvta.setAdapter(AdaptTa);
            AdaptTa.notifyDataSetChanged();
        }

        invalidateOptionsMenu();

    }

    private void MarkDelete() {

        searchView.clearFocus();
//        searchView.onActionViewCollapsed();
//        new Thread(new Runnable() {
//            public void run() {

        ListView lvno = (ListView) findViewById(R.id.list_messages);
        ListView lvta = (ListView) findViewById(R.id.list_task);

        if (tabLayout.getSelectedTabPosition() == 0) {
            for (int i = 0; i < MessaCheckedState.length; i++) {

                if (MessaCheckedState[i]) {
                    Integer ID;
                    ID = MIDList.get(i);
                    getContentResolver().delete(CONTENT_URI1, "_id  = ?", new String[]{String.valueOf(ID)});
                    MessaCheckedState[i] = false;
                }
            }


            if (MessageFragment.isSearch)
                AdaptNo = MessagesListAdapter.getSearchInstance(getBaseContext(), MessageFragment.query.trim());
            else if (MessageFragment.isFilter)
                AdaptNo = MessagesListAdapter.Filter(MessageFragment.Title.trim(), MessageFragment.Body.trim());
            else
                AdaptNo = MessagesListAdapter.getInstance();

            lvno.setAdapter(AdaptNo);
            AdaptNo.notifyDataSetChanged();

        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (int i = 0; i < TaskCheckedState.length; i++) {

                if (TaskCheckedState[i]) {
                    String ID;
                    ID = TIDList.get(i);
                    getContentResolver().delete(CONTENT_URI2, "_id  = ?", new String[]{String.valueOf(ID)});
                    TaskCheckedState[i] = false;
                }
            }


            if (TaskFragment.isSearch)
                AdaptTa = TasksAdapter.getSearchInstance(TaskFragment.query.trim());
            else if (TaskFragment.isFilter)
                AdaptTa = TasksAdapter.Filter(TaskFragment.Subject, TaskFragment.Description, TaskFragment.Creator, TaskFragment.Responsible);
            else
                AdaptTa = TasksAdapter.getInstance();

            lvta.setAdapter(AdaptTa);
            AdaptTa.notifyDataSetChanged();
        }

        invalidateOptionsMenu();
//            }
//        }).start();


    }

    private void MarkRead() {

        searchView.clearFocus();
//        searchView.onActionViewCollapsed();

//        new Thread(new Runnable() {
//            public void run() {

        ListView lvno = (ListView) findViewById(R.id.list_messages);
        ListView lvta = (ListView) findViewById(R.id.list_task);

        if (tabLayout.getSelectedTabPosition() == 0) {
            for (int i = 0; i < MessaCheckedState.length; i++) {

                if (MessaCheckedState[i]) {
                    Integer ID;
                    ID = MIDList.get(i);
                    ContentValues values = new ContentValues();
                    values.put("Seen", true);
                    getContentResolver().update(CONTENT_URI1, values, "_id  = ?", new String[]{String.valueOf(ID)});
                }
            }
            for (int i = 0; i < MessaCheckedState.length; i++) {
                MessaCheckedState[i] = false;
            }


            if (MessageFragment.isSearch)
                AdaptNo = MessagesListAdapter.getSearchInstance(getBaseContext(), MessageFragment.query.trim());
            else if (MessageFragment.isFilter)
                AdaptNo = MessagesListAdapter.Filter(MessageFragment.Title.trim(), MessageFragment.Body.trim());
            else
                AdaptNo = MessagesListAdapter.getInstance();

            lvno.setAdapter(AdaptNo);
            AdaptNo.notifyDataSetChanged();
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            for (int i = 0; i < TaskCheckedState.length; i++) {

                if (TaskCheckedState[i]) {
                    String ID;
                    ID = TIDList.get(i);
                    ContentValues values = new ContentValues();
                    values.put("isSeen", true);
                    getContentResolver().update(CONTENT_URI2, values, "_id  = ?", new String[]{String.valueOf(ID)});
                }
            }
            for (int i = 0; i < TaskCheckedState.length; i++) {
                TaskCheckedState[i] = false;
            }

            if (TaskFragment.isSearch)
                AdaptTa = TasksAdapter.getSearchInstance(TaskFragment.query.trim());
            else if (TaskFragment.isFilter)
                AdaptTa = TasksAdapter.Filter(TaskFragment.Subject, TaskFragment.Description, TaskFragment.Creator, TaskFragment.Responsible);
            else
                AdaptTa = TasksAdapter.getInstance();

            lvta.setAdapter(AdaptTa);
            AdaptTa.notifyDataSetChanged();
        }

        invalidateOptionsMenu();
//            }
//        }).start();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int TabNum = tabLayout.getSelectedTabPosition();

        switch (item.getItemId()) {
            case R.id.menu_delete:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                item_search.collapseActionView();
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
                item_search.collapseActionView();
                MarkRead();
                return true;
            case R.id.menu_select_all:
                item_search.collapseActionView();
                MarkAll();
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Info about MPAS", Toast.LENGTH_SHORT).show();
                return true;
            case 10000:
//                Filter = false;
                if (TabNum == 0) {

                    MessageFragment.isFilter = true;
                    setTab = TabController.Tabs.Message;

                    message = (EditText) findViewById(R.id.titlefilter);
                    body = (EditText) findViewById(R.id.bodyfilter);

                    String title = message.getText().toString();
                    String detail = body.getText().toString();

                    Bundle b = new Bundle();
                    b.putString(getString(R.string.Title), title);
                    b.putString(getString(R.string.Body), detail);
                    b.putInt("Type", 0);

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                } else if (TabNum == 1) {
                    TaskFragment.isFilter = true;
                    setTab = TabController.Tabs.Task;

                    subject = (EditText) findViewById(R.id.titlefilter);
                    description = (EditText) findViewById(R.id.descriptionfilter);
                    AutoCompleteTextView creator = (AutoCompleteTextView) findViewById(R.id.creatorfilter);
                    AutoCompleteTextView responsible = (AutoCompleteTextView) findViewById(R.id.responsiblefilter);


                    Cursor c = getContentResolver().query(CONTENT_URI, null, null, null, null);
                    final String[] Names = new String[c.getCount()];
//                    Responsible = new String[c.getCount()];
                    int index = 0;
                    if (c.moveToFirst()) {
                        do {
                            Names[index] = c.getString(1);
//                            Responsible[index] = c.getString(0);
                            index++;
                        } while (c.moveToNext());
                    }
                    c.close();

                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    responsible.setAdapter(adapter);
                    responsible.setThreshold(1);
                    adapter.notifyDataSetChanged();
                    NameResponsible = "";
                    responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameResponsible = (String) parent.getItemAtPosition(position);
                        }
                    });


                    final ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    creator.setAdapter(ad);
                    creator.setThreshold(1);
                    ad.notifyDataSetChanged();
                    NameCreator = "";
                    creator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameCreator = (String) parent.getItemAtPosition(position);

                        }
                    });

                    String title = subject.getText().toString();
                    String body = description.getText().toString();

                    Bundle b = new Bundle();
                    b.putString(getString(R.string.Subject), title);
                    b.putString(getString(R.string.Description), body);
                    b.putString(getString(R.string.Responsible), NameResponsible);
                    b.putString(getString(R.string.Creator), NameCreator);
                    b.putInt("Type", 1);

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                } else if (TabNum == 2) {
                    MeetingFragment.isFilter = true;
                    setTab = TabController.Tabs.Meeting;

                    Date = (EditText) findViewById(R.id.DateFilter);
//                    Secretary = (EditText) findViewById(R.id.SecretaryFilter);
                    AutoCompleteTextView Creator = (AutoCompleteTextView) findViewById(R.id.CreatorFilter);
                    AutoCompleteTextView Secretary = (AutoCompleteTextView) findViewById(R.id.SecretaryFilter);


                    Cursor c = getContentResolver().query(CONTENT_URI, null, null, null, null);
                    final String[] Names = new String[c.getCount()];
//                    Responsible = new String[c.getCount()];
                    int index = 0;
                    if (c.moveToFirst()) {
                        do {
                            Names[index] = c.getString(1);
//                            Responsible[index] = c.getString(0);
                            index++;
                        } while (c.moveToNext());
                    }
                    c.close();

                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    Creator.setAdapter(adapter);
                    Creator.setThreshold(1);
                    adapter.notifyDataSetChanged();
                    NameCreator = "";
                    Creator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameCreator = (String) parent.getItemAtPosition(position);
                        }
                    });


                    final ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    Secretary.setAdapter(ad);
                    Secretary.setThreshold(1);
                    ad.notifyDataSetChanged();
                    NameSecretary = "";
                    Secretary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameSecretary = (String) parent.getItemAtPosition(position);

                        }
                    });


                    Date.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(MainActivity.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });


                    String DueDate = Date.getText().toString();


                    Bundle b = new Bundle();
                    b.putString(getString(R.string.Date), DueDate);
                    b.putString(getString(R.string.Secretary), NameSecretary);
                    b.putString(getString(R.string.Creator), NameCreator);
                    b.putInt("Type", 2);

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);

                }

                return true;

            case R.id.action_filter:
                item_search.collapseActionView();
                TabNum = tabLayout.getSelectedTabPosition();
                item_filter.setVisible(false);
//                searchView.setEnabled(false);
//                searchView.setVisibility(View.INVISIBLE);
//                invalidateOptionsMenu();
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
                GetUserList userList = new GetUserList(getBaseContext());
                userList.execute();
                if (TabNum == 0) {
                    //filter by message title - body - date
                    MessageFragment.isFilter = true;
                    setTab = TabController.Tabs.Message;
                    setContentView(R.layout.message_filter);
//                    searchView.setVisibility(View.INVISIBLE);
                    message = (EditText) findViewById(R.id.titlefilter);
                    body = (EditText) findViewById(R.id.bodyfilter);

                    Button Filter_Done = (Button) findViewById(R.id.FilterDone);


                    Filter_Done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String title = message.getText().toString();
                            String detail = body.getText().toString();

                            Bundle b = new Bundle();
                            b.putString(getString(R.string.Title), title);
                            b.putString(getString(R.string.Body), detail);
                            b.putInt("Type", 0);

                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });


                } else if (TabNum == 1) {
                    //filter by creator - responsible - title - description
                    TaskFragment.isFilter = true;
                    setTab = TabController.Tabs.Task;
                    setContentView(R.layout.task_filter);
//                    searchView.setVisibility(View.INVISIBLE);
                    subject = (EditText) findViewById(R.id.titlefilter);
                    description = (EditText) findViewById(R.id.descriptionfilter);
                    AutoCompleteTextView creator = (AutoCompleteTextView) findViewById(R.id.creatorfilter);
                    AutoCompleteTextView responsible = (AutoCompleteTextView) findViewById(R.id.responsiblefilter);

                    Button Filter_Done = (Button) findViewById(R.id.FilterDone);


                    Cursor c = getContentResolver().query(CONTENT_URI, null, null, null, null);
                    final String[] Names = new String[c.getCount()];
//                    Responsible = new String[c.getCount()];
                    int index = 0;
                    if (c.moveToFirst()) {
                        do {
                            Names[index] = c.getString(1);
//                            Responsible[index] = c.getString(0);
                            index++;
                        } while (c.moveToNext());
                    }
                    c.close();

                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    responsible.setAdapter(adapter);
                    responsible.setThreshold(1);
                    adapter.notifyDataSetChanged();
                    NameResponsible = "";
                    responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameResponsible = (String) parent.getItemAtPosition(position);
                        }
                    });


                    final ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    creator.setAdapter(ad);
                    creator.setThreshold(1);
                    ad.notifyDataSetChanged();
                    NameCreator = "";
                    creator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameCreator = (String) parent.getItemAtPosition(position);

                        }
                    });

                    Filter_Done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String title = subject.getText().toString();
                            String body = description.getText().toString();

                            Bundle b = new Bundle();
                            b.putString(getString(R.string.Subject), title);
                            b.putString(getString(R.string.Description), body);
                            b.putString(getString(R.string.Responsible), NameResponsible);
                            b.putString(getString(R.string.Creator), NameCreator);
                            b.putInt("Type", 1);

                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });


                } else if (TabNum == 2) {
                    MeetingFragment.isFilter = true;
                    setTab = TabController.Tabs.Meeting;
                    setContentView(R.layout.meeting_filter);
//                    searchView.setVisibility(View.INVISIBLE);
                    Date = (EditText) findViewById(R.id.DateFilter);
//                    description = (EditText) findViewById(R.id.descriptionfilter);
                    AutoCompleteTextView creator = (AutoCompleteTextView) findViewById(R.id.CreatorFilter);
                    AutoCompleteTextView secretary = (AutoCompleteTextView) findViewById(R.id.SecretaryFilter);

                    Button Filter_Done = (Button) findViewById(R.id.Filter);


                    Cursor c = getContentResolver().query(CONTENT_URI, null, null, null, null);
                    final String[] Names = new String[c.getCount()];
//                    Responsible = new String[c.getCount()];
                    int index = 0;
                    if (c.moveToFirst()) {
                        do {
                            Names[index] = c.getString(1);
//                            Responsible[index] = c.getString(0);
                            index++;
                        } while (c.moveToNext());
                    }
                    c.close();

                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    creator.setAdapter(adapter);
                    creator.setThreshold(1);
                    adapter.notifyDataSetChanged();
                    NameCreator = "";
                    creator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameCreator = (String) parent.getItemAtPosition(position);
                        }
                    });


                    final ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Names);
                    secretary.setAdapter(ad);
                    secretary.setThreshold(1);
                    ad.notifyDataSetChanged();
                    NameSecretary = "";
                    secretary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            NameSecretary = (String) parent.getItemAtPosition(position);

                        }
                    });


                    Date.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(MainActivity.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });


                    Filter_Done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String DueDate = Date.getText().toString();
//                            String body = description.getText().toString();

                            Bundle b = new Bundle();
//                            b.putString(getString(R.string.Subject), title);
                            b.putString(getString(R.string.Date), DueDate);
                            b.putString(getString(R.string.Creator), NameCreator);
                            b.putString(getString(R.string.Secretary), NameSecretary);
                            b.putInt("Type", 2);

                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });
                }


                invalidateOptionsMenu();

                return true;
//            case
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
        ListView lvmt = (ListView) findViewById(R.id.list_meeting);


        searchView.clearFocus();
        searchView.setIconified(true);
        item_search.collapseActionView();

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
                if (MessageFragment.isSearch)
                    AdaptNo = MessagesListAdapter.getSearchInstance(getBaseContext(), MessageFragment.query.trim());
                else if (MessageFragment.isFilter)
                    AdaptNo = MessagesListAdapter.Filter(MessageFragment.Title.trim(), MessageFragment.Body.trim());
                else
                    AdaptNo = MessagesListAdapter.getInstance();

                lvno.setAdapter(AdaptNo);
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
//
                if (TaskFragment.isSearch)
                    AdaptTa = TasksAdapter.getSearchInstance(TaskFragment.query.trim());
                else if (TaskFragment.isFilter)
                    AdaptTa = TasksAdapter.Filter(TaskFragment.Subject, TaskFragment.Description, TaskFragment.Creator, TaskFragment.Responsible);
                else
                    AdaptTa = TasksAdapter.getInstance();

                lvta.setAdapter(AdaptTa);
                AdaptTa.notifyDataSetChanged();
            }
        }
//        if ((TaskFragment.isSearch | MessageFragment.isSearch)& search) {
//            searchView.setIconified(false);
//            searchView.clearFocus();
//            item_search.collapseActionView();
//
////            MessagesListAdapter Mad = MessagesListAdapter.getInstance(this);
////            lvno.setAdapter(Mad);
//            AdaptNo = MessagesListAdapter.getInstance(this);
//            lvno.setAdapter(AdaptNo);
//            AdaptNo.notifyDataSetChanged();
//
////            TasksAdapter Tad = TasksAdapter.getInstance(this);
////            lvta.setAdapter(Tad);
//            AdaptTa = TasksAdapter.getInstance(this);
//            lvta.setAdapter(AdaptTa);
//            AdaptTa.notifyDataSetChanged();
//            search = false;
//        } else
        if (TaskFragment.isSearch | MessageFragment.isSearch | MeetingFragment.isSearch) {
            TaskFragment.isSearch = false;
            MessageFragment.isSearch = false;
            MeetingFragment.isSearch = false;
            AdaptNo = MessagesListAdapter.getInstance();
//            lvno.setAdapter(AdaptNo);
            AdaptNo.notifyDataSetChanged();


            AdaptTa = TasksAdapter.getInstance();
//            lvta.setAdapter(AdaptTa);
            AdaptTa.notifyDataSetChanged();


            AdaptMt = MeetingAdapter.getInstance();
//            lvmt.setAdapter(AdaptMt);
            AdaptMt.notifyDataSetChanged();

        } else if (MessageFragment.isFilter | TaskFragment.isFilter | MeetingFragment.isFilter) {

            MessageFragment.isFilter = false;
            TaskFragment.isFilter = false;
            MeetingFragment.isFilter = false;
            Filter = true;
            setIntent(null);
            Handle(null);
        } else if (!fl) {
            super.onBackPressed();
        }

        invalidateOptionsMenu();
        item_search.collapseActionView();
        searchView.clearFocus();

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
