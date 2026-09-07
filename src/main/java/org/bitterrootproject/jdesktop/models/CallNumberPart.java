package org.bitterrootproject.jdesktop.models;

/**
 * All call number parts must have the ability to get the string names and numbers.
 */
public interface CallNumberPart {
	String getNumber();
	String getName();
}
