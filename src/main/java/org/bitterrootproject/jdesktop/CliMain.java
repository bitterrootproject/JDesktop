package org.bitterrootproject.jdesktop;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.bitterrootproject.jdesktop.models.*;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class CliMain {
	public static void main(String[] args) throws Exception {
		AppDirs appDirs = AppDirsFactory.getInstance();
		
		// Set the right directory for all logs to be stored in.
		// String logLocation;
		if (isRuntimeContextDev()) {
			String logLocation = "dev-data";
			System.setProperty("log.location", logLocation);
			System.out.printf("Set log file path: %s\n", logLocation);
		} else {
			@org.jetbrains.annotations.NotNull String logLocation = appDirs.getUserLogDir(
					"JDesktop",
					null,
					"Bitterroot Project"
			);
			System.setProperty("log.location", logLocation);
			System.out.printf("Set log file path: %s\n", logLocation);
		}
		
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		// SAXParser parser = factory.newSAXParser();
		// PartInventoryDeserializer handler = new PartInventoryDeserializer();
		//
		// parser.parse("should-be.xml", handler);
		
		var dbManager = DatabaseManager.getInstance();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.newDocument();
		
		Element documentRoot = document.createElement("CallNumberParts");
		document.appendChild(documentRoot);
		
		Element elSubjects = document.createElement("Subjects");
		for (Subject subject : dbManager.subjects.queryForAll()) {
			elSubjects.appendChild(subject.serialize(document));
		}
		documentRoot.appendChild(elSubjects);
		
		Element elDomains = document.createElement("Domains");
		for (Domain domain : dbManager.domains.queryForAll()) {
			elDomains.appendChild(domain.serialize(document));
		}
		documentRoot.appendChild(elDomains);
		
		Element elRoots = document.createElement("Roots");
		for (Root root : dbManager.roots.queryForAll()) {
			elRoots.appendChild(root.serialize(document));
		}
		documentRoot.appendChild(elRoots);
		
		Element elAspects = document.createElement("Aspects");
		for (Aspect aspect : dbManager.aspects.queryForAll()) {
			elAspects.appendChild(aspect.serialize(document));
		}
		documentRoot.appendChild(elAspects);
		
		Element elTopics = document.createElement("Topics");
		for (Topic topic : dbManager.topics.queryForAll()) {
			elTopics.appendChild(topic.serialize(document));
		}
		documentRoot.appendChild(elTopics);
		
		Element elAuthorPublishers = document.createElement("AuthorPublishers");
		for (AuthorPublisher authorPublisher : dbManager.authorPublishers.queryForAll()) {
			elAuthorPublishers.appendChild(authorPublisher.serialize(document));
		}
		documentRoot.appendChild(elAuthorPublishers);
		
		DOMSource dom = new DOMSource(document);
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		
		StreamResult result = new StreamResult(new File("dev-data/what-it-is.xml"));
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(dom, result);
	}
	
	private static boolean isRuntimeContextDev() {
		@Nullable String rawValue = System.getenv("DEV");
		
		if (rawValue == null) {
			return false;
			
		} else {
			String[] truths = {"t", "true", "y", "yes", "1"};
			return Arrays.stream(truths).anyMatch(
					rawValue.toLowerCase(Locale.ROOT)::startsWith
			);
		}
	}
}
