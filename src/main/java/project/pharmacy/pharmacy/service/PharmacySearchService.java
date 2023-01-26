package project.pharmacy.pharmacy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.pharmacy.pharmacy.dto.PharmacyDto;
import project.pharmacy.pharmacy.entity.Pharmacy;
import project.pharmacy.pharmacy.repository.PharmacyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacySearchService {

    private final PharmacyRepository pharmacyRepository;

    public List<PharmacyDto> searchPharmacyDtoList(){

        return pharmacyRepository.findAll()
                .stream()
                .map(entity->convertToPharmacyDto(entity))
                .collect(Collectors.toList());
    }

    private PharmacyDto convertToPharmacyDto(Pharmacy pharmacy){
        return PharmacyDto.builder().id(pharmacy.getId())
                .pharmacyName(pharmacy.getPharmacyName())
                .pharmacyAddress(pharmacy.getPharmacyAddress())
                .latitude(pharmacy.getLatitude())
                .longitude(pharmacy.getLongitude())
                .build();
    }


}
