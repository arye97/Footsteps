package com.springvuegradle.seng302team600.specifications;

import com.springvuegradle.seng302team600.model.Activity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class DoesNotContainsFragment implements Specification<Activity> {

    private String fragment;

    public DoesNotContainsFragment(String fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notLike(root.get("name"), '%' + fragment + '%');
    }
}
