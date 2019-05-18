package com.example.brianalmanzar.myinventoryapp;

public class Product {
    private String productName;
    private long productId;
    private float productPrice;
    private int productQuantity;
    private String supplierName;
    private String supplierPhoneNumber;

    /**
     * @param productId <long> : The Id of the product. It is assigned by the database
     * @param productName <String> : Name of the product
     * @param productPrice <float>
     * @param productQuantity <int> : How many there are on stock
     * @param supplierName <String> : The name of the company that make the product
     * @param supplierPhoneNumber <String> : Supplier phone number
     */
    public Product(long productId, String productName, float productPrice, int productQuantity, String supplierName, String supplierPhoneNumber){
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.supplierName = supplierName;
        this.supplierPhoneNumber = supplierPhoneNumber;
    }

    /**
     *  Properties Getters
     **/
    public String getProductName(){
        return this.productName;
    }

    public long getProductId(){
        return this.productId;
    }

    public float getProductPrice(){
        return this.productPrice;
    }

    public int getProductQuantity(){
        return this.productQuantity;
    }

    public String getSupplierName(){
        return this.supplierName;
    }

    public String getSupplierPhoneNumber(){
        return this.supplierPhoneNumber;
    }
}
