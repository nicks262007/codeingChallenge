package com.template.webserver;

public class ProductInfo {

    private String productName;
    private String productColor;
    private String status;
    private String otherParty;

    public ProductInfo() {
    }

    public String getProductName(){
        return productName;
    }

    public String getProductColor(){
        return productColor;
    }

    public String getStatus(){
        return status;
    }

    public String getOtherParty(){
        return otherParty;
    }
    public void setProductName(String productName){
        this.productName=productName;
    }


    public void setProductColor(String productColor){
        this.productColor=productColor;
    }

    public void setStatus(String status){
        this.status=status;
    }

    public void setOtherParty(String otherParty){
        this.otherParty=otherParty;
    }
}
