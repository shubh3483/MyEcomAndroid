package com.example.myecom.tmp;

import android.net.Uri;

import com.example.models.Product;
import com.example.models.Variant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents helper class to fetch products from web
 */
public class ProductsHelper {

    /**
     * To get the list of the products
     * @return list of the products
     */
    public static List<Product> getProducts(){

        List<Product> products = new ArrayList<>();

        // Adding some products
        products.add(new Product("Apple", Uri.parse("android.resource://com.example.myecom/drawable/apple").toString(), 1, 80));
        products.add(new Product("Kiwi", Uri.parse("android.resource://com.example.myecom/drawable/kiwi").toString(), new ArrayList<>(Arrays.asList(
                        new Variant("500g", 90),
                        new Variant("1kg", 150)
                ))));
        products.add(new Product("Banana", Uri.parse("android.resource://com.example.myecom/drawable/banana").toString(),2,30));
        products.add(new Product("Surf Excel", Uri.parse("android.resource://com.example.myecom/drawable/surf_excel").toString(), new ArrayList<>(Arrays.asList(
                        new Variant("1kg", 95),
                        new Variant("2kg", 180),
                        new Variant("5kg", 400)
                ))));
        products.add(new Product("Milk", Uri.parse("android.resource://com.example.myecom/drawable/milk").toString(), new ArrayList<>(Collections.singletonList(
                        new Variant("1kg", 50)
                ))));
        products.add(new Product("Watermelon", Uri.parse("android.resource://com.example.myecom/drawable/watermelon").toString(), 1, 20));
        products.add(new Product("Toothpaste", Uri.parse("android.resource://com.example.myecom/drawable/toothpaste").toString(), new ArrayList<>(Arrays.asList(
                new Variant("100g", 65),
                new Variant("200kg", 120)
        ))));
        products.add(new Product("Potato", Uri.parse("android.resource://com.example.myecom/drawable/potato").toString(), 2.5f, 30));
        products.add(new Product("Ketchup", Uri.parse("android.resource://com.example.myecom/drawable/ketchup").toString(), new ArrayList<>(Arrays.asList(
                new Variant("500g", 110),
                new Variant("1kg", 100)
        ))));
        products.add(new Product("Tomato", Uri.parse("android.resource://com.example.myecom/drawable/tomato").toString(), 1.5f, 40));
        products.add(new Product("Almonds", Uri.parse("android.resource://com.example.myecom/drawable/almonds").toString(), new ArrayList<>(Arrays.asList(
                new Variant("500g", 550),
                new Variant("1kg", 1000)
        ))));
        products.add(new Product("Carrot", Uri.parse("android.resource://com.example.myecom/drawable/carrot").toString(), 1, 35));
        products.add(new Product("Cashew Nuts", Uri.parse("android.resource://com.example.myecom/drawable/cashewnuts").toString(), new ArrayList<>(Arrays.asList(
                new Variant("500g", 400),
                new Variant("1kg", 700),
                new Variant("5kg", 3000)
        ))));
        products.add(new Product("Okra", Uri.parse("android.resource://com.example.myecom/drawable/okra").toString(), 0.5f, 50));
        products.add(new Product("Coriander", Uri.parse("android.resource://com.example.myecom/drawable/coriander").toString(), new ArrayList<>(Collections.singletonList(
                new Variant("100g", 10)
        ))));

        return products;
    }
}
