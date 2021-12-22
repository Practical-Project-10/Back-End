package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;

    @Transactional
    public void createCarpool(String skiResort, CarpoolDto.RequestDto requestDto, User user) {
        Carpool carpool = new Carpool(user, requestDto, skiResort);
        carpoolRepository.save(carpool);
    }

    @Transactional
    public void updateCarpool(Long carpoolId, CarpoolDto.RequestDto requestDto) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );
        carpool.update(requestDto);
    }

    @Transactional
    public void deleteCarpool(Long carpoolId) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );
        carpoolRepository.deleteById(carpoolId);
    }

    
    //region 카풀 카테고리 분류
    @Transactional
    public ResponseEntity<List<CarpoolDto.CategoryResponseDto>> sortCarpools(
            CarpoolDto.CategoryRequestDto categoryRequestDto
    ) {
        List<Carpool> sortedCategories =
                carpoolRepository.findAllByCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateAndMemberNumIsLessThanEqual
        (
                categoryRequestDto.getCarpoolType(), //빈 값은 "" 으로
                categoryRequestDto.getStartLocation(), //빈 값은 "" 으로
                categoryRequestDto.getEndLocation(), //빈 값은 "" 으로
                categoryRequestDto.getDate(), //빈 값은 "" 으로
                categoryRequestDto.getMaxMemberNum()// 빈 값은 숫자 맥스로
        );
        List<CarpoolDto.CategoryResponseDto> categoryResponseDto = sortedCategories.stream()
                .map(Carpool::toCatogoryResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(categoryResponseDto);
    }
    //endregion
}
