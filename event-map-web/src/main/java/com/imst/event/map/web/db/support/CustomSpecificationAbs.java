package com.imst.event.map.web.db.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.log4j.Log4j2;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Log4j2
public abstract class CustomSpecificationAbs<D, T>  implements CustomSpecification<D, T>{
	
	private Class<D> domain;
	private Class<T> target;
	private Map<String, List<String>> conParams = new HashMap<>();
	private List<List<String>> fieldPathParams = new ArrayList<>();
	
	public CustomSpecificationAbs() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.domain = (Class<D>) type.getActualTypeArguments()[0];
		this.target = (Class<T>) type.getActualTypeArguments()[1];
		
		
		Constructor<?>[] constructors = target.getConstructors();
		
		Field[] declaredFields = target.getDeclaredFields();

		Map<String, String> annotationNameMap = new HashMap<>();
		for (Field field : declaredFields) {
			Column column = field.getAnnotation(javax.persistence.Column.class);
			if(column != null) {
				annotationNameMap.put(field.getName(), column.name());
			}
		}
		
		if (constructors.length == 0) {
			throw new NotImplementedException();
		}
		
		try {
			for (Constructor<?> constructor : constructors) {
				
				Parameter[] parameters = constructor.getParameters();
				for (Parameter parameter : parameters) {
					
					String name = parameter.getName();
					if(annotationNameMap.containsKey(parameter.getName())) {
						name = annotationNameMap.get(parameter.getName());
					}
					
					conParams.put(parameter.getName(), parameterFix(parameter.getName()));
					
					fieldPathParams.add(parameterFix(name));
				}
				if (conParams.size() > 0) {
					break;
				}
			}
		} catch (Exception e) {
			
			log.error(e);
		}
	}
	
	private List<String> parameterFix(String parameterName) {
		
		if (parameterName == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(StringUtils.splitByWholeSeparator(parameterName, "."));
	}
	
	
	@Override
	public Predicate toPredicate(Root<D> root, CriteriaQuery<T> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		return null;
	}
	
	@Override
	public Predicate toPredicateCount(Root<D> root, CriteriaQuery<Long> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		return null;
	}
	
	@Override
	public Class<D> getDomain() {
		
		return this.domain;
	}
	
	@Override
	public Class<T> getTarget() {
		
		return this.target;
	}
	
	
	@Override
	public Selection<?>[] getSelections(Root<D> root) {
		
		Selection<?>[] constructorParams = getConstructorParams(root);
		if (constructorParams != null) {
			return constructorParams;
		}
		
		
		List<Selection<?>> selectionList = new ArrayList<>();
		
		for (List<String> fieldPath : fieldPathParams) {
			
			
			Path<?> objectPath = null;
			for (String paramPart : fieldPath) {
				
				if (objectPath == null) {
					objectPath = root.get(paramPart);
				} else {
					objectPath = objectPath.get(paramPart);
				}
			}
			selectionList.add(objectPath);
		}
		
		return selectionList.toArray(new Selection<?>[0]);
	}
	
	/**
	 * @return null-> default parameter names from target constractor
	 */
	public abstract Selection<?>[] getConstructorParams(Root<D> root);

	
	public Predicate ilike(CriteriaBuilder criteriaBuilder, Path<String> name, String search) {
		
		return criteriaBuilder.like(
				criteriaBuilder.upper(criteriaBuilder.lower(name)),
				"%" + search.toLowerCase(new Locale("tr")).toUpperCase() + "%");
	}
}
