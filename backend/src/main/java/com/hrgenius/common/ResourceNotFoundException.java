package com.hrgenius.common;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) { super(resource + " was not found"); }
}
