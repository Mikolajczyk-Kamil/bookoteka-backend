package com.mikolajczyk.redude.backend.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse {

    private StatusResponse message;
    private Object data;

    public ApiResponse(Object data, StatusResponse message) {
        this.data = data;
        this.message = message;
    }
}
