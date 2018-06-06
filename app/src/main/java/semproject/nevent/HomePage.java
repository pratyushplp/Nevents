package semproject.nevent;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;
import semproject.nevent.MapsActivities.ShowEvents;
import semproject.nevent.Request.AttendingEventRequest;
import semproject.nevent.Request.CheckinvitationsRequest;
import semproject.nevent.Request.RecyclerRequest;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener{
    final String STRING_TAG= "HomePage";
    private int count = 1;
    NavigationView navigationView=null;
    Toolbar toolbar=null;
    String username;
    Context context;
    int id;
    static public EventRecyclerView staticeventRecyclerView = new EventRecyclerView();
    static EventRecyclerView stat_forsearch_eventRecyclerView = new EventRecyclerView();
    static EventRecyclerView stat_forinvite_eventRecyclerView = new EventRecyclerView();

    static public EventRecyclerView.AllItemAdapter staticadapter=new EventRecyclerView.AllItemAdapter();
    static EventRecyclerView.FollowItemAdapter stat_forsearch_Useradapter=new EventRecyclerView.FollowItemAdapter();
    //static EventRecyclerView.FollowItemAdapter stat_forinvite_Useradapter=new EventRecyclerView.FollowItemAdapter();


    List<String>search_userid=new ArrayList<>();
    List<String>search_username=new ArrayList<>();
    List<String>search_useremail=new ArrayList<>();

    List<String>invite_userid=new ArrayList<>();
    List<String>invite_username=new ArrayList<>();
    List<String>invite_useremail=new ArrayList<>();

    AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    MenuItem fb_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        staticeventRecyclerView = new EventRecyclerView();
        stat_forsearch_eventRecyclerView = new EventRecyclerView();
        staticadapter=new EventRecyclerView.AllItemAdapter();
        stat_forsearch_Useradapter=new EventRecyclerView.FollowItemAdapter();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        id=intent.getIntExtra("id",3);
        Button userbutton=(Button)findViewById(R.id.user_button);
        userbutton.setText(username);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        listenerFunction(username,search_userid,search_username,search_useremail,true);
        if (id==1)
        {
            listenerFunction(username,invite_userid,invite_username,invite_useremail,false);
            Recent recent = new Recent();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putInt("id",id);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, recent);
            recent.setArguments(bundle);
            fragmentTransaction.commit();
        }
        else if(id==2)
        {
            NearByList nearByList = new NearByList();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, nearByList);
            nearByList.setArguments(bundle);
            fragmentTransaction.commit();
        }
         toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayShowTitleEnabled(false);
         toolbar.setLogo(R.drawable.logo);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed(); //use property of super class - Appcompat actvity
        }
    }

    @Override //http://stackoverflow.com/questions/31231609/creating-a-button-in-android-toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
        new Checkinvitations(username,menu).execute();
        fb_icon=menu.findItem(R.id.action_facebook);
        updateWithToken(AccessToken.getCurrentAccessToken());
/*        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.support.v4.app.FragmentTransaction fragmentTransaction;
        switch (item.getItemId()){
            case R.id.action_location:
                ShowEvents showEvents=new ShowEvents();
                Log.e("HOMEPAGE","showEvents");
                Intent intent=new Intent(this, ShowEvents.class);
                intent.putExtra("username",username);
                finish();
                startActivity(intent);
                break;

            case R.id.action_search:
                Log.e("Searching","action_search");
                Intent searchintent=new Intent(this, SearchResultActivity.class);
                searchintent.putExtra("username",username);
                startActivity(searchintent);
                break;

            case R.id.action_invitation:
                setinviteseen(username,item);
                Bundle bundle=new Bundle();
                bundle.putString("username", username);
                Invite invite=new Invite();
                fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, invite);
                invite.setArguments(bundle);
                fragmentTransaction.commit();
                break;

            case R.id.action_facebook:
                Bundle bundlefb=new Bundle();
                Log.e("HOMEPAGE","facebookclicked");
                bundlefb.putString("username", username);
                FacebookEvents facebookEvents=new FacebookEvents();
                fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, facebookEvents);
                facebookEvents.setArguments(bundlefb);
                fragmentTransaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Button all = (Button) findViewById(R.id.button4);


        Button user = (Button) findViewById(R.id.user_button);

        Button trend = (Button) findViewById(R.id.button5);

        Button feeds = (Button) findViewById(R.id.button6);

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        if (id == R.id.nav_sports) {

            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);


            Sports sports=new Sports();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, sports);
            sports.setArguments(bundle);
            fragmentTransaction.commit();

            // Handle the camera action
        } else if (id == R.id.nav_parties) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Parties parties=new Parties();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, parties);
            parties.setArguments(bundle);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_conferences) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Conference conference=new Conference();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, conference);
            conference.setArguments(bundle);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_donations) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Donations donations=new Donations();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, donations);
            donations.setArguments(bundle);
            fragmentTransaction.commit();


        }

        else if (id == R.id.nav_others) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Others others=new Others();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, others);
            others.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_business) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Business business=new Business();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, business);
            business.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_concert) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Concert concert=new Concert();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, concert);
            concert.setArguments(bundle);
            fragmentTransaction.commit();


        }

        else if (id == R.id.nav_educational) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Educational educational=new Educational();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, educational);
            educational.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_exhibition) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Exhibition exhibition=new Exhibition();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, exhibition);
            exhibition.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_gaming) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            feeds.setBackgroundResource(R.drawable.cdefault);

            Gaming gaming=new Gaming();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, gaming);
            gaming.setArguments(bundle);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if(currentAccessToken!=null){
            fb_icon.setVisible(true);
            Log.e("Facebook","Loggedin");
        }
        else{
            fb_icon.setVisible(false);
            Log.e("Facebook","Not logged in");
        }
    }

    public void feed(View view)
    {
        Button user = (Button) findViewById(R.id.user_button);
        user.setBackgroundResource(R.drawable.cdefault);

        Button trend = (Button) findViewById(R.id.button5);
        trend.setBackgroundResource(R.drawable.cdefault);

        Button all = (Button) findViewById(R.id.button4);
        all.setBackgroundResource(R.drawable.cdefault);

        Button feeds = (Button) findViewById(R.id.button6);
        feeds.setBackgroundResource(R.drawable.shape3);
        if(checkConnection(this))
        {
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            Feed feed=new Feed();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, feed);
            feed.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }

    public void trending(View view) {
        Button all = (Button) findViewById(R.id.button4);
        all.setBackgroundResource(R.drawable.cdefault);

        Button user = (Button) findViewById(R.id.user_button);
        user.setBackgroundResource(R.drawable.cdefault);

        Button feeds = (Button) findViewById(R.id.button6);
        feeds.setBackgroundResource(R.drawable.cdefault);

        Button trend = (Button) findViewById(R.id.button5);
        trend.setBackgroundResource(R.drawable.shape3);
        if(checkConnection(this)){
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            Trending trending=new Trending();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, trending);
            trending.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }

    public void recent(View view) {

        Button user = (Button) findViewById(R.id.user_button);
        user.setBackgroundResource(R.drawable.cdefault);

        Button trend = (Button) findViewById(R.id.button5);
        trend.setBackgroundResource(R.drawable.cdefault);

        Button feeds = (Button) findViewById(R.id.button6);
        feeds.setBackgroundResource(R.drawable.cdefault);


        Button all = (Button) findViewById(R.id.button4);
        all.setBackgroundResource(R.drawable.shape3);
        if(checkConnection(this)){
            Recent recent=new Recent();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, recent);
            recent.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }

    public void addevents(View view) {
        if(checkConnection(this)){
            Intent i=new Intent(this, Upload.class);
            i.putExtra("username",username);
            finish();
            startActivity(i);
        }
    }

   /* public void check(View view)
    {
        if(checkConnection(this)){
            Bundle bundle = new Bundle();
            bundle.putString("otherusername", "Sagun");
            bundle.putString("username", username);
            Otherusersprofile userDetail=new Otherusersprofile();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, userDetail);
            userDetail.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }*/

    public void userdetails(View view){
        Button all = (Button) findViewById(R.id.button4);
        all.setBackgroundResource(R.drawable.cdefault);

        Button trend = (Button) findViewById(R.id.button5);
        trend.setBackgroundResource(R.drawable.cdefault);

        Button user = (Button) findViewById(R.id.user_button);
        user.setBackgroundResource(R.drawable.shape3);

        if(checkConnection(this)){
            /*Intent intent = new Intent(this, UserDetails.class);
            intent.putExtra("username",username);
            startActivity(intent);
            finish();*/

            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            Userdetail userDetails=new Userdetail();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, userDetails);
            userDetails.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }

     public void retreiveFromDatabase(Context context,Boolean isforsearch){
        Log.e(STRING_TAG,"database");
        if(checkConnection(this)){
            if(isforsearch) {
                for (int i = 0; i < search_userid.size(); i++) {
                    Log.i("Value ofselement " + i, search_username.get(i));
                    stat_forsearch_eventRecyclerView.initializeDataFollow(search_userid.get(i), search_username.get(i), search_useremail.get(i), context);
                    stat_forsearch_Useradapter = new EventRecyclerView.FollowItemAdapter(context, stat_forsearch_eventRecyclerView.getItemFollow(), username, false);
                   /* mRecyclerView.setAdapter(staticadapter);*/
                }
            }
            else {
                for (int i = 0; i < invite_userid.size(); i++) {
                    Log.i("Value of homeelement " + i, invite_username.get(i));
                    stat_forinvite_eventRecyclerView.initializeDataFollow(invite_userid.get(i), invite_username.get(i), invite_useremail.get(i), context);
                    //stat_forinvite_Useradapter = new EventRecyclerView.FollowItemAdapter(context, stat_forsearch_eventRecyclerView.getItemFollow(), username, false);
                    //mRecyclerView.setAdapter(staticadapter);
                }
            }

        }

    }

    public void listenerFunction(String username, final List<String>userid, final List<String>fusername, final List<String>useremail, final Boolean isforsearch){
        Log.e(STRING_TAG,"insideListiner");
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("userid");

                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Log.e(STRING_TAG,"insideSuccess");
                        //JSONArray jsonArray = jsonObject.getJSONArray("userid");
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

                            retreiveFromDatabase(HomePage.this,isforsearch);
                        }
                        else
                            Log.e(STRING_TAG,"insideNull");

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(HomePage.this);
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
            if(isforsearch){
                RecyclerRequest recyclerRequest=new RecyclerRequest(username,"alluser", responseListener);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(recyclerRequest);
            }
            else{
                stat_forinvite_eventRecyclerView=new EventRecyclerView();
                RecyclerRequest recyclerRequest=new RecyclerRequest(username,"followuser", responseListener);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(recyclerRequest);
            }
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


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }


    private class Checkinvitations extends AsyncTask<Void, Void, Bitmap> {
        private final Menu menu;
        String name;

        public Checkinvitations(String name, Menu menu) {
            this.name = name;
            this.menu = menu;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {

                Response.Listener<String> responselistener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(STRING_TAG, "going pressed");
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            int count = jsonObject.getInt("count");
                            if (success) {
                                Log.d("CHEck", "going button pressed"+count);
                                MenuItem menuItem = menu.findItem(R.id.action_invitation);
                                menuItem.setIcon(buildCounterDrawable(count, R.drawable.invites));

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
                                builder.setMessage("Connection Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                if (checkConnection(HomePage.this)) {
                    CheckinvitationsRequest checkinvitationsrequest = new CheckinvitationsRequest(name, "checkinvitation", responselistener);
                    RequestQueue queue = Volley.newRequestQueue(HomePage.this);
                    queue.add(checkinvitationsrequest);
                }
            } catch (Exception e) {
                e.printStackTrace();}
                return null;



    }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    public void setinviteseen(String username, final MenuItem item)
    {
        Response.Listener<String> responselistener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG, "going pressed");
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success)
                    {
                        item.setIcon(buildCounterDrawable(0, R.drawable.invites));

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
                        builder.setMessage("Connection Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        if (checkConnection(HomePage.this)) {
            CheckinvitationsRequest checkinvitationsrequest = new CheckinvitationsRequest(username, "setinviteseen", responselistener);
            RequestQueue queue = Volley.newRequestQueue(HomePage.this);
            queue.add(checkinvitationsrequest);
        }
    }

}
