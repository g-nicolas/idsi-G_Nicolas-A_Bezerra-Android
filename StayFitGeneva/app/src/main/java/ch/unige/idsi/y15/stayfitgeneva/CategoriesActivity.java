package ch.unige.idsi.y15.stayfitgeneva;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class CategoriesActivity extends ActionBarActivity {
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    public final static String EXTRA_TEXT = "ch.unige.idsi.y15.stayfitgeneva.EXTRA_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        final Intent intent_selected_cat = new Intent(CategoriesActivity.this, DisplayMapActivity.class);

        button1 = (Button) findViewById(R.id.button_bike_hiking_trail);
        button2 = (Button) findViewById(R.id.button_sports_center);
        button3 = (Button) findViewById(R.id.button_pharmacy);
        button4 = (Button) findViewById(R.id.button_patio);

        /**
         *  When button is clicked,
         *
         * */
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Intent intent = new Intent(this, DisplayMapActivity.class);
                String buttonText = button1.getText().toString();

                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);

            }
        });


        /**
         *  When button2 is clicked,
         *
         * */
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Intent intent = new Intent(this, DisplayMapActivity.class);
                String buttonText = button2.getText().toString();

                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);

            }
        });

        /**
         *  When button3 is clicked,
         *
         * */
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Intent intent = new Intent(this, DisplayMapActivity.class);
                String buttonText = button3.getText().toString();

                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);

            }
        });

        /**
         *  When button4 is clicked,
         *
         * */
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Intent intent = new Intent(this, DisplayMapActivity.class);
                String buttonText = button4.getText().toString();

                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);


            }
        });
    }
}
