package com.example.bookingservice.model;

public class User {
    private Long id;
    private String name;
    private boolean maintenanceFeePaid;
    
    public User(Long id, String name, boolean maintenanceFeePaid) {
        this.id = id;
        this.name = name;
        this.maintenanceFeePaid = maintenanceFeePaid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMaintenanceFeePaid() {
        return maintenanceFeePaid;
    }

    public void setMaintenanceFeePaid(boolean maintenanceFeePaid) {
        this.maintenanceFeePaid = maintenanceFeePaid;
    }
}
