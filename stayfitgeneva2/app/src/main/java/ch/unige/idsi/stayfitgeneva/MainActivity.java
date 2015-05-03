package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    //private  String xmlFolderPath;
    //private  String environmentxmlFolderPath;
    //private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        position =(Button)findViewById(R.id.maposition);
        //tv = (TextView)findViewById(R.id.textView);
        weather = (Button)findViewById(R.id.weatherbutton);
        tpg = (Button)findViewById(R.id.tpg);
       // try {
         //   getArret();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //} catch (XmlPullParserException e) {
        //    e.printStackTrace();
        //}


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
    }

   /** private class getArret extends AsyncTask<URL, void, void> {


        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory;
        xmlPullParserFactory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
        URL url = new URL("http://rtpi.data.tpg.ch/v1/GetPhysicalStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b");
        URLConnection connection = url.openConnection();
        InputStreamReader in = new InputStreamReader(connection.getInputStream());
        File filexml = new File(String.valueOf(in));
        FileInputStream fileInputStream = new FileInputStream(filexml);
        xmlPullParser.setInput(new InputStreamReader(fileInputStream));
        int length = connection.getContentLength();
        int eventType = xmlPullParser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){

            StringWriter out = new StringWriter(length);
            String content = out.toString();
            tv.setText(content);}

        in.close();

    }*/


}
