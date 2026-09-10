package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * This is the first component of a call number. All call numbers must have at least a subject.
 * <br>
 * <b>Parent part:</b> none.
 */

@EqualsAndHashCode(callSuper = true)
@DatabaseTable(tableName = "subjects")
@AllArgsConstructor
public @Data class Subject extends CallNumberPart {}