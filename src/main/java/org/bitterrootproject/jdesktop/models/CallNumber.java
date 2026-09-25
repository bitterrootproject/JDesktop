package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;


/**
 * This model represents an "assembled" call number.
 */

// Define the table
@DatabaseTable(tableName = "call_numbers")

// Lombok magic: https://projectlombok.org/features/constructor
@NoArgsConstructor
@AllArgsConstructor
public @Data class CallNumber {  // the @Data decorator is also Lombok magic: https://projectlombok.org/features/Data
	
	@DatabaseField(generatedId = true)
	private long id;
	
	@NonNull
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true,
			canBeNull = false
	)
	private Domain domain;
	
	@Nullable
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Subdomain subdomain;
	
	@NonNull
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true,
			canBeNull = false
	)
	private Root root;
	
	@NonNull
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true,
			canBeNull = false
	)
	private Aspect aspect;
	
	@Nullable
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Topic topic;
	
	@Nullable
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private AuthorPublisher authorPublisher;
	
	@Nullable
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private String indicator;
	
	
	// Use something like https://github.com/datazuul/iso-639-javalib?
	@Nullable
	@DatabaseField
	private String language;
	
	
	@NonNull
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	@DatabaseField(canBeNull = false)
	private HashMap<String, String> metadata = new HashMap<>();
	public void setMetadata(String key, String value) {
		metadata.put(key, value);
	}
	@Nullable
	public String getMetadata(String key) {
		return metadata.get(key);
	}
	public boolean hasMetadataKey(String key) {
		return metadata.containsKey(key);
	}
}
