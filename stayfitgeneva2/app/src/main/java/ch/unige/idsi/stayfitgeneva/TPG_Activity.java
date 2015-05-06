package ch.unige.idsi.stayfitgeneva;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class TPG_Activity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LatLng gva;
    getStopsPosition getstopsposition;
    getStopCode getstopcode;
    //getDetailMarker getDetailMarker;
    HashMap<String, ArrayList<ArrayList<String>>> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpg_);
        hashMap = new HashMap();
        getstopcode = new getStopCode();
        getstopsposition = new getStopsPosition();
        // getDetailMarker = new getDetailMarker();

        setUpMapIfNeeded();

        try {
            ArrayList<String> arrayList = getstopcode.get();
            getstopsposition.execute(arrayList);

        } catch (InterruptedException | ExecutionException e) {
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

        ArrayList<String> geloc = new ArrayList<>();
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String lgt;
        String lt;
        double lg;
        double lat;
        if (l != null) {
            lg = l.getLongitude();
            lat = l.getLatitude();
            gva = new LatLng(lat, lg);

        } else {
            lat = 46.198198;
            lg = 6.140999;
        }
        lgt = String.valueOf(lg);
        lt = String.valueOf(lat);
        geloc.addAll(Arrays.asList(lt, lgt));
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gva));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
       /* mMap.addMarker(new MarkerOptions()
                .position(gva));*/
        /*
        //affiche la latitude et longitude actuelle de l'utilisateur
        String lati = String.valueOf(
                lat);
        String longi = String.valueOf(
                lg);

        CharSequence text = "Lat: " + lati + " Long: " + longi;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();*/

        getstopcode.execute(geloc);


    }

    class getStopCode extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
        ArrayList<String> stopCode = new ArrayList<>();

        @SafeVarargs
        @Override

        protected final ArrayList<String> doInBackground(ArrayList<String>... params) throws NumberFormatException {
            try {
                String val1;
                String val2;

                ArrayList<String> geoloc = params[0];

                val1 = geoloc.get(0);
                val2 = geoloc.get(1);

                String url = "http://rtpi.data.tpg.ch/v1/GetStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&" + "latitude=" + val1 + "&longitude=" + val2;
                Document doc = Jsoup.connect(url).get();
                String m;
                int dist;

                for (Element e : doc.select("stop")) {
                    Elements elements1 = e.select("stopCode");
                    Elements elements2 = e.select("distance");

                    String string = elements2.toString().replace("<distance>", "").replace("</distance>", "");
                    string = string.replaceAll("\n", "");
                    string = string.replaceAll(" ", "");

                    dist = Integer.parseInt(string);
                    if (dist < 300) {
                        m = elements1.toString().replace("<stopcode>", "").replace("</stopcode>", "");
                        stopCode.add(m);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return stopCode;

        }


        /*protected void onPostExecute(ArrayList<String> result) {


            getWaitingTime getwaitingtime = new getWaitingTime();
            getwaitingtime.execute(stopCode);

        }*/
    }


    class getStopsPosition extends AsyncTask<ArrayList<String>, Void, HashMap<String, ArrayList<LatLng>>> {


        ArrayList<String> codeStop = new ArrayList<>();
        ArrayList<String> code = new ArrayList<>();
        ArrayList<LatLng> arret = new ArrayList<>();
        HashMap<String, ArrayList<LatLng>> hashMap = new HashMap<>();
        //ArrayList<ArrayList<String>> stopposition = new ArrayList<>();


        @SafeVarargs
        @Override
        protected final HashMap<String, ArrayList<LatLng>> doInBackground(ArrayList<String>... params) {
            Document document;

            codeStop = params[0];

            /*for (String tempString : codeStop) {

                tempString.replaceAll("\n", "");
                tempString.replaceAll(" ", "");
                tempString.substring(0, tempString.length() - 1);
                code.add(tempString);

            }*/

            for (String tempString : codeStop) {
                StringBuilder sb = new StringBuilder();
                sb.append(tempString);
                String s = sb.toString().replaceAll("\n", "");
                s = s.replaceAll(" ", "");
                //s = s.substring(0, s.length() - 1);
                code.add(s);

            }
            for (String codearret : code) {
                //String codearret = code.get(i);
                String url = "http://rtpi.data.tpg.ch/v1/GetPhysicalStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + codearret;
                //String url2 = new String("http://rtpi.data.tpg.ch/v1/GetNextDepartures.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + code.get(i));
               /* ArrayList<String> latitudelongitude = new ArrayList<>();
                ArrayList<String> lignearret = new ArrayList<>();
                ArrayList<String> nomarret = new ArrayList<>();
                ArrayList<String> destinationarret = new ArrayList<>();*/
                ArrayList<String> latitude = new ArrayList<>();
                ArrayList<String> longitude = new ArrayList<>();
                arret = new ArrayList<>();


                try {
                    document = Jsoup.connect(url).get();
                    for (Element f : document.select("latitude")) {
                        latitude.add(f.toString().replace("<latitude>", "").replace("</latitude>", "").replaceAll(" ", "").replaceAll("\n", ""));
                    }
                    for (Element g : document.select("longitude")) {
                        longitude.add(g.toString().replace("<longitude>", "").replace("</longitude>", "").replaceAll(" ", "").replaceAll("\n", ""));
                    }
                    int taille = latitude.size();

                    for (int i = 0; i < taille; i++) {
                        Double d1 = (Double.parseDouble(latitude.get(i)));
                        Double d2 = (Double.parseDouble(longitude.get(i)));
                        LatLng latLng = new LatLng(d1, d2);
                        arret.add(latLng);
                        hashMap.put(codearret, arret);


                    }


                    //}
                } catch (IOException e) {
                    /*for (Element element : document.getElementsByTag("physicalStop")) {

                        latitudelongitude = new ArrayList<>();
                        nomarret = new ArrayList<>();
                        lignearret = new ArrayList<>();
                       // destinationarret = new ArrayList<>();

                        Elements latitude1 = element.getElementsByTag("latitude");
                        latitudelongitude.add(latitude1.toString().replace("<latitude>", "").replace("</latitude>", "").replaceAll(" ", "").replaceAll("\n", ""));
                        Elements longitude1 = element.getElementsByTag("longitude");
                        latitudelongitude.add(longitude1.toString().replace("<longitude>", "").replace("</longitude>", "").replaceAll(" ", "").replaceAll("\n", ""));
                        Elements stopName = element.getElementsByTag("stopName");
                        nomarret.add(stopName.toString().replace("<stopname>", "").replace("</stopname>", "").replaceAll(" ", "").replaceAll("\n", ""));

                        for (Element element1 : element.getElementsByTag("connection")) {
                            Elements linecode = element1.getElementsByTag("lineCode");
                            lignearret.add(linecode.toString().replace("<linecode>", "").replace("</linecode>", "").replaceAll(" ", "").replaceAll("\n", ""));
                            Elements destname = element1.getElementsByTag("destinationName");
                            lignearret.add(destname.toString().replace("<destinationname>", "").replace("</destinationname>", "").replaceAll(" ", "").replaceAll("\n", ""));
                        }
                        // Elements linecode = element.getElementsByTag("lineCode");


                        //stopposition.add(destname.toString().replace("<destinationname>", "").replace("</destinationname>", ""));
                        //stopposition.add(linecode.toString().replace("<linecode>", "").replace("</linecode>", ""));



                        /*try {
                            doc = Jsoup.connect(url2).get();
                            Elements e = doc.getElementsByTag("waitingTime");
                            String string = e.get(0).toString().replace("<waitingtime>", "").replace("</waitingtime>", "");
                            string = string.replaceAll("\n", "");
                            string = string.replaceAll(" ", "");
                            stopposition.add(string);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    e.printStackTrace();
                }
                //stopposition.addAll(Arrays.asList(latitudelongitude, nomarret, lignearret));
                //hashMap.put(codearret, stopposition);

            }
            return hashMap;
        }


        protected void onPostExecute(HashMap<String, ArrayList<LatLng>> result) {

            final MarkerOptions marker = new MarkerOptions();
            //String url = new String();


            for (final String cd : hashMap.keySet()) {


                //getDetailMarker.execute(url);
                for (final LatLng latLng : hashMap.get(cd)) {
                    mMap.addMarker(marker.position(latLng).title(cd));
                }

            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    // if (arg0.getTitle().equals(cd)) // if marker source is clicked
                    //passer Latlng et faire recherche rdonnee sur la lat et long et retirer pour chaqeu marker etc.
                    //{
                    Toast toast = Toast.makeText(getApplicationContext(), "Code de l'arrêt voulu: " + arg0.getTitle(), Toast.LENGTH_LONG);
                    toast.show();
                    String url = "http://rtpi.data.tpg.ch/v1/GetPhysicalStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + arg0.getTitle();
                    String lng = String.valueOf(arg0.getPosition().longitude);
                    Intent intent = new Intent(TPG_Activity.this, MarkerActivity.class);
                    intent.putExtra("Url", url);
                    intent.putExtra("longitude", lng);
                    intent.putExtra("titre",arg0.getTitle());
                    startActivityForResult(intent, 1);
                    // }
                    return true;
                }

            });
        }

        //googlemap.setOnMarkerClickListener(this);

           /* MarkerOptions marker = new MarkerOptions();
            Double latitude = null;
            Double longitude = null;
            ArrayList<String> ligne = new ArrayList<>();
            ArrayList<String> destination = new ArrayList<>();
            ArrayList<String> pos;
            ArrayList<String> det;


            for (String key : hashMap.keySet()) {
                ArrayList<ArrayList<String>> value = hashMap.get(key);
                int taille = value.size();
                for (ArrayList<String> arrayList : value) {


                }
                for (int val = 0; val < value.size(); val++) {
                    pos = value.get(val);

                    if (pos.get(val).startsWith("6.")) {
                        longitude = (Double.parseDouble(pos.get(val)));
                    } else {
                        latitude = (Double.parseDouble(pos.get(val)));
                    }


                }
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(marker.position(latLng));*/
        //LatLng latLng = new LatLng(latitude,longitude);
                     /*int taille = ligne.size();
                    for(int j = 0; j < taille; j++) {
                        String numligne = ligne.get(j);
                       String nomdest = destination.get(j);*/
        // mMap.addMarker(marker.position(latLng));//.title("Arrêt: " + key + "\n" + "Ligne: " + numligne + "\n" + "Destination: " + nomdest));
        //}
        //}

    }


                /*Double longitude = (Double.parseDouble(value.get().startsWith("6."));
                Double latitude = (Double.parseDouble(value.get(1)));
                LatLng latLng = new LatLng(latitude,longitude);
                String ligne = value.get(2);
                String destination = value.get(3);




                        Intent intent=new Intent(EcrireSujet.this,EcrireQA.class);
        // putExtra marche comme HashMap (key,value)
        */


}


