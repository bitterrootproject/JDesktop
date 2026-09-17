package org.bitterrootproject.jdesktop.models;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

public interface HasParentPart<T extends CallNumberPart> {
	/**
	 * Get the parent of this call number part. This is essentially just an alias to the part's actual getter for
	 * the parent field.
	 * @return This call number part's parent.
	 */
	@NonNull
	T getParent();
	
	/**
	 * Get the child class of this parent part.
	 */
	Class<T> getParentClass();
	
	/**
	 * Set the parent of this call number part. This is essentially just an alias to the part's actual setter for
	 * the parent field.
	 * @param parent The call number part of type {@link T} to set as this part's parent.
	 */
	void setParent(@NonNull T parent);
	
	
	/**
	 * Set the parent of this part. <b><i>This method should be considered private</i></b> and an internal method, so
	 * don't call it unless you know you should be! In all other cases, you should be calling
	 * {@link #setParent(CallNumberPart)}.
	 *
	 * @param parent Untyped parent {@link Object} of this child part.
	 * @throws ClassCastException If {@code parent} is not the right parent type.
	 */
	@ApiStatus.Internal
	default void setParentCast(@NonNull Object parent) throws ClassCastException {
		Class<T> parentClass = getParentClass();
		
		if (!parentClass.isInstance(parent)) {
			throw new ClassCastException(
					String.format("Can't cast %s to %s", parent.getClass().getName(), this.getClass().getName())
			);
		}
		
		setParent(parentClass.cast(parent));
	}
}