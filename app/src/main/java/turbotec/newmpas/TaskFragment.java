package turbotec.newmpas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
        View rootView = inflater.inflate(R.layout.list_row_layout, container, false);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.messages_layout, container, false);
//        ListView list = (ListView)view.findViewById(R.id.list);
        TextView tv = (TextView) view.findViewById(R.id.empty);
        tv.setText("No Task");
//        list.setEmptyView(tv);

//        ViewGroup rootView = (ViewGroup) inflater.inflate(
//                R.layout.fragment_screen_slide_page, container, false);

//        TextView textView = (TextView) rootView.findViewById(R.id.Title);
//        textView.setText(R.string.hello_blank_fragment);

        return rootView;
    }

}
