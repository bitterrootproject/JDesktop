package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;


/**
 * This is the first component of a call number. All call numbers must have at least a domain.
 * <br>
 * <b>Parent part:</b> none.
 */

@DatabaseTable(tableName = "domains")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Domain extends CallNumberPart implements HasChildPart<Subdomain> {
	@ForeignCollectionField
	private ForeignCollection<Subdomain> subdomains;
	public ForeignCollection<Subdomain> getChildCollection() { return subdomains; }
	public Class<Subdomain> getChildClass() { return Subdomain.class; }
	public int countChildren() { return subdomains.size(); }
}