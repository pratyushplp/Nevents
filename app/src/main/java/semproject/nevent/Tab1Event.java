package semproject.nevent;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static semproject.nevent.HomePage.stat_forsearch_eventRecyclerView;
import static semproject.nevent.Recent.stat_forsearch_adapter;
import static semproject.nevent.SearchResultActivity.searchusername;


/**
 * Created by User on 7/4/2017.
 */

public class Tab1Event extends Fragment {
    String STRING_TAG= "Tab1Event";
    static RecyclerView tab1mRecyclerView;
    String from;



    public Tab1Event() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_tab1, container, false);
        // BEGIN_INCLUDE(initializeRecyclerView)

        RecyclerView.LayoutManager mLayoutManager;
        tab1mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tab1_recycler_view);
        if (tab1mRecyclerView != null) {
            tab1mRecyclerView.setHasFixedSize(true);
        }


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        tab1mRecyclerView.setLayoutManager(mLayoutManager);
        tab1mRecyclerView.setAdapter(stat_forsearch_adapter);

        return rootView;
    }
    public void showResult(String searchEvent){
        EventRecyclerView tab1recyclerview = new EventRecyclerView();;
        Log.i("tab1Searched Event","started");
        Log.i("tab1Searched Event","before "+ String.valueOf(stat_forsearch_adapter.getItemCount()));
        Log.i("tab1Searched Eventin",searchEvent);
        List<EventRecyclerView.Item> extractedItem=stat_forsearch_eventRecyclerView.getItem();
        final List<EventRecyclerView.Item> searchItem=new ArrayList<>();
        EventRecyclerView.AllItemAdapter searchadapter= new EventRecyclerView.AllItemAdapter();
        if(extractedItem.isEmpty())
            Log.e(STRING_TAG,"empty");
        else
            Log.e(STRING_TAG,"is not empty");

        for(EventRecyclerView.Item indevent: extractedItem){
            String eventname=indevent.eventLabel;
            Log.i("Searched item",eventname);

            if(eventname.contains(searchEvent)){
                searchItem.add(indevent);
                Log.i(STRING_TAG+"ind","Item added "+ eventname);
            }
        }
        if(searchItem.isEmpty()) {
            tab1recyclerview.emptyItems();
            searchadapter = new EventRecyclerView.AllItemAdapter(getContext().getApplicationContext(), tab1recyclerview.getItem(),"Sagun",false,false);
            tab1mRecyclerView.setAdapter(searchadapter);
            Log.i("tab1Searched", "SearchItem is empty");
        }else {
            for (EventRecyclerView.Item indevent : searchItem) {
                Log.i("tab1Searched Eventfl", indevent.eventLabel);
                tab1recyclerview.initializeData(indevent.eventId,indevent.eventLabel,indevent.eventCategory,indevent.eventLocation ,indevent.eventDate ,indevent.eventOrganizer ,indevent.viewcount ,indevent.context,"");
                searchadapter = new EventRecyclerView.AllItemAdapter(indevent.context.getApplicationContext(), tab1recyclerview.getItem(),"Sagun",false,false);
                tab1mRecyclerView.setAdapter(searchadapter);
                Log.i("tab1earched Event", indevent.eventLabel);
            }
        }
        /*stat_forsearch_adapter.setFilter(searchItem);*/
        /*mRecyclerView.setAdapter(stat_forsearch_adapter);*/
        Log.i("tab1Searched Event","after "+String.valueOf(stat_forsearch_adapter.getItemCount()));
        Log.i("tab1Searched Event","finished");
        searchItem.clear();
    }

    public void showResultTab2(String searchUser) {
        Log.i("tab1Searched User",searchUser);
        EventRecyclerView tab2recyclerview = new EventRecyclerView();

        Log.i("tab1Searched User","started");
        //Log.i("tab2Searched Event","before "+ String.valueOf(stat_forsearch_adapter.getItemCount()));
        Log.i("tab1Searched Userin",searchUser);
        List<EventRecyclerView.Item_follow> extractedItem=stat_forsearch_eventRecyclerView.getItemFollow();
        final List<EventRecyclerView.Item_follow> searchItem=new ArrayList<>();
        EventRecyclerView.FollowItemAdapter searchadapter= new EventRecyclerView.FollowItemAdapter();
        if(extractedItem.isEmpty())
            Log.e(STRING_TAG,"empty");
        else
            Log.e(STRING_TAG,"is not empty");

        for(EventRecyclerView.Item_follow indevent: extractedItem){
            String followusername=indevent.followusername;
            Log.i("Searched item",followusername);

            if(followusername.contains(searchUser)){
                searchItem.add(indevent);
                Log.i(STRING_TAG+"ind","Item added "+ followusername);
            }
        }
        if(searchItem.isEmpty()) {
            tab2recyclerview.emptyItemsFollow();
            searchadapter = new EventRecyclerView.FollowItemAdapter(getContext().getApplicationContext(), tab2recyclerview.getItemFollow(),searchusername,false);
            Tab2User.tab2mRecyclerView.setAdapter(searchadapter);
            Log.i("tab1Searched", "SearchItem is empty");
        }else {
            for (EventRecyclerView.Item_follow indevent : searchItem) {
                Log.i("tab1Searched Userfl", indevent.followusername);
                tab2recyclerview.initializeDataFollow(indevent.followuserid,indevent.followusername,indevent.followemail,getContext());
                searchadapter = new EventRecyclerView.FollowItemAdapter(indevent.context.getApplicationContext(), tab2recyclerview.getItemFollow(),searchusername,false);
                Tab2User.tab2mRecyclerView.setAdapter(searchadapter);
                Log.i("tab1Searched User", indevent.followusername);
            }
        }
        Log.i("tab1Searched User","finished");
        searchItem.clear();
    }
}
