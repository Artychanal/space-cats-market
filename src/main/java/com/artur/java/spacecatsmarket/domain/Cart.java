package com.artur.java.spacecatsmarket.domain;


import lombok.*;
import java.util.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class Cart {
    private String id; // session/user id
    private List<Item> items = new ArrayList<>();
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Item {
        private java.util.UUID productId;
        private int qty;
    }
}