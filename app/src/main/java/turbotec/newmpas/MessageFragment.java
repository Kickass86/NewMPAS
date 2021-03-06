package turbotec.newmpas;


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
public class MessageFragment extends Fragment {


    //    private SimpleCursorAdapter m_adapter;
//    static final String PROVIDER_NAME = "TURBOTEC.NEWMPAS.MESSAGEPROVIDER";
//    static final String URL = "content://" + PROVIDER_NAME + "/messages";
//    static final Uri CONTENT_URI = Uri.parse(URL);
    static boolean isSearch;
    static boolean isFilter;
    static String Title = "", Body = "";
    static String query;

//    static MainActivity acticity;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


//    static void set(MainActivity mainActivity) {
//        acticity = mainActivity;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_tab, container, false);

//        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.notification_tab, container, false);
//        ListView list = (ListView)view.findViewById(R.id.list);


//        m_adapter = new SimpleCursorAdapter(getContext(), R.layout.list_row_layout_message, null, new String[]{}, new int[]{1}, 0);

//        list.setAdapter(m_adapter);
//        refreshValuesFromContentProvider();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView list = (ListView) view.findViewById(R.id.list_messages);
//        list.setScrollingCacheEnabled(false);
//        getActivity();
//        MessagesListAdapter.set(acticity);
//        MessagesListAdapter adapter = MessagesListAdapter.getInstance(getContext());
        if (isSearch) {
            MainActivity.AdaptNo = MessagesListAdapter.getSearchInstance(getContext(), query.trim());
        } else if (isFilter) {
            MainActivity.AdaptNo = MessagesListAdapter.Filter(Title.trim(), Body.trim());
        } else {
            MainActivity.AdaptNo = MessagesListAdapter.getInstance();
        }
        TextView tv = (TextView) view.findViewById(R.id.empty);
        tv.setText("No Message");
        list.setEmptyView(tv);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        list.setAdapter(adapter);
        list.setAdapter(MainActivity.AdaptNo);
    }

//    private void refreshValuesFromContentProvider() {
//        CursorLoader cursorLoader = new CursorLoader(getContext(), CONTENT_URI1,
//                null, null, null, null);
//        Cursor c = cursorLoader.loadInBackground();
//        m_adapter.swapCursor(c);
//    }

}
