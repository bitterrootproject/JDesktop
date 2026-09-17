package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.table.DatabaseTable;

import lombok.*;


/**
 * This is the sixth and final component of a call number. Some call numbers will have an author or publisher.
 * <br>
 * <b>Parent part:</b> none
 */

@DatabaseTable(tableName = "authors_publishers")

@Getter @Setter
@AllArgsConstructor
// @NoArgsConstructor
public class AuthorPublisher extends CallNumberPart {}