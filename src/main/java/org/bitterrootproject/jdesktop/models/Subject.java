package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;


/**
 * This is the first component of a call number. All call numbers must have at least a subject.
 * <br>
 * <b>Parent part:</b> none.
 */

@DatabaseTable(tableName = "subjects")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subject extends CallNumberPart implements HasChildPart<Domain> {
	@ForeignCollectionField
	private ForeignCollection<Domain> domains;
	public ForeignCollection<Domain> getChildCollection() { return domains; }
	public Class<Domain> getChildClass() { return Domain.class; }
	public int countChildren() { return domains.size(); }
}