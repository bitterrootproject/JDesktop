package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.dao.ForeignCollection;

public interface HasChildPart<T extends CallNumberPart> {
	/**
	 * Get the ForeignCollection of the child part of this parent part.
	 */
	ForeignCollection<T> getChildCollection();
	
	/**
	 * Get the child class of this parent part.
	 */
	Class<? extends CallNumberPart> getChildClass();
	
	int countChildren();
}
