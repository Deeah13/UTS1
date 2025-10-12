package com.bps.uts.sipakjabat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalResponseDTO<T> {
    private String status;
    private String message;
    private T data;
}