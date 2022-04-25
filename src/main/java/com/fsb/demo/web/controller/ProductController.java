package com.fsb.demo.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.fsb.demo.web.model.Product;
import com.fsb.demo.web.service.RequestExtractorField;
import com.fsb.demo.web.service.Validators;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProductController {


    private final String DIR_UPLOAD = Paths.get((Paths.get(this.getClass().getResource("/").getPath())).toAbsolutePath() + "/static/img") + "/";
    

    private static List<Product> products = new ArrayList<Product>();
    private static Long idCount = 0L;
    static {
        try {
            products.add(new Product(++idCount, "SS-S9", "Samsung Galaxy S9", 1000D, "samsung-s9.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            products.add(new Product(++idCount, "NK-5P", "Nokia 5.1 Plus", 600D, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            products.add(new Product(++idCount, "IP-7", "iPhone 7", 1500D, "iphone-7.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer indexOfProduct(Long id) {
        Integer index = 0;
        for (Product product : products) {
            if (product.getId() == id) return index;
            ++index;
        }
        return null;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/")
    public ResponseEntity<Object> index() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("info", "AbdRahmen is my name");
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/api/product")
    public ResponseEntity<Object> getProducts() {
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();
        Integer i = 1;
        for (Product product : products) {
            data.put(i.toString(), product.toJSON());
            i++;
        }
        response.put("data", data);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/api/product/{id}")
    public ResponseEntity<Object> getProduct(
        @PathVariable("id") Long id
            ) {
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();
        if(products.get(indexOfProduct(id)) instanceof Product){
            data.put(Long.toString(products.get(indexOfProduct(id)).getId()), products.get(indexOfProduct(id)).toJSON());
            response.put("data", data);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        response.put("error", "product not found");
        return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/api/product")
    public ResponseEntity<Object> postProduct(
        @RequestParam("data") Object request,
        @RequestParam("file") Optional<MultipartFile> file
        ) throws IOException {
        HashMap<String, Object> response = new HashMap<>();
        Path path = null;
        byte[] bytes = null;
        ArrayList<String[]> x = new ArrayList<>();
        ArrayList<String> required_fields = new ArrayList<>(Arrays.asList("short_name", "long_name", "price"));
        try {
            x = RequestExtractorField.get(request);
        } catch (Exception e) {
            response.put("error", "invalid request");
            return new ResponseEntity<Object>(response,HttpStatus.BAD_REQUEST);
        }
        Validators validators = new Validators(x);
        if (!file.isEmpty()) {
            path = Paths.get(DIR_UPLOAD + file.get().getOriginalFilename());
            bytes = file.get().getBytes();
            String[] field = { "img", file.get().getOriginalFilename() };
            validators.addField(field);
            if(!validators.isValideImage()){
                response.put("error", "invalid image file");
                return new ResponseEntity<Object>(response,HttpStatus.NOT_ACCEPTABLE);
            }
        }
        if (!validators.requireAll(required_fields)) {
            response.put("error", "invalid request");
            return new ResponseEntity<Object>(response,HttpStatus.BAD_REQUEST);
        }
        try {
            products.add(new Product(++idCount, validators.getField("short_name"), validators.getField("long_name"), validators.getDouble("price"), validators.require("img") ? validators.getField("img") : null));
        } catch (Exception e) {
            response.put("error", "cannot create this product -- try later");
            return new ResponseEntity<Object>(response, HttpStatus.NOT_ACCEPTABLE);
        }

        if(!file.isEmpty()) Files.write(path, bytes);
        response.put("succes", "product created");
        return new ResponseEntity<Object>(response,HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping(value = "api/product/{id}")
    public ResponseEntity<Object> putProduct(
            @RequestParam("data") Object request,
            @RequestParam("file") Optional<MultipartFile> file,
            @PathVariable("id") Long id)
            throws IOException {
        HashMap<String, Object> response = new HashMap<>();
        Path path = null;
        byte[] bytes = null;

        ArrayList<String[]> x = new ArrayList<>();
        try {
            x = RequestExtractorField.get(request);
        } catch (Exception e) {
            response.put("error", "invalid request");
            return new ResponseEntity<Object>(response,HttpStatus.BAD_REQUEST);
        }
        Validators validators = new Validators(x);
        if (!file.isEmpty()) {
            path = Paths.get(DIR_UPLOAD + file.get().getOriginalFilename());
            bytes = file.get().getBytes();
            String[] field = { "img", file.get().getOriginalFilename() };
            validators.addField(field);
        }

        if (!validators.requireAll(validators.getFields())){
            response.put("error", "invalid request");
            return new ResponseEntity<Object>(response,HttpStatus.BAD_REQUEST);
        }

        for (Product product : products) {
            if (product.getId() == id) {
                try {
                    product.setByFields(validators);
                } catch (Exception e) {
                    response.put("error", "invalid field");
                    return new ResponseEntity<Object>(response,HttpStatus.NOT_ACCEPTABLE);
                }
                if (!file.isEmpty()) {
                    Files.write(path, bytes);
                }
                response.put("succes", "product updated");
                return new ResponseEntity<Object>(response,HttpStatus.OK);
            }
        }
        response.put("error", "product not found");
        return new ResponseEntity<Object>(response,HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/api/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable("id") Long id) {
        HashMap<String, Object> response = new HashMap<>();
        if (indexOfProduct(id) != null) {
            if(products.get(indexOfProduct(id).intValue()).getImg() != null){
                String fileName = products.get(Integer.parseInt(id.toString())).getImg();
                File image = new File(DIR_UPLOAD + fileName);
                image.delete();

            }
            products.remove(indexOfProduct(id).intValue());
            response.put("succes", "product deleted");
            return new ResponseEntity<Object>(response,HttpStatus.OK);
        } else{
            response.put("error", "product not found");
            return new ResponseEntity<Object>(response,HttpStatus.BAD_REQUEST);
        }
    }
}
