package semproject.nevent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;
import semproject.nevent.MapsActivities.ShowLocation;
import semproject.nevent.Request.AttendingEventRequest;
import semproject.nevent.Request.CountRequest;
import semproject.nevent.Request.DetailRequest;
import semproject.nevent.Request.InviteRequest;
import semproject.nevent.Request.ParticipantRequest;

import static semproject.nevent.EventRecyclerView.selecctedUser;
import static semproject.nevent.HomePage.stat_forinvite_eventRecyclerView;
import static semproject.nevent.HomePage.stat_forsearch_Useradapter;
import static semproject.nevent.HomePage.stat_forsearch_eventRecyclerView;

public class EventDetails extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    String STRING_TAG="EventDetails";
    private static final String SERVER_ADDRESS="http://avsadh96.000webhostapp.com/";
    ImageView downloadedimage;
    TextView veventLabel,veventLocation,veventDate,veventOrganizer,veventCategory,veventId,veventDetails,attendingtext, participantnumber;
    Button attendingbutton, invitebutton;
    Friendinvite friendinvite;
    static public RecyclerView mRecyclerView;
    //public RecyclerView.Adapter mAdapter;
    Bitmap eventImage;
    String eventId, eventLabel, eventLocation, eventDate, eventPath,eventOrganizer, eventCategory,eventDetails,eventLatitude, eventLongitude, username,attendingcount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        veventLabel= (TextView) findViewById(R.id.deventLabel);
        veventLocation= (TextView) findViewById(R.id.deventLocation);
        veventDate= (TextView) findViewById(R.id.deventDate);
        veventOrganizer= (TextView) findViewById(R.id.deventOrganizer);
        veventCategory= (TextView) findViewById(R.id.deventCategory);
        veventId= (TextView) findViewById(R.id.deventId);
        veventDetails= (TextView) findViewById(R.id.deventDetails);
        attendingbutton=(Button) findViewById(R.id.going);
        attendingtext=(TextView) findViewById(R.id.attend);
        participantnumber=(TextView) findViewById(R.id.goingnumber);
        invitebutton= (Button) findViewById(R.id.invite_button);

        Intent intent = getIntent();
        username=intent.getStringExtra("username");
        eventId=intent.getStringExtra("eventId");
        eventLabel=intent.getStringExtra("eventLabel");
        eventLocation=intent.getStringExtra("eventLocation");
        eventDate=intent.getStringExtra("eventDate");
        eventOrganizer=intent.getStringExtra("eventOrganizer");
        eventCategory=intent.getStringExtra("eventCategory");
        Boolean check= intent.getBooleanExtra("check",true);
        downloadedimage=(ImageView) findViewById(R.id.detaildownloadedimage);
        if(!check) {
            eventDetails = intent.getStringExtra("description");
            eventLatitude = intent.getStringExtra("latitude");
            eventLongitude = intent.getStringExtra("longitude");
            eventPath = intent.getStringExtra("path");
            attendingcount=intent.getStringExtra("attendingcount");
            participantnumber.setText(attendingcount);
            attendingbutton.setVisibility(View.GONE);
            attendingtext.setVisibility(View.GONE);
            invitebutton.setVisibility(View.GONE);


            /*try{
                eventImage=intent.getParcelableExtra("eventImage");
                downloadedimage.setImageBitmap(eventImage);
            }
            catch (Exception e){
                downloadedimage.setVisibility(View.GONE);
                Log.e(STRING_TAG,"Image not found");
            }*/
            veventDetails.setText(eventDetails);
            new Downloadimagefacebook(eventPath).execute();
        }
        else{
            listenerFunction();
            checkgoing();
            new Downloadimage(eventLabel).execute();
        }
        setvalues();

    }

    private void setvalues() {
        veventId.setText(eventId);
        veventLabel.setText(eventLabel);
        veventLocation.setText(eventLocation);
        veventDate.setText(eventDate);
        veventOrganizer.setText(eventOrganizer);
        veventCategory.setText(eventCategory);
    }
    //String eventId,String eventLabel,String eventLocation,String eventDate,String eventOrganizer,String eventCategory


    public void showlocation(View view)
    {
        if(Double.parseDouble(eventLatitude)==0.0&&Double.parseDouble(eventLongitude)==0.0){
            String toastMesg = "Location is not set in map for this event.";
            Toast toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
        else{
            Intent i= new Intent(this,ShowLocation.class);
            i.putExtra("eventname",eventLabel);
            i.putExtra("latitude",eventLatitude);
            i.putExtra("longitude",eventLongitude);
            startActivity(i);
        }


    }

    public void attendingevents(View view)
    {
        Log.e(STRING_TAG,"going button pressed");
        attendingbutton.setVisibility(View.GONE);
        attendingtext.setVisibility(View.GONE);
        Response.Listener<String> responselistener= new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String number= Integer.toString(jsonObject.getInt("participants"));
                    if(success){
                        participantnumber.setText(number);
                        Log.e(STRING_TAG,"after btn insideSuccess");
                        String toastMesg = "See you there.";
                        Toast toast = Toast.makeText(getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        if (v != null) v.setGravity(Gravity.CENTER);
                        toast.show();

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EventDetails.this);
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
            AttendingEventRequest attendingEventRequest=new AttendingEventRequest(username, eventId ,responselistener);
            RequestQueue queue = Volley.newRequestQueue(EventDetails.this);
            queue.add(attendingEventRequest);
        }
    }

    public void checkgoing(){
        Response.Listener<String> responselistener= new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    Log.e(STRING_TAG,"insideCheckGoing");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    boolean going= jsonObject.getBoolean("going");
                    String number= Integer.toString(jsonObject.getInt("participants"));
                    Log.e(STRING_TAG,"insideCheckGoing"+number);
                    if(success){
                        if(going){
                            attendingbutton.setVisibility(View.GONE);
                            attendingtext.setVisibility(View.GONE);
                        }
                        participantnumber.setText(number);

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EventDetails.this);
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
            ParticipantRequest participantRequest=new ParticipantRequest(username, eventId ,responselistener);
            RequestQueue queue = Volley.newRequestQueue(EventDetails.this);
            queue.add(participantRequest);
        }
    }

    public void listenerFunction(){
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
                        eventDetails = jsonObject.getString("event_details");
                        eventLatitude=jsonObject.getString("event_lat");
                        eventLongitude=jsonObject.getString("event_long");
                        veventDetails.setText(eventDetails);
                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EventDetails.this);
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
            DetailRequest detailRequest=new DetailRequest(eventId,responseListener);
            RequestQueue queue = Volley.newRequestQueue(EventDetails.this);
            queue.add(detailRequest);
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

    public void invite(View view)
    {
        friendinvite = new Friendinvite();
        Bundle bundle=new Bundle();
        bundle.putString("username",username);
        bundle.putString("eventID",eventId);
        friendinvite.setArguments(bundle);
        friendinvite.show(getSupportFragmentManager(), "details");
    }


    public static class Friendinvite extends DialogFragment {
        String STRING_TAG="Friendinvite";
        RecyclerView mRecyclerView;
        EventRecyclerView inviterecyclerview = new EventRecyclerView();
        EventRecyclerView.FollowItemAdapter adapter;
        String username,eventID;
        Boolean showtoast=false;
        public Friendinvite()
        {
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            username=getArguments().getString("username");
            eventID=getArguments().getString("eventID");
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Friend List")
                    .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(String names:selecctedUser){
                                listenerfunction(names);
                                Log.e(STRING_TAG,names);
                            }
                            selecctedUser.clear();
                            if(showtoast) {
                                String toastMesg = "Thanks for promoting events!!";
                                Toast toast = Toast.makeText(getContext().getApplicationContext(), toastMesg, Toast.LENGTH_SHORT);
                                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                if (v != null) v.setGravity(Gravity.CENTER);
                                toast.show();
                            }
                            Log.e(STRING_TAG,"Inside dialog box");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            selecctedUser.clear();// User cancelled the dialog
                        }
                    });
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_box, null);
            builder.setView(view);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.invite_recycler_view);
            if (mRecyclerView != null) {
                mRecyclerView.setHasFixedSize(true);
            }
            RecyclerView.LayoutManager mLayoutManager;
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            setAdapterforInvite();
            /*mRecyclerView.setAdapter(stat_forsearch_Useradapter);*/
            // Create the AlertDialog object and return it
            return builder.create();


        }

        void setAdapterforInvite(){
            List<EventRecyclerView.Item_follow> extractedItem=stat_forinvite_eventRecyclerView.getItemFollow();
            for(EventRecyclerView.Item_follow indevent: extractedItem){
                Log.e("Invite","started");
                inviterecyclerview.initializeDataFollow(indevent.followuserid,indevent.followusername,indevent.followemail,getContext());
                adapter = new EventRecyclerView.FollowItemAdapter(indevent.context.getApplicationContext(), inviterecyclerview.getItemFollow(),username,true);
                mRecyclerView.setAdapter(adapter);
            }

        }

        void listenerfunction(String invitedname){
            Log.e(STRING_TAG,"insideListiner");
            Response.Listener<String> responseListener= new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(STRING_TAG,"try");
                        JSONObject jsonObject=new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success1");
                        if(success){
                            showtoast=true;
                            Log.e(STRING_TAG,"insideSuccess");
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
            Log.e(STRING_TAG,username);
            Log.e(STRING_TAG,invitedname);
            Log.e(STRING_TAG,eventID);
            InviteRequest inviteRequest=new InviteRequest(username,invitedname,eventID,responseListener);
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(inviteRequest);
        }

    }

    //For retrieving the image of event.
    private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
    {
        String name;
        public Downloadimage(String name)
        {
            this.name=name;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            if(name.contains(" "))
            {
                name = name.replaceAll(" ", "_");
            }
            String url=SERVER_ADDRESS+"pictures/eventimages/"+name+".JPG";
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
                Log.e(STRING_TAG,"Image downloaded");
                downloadedimage.setImageBitmap(bitmap);
            }
        }
    }

    //For retrieving the image of facebook event
    private class Downloadimagefacebook extends AsyncTask<Void, Void, Bitmap>
    {
        String path;
        public Downloadimagefacebook(String path)
        {
            this.path=path;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            try{
                URLConnection connection=new URL(path).openConnection();
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
                Log.e(STRING_TAG,"FacebookImage downloaded");
                downloadedimage.setImageBitmap(bitmap);
            }
        }
    }
}
