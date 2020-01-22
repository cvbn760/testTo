package com.space.specifications;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component("Spec")
public class SpecificationShip implements Specification<Ship>
{
    private List<SearchCriteria> searchCriteriaList;

    public SpecificationShip()
    {
        searchCriteriaList = new ArrayList<>();
    }

    public void addSearchCriteria(SearchCriteria searchCriteria)
    {
        searchCriteriaList.add(searchCriteria);
    }

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
    {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria searchCriteria : searchCriteriaList)
        {
            switch (searchCriteria.getSearchOperaton())
            {
                case CONSTAINS:
                    predicates.add(criteriaBuilder.like(root.get(searchCriteria.getKey()), "%" + searchCriteria.getValue().toString() + "%"));
                    break;
                case SHIP_TYPE_EQUAL:
                    predicates.add(criteriaBuilder.equal(root.get(searchCriteria.getKey()), (ShipType) searchCriteria.getValue()));
                    break;
                case DATE_LESS_OR_EQUAL:
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()),(Date)searchCriteria.getValue()));
                    break;
                case DATE_GREAT_OR_EQUAL:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()),(Date)searchCriteria.getValue()));
                    break;
                case USED_TRUE:
                    predicates.add(criteriaBuilder.isTrue(root.get(searchCriteria.getKey())));
                    break;
                case USED_FALSE:
                    predicates.add(criteriaBuilder.isFalse(root.get(searchCriteria.getKey())));
                    break;
                case SPEED_LESS_OR_EQUAL:
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()),(Double) searchCriteria.getValue()));
                    break;
                case SPEED_GREAT_OR_EQUAL:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()),(Double) searchCriteria.getValue()));
                    break;
                case CREW_SIZE_LESS_OR_EQUAL:
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()),(Integer) searchCriteria.getValue()));
                    break;
                case CREW_SIZE_GREAT_OR_EQUAL:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()),(Integer) searchCriteria.getValue()));
                    break;
                case RATING_LESS_OR_EQUAL:
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()),(Double) searchCriteria.getValue()));
                    break;
                case RATING_GREAT_OR_EQUAL:
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()),(Double) searchCriteria.getValue()));
                    break;
                case NULL:
                    searchCriteriaList = new ArrayList<>();
                    break;
                default:
                    System.out.println("Ошибка критерия поиска");
                    break;
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
