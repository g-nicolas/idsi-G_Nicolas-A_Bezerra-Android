package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class CategoriesActivity extends Activity {
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        button1 = (Button) findViewById(R.id.button_hiking_trail);
        button2 = (Button) findViewById(R.id.button_bike_trail);
        button3 = (Button) findViewById(R.id.button_sports_center);
        button4 = (Button) findViewById(R.id.button_pharmacy);
        button5 = (Button) findViewById(R.id.button_patio);

        /**
         *  When button Parcours randonnée is clicked,
         *
         * */
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String buttonText = button1.getText().toString();
                Intent intent_selected_cat = new Intent(CategoriesActivity.this, OpenDataExtractorActivity.class);
                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);

            }
        });
        /**
         *  When button Parcours vélo is clicked,
         *
         * */
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String buttonText = button2.getText().toString();
                Intent intent_selected_cat = new Intent(CategoriesActivity.this, OpenDataExtractorActivity.class);
                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);
            }
        });


        /**
         *  When button3 Complexe sportif is clicked,
         *
         * */
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String buttonText = button3.getText().toString();
                Intent intent_selected_cat = new Intent(CategoriesActivity.this, OpenDataExtractorActivity.class);
                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);
            }
        });

        /**
         *  When button4 Pharmacies/Hopital is clicked,
         *
         * */
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String buttonText = button4.getText().toString();
                Intent intent_selected_cat = new Intent(CategoriesActivity.this, OpenDataExtractorActivity.class);
                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);
            }
        });

        /**
         *  When button5 Terrasses de cafés is clicked,
         *
         * */
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String buttonText = button5.getText().toString();
                Intent intent_selected_cat = new Intent(CategoriesActivity.this, OpenDataExtractorActivity.class);
                intent_selected_cat.putExtra("selected_cat", buttonText);
                CategoriesActivity.this.startActivity(intent_selected_cat);
            }
        });
    }
}
