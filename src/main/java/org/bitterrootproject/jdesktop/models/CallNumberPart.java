package org.bitterrootproject.jdesktop.models;

import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * This abstract class defines the base fields and methods required by all call number parts:
 *
 * <ul>
 *     <li>{@code id} - the internal database primary key.</li>
 *     <li>{@code number} - the compact, 1-3 digit number (which may contain letters) used in the formatted CallNumber</li>
 *     <li>{@code name} - the longer, human-readable name</li>
 *     <li>{@code toString()} - formats the part like "{number} - {name}"</li>
 *     <li>
 *         A public static String named {@code FIELD_NAME_{NAME}} for each field, which is just the string representation
 *         of the actual class field. This follows
 *         <a href="https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#QueryBuilder-Basics">ORMLite best practices</a>
 *         and makes querying less error-prone.
 *     </li>
 * </ul>
 *
 * <br>
 * All call number parts which extend this abstract class must have the following decorators:
 *
 * <ul>
 *     <li>{@link lombok.EqualsAndHashCode} - set {@code callSuper} to {@code true}</li>
 *     <li>
 *         {@link com.j256.ormlite.table.DatabaseTable} - set the {@code tableName} parameter to the name of the
 *         database table. This should be the same name of the class, but plural, lower case, and using camel case.
 *     </li>
 *     <li>
 *         {@link lombok.AllArgsConstructor}. Do not decorate it with &commat;NoArgsConstructor, as a no-args
 *         constructor seems to be automatically created when extending this abstract class, so including it will cause
 *         compilation errors.
 *     </li>
 *     <li>{@link lombok.Data}</li>
 * </ul>
 *
 * <br>
 * A call number part can also have a parent part. For example, {@link Domain}'s parent part is {@link Subject}.
 * Establishing this relationship requires implementing the {@link HasParentPart} interface adding a few fields,
 * replacing "{parent_name}" where necessary. See {@link Domain} for an example of how this is done.
 *
 * <ul>
 *     <li>
 *        A private field of the parent's class, decorated with {@link DatabaseField}.
 *     </li>
 *     <li>
 *         Two public static Strings, one named {@code FIELD_NAME_{parent_name}} and one named {@code FIELD_NAME_PARENT},
 *         with the latter's value being equal to the former's.
 *     </li>
 *     <li>
 *         The child call number part must completely implement the {@link HasParentPart} interface and set the generic
 *         type parameter to the parent's type.
 *     </li>
 *     <li>
 *         Decorate the class with the {@link NoArgsConstructor} decorator.
 *     </li>
 * </ul>
 *
 */

public abstract class CallNumberPart {
	@Getter @Setter
	@DatabaseField(generatedId = true)
	protected long id;
	public static String FIELD_NAME_ID = "id";
	
	@Getter @Setter
	@DatabaseField(canBeNull = false)
	protected String number;
	public static String FIELD_NAME_NUMBER = "number";
	
	@Getter @Setter
	@DatabaseField(canBeNull = false)
	protected String name;
	public static String FIELD_NAME_NAME = "name";
	
	public String toString() {
		return String.format("%s - %s", this.number, this.name);
	}
}
