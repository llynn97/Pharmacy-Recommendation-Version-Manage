package project.pharmacy.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KAKAO_LOCAL_CATEGORY_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    public URI buildUriByAddressSearch(String address) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build().encode().toUri();
        log.info("[KakaoAddressSearchService buildUriByAddressSearch] address: {}, uri: {}", address, uri);

        return uri;
    }

    public URI buildUriByCategorySearch(double latitude, double longitude, double radius, String category) {

        double metaRadius = radius * 1000;   // 반경은 km 단위로

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_ADDRESS_URL);
        uriBuilder.queryParam("category_group_code", category);
        uriBuilder.queryParam("x", longitude);
        uriBuilder.queryParam("y", latitude);
        uriBuilder.queryParam("radius", radius);
        uriBuilder.queryParam("sort", "distance");

        URI uri = uriBuilder.build().encode().toUri();
        log.info("[KakaoAddressSearchService buildUriByCategorySearch] uri: {}",  uri);

        return uri;
    }
}
