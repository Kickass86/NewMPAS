package turbotec.newmpas;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    //    private SimpleCursorAdapter m_adapter;
    static final String PROVIDER_NAME = "TURBOTEC.NEWMPAS.MESSAGEPROVIDER";
    static final String URL = "content://" + PROVIDER_NAME + "/messages";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static MainActivity acticity;

    public NotificationFragment() {
        // Required empty public constructor
    }


    static void set(MainActivity mainActivity) {
        acticity = mainActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_tab, container, false);

//        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.notification_tab, container, false);
//        ListView list = (ListView)view.findViewById(R.id.list);



//        m_adapter = new SimpleCursorAdapter(getContext(), R.layout.list_row_layout, null, new String[]{}, new int[]{1}, 0);

//        list.setAdapter(m_adapter);
//        refreshValuesFromContentProvider();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView list = (ListView) view.findViewById(R.id.list_notification);
//        getActivity();
        CustomAdapter.set(acticity);
        CustomAdapter adapter = CustomAdapter.getInstance(getContext());


        TextView tv = (TextView) view.findViewById(R.id.empty1);
        tv.setText("No Message");
        list.setEmptyView(tv);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
    }

//    private void refreshValuesFromContentProvider() {
//        CursorLoader cursorLoader = new CursorLoader(getContext(), CONTENT_URI,
//                null, null, null, null);
//        Cursor c = cursorLoader.loadInBackground();
//        m_adapter.swapCursor(c);
//    }

}
