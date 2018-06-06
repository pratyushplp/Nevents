package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;

/**
 * Created by User on 8/11/2017.
 */

public class FacebookEvents extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{
    String STRING_TAG="FacebookEvents";

    private RecyclerView mRecyclerView;
    String username;
    public static List<String> fbeventList=new ArrayList<>();
    public static List<String>fbeventLocation=new ArrayList<>();
    public static List<String>fbeventDate=new ArrayList<>();
    public static List<String>fbeventCategory=new ArrayList<>();
    public static List<String>fbeventId=new ArrayList<>();
    public static List<Integer>fbviewcount=new ArrayList<>();

    //for page events
    public static List<String>fbevent_org=new ArrayList<>();
    public static List<String>fbevent_descrp=new ArrayList<>();
    public static List<String>fbevent_picpath=new ArrayList<>();
    public static List<Double>fblatitude=new ArrayList<>();
    public static List<Double>fblongitude=new ArrayList<>();
    int start=0;

    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    TextView denoteempty;

    EventRecyclerView eventRecyclerView;

    public FacebookEvents(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventRecyclerView = new EventRecyclerView();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
        username = getArguments().getString("username");

        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);
        denoteempty=(TextView) rootView.findViewById(R.id.empty_text_allevents);
        // BEGIN_INCLUDE(initializeRecyclerView)
        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadfacebook();
        return rootView;
    }

    void loadfacebook(){
        fbeventLocation.clear();
        fbeventList.clear();//name
        fbeventId.clear();
        fbeventDate.clear();
        fbeventCategory.clear();
        fbviewcount.clear();
        fbevent_org.clear();
        start=0;
        eventRecyclerView = new EventRecyclerView();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            JSONArray posts = object.getJSONObject("likes").optJSONArray("data");
                                    /*String proname= object.getString("name");
                                    Log.e("Profile name",proname);*/
                            for (int i = 0; i<posts.length(); i++) {
                                JSONObject post = posts.optJSONObject(i);
                                String id = post.optString("id");
                                String pagecategory = post.optString("category");
                                String pagename = post.optString("name");
                                eventsofpage(id,AccessToken.getCurrentAccessToken(),pagename);
                                //int count = post.optInt("likes");
                                // print id, page name and number of like of facebook page
                                //Log.e("Facebook","id "+ id+ "name "+name+ " category"+ category );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();//contains details to be extracted
        parameters.putString("fields", "likes{id,name,category}");
        request.setParameters(parameters);
        request.executeAsync();
    }

    void eventsofpage(String id, AccessToken accessToken,final String pagename){
        String path="/"+id+"/events";
        Bundle parameters = new Bundle();//contains details to be extracted
        parameters.putString("fields", "name,attending_count,start_time,cover{source},category,description,place{name,location{latitude,longitude,city}}");
        GraphRequestAsyncTask eventrequest= new GraphRequest(accessToken,path,parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject graphResponse = response.getJSONObject();
                        String postId = null;
                        try {
                            JSONArray eventsJSON= graphResponse.getJSONArray("data");
                            for (int i = 0; i<eventsJSON.length(); i++) {
                                fbevent_org.add(pagename);
                                Log.e("FacebookEvents","insidefun4");
                                JSONObject indiv = eventsJSON.optJSONObject(i);
                                int event_id;
                                try{
                                    event_id=indiv.getInt("id");
                                }
                                catch(Exception e){
                                    event_id=0;
                                }
                                fbeventId.add(Integer.toString(event_id));
                                Log.e("FacebookEvents","id "+ event_id);

                                String event_name;
                                try{
                                    event_name = indiv.optString("name");}
                                catch(Exception e){
                                    event_name="";
                                }
                                fbeventList.add(event_name);
                                Log.e("FacebookEvents","name "+ event_name);

                                //place{name,location{latitude,longitude,city}}
                                String location_name;
                                JSONObject place=null;
                                try{
                                    place= indiv.getJSONObject("place");
                                    location_name= place.getString("name");}
                                catch (Exception e){
                                    location_name=" ";
                                }
                                fbeventLocation.add(location_name);
                                Log.e("FacebookEvents", " location: " + location_name);

                                Double lat,longi;
                                try {
                                    JSONObject location = place.getJSONObject("location");
                                    lat = location.getDouble("latitude");
                                    longi = location.getDouble("longitude");
                                }
                                catch (Exception e){
                                    longi=0.0;
                                    lat=0.0;
                                }
                                fblatitude.add(lat);
                                fblongitude.add(longi);
                                Log.e("FacebookEvents", "lat " + Double.toString(lat));
                                Log.e("FacebookEvents", "log " + Double.toString(longi));

                                int count_view;
                                try {
                                    count_view = indiv.getInt("attending_count");
                                }catch (Exception e){
                                    count_view=0;
                                }
                                fbviewcount.add(count_view);
                                Log.e("FacebookEvents", "view " + Integer.toString(count_view));

                                String date;
                                try {
                                    String[] slipted;
                                    date = indiv.getString("start_time");
                                    slipted=date.split("T");
                                    date= slipted[0];
                                }catch (Exception e){
                                    date=" ";
                                }
                                fbeventDate.add(date);
                                Log.e("FacebookEvents","date "+date);

                                String category;
                                try{
                                    category=indiv.getString("category");
                                }catch (Exception e){
                                    category="Other";
                                }
                                fbeventCategory.add(category);
                                Log.e("FacebookEvents","category "+category);

                                String description;
                                try{
                                    description = indiv.getString("description");
                                }catch (Exception e){
                                    description=" ";
                                }
                                fbevent_descrp.add(description);

                                String picpath;
                                try {
                                    JSONObject coverpic = indiv.getJSONObject("cover");
                                    picpath = coverpic.getString("source");
                                }catch (Exception e){
                                    picpath=" ";
                                }
                                fbevent_picpath.add(picpath);
                                Log.e("FacebookEvents",picpath);
                            }
                            addforFacebook();
                            Log.e("FacebookEvents","end.............");
                        } catch (JSONException e) {
                            Log.i("Facebook error", "JSON error " + e.getMessage());
                        }
                    }
                }
        ).executeAsync();
    }

    void addforFacebook(){
        if(checkConnection(getActivity())) {
            if (fbeventList.isEmpty()) {
                eventRecyclerView.emptyItemsFacebook();
                RecyclerView.Adapter mAdapter = new EventRecyclerView.FacebookItemAdapter(getContext(), eventRecyclerView.getItemFacebook(), username,false);
                mRecyclerView.setAdapter(mAdapter);
                denoteempty.setVisibility(View.VISIBLE);
            }
            else {
                Log.i("TValue start.... ",Integer.toString(fbeventList.size()));
                for (int i=start ; i < fbeventList.size(); i++) {
                    start++;
                    denoteempty.setVisibility(View.GONE);
                    Log.i("FValue element " + i, fbeventId.get(i));
                    Log.i("FValue element " + i, fbeventList.get(i));
                    Log.i("FValue element " + i, fbeventCategory.get(i));
                    Log.i("FValue element " + i, fbeventLocation.get(i));
                    Log.i("FValue element " + i, fbeventDate.get(i));
                    Log.i("FValue element " + i, fbevent_org.get(i));
                    Log.i("FValue element " + i, Integer.toString(fbviewcount.get(i)));
                    Log.i("FValue element " + i, Double.toString(fblatitude.get(i)));
                    Log.i("FValue element " + i, Double.toString(fblongitude.get(i)));
                    Log.i("FValue element " + i, fbevent_picpath.get(i));

                    eventRecyclerView.initializeDataFacebook(fbeventId.get(i), fbeventList.get(i), fbeventCategory.get(i), fbeventLocation.get(i), fbeventDate.get(i),
                            fbevent_org.get(i), fbviewcount.get(i), fblatitude.get(i), fblongitude.get(i), fbevent_picpath.get(i), fbevent_descrp.get(i), getContext(),0);
                    RecyclerView.Adapter mAdapter = new EventRecyclerView.FacebookItemAdapter(getContext(), eventRecyclerView.getItemFacebook(), username,false);
                    mRecyclerView.setAdapter(mAdapter);
                }
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
