package my.own;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Main {
    static String[] fileNames = {"PaymentTypes-bd.json", "PaymentTypes-bg.json", "PaymentTypes-hk.json",
            "PaymentTypes-jp.json", "PaymentTypes-kh.json", "PaymentTypes-la.json", "PaymentTypes-mm.json",
            "PaymentTypes-my.json", "PaymentTypes-ph.json", "PaymentTypes-pk.json", "PaymentTypes-ro.json",
            "PaymentTypes-sg.json", "PaymentTypes-sk.json", "PaymentTypes-th.json", "PaymentTypes-tw.json"};

    static String[] countries = {"bd", "bg", "hk", "jp", "kh", "la", "mm", "my", "ph", "pk", "ro", "sg", "sk", "th", "tw"};
    final static String protocol = "https://";
    final static String hostSuffix = ".fd-api.com/";
    final static String endPoint = "api/v5/payment/methods?active=true";
    static Set<String> paymentMethodSet = new HashSet<>();
    public static void main(String[] args){
        for(int idx = 0; idx < countries.length; idx++){
            String country = countries[idx];
            JSONObject resp = RestCall(country);
            JSONObject data = (JSONObject) resp.get("data");
            JSONArray paymentMethods = (JSONArray) data.get("payment_methods");
            paymentMethods.forEach( paymentMethod -> parsePaymentMethod( (JSONObject) paymentMethod ) );
        }

        paymentMethodSet.forEach(paymentMethod -> System.out.println(paymentMethod));
    }

    public static void parsePaymentMethod(JSONObject paymentMethod){
        String code = (String) paymentMethod.get("code");
        paymentMethodSet.add(code);
    }

    public static JSONObject RestCall(String country){
        JSONObject obj = null;
        JSONParser jsonParser = new JSONParser();
        try {
            URL url = new URL(protocol + country + hostSuffix + endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;

            while ((output = br.readLine()) != null) {
                obj = (JSONObject) jsonParser.parse(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

}
