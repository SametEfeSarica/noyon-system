package com.noyon.system.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Frontend ile haberleşmek için kullanılan DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryItemDto {

    private Long id;

    private String title;
    private String author;
    private String category;
    private Integer pages;

    // DÜZELTME 1: int yerine Integer yapıldı (Null çökmesini engeller)
    private Integer progress;

    private Integer year;
    private String description;

    private List<String> color;
    private String accent;
    private String spine;
    private String cover;
    private List<String> tags;

    // DÜZELTME 2: int yerine Integer yapıldı
    private Integer rating;

    // DÜZELTME 3: boolean yerine Boolean yapıldı
    @JsonProperty("isFavorite")
    private Boolean favorite;

    // ── Request DTO (POST / PUT için) ────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String title;
        private String author;
        private String category;
        private Integer pages;

        // DÜZELTME 4: int yerine Integer
        private Integer progress;

        private Integer year;
        private String description;

        @Builder.Default
        private List<String> color = new ArrayList<>();

        private String accent;
        private String spine;
        private String cover;

        @Builder.Default
        private List<String> tags = new ArrayList<>();

        // DÜZELTME 5: int yerine Integer
        private Integer rating;

        // DÜZELTME 6: boolean yerine Boolean
        @JsonAlias({"favorite", "isFavorite"})
        private Boolean favorite;
    }
    
}