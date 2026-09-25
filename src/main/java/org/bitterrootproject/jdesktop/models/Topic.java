package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;
import org.jspecify.annotations.NonNull;


/**
 * This is the fifth component of a call number. Many call numbers will have a topic.
 * <br>
 * <b>Parent part:</b> {@link Aspect}
 */

@DatabaseTable(tableName = "topics")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Topic extends CallNumberPart implements HasParentPart<Aspect> {
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Aspect aspect;
	
	public static String FIELD_NAME_ASPECT = "aspect_id";
	
	public @NonNull Aspect getParent() { return aspect; }
	public void setParent(@NonNull Aspect aspect) { setAspect(aspect); }
	public @NonNull Class<Aspect> getParentClass() { return Aspect.class; }
}