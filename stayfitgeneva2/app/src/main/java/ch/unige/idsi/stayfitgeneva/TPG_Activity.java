package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;


public class TPG_Activity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager lm;
    private double lg;
    private double lat;
    private LatLng gva;
    private ArrayList<String> geloc;
    getStops getstop;
    getStopCode getstopcode;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpg_);
        getstopcode = new getStopCode();
        getstop = new getStops();
        setUpMapIfNeeded();
        try {
            ArrayList<String> arrayList = getstopcode.get();
            getstop.execute(arrayList);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        geloc = new ArrayList<String>();
        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String lgt = new String();
        String lt = new String();
        if (l != null) {
            lg = l.getLongitude();
            lat = l.getLatitude();
            gva = new LatLng(lat, lg);

        } else {
            Toast toast = Toast.makeText(this, "pas de localisation", Toast.LENGTH_LONG);
            toast.show();


        }
        lgt = String.valueOf(lg);
        lt = String.valueOf(lat);
        geloc.addAll(Arrays.asList(lt, lgt));
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gva, 14));
       /* mMap.addMarker(new MarkerOptions()
                .position(gva));*/
        String lati = String.valueOf(
                lat);
        String longi = String.valueOf(
                lg);

        CharSequence text = "Lat: " + lati + " Long: " + longi;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();

        getstopcode.execute(geloc);


    }

    class getStopCode extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
        ArrayList<String> stopCode = new ArrayList<String>();

        @Override

        protected ArrayList<String> doInBackground(ArrayList<String>... params) {
            try {
                String val1 = new String();
                String val2 = new String();

                ArrayList<String> geoloc = params[0];

                val1 = geoloc.get(0);
                val2 = geoloc.get(1);

                String url = new String("http://rtpi.data.tpg.ch/v1/GetStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&" + "latitude=" + val1 + "&longitude=" + val2);
                Document doc = Jsoup.connect(url).get();
                String m = new String();
                int dist = 0;

                for (Element e : doc.select("stop")) {
                  Elements elements1 = e.select("stopCode");
                  Elements elements2 = e.select("distance");
                  String string =elements2.toString().replace("<distance>", "").replace("</distance>", "");
                  string = string.replaceAll("\n", "");
                  string = string.replaceAll(" ","");

                    try {
                        dist = Integer.parseInt(string);
                        if(dist < 300){
                            m = elements1.toString().replace("<stopcode>", "").replace("</stopcode>", "");
                            stopCode.add(m);
                        }

                    } catch(NumberFormatException nfe) {

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return stopCode;
        }


        /*protected void onPostExecute(ArrayList<String> result) {


            getstop = new getStops();
            getstop.execute(stopCode);

        }*/
    }


    class getStops extends AsyncTask<ArrayList<String>, Void, ArrayList<LatLng>> {

        ArrayList<LatLng> arret = new ArrayList<LatLng>();



        @Override
        protected ArrayList<LatLng> doInBackground(ArrayList<String>... params) {
            Document document = null;
            try {
                ArrayList<String> codeStop = params[0];
                StringBuilder sb = new StringBuilder();

                for (String tempString : codeStop) {
                    sb.append(tempString).append(",");
                }


                String url = new String("http://rtpi.data.tpg.ch/v1/GetPhysicalStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode="+sb.toString());

                    url = url.substring(0, url.length()-1);
                    url = url.replaceAll(" ","");
                    url=  url.replaceAll("\n", "");

                /*StringBuilder build = new StringBuilder();
                AssetManager ast = getAssets();
                InputStream instr = ast.open("Layers/GetPhysicalStops.xml");
                BufferedReader in = new BufferedReader(new InputStreamReader(instr));

                String str = new String();


                while ((str = in.readLine()) != null) {
                    build.append(str);
                }
                in.close();
                String html = build.toString();*/


                ArrayList<String> latitude = new ArrayList<String>();
                ArrayList<String> longitude = new ArrayList<String>();

                    document = Jsoup.connect(url).get();
                    for (Element f : document.select("latitude")) {
                        latitude.add(f.toString().replace("<latitude>", "").replace("</latitude>", ""));
                    }
                    for (Element g : document.select("longitude")) {
                        longitude.add(g.toString().replace("<longitude>", "").replace("</longitude>", ""));
                    }
                    int taille = latitude.size();

                for (int i = 0; i < taille; i++)
                {
                    Double d1 = (Double.parseDouble(latitude.get(i)));
                    Double d2 = (Double.parseDouble(longitude.get(i)));
                    LatLng latLng = new LatLng(d1,d2);
                    arret.add(latLng);

                }







                //ArrayList<LatLng> listeArret = new ArrayList<LatLng>();


                /*for (Element element : doc.select("stop")) {
                    for (Element q : element.select("latitude")) {
                        p = q.toString().replace("<latitude>", "").replace("</latitude>", "");
                    }
                    ;
                    for (Element r : element.select("longitude")) {
                        z = r.toString().replace("<longitude>", "").replace("</longitude>", "");
                    }
                    ;


                    LatLng latLng = new LatLng(Double.parseDouble(p), Double.parseDouble(z));
                    arret.add(latLng);*/






            } catch (IOException e) {
                e.printStackTrace();
            }
            return arret;
        }

        protected void onPostExecute(ArrayList<LatLng> result) {

            MarkerOptions marker = new MarkerOptions();
            for (LatLng latLng : arret) {

                marker.position(latLng);

                mMap.addMarker(marker.position(latLng));
                //LatLng stop = new LatLng(latLong.longitude, latLong.latitude);

            }
            // mMap.addMarker(new MarkerOptions().position(stop));

        }
    }

}




