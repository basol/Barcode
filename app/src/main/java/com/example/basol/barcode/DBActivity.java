package com.example.basol.barcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class DBActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView countryCode, productName, category, url, dimensions, shopName, price, currency;
    private Spinner itemSpinner;
    private Button btnBack, btnShow, btnDelete;
    private int select = 0;
    ArrayList<Item> itemList;
    DBHelper db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        db = new DBHelper(this);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnShow = (Button) findViewById(R.id.btnShow);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnBack.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        itemSpinner = (Spinner) findViewById(R.id.itemSpinner);
        countryCode = (TextView) findViewById(R.id.countryCode);
        productName = (TextView) findViewById(R.id.productName);
        category = (TextView) findViewById(R.id.category);
        url = (TextView) findViewById(R.id.Url);
        dimensions = (TextView) findViewById(R.id.dimensions);
        shopName = (TextView) findViewById(R.id.shopName);
        price = (TextView) findViewById(R.id.price);
        currency = (TextView) findViewById(R.id.currecy);

        itemList = db.getAllShops();
        ArrayList<String> array = new ArrayList<String>();

        for(int i = 0; i< itemList.size(); i++){

            array.add(itemList.get(i).getProductName() + ", " +itemList.get(i).getShopName() + ", " + itemList.get(i).getPrice() + " " + itemList.get(i).getCurrency());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnBack){
            finish();
        }
        if(view.getId() == R.id.btnShow){
            select = itemSpinner.getSelectedItemPosition();

            countryCode.setText("Country Code: " + itemList.get(select).getCountryCode());
            productName.setText("Product Name: " + itemList.get(select).getProductName());
            category.setText("Category: " + itemList.get(select).getCategory());
            url.setText("Url: " + itemList.get(select).getUrl());
            Linkify.addLinks(url, Linkify.WEB_URLS);
            dimensions.setText("Dimesions: " + itemList.get(select).getDimensions());
            shopName.setText("Shop Name: " + itemList.get(select).getShopName());
            price.setText("Price: " + itemList.get(select).getPrice());
            currency.setText("Currency: " + itemList.get(select).getCurrency());
        }
        if(view.getId() == R.id.btnDelete){
            db.deleteShop(itemList.get(select));
        }

    }
}
