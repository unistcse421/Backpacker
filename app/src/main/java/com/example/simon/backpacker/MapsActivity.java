package com.example.simon.backpacker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.internal.http.multipart.MultipartEntity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.zzf;
import com.google.android.gms.vision.CameraSource;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String encoded_string;
    public ArrayList<PicInfo> picList;
    private int userId;
    private int photoTotalNum;
    private Uri imageUri;
    private Marker[] marker;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        picList = new ArrayList<PicInfo>();

        userId = getIntent().getIntExtra("USERID", -1);
        photoTotalNum = getIntent().getIntExtra("PHOTONUM", 0);

        ImageButton camera = (ImageButton) findViewById(R.id.camera);
        camera.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        LatLng cur = new LatLng(gps.getLatitude(), gps.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur, 16));
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
        mMap.setMyLocationEnabled(true);

        //TO-DO : marker pic(imagebutton) by gps info from picList
        getPhoto();
        setMarker();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GridView gv = (GridView) findViewById(R.id.gridView);
                ImageGridAdapter imagegridadapter = new ImageGridAdapter(getApplicationContext(), picList, userId);

                gv.setAdapter(imagegridadapter);

                gv.setVisibility(View.VISIBLE);

                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                GridView gv = (GridView) findViewById(R.id.gridView);
                gv.setVisibility(View.GONE);
            }
        });
    }

    private void setMarker() {
        marker = new Marker[10000];
        for (int i = 0; i < picList.size(); i++) {
            Bitmap tempBit = picList.get(i).getBitmap();
            double tempLatitude = picList.get(i).getLatitude();
            double tempLongtitude = picList.get(i).getLongtitude();

            int index = isSetMaker(tempLatitude, tempLongtitude, i);
            if (index != -1) {
                marker[index].remove();
                marker[i] = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(tempLatitude, tempLongtitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(tempBit))));
            } else {
                marker[i] = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(tempLatitude, tempLongtitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(tempBit)));
            }
        }
    }

    private void getPhoto() {
        try {
            new getGps().execute(userId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < photoTotalNum; i++) {
            //make url
            String url = "http://uni07.unist.ac.kr/~cs20111412/imgs/" + String.valueOf(userId) + "_" + String.valueOf(i + 1) + ".jpg";

            Bitmap photo = null;
            String[] gps = null;

            try {
                getPhoto task = new getPhoto();
                photo = task.execute(url).get();

                //getGps task2 = new getGps();
                //gps = task2.execute(userId,i+1).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //PicInfo picInfo = new PicInfo(photo,Double.parseDouble(gps[0]),Double.parseDouble(gps[1]));
            //picList.add(picInfo);
            picList.get(i).setBitmap(photo);
        }

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //call camera app
    private void dispatchTakePictureIntent() {

        File photo = new File(Environment.getExternalStorageDirectory(),
                ".camera.jpg");
        imageUri = Uri.fromFile(photo);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        /*
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
 */
    }

    //get photo to bitmap file and upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        int degree;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            photo = (Bitmap) extras.get("data");

            String imagePath = imageUri.getPath();
            Bitmap image = BitmapFactory.decodeFile(imagePath);

            degree = GetExifOrientation(imagePath);
            photo = GetRotatedBitmap(image, degree);
        } else
            return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 70, stream);

        byte[] array = stream.toByteArray();
        encoded_string = Base64.encodeToString(array, 0);

        Gps gps = new Gps(MapsActivity.this);
        //server connection
        String te = null;
        try {
            te = new connection().execute(String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude())).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "전송을 성공하였습니다", Toast.LENGTH_SHORT).show();

        encoded_string = null;

        Bitmap sizingBmp = Bitmap.createScaledBitmap(photo, (int) 150, (int) 150, true);

        int index = isSetMaker(gps.getLatitude(), gps.getLongitude(), picList.size());
        if (index != -1) {
            marker[index].remove();
            marker[picList.size()] = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(gps.getLatitude(), gps.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(sizingBmp))));
        } else {
            marker[picList.size()] = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(gps.getLatitude(), gps.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(sizingBmp)));
        }

        PicInfo t1 = new PicInfo(sizingBmp, gps.getLatitude(), gps.getLongitude());
        picList.add(t1);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GridView gv = (GridView) findViewById(R.id.gridView);

                ImageGridAdapter imagegridadapter = new ImageGridAdapter(getApplicationContext(), picList, userId);

                gv.setAdapter(imagegridadapter);

                gv.setVisibility(View.VISIBLE);

                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.simon.backpacker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.simon.backpacker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class connection extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String temp = null;
            String name = Integer.toString(userId) + "_" + Integer.toString(++photoTotalNum) + ".jpg";

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("encoded_string", encoded_string));
            nameValuePairs.add(new BasicNameValuePair("image_name", name));
            nameValuePairs.add(new BasicNameValuePair("latitude", params[0]));
            nameValuePairs.add(new BasicNameValuePair("longitude", params[1]));
            nameValuePairs.add(new BasicNameValuePair("user_num", Integer.toString(userId)));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://uni07.unist.ac.kr/~cs20111412/save_img.php");
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entityResponse = response.getEntity();
                InputStream stream = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HTTP.UTF_8));

                temp = reader.readLine();

                stream.close();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp;
        }
    }

    private class Gps implements LocationListener {

        double latitude;
        double longitude;

        boolean isGPSEnabled = false, isNetworkEnabled = false, isGetLocation = false;

        protected LocationManager locationManager;
        Location location;

        protected Gps(Context context) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

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

        protected double getLatitude() {
            return latitude;
        }

        protected double getLongitude() {
            return longitude;
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

        PicInfo(Bitmap _bitmap, double _latitude, double _longtitude) {
            bitmap = _bitmap;
            latitude = _latitude;
            longitude = _longtitude;
        }

        PicInfo(double _latitude, double _longtitude) {
            latitude = _latitude;
            longitude = _longtitude;
        }

        Bitmap getBitmap() {
            return bitmap;
        }

        void setBitmap(Bitmap _bitmap_) {
            bitmap = _bitmap_;
        }

        double getLatitude() {
            return latitude;
        }

        void setLatitude(double _latitude) {
            latitude = _latitude;
        }

        double getLongtitude() {
            return longitude;
        }

        void setLongitude(double _longitutde) {
            longitude = _longitutde;
        }
    }

    public synchronized static int GetExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap.recycle();
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return bitmap;
    }

    private class getPhoto extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            asyncDialog.dismiss();
            super.onPostExecute(bitmap);
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다...");
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bitmap = BitmapFactory.decodeStream(is);

                bitmap = Bitmap.createScaledBitmap(bitmap, (int) 150, (int) 150, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private class getGps extends AsyncTask<Integer, Void, String[]> {

        @Override
        protected String[] doInBackground(Integer... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("userid", String.valueOf(params[0])));
            //nameValuePairs.add(new BasicNameValuePair("photoid",String.valueOf(params[1])));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://uni07.unist.ac.kr/~cs20111412/getcoor.php");
            //String[] temp = new String[2];
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entityResponse = response.getEntity();
                InputStream stream = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HTTP.UTF_8));

                for (int i = 0; i < photoTotalNum; i++) {
                    double templa = Double.parseDouble(reader.readLine());
                    double templo = Double.parseDouble(reader.readLine());
                    PicInfo picInfo = new PicInfo(templa, templo);
                    picList.add(picInfo);
                }

                stream.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // return temp;
            return null;
        }
    }

    int isSetMaker(double latitude, double longitude, int index) {
        for (int i = index - 1; i >= 0; i--) {
            if (picList.get(i).getLatitude() + 0.001 > latitude && picList.get(i).getLatitude() - 0.001 < latitude &&
                    picList.get(i).getLongtitude() + 0.001 > longitude && picList.get(i).getLongtitude() - 0.001 < longitude) {
                return i;
            }
        }
        return -1;
    }

    private Bitmap getMarkerBitmapFromView(Bitmap _bitmap) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_multi, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.multi_marker);
        markerImageView.setImageBitmap(_bitmap);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


}
