package com.example.catalogservice.model;

public record ServiceOffering(
    String id,
    String name,
    String description,
    String type,
    boolean available
) {
}
