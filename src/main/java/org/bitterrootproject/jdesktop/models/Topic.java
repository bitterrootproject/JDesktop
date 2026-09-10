package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * This is the fifth component of a call number. Many call numbers will have a topic.
 * <br>
 * <b>Parent part:</b> {@link Aspect}
 */

@EqualsAndHashCode(callSuper = true)
@DatabaseTable(tableName = "topics")
@NoArgsConstructor
@AllArgsConstructor
public @Data class Topic extends CallNumberPart implements HasParentPart<Aspect> {
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Aspect aspect;
	
	public static String FIELD_NAME_ASPECT = "aspect";
	public static String FIELD_NAME_PARENT = Topic.FIELD_NAME_ASPECT;
	
	public Aspect getParent() { return aspect; }
	public void setParent(Aspect aspect) { setAspect(aspect); }
	
}