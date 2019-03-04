package com.example.nenad.projekat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerMapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,RoutingListener {

    private GoogleMap mMap;
    private  DatabaseReference rejtingref;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Button mCallUber,mSlika;
    private Button mCancel;
    private Button mLogout;
    private Button mZakazaneVoznje;
    private String vozacPrihvatioZahtev;
    private LatLng start;
    private LatLng destination;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private PolylineOptions polylineOptions=null;
    private Customer customer;
    private ArrayList<Driver> listaVozaca = new ArrayList<Driver>();
    //private Set<Driver> listaVozaca=new HashSet<Driver>();
    private ArrayList<String> listaVozacaId = new ArrayList<String>();
    private boolean callonce=true;
    private int duration;
    private int distance;
    private TrenutnoZahtevanaVoznja zahtevanaVoznja;
    private Button mChat;
    private Spinner chatWithDrivers;
    private int pom=0;
    @Override//Odavde kreće
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_home);

        ///--------------------------interfejs
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer==null)
        Log.i("drawer", "null");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
       // MenuInflater inflater = getMenuInflater();
       //Menu action_setting;
        //inflater.inflate(R.menu.home, action_setting);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView==null)
            Log.i("navigation bar" ," null");
       navigationView.setNavigationItemSelectedListener(this);
        //-------------------------------------------------------------------------------
        polylines=new ArrayList<>();

        mCallUber = (Button) findViewById(R.id.button2);

        mCancel=(Button) findViewById(R.id.cancel);
        mLogout=(Button) findViewById(R.id.odjava);
        mChat=(Button) findViewById(R.id.button4);
        mZakazaneVoznje=(Button) findViewById(R.id.zakazanevoznje2);
       // mSlika = (Button) findViewById(R.id.slika);
        mChat.setVisibility(View.GONE);
        chatWithDrivers=(Spinner) findViewById(R.id.chat3);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customer= dataSnapshot.getValue(Customer.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                if(start==null)
                {//

                    start = place.getLatLng();
                    Marker marker=mMap.addMarker(new MarkerOptions()
                            .position(start)
                            .draggable(true));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(start));//mkd

                }
                else if(destination==null)
                {
                    destination=place.getLatLng();
                    Marker marker=mMap.addMarker(new MarkerOptions()
                            .position(destination)
                            .draggable(true));
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();//zumiranje mape za dva markera, postupak je da se napravi builder
                    builder.include(start);
                    builder.include(destination);
                    LatLngBounds latLngBounds = builder.build();
                    int padding = (int)(getResources().getDisplayMetrics().widthPixels * 0.2);//izracunavanje paddinga da ne bi markeri bili na ivici
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,padding));//pomeranje kamere uz animaciju
                    drawDirections(start,destination);//iscrtavanje rute

                }


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });



        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("DriversAvailable");
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelRequest();

            }
        });

        mCallUber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(destination!=null&&start!=null && zahtevanaVoznja!=null) {
                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest").child(userid).child("start");

                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userid, new GeoLocation(start.latitude, start.longitude));

                    ref = FirebaseDatabase.getInstance().getReference("CustomerRequest").child(userid).child("destination");
                    geoFire = new GeoFire(ref);

                    geoFire.setLocation(userid, new GeoLocation(destination.latitude, destination.longitude));
                    Toast.makeText(CustomerMapsActivity.this, "" + listaVozaca.size(), Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(CustomerMapsActivity.this, PrikazVozacaActivity.class);
                    intent.putExtra("lista", listaVozaca);
                    intent.putExtra("zahtevanaVoznja",zahtevanaVoznja);
                    startActivity(intent);
                    finish();


//                for (Driver vozac : listaVozaca)
//                {
//                    final DatabaseReference referencaposebna = FirebaseDatabase.getInstance().getReference().child("Users")
//                            .child("Drivers").child(vozac.getId()).child("Zahtev");
//                    referencaposebna.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if(dataSnapshot.exists())
//                                Toast.makeText(CustomerMapsActivity.this,"Vozac ima zahtev",Toast.LENGTH_LONG).show();
//                            else
//                            {
//                                dataSnapshot.getRef().setValue(customer);
//
//                            }
//                            referencaposebna.removeEventListener(this);
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }


                }
                else
                    Toast.makeText(CustomerMapsActivity.this,"Izaberite pocetnu i krajnjy lokaciju", Toast.LENGTH_LONG).show();


            }
        });
       /* mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(CustomerMapsActivity.this,CustomerLoginActivity.class);
                startActivity(intent);
                finish();

            }
        });*/
        DatabaseReference prihvacen = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Prihvacen");
        prihvacen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String pom=(String)dataSnapshot.getValue();
                    if(!pom.equals("rejtuj"))
                    {
                        mChat.setVisibility(View.VISIBLE);
                        vozacPrihvatioZahtev=pom;
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        rejtingref = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Rejtuj");
        rejtingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(CustomerMapsActivity.this, "Rejting", Toast.LENGTH_LONG).show();

                    vozacPrihvatioZahtev = null;
                    mChat.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Prihvacen").removeValue();
                    Intent intent = new Intent(CustomerMapsActivity.this,RejtingActivity.class);
                    intent.putExtra("driver",dataSnapshot.getValue(Driver.class));

                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapsActivity.this,ChatActivity.class);
                intent.putExtra("type","customers");
                intent.putExtra("visit_user_id",vozacPrihvatioZahtev);
                intent.putExtra("user_name",FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });

//        chatWithDrivers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent chat = new Intent(CustomerMapsActivity.this, AllDriversActivity.class);
//                //chat.putExtra("lista", listaVozaca);
//                startActivity(chat);
//            }
//        });
//        mZakazaneVoznje.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CustomerMapsActivity.this,PregledZakazanihVoznjiKorisnikActivity.class);
//                startActivity(intent);
//            }
//        });
//        mSlika.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CustomerMapsActivity.this,SettingsActivity.class);
//                intent.putExtra("type","customers");
//                startActivity(intent);
//            }
//        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] niz = new String[2];
            niz[0]=Manifest.permission.ACCESS_FINE_LOCATION;

            niz[1]= Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(this,niz,1 );




            return;
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        mapFragment.getMapAsync(this);




        //----------------------------------------------

        //---------------------------------------------


    }



    private void drawDirections(LatLng start, LatLng destination) {
        Routing routing = new Routing.Builder().key("AIzaSyAajs_KEvEhwgzbYE-PyyHXhXlZqoi4cxc")
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start,destination)
                .build();
        routing.execute();
    }

    //Izvlačenje lokacije iz adrese, radi perfektno povratna vrednost geografska širina i dužina u okviru LatLng
    public LatLng getLocationFromAddress(Geocoder geocoder,String strAddress) {

        Geocoder coder =geocoder;
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    @Override
    //Ovaj override neophodan zbog API-a, proverava se da li su dozvole date i ako nisu treba posebna logika da se obradi
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
    {
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map2);
            mapFragment.getMapAsync(this);


        }



    }






    @Override
    //Ova funkcija se poziva kada je mapa spremna, na ovo mesto je pomeren poziv setMyLocationEnabled, trebalo bi da je u
    //onCreate ali tamo je mMap null pa ovde je logičnije
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(CustomerMapsActivity.this,"no access", Toast.LENGTH_LONG);
            return;
        }
        mMap.setMyLocationEnabled(true);



    }
    public String requestDirection(LatLng start,LatLng destination) throws JSONException {
        String origin = "origin="+start.latitude+","+start.longitude;
        String dest="destination="+destination.latitude+","+destination.longitude;
        String reqUrl= "https://maps.googleapis.com/maps/api/directions/json?"+origin+"&"+destination;
        String response="";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream=null;
        try{
           URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null)
            {
                stringBuffer.append(line);
            }
            response=stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        }
        catch (Exception e)
        {

        }
        finally {
            if (inputStream!=null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            httpURLConnection.disconnect();
        }


        return response;
    }//staro


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    //Svaki put se poziva prilikom promene lokacije, čak i ako korisnik stoji u mesto opet se poziva na interval koji se postavi
    public void onLocationChanged(Location location) {
        if(mLastLocation==null)
        {
            LatLng mLatLng= new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
        mLastLocation=location;


        //String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(vozacPrihvatioZahtev!=null) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DriversAvailable");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.getLocation(vozacPrihvatioZahtev, new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {
                    if(location!=null)
                        mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(location.latitude,location.longitude)));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
//
       MapRefresh();
       drawStartDestinationMarkers();
//       MapRefresh();//brisanje mape i ponovo iscrtavanje potrebnih markera
//

//        geoFire.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
//            @Override
//            public void onComplete(String key, DatabaseError error) {
//
//            }
//        });
        if(callonce) {
            QueryDrivers();
            callonce=false;
        }











    }
    public  void drawStartDestinationMarkers()//za start i destinaciju markeri se posebno iscrtavaju jer se svi markeri brisu u OnLocationChanged
    {
        if (start!=null)
            mMap.addMarker(new MarkerOptions().draggable(true).position(start));
        if (destination!=null)
            mMap.addMarker(new MarkerOptions().draggable(true).position(destination));
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if(mGoogleApiClient.isConnected())
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onStop() {

        super.onStop();
        Log.i("onStop","OnStop");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


    }

////!!!!!!!!!!!!!///////////////////////////////////////Funkcije dodate za Routing Listener, vezano za iscrtavanje ruta
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = 0;

            polylineOptions = new PolylineOptions();
            polylineOptions.color(getResources().getColor(COLORS[colorIndex]));
            polylineOptions.width(10 + i * 3);
            polylineOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polylineOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            ObradaInfromacijaOVoznji(route.get(i).getDistanceValue(),route.get(i).getDistanceValue());
        }




    }

    @Override
    public void onRoutingCancelled() {

    }
    /////!!!!!!!!!!!!/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void CancelRequest()
    {
       // polylines.clear();
       // if(polylines.size()>0) {
           // for (Polyline poly : polylines) {
                //poly.remove();
            //}
       // }
       // polylines.clear();
      //  polylines=new ArrayList<>();
        polylineOptions=null;


        start=null;
        destination=null;
        zahtevanaVoznja=null;
        MapRefresh();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),11));

        String userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("CustomerRequest").child(userid).removeValue();

    }
    public void MapRefresh()
    {
        mMap.clear();//svi se markeri brisu da se refresuje, inace ostavlja i stare markere
        drawStartDestinationMarkers();
        if(polylineOptions!=null)
            mMap.addPolyline(polylineOptions);
    }
    public void QueryDrivers()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");
        GeoFire geoFire = new GeoFire(ref);
        HashSet set = new HashSet();

        GeoQuery geoQuery= geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),3);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                listaVozaca.clear();
                DatabaseReference referenca = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                referenca.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // for (DataSnapshot ds : dataSnapshot.getChildren())
                        listaVozaca.add(dataSnapshot.getValue(Driver.class));
                        Toast.makeText(CustomerMapsActivity.this,"Novi vozac",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
              Toast.makeText(CustomerMapsActivity.this,"Izaso vozac",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    public void ObradaInfromacijaOVoznji(int distance, int duration)
    {
        zahtevanaVoznja = new TrenutnoZahtevanaVoznja(customer,start.latitude,start.longitude,destination.latitude,destination.longitude,distance,duration);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("onReusme","OnResume");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

    }
    @Override
    protected  void onPause()
    {
        super.onPause();
        Log.i("onPause","OnPause");

    }
    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        Log.i("onDestroy","OnDestroy");
        Toast.makeText(CustomerMapsActivity.this,"destroy",Toast.LENGTH_LONG).show();

    }



    /////////////--------------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.chat3) {
            //Toast.makeText(CustomerMapsActivity.this,"chat", Toast.LENGTH_LONG).show();
            // Handle the camera action
            Intent chat = new Intent(CustomerMapsActivity.this, AllDriversActivity.class);
            //chat.putExtra("lista", listaVozaca);
            startActivity(chat);

        } else if (id == R.id.help) {


        } else if (id == R.id.odjava) {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(CustomerMapsActivity.this,CustomerLoginActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.promena) {
            Intent in = new Intent(CustomerMapsActivity.this,UpdateInformationActivity.class);
            in.putExtra("type","customers");
            startActivity(in);

        }
        else if(id==R.id.zakazanevoznje2)
        {
            Intent intent = new Intent(CustomerMapsActivity.this,PregledZakazanihVoznjiKorisnikActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
