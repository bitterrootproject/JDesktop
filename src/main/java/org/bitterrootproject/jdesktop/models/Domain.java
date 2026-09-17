package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;
import org.jspecify.annotations.NonNull;



/**
 * This is the second component of a call number. Most call numbers will have a domain.
 * <br>
 * <b>Parent part:</b> {@link Subject}
 */

@DatabaseTable(tableName = "domains")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Domain extends CallNumberPart implements HasParentPart<Subject> {
	
	@NonNull
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true,
			canBeNull = false
	)
	private Subject subject;
	
	public static String FIELD_NAME_SUBJECT = "subject_id";
	@SuppressWarnings("unused")
	public static String FIELD_NAME_PARENT = Domain.FIELD_NAME_SUBJECT;
	
	@NonNull
	public Subject getParent() { return subject; }
	public void setParent(@NonNull Subject subject) { setSubject(subject); }
	public Class<Subject> getParentClass() { return Subject.class; }
}