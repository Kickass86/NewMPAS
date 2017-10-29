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
 * Created by ZAMANI on 10/22/2017.
 */

public class MeetingFragment extends Fragment {


    static boolean isSearch;
    static boolean isFilter;
    static String Date = "", NameCreator = "", NameSecretary = "";
    static String query;


    public MeetingFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.meeting_tab, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView list = (ListView) view.findViewById(R.id.list_meeting);

        if (isSearch) {
            MainActivity.AdaptMt = MeetingAdapter.getSearchInstance(query.trim());
        } else if (isFilter) {
            MainActivity.AdaptMt = MeetingAdapter.Filter(NameCreator.trim(), NameSecretary.trim(), Date);
        } else {
            MainActivity.AdaptMt = MeetingAdapter.getInstance();
        }
        TextView tv = (TextView) view.findViewById(R.id.empty);
        tv.setText("No Meeting");
        list.setEmptyView(tv);

        list.setAdapter(MainActivity.AdaptMt);
    }


}
