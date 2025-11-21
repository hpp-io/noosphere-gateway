package io.hpp.noosphere.gw.web.util;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for handling pagination.
 *
 * <p>
 * Pagination uses the same principles as the <a href="https://docs.github.com/en/rest/guides/using-pagination-in-the-rest-api">GitHub API</a>,
 * and follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link header)</a>.
 * </p>
 */
@Component
public final class PaginationUtil {

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_LINK_FORMAT = "<{0}>; rel=\"{1}\"";

    private PaginationUtil() {}

    public static <T> HttpHeaders generatePaginationHttpHeaders(URI uri, List<T> list) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, Long.toString(list.size()));
        headers.add(HttpHeaders.LINK, UriComponentsBuilder.fromUri(uri).build().toString());
        return headers;
    }
}