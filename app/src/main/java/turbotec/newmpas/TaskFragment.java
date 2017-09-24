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

    FloatingActionButton b;



    public TaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tasks_tab, container, false);

//        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.notification_tab, container, false);
//        ListView list = (ListView)view.findViewById(R.id.list);
//        ListView list = (ListView) view.findViewById(R.id.list_task);

//        NotificationsAdapter adapter = NotificationsAdapter.getInstance();
//        list.setAdapter(adapter);

//        tv.setText("No Task");
//        list.setEmptyView(tv);
//        m_adapter = new SimpleCursorAdapter(getContext(), R.layout.list_row_layout_message, null, new String[]{}, new int[]{1}, 0);

//        ViewGroup rootView = (ViewGroup) inflater.inflate(
//                R.layout.fragment_screen_slide_page, container, false);

//        TextView textView = (TextView) rootView.findViewById(R.id.Title);
//        textView.setText(R.string.hello_blank_fragment);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        b = (FloatingActionButton) view.findViewById(R.id.fab);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetUserList userList = new GetUserList(getContext());
                userList.execute();

                Intent intent = new Intent(getContext(), AddActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
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
//                        dX = x - b.getX();
//                        dY = y - b.getY();
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
//        NotificationsAdapter.set(acticity);
//        TasksAdapter adapter = TasksAdapter.getInstance(getContext());
        MainActivity.AdaptTa = TasksAdapter.getInstance(getContext());


        TextView tv = (TextView) view.findViewById(R.id.tempty);
        tv.setText("No Task");
        list.setEmptyView(tv);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        list.setAdapter(adapter);
        list.setAdapter(MainActivity.AdaptTa);

//        NotificationsAdapter adapter = NotificationsAdapter.getInstance();
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        list.setAdapter(adapter);
    }

}
