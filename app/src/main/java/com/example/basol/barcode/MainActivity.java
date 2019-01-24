package com.example.basol.barcode;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnClickListener {


    private ArrayList<String> genel;
    private ArrayList<String> products;
    private final DBHelper db = new DBHelper(this);

    private Button scanBtn, btnSearch, addToDB, btnHistory;
    private TextView formatTxt, contentTxt;
    private Spinner itemSpinner;
    private ArrayAdapter<String> adapter;
    private int select = 0;
    private TextView countryCode, productName, category, url, dimensions, shopName, price, currency;
    private String barcode = "886798420486";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setGUI();

        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                select = itemSpinner.getSelectedItemPosition();
                Log.e(String.valueOf(select), "spinner sayısı");
                setInformation(genel, products, select);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    
    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
        if(v.getId()==R.id.btnSearch){
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            Log.e("barkod is : ", barcode);
            new HTTPAsyncTask().execute("https://api.priceapi.com/products/single?token=awstoken&country=us&source=amazon&currentness=daily_updated&completeness=one_page&key=gtin&value=" + barcode);
        }
        if(v.getId() == R.id.addToDB){
            Item item = new Item();
            item.setId(1);
            item.setProductName(genel.get(2));
            item.setCountryCode(genel.get(1));
            item.setCategory(genel.get(3));
            item.setUrl(genel.get(4));
            item.setDimensions(genel.get(5));
            item.setShopName(products.get(0 + select));
            item.setPrice(products.get(1 + select));
            item.setCurrency(products.get(4 + select ));
            db.addItem(item);
            Toast.makeText(this, "Item added to DB", Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == R.id.btnHistory){
            Intent intent = new Intent(this, DBActivity.class);
            startActivity(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            barcode = scanContent;
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);
            checkNetworkConnection();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String>  {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpGet(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute (String result) {
            Log.e("first" , result);
            try {
                JSONParse(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String HttpGet(String urlString) throws IOException, JSONException{
        InputStream is = null;
        String result = "";
        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        is = conn.getInputStream();

        if(is != null) {
            result = convertInputStreamToString(is);
        }
        else {
            result = "Did not work!";
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(is));
        String line = "";
        String result = "";
        while((line = br.readLine()) != null) {
            result += line;
        }
        is.close();
        return result;
    }

    private void setGUI(){

        itemSpinner = (Spinner) findViewById(R.id.itemSpinner);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        scanBtn = (Button)findViewById(R.id.scan_button);
        addToDB = (Button) findViewById(R.id.addToDB);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);

        scanBtn.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        addToDB.setOnClickListener(this);
        btnHistory.setOnClickListener(this);

        countryCode = (TextView) findViewById(R.id.countryCode);
        productName = (TextView) findViewById(R.id.productName);
        category = (TextView) findViewById(R.id.category);
        url = (TextView) findViewById(R.id.Url);
        dimensions = (TextView) findViewById(R.id.dimensions);
        shopName = (TextView) findViewById(R.id.shopName);
        price = (TextView) findViewById(R.id.price);
        currency = (TextView) findViewById(R.id.currecy);

    }

    private void setInformation(ArrayList<String> info, ArrayList<String> shops, int selection){

        select = select * 6;

        countryCode.setText("Country Code: " + info.get(1));
        productName.setText("Product Name: " + info.get(2));
        category.setText("Category: " + info.get(3));
        url.setText("Url: " + info.get(4));
        Linkify.addLinks(url, Linkify.WEB_URLS);
        dimensions.setText("Dimesions: " + info.get(5));
        shopName.setText("Shop Name: " + shops.get(0 + select));
        price.setText("Price: " + shops.get(1 + select));
        currency.setText("Currency: " + shops.get(4 + select));


        Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
    }

    private void JSONParse(String json) throws JSONException {

        JSONObject productsObject = new JSONObject(json);
        JSONArray conditionArray = productsObject.getJSONArray("products");
        genel = new ArrayList<String>();
        products = new ArrayList<String>();

        for (int i = 0; i < conditionArray.length(); i++) {
            JSONObject objForCondition = conditionArray.getJSONObject(i);

            if (!objForCondition.has("name")){
                Toast.makeText(this, "Product is not found", Toast.LENGTH_SHORT).show();
                return;
            }
            if (objForCondition.getString("source") != null) {
                genel.add(objForCondition.getString("source"));
            }
            if (objForCondition.getString("country") != null) {
                genel.add(objForCondition.getString("country"));
            }
            if (objForCondition.getString("name") != null) {
                genel.add(objForCondition.getString("name")); // public
            }
            if (objForCondition.getString("category_name") != null) {
                genel.add(objForCondition.getString("category_name")); // public
            }
            if (objForCondition.getString("url") != null) {
                genel.add(objForCondition.getString("url"));

            }
            if (objForCondition.getString("dimensions") != null) {
                genel.add(objForCondition.getString("dimensions"));
            }

            JSONArray conditionArray1 = objForCondition.getJSONArray("offers");
            int a = 6;
            for (int j = 0; j < conditionArray1.length(); j++) {
                JSONObject objForCondition1 = conditionArray1.getJSONObject(j);
                //shop_name = objForCondition1.getString("shop_name");
                //detailsString = objForCondition1.getString("details");

                if (objForCondition1.getString("shop_name") != null) {
                    products.add(objForCondition1.getString("shop_name")); // public
                }
                if (objForCondition1.getString("price") != null) {
                    products.add(objForCondition1.getString("price"));
                }
                if (objForCondition1.getString("price_with_shipping") != null) {
                    products.add(objForCondition1.getString("price_with_shipping"));
                }
                if (objForCondition1.getString("shipping_costs") != null) {
                    products.add(objForCondition1.getString("shipping_costs"));
                }
                if (objForCondition1.getString("currency") != null) {
                    products.add(objForCondition1.getString("currency"));
                }
                if (objForCondition1.getString("details") != null) {
                    products.add(objForCondition1.getString("details"));
                }

            }
        }

        ArrayList<String> array = new ArrayList<String>();

        for (int i = 0; i< products.size(); i++){
            if (i % 6 ==0 && i + 6 < products.size())
                array.add(products.get(i) + ", " + products.get(i + 1) + " " + products.get(i + 4));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);

        setInformation(genel, products, select);
    }



    public boolean checkNetworkConnection(){

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean isConnected = false;

        if(networkInfo != null && (isConnected = networkInfo.isConnected())){

            btnSearch.setTextColor(Color.GREEN);
        }
        else {
            btnSearch.setTextColor(Color.RED);
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();

        }
        return isConnected;
    }


}