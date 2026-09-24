package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;
import org.jspecify.annotations.NonNull;



/**
 * This is the second component of a call number. Many call numbers will have a subdomain.
 * <br>
 * <b>Parent part:</b> {@link Domain}
 */

@DatabaseTable(tableName = "subdomains")

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subdomain extends CallNumberPart implements HasParentPart<Domain> {
	
	@NonNull
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true,
			canBeNull = false
	)
	private Domain domain;
	
	public static String FIELD_NAME_DOMAIN = "domain_id";
	@SuppressWarnings("unused")
	public static String FIELD_NAME_PARENT = Subdomain.FIELD_NAME_DOMAIN;
	
	@NonNull
	public Domain getParent() { return domain; }
	public void setParent(@NonNull Domain domain) { setDomain(domain); }
	public Class<Domain> getParentClass() { return Domain.class; }
}