package com.fsb.demo.web.service;

import java.util.ArrayList;

public class RequestExtractorField {
    public static ArrayList<String[]> get(Object request) throws Exception{
        String r = request.toString();
        ArrayList<String[]> res = new ArrayList<>();

        r = r.replaceAll("\\{", "");
        r = r.replaceAll("\\}", "");
        r = r.replaceAll("\"", "");
        r = r.replaceAll("\'", "");

        for(String i : r.split(",")){
            if(i.contains("=")){
                res.add(i.split("="));
            }else if(i.contains(":")){
                res.add(i.split(":"));
            }
        }
        return res;
    }
}
