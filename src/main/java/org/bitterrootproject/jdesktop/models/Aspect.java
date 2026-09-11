package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;


/**
 * This is the fourth component of a call number. Many call numbers will have an aspect.
 * <br>
 * <b>Parent part:</b> {@link Root}
 */

@DatabaseTable(tableName = "aspects")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aspect extends CallNumberPart implements HasParentPart<Root>, HasChildPart<Topic> {
	
	// Parent field
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Root root;
	
	public static String FIELD_NAME_ROOT = "root_id";
	@SuppressWarnings("unused")
	public static String FIELD_NAME_PARENT = Aspect.FIELD_NAME_ROOT;
	
	public Root getParent() { return root; }
	public void setParent(Root root) { setRoot(root); }
	
	// Child field
	
	@ForeignCollectionField
	private ForeignCollection<Topic> topics;
	
	public ForeignCollection<Topic> getChildCollection() { return topics; }
	public Class<Topic> getChildClass() { return Topic.class; }
	public int countChildren() { return topics.size(); }
}