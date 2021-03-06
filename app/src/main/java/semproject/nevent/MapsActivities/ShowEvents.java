package semproject.nevent.MapsActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.internal.BoltsMeasurementEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import semproject.nevent.EventRecyclerView;
import semproject.nevent.HomePage;
import semproject.nevent.R;

import static semproject.nevent.HomePage.staticadapter;
import static semproject.nevent.HomePage.staticeventRecyclerView;

import static semproject.nevent.Recent.extracteventList;
import static semproject.nevent.Recent.extractlatitude;
import static semproject.nevent.Recent.extractlongitude;
import static semproject.nevent.Recent.latitude;
import static semproject.nevent.Recent.longitude;
import static semproject.nevent.FacebookEvents.fbeventList;
import static semproject.nevent.FacebookEvents.fblatitude;
import static semproject.nevent.FacebookEvents.fblongitude;

public class ShowEvents extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    static public List<Double> la=new ArrayList<>();
    static public List<Double> ln=new ArrayList<>();
    static public List<Double> fbla=new ArrayList<>();
    static public List<Double> fbln=new ArrayList<>();
    String eventname;
    static public List<Float> distance=new ArrayList<>();
    static public List<Float> fbdistance=new ArrayList<>();
    List<Marker> markerArray=new ArrayList<>();
    Button nearBybutton;
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    static public Boolean isFbpresent=false;

/*    double[] la;
    double[] ln;*/
    /*float[] distance=null;*/
    String username;
    int id=2;

    Location mLastLocation,mCurrentLocation;

    public ShowEvents(){
        staticeventRecyclerView=new EventRecyclerView();
        staticadapter=new EventRecyclerView.AllItemAdapter();
        la.clear();
        ln.clear();
        fbla.clear();
        fbln.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);
        Intent intent = getIntent();
        nearBybutton=(Button) findViewById(R.id.nearbylist);
        username = intent.getStringExtra("username");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };
        /*ShowNearbyEvents();*/
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        } else {
            //Create the LocationRequest object

            mLocationRequest = LocationRequest.create()

                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                    .setInterval( 1000)

                    //10 Seconds in millisecond

                    .setFastestInterval(10 * 1000);
            client =new GoogleApiClient.Builder(this)

                    .addConnectionCallbacks(this)

                    .addOnConnectionFailedListener(this)

                    .addApi(LocationServices.API)

                    .build();
            client.connect();
            locateMyLocation();


        }
        //lets imagine a function which shows all events


        //infowindow click listener (this is for showing decriptions os insted of toast put thedescription from online db
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //TODO:add your desiredcode here and removve thetoast.
                Toast.makeText(ShowEvents.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        //end of infowindow clicklistener
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if(currentAccessToken!=null){
            isFbpresent=true;
            ShowNearbyEvents(true);
            Log.e("Facebook","Loggedin");
        }
        else{
            Log.e("Facebook","NearBy event not found");
        }
    }


    public void ShowNearbyEvents(Boolean isFbpresent)
    {
        if(isFbpresent){
            mainWork(fblatitude,fblongitude,fblatitude,fblongitude,fbeventList,isFbpresent);
        }else{
            mainWork(latitude,longitude,extractlatitude,extractlongitude,extracteventList,isFbpresent);
        }

    }

    public void mainWork(List<Double> latitude,List<Double> longitude,List<Double> extractlatitude,List<Double> extractlongitude,List<String> extracteventList,Boolean isFbpresent){
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 9.0f));            ;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f), 2000, null);
        LatLng pos= new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        Log.d("current",mCurrentLocation.toString());
        Log.d("current long","value"+mCurrentLocation.getLatitude());

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(pos)
                .radius(5000)
                .strokeColor(Color.rgb(0, 136, 255))
                .fillColor(Color.argb(20, 0, 136, 255)));
        int i=0;
        Double longs;

        for (double lat:latitude) {
            longs = longitude.get(i);
            //mMap.addMarker(new MarkerOptions().position(Mark).title(EventName));
            LatLng latlang = new LatLng(lat,longs);
            float results[] = new float[1];
            Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                    latlang.latitude, latlang.longitude,
                    results);
            if (results[0] < 5000)
            {
                for(double lat2:extractlatitude)
                {

                    if ((Double.compare(lat, lat2) == 0) && (Double.compare(longs, extractlongitude.get(i)) == 0))
                    {

                        eventname=extracteventList.get(i);
                    }
                }
                Log.e("MapNear",eventname);
                Log.e("MapNear","value"+latlang.latitude);
                Log.e("MapNear","5 km");
                //markerArray.add(mMap.addMarker(new MarkerOptions().position(latlang).title(eventname)));
                Marker marker=mMap.addMarker(new MarkerOptions().position(latlang).title(eventname));
                markerArray.add(marker);
                if(isFbpresent){
                    if(fbla.add(lat))
                        Log.e("MapNear",Double.toString(lat));
                    fbln.add(longs);
                    fbdistance.add(results[0]);
                }else{
                    if(la.add(lat))
                        Log.e("MapNear",Double.toString(lat));
                    ln.add(longs);
                    distance.add(results[0]);
                }

            }
            i++;
        }Log.e("MapNear","end.............");
    }

   /* public List<Double> getLa() {
        Log.e("Getlatitude","inside");
        if(la.isEmpty())
            Log.e("Getlatitude","inside null");

        return la;
    }

    public List<Float> getDistance() {
        return distance;
    }



    public List<Double> getLn() {
        for(double longs:ln){
            Log.e("GetLongitude",Double.toString(longs));
        }
        return ln;
    }*/


/*    public void ShowNearbyEvents()
    {
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .radius(10000)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
        //ADD visible(false) later
        //KEEP LOOP SO THAT DISTANCE BETWEEN ALL MARKERS ARE COMPUTED
        float results[] = new float[1];

        Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                latLongB.latitude, latLongB.longitude,
                results);
        if(results[0]<50000)

        {
            //make the markers visible otherwise invisible
            *//*
                             BACK UP PLAN




          private List<Marker> markers = new ArrayList<>();

// ...

private void drawMap(LatLng latLng, List<LatLng> positions) {
    for (LatLng position : positions) {
        Marker marker = mMap.addMarker(
                new MarkerOptions()
                        .position(position)
                        .visible(false)); // Invisible for now
        markers.add(marker);
    }

    //Draw your circle
    Circle circle = mMap.addCircle(new CircleOptions()
            .center(latLng)
            .radius(400)
            .strokeColor(Color.rgb(0, 136, 255))
            .fillColor(Color.argb(20, 0, 136, 255)));

    for (Marker marker : markers) {
        if (SphericalUtil.computeDistanceBetween(latLng, marker.getPosition()) < 400) {
            marker.setVisible(true);
        }
    }
}



        *//*
        }



    }
    */

    //CODE FOR SHOWING CURRENT LOCATION
    private void locateMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }//if this line is not put error is shownS

        mMap.setMyLocationEnabled(true);


//CODE FOR RESTRICATION WITHIN NEPAL IN CERTAIN ZOOM LEVEL
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                /*Location mylocation = mMap.getMyLocation();
                LatLng myLatLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(myLatLng).zoom(17).bearing(mylocation.getBearing()).build();
                mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(myPosition));*/

                //this conditon is removed in the onLocaitonChanged Method
             /*   if (cameraPosition.zoom < 7.0f) {


                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(27.7172, 85.3240), 14.0f));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f), 2000, null);

                }*/


            }


        });
    }

    //??
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locateMyLocation();
    }


//????????????????

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;



    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Permission", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        mCurrentLocation=mLastLocation;
        if (mLastLocation != null) {
            Log.d("Location",mLastLocation.toString());

        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        ShowNearbyEvents(false);
        updateWithToken(AccessToken.getCurrentAccessToken());

    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed OnConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client.isConnected()) {

            LocationServices.FusedLocationApi.

                    removeLocationUpdates(client, this);

            client.disconnect();


        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                client, mLocationRequest, this);
        Toast.makeText(this, "Request", Toast.LENGTH_SHORT).show();
    }

    public void nearbylistbutton(View view)
    {
        staticeventRecyclerView=new EventRecyclerView();
        staticadapter=new EventRecyclerView.AllItemAdapter();
        mMap.clear();
        if(markerArray.isEmpty()){
            String toastMesg = "No events to show";
            Toast toast = Toast.makeText(view.getContext(), toastMesg, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
        else{
            Intent intent= new Intent(this,HomePage.class);
            intent.putExtra("id",id);
            intent.putExtra("username",username);
           /* intent.putExtra("latitude",la);
            intent.putExtra("longitude",ln);
            intent.putExtra("distance",distance);*/
            finish();
            startActivity(intent);
        }

    }
}
