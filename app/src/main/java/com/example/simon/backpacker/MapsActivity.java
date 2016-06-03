package com.example.simon.backpacker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String encoded_string;
    private static final String url = "10.36.120.47/android.php";
    protected ArrayList<PicInfo> picList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        String userId = getIntent().getExtras().getString("USERID");

        ImageButton camera = (ImageButton) findViewById(R.id.camera);
        camera.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //TO-DO : get pic from server and save at picList
        picList = new ArrayList<PicInfo>();
        Bitmap testbit = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        double la = -34, lo = 151;
        PicInfo test = new PicInfo(testbit,la,lo);
        picList.add(test);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap t11 = BitmapFactory.decodeResource(getResources(),R.mipmap.test1,options);
        double la1 = -34.1, lo1=151.1;
        PicInfo t1 = new PicInfo(t11,la1,lo1);
        picList.add(t1);
        t11 = BitmapFactory.decodeResource(getResources(),R.mipmap.test2,options);
        la1 = -34.11;
        lo1=151.11;
        t1 = new PicInfo(t11,la1,lo1);
        picList.add(t1);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        Gps gps = new Gps(this);
        LatLng cur = new LatLng(gps.getLatitude(),gps.getLongitude());
        mMap.addMarker(new MarkerOptions().position(cur).title("current"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cur));


        //TO-DO : marker pic(imagebutton) by gps info from picList
        for(int i=0; i<picList.size(); i++){
            Bitmap tempBit = picList.get(i).getBitmap();
            double tempLatitude = picList.get(i).getLatitude();
            double tempLongtitude = picList.get(i).getLongtitude();

            googleMap.addMarker(new MarkerOptions().position(new LatLng(tempLatitude,tempLongtitude)).icon(BitmapDescriptorFactory.fromBitmap(tempBit)));
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    GridView gv = (GridView)findViewById(R.id.gridView);
                    ImageGridAdapter imagegridadapter = new ImageGridAdapter(getApplicationContext(),picList);

                    gv.setAdapter(imagegridadapter);

                    gv.setVisibility(View.VISIBLE);

                    return false;
                }
            });
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                GridView gv = (GridView)findViewById(R.id.gridView);
                gv.setVisibility(View.GONE);
            }
        });

        //TO-DO : imagebutton onclicklistener -> make fragment which show picture list at gridview
        //TO-DO : map onclicklistener -> hide fragment(gridview)
        //TO-DO : gridview onclicklistener -> only imageview with black background
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //call camera app
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //get photo to bitmap file and upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo;

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
        } else
            return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] array = stream.toByteArray();
        encoded_string = Base64.encodeToString(array, 0);

        //server connection
        new connection().execute(encoded_string);
    }

    private class connection extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            /*
            TO-DO:image name and Gps info
             */
            Gps gps = new Gps(MapsActivity.this);

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("encoded_string", encoded_string));
            nameValuePairs.add(new BasicNameValuePair("image_name", "simon.jpg"));
            nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(gps.getLatitude())));
            nameValuePairs.add(new BasicNameValuePair("longitude", Double.toString(gps.getLongitude())));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://10.36.120.33/test.php");
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpPost);
                /*
                HttpEntity entityResponse = response.getEntity();
                InputStream stream = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HTTP.UTF_8));

                String temp = reader.readLine();
                stream.close();

                if(temp == encoded_string)
                    Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                    */

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(), "전송을 성공하였습니다", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private class Gps implements LocationListener {

        double latitude;
        double longitude;

        boolean isGPSEnabled = false, isNetworkEnabled = false, isGetLocation = false;

        protected LocationManager locationManager;
        Location location;

        protected Gps(Context context) {
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return  ;
            }

            locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.isGetLocation = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            60000,
                            10, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                60000,
                                10, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }

        protected double getLatitude(){
            return latitude;
        }

        protected double getLongitude(){
            return  longitude;
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public class PicInfo {
        private Bitmap bitmap;
        private double latitude;
        private double longitude;

        PicInfo(Bitmap _bitmap, double _latitude, double _longtitude){
            bitmap = _bitmap;
            latitude = _latitude;
            longitude = _longtitude;
        }

        Bitmap getBitmap(){
            return bitmap;
        }

        double getLatitude(){
            return latitude;
        }

        double getLongtitude() { return longitude; }
    }
}
