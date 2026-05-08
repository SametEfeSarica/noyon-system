package com.noyon.system.mapper;

import com.noyon.system.dto.LibraryItemDto;
import com.noyon.system.entity.LibraryItem;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Entity ↔ DTO dönüşümlerini merkezi olarak yönetir.
 * MapStruct yerine elle yazıldı; bağımlılık eklenmesini gerektirmez.
 */
@Component
public class LibraryItemMapper {

    // ── Entity → DTO ──────────────────────────────────────────────────────────

    public LibraryItemDto toDto(LibraryItem entity) {
        if (entity == null) return null;

        // color: colorStart + colorEnd → ["#...", "#..."]
        List<String> colorList = new ArrayList<>();
        colorList.add(entity.getColorStart() != null ? entity.getColorStart() : "#111111");
        colorList.add(entity.getColorEnd()   != null ? entity.getColorEnd()   : "#222222");

        return LibraryItemDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .category(entity.getCategory())
                .pages(entity.getPages())
                .progress(entity.getProgress())
                .year(entity.getYear())
                .description(entity.getDescription())
                .color(colorList)
                .accent(entity.getAccent())
                .spine(entity.getSpine())
                .cover(entity.getCover())
                .tags(entity.getTagList())
                .rating(entity.getRating())
                .favorite(entity.isFavorite())
                .build();
    }

    // ── DTO.Request → Entity (yeni kayıt) ────────────────────────────────────

    public LibraryItem toEntity(LibraryItemDto.Request dto) {
        if (dto == null) return null;

        List<String> colors = dto.getColor() != null ? dto.getColor() : new ArrayList<>();

        LibraryItem entity = new LibraryItem();
        applyRequest(entity, dto, colors);
        return entity;
    }

    // ── DTO.Request → mevcut Entity (güncelleme) ─────────────────────────────

    public void updateEntity(LibraryItem entity, LibraryItemDto.Request dto) {
        List<String> colors = dto.getColor() != null ? dto.getColor() : new ArrayList<>();
        applyRequest(entity, dto, colors);
    }

    // ── Ortak uygulama ────────────────────────────────────────────────────────

    private void applyRequest(LibraryItem entity, LibraryItemDto.Request dto, List<String> colors) {
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setCategory(dto.getCategory());
        entity.setPages(dto.getPages());
        entity.setProgress(dto.getProgress() != null ? dto.getProgress() : 0);
        entity.setYear(dto.getYear());
        entity.setDescription(dto.getDescription());
        if (dto.getAccent() != null) entity.setAccent(dto.getAccent());
        if (dto.getSpine()  != null) entity.setSpine(dto.getSpine());
        entity.setCover(dto.getCover());
        entity.setRating(dto.getRating()   != null ? dto.getRating()   : 0);
        entity.setFavorite(dto.getFavorite() != null ? dto.getFavorite() : false);

        // color[0] → colorStart, color[1] → colorEnd
        if (colors.size() > 0) entity.setColorStart(colors.get(0));
        if (colors.size() > 1) entity.setColorEnd(colors.get(1));

        // tags list → virgülle ayrılmış string
        List<String> tags = dto.getTags();
        if (tags != null && !tags.isEmpty()) {
            entity.setTags(String.join(",", tags));
        } else {
            entity.setTags("");
        }
    }
}
