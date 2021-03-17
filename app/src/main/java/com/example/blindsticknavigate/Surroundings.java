package com.example.blindsticknavigate;

public class Surroundings {

    private String lng;
    private String lat;

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void getSurroundingPois(){
        POIRequest request = new POIRequest();
        request.setKey(profile.gaodeKey);
        request.setLat(this.lat);
        request.setLng(this.lng);
        request.setTypes("990000");

    }

}
