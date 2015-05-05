package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import org.xmlpull.v1.*;
import android.util.Xml;

/**
 * Created by andre on 11.04.15.
 */

public class MainActivity extends Activity {
    private Button position;
    private Button weather;
    private Button tpg;
    private Button test;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        position =(Button)findViewById(R.id.maposition);
        weather = (Button)findViewById(R.id.weatherbutton);
        tpg = (Button)findViewById(R.id.tpg);
        test = (Button)findViewById(R.id.testmap);

        gestureDetector = new GestureDetector(new SwipeGestureDetector());

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivityForResult(intent,1);
            }
        });
        weather.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,WeatherActivity.class);
                startActivityForResult(intent,2);
            }
        });
        tpg.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,TPG_Activity.class);
                startActivityForResult(intent,3);
            }
        });
        test.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,MapsActivity2.class);
                startActivityForResult(intent,4);
            }
        });

    }
    //Méthode pour switcher entre les activités à gauche ou à droite.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onLeftSwipe() {
        Intent intent= new Intent(MainActivity.this,WeatherActivity.class);
        startActivityForResult(intent,2);
    }

    private void onRightSwipe() {
        Intent intent= new Intent(MainActivity.this,TPG_Activity.class);
        startActivityForResult(intent,3);
    }
    private class SwipeGestureDetector
            extends GestureDetector.SimpleOnGestureListener {
        // Swipe properties, you can change it to make the swipe
        // longer or shorter and speed
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    MainActivity.this.onLeftSwipe();

                    // Right swipe
                } else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    MainActivity.this.onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error on gestures");
            }
            return false;
        }
    }
}
