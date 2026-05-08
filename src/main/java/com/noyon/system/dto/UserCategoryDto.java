// dto/UserCategoryDto.java
package com.noyon.system.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserCategoryDto {
    private Long id;
    private String categoryId;
    private String label;
    private String color;
}