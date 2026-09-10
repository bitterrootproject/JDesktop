package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * This is the fourth component of a call number. Many call numbers will have an aspect.
 * <br>
 * <b>Parent part:</b> {@link Root}
 */

@EqualsAndHashCode(callSuper = true)
@DatabaseTable(tableName = "aspects")
@NoArgsConstructor
@AllArgsConstructor
public @Data class Aspect extends CallNumberPart implements HasParentPart<Root> {
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Root root;
	
	public static String FIELD_NAME_ROOT = "root";
	public static String FIELD_NAME_PARENT = Aspect.FIELD_NAME_ROOT;
	
	public Root getParent() { return root; }
	public void setParent(Root root) { setRoot(root); }
	
}