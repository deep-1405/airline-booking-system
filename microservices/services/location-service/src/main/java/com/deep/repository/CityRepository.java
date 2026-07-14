package com.deep.repository;

import com.deep.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {


    Optional<City> findByName(String name);

    Optional<City> findByCityCode(String cityCode);

    boolean existsByCityCode(String cityCode);

    boolean existsByCityCodeAndIdNot(String cityCode, Long id);

    Page<City> findByCountryCodeIgnoreCase(String countryCode, Pageable pageable);

    Page<City> findByCountryNameContainingIgnoreCase(String countryName, Pageable pageable);

    Page<City> findByRegionCodeIgnoreCase(String regionCode, Pageable pageable);

    default List<City> findByCountryCodeIgnoreCaseOrderByNameAsc(String countryCode) {
        return null;
    }

    @Query("""
    select c from City c
    where lower(c.name) like lower(concat('%', :keyWord, '%'))
    OR lower(c.cityCode) like lower(concat('%', :keyWord, '%'))
    OR lower(c.countryCode) like lower(concat('%', :keyWord, '%'))
    OR lower(c.countryName) like lower(concat('%', :keyWord, '%'))
    OR lower(c.regionCode) like lower(concat('%', :keyWord, '%'))
""")
    Page<City> searchByKeyword(String keyWord, Pageable pageable);
}
