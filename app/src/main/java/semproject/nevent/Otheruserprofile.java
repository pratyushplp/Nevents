package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;
import semproject.nevent.Request.FollowRequest;
import semproject.nevent.Request.RecyclerRequest;
import semproject.nevent.Request.TryforotheruserRequest;

public class Otheruserprofile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    String STRING_TAG="OtherUserdetail";
    private RecyclerView mRecyclerView;
    private static final String SERVER_ADDRESS="http://avsadh96.000webhostapp.com/";
    ImageView downloadedimage;
    List<String> eventList=new ArrayList<>();
    List<String>eventLocation=new ArrayList<>();
    List<String>eventDate=new ArrayList<>();
    List<String>eventCategory=new ArrayList<>();
    List<String>eventId=new ArrayList<>();
    List<Integer>viewcount=new ArrayList<>();
    TextView user_name, followers, following;
    Button followbutton;
    String username;
    String otherusername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otheruserprofile);
        Intent intent = getIntent();
        otherusername = intent.getStringExtra("otherusername");
        username=intent.getStringExtra("username");

        user_name=(TextView) findViewById(R.id.otherusername);
        followers=(TextView) findViewById(R.id.followers);
        following=(TextView) findViewById(R.id.following);
        user_name.setText(otherusername);
        followbutton=(Button) findViewById(R.id.followbutton);
        downloadedimage=(ImageView) findViewById(R.id.otheruserprofilepicture);
        new Downloadimage(otherusername).execute();

        // For checking if the user is already followed.
        Response.Listener<String> responselistener= new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {

                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    boolean alreadyfollowing= jsonObject.getBoolean("alreadyfollowing");
                    String countfollow= Integer.toString(jsonObject.getInt("countfollow"));
                    String countfollowers= Integer.toString(jsonObject.getInt("countfollowers"));
                    if(success)
                    {
                        if(alreadyfollowing)
                        {
                            followbutton.setVisibility(View.GONE);
                        }

                        following.setText(countfollow+ "\nFollowing");
                        Log.e("countfollow",countfollow);
                        Log.e("Followers",countfollowers);
                        followers.setText(countfollowers+ "\nFollowers");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        if(checkConnection(this)){
            FollowRequest followRequest=new FollowRequest(username, otherusername , "followcheck", responselistener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(followRequest);
        }

        // BEGIN_INCLUDE(initializeRecyclerView)
        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.otherusers_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        followbutton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                followbutton.setVisibility(View.GONE);
                                                Response.Listener<String> responselistener= new Response.Listener<String>()
                                                {
                                                    @Override
                                                    public void onResponse(String response)
                                                    {
                                                        try {

                                                            JSONObject jsonObject=new JSONObject(response);
                                                            boolean success = jsonObject.getBoolean("success");
                                                            String countfollowers= Integer.toString(jsonObject.getInt("countfollowers"));
                                                            if(success){
                                                                Log.e(STRING_TAG,"Successfully followed");
                                                                followers.setText(countfollowers+ "\nFollowers");
                                                                String toastMesg = "You are now following "+ otherusername;
                                                                Toast toast = Toast.makeText(Otheruserprofile.this, toastMesg, Toast.LENGTH_SHORT);
                                                                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                                                if (v != null) v.setGravity(Gravity.CENTER);
                                                                toast.show();

                                                            }
                                                            else {
                                                                AlertDialog.Builder builder= new AlertDialog.Builder(Otheruserprofile.this);
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
                                                if(checkConnection(Otheruserprofile.this)){
                                                    FollowRequest followRequest=new FollowRequest(username, otherusername , "followbutton", responselistener);
                                                    RequestQueue queue = Volley.newRequestQueue(Otheruserprofile.this);
                                                    queue.add(followRequest);
                                                }
                                            }
                                        }
        );

        userListener(true);
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

    public void retreiveFromDatabase(boolean ownEvents){
        Log.e(STRING_TAG,"database");
        Log.e(STRING_TAG, Integer.toString(eventList.size()));
        EventRecyclerView eventRecyclerView = new EventRecyclerView();
        if(checkConnection(this)){
            if(ownEvents){
                for (int i=0;i < eventList.size();i++)
                {

                    Log.i("Value of element "+i,eventList.get(i));
                    eventRecyclerView.initializeData(eventId.get(i),eventList.get(i),eventCategory.get(i),eventLocation.get(i),eventDate.get(i),otherusername,viewcount.get(i),this,"");
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.ItemAdapter(this, eventRecyclerView.getItem(),otherusername);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
            else {
                for (int i=0;i < eventList.size();i++)
                {
                    Log.i("Value of element "+i,eventList.get(i));
                    eventRecyclerView.initializeData(eventId.get(i),eventList.get(i),eventCategory.get(i),eventLocation.get(i),eventDate.get(i),otherusername,viewcount.get(i),this,"");
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.AllItemAdapter(this, eventRecyclerView.getItem(),otherusername,false,false);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

        }

    }


    public void userListener(final boolean ownEvents){
        Log.e(STRING_TAG,"insideListiner");
        eventLocation=new ArrayList<>();
        eventList=new ArrayList<>();
        eventId=new ArrayList<>();
        eventDate=new ArrayList<>();
        eventCategory=new ArrayList<>();
        viewcount=new ArrayList<>();
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    Log.v("Success", Boolean.toString(success));
                    if(success){
                        Log.e(STRING_TAG,"insideSuccess");
                        JSONArray jsonArray = jsonObject.getJSONArray("event_name1");
                        JSONArray jsonArray2 = jsonObject.getJSONArray("location_name1");
                        JSONArray jsonArray3 = jsonObject.getJSONArray("event_date1");
                        JSONArray jsonArray4 = jsonObject.getJSONArray("event_category1");
                        JSONArray jsonArray5 = jsonObject.getJSONArray("event_id1");
                        JSONArray jsonArray7 = jsonObject.getJSONArray("viewcount1");

                        int count= jsonObject.getInt("count1");
                        Log.e(STRING_TAG + "count",Integer.toString(count));
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            Log.e(STRING_TAG + "len",Integer.toString(len));
                            //for eventid
                            for (int i=0;i<len;i++){
                                eventId.add(jsonArray5.get(i).toString());
                            }

                            //for eventname
                            for (int i=0;i<len;i++){
                                eventList.add(jsonArray.get(i).toString());
                            }

                            //for eventlocation
                            for (int i=0;i<len;i++){
                                eventLocation.add(jsonArray2.get(i).toString());
                            }

                            //for eventdate
                            for (int i=0;i<len;i++){
                                eventDate.add(jsonArray3.get(i).toString());
                            }

                            //for eventcategory
                            for (int i=0;i<len;i++){
                                eventCategory.add(jsonArray4.get(i).toString());
                            }

                            //for count
                            for (int i=0;i<len;i++){
                                int value=(Integer) jsonArray7.get(i);
                                viewcount.add(value);
                            }
                            retreiveFromDatabase(ownEvents);
                        }
                        else
                            Log.e(STRING_TAG,"insideNull");

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(Otheruserprofile.this);
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
            if(ownEvents)
            {   Log.e(STRING_TAG+" lkasdkf",Boolean.toString(ownEvents));
                Log.e("Listener otherusername", otherusername);
                /*RecyclerRequest recyclerRequest=new RecyclerRequest(otherusername,"own",responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(recyclerRequest);*/
                TryforotheruserRequest recyclerRequest=new TryforotheruserRequest(otherusername,responseListener);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(recyclerRequest);
            }
            else {
                Log.e(STRING_TAG+" ajf;lkd",Boolean.toString(ownEvents));
                RecyclerRequest recyclerRequest=new RecyclerRequest(otherusername,"getgoing",responseListener);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(recyclerRequest);
            }

        }
    }

    //For retrieving the image of user.
    private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
    {
        String name;
        public Downloadimage(String name)
        {
            this.name=name;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url=SERVER_ADDRESS+"pictures/userimages/"+name+".JPG";
            try{
                URLConnection connection=new URL(url).openConnection();
                connection.setConnectTimeout(1000*30);
                connection.setReadTimeout(1000*30);
                return BitmapFactory.decodeStream((InputStream) connection.getContent(),null,null);

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null)
            {
                downloadedimage.setImageBitmap(bitmap);
            }
        }
    }
}
