package com.example.catalogservice.model;

public class ServiceOffering {
    private String id;
    private String name;
    private String description;
    private String type; // "AMENITY" or "REPAIR"
    private boolean available;
    
    public ServiceOffering() {
    }
    
    public ServiceOffering(String id, String name, String description, String type, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
