package com.example.joe.bwa;


public class Upload {

    private User namauser;
    private int rating;
    private String eventId;
    public String lokasi;
    public String url;
    public String tentang;
    public String jam;
    public String tanggal;
    public String nama;
    private String pushId;
    private String phone;
    private String postTime;


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPostTime() {
        return postTime;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Upload() {
    }
    // Default constructor required for calls to
    // DataSnapshot.getValue(com.example.joe.bwa.Splash.User.class)

    public Upload(User namauser, String lokasi, String tentang, String tanggal, String jam, String nama, String url) {
        this.lokasi = lokasi;
        this.url = getLargeImageUrl(url);
        this.tentang = tentang;
        this.jam = jam;
        this.tanggal = tanggal;
        this.nama = nama;
        this.namauser = namauser;


    }
    public User getNamaUser() {

        return namauser;

    }
    public void setNamaUser(User namauser) {
        this.namauser = namauser;
    }
    public String getLokasi() {
        return lokasi;

    }

    public String getUrl() {
        return url;
    }

    public String getLargeImageUrl(String url) {
        String largeImageUrl = url.substring(0, url.length() - 6).concat("o.jpg");
        return largeImageUrl;
    }

    public String getTentang() {
        return tentang;
    }

    public String getJam() {
        return jam;
    }


    public String getTanggal() {
        return tanggal;
    }

    public String getNama() {
        return nama;

    }


    public String getPushId() {
        return pushId;
    }


    public void setPushId(String pushId) {
        this.pushId = pushId;
    }



}