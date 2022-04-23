package com.fsb.demo.web.service;

import java.util.ArrayList;
import java.util.Arrays;

public class Validators {
    private ArrayList<String[]> data;
    private static final ArrayList<String> allowed_img = new ArrayList<>(Arrays.asList(".png",".jpg",".jpeg",".webp"));

    public Validators(ArrayList<String[]> data) {
        this.data = data;
    }

    public Boolean isValideImage(){
        String img = this.getField("img");
        String img2 = img;
        img2.replace(".", "");
        if(img2.indexOf(".")!= -1 ) return false;
        for(String str : allowed_img){
            if(img.endsWith(str)) return true;
        }
        return false;
    }

    public void addField(String[] field){
        this.data.add(field);
    }
    public String getField(String field){
        try {
            for(String[] i : data) if(i[0].equals(field)) return i[1];
            return null;
        } catch (Exception e) {
            return null;

        }
    }

    public ArrayList<String> getFields(){
        ArrayList<String> fields = new ArrayList<>();
        for(String[] i : this.data) fields.add(i[0]);
        return fields;
    }

    public Boolean require(String field){
        for(String i : this.getFields()){
            if(field.equals(i) && this.getField(field) != null) return true;
        }
        return false;
    }

    public Boolean requireAll(ArrayList<String> fields){
        for(String s : fields){
            if(!this.require(s)) return false;
        }
        return true;
    }

    public Character getChar(String field){
        Character c;
        try {
            c = this.getField(field).charAt(0);
        } catch (Exception e) {
            return null;
        }
        return c;
    }

    public Integer getInt(String field){
        Integer c;
        try {
            c = Integer.parseInt(this.getField(field));
        } catch (Exception e) {
            return null;
        }
        return c;
    }

    public Double getDouble(String field){
        Double c;
        try {
            c = Double.parseDouble(this.getField(field));
        } catch (Exception e) {
            return null;
        }
        return c;
    }

}
