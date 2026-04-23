package com.app.trashmasters.bin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BinFlagRequest {
    @Schema(example = "true")
    private boolean isFlagged;
    @Schema(example = "Lid broken, needs replacement")
    private String issue;
}