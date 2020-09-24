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
    public List<Activity> findAllByKeywordUsingMethod(@Param("keywords") List<String> keywords) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Activity> query = cb.createQuery(Activity.class);
        Root<Activity> activity = query.from(Activity.class);

        List<Predicate> predicates = new ArrayList<>();
        Path<String> activity_name = activity.get("name");
        System.out.println(keywords);

        boolean orMode = false;
        Predicate predicate = cb.like(activity_name, "");
        if (keywords.size() > 1) orMode = keywords.get(1).equals("\"+\"");
        if (orMode) {
            predicate = cb.notLike(activity_name, "");
        }
        System.out.println(orMode);
        for (int i = 0; i < keywords.size(); i++) {
            String word = keywords.get(i);
            if (word.equals("\"+\"")) {
                continue; //Ignore the or symbol
            } else {
                Predicate currentPredicate = predicate = cb.like(activity_name, word);
                if (orMode) {
                    Predicate base = predicate;
                    System.out.println("Or with " + word);
                    predicate = cb.or(base, currentPredicate);
                } else {
                    Predicate base = predicate;
                    System.out.println("And with " + word);
                    predicate = cb.and(base, currentPredicate);
                }

                if (keywords.size() > i + 1) {
                    if (keywords.get(i + 1).equals("\"+\"")) {
                        if (!orMode) {
                            System.out.println("Swapping from AND to OR mode");
                            predicates.add(predicate);
                            predicate = cb.notLike(activity_name, "");
                        }
                        orMode = true;
                    } else {
                        if (orMode) {
                            System.out.println("Swapping from OR to AND mode");
                            predicates.add(predicate);
                            predicate = cb.like(activity_name, "");
                        }
                        orMode = false;
                    }
                } else {
                    System.out.println("Pushing final predicate");
                    predicates.add(predicate);
                }
            }
        }

        query.select(activity).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));


        return entityManager.createQuery(query)
                .getResultList();
    }

}
