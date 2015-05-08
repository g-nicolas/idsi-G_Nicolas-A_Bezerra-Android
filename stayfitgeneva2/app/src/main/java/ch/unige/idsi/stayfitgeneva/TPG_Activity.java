package ch.unige.idsi.stayfitgeneva;

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
    getStopsPosition getstopsposition;
    getStopCode getstopcode;
    HashMap<String, ArrayList<ArrayList<String>>> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpg_);
        hashMap = new HashMap();
        getstopcode = new getStopCode();
        getstopsposition = new getStopsPosition();
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
     *Création de la map et récupération de la géolocalisation de l'utilisateur
     *
     */
    private void setUpMap() {

        ArrayList<String> geloc = new ArrayList<>();
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String lgt;
        String lt;
        double lg;
        double lat;
        LatLng gva;
        if (l != null) {
            lg = l.getLongitude();
            lat = l.getLatitude();
            gva = new LatLng(lat, lg);
   /**
    * J'ai eu quelques soucis avec la géolocalisation de mon smartphone alors j'ai défini
    * une valeur par défaut si jamais la géolocalisation me venait à manquer.
    */
        } else {
            lat = 46.198198;
            lg = 6.140999;
            gva = new LatLng(lat, lg);
        }
        lgt = String.valueOf(lg);
        lt = String.valueOf(lat);
        geloc.addAll(Arrays.asList(lt, lgt));// j'enregistre la géoloc en String() afin de la récupérer dans l'asynctask suivant
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gva));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
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
                //Je récupère les données de géoloc afin de les utiliser pour localiser les arrêts autour de la position de l'utilisateur
                String url = "http://rtpi.data.tpg.ch/v1/GetStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&" + "latitude=" + val1 + "&longitude=" + val2;
                Document doc = Jsoup.connect(url).get();
                String m;
                //Je récupère la distance dans l'api afin de la réduire de 500m à 300m autour de la position de l'utilisateur
                int dist;
                for (Element e : doc.select("stop")) {
                    Elements elements1 = e.select("stopCode");
                    Elements elements2 = e.select("distance");
                    String string = elements2.toString().replace("<distance>", "").replace("</distance>", "");
                    string = string.replaceAll("\n", "");
                    string = string.replaceAll(" ", "");
                    dist = Integer.parseInt(string);
                    if (dist < 300) {// ici la condition qui permet de réduire la distance
                        m = elements1.toString().replace("<stopcode>", "").replace("</stopcode>", "");
                        stopCode.add(m);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stopCode;// j'envoie ensuite les stopCode étant autours de la position de l'utilsateur
        }
    }

    class getStopsPosition extends AsyncTask<ArrayList<String>, Void, HashMap<String, ArrayList<LatLng>>> {
        ArrayList<String> codeStop = new ArrayList<>();
        ArrayList<String> code = new ArrayList<>();
        ArrayList<LatLng> arret = new ArrayList<>();
        HashMap<String, ArrayList<LatLng>> hashMap = new HashMap<>();

        @SafeVarargs
        @Override
        protected final HashMap<String, ArrayList<LatLng>> doInBackground(ArrayList<String>... params) {

            Document document;
            codeStop = params[0]; // Je récupère les données issues de l'asynctask précédente, il s'agit des stopCode de l'API TPG
            // Les codes sont traités afin de pouvoir être utilisé dans l'API TPG
            for (String tempString : codeStop) {
                StringBuilder sb = new StringBuilder();
                sb.append(tempString);
                String s = sb.toString().replaceAll("\n", "");
                s = s.replaceAll(" ", "");
                code.add(s);
            }
            for (String codearret : code) {
                String url = "http://rtpi.data.tpg.ch/v1/GetPhysicalStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + codearret;
                ArrayList<String> latitude = new ArrayList<>();
                ArrayList<String> longitude = new ArrayList<>();
                arret = new ArrayList<>();
                //Grâce aux codes nous pouvons récupérer les coordonnées autours des arrêts affilié au code
                try {
                    document = Jsoup.connect(url).get();
                    for (Element f : document.select("latitude")) {
                        latitude.add(f.toString().replace("<latitude>", "").replace("</latitude>", "").replaceAll(" ", "").replaceAll("\n", ""));
                    }
                    for (Element g : document.select("longitude")) {
                        longitude.add(g.toString().replace("<longitude>", "").replace("</longitude>", "").replaceAll(" ", "").replaceAll("\n", ""));
                    }
                    int taille = latitude.size();
                    /*
                     * Les coordonnées sont ensuites mises dans une hashMap comme valeur, la clé étant le code nous ayant permis
                     * de récupérer les coordonées ce code nous servira par la suite.
                     */
                    for (int i = 0; i < taille; i++) {
                        Double d1 = (Double.parseDouble(latitude.get(i)));
                        Double d2 = (Double.parseDouble(longitude.get(i)));
                        LatLng latLng = new LatLng(d1, d2);
                        arret.add(latLng);
                        hashMap.put(codearret, arret);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return hashMap;// la HashMap est retournée comme valeur afin d'être utilisée à la fin de l'asynctask
        }

        protected void onPostExecute(HashMap<String, ArrayList<LatLng>> result) {

            final MarkerOptions marker = new MarkerOptions();
            for (final String cd : hashMap.keySet()) {
                for (final LatLng latLng : hashMap.get(cd)) {
                    mMap.addMarker(marker.position(latLng).title(cd));// les marqueurs sont ajoutés
                }
            }
            /* Un setOnMarkerListener est ensuite implémenté afin que lorsque l'on clique sur
             * un marker l'activité MarkerActivitée est lancée avec en extra des données tel que
             * l'url avec comme paramètres le stopcode du marker (arrêt) sélectionné,
             * la longitude est aussi transmise à des fins d'authentification car pour un stopCode donné
             * il se peut qu'il y aie plusieurs arrêt ayant à chaque fois une longitude différente (la latitude peut
             * dans certain cas être identique)
             * Le titre du marqueur qui correspond au code est aussi envoyé.
             */
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Code de l'arrêt voulu: " + arg0.getTitle(), Toast.LENGTH_LONG);
                    toast.show();
                    String url = "http://rtpi.data.tpg.ch/v1/GetPhysicalStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + arg0.getTitle();
                    String lng = String.valueOf(arg0.getPosition().longitude);
                    Intent intent = new Intent(TPG_Activity.this, MarkerActivity.class);
                    intent.putExtra("Url", url);
                    intent.putExtra("longitude", lng);
                    intent.putExtra("titre", arg0.getTitle());
                    startActivityForResult(intent, 1);
                    return true;
                }
            });
        }
    }
}