package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
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

        Spannable buttonLabel = new SpannableString(" ");
        buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.olympic,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        button3.setText(buttonLabel);

        Spannable buttonLabel2 = new SpannableString(" ");
        buttonLabel2.setSpan(new ImageSpan(getApplicationContext(), R.drawable.cocktail,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        button5.setText(buttonLabel2);

        Spannable buttonLabel3 = new SpannableString(" ");
        buttonLabel3.setSpan(new ImageSpan(getApplicationContext(), R.drawable.hospital,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        button4.setText(buttonLabel3);

        Spannable buttonLabel4 = new SpannableString(" ");
        buttonLabel4.setSpan(new ImageSpan(getApplicationContext(), R.drawable.bicycle,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        button2.setText(buttonLabel4);

        Spannable buttonLabel5 = new SpannableString(" ");
        buttonLabel5.setSpan(new ImageSpan(getApplicationContext(), R.drawable.rando,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        button1.setText(buttonLabel5);
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
