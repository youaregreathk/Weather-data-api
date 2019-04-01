package com.lc.spring.model.response;

import lombok.Getter;
import lombok.Setter;

public class WeatherDataResponse<T> {
    @Getter
    @Setter
    private T content;

    public WeatherDataResponse() {
    }

    /**
     * Build the response with content.
     * @param content the content
     */
    public WeatherDataResponse(T content) {
        this.setContent(content);
    }

}
