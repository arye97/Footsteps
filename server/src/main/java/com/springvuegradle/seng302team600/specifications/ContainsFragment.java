package com.springvuegradle.seng302team600.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.springvuegradle.seng302team600.model.Activity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class ContainsFragment implements Specification<Activity> {

    private String fragment;

    public ContainsFragment(String fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(root.get("name"), '%' + fragment + '%');
    }
}
