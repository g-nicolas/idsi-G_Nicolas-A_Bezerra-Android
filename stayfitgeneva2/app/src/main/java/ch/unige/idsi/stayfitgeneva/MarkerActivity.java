package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class MarkerActivity extends Activity {

    private String lng;
    private String stopCode;
    getDetailMarker getDetailMarker;
    LinearLayout wrap;
    LinearLayout inflatedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        wrap = (LinearLayout) findViewById(R.id.wrapper1);
        Intent intent = getIntent();
        getDetailMarker = new getDetailMarker();
        // les donneés issue de la classe TPG_ACTIVITY sont récupérée ci-dessous
        String url = intent.getStringExtra("Url");
        stopCode = intent.getStringExtra("titre");
        lng = intent.getStringExtra("longitude");
        getDetailMarker.execute(url);// l'asynctask est executée avec comme paramètre l'url afin d'accéder à l'API TPG pour l'arrêt cliqué
    }

    class getDetailMarker extends AsyncTask<String, Void, HashMap<ArrayList<String>, ArrayList<String>>> {
        Document doc = null;
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> temps = new ArrayList<>();

        HashMap<ArrayList<String>, ArrayList<String>> hashMap = new HashMap<>();


        @Override
        protected HashMap<ArrayList<String>, ArrayList<String>> doInBackground(String... params) {
            String url = params[0];
            try {
                doc = Jsoup.connect(url).get();
                for (Element element : doc.getElementsByTag("physicalStop")) {
                    Elements stopName = element.getElementsByTag("stopName");
                    String val1 = (stopName.toString().replace("<stopname>", "").replace("</stopname>", "").replaceAll(" ", "").replaceAll("\n", ""));
                    Elements elements = element.getElementsByTag("longitude");
                    String s = elements.toString().replace("<longitude>", "").replace("</longitude>", "").replaceAll(" ", "").replaceAll("\n", "");
                    // la longitude est comparée avec la longitude du code de l'arrêt cliqué afin de n'avoir que l'arrêt cliqué et pas les autres arrêts
                    if (s.equals(lng))
                        for (Element element2 : element.getElementsByTag("destinationName")) {
                            temps = new ArrayList<>();
                            arrayList = new ArrayList<>();
                            String d = (element2.toString().replace("<destinationname>", "").replace("</destinationname>", "").replaceAll(" ", "").replaceAll("\n", ""));
                            String url2 = "http://rtpi.data.tpg.ch/v1/GetNextDepartures.xml?key=78b36600-2a9a-11e3-921b-0002a5d5c51b&stopCode=" + stopCode;
                            String val;
                            String val2;
                            String val3;
                            try {
                                /*Ici nous accédons à l'API TPG mais aux prochains départ et comme la syntaxe n'est pas la même et qu'il est impossible de
                                 *de comparé les coordonées nous avons décidé de comparé la destination afin de faire le lien entre les deux requêtes et ne
                                 *pas perdre ainsi la position et finalement récupérer tous les détails de l'arrêt cliqué.
                                 *Les valeurs récupérée ci-dessous de l'API sont: la destination, la ligne et le temps d'arrêt, le nom de l'arrêt é
                                 *ayant déjà été récupérer au dessus.
                                 */
                                doc = Jsoup.connect(url2).get();//
                                for (Element el : doc.getElementsByTag("departure")) {

                                    Elements des = el.getElementsByTag("destinationName");
                                    val = (des.toString().replace("<destinationname>", "").replace("</destinationname>", "").replaceAll(" ", "").replaceAll("\n", ""));
                                    if (d.equals(val)) {

                                        Elements l = el.getElementsByTag("lineCode");
                                        val2 = (l.toString().replace("<linecode>", "").replace("</linecode>", "").replaceAll(" ", "").replaceAll("\n", ""));

                                        Elements destname = el.getElementsByTag("destinationName");
                                        val3 = (destname.toString().replace("<destinationname>", "").replace("</destinationname>", "").replaceAll(" ", "").replaceAll("\n", ""));
                                        Elements e = el.getElementsByTag("waitingTime");
                                        String val4 = e.get(0).toString().replace("<waitingtime>", "").replace("</waitingtime>", "");
                                        val4 = val4.replaceAll("\n", "");
                                        val4 = val4.replaceAll(" ", "");
                                        /*
                                        *  Pour chaque départ nous stockons donc les variables récupérée et les stockons dans une liste à part
                                        *  afin de ne pas les confondre si l'arret comporte plusieurs ligne de bus.
                                        */
                                        temps.add(val4);
                                        arrayList.addAll(Arrays.asList(val1, val2, val3));
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            hashMap.put(arrayList, temps);
                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hashMap;
        }

        protected void onPostExecute(HashMap<ArrayList<String>, ArrayList<String>> result) {
            /*
             *Ici nous récupérons les données issu de l'asynctask et parsons les listes afin d'afficher les données
             *dans notre layout, cependant nous avons eu de la peine afficher toutes les donneés alors nous avons décider
             * d'afficher que les deux prochains départs à l'arrêt cliqué.
             */
            Iterator it = hashMap.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                ArrayList<String> key = (ArrayList<String>) pair.getKey();
                ArrayList<String> value = (ArrayList<String>) pair.getValue();
                inflatedView = (LinearLayout) View.inflate(getApplicationContext(), R.layout.activity_wrapper, null);
                ((TextView) inflatedView.findViewById(R.id.direction)).setText(key.get(2));
                ((TextView) inflatedView.findViewById(R.id.ligne)).setText(key.get(1));
                ((TextView) inflatedView.findViewById(R.id.nomarret)).setText(key.get(0));
                for(int i = 0; i<value.size();i++){

                       if (value.get(i).equals("nomore")) {
                           value.set(i, "Aucun Bus" + "\n");
                       }
                       else if (value.get(i).equals("&amp;gt;1h")){
                           value.set(i,"Plus d'une heure" + "\n");
                       }
                        else{
                        value.set(i,value.get(i)+ " min" + "\n");
                    }
                ((TextView) inflatedView.findViewById(R.id.Departs)).setText((((TextView) inflatedView.findViewById(R.id.Departs)).getText() + value.get(i)));
                }
                wrap.addView(inflatedView);
                it.remove(); // permet éviter l'exception ConcurrentModificationException
            }
        }
    }
}



