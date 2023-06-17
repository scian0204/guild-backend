package com.daelim.guildbackend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Response<E> {
    public E data;
    public Error error;
}
