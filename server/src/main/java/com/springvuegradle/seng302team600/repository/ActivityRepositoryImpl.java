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
import java.util.List;

public abstract class ActivityRepositoryImpl implements ActivityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Activity> findAllByKeyword(@Param("keywords") List<String> keywords, String method, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Activity> query = cb.createQuery(Activity.class);
        Root<Activity> activity = query.from(Activity.class);

        Path<String> activity_name = activity.get("activity_name");

        List<Predicate> predicates = new ArrayList<>();
        for (String keyword : keywords) {
            predicates.add(cb.like(activity_name, keyword));
        }
        query.select(activity)
                .where(cb.or(predicates.toArray(new Predicate[predicates.size()])));

        List<Activity> toReturn = entityManager.createQuery(query)
                .getResultList();

        return new PageImpl<>(toReturn);
    }

}
