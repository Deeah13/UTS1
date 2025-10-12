package com.bps.uts.sipakjabat.dto;

import lombok.Data;
import java.util.Set;

@Data
public class LampirkanDokumenRequest {
    private Set<Long> dokumenIds;
}