package com.daelim.guildbackend.dto.response;

import lombok.Data;

@Data
public class Response<E> {
    private E data;
    private Error error;
}
