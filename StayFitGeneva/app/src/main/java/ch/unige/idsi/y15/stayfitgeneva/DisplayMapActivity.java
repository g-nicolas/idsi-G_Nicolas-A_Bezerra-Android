package ch.unige.idsi.y15.stayfitgeneva;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapOptions.MapType;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Polygon;


public class DisplayMapActivity extends ActionBarActivity {

    Bundle extras;
    String catID;
    MapView mMapView;
    ArcGISFeatureLayer mFeatureLayer;
    GraphicsLayer mGraphicsLayer;
    boolean mIsMapLoaded;
    String mFeatureServiceURL;

    // The basemap switching menu items.
    MenuItem mStreetsMenuItem = null;
    MenuItem mTopoMenuItem = null;
    MenuItem mHybridMenuItem = null;
    MenuItem mSatelliteMenuItem = null;

    // Create MapOptions for each type of basemap.
    final MapOptions mTopoBasemap = new MapOptions(MapType.TOPO);
    final MapOptions mStreetsBasemap = new MapOptions(MapType.STREETS);
    final MapOptions mHybrid = new MapOptions(MapType.HYBRID);
    final MapOptions mSatellite = new MapOptions(MapType.SATELLITE);
    // The current map extent, use to set the extent of the map after switching basemaps.
    Polygon mCurrentMapExtent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        catID = extras.getString("selected_cat");

        // Retrieve the map and initial extent from XML layout
        addLayersBySelectedCatID(catID);

        // Set the MapView to allow the user to rotate the map when as part of a pinch gesture.
        mMapView.setAllowRotationByPinch(true);


        // Set the Esri logo to be invisible
        mMapView.setEsriLogoVisible(false);
        // Disabled wrap around map.
        mMapView.enableWrapAround(false);



        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            public void onStatusChanged(Object source, STATUS status) {
                if ((source == mMapView) && (status == STATUS.INITIALIZED)) {
                    mIsMapLoaded = true;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_map, menu);

        // Get the basemap switching menu items.
        mTopoMenuItem = menu.getItem(0);
        mStreetsMenuItem = menu.getItem(1);
        mHybridMenuItem = menu.getItem(2);
        mSatelliteMenuItem = menu.getItem(3);

        // Also set the topo basemap menu item to be checked, as this is the default.
        mTopoMenuItem.setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Save the current extent of the map before changing the map.
        mCurrentMapExtent = mMapView.getExtent();

        // Handle menu item selection.
        switch (item.getItemId()) {
            case R.id.World_Street_Map:
                mMapView.setMapOptions(mStreetsBasemap);
                mStreetsMenuItem.setChecked(true);
                return true;
            case R.id.World_Topo:
                mMapView.setMapOptions(mTopoBasemap);
                mTopoMenuItem.setChecked(true);
                return true;
            case R.id.Hybrid:
                mMapView.setMapOptions(mHybrid);
                mHybridMenuItem.setChecked(true);
                return true;
            case R.id.Satellite:
                mMapView.setMapOptions(mSatellite);
                mSatelliteMenuItem.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addLayersBySelectedCatID(String categorie_id)
    {
        switch (categorie_id)
        {
            case "Parcours randonnée et vélo":
                setContentView(R.layout.activity_display_map1);
                mGraphicsLayer = new GraphicsLayer();
                mGraphicsLayer.removeAll();
                // Retrieve the map and initial extent from XML layout
                mMapView = (MapView) findViewById(R.id.map1);

                // Get the feature service URL from values->strings.xml
                mFeatureServiceURL = this.getResources().getString(R.string.urlLayerSentierPedestreLoc);
                break;

            case "Complexe sportif":
                setContentView(R.layout.activity_display_map2);
                mGraphicsLayer = new GraphicsLayer();

                mGraphicsLayer.removeAll();
                // Retrieve the map and initial extent from XML layout
                mMapView = (MapView) findViewById(R.id.map2);

                // Get the feature service URL from values->strings.xml
                mFeatureServiceURL = this.getResources().getString(R.string.urlLayerSportCenterLoc);
                break;

            case "Emplacement pharmacies":
                setContentView(R.layout.activity_display_map3);
                mGraphicsLayer = new GraphicsLayer();
                mGraphicsLayer.removeAll();
                // Retrieve the map and initial extent from XML layout
                mMapView = (MapView) findViewById(R.id.map3);

                // Get the feature service URL from values->strings.xml
                mFeatureServiceURL = this.getResources().getString(R.string.urlLayerPharmacyLoc);
                break;

            case "Terrasses de cafés":
                setContentView(R.layout.activity_display_map4);
                mGraphicsLayer = new GraphicsLayer();
                mGraphicsLayer.removeAll();
                // Retrieve the map and initial extent from XML layout
                mMapView = (MapView) findViewById(R.id.map4);

                // Get the feature service URL from values->strings.xml
                mFeatureServiceURL = this.getResources().getString(R.string.urlLayerTerrassesLoc);
                break;
        }
        // Add Feature layer to the MapView
        mFeatureLayer = new ArcGISFeatureLayer(mFeatureServiceURL, ArcGISFeatureLayer.MODE.ONDEMAND);
        mMapView.addLayer(mFeatureLayer);

        // Add Graphics layer to the MapView
        mGraphicsLayer = new GraphicsLayer();
        mMapView.addLayer(mGraphicsLayer);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Call MapView.pause to suspend map rendering while the activity is paused, which can save battery usage.
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Call MapView.unpause to resume map rendering when the activity returns to the foreground.
        mMapView.unpause();
    }
}