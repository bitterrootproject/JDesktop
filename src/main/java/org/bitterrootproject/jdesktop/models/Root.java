package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * This is the third component of a call number. Most (if not all) call numbers will have a root.
 * <br>
 * <b>Parent part:</b> none
 */

@EqualsAndHashCode(callSuper = true)
@DatabaseTable(tableName = "roots")
@AllArgsConstructor
public @Data class Root extends CallNumberPart {}