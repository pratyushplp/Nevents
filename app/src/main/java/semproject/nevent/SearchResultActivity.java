package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;
import semproject.nevent.Request.RecyclerRequest;

public class SearchResultActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,SearchView.OnQueryTextListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    final String STRING_TAG= "SearchResultActivity";
    static EventRecyclerView staticeventRecyclerView = new EventRecyclerView();
    static EventRecyclerView.AllItemAdapter staticadapter=new EventRecyclerView.AllItemAdapter();
    static String searchusername;
    List<String>userid=new ArrayList<>();
    List<String>fusername=new ArrayList<>();
    List<String>useremail=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        searchusername = intent.getStringExtra("username");
        //listenerFunction(username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_result, menu);
        SearchView searchView =(SearchView) menu.findItem(R.id.searchresult_search).getActionView();
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this,HomePage.class);
                upIntent.putExtra("username",searchusername);
                upIntent.putExtra("id",1);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchEvent) {
/*
        EventRecyclerView.AllItemAdapter searchadapter=new EventRecyclerView.AllItemAdapter();
        List<EventRecyclerView.Item> extractedItem=staticextratedeventRecyclerView.getItem();
        final List<EventRecyclerView.Item> searchItem=new ArrayList<>();
        if(extractedItem.isEmpty())
            Log.e(STRING_TAG,"empty");
        else
            Log.e(STRING_TAG,"is not empty");
        for(EventRecyclerView.Item indevent: extractedItem){
            String eventname=indevent.eventLabel;
            Log.i(STRING_TAG,searchEvent);
            if(eventname.contains(searchEvent)){
                searchItem.add(indevent);
                Log.i(STRING_TAG,searchEvent);
            }
        }

        searchadapter.setFilter(searchItem);
*/
        Log.e(STRING_TAG,searchEvent);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        Log.e(STRING_TAG,fragment.toString());

        if (mViewPager.getCurrentItem() == 0 && fragment != null) {
            ((Tab1Event)fragment).showResult(searchEvent);
            ((Tab1Event)fragment).showResultTab2(searchEvent);
        }
        else if (mViewPager.getCurrentItem() == 1 && fragment != null){
            ((Tab2User)fragment).showResult(searchEvent);
            ((Tab2User)fragment).showResulttab1(searchEvent);
        }

        return true;
    }

    public void retreiveFromDatabase(Context context){
        Log.e(STRING_TAG,"database");
        if(checkConnection(getApplicationContext())){
            for (int i=0;i < userid.size();i++)
            {
                Log.i("Value of element "+i,fusername.get(i));
                staticeventRecyclerView.initializeDataFollow(userid.get(i),fusername.get(i),useremail.get(i),context);
                /*staticadapter = new EventRecyclerView.FollowItemAdapter(context, staticeventRecyclerView.getItemFollow());
                mRecyclerView.setAdapter(staticadapter);*/
            }
        }

    }

    public void listenerFunction(String username){
        Log.e(STRING_TAG,"insideListiner");
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Log.e(STRING_TAG,"insideSuccess");
                        JSONArray jsonArray = jsonObject.getJSONArray("userid");
                        Log.e(STRING_TAG,"userid");
                        JSONArray jsonArray2 = jsonObject.getJSONArray("username");
                        Log.e(STRING_TAG,"username");
                        JSONArray jsonArray3 = jsonObject.getJSONArray("email");
                        Log.e(STRING_TAG,"email");

                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            Log.e(STRING_TAG,Integer.toString(len));

                            //for userid
                            for (int i=0;i<len;i++){
                                userid.add(jsonArray.get(i).toString());
                            }
                            //for username
                            for (int i=0;i<len;i++){
                                fusername.add(jsonArray2.get(i).toString());
                            }
                            //for email
                            for (int i=0;i<len;i++){
                                useremail.add(jsonArray3.get(i).toString());
                            }

                            retreiveFromDatabase(getApplicationContext());
                        }
                        else
                            Log.e(STRING_TAG,"insideNull");

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(SearchResultActivity.this);
                        builder.setMessage("Connection Failed")
                                .setNegativeButton("Retry",null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        if(checkConnection(this)){
            RecyclerRequest recyclerRequest=new RecyclerRequest(username,"alluser", responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(recyclerRequest);
        }
    }

    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(this,InternetConnection.class);
            finish();
            startActivity(intent);
        }
        return isConnected;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);
        }
        else{
            Intent intent= new Intent(this,InternetConnection.class);
            finish();
            startActivity(intent);

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).\
            switch (position) {
                case 0:
                    Tab1Event event= new Tab1Event();
                    return event;
                case 1:
                    Tab2User user= new Tab2User();
                    return user;
                default:
                    Tab1Event event1= new Tab1Event();
                    return event1;
            }
        }

        @Override
        public int getCount(){
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Events";
                case 1:
                    return "Users";
            }
            return null;
        }

        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }
    }

}
