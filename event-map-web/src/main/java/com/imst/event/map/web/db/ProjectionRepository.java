package com.imst.event.map.web.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface ProjectionRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T>, QueryByExampleExecutor<T> {
	
	List<T> findAll();
	
	Iterable<T> findAll(Sort var1);
	
	Page<T> findAll(Pageable var1);
	
	List<T> findAll(@Nullable Specification<T> var1);
	
	Page<T> findAll(@Nullable Specification<T> var1, Pageable var2);
	
	List<T> findAll(@Nullable Specification<T> var1, Sort var2);
	
}
