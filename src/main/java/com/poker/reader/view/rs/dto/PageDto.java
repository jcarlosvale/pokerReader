package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDto {
    private int totalPages;

    private long currentPageId;
    private long currentPageNumber;

    private long nextPageId;
    private long nextPageNumber;

    private long previousPageId;
    private long previousPageNumber;
}
