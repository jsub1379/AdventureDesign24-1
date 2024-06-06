package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.search;

public class FacilityBuildingDTO {

    private Facility facility;
    private Integer buildingId;
    private String buildingName;
    private String buildingAddress;
    private Double latitude;
    private Double longitude;

    public FacilityBuildingDTO(Facility facility, Integer buildingId, String buildingName, String buildingAddress, Double latitude, Double longitude) {
        this.facility = facility;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.buildingAddress = buildingAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingAddress() {
        return buildingAddress;
    }

    public void setBuildingAddress(String buildingAddress) {
        this.buildingAddress = buildingAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
