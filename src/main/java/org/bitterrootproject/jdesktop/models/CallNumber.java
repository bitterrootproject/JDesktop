package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Subject subject;
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Domain domain;
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Root root;
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Aspect aspect;
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Topic topic;
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private AuthorPublisher authorPublisher;
}
