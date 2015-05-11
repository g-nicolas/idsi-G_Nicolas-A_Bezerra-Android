package ch.unige.idsi.stayfitgeneva;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class OpenDataExtractorActivity extends ActionBarActivity {
    HashMap<String, ArrayList<ArrayList<String>>> hashMapDataSet;
    Bundle extras;
    String catID;
    ArrayList<String> ressources_URL_Layers;
    GoogleMap googleMap;
    Geocoder geoCoder;
    ArrayList<Double> listLat;
    ArrayList<Double> listLong;
    ArrayList<String> listMarkerName;
    ArrayList<String> fieldValues;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        catID = extras.getString("selected_cat");
        hashMapDataSet = new HashMap<>();
        fieldValues = new ArrayList<>();
        ressources_URL_Layers = new ArrayList<>();
        // Retrieve the map and initial extent from XML layout
        setContentView(R.layout.map_activity);
        createMapView();
        /*
         * PARTIE GESTION DU LEFT DRAWER
         * */
        // Looking for drawer parts by the IDs

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // get array mapType items from strings.xml
        String[] drawerListViewItems = getResources().getStringArray(R.array.mapType);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.activity_main_drawer_list_item, drawerListViewItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        /*
         * PARTIE GESTION DU LEFT DRAWER
         * */
        // Looking for drawer parts by the IDs
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // get list items from strings.xml
        //drawerListViewItems = getResources().getStringArray(R.array.mapType);
        //String strTest = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/81/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=false&maxAllowableOffset=&outSR=&outFields=*&f=pjson";
        listLat = new ArrayList<>();
        listLong = new ArrayList<>();
        listMarkerName = new ArrayList<>();
        try {
            Log.e("CHECKPOINT", "setupContentExtractor");
            setupContentExtractor();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void setupContentExtractor() throws IOException, JSONException {
        switch (catID) {
            case "Parcours randonnée":
                setTitle(catID);
                new OpenDataExtractor().execute();
                break;

            case "Parcours vélo":
                setTitle(catID);
                new OpenDataExtractor().execute();
                break;

            case "Complexe sportif":
                setTitle(catID);
                // Here execute to retrieve array sources to getJSON from URL in a new AsyncTask
                new OpenDataExtractor().execute();
                break;

            case "Emplacement pharmacies":
                setTitle(catID);
                // Here execute to retrieve array sources to getJSON from URL in a new AsyncTask
                new OpenDataExtractor().execute();
                break;

            case "Terrasses de cafés":
                setTitle(catID);
                new OpenDataExtractor().execute();
                break;
        }
    }

    private void createMapView() {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if (null == googleMap) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.gmap_frag1)).getMap();
                googleMap.setMyLocationEnabled(true);

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if (null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("GoogleMapsApp", exception.toString());
        }
    }

    public void changeMapType(String drawer_item) {
        //
        // Save the current extent of the map before changing the map.
        switch (drawer_item) {
            case "Topographic map":
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "Street map":
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "Hybrid map":
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "Satellite map":
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
        }
    }

    /**
     * Adds a marker to the map by specifying the coordinates
     */
    /*
    private void addMarker(double latitude, double longitude, String markerName) {

        // Make sure that the map has been initialised
        if (null != googleMap) {
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(markerName)
                            .draggable(false)
            );
        }
    }*/

    private void addMarker(double latitude, double longitude, String markerName, int icon) {

        /** Make sure that the map has been initialised **/
        if (null != googleMap) {
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(markerName)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(icon))
            );
        }
    }

    private void addPolyline(ArrayList<Double> array_latitude, ArrayList<Double> array_longitude, int color) {
        //
        Double latitude;
        Double longitude;
        PolylineOptions poly = new PolylineOptions();
        poly.color(color);
        poly.geodesic(true);
        poly.width(4);
        for (int i = 0; i < array_latitude.size(); i++) {
            latitude = array_latitude.get(i);
            longitude = array_longitude.get(i);
            poly.add(new LatLng(latitude, longitude));

        }
        //Polyline Polyline = googleMap.addPolyline(poly);
        googleMap.addPolyline(poly);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_data_extractor, menu);
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

    public String clean_Addresse(String lieu) {
        //String result = lieu.substring(s.lastIndexOf(',') + 1).trim();
        //Log.w("CHECKPOINT", "Original  "+lieu);
        String[] parts = lieu.split(", ");
        if (parts.length < 2) {
            return lieu;
        } else {
            String beforeFirstDot = parts[0];
            String afterFirstDot = parts[1];
            if (Character.isLetterOrDigit(afterFirstDot.charAt(afterFirstDot.length() - 1))) {
                return afterFirstDot + " " + beforeFirstDot;
            } else {
                return afterFirstDot + beforeFirstDot;
            }
        }
    }

    public double[] getCoordLat_Long(String adr) throws IOException {
        //
        double latitude = 0.0, longitude = 0.0;
        geoCoder = new Geocoder(OpenDataExtractorActivity.this);
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) geoCoder.getFromLocationName(adr, 50);
            for (Address add : adresses) {
                latitude = add.getLatitude();
                longitude = add.getLongitude();
            }
            return new double[]{latitude, longitude};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class OpenDataExtractor extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            switch (catID) {
                case "Parcours randonnée":
                    final String source_hike_trail = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/144/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=true&maxAllowableOffset=&outSR=4326&outFields=*&f=pjson";
                    final String[] json_hike_trail = new String[1];
                    // Here send array sources to getJSONfromURL in a new thread
                    try {
                        json_hike_trail[0] = getJSONfromURL(source_hike_trail);
                        // Here parse the json
                        Log.e("CHECKPOINT JSON HIKE", json_hike_trail[0]);
                        parseJSON(1, json_hike_trail[0]);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "Parcours vélo":
                    final String source_bike_trail = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/299/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=true&maxAllowableOffset=&outSR=4326&outFields=*&f=pjson";
                    final String[] json_bike_trail = new String[1];
                    // Here send array sources to getJSONfromURL in a new thread
                    try {
                        json_bike_trail[0] = getJSONfromURL(source_bike_trail);
                        // Here parse the json
                        Log.e("CHECKPOINT JSON BIKE", json_bike_trail[0]);
                        parseJSON(1, json_bike_trail[0]);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "Complexe sportif":
                    final String source_sports_facilities_location = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/419/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=true&maxAllowableOffset=&outSR=4326&outFields=*&f=pjson";
                    final String source_sports_facilities_trail = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/420/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=true&maxAllowableOffset=&outSR=4326&outFields=*&f=pjson";
                    final String[] json_sports_facilities_trail = new String[1];
                    final String[] json_sports_facilities_location = new String[1];
                    // Here send array sources to getJSONfromURL in a new thread
                    try {
                        json_sports_facilities_location[0] = getJSONfromURL(source_sports_facilities_location);
                        json_sports_facilities_trail[0] = getJSONfromURL(source_sports_facilities_trail);
                        // Here parse the json
                        Log.e("CHECKPOINT", "parse the json sport ");
                        parseJSON(1, json_sports_facilities_location[0]);
                        parseJSON(2, json_sports_facilities_trail[0]);
                        /*
                        Log.w("CHECKPOINT Hmap", String.valueOf(hashMapDataSet.values()));
                        Log.w("CHECKPOINT Hmap2", String.valueOf(hashMapDataSet.entrySet()));
                        Log.i("CHECKPOINT Hmap3", String.valueOf(hashMapDataSet.get("PHARMACIE")));*/
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "Emplacement pharmacies":
                    // source1 = pharmacies ; source2 = hospitals
                    final String source_pharmacies = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/80/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=false&maxAllowableOffset=&outSR=&outFields=*&f=pjson";
                    final String source_hospitals = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/81/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=false&maxAllowableOffset=&outSR=&outFields=*&f=pjson";
                    final String[] json_pharmacies = new String[1];
                    final String[] json_hospitals = new String[1];
                    // Here send array sources to getJSONfromURL in a new thread
                    try {
                        Log.e("CHECKPOINT", "parse the json pharma ");
                        json_pharmacies[0] = getJSONfromURL(source_pharmacies);
                        json_hospitals[0] = getJSONfromURL(source_hospitals);
                        // Here parse the json
                        parseJSON(1, json_pharmacies[0]);
                        parseJSON(2, json_hospitals[0]);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "Terrasses de cafés":
                    final String source_terrasses = "http://ge.ch/ags1/rest/services/open_data/vector_layers/MapServer/421/query?text=&geometry=&geometryType=esriGeometryPoint&inSR=2056&spatialRel=esriSpatialRelIntersects&relationParam=&objectIds=&where=0%3C1&time=&returnCountOnly=false&returnIdsOnly=false&returnGeometry=false&maxAllowableOffset=&outSR=&outFields=*&f=pjson";
                    final String[] json_terrasses = new String[1];
                    // Here send array sources to getJSONfromURL in a new thread
                    try {
                        json_terrasses[0] = getJSONfromURL(source_terrasses);
                        // Here parse the json
                        Log.e("CHECKPOINT", "parse the json terrasse ");
                        parseJSON(1, json_terrasses[0]);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return 1;
        }

        /*
        * Send a GET to retrieve JSON from URL
        * And return the given JSON in a string[]
        * Must be execute on another thread, not on the main one
        * */
        private String getJSONfromURL(String url) throws IOException {
            // Initialize
            String resultString;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();


            con.setRequestMethod("GET");
            // Fetching the JSON data from sitg
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            resultString = response.toString();
            return resultString;
        }

        /*
        * Parse a retrieved JSON from URL accordind to the selected category
        * And return ...
        * */
        private void parseJSON(int id, String json_String) throws IOException, JSONException {
            // Initialize
            //try parse the string to a JSON object
            // That will give the entire string as a Json Object
            JSONObject jsonObject = new JSONObject(json_String);
            // From there, pull out an individual array as a JsonArray
            JSONArray jsonArray = jsonObject.getJSONArray("features");
            // Access each object of the array under "features"
            switch (catID) {
                case "Parcours randonnée":
                    if (id == 1) {
                        ArrayList<ArrayList<String>> array_list = new ArrayList<>();
                        Log.v("CHECKPOINT", " HIKING TRAILS");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ArrayList<String> objectValues = new ArrayList<>();
                            JSONObject object_JSON = jsonArray.getJSONObject(i);
                            // Access data from the object "attributes"
                            JSONObject curr_JSONObject = object_JSON.getJSONObject("attributes");
                            String nom_itineraire = curr_JSONObject.getString("NOM_ITINERAIRE");
                            //Log.e("CHECKPOINT", nom_itineraire);
                            String no_itineraire = curr_JSONObject.getString("NUMERO_ITINERAIRE");
                            JSONObject curr_JSONObject_geometry = object_JSON.getJSONObject("geometry");
                            String paths = curr_JSONObject_geometry.getString("paths");
                            objectValues.add(nom_itineraire);
                            objectValues.add(paths);
                            objectValues.add(no_itineraire);
                            array_list.add(objectValues);
                        }
                        hashMapDataSet.put("HIKING_TRAIL", array_list);
                    }
                    break;

                case "Parcours vélo":
                    if (id == 1) {
                        ArrayList<ArrayList<String>> array_list = new ArrayList<>();
                        Log.v("CHECKPOINT", " HIKING TRAILS");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ArrayList<String> objectValues = new ArrayList<>();
                            JSONObject object_JSON = jsonArray.getJSONObject(i);
                            // Access data from the object "attributes"
                            JSONObject curr_JSONObject = object_JSON.getJSONObject("attributes");
                            String nom_itineraire = curr_JSONObject.getString("NOM");
                            String type_itineraire = curr_JSONObject.getString("TYPE");
                            String statut_itineraire = curr_JSONObject.getString("STATUT");
                            JSONObject curr_JSONObject_geometry = object_JSON.getJSONObject("geometry");
                            String paths = curr_JSONObject_geometry.getString("paths");
                            objectValues.add(nom_itineraire);
                            objectValues.add(paths);
                            objectValues.add(type_itineraire);
                            objectValues.add(statut_itineraire);
                            array_list.add(objectValues);
                        }
                        hashMapDataSet.put("BIKE_TRAIL", array_list);
                    }
                    break;

                case "Complexe sportif":
                    Log.e("CHECKPOINT", "SPORT");
                    if (id == 1) {
                        ArrayList<ArrayList<String>> array_list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ArrayList<String> objectValues = new ArrayList<>();
                            JSONObject object_JSON = jsonArray.getJSONObject(i);
                            // Access data from the object "attributes"
                            JSONObject curr_JSONObject = object_JSON.getJSONObject("attributes");
                            String sport = curr_JSONObject.getString("SPORT");
                            String type = curr_JSONObject.getString("TYPE");
                            String commune = curr_JSONObject.getString("COMMUNE");
                            String lien_fiche_descriptive = curr_JSONObject.getString("LIEN_FICHE_DESCRIPTIVE");
                            JSONObject curr_JSONObject_geometry = object_JSON.getJSONObject("geometry");
                            String latitude = curr_JSONObject_geometry.getString("y");
                            String longitude = curr_JSONObject_geometry.getString("x");
                            objectValues.add(sport);
                            objectValues.add(latitude);
                            objectValues.add(longitude);
                            objectValues.add(type);
                            objectValues.add(commune);
                            objectValues.add(lien_fiche_descriptive);
                            array_list.add(objectValues);
                        }
                        hashMapDataSet.put("SPORTS_CENTER_LOCATION", array_list);
                    } else if (id == 2) {
                        ArrayList<ArrayList<String>> array_list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ArrayList<String> objectValues = new ArrayList<>();
                            JSONObject object_JSON = jsonArray.getJSONObject(i);
                            // Access data from the object "attributes"
                            JSONObject curr_JSONObject = object_JSON.getJSONObject("attributes");
                            String designation = curr_JSONObject.getString("DESIGNATION");
                            String lien_fiche_descriptive = curr_JSONObject.getString("LIEN_FICHE_DESCRIPTIVE");
                            String promnb = curr_JSONObject.getString("PROMNB");
                            JSONObject curr_JSONObject_geometry = object_JSON.getJSONObject("geometry");
                            String paths = curr_JSONObject_geometry.getString("paths");
                            objectValues.add(designation);
                            objectValues.add(paths);
                            objectValues.add(lien_fiche_descriptive);
                            objectValues.add(promnb);
                            array_list.add(objectValues);
                        }
                        Log.e("ValueSTR", String.valueOf(array_list));
                        hashMapDataSet.put("SPORTS_CENTER_TRAIL", array_list);
                    }
                    break;
                case "Emplacement pharmacies":
                    if (id == 1) {
                        ArrayList<ArrayList<String>> array_list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ArrayList<String> objectValues = new ArrayList<>();
                            JSONObject object_JSON = jsonArray.getJSONObject(i);
                            // Access data from the object "attributes"
                            JSONObject curr_JSONObject = object_JSON.getJSONObject("attributes");
                            //String idPADR = curr_JSONObject.getString("IDPADR");
                            String pharmacie = curr_JSONObject.getString("PHARMACIE");
                            String adresse = curr_JSONObject.getString("ADRESSE");
                            String no_postal = curr_JSONObject.getString("NO_POSTAL");
                            String commune = curr_JSONObject.getString("COMMUNE");
                            String telephone = curr_JSONObject.getString("TELEPHONE");
                            String adr = adresse + ", " + no_postal + " " + commune;
                            double coordLatLong[] = getCoordLat_Long(adr);
                            objectValues.add(pharmacie);
                            objectValues.add(Double.toString(coordLatLong[0]));
                            objectValues.add(Double.toString(coordLatLong[1]));
                            objectValues.add(adr);
                            objectValues.add(no_postal);
                            objectValues.add(commune);
                            objectValues.add(telephone);
                            array_list.add(objectValues);
                        }
                        Log.e("ValueSTR", String.valueOf(array_list));
                        hashMapDataSet.put("PHARMACIE", array_list);
                    } else if (id == 2) {
                        ArrayList<ArrayList<String>> array_list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ArrayList<String> objectValues = new ArrayList<>();
                            JSONObject object_JSON = jsonArray.getJSONObject(i);
                            // Access data from the object "attributes"
                            JSONObject curr_JSONObject = object_JSON.getJSONObject("attributes");
                            String hopital = curr_JSONObject.getString("NOM_ETABLISSEMENT");
                            String adresse = curr_JSONObject.getString("ADRESSE");
                            String type_etablissement = curr_JSONObject.getString("TYPE_ETABLISSEMENT");
                            String telephone = curr_JSONObject.getString("TELEPHONE");
                            String fax = curr_JSONObject.getString("FAX");
                            String siteweb = curr_JSONObject.getString("SITE_WEB");
                            double coordLatLong[] = getCoordLat_Long(adresse);
                            objectValues.add(hopital);
                            objectValues.add(Double.toString(coordLatLong[0]));
                            objectValues.add(Double.toString(coordLatLong[1]));
                            objectValues.add(adresse);
                            objectValues.add(type_etablissement);
                            objectValues.add(telephone);
                            objectValues.add(fax);
                            objectValues.add(siteweb);
                            array_list.add(objectValues);
                        }
                        hashMapDataSet.put("HOPITAL", array_list);
                    }
                    break;
                case "Terrasses de cafés":
                    if (id == 1) {
                        ArrayList<ArrayList<String>> array_list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ArrayList<String> objectValues = new ArrayList<>();
                            JSONObject object_JSON = jsonArray.getJSONObject(i);
                            // Access data from the object "attributes"
                            JSONObject curr_JSONObject = object_JSON.getJSONObject("attributes");
                            String nom_cafe = curr_JSONObject.getString("NOM_CAFE");
                            String lieu = curr_JSONObject.getString("LIEU");
                            String no_adresse = curr_JSONObject.getString("NUMERO");
                            String no_terrasses = curr_JSONObject.getString("NO_TERRASSE");
                            String rive = curr_JSONObject.getString("RIVE");
                            String periode_debut = curr_JSONObject.getString("DEBUT");
                            String periode_fin = curr_JSONObject.getString("FIN");
                            String type_terrasse = curr_JSONObject.getString("OBJET");
                            String adr = clean_Addresse(lieu) + " " + no_adresse + ", Genève";
                            double coordLatLong[] = getCoordLat_Long(adr);
                            String periode = periode_debut + " -- " + periode_fin;
                            objectValues.add(nom_cafe);
                            objectValues.add(Double.toString(coordLatLong[0]));
                            objectValues.add(Double.toString(coordLatLong[1]));
                            objectValues.add(adr);
                            objectValues.add(no_terrasses);
                            objectValues.add(rive);
                            objectValues.add(periode);
                            objectValues.add(type_terrasse);
                            array_list.add(objectValues);
                        }
                        Log.e("ValueSTR", String.valueOf(array_list));
                        hashMapDataSet.put("TERRASSES", array_list);
                    }
                    break;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            //
            Log.w("CHECKPOINT", "onPostExecute");
            Random rnd = new Random();
            //Log.w("CHECKPOINT", "Fin de la AsyncTask");
            switch (catID) {
                case "Parcours randonnée":
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    for (int i = 0; i < hashMapDataSet.get("HIKING_TRAIL").size(); i++) {
                        // For each in String values
                        ArrayList<String> array = hashMapDataSet.get("HIKING_TRAIL").get(i);
                        String paths = array.get(1);
                        //Log.e("Nom Array", array.get(0));
                        //Log.e("Contenu Array", array.get(1));
                        String delims = "\\]+";
                        String[] paths_values = paths.split(delims);
                        ArrayList<Double> arrayDoubleLat = new ArrayList<>();
                        ArrayList<Double> arrayDoubleLng = new ArrayList<>();
                        // Each values of Parcours
                        for (String values : paths_values) {
                            values = values.trim();
                            //Log.e("Content values", String.valueOf(values));
                            // Beginning of subArray in array Paths
                            if (values.startsWith(",[[")) {
                                // When a SubArray is found, Add the current array to paths
                                // Create a new current array
                                String[] stringCoordLatLong = values.split(",\\[\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                        addPolyline(arrayDoubleLat, arrayDoubleLng, color);
                                arrayDoubleLat = new ArrayList<>();
                                arrayDoubleLng = new ArrayList<>();
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                            // Beginning of paths
                            else if (values.startsWith("[[[")) {
                                String[] stringCoordLatLong = values.split("\\[\\[\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                            // Other LatLng Values
                            else {
                                String[] stringCoordLatLong = values.split(",\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                        }
                        // Add Polyline for path without subArray
                        addPolyline(arrayDoubleLat, arrayDoubleLng, color);
                    }
                    break;

                case "Parcours vélo":
                    for (int i = 0; i < hashMapDataSet.get("BIKE_TRAIL").size(); i++) {
                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        // For each in String values
                        ArrayList<String> array = hashMapDataSet.get("BIKE_TRAIL").get(i);
                        String str_values = array.get(1);
                        //Log.e("Nom Array", array.get(0));
                        //Log.e("Contenu Array", array.get(1));
                        String delims = "\\]+";
                        String[] values = str_values.split(delims);
                        ArrayList<Double> arrayDoubleLat = new ArrayList<>();
                        ArrayList<Double> arrayDoubleLng = new ArrayList<>();
                        // Each values of Parcours
                        for (String val : values) {
                            val = val.trim();
                            //Log.e("Content values", String.valueOf(values));
                            // Beginning of subArray in array Paths
                            if (val.startsWith(",[[")) {
                                // When a SubArray is found, Add the current array to paths
                                // Create a new current array
                                String[] stringCoordLatLong = val.split(",\\[\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                        addPolyline(arrayDoubleLat, arrayDoubleLng, color);
                                arrayDoubleLat = new ArrayList<>();
                                arrayDoubleLng = new ArrayList<>();
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                            // Beginning of paths
                            else if (val.startsWith("[[[")) {
                                String[] stringCoordLatLong = val.split("\\[\\[\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                            // Other LatLng Values
                            else {
                                String[] stringCoordLatLong = val.split(",\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                        }
                        // Add Polyline for path without subArray
                        addPolyline(arrayDoubleLat, arrayDoubleLng, color);
                    }
                    break;

                case "Complexe sportif":
                    for (int i = 0; i < hashMapDataSet.get("SPORTS_CENTER_TRAIL").size(); i++) {
                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        // For each in String values
                        ArrayList<String> array = hashMapDataSet.get("SPORTS_CENTER_TRAIL").get(i);
                        String str_values = array.get(1);
                        //Log.e("Contenu Array", array.get(1));
                        String delims = "\\]+";
                        String[] values = str_values.split(delims);
                        ArrayList<Double> arrayDoubleLat = new ArrayList<>();
                        ArrayList<Double> arrayDoubleLng = new ArrayList<>();
                        // Each values of Parcours
                        for (String val : values) {
                            val = val.trim();
                            //Log.e("Content values", String.valueOf(values));
                            // Beginning of subArray in array Paths
                            if (val.startsWith(",[[")) {
                                // When a SubArray is found, Add the current array to paths
                                // Create a new current array
                                String[] stringCoordLatLong = val.split(",\\[\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                        addPolyline(arrayDoubleLat, arrayDoubleLng, color);
                                arrayDoubleLat = new ArrayList<>();
                                arrayDoubleLng = new ArrayList<>();
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                            // Beginning of paths
                            else if (val.startsWith("[[[")) {
                                String[] stringCoordLatLong = val.split("\\[\\[\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                            // Other LatLng Values
                            else {
                                String[] stringCoordLatLong = val.split(",\\[");
                                // Lat = coordLatLong[1]   Long = coordLatLong[0]
                                String[] coordLatLong = stringCoordLatLong[1].split(",");
                                arrayDoubleLat.add(Double.valueOf(coordLatLong[1]));
                                arrayDoubleLng.add(Double.valueOf(coordLatLong[0]));
                            }
                        }
                        // Add Polyline for path without subArray
                        addPolyline(arrayDoubleLat, arrayDoubleLng, color);
                    }
                    Log.v("CHECKPOINT", "SPORTS_CENTER_LOCATION");
                    for (int i = 0; i < hashMapDataSet.get("SPORTS_CENTER_LOCATION").size(); i++) {
                        // For each in String values
                        ArrayList<String> array = hashMapDataSet.get("SPORTS_CENTER_LOCATION").get(i);
                        //Log.e("Nom Array", array.get(0));
                        Double coordLat = Double.valueOf(array.get(1));
                        Double coordLong = Double.valueOf(array.get(2));
                        String sport = array.get(0);
                        int marker = R.drawable.map_marker_orange;
                        switch (sport){
                            case "Basketball":
                                marker = R.drawable.basketball_marker;
                                break;
                            case "Football":
                                marker = R.drawable.football_marker;
                                break;
                            case "Badminton":
                                marker = R.drawable.badmington_racket_marker;
                                break;
                            case "Skateboard":
                                marker = R.drawable.skateboard_marker;
                                break;
                            case "Musculation":
                                marker = R.drawable.fitness_marker;
                                break;
                            case "Tennis de table":
                                marker = R.drawable.pingpong_marker;
                                break;
                            case "Fitness":
                                marker = R.drawable.fitness_marker;
                                break;
                            case "Tennis":
                                marker = R.drawable.tennisball_marker;
                                break;
                            case "Volley-ball":
                                marker = R.drawable.volleyball_marker;
                                break;
                            case "Unihockey":
                                marker = R.drawable.hockey_stick_marker;
                                break;
                            case "Golf":
                                marker = R.drawable.golf_marker;
                                break;
                            case "Athlétisme":
                                marker = R.drawable.athlete_marker;
                                break;
                        }
                        addMarker(coordLat, coordLong, array.get(0), marker);
                    }
                    break;

                case "Emplacement pharmacies":
                    //Log.e("Content hashMapDataSet", String.valueOf(hashMapDataSet.size()));

                    // For each ArrayList in the set of the hashmap values
                    for (int i = 0; i < hashMapDataSet.get("PHARMACIE").size(); i++) {
                        // For each in String values
                        ArrayList<String> array = hashMapDataSet.get("PHARMACIE").get(i);
                        Double coordLat = Double.valueOf(array.get(1));
                        Double coordLong = Double.valueOf(array.get(2));
                        addMarker(coordLat, coordLong, array.get(0), R.drawable.redcrossicon);
                    }
                    for (int i = 0; i < hashMapDataSet.get("HOPITAL").size(); i++) {
                        // For each in String values
                        ArrayList<String> array = hashMapDataSet.get("HOPITAL").get(i);
                        //Log.e("Content Array", String.valueOf());
                        //Log.e("Size Array", String.valueOf(array.size()));Double.parseDouble
                        Double coordLat = Double.valueOf(array.get(1));
                        Double coordLong = Double.valueOf(array.get(2));
                        addMarker(coordLat, coordLong, array.get(0), R.drawable.hospital_h_icon);
                    }
                    break;

                case "Terrasses de cafés":
                    for (int i = 0; i < hashMapDataSet.get("TERRASSES").size(); i++)
                    //(ArrayList<String> array : hashMapDataSet.get("PHARMACIE"))
                    {
                        // For each in String values
                        ArrayList<String> array = hashMapDataSet.get("TERRASSES").get(i);
                        //Log.e("Content Array", String.valueOf());
                        //Log.e("Size Array", String.valueOf(array.size()));Double.parseDouble
                        Double coordLat = Double.valueOf(array.get(1));
                        Double coordLong = Double.valueOf(array.get(2));
                        String type = array.get(7);
                        int marker = R.drawable.juice_marker;
                        switch (type) {
                            case "terrasse à l'année":
                                marker = R.drawable.wineglass_marker;
                                break;
                            case "terrasse parisienne":
                                marker = R.drawable.coffee_marker;
                                break;
                        }
                        addMarker(coordLat, coordLong, array.get(0), marker);
                    }
                    break;
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            mDrawerLayout.closeDrawer(mDrawerList);
            String drawer_item_text = ((TextView) view).getText().toString();
            // DO SOMETHING HERE
            changeMapType(drawer_item_text);
        }

    }
}