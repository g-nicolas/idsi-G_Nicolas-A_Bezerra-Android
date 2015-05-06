package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andre on 05.05.15.
 */
public class MarkerActivity extends Activity {
    private TextView ligne;
    private TextView arret;
    private TextView dest;
    private TextView depart;
    private String lng;
    private String s;
    private String val1;
    private String val2;
    private String val3;
    private String stopCode;
    getDetailMarker getDetailMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        dest = (TextView)findViewById(R.id.direction);
        ligne = (TextView)findViewById(R.id.ligne);
        arret = (TextView)findViewById(R.id.nomarret);
        depart = (TextView)findViewById(R.id.Departs);
        Intent intent = getIntent();
        getDetailMarker = new getDetailMarker();
        String url = intent.getStringExtra("Url");
        stopCode = intent.getStringExtra("titre");
        lng = intent.getStringExtra("longitude");
        getDetailMarker.execute(url);
        String url2 = new String("http://rtpi.data.tpg.ch/v1/GetNextDepartures.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + stopCode);
        //LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
        //String check = "http://rtpi.data.tpg.ch/v1/GetStops.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&" + "latitude=" + lat + "&longitude=" + lng;

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
    class getDetailMarker extends AsyncTask<String, Void, ArrayList<String>> {


        Document doc = null;
        ArrayList<String> arrayList = new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            String url = params[0];
            try {
                doc = Jsoup.connect(url).get();
                for (Element element : doc.getElementsByTag("physicalStop")) {
                    Elements elements = element.getElementsByTag("longitude");
                     s = elements.toString().replace("<longitude>", "").replace("</longitude>", "").replaceAll(" ", "").replaceAll("\n", "");
                  //  Log.e("Erreur", s);
                    if(s.equals(lng)){
                        Elements stopName = element.getElementsByTag("stopName");
                        val1 = (stopName.toString().replace("<stopname>", "").replace("</stopname>", "").replaceAll(" ", "").replaceAll("\n", ""));

                        for (Element element1 : element.getElementsByTag("connection")) {
                            Elements linecode = element1.getElementsByTag("lineCode");
                            val2 = (linecode.toString().replace("<linecode>", "").replace("</linecode>", "").replaceAll(" ", "").replaceAll("\n", ""));
                            Elements destname = element1.getElementsByTag("destinationName");
                            val3 = (destname.toString().replace("<destinationname>", "").replace("</destinationname>", "").replaceAll(" ", "").replaceAll("\n", ""));
                        }
                        arrayList.addAll(Arrays.asList(val1,val2,val3));
                    }

                }
                String url2 = new String("http://rtpi.data.tpg.ch/v1/GetNextDepartures.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + stopCode);
                try {
                            doc = Jsoup.connect(url2).get();
                        for(Element element: doc.getElementsByTag("departure")){
                          Elements line = element.getElementsByTag("lineCode");
                            String var = (line.toString().replace("<linecode>", "").replace("</linecode>", "").replaceAll(" ", "").replaceAll("\n", ""));
                            if(var.equals(val2)){

                                Elements e = element.getElementsByTag("waitingTime");
                                String string = e.get(0).toString().replace("<waitingtime>", "").replace("</waitingtime>", "");
                                string = string.replaceAll("\n", "");
                                string = string.replaceAll(" ", "");
                                arrayList.add(string);

                            }
                        }



                        } catch (IOException e) {
                            e.printStackTrace();
                        }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return arrayList;
        }
        protected void onPostExecute(ArrayList<String> result) {

           arret.setText(result.get(0));
            ligne.setText(result.get(1));
            dest.setText(result.get(2));
            depart.setText(result.get(3));


        }
    }

}

