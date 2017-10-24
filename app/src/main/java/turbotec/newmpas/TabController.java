package turbotec.newmpas;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZAMANI on 10/2/2017.
 */

public class TabController {

    private static TabController instance;
    private SharedPreferenceHandler share;
    private Context context = null;
    private String Enable;

    private TabController(Context act) {
        context = act;
        share = SharedPreferenceHandler.getInstance(act);
        Enable = share.GetTabsControl();
    }

    public static TabController getInstance(Context mContext) {
        if (instance == null) {
            instance = new TabController(mContext);
        }
        return instance;
    }

    public int GetTabNum(Tabs tab) {
        switch (tab) {
            case Message:
                return 0;
            case Task:
                if (0 == findNthIndexOf(Enable, "1", 1)) {
                    return 1;
                } else {
                    return 0;
                }
            case Meeting:
                if ((0 == findNthIndexOf(Enable, "1", 1)) & (1 == findNthIndexOf(Enable, "1", 2))) {
                    return 2;
                } else if ((0 == findNthIndexOf(Enable, "1", 1)) | (1 == findNthIndexOf(Enable, "1", 2))) {
                    return 1;
                } else {
                    return 0;
                }
        }
        return 0;
    }

    public int findNthIndexOf(String str, String needle, int occurence)
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


    public enum Tabs {
        Message, Task, Meeting
    }
}
