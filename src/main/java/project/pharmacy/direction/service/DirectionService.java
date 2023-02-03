package project.pharmacy.direction.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import project.pharmacy.api.dto.DocumentDto;
import project.pharmacy.api.service.KakaoCategorySearchService;
import project.pharmacy.direction.entity.Direction;
import project.pharmacy.direction.repository.DirectionRepository;
import project.pharmacy.pharmacy.dto.PharmacyDto;
import project.pharmacy.pharmacy.service.PharmacySearchService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectionService {

    private static final int MAX_SEARCH_COUNT = 3; // 약국 최대 검색 갯수
    private static final double RADIUS_KM = 10.0;  // 반경 1Okm
    private final PharmacySearchService pharmacySearchService;

    private final DirectionRepository directionRepository;

    private final KakaoCategorySearchService kakaoCategorySearchService;

    private final Base62Service base62Service;




    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if (CollectionUtils.isEmpty(directionList)) return Collections.emptyList();
        return directionRepository.saveAll(directionList);
    }

    public Direction findById(String encodedId) {
        Long decodedId = base62Service.decodeDirectionId(encodedId);
        return directionRepository.findById(decodedId).orElse(null);
    }

    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        if (Objects.isNull(documentDto)) return Collections.emptyList();
        // 약국 데이터 조회
        return pharmacySearchService.searchPharmacyDtoList()
                .stream().map(pharmacyDto -> Direction.builder()
                        .inputAddress(documentDto.getAddressName())
                        .inputLatitude(documentDto.getLatitude())
                        .inputLongitude(documentDto.getLongitude())
                        .targetAddress(pharmacyDto.getPharmacyAddress())
                        .targetPharmacyName(pharmacyDto.getPharmacyName())
                        .targetLatitude(pharmacyDto.getLatitude())
                        .targetLongitude(pharmacyDto.getLatitude())
                        .distance(calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                pharmacyDto.getLatitude(), pharmacyDto.getLongitude()))
                        .build())
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(Direction::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());

        // 거리계산 알고리즘을 이용하여, 고객과 약국 사이읭 거리를 계산 후 정렬
    }

    // 카테고리 검색 카카오 api 사용하여 검색
    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto) {
        if (Objects.isNull(inputDocumentDto)) return Collections.emptyList();

        return kakaoCategorySearchService
                .requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
                .getDocumentList()
                .stream().map(resultDocumentDto ->
                        Direction.builder()
                                .inputAddress(inputDocumentDto.getAddressName())
                                .inputLatitude(inputDocumentDto.getLatitude())
                                .inputLongitude(inputDocumentDto.getLongitude())
                                .targetPharmacyName(resultDocumentDto.getPlaceName())
                                .targetAddress(resultDocumentDto.getAddressName())
                                .targetLatitude(resultDocumentDto.getLatitude())
                                .targetLongitude(resultDocumentDto.getLongitude())
                                .distance(resultDocumentDto.getDistance() * 0.001) // km 단위
                                .build())
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    // Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
