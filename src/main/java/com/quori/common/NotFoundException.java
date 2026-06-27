package com.quori.common;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String entity, UUID id) {
        super(entity + " not found: " + id);
    }
}
