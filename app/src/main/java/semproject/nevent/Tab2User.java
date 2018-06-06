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

import static semproject.nevent.HomePage.stat_forsearch_Useradapter;
import static semproject.nevent.HomePage.stat_forsearch_eventRecyclerView;
import static semproject.nevent.Recent.stat_forsearch_adapter;
import static semproject.nevent.SearchResultActivity.searchusername;

/**
 * Created by User on 7/4/2017.
 */

public class Tab2User extends Fragment {
    String check;
    String STRING_TAG= "Tab2User";
    static RecyclerView tab2mRecyclerView;
    String from;


    public Tab2User() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_tab2, container, false);
        // BEGIN_INCLUDE(initializeRecyclerView)

        RecyclerView.LayoutManager mLayoutManager;
        tab2mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tab2_recycler_view);
        if (tab2mRecyclerView != null) {
            tab2mRecyclerView.setHasFixedSize(true);
        }


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        tab2mRecyclerView.setLayoutManager(mLayoutManager);
        tab2mRecyclerView.setAdapter(stat_forsearch_Useradapter);
        return rootView;
    }

    public void showResult(String searchUser) {
        Log.i("tab2Searched User",searchUser);
        Log.i("tab2Searched User","started");
        EventRecyclerView tab2recyclerview = new EventRecyclerView();
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
            tab2mRecyclerView.setAdapter(searchadapter);
            Log.i("tab2Searched User", "SearchItem is empty");
        }else {
            for (EventRecyclerView.Item_follow indevent : searchItem) {
                Log.i("tab2Searched Userfl", indevent.followusername);
                tab2recyclerview.initializeDataFollow(indevent.followuserid,indevent.followusername,indevent.followemail,getContext());
                searchadapter = new EventRecyclerView.FollowItemAdapter(indevent.context.getApplicationContext(), tab2recyclerview.getItemFollow(),searchusername,false);
                tab2mRecyclerView.setAdapter(searchadapter);
                Log.i("tab2Searched Userfl", indevent.followusername);
            }
        }
        /*stat_forsearch_adapter.setFilter(searchItem);*/
        /*mRecyclerView.setAdapter(stat_forsearch_adapter);*/
        //Log.i("tab1Searched Event","after "+String.valueOf(stat_forsearch_adapter.getItemCount()));
        Log.i("tab2Searched User","finished");
        searchItem.clear();
    }

    public void showResulttab1(String searchEvent){
        EventRecyclerView tab1recyclerview = new EventRecyclerView();;
        Log.i("tab2Searched Event","started");
        Log.i("tab2Searched Event","before "+ String.valueOf(stat_forsearch_adapter.getItemCount()));
        Log.i("tab2Searched Eventin",searchEvent);
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
            Tab1Event.tab1mRecyclerView.setAdapter(searchadapter);
            Log.i("tab2Searched", "SearchItem is empty");
        }else {
            for (EventRecyclerView.Item indevent : searchItem) {
                Log.i("tab2Searched Eventfl", indevent.eventLabel);
                tab1recyclerview.initializeData(indevent.eventId,indevent.eventLabel,indevent.eventCategory,indevent.eventLocation ,indevent.eventDate ,indevent.eventOrganizer ,indevent.viewcount ,indevent.context,"");
                searchadapter = new EventRecyclerView.AllItemAdapter(indevent.context.getApplicationContext(), tab1recyclerview.getItem(),"Sagun",false,false);
                Tab1Event.tab1mRecyclerView.setAdapter(searchadapter);
                Log.i("tab2Searched Event", indevent.eventLabel);
            }
        }
        /*stat_forsearch_adapter.setFilter(searchItem);*/
        /*mRecyclerView.setAdapter(stat_forsearch_adapter);*/
        Log.i("tab2Searched Event","after "+String.valueOf(stat_forsearch_adapter.getItemCount()));
        Log.i("tab2Searched Event","finished");
        searchItem.clear();
    }
}