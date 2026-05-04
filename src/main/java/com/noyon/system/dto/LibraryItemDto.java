package com.noyon.system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Frontend ile haberleşmek için kullanılan DTO.
 *
 * Library.jsx'in beklediği JSON şekli:
 * {
 *   "id": 1,
 *   "title": "Clean Code",
 *   "author": "Robert C. Martin",
 *   "category": "engineering",
 *   "pages": 464,
 *   "progress": 100,
 *   "color": ["#0f1923", "#1a2b38"],   ← colorStart + colorEnd birleşimi
 *   "accent": "#0ea5e9",
 *   "spine": "#0284c7",
 *   "cover": "CC",
 *   "year": 2008,
 *   "description": "...",
 *   "tags": ["Code", "Best Practices"],
 *   "rating": 5,
 *   "isFavorite": true
 * }
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
    private int progress;
    private Integer year;
    private String description;

    /**
     * Frontend color[0] ve color[1] olarak kullanıyor.
     * [colorStart, colorEnd] şeklinde dizi gönderilir.
     */
    private List<String> color;

    private String accent;
    private String spine;
    private String cover;

    private List<String> tags;

    private int rating;

    /**
     * Jackson'ın boolean getter kuralı: field adı "favorite" olduğunda
     * getter isFavorite() üretir → JSON'da "favorite" olarak çıkar.
     * @JsonProperty ile "isFavorite" olarak zorla — frontend bunu bekliyor.
     */
    @JsonProperty("isFavorite")
    private boolean favorite;

    // ── Request DTO (POST / PUT için) ────────────────────────────────────────────

    /**
     * Gelen isteğin gövdesi için inner class.
     * color dizisi olarak da kabul edilir.
     */
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
        private int progress;
        private Integer year;
        private String description;

        /** Frontend color[0], color[1] gönderir; burada list olarak alınır. */
        @Builder.Default
        private List<String> color = new ArrayList<>();

        private String accent;
        private String spine;
        private String cover;

        @Builder.Default
        private List<String> tags = new ArrayList<>();

        private int rating;

        @JsonProperty("isFavorite")
        private boolean favorite;
    }
}
