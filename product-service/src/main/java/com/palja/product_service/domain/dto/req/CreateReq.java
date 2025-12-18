package com.palja.product_service.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateReq {

    private String name;
    private String description;
    private Long price;
    private Long stock;
    private String category;
}
