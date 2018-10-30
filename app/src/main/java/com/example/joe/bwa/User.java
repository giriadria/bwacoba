package com.example.joe.bwa;



public class User {

    public String url;
    public String namauser;
    private String phone;


    public User() {
    }

    public User(String namauser,  String url) {

        this.url= getLargeImageUrl(url);



        this.namauser = namauser;


    }


    public void setNamaUser(String namauser) {
        this.namauser = namauser;
    }





    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }








    public String getUrl() { return url;
    }
    public String getLargeImageUrl(String url) {
        String largeImageUrl = url.substring(0, url.length() - 6).concat("o.jpg");
        return largeImageUrl;}


    public  String getNamaUser(){
        return namauser;

    }



    }

