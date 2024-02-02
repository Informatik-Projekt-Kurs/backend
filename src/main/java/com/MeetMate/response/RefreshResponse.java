package com.MeetMate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RefreshResponse {

    String access_Token;
    long expires_at;
}
