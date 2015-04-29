package ch.unige.idsi.stayfitgeneva;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.res.AssetManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private LocationManager lm;
    private double lg;
    private double lat;
    private LatLng gva;
    private double latitude;
    private double longitude;
    private GoogleMap map;
    private Location location;
    parseLayer parse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        parse = new parseLayer();
        parse.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }


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
     * just add a marker near Geneva.
     */
    private void setUpMap() {
        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (l != null) {
            lg = l.getLongitude();
            lat = l.getLatitude();
            gva = new LatLng(lat, lg);
        } else {
            Toast toast = Toast.makeText(this, "pas de localisation", Toast.LENGTH_LONG);
            toast.show();


        }

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gva, 14));
        mMap.addMarker(new MarkerOptions()
                //  .title("Genève")
                .position(gva));
        String lati = String.valueOf(
                lat);
        String longi = String.valueOf(
                lg);

        CharSequence text = "Lat: " + lati + " Long: " + longi;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
        //startDemo();
        // Polylines are useful for marking paths and routes on the map.
        /** mMap.addPolyline(new PolylineOptions().geodesic(true)
         .add(new LatLng(46.224778, 6.036249))
         .add(new LatLng(46.162391, 6.191776))
         );*/

    }


        class parseLayer extends AsyncTask<Void, Void, ArrayList<ArrayList<LatLng>>> {


            ArrayList<ArrayList<LatLng>> allTracks = new ArrayList<ArrayList<LatLng>>();
            @Override
            protected ArrayList<ArrayList<LatLng>> doInBackground(Void... params) {
                try {
                    StringBuilder build = new StringBuilder();
                    AssetManager ast = getAssets();
                    InputStream instr = ast.open("Layers/doc.kml");
                    BufferedReader in = new BufferedReader(new InputStreamReader(instr));
                    String str = new String();

                    while ((str = in.readLine()) != null) {
                        build.append(str);
                    }
                    in.close();
                    String html = build.toString();
                    Document doc = Jsoup.parse(html, "", Parser.xmlParser());
                    ArrayList<String> tracksString = new ArrayList<String>();
                    for (Element element : doc.select("coordinates")) {
                        tracksString.add(element.toString().replace("<coordinates>", "").replace("</coordinates>", ""));
                    }

                    for (int i = 0; i < tracksString.size(); i++) {
                        ArrayList<LatLng> oneTrack = new ArrayList<LatLng>();
                        ArrayList<String> oneTrackString = new ArrayList<String>(Arrays.asList(tracksString.get(i).split("\\s+")));
                        for (int k = 1; k < oneTrackString.size(); k++) {
                            LatLng latLng = new LatLng(Double.parseDouble(oneTrackString.get(k).split(",")[0]),
                                    Double.parseDouble(oneTrackString.get(k).split(",")[1]));
                            oneTrack.add(latLng);
                        }
                        allTracks.add(oneTrack);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return allTracks;
            }


            protected void onPostExecute(ArrayList<ArrayList<LatLng>> result) {
            for(ArrayList<LatLng> arraylist : allTracks){
                PolylineOptions poly = new PolylineOptions();
                for (LatLng latLong : arraylist) {
                    //System.out.println(latLng.latitude + " - " + latLng.longitude);
                    LatLng temp = new LatLng(latLong.longitude, latLong.latitude);
                    poly.add(temp);

                }
                Polyline polyline = mMap.addPolyline(poly);
                polyline.setColor(Color.RED);

            }


            }


        }
    }
