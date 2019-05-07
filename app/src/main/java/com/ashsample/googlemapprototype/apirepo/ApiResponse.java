package com.ashsample.googlemapprototype.apirepo;

public interface ApiResponse {
    void onSuccess(String response);
    void onFailure();

}
