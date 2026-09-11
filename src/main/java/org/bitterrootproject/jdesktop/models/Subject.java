package org.bitterrootproject.jdesktop.models;

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
public class Subject extends CallNumberPart {}