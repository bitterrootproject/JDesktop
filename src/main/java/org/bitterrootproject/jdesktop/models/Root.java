package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;


/**
 * This is the third component of a call number. Most (if not all) call numbers will have a root.
 * <br>
 * <b>Parent part:</b> {@link Domain} xor {@link Subdomain}
 */

@DatabaseTable(tableName = "roots")
@Log4j2
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Root extends CallNumberPart implements HasChildPart<Aspect>, HasParentPart<DomainLike> {
	
	// Parent: Domain xor Subdomain
	@Nullable
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Domain domain;
	public static String FIELD_NAME_DOMAIN = "domain_id";
	
	@Nullable
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Subdomain subdomain;
	public static String FIELD_NAME_SUBDOMAIN = "domain_id";
	
	
	private Class<? extends DomainLike> parentClass;
	
	
	public void setParent(@NonNull DomainLike part) {
		var partClass = part.getClass();
		
		if (partClass.equals(Domain.class)) {
			this.domain = (Domain) part;
			this.subdomain = null;
			this.parentClass = Domain.class;
		} else if (partClass.equals(Subdomain.class)) {
			this.domain = null;
			this.subdomain = (Subdomain) part;
			this.parentClass = Subdomain.class;
		} else {
			throw new IllegalArgumentException(String.format(
					"Roots do not support setting %s as the parent type",
					partClass.getSimpleName()
			));
		}
	}
	
	
	public @NonNull DomainLike getParent() {
		if (this.domain != null && this.subdomain == null) {
			return this.domain;
		} else if (this.domain == null && this.subdomain != null) {
			return this.subdomain;
		} else {
			throw new RuntimeException(
					"Somehow, this Root has both a domain and subdomain as its parent, or it has neither, neither of" +
					"which should be possible."
			);
		}
	}
	
	
	// Child: Aspect
	@ForeignCollectionField
	private ForeignCollection<Aspect> aspects;
	
	public ForeignCollection<Aspect> getChildCollection() { return aspects; }
	public Class<Aspect> getChildClass() { return Aspect.class; }
	public int countChildren() { return aspects.size(); }
}