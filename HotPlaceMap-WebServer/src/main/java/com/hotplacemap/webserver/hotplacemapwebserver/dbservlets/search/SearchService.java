package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    public List<FacilityBuildingDTO> searchFacilities(String keyword) {
        return searchRepository.findByKeyword(keyword);
    }
}
