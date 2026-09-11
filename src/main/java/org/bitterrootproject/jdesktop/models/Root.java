package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;


/**
 * This is the third component of a call number. Most (if not all) call numbers will have a root.
 * <br>
 * <b>Parent part:</b> none
 */

@DatabaseTable(tableName = "roots")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Root extends CallNumberPart implements HasChildPart<Aspect> {
	@ForeignCollectionField
	private ForeignCollection<Aspect> aspects;
	
	public ForeignCollection<Aspect> getChildCollection() { return aspects; }
	public Class<Aspect> getChildClass() { return Aspect.class; }
	public int countChildren() { return aspects.size(); }
}