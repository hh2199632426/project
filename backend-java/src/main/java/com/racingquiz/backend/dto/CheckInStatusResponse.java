package com.racingquiz.backend.dto;

import lombok.Data;

@Data
public class CheckInStatusResponse {
    private boolean hasCheckedIn;
    private int totalCheckinDays;
}
