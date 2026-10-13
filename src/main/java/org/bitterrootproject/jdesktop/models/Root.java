package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


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
public class Root extends CallNumberPart implements HasParentPart<DomainLike> {
	
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
	public static String FIELD_NAME_SUBDOMAIN = "subdomain_id";
	
	
	@NonNull
	public Class<? extends DomainLike> getParentClass() {
		return getParent().getClass();
	}
	
	
	public void setParent(@NonNull DomainLike part) {
		if (part == null) {
			throw new IllegalArgumentException("part must not be null");
		}
		
		var partClass = part.getClass();
		
		if (partClass.equals(Domain.class)) {
			this.domain = (Domain) part;
			this.subdomain = null;
		} else if (partClass.equals(Subdomain.class)) {
			this.domain = null;
			this.subdomain = (Subdomain) part;
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
}