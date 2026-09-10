package org.bitterrootproject.jdesktop.models;

public interface HasParentPart<T extends CallNumberPart> {
	/**
	 * Get the parent of this call number part. This is essentially just an alias to the part's actual getter for
	 * the parent field.
	 * @return This call number part's parent.
	 */
	T getParent();
	
	/**
	 * Set the parent of this call number part. This is essentially just an alias to the part's actual setter for
	 * the parent field.
	 * @param parent The call number part of type {@link T} to set as this part's parent.
	 */
	void setParent(T parent);
}
