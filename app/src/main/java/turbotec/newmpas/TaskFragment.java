package turbotec.newmpas;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {

    static boolean isSearch;
    static boolean isFilter;
    static String query;
    static String Subject = "", Description = "", Creator = "", Responsible = "";
    FloatingActionButton b;
    View rootView;


    public TaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tasks_tab, container, false);

//        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.notification_tab, container, false);
//        ListView list = (ListView)view.findViewById(R.id.list);
//        ListView list = (ListView) view.findViewById(R.id.list_task);

//        MessagesListAdapter adapter = MessagesListAdapter.getInstance();
//        list.setAdapter(adapter);

//        tv.setText("No Task");
//        list.setEmptyView(tv);
//        m_adapter = new SimpleCursorAdapter(getContext(), R.layout.list_row_layout_message, null, new String[]{}, new int[]{1}, 0);

//        ViewGroup rootView = (ViewGroup) inflater.inflate(
//                R.layout.fragment_screen_slide_page, container, false);

//        TextView textView = (TextView) rootView.findViewById(R.id.Topic);
//        textView.setText(R.string.hello_blank_fragment);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetUserList userList = new GetUserList(getContext());
        userList.execute();

        b = (FloatingActionButton) view.findViewById(R.id.fab);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(getContext(), AddActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                MainActivity.setTab = TabController.Tabs.Task;
                MainActivity.Gone = true;


            }
        });


//        b.setOnTouchListener(new View.OnTouchListener() {
//
//            float x, y;
//            float[] Outlocation = new float[2];
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//
////                    case MotionEvent.ACTION_MOVE:
////
////                        b.setX(b.getRawX() + (event.getRawX() - x));
////                        b.setY(b.getRawY() + (event.getRawY() - y));
////                        return true;
//                    case MotionEvent.ACTION_MOVE:
//
//                        float new_x = event.getRawX() ;
//                        float new_y = event.getRawY();
//
//
////                        v.getLocationOnScreen(Outlocation);
////                        DisplayMetrics metrics = getResources().getDisplayMetrics();
////                        Outlocation[0] = metrics.widthPixels;
////                        Outlocation[1] = metrics.heightPixels;
//
//                        if (new_x > Outlocation[0]) {
//                            new_x =  x ;
////                            new_x = x;
//                        } else if (new_x < 0) {
//                            new_x = x;
////                            new_x = -x;
//                        }
//                        if (new_y > Outlocation[1]) {
//                            new_y = y;
////                            new_y = y;
//                        } else if (new_y < 0) {
//                            new_y = y;
////                            new_y = -y;
//                        }
//                        b.setX(b.getX() + (new_x - x));
//                        b.setY(b.getY() + (new_y - y));
//                        return true;
//
//
//                    case MotionEvent.ACTION_DOWN:
//                        x = event.getRawX();
//                        y = event.getRawY();
//                        Outlocation[0] = x;
//                        Outlocation[1] = y;
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        if(x == event.getRawX() & (y == event.getRawY())) {
//                            Intent intent = new Intent(getContext(), AddActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            MainActivity.Gone = true;
//                            MainActivity.setTab = 1;
//                            return true;
//                        }
//                }
//
//                return false;
//            }
//        });


//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        final int windowWidth = metrics.widthPixels;
//        final int windowHeight = metrics.heightPixels;
//        b.setOnTouchListener(new View.OnTouchListener() {
//
//            float x, y;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                final int x = (int) event.getRawX();
//                final int y = (int) event.getRawY();
//
//                float newX, newY;
//                int lastAction = 0;
//
//                switch (event.getActionMasked()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        dX = x - b.getRawX();
//                        dY = y - b.getRawY();
//                        lastAction = MotionEvent.ACTION_DOWN;
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//
//                        newX = event.getRawX() + dX;
//                        newY = event.getRawY() + dY;
//
//                        if ( (newX <= 0 || newX >= windowWidth) ||
//                                (newY <= 0 || newY >= windowHeight) )
//                            break;
//
//                        b.setX(newX);
//                        b.setY(newY);
//
//                        lastAction = MotionEvent.ACTION_MOVE;
//
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        if (lastAction == MotionEvent.ACTION_DOWN)
////                            Toast.makeText(DraggableView.this, "Clicked!", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    default:
//                        return false;
//                }
//                return true;
//            }
//        });

//        ListView list = (ListView) view.findViewById(R.id.list_task);
//
//        TextView tv = (TextView) view.findViewById(R.id.empty2);
//        tv.setText("No Tasks");
//        list.setEmptyView(tv);
        ListView list = (ListView) view.findViewById(R.id.list_task);
//        getActivity();
//        MessagesListAdapter.set(acticity);
//        TasksAdapter adapter = TasksAdapter.getInstance(getContext());
        if (isSearch) {
            MainActivity.AdaptTa = TasksAdapter.getSearchInstance(query.trim());
            b.setVisibility(View.GONE);
        } else if (isFilter) {
            MainActivity.AdaptTa = TasksAdapter.Filter(Subject, Description, Creator, Responsible);
        } else {
            MainActivity.AdaptTa = TasksAdapter.getInstance();
        }

        TextView tv = (TextView) view.findViewById(R.id.empty);
        tv.setText("No Task");
        list.setEmptyView(tv);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        list.setAdapter(adapter);
        list.setAdapter(MainActivity.AdaptTa);

//        MessagesListAdapter adapter = MessagesListAdapter.getInstance();
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        list.setAdapter(adapter);
    }

}
