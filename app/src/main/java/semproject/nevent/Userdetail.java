package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;
import semproject.nevent.EventRecyclerView;
import semproject.nevent.MainActivity;
import semproject.nevent.R;
import semproject.nevent.Request.RecyclerRequest;

import static semproject.nevent.MainActivity.PreferenceFile;

/**
 * Created by User on 1/29/2017.
 */

public class Userdetail extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    String STRING_TAG="Userdetail";
    private static final String SERVER_ADDRESS="http://avsadh96.000webhostapp.com/";
    ImageView downloadedimage;
    TextView user_name;
    SharedPreferences sharedpreferences;
    private RecyclerView mRecyclerView;
    String username;
     List<String> eventList=new ArrayList<>();
     List<String>eventLocation=new ArrayList<>();
     List<String>eventDate=new ArrayList<>();
     List<String>eventCategory=new ArrayList<>();
    List<String>eventId=new ArrayList<>();
     List<Integer>viewcount=new ArrayList<>();

    //for page events
     List<String>event_org=new ArrayList<>();
     List<String>event_descrp=new ArrayList<>();
     List<String>event_picpath=new ArrayList<>();
     List<Double>latitude=new ArrayList<>();
     List<Double>longitude=new ArrayList<>();
    int start=0;

    TextView denoteempty;
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    EventRecyclerView eventRecyclerView;

    public Userdetail(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventRecyclerView = new EventRecyclerView();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
        username = getArguments().getString("username");

        View rootView = inflater.inflate(R.layout.fragment_userdetails, container, false);
        user_name=(TextView) rootView.findViewById(R.id.user_name);
        denoteempty= (TextView) rootView.findViewById(R.id.empty_text);
        user_name.setText(username);

        downloadedimage=(ImageView) rootView.findViewById(R.id.profileimage);
        new Downloadimage(username).execute();
        // BEGIN_INCLUDE(initializeRecyclerView)
        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.user_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Button userevents=(Button) rootView.findViewById(R.id.userevents);
        userevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection(getContext())){
                    userListener(true);
                }
            }
        });

        Button goingevents=(Button) rootView.findViewById(R.id.goingevents);
        goingevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection(getContext())){
/*                    userevents.setVisibility(View.VISIBLE);*/
                    userListener(false);
                }
            }
        });
        Button userlogout=(Button) rootView.findViewById(R.id.userlogout);
        userlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection(getContext())){
                    sharedpreferences = getActivity().getSharedPreferences(PreferenceFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.apply();
                    LoginManager.getInstance().logOut();
                    Intent intent= new Intent(getContext(),MainActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };



        info = (TextView)rootView.findViewById(R.id.info);

        loginButton = (LoginButton)rootView.findViewById(R.id.fblogin_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                String token= AccessToken.getCurrentAccessToken().getToken();
                String toastMesg = "Succesfully logged in on Facebook";
                Log.e("Facebook",token);
                Toast toast = Toast.makeText(getContext().getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }

            @Override
            public void onCancel() {
                String toastMesg = "Try again.";
                Intent i= new Intent(getContext(), HomePage.class);
                getActivity().finish();
                startActivity(i);
                Toast toast = Toast.makeText(getContext().getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();

            }

            @Override
            public void onError(FacebookException e) {
                String toastMesg = "Login Failed";
                Toast toast = Toast.makeText(getContext().getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }

        });
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("user_likes"));

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if(currentAccessToken!=null){
            //fbbutton.setVisibility(View.VISIBLE);
            Log.e("Facebook","Loggedin");

        }
        else{
            Log.e("Facebook","Not logged in");
        }
    }


    public void retreiveFromDatabase(boolean ownEvents){
        Log.e(STRING_TAG,"database");
        Log.e(STRING_TAG, Integer.toString(eventList.size()));
        EventRecyclerView eventRecyclerView = new EventRecyclerView();
        if(checkConnection(getContext())){
            if(ownEvents){
                if ( eventList.isEmpty()){
                    eventRecyclerView.emptyItems();
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.ItemAdapter(getContext(), eventRecyclerView.getItem(),username);
                    mRecyclerView.setAdapter(mAdapter);
                    denoteempty.setVisibility(View.VISIBLE);
                }
                for (int i=0;i <  eventList.size();i++)
                {
                    denoteempty.setVisibility(View.GONE);
                    Log.i("Value of element "+i, eventList.get(i));
                    eventRecyclerView.initializeData( eventId.get(i), eventList.get(i), eventCategory.get(i), eventLocation.get(i), eventDate.get(i),username, viewcount.get(i),getContext(),"");
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.ItemAdapter(getContext(), eventRecyclerView.getItem(),username);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
            else {
                if ( eventList.isEmpty()){
                    eventRecyclerView.emptyItems();
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.AllItemAdapter(getContext(), eventRecyclerView.getItem(),username,false,false);
                    mRecyclerView.setAdapter(mAdapter);
                    denoteempty.setVisibility(View.VISIBLE);
                }
                for (int i=0;i <  eventList.size();i++)
                {
                    denoteempty.setVisibility(View.GONE);
                    Log.i("Value of element "+i, eventList.get(i));
                    eventRecyclerView.initializeData( eventId.get(i), eventList.get(i), eventCategory.get(i), eventLocation.get(i), eventDate.get(i),username, viewcount.get(i),getContext(),"");
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.AllItemAdapter(getContext(), eventRecyclerView.getItem(),username,false,false);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

        }

    }


    public void userListener(final boolean ownEvents){
        Log.e(STRING_TAG,"insideListiner");
         eventLocation.clear();
         eventList.clear();
         eventId.clear();
         eventCategory.clear();
         viewcount.clear();
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success1");
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
                        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
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
        if(checkConnection(getContext())){
            if(ownEvents)
            {   Log.e(STRING_TAG+" own",Boolean.toString(ownEvents));
                RecyclerRequest recyclerRequest=new RecyclerRequest(username,"own",responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(recyclerRequest);
            }
            else {
                Log.e(STRING_TAG+" going",Boolean.toString(ownEvents));
                RecyclerRequest recyclerRequest=new RecyclerRequest(username,"getgoing",responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
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

    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(getContext(),InternetConnection.class);
            getActivity().finish();
            startActivity(intent);
        }
        return isConnected;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(getContext(),MainActivity.class);
            getActivity().finish();
            startActivity(intent);
        }
        else{
            Intent intent= new Intent(getContext(),InternetConnection.class);
            getActivity().finish();
            startActivity(intent);

        }
    }
}
