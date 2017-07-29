package turbotec.newmpas;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {


    public TaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_tab, container, false);

//        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.notification_tab, container, false);
//        ListView list = (ListView)view.findViewById(R.id.list);
//        ListView list = (ListView) view.findViewById(R.id.list_task);

//        CustomAdapter adapter = CustomAdapter.getInstance();
//        list.setAdapter(adapter);

//        tv.setText("No Task");
//        list.setEmptyView(tv);
//        m_adapter = new SimpleCursorAdapter(getContext(), R.layout.list_row_layout, null, new String[]{}, new int[]{1}, 0);

//        ViewGroup rootView = (ViewGroup) inflater.inflate(
//                R.layout.fragment_screen_slide_page, container, false);

//        TextView textView = (TextView) rootView.findViewById(R.id.Title);
//        textView.setText(R.string.hello_blank_fragment);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ListView list = (ListView) view.findViewById(R.id.list_task);
//
//        TextView tv = (TextView) view.findViewById(R.id.empty2);
//        tv.setText("No Tasks");
//        list.setEmptyView(tv);

//        CustomAdapter adapter = CustomAdapter.getInstance();
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        list.setAdapter(adapter);
    }

}
