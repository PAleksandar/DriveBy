package com.example.nenad.projekat;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DriverMapsActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,RoutingListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Driver driver;
    private LatLng start;
    private LatLng destination;
    private Button mZakaziVoznju,mOdbij,mPrihvati,mLogout,mZavrsenaVoznja,mChat,mMojeVoznje;
    private List<Polyline> polylines= new ArrayList<Polyline>();
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private TextView duration;
    private TrenutnoZahtevanaVoznja zahtevanaVoznja;
    PolylineOptions polylineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_home2);

        ///--------------------------interfejs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        // setSupportActionBar(toolbar);





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        if(drawer==null)
            Log.i("drawer", "null");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // MenuInflater inflater = getMenuInflater();
        //Menu action_setting;
        //inflater.inflate(R.menu.home, action_setting);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        if(navigationView==null)
            Log.i("navigation bar" ," null");
        navigationView.setNavigationItemSelectedListener(this);
        //-------------------------------------------------------------------------------


//        mZakaziVoznju = (Button) findViewById(R.id.zakazi);
      //  duration = (TextView) findViewById(R.id.duration);
        mOdbij = (Button) findViewById(R.id.odbij4);
        mPrihvati = (Button) findViewById(R.id.prihvati4);
        //mLogout = (Button) findViewById(R.id.logout);
        mZavrsenaVoznja = (Button) findViewById(R.id.zavrsenavoznja4);
       // mChat = (Button) findViewById(R.id.chat);
        //mMojeVoznje=(Button) findViewById(R.id.mojevoznje);



        mOdbij.setVisibility(View.GONE);
        mPrihvati.setVisibility(View.GONE);
        mZavrsenaVoznja.setVisibility(View.GONE);




        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("DriversAvailable");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driver= dataSnapshot.getValue(Driver.class);
                Toast.makeText(DriverMapsActivity.this,driver.getEmail(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref = ref.child("Zahtev");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    Toast.makeText(DriverMapsActivity.this,"Zahtev uklonjen",Toast.LENGTH_LONG).show();
                    CancelRequest();

                }
                else
                {

                    zahtevanaVoznja = dataSnapshot.getValue(TrenutnoZahtevanaVoznja.class);
                    drawDirections(zahtevanaVoznja.getStart(),zahtevanaVoznja.getDestination());
                   // duration.setText(zahtevanaVoznja.getDuration()+"");
                    start = zahtevanaVoznja.getStart();
                    destination= zahtevanaVoznja.getDestination();
                    mOdbij.setVisibility(View.VISIBLE);
                    mPrihvati.setVisibility(View.VISIBLE);
                    Toast.makeText(DriverMapsActivity.this,"Pristigo zahtev "+zahtevanaVoznja.getStart(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mZakaziVoznju.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(DriverMapsActivity.this, ZakazivanjeVoznjeActivity.class);
//                intent.putExtra("driver",driver);
//                startActivity(intent);
//
//
//            }
//        });
        mPrihvati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                .child(zahtevanaVoznja.getCustomer().getId()).child("Prihvacen").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mOdbij.setVisibility(View.GONE);
                mPrihvati.setVisibility(View.GONE);
                mZavrsenaVoznja.setVisibility(View.VISIBLE);

            }
        });
        mOdbij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Zahyev").removeValue();
                mOdbij.setVisibility(View.GONE);
                mPrihvati.setVisibility(View.GONE);

            }
        });

//        mLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");
//
//                GeoFire geoFire = new GeoFire(ref);
//                geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
//                    @Override
//                    public void onComplete(String key, DatabaseError error) {
//
//                    }
//                });
//                FirebaseAuth.getInstance().signOut();
//                Intent intent=new Intent(DriverMapsActivity.this,DriverLoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        mZavrsenaVoznja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(zahtevanaVoznja.getCustomer().getId())
                        .child("Rejtuj").setValue(driver);


                mZavrsenaVoznja.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Zahtev").removeValue();


            }
        });
//        mChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DriverMapsActivity.this,DriverChatActivity.class);
//                startActivity(intent);
//            }
//        });
//        mMojeVoznje.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DriverMapsActivity.this,PregledZakazanihVoznjiVozacActivity.class);
//                startActivity(intent);
//            }
//        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map4);
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
        mapFragment.getMapAsync(this);




    }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
    {
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Granted");
            AlertDialog ad = builder.create();
            ad.show();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map4);
            mapFragment.getMapAsync(this);


        }



    }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(DriverMapsActivity.this,"no access", Toast.LENGTH_LONG);
            return;
        }
        mMap.setMyLocationEnabled(true);



    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLastLocation==null)
        {
            LatLng mLatLng= new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
        mLastLocation=location;

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if(u!=null) {
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");
            //ref.setValue(driver);

            MapRefresh();
            drawStartDestinationMarkers();

            GeoFire geoFire = new GeoFire(ref);//ubacivanje lokacije korisnika u bazu
            geoFire.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                }
            });
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 3);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {

                    LatLng latLng = new LatLng(location.latitude, location.longitude);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(true));


                }


                @Override
                public void onKeyExited(String key) {

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

    }
    public void MapRefresh()
    {
        mMap.clear();//svi se markeri brisu da se refresuje, inace ostavlja i stare markere
        drawStartDestinationMarkers();
        if(polylineOptions!=null)
            mMap.addPolyline(polylineOptions);
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if(mGoogleApiClient.isConnected())
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(DriverMapsActivity.this, "OnConnectionSuspended",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onStop() {

        super.onStop();
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Toast.makeText(DriverMapsActivity.this,"onStop",Toast.LENGTH_SHORT);
//        if(user!=null) {
//            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");
//
//            GeoFire geoFire = new GeoFire(ref);
//            geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
//                @Override
//                public void onComplete(String key, DatabaseError error) {
//
//                }
//            });
//        }

    }
    private void drawDirections(LatLng start, LatLng destination) {
        Routing routing = new Routing.Builder().key("AIzaSyAajs_KEvEhwgzbYE-PyyHXhXlZqoi4cxc")
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start,destination)
                .build();
        routing.execute();

        Marker marker=mMap.addMarker(new MarkerOptions()
                .position(destination)
                .draggable(true));
        Marker marker2= mMap.addMarker(new MarkerOptions()
        .position(start)
        .draggable(true));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();//zumiranje mape za dva markera, postupak je da se napravi builder
        builder.include(start);
        builder.include(destination);
        LatLngBounds latLngBounds = builder.build();
        int padding = (int)(getResources().getDisplayMetrics().widthPixels * 0.2);//izracunavanje paddinga da ne bi markeri bili na ivici
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,padding));//pomeranje kamere uz animaciju
       ;//iscrtavanje rute
    }


    @Override
    public void onRoutingFailure(RouteException e) {

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


        }




    }

    @Override
    public void onRoutingCancelled() {

    }
    private  void CancelRequest()
    {
        zahtevanaVoznja=null;
        polylineOptions=null;
        start=null;
        destination=null;
        MapRefresh();
        if(mLastLocation!=null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),11));
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        Toast.makeText(DriverMapsActivity.this,"onResume",Toast.LENGTH_LONG).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map4);
        mapFragment.getMapAsync(this);

    }


    /////------------------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
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

        if (id == R.id.chat4) {
            Intent intent = new Intent(DriverMapsActivity.this,PrikazKorisnikaActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.help4) {

        } else if (id == R.id.update4) {
            Intent in = new Intent(DriverMapsActivity.this,UpdateInformationActivity.class);
            in.putExtra("type","drivers");
            startActivity(in);

        } else if (id == R.id.odjava4) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(DriverMapsActivity.this,DriverLoginActivity.class);
                startActivity(intent);
                finish();

        }else if (id == R.id.zakazivoznju4) {

                Intent intent = new Intent(DriverMapsActivity.this, ZakazivanjeVoznjeActivity.class);
                intent.putExtra("drivers",driver);
                startActivity(intent);

        }else if (id == R.id.mojevoznje4) {
            Intent intent = new Intent(DriverMapsActivity.this,PregledZakazanihVoznjiVozacActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(DriverMapsActivity.this,"Destroy",Toast.LENGTH_LONG).show();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(DriverMapsActivity.this,"onStop",Toast.LENGTH_SHORT);
        if(user!=null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                }
            });
        }
    }



}
