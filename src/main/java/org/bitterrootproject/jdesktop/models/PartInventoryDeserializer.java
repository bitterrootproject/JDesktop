package org.bitterrootproject.jdesktop.models;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


@Log4j2
public class PartInventoryDeserializer extends DefaultHandler {
	
	@Getter
	private final HashMap<Long, Subject> subjects = new HashMap<>();
	@Getter
	private final HashMap<Long, Domain> domains = new HashMap<>();
	@Getter
	private final HashMap<Long, Root> roots = new HashMap<>();
	@Getter
	private final HashMap<Long, Aspect> aspects = new HashMap<>();
	@Getter
	private final HashMap<Long, Topic> topics = new HashMap<>();
	@Getter
	private final HashMap<Long, AuthorPublisher> authorPublishers = new HashMap<>();
	
	private StringBuilder elementValue;
	
	private Object scratchObject;
	private Class<?> scratchClass;
	private Long scratchId;
	
	private Object parentObject;
	private Class<?> parentClass;
	
	
	@Nullable
	private Object createNewPart() {
		log.debug("Creating new part of type '{}' for deserialization", scratchClass.getSimpleName());
		try {
			Constructor<?> constructor = scratchClass.getConstructor();
			return constructor.newInstance();
			
		} catch (InvocationTargetException e) {
			log.error("Part constructor for '{}' threw an error", scratchClass.getSimpleName(), e);
		} catch (NoSuchMethodException e) {
			log.error("Unable to get or find the constructor for this part type '{}'", scratchClass.getSimpleName(), e);
		} catch (InstantiationException e) {
			log.error("Somehow, the constructor found for '{}' is abstract, and we can't instantiate abstract classes", scratchClass.getSimpleName(), e);
		} catch (IllegalAccessException e) {
			log.error("Can't access the constructor for '{}' due to member access restrictions", scratchClass.getSimpleName(), e);
		}
		
		return null;
	}
	
	private void callMethod(String methodName, Class<?> argType, Object arg) {
		try {
			scratchClass.getMethod(methodName, argType).invoke(scratchObject, arg);
		} catch (NoSuchMethodException e) {
			log.error("Unable to call `setId` function on {}", scratchClass.getSimpleName(), e);
		} catch (IllegalAccessException e) {
			log.error("Can't access `setId` for '{}' due to member access restrictions", scratchClass.getSimpleName(), e);
		} catch (InvocationTargetException e) {
			log.error("`setID` method for '{}' threw an error", scratchClass.getSimpleName(), e);
		}
	}
	
	private void setPartId(long id) {
		callMethod("setId", Long.class, id);
		scratchId = id;
	}
	
	private void setName(String name) {
		callMethod("setName", String.class, name);
	}
	
	private void setNumber(String number) {
		callMethod("setNumber", String.class, number);
	}
	
	private void setParent(@NonNull Object parent) {
		if (partHasParent()) {
			((HasParentPart<?>) scratchObject).setParentCast(parent);
		} else {
			log.warn("Tried to set '{}' parent to '{}', but that part doesn't have a parent", scratchClass.getSimpleName(), parentClass.getSimpleName());
		}
	}
	
	private boolean partHasParent() {
		switch (scratchClass.getSimpleName()) {
			case "Domain", "Aspect", "Topic" -> { return true; }
			default -> { return false; }
		}
	}
	
	@Override
	public void characters(char[] chars, int start, int length) {
		if (this.elementValue == null) {
			this.elementValue = new StringBuilder();
		} else {
			this.elementValue.append(chars, start, length);
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		// Upper-case names are the actual parts themselves, and will contain the `id` attribute.
		// Lower-case names are used to reference the parent element, and will contain the `ref` attribute
		// or are the name/number field of the part.
		switch (qName) {
			case "Subject", "Domain", "AuthorPublisher", "Topic", "Aspect", "Root" -> initScratch(attributes);
			
			case "Subjects" -> scratchClass = Subject.class;
			// parent ref element
			case "subject" -> parentObject = subjects.get(Long.parseLong(
					attributes.getValue("ref")
			));
			
			case "Domains" -> { scratchClass = Domain.class; parentClass = Subject.class; }
			
			case "Roots" -> scratchClass = Root.class;
			case "root" -> parentObject = roots.get(Long.parseLong(
					attributes.getValue("ref")
			));
			
			case "Aspects" -> { scratchClass = Aspect.class; parentClass = Root.class; }
			case "aspect" -> parentObject = aspects.get(Long.parseLong(
					attributes.getValue("ref")
			));
			
			case "Topics" -> { scratchClass = Topic.class; parentClass = Aspect.class; }
			
			case "AuthorPublishers" -> scratchClass = AuthorPublisher.class;
			
			case "number", "name" -> elementValue = new StringBuilder();
		}
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName) {
		// Upper-case names are the actual parts themselves, and will contain the `id` attribute.
		// Lower-case names are used to reference the parent element, and will contain the `ref` attribute
		// or are the name/number field of the part.
		switch (qName) {
			case "Subjects", "AuthorPublishers", "Roots" -> scratchClass = null;
			case "Domains", "Topics", "Aspects" -> { scratchClass = null; parentClass = null; }
			
			
			case "Subject" -> {
				subjects.put(scratchId, (Subject) scratchObject);
				cleanupScratch();
			}
			
			case "Domain" -> {
				setParent(parentObject);
				parentObject = null;
				domains.put(scratchId, (Domain) scratchObject);
				cleanupScratch();
			}
			
			case "Root" -> {
				roots.put(scratchId, (Root) scratchObject);
				cleanupScratch();
			}
			
			case "Aspect" -> {
				setParent(parentObject);
				parentObject = null;
				aspects.put(scratchId, (Aspect) scratchObject);
				cleanupScratch();
			}
			
			case "Topic" -> {
				setParent(parentObject);
				parentObject = null;
				topics.put(scratchId, (Topic) scratchObject);
				cleanupScratch();
			}
			
			case "AuthorPublisher" -> {
				authorPublishers.put(scratchId, (AuthorPublisher) scratchObject);
				cleanupScratch();
			}
			
			
			case "number" -> setNumber(elementValue.toString().strip());
			case "name" -> setName(elementValue.toString().strip());
		}
	}
	
	
	private void initScratch(Attributes attributes) {
		var id = Long.parseLong(attributes.getValue("id"));
		initScratch(id);
	}
	
	private void initScratch(Long id) {
		scratchObject = createNewPart();
		scratchId = id;
		setPartId(id);
	}
	
	private void cleanupScratch() {
		scratchId = null;
		scratchObject = null;
	}
}
