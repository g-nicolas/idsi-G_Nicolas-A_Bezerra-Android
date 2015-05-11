package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    public final static String EXTRA_TEXT = "ch.unige.idsi.y15.stayfitgeneva.EXTRA_TEXT";
    Button position;
    Button weather;
    Button tpg;
    GestureDetector gestureDetector;
    Button maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        position = (Button) findViewById(R.id.maposition);
        weather = (Button) findViewById(R.id.weatherbutton);
        tpg = (Button) findViewById(R.id.tpg);
        final Intent intent_category_list = new Intent(MainActivity.this, CategoriesActivity.class);
        maps = (Button) findViewById(R.id.button_maps);
        /**
         *  When button Maps is clicked,
         *
         * */

        maps.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Perform action on click
                String buttonText = maps.getText().toString();
                intent_category_list.putExtra(EXTRA_TEXT, buttonText);
                MainActivity.this.startActivity(intent_category_list);
            }
        });

        gestureDetector = new GestureDetector(new SwipeGestureDetector());

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        weather.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        tpg.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TPG_Activity.class);
                startActivityForResult(intent, 3);
            }
        });


    }

    //Méthode pour switcher entre les activités à gauche ou à droite.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private void onLeftSwipe() {
        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
        startActivityForResult(intent, 2);
    }

    private void onRightSwipe() {
        Intent intent = new Intent(MainActivity.this, TPG_Activity.class);
        startActivityForResult(intent, 3);
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
