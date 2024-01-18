package com.imst.event.map.web.db.support;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

@SuppressWarnings("serial")
public abstract class CustomSpecificationImpl<D, T>  extends CustomSpecificationAbs<D, T>{

	@Override
	public Selection<?>[] getConstructorParams(Root<D> root) {
		return null;
	}
	
}
