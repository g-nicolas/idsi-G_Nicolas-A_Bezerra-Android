package ch.unige.idsi.stayfitgeneva;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends Activity {
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weathericon;
    Typeface weatherfont;
    getPollen getpol;
    getOzone getozo;
    String url;
    String OPEN_WEATHER_MAP_API;
    Handler handler;



    /**
     * L'objet TypeFace pointe sur les icones qui se trouve dans le dossier assets/fonts
     * <p/>
     * Dans cette classe nous allons faire appel à un nouveau thread afin d'aller chercher les
     * données de façon asynchrone et ainsi ne pas faire crasher l'application en surchargeant le
     * MainThread.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        url = "http://fr.meteovista.be/Europe/Suisse/Geneve/4076470";

        weatherfont = Typeface.createFromAsset(getApplication().getAssets(), "fonts/weather.ttf");
        updateWeatherData();
        weathericon = (TextView) findViewById(R.id.weather_icon);
        cityField = (TextView) findViewById(R.id.cityField);
        updatedField = (TextView) findViewById(R.id.updatedField);
        detailsField = (TextView) findViewById(R.id.detailsField);
        currentTemperatureField = (TextView) findViewById(R.id.currentTemperatureField);
        getpol = new getPollen();
        getozo = new getOzone();
        getpol.execute(url);
        getozo.execute(url);
        handler = new Handler();
        weathericon.setTypeface(weatherfont);
    }

    /**
     * Nous avons fait appel à un handler car il n'est pas possible de mettre à jour l'UI
     * à partir d'un background thread. Seul le MainThread peut mettre à jour l'UI. De facto
     * faire appel à la méthode renderWeather à partir du background thread conduirais à un crash
     * de l'application d'ou la nécéssité de faire appel à un handler.post qui fera en quelque sorte le lien
     * entre le main et le background thread.
     */

    private void updateWeatherData() {
        new Thread() {
            public void run() {
                final JSONObject json = JsonParser();
                if (json == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Erreur dans la récupération des donneés météorologique",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * Cette classe à comme but d'aller chercher les données météorologiques
     * issue de l'API OpenWeatherMap
     * <p/>
     * HttpUrlConnection nous sert à faire la requête.
     * <p/>
     * Nous utilisons un BufferedReader afin de lire la réponse de l' API dans un StringBuffer,
     * qui sera ensuite convertis en un objet JsonObject .
     * <p/>
     * Afin de savoir si les données météorologique sont disponibles nous comparons la valeur
     * de "cod" à 200. Si c'est le cas alors les donneés météorologiques sont disponible.
     * (la valeur de "cod" est la même peut importe la ville questionnée)
     */
    private JSONObject JsonParser() {

        OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=Geneva,Switzerland&units=metric&lang=fr&APPID=de5d0a46fa1dacc1f431c25c2935d5e0";
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    /**
    * Cette méthode va récupérer les informations météorologique et les associés avec le layout.
    *
    * */
    private void renderWeather(JSONObject json) {

        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp")) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);


        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    /**
    *
    * Cette classe invoquée à la fin de la méthode renderWeather, nous permet de définir l'icone
    * précise quant à la météo actuelle.
    *
    * Les limites pour chaque type d'icon à été défini selon les conditions définies sur:
    * http://openweathermap.org/weather-conditions
    *
    * Les icones proviennent du repo github suivant:
    * https://github.com/erikflowers/weather-icons
    *
    * */
    private void setWeatherIcon(int actualId, long sunrise, long sunset) throws IOException {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getApplication().getString(R.string.weather_sunny);
            } else {
                icon = getApplication().getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getApplication().getString(R.string.weather_thunder);

                    break;
                case 3:
                    icon = getApplication().getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = getApplication().getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getApplication().getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = getApplication().getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = getApplication().getString(R.string.weather_rainy);
                    break;
            }
            weathericon.setText(icon);
        }



    }




        class getPollen extends AsyncTask<String, Void, String> {
            String pollenData = "";
            @Override
            protected String doInBackground(String... params) {
                try {
                    ArrayList<Elements> elementsArrayList = new ArrayList<>();
                    String str = params[0];
                    Document doc = Jsoup.connect(str).get();
                    Elements good = doc.select("div[class=rating hayfever_good_sm]");
                    Elements fine = doc.select("div[class=rating hayfever_fine_sm]");
                    Elements bad = doc.select("div[class=rating hayfever_bad_sm]");
                    Elements worst = doc.select("div[class=rating hayfever_worst_sm]");
                    elementsArrayList.addAll(Arrays.asList(good,fine,bad,worst));
                    for (Elements elements : elementsArrayList){
                        if(elements.size() !=0){
                            pollenData = elements.attr("title");
                        }
                        else{
                            Log.e("Pollen", "Pas de données sur le pollen");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return pollenData;
            }
            @Override
            protected void onPostExecute(String result) {
                TextView pol = (TextView) findViewById(R.id.pollen);
                pol.setText(pollenData);

            }
        }

        class getOzone extends AsyncTask<String, Void, String> {
            String uvData = "";
            String soleil = "";

            @Override
            protected String doInBackground(String... params) {
            try {
                String str = params[0];
                ArrayList<Elements> elementsArrayList = new ArrayList<>();
                Document doc = Jsoup.connect(str).get();
                Elements a = doc.select("div[class=rating uv_icon_medium_1_xs]");
                Elements b = doc.select("div[class=rating uv_icon_medium_2_xs]");
                Elements c = doc.select("div[class=rating uv_icon_medium_3_xs]");
                Elements d = doc.select("div[class=rating uv_icon_medium_4_xs]");
                Elements e = doc.select("div[class=rating uv_icon_medium_5_xs]");
                Elements f = doc.select("div[class=rating uv_icon_medium_6_xs]");
                Elements g = doc.select("div[class=rating uv_icon_medium_7_xs]");
                Elements h = doc.select("div[class=rating uv_icon_medium_8_xs]");
                Elements i = doc.select("div[class=rating uv_icon_medium_9_xs]");
                Elements j = doc.select("div[class=rating uv_icon_medium_10_xs]");
                Elements k = doc.select("div[class=rating uv_icon_medium_11_xs]");
                elementsArrayList.addAll(Arrays.asList(a,b,c,d,e,f,g,h,i,j,k));
                for (Elements elements : elementsArrayList){
                    if(elements.size() !=0){
                        uvData = elements.attr("title");
                        if((uvData.endsWith("1"))||(uvData.endsWith("2"))){
                            soleil = "Aucun risque de coup de soleil";
                        }
                        else if((uvData.endsWith("3"))||(uvData.endsWith("4"))){
                            soleil = "Faible risque de coup de soleil";

                        }
                        else if((uvData.endsWith("5"))||(uvData.endsWith("6"))){
                            soleil = "Risque modéré de coup de soleil";

                        }
                        else if((uvData.endsWith("7"))||(uvData.endsWith("8"))){
                            soleil = "Risque élevé de coup de soleil";

                        }
                        else if((uvData.endsWith("9"))||(uvData.endsWith("10"))||(uvData.endsWith("11"))){
                            soleil = "Risque très élevé de coup de soleil";

                        }
                        else{
                            Log.e("Soleil", "Pas de données sur l'indice UV");

                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return soleil;
        }
        @Override
        protected void onPostExecute(String result) {
            // Set description into TextView
            TextView oz = (TextView) findViewById(R.id.uv);
            oz.setText(soleil);

        }
    }

}



