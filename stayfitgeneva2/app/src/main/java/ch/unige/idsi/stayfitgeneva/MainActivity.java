package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    public final static String EXTRA_TEXT = "ch.unige.idsi.y15.stayfitgeneva.EXTRA_TEXT";
    private Button weather;
    private Button tpg;
    private Button maps;
    private Typeface weatherfont;

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
}
