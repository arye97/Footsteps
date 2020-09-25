package com.springvuegradle.seng302team600.repository;

import com.springvuegradle.seng302team600.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom {
    public static class SearchResponse {
        public final List<Activity> activities;
        public final int count;
        public SearchResponse(List<Activity> activities, Long count) {
            this.activities = activities;
            this.count = count.intValue();
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SearchResponse findAllByKeyword(@Param("keywords") List<String> keywords, int pageSize,
                                           int page, Integer minFitness, Integer maxFitness) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Activity> query = cb.createQuery(Activity.class);
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<Activity> activity = query.from(Activity.class);
        List<Predicate> predicates = new ArrayList<>();
        Path<String> activity_name = activity.get("name");
        Path<Integer> activityFitness = activity.get("fitnessLevel");

        boolean orMode = false;
        Predicate predicate = null;
        if (keywords.size() > 1) orMode = keywords.get(1).equals("\"+\"");
        if (orMode) predicate = cb.notLike(activity_name, "%");
        else predicate = cb.like(activity_name, "%");
        for (int i = 0; i < keywords.size(); i++) {
            String word = keywords.get(i);
            if (word.equals("\"+\"")) continue; //Ignore the or symbol
            else word = "%" + word + "%";
            Predicate currentPredicate = cb.like(activity_name, word);
            Predicate basePredicate = predicate;

            if (orMode) predicate = cb.or(basePredicate, currentPredicate);
            else predicate = cb.and(basePredicate, currentPredicate);

            if (keywords.size() > i + 1) {
                if (keywords.get(i + 1).equals("\"+\"") ||
                    keywords.size() > i + 2 && keywords.get(i + 2).equals("\"+\"")) {
                    if (!orMode) {
                        predicates.add(predicate);
                        predicate = cb.notLike(activity_name, "%");
                    }
                    orMode = true;
                } else {
                    if (orMode) {
                        predicates.add(predicate);
                        predicate = cb.like(activity_name, "%");
                    }
                    orMode = false;
                }
            }
            else {
                predicates.add(predicate);
            }
        }
        predicates.add(cb.and(
                cb.greaterThanOrEqualTo(activityFitness, minFitness),
                cb.lessThanOrEqualTo(activityFitness, maxFitness)
        ));
        Predicate searchPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        query.select(activity).where(searchPredicate);
        cQuery.select(cb.count(cQuery.from(Activity.class))).where(searchPredicate);
        Query finalQuery = entityManager.createQuery(query);
        finalQuery.setFirstResult(page);
        finalQuery.setMaxResults(pageSize);
        List<Activity> activities = finalQuery.getResultList();
        Long count = entityManager.createQuery(cQuery).getSingleResult();
        return new SearchResponse(activities, count);
    }

}
