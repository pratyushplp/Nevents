package semproject.nevent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;
import semproject.nevent.Request.CountRequest;
import semproject.nevent.Request.DeleteRequest;

/**
 * Created by User on 12/26/2016.
 */

public class EventRecyclerView {
    String STRING_TAG= "EventRecyclerView";
    private final static String LOG_TAG = EventRecyclerView.class.getSimpleName();
    private final static String LOG_TAGS="Holder name down";
    private List<Item> items = new ArrayList<>();
    private List<Item_follow> items_follow = new ArrayList<>();
    private List<Item_facebook> items_facebook=new ArrayList<>();
    public static List<String> selecctedUser=new ArrayList<>();

    public EventRecyclerView() {

    }

    public void initializeData(String eventid,String eventname,String eventcategory,String eventlocation,String eventdate,String organizer,Integer viewcount,Context context,String distance) {
        items.add(new Item(eventid,eventname, eventcategory,eventlocation,eventdate,organizer,viewcount,context,distance));
        Log.e(STRING_TAG+"item",eventname+" data initialized");
        Log.e(STRING_TAG+"init",Integer.toString(items.size()));

    }

    public void initializeDataFollow(String followid,String followusername,String followemail, Context context) {
        items_follow.add(new Item_follow(followid,followusername, followemail,context));
        Log.e(STRING_TAG+"follow",followusername+" data initialized");
        Log.e(STRING_TAG+"init",Integer.toString(items_follow.size()));
    }

    public void initializeDataFacebook(String eventid,String eventname,String eventcategory,String eventlocation,String eventdate,String eventOrganizer,Integer count,Double lat, Double longi, String path,String description,Context context,float distance) {
        try{
            items_facebook.add(new Item_facebook(eventid,eventname,eventcategory,eventlocation,eventdate,eventOrganizer,count,lat,longi,path,description,context,distance));
            Log.e(STRING_TAG+"fb",eventname+" facebook data initialized");
            Log.e(STRING_TAG+"init",Integer.toString(items_facebook.size()));
        }catch (Exception e){
            Log.e(STRING_TAG,"Unexpected Error");
            /*String toastMesg = "Facebook server error. Try again!!";
            Toast toast = Toast.makeText(context, toastMesg, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();*/
        }

    }

    public List getItem(){
        return items;
    }
    public List getItemFollow(){
        //Log.i(STRING_TAG,"Total Items"+Integer.toString(items_facebook.size()));
        return items_follow;}
    public List getItemFacebook(){
        Log.i(STRING_TAG,"Total Items"+Integer.toString(items_facebook.size()));
        return items_facebook;}

    public void emptyItems(){
        items=Collections.emptyList();
    }
    public void emptyItemsFollow(){items_follow=Collections.emptyList();}
    public void emptyItemsFacebook(){
        if(!items_facebook.isEmpty())
            items_facebook=Collections.emptyList();}
    public class Item {
        public String eventLabel;
        public String eventId,eventLocation,eventDate,eventOrganizer,eventCategory;
        public Context context;
        public int viewcount;
        public String distance;

        Item(String eventid,String eventname,String eventcategory,String eventlocation,String eventdate,String eventOrganizer,Integer count,Context context,String distance) {
            this.eventId=eventid;
            this.eventLabel=eventname;
            this.eventLocation=eventlocation;
            this.eventDate=eventdate;
            this.eventOrganizer=eventOrganizer;
            this.eventCategory=eventcategory;
            this.context=context;
            this.distance=distance;
            viewcount=count;
        }

    }

    public class Item_follow {
        public String followusername;
        public String followuserid;
        public String followemail;
        public Context context;

        Item_follow(String followuserid,String followusername,String followemail, Context context) {
            this.followuserid=followuserid;
            this.followusername=followusername;
            this.followemail= followemail;
            this.context=context;
        }

    }

    public class Item_facebook {
        public String eventLabel;
        public String eventId,eventLocation,eventDate,eventOrganizer,eventCategory;
        public Context context;
        public int viewcount;
        public Double latitude;
        public Double longitude;
        public String picpath;
        public String description;
        public Float distance;

        Item_facebook(String eventid,String eventname,String eventcategory,String eventlocation,String eventdate,String eventOrganizer,Integer count,Double lat, Double longi, String path,String description,Context context,float distance) {
            this.eventId=eventid;
            this.eventLabel=eventname;
            this.eventLocation=eventlocation;
            this.eventDate=eventdate;
            this.eventOrganizer=eventOrganizer;
            this.eventCategory=eventcategory;
            this.context=context;
            this.description=description;
            this.distance=distance;
            latitude=lat;
            longitude=longi;
            picpath=path;
            viewcount=count;
        }

    }

    private static void listenerFunction(String eventname,Integer viewcount,final Context context){
        Log.e("EventRecyclerView","insideListiner");
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("EventRecyclerView","try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Log.e("EventRecyclerView","insideSuccess");
                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(context);
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
        CountRequest countRequest=new CountRequest(eventname,viewcount,"incr",responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(countRequest);
    }

    // Creating an Adapter i.e to add each items in recyclerView
    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements ConnectivityReceiver.ConnectivityReceiverListener {
        String STRING_TAG= "ItemAdapter";
        String eventnamedelete;
        String username;
        private static final String SERVER_ADDRESS="http://avsadh96.000webhostapp.com/";
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item> items = Collections.emptyList();
        Item currentItem;
        private boolean checkConnection() {
            Log.e(STRING_TAG,"checkConnection");
            boolean isConnected = ConnectivityReceiver.isConnected(currentItem.context);
            if(!isConnected){
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            return isConnected;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            if(isConnected){
                Intent intent= new Intent(currentItem.context,MainActivity.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            else{
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
        }

        // Constructor to inflate layout of each item in RecyclerView
        public ItemAdapter(Context context, List<Item> items,String name) {
            inflater = LayoutInflater.from(context);
            this.items = items;
            this.username =name;
        }

        //create a view holder of items
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.v(LOG_TAG, "onCreateViewHolder called.");
            View view = inflater.inflate(R.layout.events_details, parent, false);

            ItemViewHolder holder = new ItemViewHolder(view);

            return holder;
        }

        //binds all the views from view holder to form a single view and show the binded view
        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            Log.v(LOG_TAG, "onBindViewHolder called.");
            String defaultLabel="Activity";
            String eventname;
            currentItem = items.get(position);

            if(holder.eventLabel.getText().equals(" "))
                holder.eventLabel.setText(defaultLabel);
            else


            if(currentItem.eventLabel.contains(" "))
            {
                eventname = currentItem.eventLabel.replaceAll(" ", "_");
                holder.downloadedimage.setTag(eventname);
            }
            else {
                holder.downloadedimage.setTag(currentItem.eventLabel);
            }
            holder.downloadedimage.setVisibility(View.GONE);
            new Downloadimage(holder, currentItem.eventLabel, position).execute();
            holder.eventLabel.setText(currentItem.eventLabel);
            holder.eventLocation.setText(currentItem.eventLocation);
            holder.eventDate.setText(currentItem.eventDate);
            holder.eventCategory.setText(currentItem.eventCategory);
            holder.eventOrganizer.setText(currentItem.eventOrganizer);
            holder.eventId.setText(currentItem.eventId);
            holder.eventLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkConnection()){
                        Intent intent=new Intent (currentItem.context,EventDetails.class);
                        intent.putExtra("eventId",holder.eventId.getText().toString());
                        intent.putExtra("eventLabel",holder.eventLabel.getText().toString());
                        intent.putExtra("eventLocation",holder.eventLocation.getText().toString());
                        intent.putExtra("eventDate",holder.eventDate.getText().toString());
                        intent.putExtra("eventCategory",holder.eventCategory.getText().toString());
                        intent.putExtra("eventOrganizer",holder.eventOrganizer.getText().toString());
                        intent.putExtra("username",username);
                        intent.putExtra("check",true);
                        currentItem.context.startActivity(intent);
                    }

                }

            });
            holder.eventDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(LOG_TAG, "Item Clicked.");
                    Log.v(LOG_TAG,holder.eventLabel.getText().toString());
                    Log.v(LOG_TAG+" delete",Integer.toString(position));
                    removeAt(position,currentItem,holder);
                    holder.downloadedimage.setImageBitmap(null);
                }
            });
            // click event handler when Item in RecyclerView is clicked

        }
        public void removeAt(final int position, final Item item,final ItemViewHolder holder ) {
            final Context context=item.context;
            AlertDialog.Builder builder= new AlertDialog.Builder(item.context);
            builder.setMessage("Do you really want to delete this event?")
                    .setTitle("Confirmation")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            listenerFunction(context,holder);
                        }
                    })
                    .setNegativeButton("NO",null)
                    .create()
                    .show();

        }

        public void listenerFunction(final Context context,final ItemViewHolder holder){

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
                            String toastMesg = "You have deleted your event from database";
                            Toast toast = Toast.makeText(context, toastMesg, Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                        else {
                            AlertDialog.Builder builder= new AlertDialog.Builder(context);
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
            if(holder.eventLabel.getText().toString().contains(" "))
            {
                eventnamedelete = holder.eventLabel.getText().toString().replaceAll(" ", "_");
                new Deleteimage(eventnamedelete).execute();
            }
            else
            {
                new Deleteimage(holder.eventLabel.getText().toString()).execute();
            }


            DeleteRequest deleteRequest=new DeleteRequest(holder.eventOrganizer.getText().toString()
                    ,holder.eventLabel.getText().toString()
                    ,holder.eventDate.getText().toString()
                    ,holder.eventCategory.getText().toString()
                    ,holder.eventLocation.getText().toString()
                    ,responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(deleteRequest);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /* ViewHolder for this adapter */
        class ItemViewHolder extends RecyclerView.ViewHolder {
            LinearLayout eventLinear;
            TextView eventLabel;
            TextView eventLocation;
            TextView eventDate;
            TextView eventOrganizer;
            ImageButton eventDelete;
            TextView eventCategory;
            ImageView downloadedimage;
            TextView eventId;

            public ItemViewHolder(View itemView) {
                super(itemView);
                eventLinear=(LinearLayout) itemView.findViewById(R.id.linear1);
                eventId=(TextView) itemView.findViewById(R.id.eventId);
                eventCategory=(TextView) itemView.findViewById(R.id.eventCategory);
                eventLabel = (TextView) itemView.findViewById(R.id.eventLabel);
                eventLocation = (TextView) itemView.findViewById(R.id.eventLocation);
                eventDate=(TextView) itemView.findViewById(R.id.eventDate);
                eventOrganizer=(TextView) itemView.findViewById(R.id.eventOrganizer);
                eventDelete=(ImageButton) itemView.findViewById(R.id.eventDelete);
                downloadedimage=(ImageView) itemView.findViewById(R.id.downloadedpicture);
            }
        }


        //For retrieving the image of event.
        private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
        {
            String event_name;
            int position;
            ItemViewHolder holder;
            public Downloadimage(ItemViewHolder holder, String name, int position)
            {
                this.position=position;
                this.holder=holder;
                this.event_name=name;
            }
            @Override
            protected Bitmap doInBackground(Void... params) {
                if(event_name.contains(" "))
                {
                    event_name = event_name.replaceAll(" ", "_");
                }
                String url=SERVER_ADDRESS+"pictures/eventimages/"+event_name+".JPG";
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
                if(bitmap!=null && holder.downloadedimage.getTag().toString().equals(event_name))
                {
                    Log.v(LOG_TAGS, "Photo received.");

                    holder.downloadedimage.setVisibility(View.VISIBLE);
                    holder.downloadedimage.setImageBitmap(bitmap);
                }
                else
                {
                    holder.downloadedimage.setVisibility(View.GONE);
                }
            }
        }

        private class Deleteimage extends AsyncTask<Void,Void,Void>
        {
            String name;
            public Deleteimage(String name)
            {
                this.name=name;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }

            @Override
            protected Void doInBackground(Void... params) {

                ArrayList<NameValuePair> datatosend=new ArrayList<>();
                datatosend.add(new BasicNameValuePair("name",name));

                HttpParams httpRequestParams=getHttpRequestParams();
                HttpClient client=new DefaultHttpClient(httpRequestParams);
                HttpPost post=new HttpPost(SERVER_ADDRESS + "Deletephoto.php");

                try{
                    post.setEntity(new UrlEncodedFormEntity(datatosend));
                    client.execute(post);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }


        private HttpParams getHttpRequestParams()
        {
            HttpParams httpRequestParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*30);
            HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
            return httpRequestParams;
        }
    }

    //For all events
    public static class AllItemAdapter extends RecyclerView.Adapter<AllItemAdapter.AllItemViewHolder>implements ConnectivityReceiver.ConnectivityReceiverListener{
        String STRING_TAG= "ItemAdapter";

        private static final String SERVER_ADDRESS="http://avsadh96.000webhostapp.com/";
        String[] admin={"Aayush","Sagun","Pratyush","Avash","Prabin"};
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item> items = Collections.emptyList();
        Item currentItem;
        String username;
        boolean check=false;
        boolean isInvite=false;

        public AllItemAdapter(){
           /* Log.e(STRING_TAG,Integer.toString(check));
            check++;*/
        }
        // Constructor to inflate layout of each item in RecyclerView
        public AllItemAdapter(Context context, List<Item> items, String name, boolean isNearlist, boolean isInvite) {
            inflater = LayoutInflater.from(context);
            this.items = items;
            username=name;
            this.check=isNearlist;
            this.isInvite=isInvite;
           /* Log.e(STRING_TAG,"itemadpter "+Integer.toString(check));*/

        }

        private boolean checkConnection() {
            Log.e(STRING_TAG,"checkConnection");
            boolean isConnected = ConnectivityReceiver.isConnected(currentItem.context);
            if(!isConnected){
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            return isConnected;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            if(isConnected){
                Intent intent= new Intent(currentItem.context,MainActivity.class);
                currentItem.context.startActivity(intent);
                ((Activity)currentItem.context).finish();
            }
            else{
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                currentItem.context.startActivity(intent);
                ((Activity)currentItem.context).finish();
            }
        }

        //create a view holder of items
        @Override
        public AllItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.v(LOG_TAG, "onCreateViewHolder called.");
            View view = inflater.inflate(R.layout.allevents_details, parent, false);

            AllItemAdapter.AllItemViewHolder holder = new AllItemAdapter.AllItemViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final AllItemViewHolder holder,final int position) {
            Log.v(LOG_TAG, "onBindViewHolder called.");
            String defaultLabel="Activity";
            String eventname;
            currentItem = items.get(position);

            if(holder.eventLabel.getText().equals(" "))
                holder.eventLabel.setText(defaultLabel);
            else

            if(currentItem.eventLabel.contains(" "))
            {
                eventname = currentItem.eventLabel.replaceAll(" ", "_");
                holder.downloadedimage.setTag(eventname);
            }
            else {
                holder.downloadedimage.setTag(currentItem.eventLabel);
            }

            holder.downloadedimage.setVisibility(View.GONE);
            new Downloadimage(holder, currentItem.eventLabel, position).execute();
            holder.eventLabel.setText(currentItem.eventLabel);
            holder.eventLocation.setText(currentItem.eventLocation);
            holder.eventDate.setText(currentItem.eventDate);
            holder.eventCategory.setText(currentItem.eventCategory);
            holder.eventOrganizer.setText(currentItem.eventOrganizer);
            holder.eventView.setText(String.valueOf(currentItem.viewcount));
            holder.eventId.setText(currentItem.eventId);
            Log.v("Holder name up", holder.eventLabel.getText().toString());

            holder.eventLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkConnection()){
                        Log.e("Count",holder.eventView.getText().toString());
                        int views=Integer.decode(holder.eventView.getText().toString());
                        views++;
                        Log.v("Count",String.valueOf(views));
                        listenerFunction(holder.eventLabel.getText().toString(),views,currentItem.context);
                        Intent intent=new Intent (currentItem.context,EventDetails.class);
                        intent.putExtra("username",username);
                        intent.putExtra("eventId",holder.eventId.getText().toString());
                        intent.putExtra("eventLabel",holder.eventLabel.getText().toString());
                        intent.putExtra("eventLocation",holder.eventLocation.getText().toString());
                        intent.putExtra("eventDate",holder.eventDate.getText().toString());
                        intent.putExtra("eventCategory",holder.eventCategory.getText().toString());
                        intent.putExtra("eventOrganizer",holder.eventOrganizer.getText().toString());
                        intent.putExtra("check",true);
                        currentItem.context.startActivity(intent);
                    }
                }
            });
            Log.v(STRING_TAG,username);
            if (Arrays.asList(admin).contains(username)) {
                holder.eventDelete.setVisibility(View.VISIBLE);
                holder.eventDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(LOG_TAG, "Item Clicked.");
                        Log.v(LOG_TAG,holder.eventLabel.getText().toString());
                        Log.v(LOG_TAG+" delete",Integer.toString(position));
                        removeAt(position,currentItem,holder);
                        holder.downloadedimage.setImageBitmap(null);
                    }
                });
            }
            if(check){
                //String holderdistance=Float.toString(currentItem.distance);
                holder.distancevalue.setText(currentItem.distance);
                holder.distancetext.setVisibility(View.VISIBLE);
                holder.distancevalue.setVisibility(View.VISIBLE);
            }

            if(isInvite){
                //String holderdistance=Float.toString(currentItem.distance);
                holder.distancevalue.setText(currentItem.distance);
                holder.distancetext.setText("Invited by: ");
                holder.distancetext.setVisibility(View.VISIBLE);
                holder.distancevalue.setVisibility(View.VISIBLE);
            }
            // click event handler when Item in RecyclerView is clicked

        }
        public void removeAt(final int position, final Item item,final AllItemViewHolder holder ) {
            final Context context=item.context;
            AlertDialog.Builder builder= new AlertDialog.Builder(item.context);
            builder.setMessage("Do you really want to delete this event?")
                    .setTitle("Confirmation")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            deleteFunction(context,holder);
                        }
                    })
                    .setNegativeButton("NO",null)
                    .create()
                    .show();

        }
       /* public void removeAt(final int position, final Item item) {
            AlertDialog.Builder builder= new AlertDialog.Builder(item.context);
            builder.setMessage("Do you really want to delete this event?")
                    .setTitle("Confirmation")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            deleteFunction(item);
                        }
                    })
                    .setNegativeButton("NO",null)
                    .create()
                    .show();

        }*/

        public void deleteFunction(final Context context,final AllItemViewHolder holder){
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
                            String toastMesg = "You have sucessfully deleted an event.";
                            Toast toast = Toast.makeText(context, toastMesg, Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                        else {
                            AlertDialog.Builder builder= new AlertDialog.Builder(context);
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
            DeleteRequest deleteRequest=new DeleteRequest(holder.eventOrganizer.getText().toString(),
                    holder.eventLabel.getText().toString(),
                    holder.eventDate.getText().toString(),
                    holder.eventCategory.getText().toString(),
                    holder.eventLocation.getText().toString(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(deleteRequest);
        }




        @Override
        public int getItemCount() {
            return items.size();
        }

        /* ViewHolder for this adapter */
        class AllItemViewHolder extends RecyclerView.ViewHolder {
            LinearLayout eventLinear;
            TextView eventLabel;
            TextView eventLocation;
            TextView eventDate;
            TextView eventOrganizer;
            TextView eventCategory;
            TextView eventId;
            TextView eventView;
            ImageView downloadedimage;
            ImageButton eventDelete;
            TextView distancetext;
            TextView distancevalue;

            public AllItemViewHolder(View itemView) {
                super(itemView);
                eventLinear=(LinearLayout) itemView.findViewById(R.id.alllinear1);
                eventId=(TextView) itemView.findViewById(R.id.alleventId);
                eventCategory=(TextView) itemView.findViewById(R.id.alleventCategory);
                eventLabel = (TextView) itemView.findViewById(R.id.alleventLabel);
                eventLocation = (TextView) itemView.findViewById(R.id.alleventLocation);
                eventDate=(TextView) itemView.findViewById(R.id.alleventDate);
                eventOrganizer=(TextView) itemView.findViewById(R.id.alleventOrganizer);
                eventView= (TextView) itemView.findViewById(R.id.alleventView);
                eventDelete=(ImageButton) itemView.findViewById(R.id.alleventDelete);
                downloadedimage=(ImageView) itemView.findViewById(R.id.alldownloadedimage);
                distancetext=(TextView) itemView.findViewById(R.id.distanceid);
                distancevalue=(TextView) itemView.findViewById(R.id.distancevalue);

            }
        }

        public void setFilter(List<Item> searchitem){
            if(items.isEmpty())
                Log.e(STRING_TAG,"SetFilter empty");
            //Log.e(STRING_TAG,"SetFilter "+Integer.toString(check));
            items=Collections.emptyList();
            items=searchitem;

            notifyDataSetChanged();
        }


        //For retrieving the image of event.
        private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
        {
            String name;
            int position;
            AllItemViewHolder holder;
            public Downloadimage(AllItemViewHolder holder,String name, int position)

            {
                this.position=position;
                this.holder=holder;
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

                    return null;
                }

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Log.e("Holder set tag name _",holder.downloadedimage.getTag().toString());
                if(bitmap!=null && holder.downloadedimage.getTag().toString().equals(name))
                {

                    Log.v(LOG_TAGS, holder.eventLabel.getText().toString());

                    //holder.downloadedimage.getLayoutParams().height = 90;
                    holder.downloadedimage.setVisibility(View.VISIBLE);
                    holder.downloadedimage.setImageBitmap(bitmap);

                }
                else
                {
                    holder.downloadedimage.setVisibility(View.GONE);
                }

            }
        }

    }

    //For following user
    public static class FollowItemAdapter extends RecyclerView.Adapter<FollowItemAdapter.FollowViewHolder> implements ConnectivityReceiver.ConnectivityReceiverListener{
        String STRING_TAG= "FollowItemAdapter";

        private static final String SERVER_ADDRESS="http://avsadh96.000webhostapp.com/";
        String[] admin={"Aayush","Sagun","Pratyush","Avash","Prabin"};
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item_follow> items_follow= Collections.emptyList();
        Item_follow currentItem;
        String username;
        Boolean fromInvite=false;
        public FollowItemAdapter(){
           /* Log.e(STRING_TAG,Integer.toString(check));
            check++;*/
        }
        // Constructor to inflate layout of each item in RecyclerView
        public FollowItemAdapter(Context context, List<Item_follow> items, String username, Boolean fromInvite) {
            inflater = LayoutInflater.from(context);
            this.items_follow = items;
            this.username=username;
            this.fromInvite=fromInvite;
           /* Log.e(STRING_TAG,"itemadpter "+Integer.toString(check));*/

        }

        @Override
        public FollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.v(LOG_TAG, "onCreateViewHolder called.");
            View view = inflater.inflate(R.layout.follow_userprofile, parent, false);

            FollowItemAdapter.FollowViewHolder holder = new FollowItemAdapter.FollowViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final FollowViewHolder holder, int position) {
            Log.v(LOG_TAG, "onBindViewHolder called.");
            String defaultLabel="Activity";
            String eventname;
            currentItem = items_follow.get(position);

            if(holder.followUsername.getText().equals(" "))
                holder.followUsername.setText(defaultLabel);
            else

            if(currentItem.followusername.contains(" "))
            {
                eventname = currentItem.followusername.replaceAll(" ", "_");
                holder.followUserpro.setTag(eventname);
            }
            else {
                holder.followUserpro.setTag(currentItem.followusername);
            }

            holder.followUserpro.setVisibility(View.GONE);
            new FollowItemAdapter.Downloadimage(holder, currentItem.followusername, position).execute();
            holder.followUsername.setText(currentItem.followusername);
            holder.followUserid.setText(currentItem.followuserid);
            Log.v("Holder name up", holder.followUsername.getText().toString());

            if(fromInvite){
                holder.followLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView name= (TextView) v.findViewById(R.id.follow_unclick);
                        String check= name.getText().toString();
                        String followusername= holder.followUsername.getText().toString();
                        TextView followname= (TextView) v.findViewById(R.id.follow_username);
                        LinearLayout ll = (LinearLayout) v.findViewById(R.id.follow_layout);
                        if(check.contains("true")){
                            selecctedUser.remove(followusername);
                            ll.setBackgroundResource(0);
                            followname.setTextColor(Color.BLACK);
                            name.setText("false");}
                        else{
                            name.setText("true");
                            ll.setBackgroundResource(R.drawable.selectuserback);
                            followname.setTextColor(Color.BLUE);
                            selecctedUser.add(followusername);
                        }
                    }
                });

            }else {
                holder.followLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkConnection()){
                            Intent i=new Intent(currentItem.context, Otheruserprofile.class);
                            i.putExtra("username",username);
                            i.putExtra("otherusername",holder.followUsername.getText().toString());
                            currentItem.context.startActivity(i);
                            Log.e(STRING_TAG,username);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return items_follow.size();
        }

        private boolean checkConnection() {
            Log.e(STRING_TAG,"checkConnection");
            boolean isConnected = ConnectivityReceiver.isConnected(currentItem.context);
            if(!isConnected){
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            return isConnected;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            if(isConnected){
                Intent intent= new Intent(currentItem.context,MainActivity.class);
                currentItem.context.startActivity(intent);
                ((Activity)currentItem.context).finish();
            }
            else{
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                currentItem.context.startActivity(intent);
                ((Activity)currentItem.context).finish();
            }
        }


        class FollowViewHolder extends RecyclerView.ViewHolder {
            LinearLayout followLinear;
            TextView followUsername;
            TextView followUserid;
            ImageView followUserpro;

            public FollowViewHolder(View itemView) {
                super(itemView);
                followLinear=(LinearLayout) itemView.findViewById(R.id.follow_layout);
                followUserid=(TextView) itemView.findViewById(R.id.follow_id);
                followUsername = (TextView) itemView.findViewById(R.id.follow_username);
                followUserpro=(ImageView) itemView.findViewById(R.id.follow_profilepic);
            }
        }

        public void setFilter(List<Item_follow> searchitem){
            if(items_follow.isEmpty())
                Log.e(STRING_TAG,"SetFilter empty");
            //Log.e(STRING_TAG,"SetFilter "+Integer.toString(check));
            items_follow=Collections.emptyList();
            items_follow=searchitem;
            notifyDataSetChanged();
        }


        //For retrieving the image of event.
        private class Downloadimage extends AsyncTask<Void, Void, Bitmap> {
            String name;
            int position;
            FollowItemAdapter.FollowViewHolder holder;

            public Downloadimage(FollowItemAdapter.FollowViewHolder holder, String name, int position) {
                this.position = position;
                this.holder = holder;
                this.name = name;
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                if (name.contains(" ")) {
                    name = name.replaceAll(" ", "_");
                }

                String url = SERVER_ADDRESS + "pictures/userimages/" + name + ".JPG";

                try {
                    URLConnection connection = new URL(url).openConnection();
                    connection.setConnectTimeout(1000 * 30);
                    connection.setReadTimeout(1000 * 30);
                    return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

                } catch (Exception e) {

                    return null;
                }

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Log.e("Holder set tag name _", holder.followUserpro.getTag().toString());
                if (bitmap != null && holder.followUserpro.getTag().toString().equals(name)) {

                    Log.v(LOG_TAGS, holder.followUsername.getText().toString());

                    //holder.downloadedimage.getLayoutParams().height = 90;
                    holder.followUserpro.setVisibility(View.VISIBLE);
                    holder.followUserpro.setImageBitmap(bitmap);

                } else {
                    holder.followUserpro.setVisibility(View.GONE);
                }

            }
        }
    }

    //For facebook events
    public static class FacebookItemAdapter extends RecyclerView.Adapter<FacebookItemAdapter.FacebookViewHolder> implements ConnectivityReceiver.ConnectivityReceiverListener {

        String STRING_TAG= "FacebookItemAdapter";
        String username;
        Boolean isfornearby;
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item_facebook> items = Collections.emptyList();
        Item_facebook currentItem;
        private boolean checkConnection() {
            Log.e(STRING_TAG,"checkConnection");
            boolean isConnected = ConnectivityReceiver.isConnected(currentItem.context);
            if(!isConnected){
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            return isConnected;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            if(isConnected){
                Intent intent= new Intent(currentItem.context,MainActivity.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            else{
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
        }

        // Constructor to inflate layout of each item in RecyclerView
        public FacebookItemAdapter(Context context, List<Item_facebook> items,String name,Boolean isfornearby) {
            inflater = LayoutInflater.from(context);
            this.items = items;
            this.username =name;
            this.isfornearby=isfornearby;
        }

        //create a view holder of items
        @Override
        public FacebookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.v(LOG_TAG, "onCreateViewHolder called.");
            View view = inflater.inflate(R.layout.allevents_details, parent, false);

            FacebookViewHolder holder = new FacebookViewHolder(view);

            return holder;
        }

        //binds all the views from view holder to form a single view and show the binded view
        @Override
        public void onBindViewHolder(final FacebookViewHolder holder, final int position) {
            Log.v(LOG_TAG, "onBindViewHolder called.");
            currentItem = items.get(position);

            holder.downloadedimage.setVisibility(View.GONE);
            new Downloadimagefacebook(holder, currentItem.picpath, position).execute();
            holder.eventLocation.setText(currentItem.eventLocation);
            holder.eventLabel.setText(currentItem.eventLabel);
            holder.eventDate.setText(currentItem.eventDate);
            holder.eventCategory.setText(currentItem.eventCategory);
            holder.eventOrganizer.setText(currentItem.eventOrganizer);
            holder.eventId.setText(currentItem.eventId);
            holder.eventDelete.setVisibility(View.GONE);
            holder.fbeventpath.setText(currentItem.picpath);
            holder.longitude.setText(String.valueOf(currentItem.longitude));
            holder.latitude.setText(String.valueOf(currentItem.latitude));
            holder.description.setText(currentItem.description);
            holder.eventView.setText(String.valueOf(currentItem.viewcount));
            if(isfornearby){
                String holderdistance=Float.toString(currentItem.distance);
                holder.distancevalue.setText(holderdistance);
                holder.distancetext.setVisibility(View.VISIBLE);
                holder.distancevalue.setVisibility(View.VISIBLE);
            }
            holder.eventLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkConnection()){
                        Intent intent=new Intent (currentItem.context,EventDetails.class);
                        intent.putExtra("eventId",holder.eventId.getText().toString());
                        intent.putExtra("eventLabel",holder.eventLabel.getText().toString());
                        intent.putExtra("eventLocation",holder.eventLocation.getText().toString());
                        intent.putExtra("eventDate",holder.eventDate.getText().toString());
                        intent.putExtra("eventCategory",holder.eventCategory.getText().toString());
                        intent.putExtra("eventOrganizer",holder.eventOrganizer.getText().toString());
                        intent.putExtra("latitude",holder.latitude.getText().toString());
                        intent.putExtra("longitude",holder.longitude.getText().toString());
                        intent.putExtra("path",holder.fbeventpath.getText().toString());
                        intent.putExtra("description",holder.description.getText().toString());
                        intent.putExtra("attendingcount",holder.eventView.getText().toString());
                        intent.putExtra("username",username);
                        /*try{
                        intent.putExtra("eventImage", ((BitmapDrawable)holder.downloadedimage.getDrawable()).getBitmap());}
                        catch (Exception e){
                            Log.e(STRING_TAG,"image not found");
                        }*/
                        intent.putExtra("check",false);
                        currentItem.context.startActivity(intent);
                    }

                }

            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /* ViewHolder for this adapter */
        class FacebookViewHolder extends RecyclerView.ViewHolder {
            LinearLayout eventLinear;
            TextView eventLabel;
            TextView eventLocation;
            TextView eventDate;
            TextView eventOrganizer;
            ImageButton eventDelete;
            TextView eventCategory;
            ImageView downloadedimage;
            TextView eventId;
            TextView eventView;
            TextView distancetext;
            TextView distancevalue;
            TextView fbeventpath;
            TextView longitude;
            TextView latitude;
            TextView description;

            public FacebookViewHolder(View itemView) {
                super(itemView);
                eventLinear=(LinearLayout) itemView.findViewById(R.id.alllinear1);
                eventId=(TextView) itemView.findViewById(R.id.alleventId);
                eventCategory=(TextView) itemView.findViewById(R.id.alleventCategory);
                eventLabel = (TextView) itemView.findViewById(R.id.alleventLabel);
                eventLocation = (TextView) itemView.findViewById(R.id.alleventLocation);
                eventDate=(TextView) itemView.findViewById(R.id.alleventDate);
                eventOrganizer=(TextView) itemView.findViewById(R.id.alleventOrganizer);
                eventView= (TextView) itemView.findViewById(R.id.alleventView);
                eventDelete=(ImageButton) itemView.findViewById(R.id.alleventDelete);
                downloadedimage=(ImageView) itemView.findViewById(R.id.alldownloadedimage);
                distancetext=(TextView) itemView.findViewById(R.id.distanceid);
                distancevalue=(TextView) itemView.findViewById(R.id.distancevalue);
                fbeventpath=(TextView) itemView.findViewById(R.id.fbeventPath);
                longitude=(TextView) itemView.findViewById(R.id.fbeventLong);
                latitude=(TextView) itemView.findViewById(R.id.fbeventLat);
                description=(TextView) itemView.findViewById(R.id.fbeventDesc);
            }
        }


        //For retrieving the image of event.
        private class Downloadimagefacebook extends AsyncTask<Void, Void, Bitmap>
        {
            String phototpath;
            int position;
            FacebookViewHolder holder;
            public Downloadimagefacebook(FacebookViewHolder holder, String path, int position)
            {
                this.position=position;
                this.holder=holder;
                this.phototpath=path;
            }
            @Override
            protected Bitmap doInBackground(Void... params) {
                try{
                    URLConnection connection=new URL(phototpath).openConnection();
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
                    Log.v(LOG_TAGS, "Photo received.");
                    holder.downloadedimage.setVisibility(View.VISIBLE);
                    holder.downloadedimage.setImageBitmap(bitmap);
                }
                else
                {
                    holder.downloadedimage.setVisibility(View.GONE);
                }
            }
        }
    }
}










