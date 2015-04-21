package ch.unige.idsi.y15.stayfitgeneva;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    /*
    * When the app is launched,
    * */
    Button button1;
    Button button2;
    Button button3;
    public final static String EXTRA_TEXT = "ch.unige.idsi.y15.stayfitgeneva.EXTRA_TEXT";

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //setContentView(R.layout.basic_map_layout);
         // setContentView(R.layout.category_menu);
         setContentView(R.layout.activity_main);
         //setContentView(R.layout.activity_display_map);

         final Intent intent_category_list = new Intent(MainActivity.this, CategoriesActivity.class);

         button1 = (Button) findViewById(R.id.button_maps);
         button2 = (Button) findViewById(R.id.button_infos_pollen);
         button3 = (Button) findViewById(R.id.button_tpg);

         /**
          *  When button is clicked,
          *
          * */

          button1.setOnClickListener(new View.OnClickListener() {

             public void onClick(View v) {
                 // Perform action on click
                 String buttonText = button1.getText().toString();
                 intent_category_list.putExtra(EXTRA_TEXT, buttonText);
                 MainActivity.this.startActivity(intent_category_list);
             }
         });

         button2.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click
                 String buttonText = button2.getText().toString();
                 //intent.putExTEXTRA_MESSAGE, buttonText);
             }
         });

         button3.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click
                 String buttonText = button3.getText().toString();
               //intent.putExTEXTRA_MESSAGE, bu
               }
         });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
