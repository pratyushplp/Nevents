package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import semproject.nevent.Connection.ConnectivityReceiver;
import semproject.nevent.Connection.InternetConnection;
import semproject.nevent.EventRecyclerView;
import semproject.nevent.MainActivity;
import semproject.nevent.MapsActivities.ShowEvents;
import semproject.nevent.R;

import static semproject.nevent.HomePage.staticadapter;
import static semproject.nevent.HomePage.staticeventRecyclerView;

import static semproject.nevent.Recent.extracteventCategory;
import static semproject.nevent.Recent.extracteventDate;
import static semproject.nevent.Recent.extracteventId;
import static semproject.nevent.Recent.extracteventList;
import static semproject.nevent.Recent.extracteventLocation;
import static semproject.nevent.Recent.extracteventOrganizer;
import static semproject.nevent.Recent.extractlatitude;
import static semproject.nevent.Recent.extractlongitude;
import static semproject.nevent.Recent.extractviewcount;
import static semproject.nevent.FacebookEvents.fbeventCategory;
import static semproject.nevent.FacebookEvents.fbeventDate;
import static semproject.nevent.FacebookEvents.fbeventId;
import static semproject.nevent.FacebookEvents.fbeventList;
import static semproject.nevent.FacebookEvents.fbeventLocation;
import static semproject.nevent.FacebookEvents.fbevent_descrp;
import static semproject.nevent.FacebookEvents.fbevent_org;
import static semproject.nevent.FacebookEvents.fbevent_picpath;
import static semproject.nevent.FacebookEvents.fblatitude;
import static semproject.nevent.FacebookEvents.fblongitude;
import static semproject.nevent.FacebookEvents.fbviewcount;


/**
 * Created by Aayush on 3/11/2017.
 */

public class NearByList extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private RecyclerView mRecyclerView;
    private RecyclerView mfbRecyclerView;
    String STRING_TAG="NearByList";
    String username;
    public static List<String> neareventId=new ArrayList<>();
    public static List<String>neareventList=new ArrayList<>();
    public static List<String>neareventLocation=new ArrayList<>();
    public static List<String>neareventDate=new ArrayList<>();
    public static List<String>neareventCategory=new ArrayList<>();
    public static List<String>neareventOrganizer=new ArrayList<>();
    public static List<Integer>nearviewcount=new ArrayList<>();
    public static List<Double>nearlatitude=new ArrayList<>();
    public static List<Double>nearlongitude=new ArrayList<>();
    public static List<String>neareventPath=new ArrayList<>();
    public static List<String>neareventDescrip=new ArrayList<>();



    public NearByList() {
        staticeventRecyclerView=new EventRecyclerView();
        staticadapter=new EventRecyclerView.AllItemAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        staticeventRecyclerView=new EventRecyclerView();
        staticadapter=new EventRecyclerView.AllItemAdapter();
        username = getArguments().getString("username");
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        // Required empty public constructor
        // BEGIN_INCLUDE(initializeRecyclerView)


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.

        //second recycler
        mfbRecyclerView = (RecyclerView) rootView.findViewById(R.id.nearby_recycler_view);
        if (mfbRecyclerView != null) {
            mfbRecyclerView.setHasFixedSize(true);
        }
        mfbRecyclerView.setLayoutManager(
                new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL, false));

        //first recycler
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL, false));

        listenerFunction(false,ShowEvents.la,ShowEvents.ln,extracteventId,
                extracteventList,extracteventLocation,extracteventDate,extracteventCategory,
                extracteventOrganizer,extractviewcount,extractlatitude,extractlongitude,null,null);

        if(ShowEvents.isFbpresent){
            Log.e("Nearlistener","facebook");
            listenerFunction(ShowEvents.isFbpresent,ShowEvents.fbla,ShowEvents.fbln,fbeventId,
                    fbeventList,fbeventLocation,fbeventDate,fbeventCategory,fbevent_org,
                    fbviewcount,fblatitude,fblongitude,fbevent_picpath,fbevent_descrp);}
        return rootView;
    }
    public void retreiveFromDatabase(Boolean isFbpresent){
        float dist;
        Log.e(STRING_TAG,"nearbydatabase");
        if(checkConnection(getContext())){
            for (int i=0;i < neareventList.size();i++)
            {
                if(isFbpresent) {
                    Log.i("FBIndexRemove "+i," "+neareventList.get(i));
                    dist = ShowEvents.fbdistance.get(i) / 1000;
                    DecimalFormat numberformat= new DecimalFormat("#.00");
                    dist=Float.parseFloat(numberformat.format(dist));
                    staticeventRecyclerView.initializeDataFacebook(neareventId.get(i),neareventList.get(i),neareventCategory.get(i),neareventLocation.get(i),neareventDate.get(i),neareventOrganizer.get(i),nearviewcount.get(i),nearlatitude.get(i),nearlongitude.get(i),neareventPath.get(i),neareventDescrip.get(i),getContext(),dist);
                    EventRecyclerView.FacebookItemAdapter fbadapter = new EventRecyclerView.FacebookItemAdapter(getContext(), staticeventRecyclerView.getItemFacebook(),username,true);
                    mfbRecyclerView.setAdapter(fbadapter);
                }else{
                    Log.i("NFBIndexRemove "+i," "+neareventList.get(i));
                    dist = ShowEvents.distance.get(i) / 1000;
                    DecimalFormat numberformat= new DecimalFormat("#.00");
                    dist=Float.parseFloat(numberformat.format(dist));
                    staticeventRecyclerView.initializeData(neareventId.get(i),neareventList.get(i),neareventCategory.get(i),neareventLocation.get(i),neareventDate.get(i),neareventOrganizer.get(i),nearviewcount.get(i),getContext(),Float.toString(dist));
                    EventRecyclerView.AllItemAdapter adapter = new EventRecyclerView.AllItemAdapter(getContext(), staticeventRecyclerView.getItem(),username,true,false);
                    mRecyclerView.setAdapter(adapter);}

            }
            neareventId.clear();
            neareventList.clear();
            neareventLocation.clear();
            neareventDate.clear();
            neareventCategory.clear();
            neareventOrganizer.clear();
            nearviewcount.clear();
            nearlatitude.clear();
            nearlongitude.clear();
            neareventDescrip.clear();
            neareventPath.clear();

        }

    }

    public void listenerFunction(Boolean isFbpresent,List<Double>la,List<Double>ln,List<String> extracteventId,
                                 List<String>extracteventList,List<String>extracteventLocation,
                                 List<String>extracteventDate,List<String>extracteventCategory ,
                                 List<String>extracteventOrganizer,List<Integer>extractviewcount ,
                                 List<Double>extractlatitude,List<Double>extractlongitude,
                                 List<String>extracteventPath,List<String>extracteventDescrip){

        Log.e("Nearlistener","insideListiner");
        int out=0;
        for(double lat1:la){
            int inside=0;
            if(extractlatitude.isEmpty())
                Log.e("Nearlistener","Null");
            else{
                Log.e("Nearlistener","NOTNULL");
                Log.e("Nearlistener ","Size"+Integer.toString(extracteventList.size()));
                for(double lat2:extractlatitude){
                    Log.e("Nearlistener ",Double.toString(lat1)+" "+Double.toString(lat2));
                    if((Double.compare(lat1,lat2)==0)&&(Double.compare(ln.get(out),extractlongitude.get(inside))==0)){
                        Log.e("Nearlistener ",Integer.toString(inside)+" "+extracteventList.get(inside));
                        nearlatitude.add(extractlatitude.get(inside));
                        nearlongitude.add(extractlongitude.get(inside));
                        neareventList.add(extracteventList.get(inside));
                        neareventCategory.add(extracteventCategory.get(inside));
                        neareventDate.add(extracteventDate.get(inside));
                        neareventId.add(extracteventId.get(inside));
                        neareventLocation.add(extracteventLocation.get(inside));
                        neareventOrganizer.add(extracteventOrganizer.get(inside));
                        nearviewcount.add(extractviewcount.get(inside));
                        if(isFbpresent){
                            neareventPath.add(extracteventPath.get(inside));
                            neareventDescrip.add(extracteventDescrip.get(inside));
                        }
                    }
                    inside++;
                }
            }

            out++;
        }
        Log.e("Nearlistener","outsideListiner.......");
        retreiveFromDatabase(isFbpresent);
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
