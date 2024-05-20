package com.example.tryksave;

public class TravelInfo {
    private String distanceText;
    private String durationText;
    private String estimatedFarePrice;

    public TravelInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(TravelInfo.class)
    }

    public TravelInfo(String distanceText, String durationText, String estimatedFarePrice) {
        this.distanceText = distanceText;
        this.durationText = durationText;
        this.estimatedFarePrice = estimatedFarePrice;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getEstimatedFarePrice() {
        return estimatedFarePrice;
    }

    public void setEstimatedFarePrice(String estimatedFarePrice) {
        this.estimatedFarePrice = estimatedFarePrice;
    }
}
