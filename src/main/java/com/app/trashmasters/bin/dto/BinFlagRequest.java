package com.app.trashmasters.bin.dto;

import lombok.Data;

@Data
public class BinFlagRequest {
    private boolean isFlagged;
    private String issue;
}