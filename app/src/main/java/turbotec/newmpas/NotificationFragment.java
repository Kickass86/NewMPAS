package turbotec.newmpas;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    private SimpleCursorAdapter m_adapter;
    static final String PROVIDER_NAME = "TURBOTEC.NEWMPAS.MESSAGEPROVIDER";
    static final String URL = "content://" + PROVIDER_NAME + "/messages";
    static final Uri CONTENT_URI = Uri.parse(URL);

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_row_layout, container, false);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.messages_layout, container, false);
//        ListView list = (ListView)view.findViewById(R.id.list);

        TextView tv = (TextView) view.findViewById(R.id.empty);
        tv.setText("No Message");
//        list.setEmptyView(tv);

        m_adapter = new SimpleCursorAdapter(getContext(), R.layout.list_row_layout, null, new String[]{}, new int[]{}, 0);

//        list.setAdapter(m_adapter);
        refreshValuesFromContentProvider();

        return rootView;
    }

    private void refreshValuesFromContentProvider() {
        CursorLoader cursorLoader = new CursorLoader(getContext(), CONTENT_URI,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        m_adapter.swapCursor(c);
    }

}