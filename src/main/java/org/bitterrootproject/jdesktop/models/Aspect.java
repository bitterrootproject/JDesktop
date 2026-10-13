package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
// import org.jspecify.annotations.NonNull;


/**
 * This is the fourth component of a call number. Many call numbers will have an aspect.
 * <br>
 * <b>Parent part:</b> {@link Root}
 */

@DatabaseTable(tableName = "aspects")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aspect extends CallNumberPart implements HasParentPart<Domain>, HasChildPart<Topic> {
	
	// Parent field
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Domain domain;
	
	public static String FIELD_NAME_DOMAIN = "domain_id";
	
	public @NonNull Domain getParent() {
		return domain;
	}
	
	public void setParent(@NonNull Domain root) {
		setDomain(root);
	}
	
	public @NonNull Class<Domain> getParentClass() {
		return Domain.class;
	}
	
	// Child field
	
	@ForeignCollectionField
	private ForeignCollection<Topic> topics;
	
	public ForeignCollection<Topic> getChildCollection() { return topics; }
	public Class<Topic> getChildClass() { return Topic.class; }
	public int countChildren() { return topics.size(); }
}