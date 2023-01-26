package project.pharmacy.direction.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.pharmacy.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="direction")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
public class Direction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자
    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    //약국
    private String targetPharmacyName;
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    //사용자 주소와 약국 사이의 거리
    private double distance;

}
