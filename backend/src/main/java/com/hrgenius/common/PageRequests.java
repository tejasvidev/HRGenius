package com.hrgenius.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PageRequests {
    private static final int MAX_PAGE_SIZE = 100;
    private PageRequests() { }

    public static Pageable capped(Pageable pageable) {
        if (pageable.getPageSize() <= MAX_PAGE_SIZE) return pageable;
        return PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE, pageable.getSort());
    }
}
