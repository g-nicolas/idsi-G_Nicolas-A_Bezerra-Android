package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by andre on 05.05.15.
 */
public class MarkerActivity extends Activity {
    private TextView ligne;
    private TextView arret;
    private TextView dest;
    getDetailMarker getDetailMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        dest = (TextView)findViewById(R.id.direction);
        ligne = (TextView)findViewById(R.id.ligne);
        arret = (TextView)findViewById(R.id.nomarret);
        Intent intent = getIntent();
        getDetailMarker = new getDetailMarker();
        String url = intent.getStringExtra("Url");
        getDetailMarker.execute(url);
        /*Bundle infoQA = this.getIntent().getExtras();
        if (infoQA != null){
            qq=infoQA.getString("question");
            aa=infoQA.getString("reponse");
            s=infoQA.getString("sujet");} */



       /* String lignes = intent.getStringExtra("lignes");
        ligne.setText(lignes);
        String destination = intent.getStringExtra("destination");
        dest.setText(destination);
        String nomArret = intent.getStringExtra("nomarret");
        arret.setText(nomArret);*/



    }
    class getDetailMarker extends AsyncTask<String, Void, Void> {


        Document doc = null;

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            try {
                doc = Jsoup.connect(url).get();
                for (Element element : doc.getElementsByTag("physicalStop")) {
                    Elements stopName = element.getElementsByTag("stopName");
                    arret.setText(stopName.toString().replace("<stopname>", "").replace("</stopname>", "").replaceAll(" ", "").replaceAll("\n", ""));

                    for (Element element1 : element.getElementsByTag("connection")) {
                        Elements linecode = element1.getElementsByTag("lineCode");
                        ligne.setText(linecode.toString().replace("<linecode>", "").replace("</linecode>", "").replaceAll(" ", "").replaceAll("\n", ""));
                        Elements destname = element1.getElementsByTag("destinationName");
                        dest.setText(destname.toString().replace("<destinationname>", "").replace("</destinationname>", "").replaceAll(" ", "").replaceAll("\n", ""));
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

