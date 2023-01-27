package project.pharmacy.pharmacy.service;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import project.pharmacy.api.dto.DocumentDto;
import project.pharmacy.api.dto.KakaoApiResponseDto;
import project.pharmacy.api.service.KakaoAddressSearchService;
import project.pharmacy.direction.dto.OutputDto;
import project.pharmacy.direction.entity.Direction;
import project.pharmacy.direction.service.DirectionService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class PharmacyRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    public List<OutputDto> recommendationPharmacyList(String address) {

        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) {
            log.error("[PharmacyRecommendationService.recommendPharmacyList fail] Input address: {}", address);
            return Collections.emptyList();
        }


        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);
        //List<Direction> directionList = directionService.buildDirectionList(documentDto);
        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);

        System.out.println("ddddddddddddddddddddddddd  " +directionList.get(0).getTargetPharmacyName());

        return directionService.saveAll(directionList)
                .stream().map(t ->convertToOutputDto(t))
                .collect(Collectors.toList());

    }

    private OutputDto convertToOutputDto(Direction direction) {
        return OutputDto.builder().
                pharmacyAddress(direction.getTargetAddress())
                .pharmacyName(direction.getTargetPharmacyName())
                .directionUrl("todo")
                .roadViewUrl("todo")
                .distance(String.format("%.2f km",direction.getDistance()))
                .build();
    }
}
