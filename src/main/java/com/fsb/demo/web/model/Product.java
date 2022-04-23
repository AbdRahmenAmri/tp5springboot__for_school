package com.fsb.demo.web.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.fsb.demo.web.service.Validators;

public class Product {
    private long id;
    private String short_name;
    private String long_name;
    private double price;
    private String img;
    public Product(Long id, String short_name, String long_name, double price, String img) throws Exception {
        this.id = id;
        this.short_name = short_name;
        this.long_name = long_name;
        this.price = price;
        this.img = img;
    }

    public HashMap<Object,Object> toJSON(){
        HashMap<Object,Object> JSON = new HashMap<Object,Object>();
        JSON.put("id", getId());
        JSON.put("longName", getLong_name());
        JSON.put("shortName", getShort_name());
        JSON.put("price", getPrice());
        JSON.put("image", getImg());

        return JSON;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getShort_name() {
        return short_name;
    }
    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }
    public String getLong_name() {
        return long_name;
    }
    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }


    public void setByFields(Validators validators) throws Exception{
        ArrayList<String> fields = validators.getFields();
        for(String field : fields){
            switch (field) {
                case "short_name":
                    this.setShort_name(validators.getField(field));
                    break;
                case "long_name":
                    this.setLong_name(validators.getField(field));
                    break;
                case "price":
                    this.setPrice(validators.getDouble(field));
                    break;
                case "img":
                    this.setImg(validators.getField(field));
                    break;
                default:
                    throw new Exception();
            }
        }
    }

    @Override
    public String toString() {
        return "{id:" + id + ", img:" + img + ", long_name:" + long_name + ", price:" + price + ", short_name:"
                + short_name + "}";
    }
}
