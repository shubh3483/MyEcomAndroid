package com.example.myecom.controllers;

/**
 * Represents listener for the callback
 */
public interface AdapterCallbacksListener {

    /**
     * When a item in the cart updated
     * @param itemPosition position of the item
     */
    void onCartUpdated();
}
