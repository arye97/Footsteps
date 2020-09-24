package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.repository.ActivityRepositoryCustomImpl.SearchResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ActivityRepositoryCustom {
    SearchResponse findAllByKeywordUsingMethod(@Param("keywords") List<String> keywords, int pageSize, int page);
}
