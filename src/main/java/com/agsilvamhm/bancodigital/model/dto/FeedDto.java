package com.agsilvamhm.bancodigital.model.dto;

import java.util.List;

public record FreedDto(List<FeedItemDto> feedItens,
                       int page, int pageSize, int totalPages,
                       int totalElements) {
}
