package turbotec.newmpas;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class SearchableActivity extends AppCompatActivity {

    static int TAB = 0;
    SearchView searchView;

    @Override
    public void onBackPressed() {

        MainActivity.Gone = true;
        invalidateOptionsMenu();
        searchView.onActionViewCollapsed();
        searchView.clearFocus();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_searchable);
        onNewIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
//        thisAct = this;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//            createList(query);
            switch (TAB) {
                case 0:

                    setContentView(R.layout.activity_searchable);
                    ListView lvm = (ListView) findViewById(R.id.list);
                    MessagesListAdapter ma = MessagesListAdapter.getSearchInstance(getApplicationContext(), query.trim());
                    TextView tv = (TextView) findViewById(R.id.empty1);
                    tv.setText("No Message");
                    lvm.setEmptyView(tv);
                    lvm.setAdapter(ma);
                    ma.notifyDataSetChanged();
                    break;

                case 1:
                    setContentView(R.layout.activity_searchable);
                    ListView lvt = (ListView) findViewById(R.id.list);
                    TasksAdapter ta = TasksAdapter.getSearchInstance(query.trim());
                    TextView tv1 = (TextView) findViewById(R.id.empty1);
                    tv1.setText("No Task");
                    lvt.setEmptyView(tv1);
                    lvt.setAdapter(ta);
                    ta.notifyDataSetChanged();
                    break;
            }
        }
//	      Toast.makeText(this.getApplicationContext(),"zzzzzzzzzzzzzzzzzzzzzzzzzzzzz" ,Toast.LENGTH_LONG).show();

        setIntent(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

//        MenuItem dsk = menu.findItem(R.id.action_search);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
//            case R.id.menu_delete:
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//
//                alertDialogBuilder.setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                MarkDelete();
//
//                            }
//                        })
//                        .setNegativeButton("No",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                        dialog.cancel();
//
//                                    }
//                                })
//                        .setMessage(R.string.dialog_message)
//                        .setTitle(R.string.Delete_Button);
//                AlertDialog adialog = alertDialogBuilder.create();
//                adialog.show();
//                return true;
//
//            case R.id.menu_read:
//                MarkRead();
//                return true;
//            case R.id.menu_select_all:
//                MarkAll();
//                return true;
//            case R.id.action_settings:
//                Toast.makeText(this, "Info about MPAS", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchableActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
//                SearchableActivity.TAB = tabLayout.getSelectedTabPosition();
                this.startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


}
