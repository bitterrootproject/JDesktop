package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * This is the sixth and final component of a call number. Some call numbers will have an author or publisher.
 * <br>
 * <b>Parent part:</b> none
 */

@EqualsAndHashCode(callSuper = true)
@DatabaseTable(tableName = "authors_publishers")
@AllArgsConstructor
public @Data class AuthorPublisher extends CallNumberPart {}