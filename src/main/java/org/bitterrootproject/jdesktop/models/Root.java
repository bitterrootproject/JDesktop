package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.table.DatabaseTable;

import lombok.*;


/**
 * This is the third component of a call number. Most (if not all) call numbers will have a root.
 * <br>
 * <b>Parent part:</b> none
 */

@DatabaseTable(tableName = "roots")

@Getter @Setter
@AllArgsConstructor
public class Root extends CallNumberPart {}