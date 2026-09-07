package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@DatabaseTable(tableName = "authors_publishers")
@NoArgsConstructor
@AllArgsConstructor
public @Data class AuthorPublisher implements CallNumberPart {
	
	@DatabaseField(generatedId = true)
	private long id;
	
	@DatabaseField(canBeNull = false)
	private String number;
	
	@DatabaseField(canBeNull = false)
	private String name;
	
	public String toString() {
		return String.format("%s - %s", this.number, this.name);
	}
}