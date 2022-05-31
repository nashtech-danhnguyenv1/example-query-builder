package com.piinalpin.queryrequest.domain.common.query;

import com.piinalpin.queryrequest.domain.common.operator.AndOperator;
import com.piinalpin.queryrequest.domain.common.operator.OrOperator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class SearchSpecification<T> implements Specification<T> {

    private static final long serialVersionUID = -9153865343320750644L;

    private final transient SearchRequest request;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate initialAndPredicates = cb.equal(cb.literal(Boolean.TRUE), Boolean.TRUE);

        for (FilterRequest filter : this.request.getFilters().getAndOperators()) {
            AndOperator funcBuildPredicate = AndOperator.valueOf(filter.getOperator());
            initialAndPredicates = funcBuildPredicate.build(root, cb, filter, initialAndPredicates);
        }

        if (!this.request.getFilters().getOrOperators().isEmpty()) {
            FilterRequest firstItem = this.request.getFilters().getOrOperators().get(0);
            OrOperator funcBuildPredicate = OrOperator.valueOf(firstItem.getOperator());
            Predicate initialOrPredicates = funcBuildPredicate.build(root, cb, firstItem, null);
            List<FilterRequest> listRemainOrOperators =  this.request.getFilters().getOrOperators()
                    .subList(1, this.request.getFilters().getOrOperators().size());
            for (FilterRequest filter : listRemainOrOperators) {
                initialOrPredicates = OrOperator.valueOf(filter.getOperator()).build(root, cb, filter, initialOrPredicates);
            }
            initialAndPredicates = cb.and(initialOrPredicates, initialAndPredicates);
        }

        List<Order> orders = new ArrayList<>();
        for (SortRequest sort : this.request.getSorts()) {
            orders.add(sort.getDirection().build(root, cb, sort));
        }

        query.orderBy(orders);
        return initialAndPredicates;
    }

    public static Pageable getPageable(Integer page, Integer size) {
        return PageRequest.of(Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(size, 100));
    }

}
