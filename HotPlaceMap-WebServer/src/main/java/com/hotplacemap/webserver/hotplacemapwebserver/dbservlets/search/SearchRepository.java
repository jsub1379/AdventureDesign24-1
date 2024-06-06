package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.search;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Facility, Integer> {

    @Query("SELECT new com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.search.FacilityBuildingDTO(f, b.buildingId, b.buildingName, b.buildingAddress, b.latitude, b.longitude) " +
            "FROM Facility f JOIN Building b ON f.bId = b.buildingId " +
            "WHERE f.facilityName LIKE %:keyword% " +
            "OR f.facilityAddress LIKE %:keyword% " +
            "OR b.buildingName LIKE %:keyword% " +
            "OR b.buildingAddress LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN f.facilityName LIKE %:keyword% THEN 1 ELSE 0 END DESC, " +
            "CASE WHEN f.facilityAddress LIKE %:keyword% THEN 1 ELSE 0 END DESC, " +
            "CASE WHEN b.buildingName LIKE %:keyword% THEN 1 ELSE 0 END DESC, " +
            "CASE WHEN b.buildingAddress LIKE %:keyword% THEN 1 ELSE 0 END DESC")
    List<FacilityBuildingDTO> findByKeyword(@Param("keyword") String keyword);
}
