package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@DatabaseTable(tableName = "topics")
@NoArgsConstructor
@AllArgsConstructor
public @Data class Topic implements CallNumberPart {
	
	@DatabaseField(generatedId = true)
	private long id;
	
	@DatabaseField(
			foreign = true,
			foreignAutoCreate = true,
			foreignAutoRefresh = true
	)
	private Aspect aspect;
	
	@DatabaseField(canBeNull = false)
	private String number;
	
	@DatabaseField(canBeNull = false)
	private String name;
	
	public String toString() {
		return String.format("%s - %s", this.number, this.name);
	}
}