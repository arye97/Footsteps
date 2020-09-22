package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Activity> findAllByKeywordUsingMethod(@Param("keywords") String keywords, String method, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Activity> query = cb.createQuery(Activity.class);
        Root<Activity> activity = query.from(Activity.class);

        Path<String> activity_name = activity.get("name");
        List<Predicate> predicates = new ArrayList<>();
        List<String> keywordsList = Arrays.asList(keywords.split("-"));
        for (String keyword : keywordsList) {
            keyword = "%" + keyword + "%";
            predicates.add(cb.like(activity_name, keyword));
        }

        if ((predicates.size() != keywordsList.size()) && (method.equals("AND"))) {
            return new PageImpl<>(new ArrayList<>());
        }
        query.select(activity)
                .where(cb.or(predicates.toArray(new Predicate[predicates.size()])));

        List<Activity> toReturn = entityManager.createQuery(query)
                .getResultList();
        System.out.println(entityManager.createQuery(query));
        return new PageImpl<>(toReturn);
    }

}
