package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * This is the second component of a call number. Most call numbers will have a domain.
 * <br>
 * <b>Parent part:</b> {@link Subject}
 */

@EqualsAndHashCode(callSuper = true)
@DatabaseTable(tableName = "domains")
@NoArgsConstructor
@AllArgsConstructor
public @Data class Domain extends CallNumberPart implements HasParentPart<Subject> {
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Subject subject;
	
	public static String FIELD_NAME_SUBJECT = "subject";
	public static String FIELD_NAME_PARENT = Domain.FIELD_NAME_SUBJECT;
	
	public Subject getParent() { return subject; }
	public void setParent(Subject subject) { setSubject(subject); }
	
}