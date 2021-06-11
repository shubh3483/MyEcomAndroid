package com.example.models;

/**
 * Represents Cart Item
 */
public class CartItem {

    /**
     * Name of the item
     */
    public String name;

    /**
     * Unit price of the item <br>
     * Quantity of the item
     */
    public float unitPrice, qty;

    /**
     * Initialize cart item
     * @param name name of the product
     * @param unitPrice price of the product of unit quantity
     * @param qty quantity of the product
     */
    public CartItem(String name, float unitPrice, float qty) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.qty = qty;
    }

    /**
     * Calculate the cost of the item
     * @return cost of the item
     */
    public float cost() {
        return unitPrice * qty;
    }

    /**
     * overriding {@link Object#toString()} method
     *
     * @return all data in a formatted string
     */
    @Override
    public String toString() {
        return "\n\t" + name + " ( " +
                String.format("Rs. %.2f X %.2f = %.2f Rs.", unitPrice, qty, cost()) +
                " )";
    }
}
