package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {
    public final static String EXTRA_TEXT = "ch.unige.idsi.y15.stayfitgeneva.EXTRA_TEXT";
    private Button weather;
    private Button tpg;
    private Button maps;
    private Typeface weatherfont;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weather = (Button)findViewById(R.id.weatherbutton);
        tpg = (Button)findViewById(R.id.tpg);
        maps = (Button)findViewById(R.id.button_maps);
        Spannable buttonLabel = new SpannableString(" ");
        buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.olympic,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        maps.setText(buttonLabel);
        Spannable buttonLabel2 = new SpannableString(" ");
        buttonLabel2.setSpan(new ImageSpan(getApplicationContext(), R.drawable.bus,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tpg.setText(buttonLabel2);
        weatherfont = Typeface.createFromAsset(getApplication().getAssets(), "fonts/weather.ttf");
        weather.setTypeface(weatherfont);
        String icon = getApplication().getString(R.string.weather_sunny);
        weather.setText(icon);

        final Intent intent_category_list = new Intent(MainActivity.this, CategoriesActivity.class);

        gestureDetector = new GestureDetector(new SwipeGestureDetector());


        weather.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,WeatherActivity.class);
                startActivity(intent);
            }
        });
        tpg.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,TPG_Activity.class);
                startActivity(intent);
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Perform action on click
                String buttonText = maps.getText().toString();
                intent_category_list.putExtra(EXTRA_TEXT, buttonText);
                MainActivity.this.startActivity(intent_category_list);
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
