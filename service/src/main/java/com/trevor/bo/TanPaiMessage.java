package com.trevor.bo;

import lombok.Data;

import java.util.List;

@Data
public class TanPaiMessage {
    private Long userId;

    private List<String> pokes;
}
