package org.bitterrootproject.jdesktop.gui;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.scene.paint.Color;

import javafx.animation.PauseTransition;
import javafx.util.Duration;


import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.bitterrootproject.jdesktop.DatabaseManager;
import org.bitterrootproject.jdesktop.utils.GuiTools;
import org.bitterrootproject.jdesktop.models.*;

import javafx.scene.input.MouseEvent;

import org.jetbrains.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The JavaFX controller for the call number part inventory manager.
 */
@Log4j2
public class InventoryManagerController implements Initializable {
	public static final URL RESOURCE = InventoryManagerController.class.getResource("InventoryManagerView.fxml");
	
	private DatabaseManager dbManager;
	
	// UPPER HALF: select the part //
	
	// Filter fields
	@FXML
	private TextField textFieldFilterSubject;
	
	@FXML
	private TextField textFieldFilterDomain;
	
	@FXML
	private TextField textFieldFilterRoot;
	
	@FXML
	private TextField textFieldFilterAspect;
	
	@FXML
	private TextField textFieldFilterTopic;
	
	@FXML
	private TextField textFieldFilterAuthorPublisher;
	
	private <T extends CallNumberPart> ObjectBinding<ObservableList<T>> createFilterBinding(TextField filterField, ListProperty<T> objects) {
		return Bindings.createObjectBinding(
				() -> {
					String text = filterField.getText().strip().toLowerCase(Locale.ROOT);
					if (text.isBlank()) {
						return FXCollections.observableList(objects);
					} else {
						return FXCollections.observableList(
								objects.stream()
										.filter(o -> o.getName().contains(text) || o.getNumber().contains(text))
										.collect(Collectors.toList())
						);
					}
				},
				filterField.textProperty(), objects
		);
	}
	
	
	// List views
	@FXML
	private ListView<Subject> listSubject;
	private final ListProperty<Subject> filteredSubjects = new SimpleListProperty<>();
	private final ListProperty<Subject> allSubjects = new SimpleListProperty<>();
	
	@FXML
	private ListView<Domain> listDomain;
	private final ListProperty<Domain> filteredDomains = new SimpleListProperty<>();
	private final ListProperty<Domain> allDomains = new SimpleListProperty<>();
	
	@FXML
	private ListView<Root> listRoot;
	private final ListProperty<Root> filteredRoots = new SimpleListProperty<>();
	private final ListProperty<Root> allRoots = new SimpleListProperty<>();
	
	@FXML
	private ListView<Aspect> listAspect;
	private final ListProperty<Aspect> filteredAspects = new SimpleListProperty<>();
	private final ListProperty<Aspect> allAspects = new SimpleListProperty<>();
	
	@FXML
	private ListView<Topic> listTopic;
	private final ListProperty<Topic> filteredTopics = new SimpleListProperty<>();
	private final ListProperty<Topic> allTopics = new SimpleListProperty<>();
	
	@FXML
	private ListView<AuthorPublisher> listAuthorPublisher;
	private final ListProperty<AuthorPublisher> filteredAuthorPublishers = new SimpleListProperty<>();
	private final ListProperty<AuthorPublisher> allAuthorPublishers = new SimpleListProperty<>();
	
	
	
	// LOWER HALF: part creation/editing //
	
	// Parent info
	/// Contains the human-readable name (name) and its shortened ID (number) of the *parent* of the selected part.
	@FXML
	private TextField parentNameNumberField;
	
	/// Contains the part type of the *parent* of the selected part. Should be an enum member of {@code CallNumberPartType}.
	@FXML
	private TextField parentTypeField;
	
	
	// Selected's info
	@FXML
	private TextField selectedPartNameField;
	
	@FXML
	private TextField selectedPartNumberField;
	
	
	
	/// The label of the lower half of the window (the edit pane). It should contain the selected part's *type*, or
	/// the type of the new part being made.
	@FXML
	private Label labelEditCallNumberPart;
	
	@FXML
	private Label labelSaveStatus;
	
	private void scheduleSaveStatusClear() {
		PauseTransition clearLabelSaveStatus = new PauseTransition(Duration.seconds(3));
		clearLabelSaveStatus.setOnFinished(e -> {
			labelSaveStatus.setText("");
			labelSaveStatus.setTextFill(Color.BLACK);
		});
		clearLabelSaveStatus.play();
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listSubject.setCellFactory(new CallNumberPartCellFactory<>());
		listDomain.setCellFactory(new CallNumberPartCellFactory<>());
		listRoot.setCellFactory(new CallNumberPartCellFactory<>());
		listAspect.setCellFactory(new CallNumberPartCellFactory<>());
		listTopic.setCellFactory(new CallNumberPartCellFactory<>());
		listAuthorPublisher.setCellFactory(new CallNumberPartCellFactory<>());
		
		// setLabelEditCallNumberPart(null);
		labelEditCallNumberPart.setText("Edit part");
		labelSaveStatus.setText("");

		this.dbManager = DatabaseManager.getInstance();
		
		// Set the collections to monitor
		listSubject.setItems(filteredSubjects);
		listDomain.setItems(filteredDomains);
		listRoot.setItems(filteredRoots);
		listAspect.setItems(filteredAspects);
		listTopic.setItems(filteredTopics);
		listAuthorPublisher.setItems(filteredAuthorPublishers);

		// Bind the filter field to the collections
		filteredSubjects.bind(createFilterBinding(textFieldFilterSubject, allSubjects));
		filteredDomains.bind(createFilterBinding(textFieldFilterDomain, allDomains));
		filteredRoots.bind(createFilterBinding(textFieldFilterRoot, allRoots));
		filteredAspects.bind(createFilterBinding(textFieldFilterAspect, allAspects));
		filteredTopics.bind(createFilterBinding(textFieldFilterTopic, allTopics));
		filteredAuthorPublishers.bind(createFilterBinding(textFieldFilterAuthorPublisher, allAuthorPublishers));
		
		
		loadSubjects();
		loadRoots();
		loadAuthorPublishers();
	}
	
	
	private CallNumberPart selectedPart;
	private CallNumberPart parentPart;
	
	private CallNumberPart lastSavedPart;
	
	// private Dao<? extends CallNumberPart, Long> selectedPartDao;
	
	private boolean creatingNewPart = false;
	private Class<? extends CallNumberPart> newPartClass;
	// private CallNumberPartType newPartType;
	
	/**
	 * Select this part for editing, and display its child parts. Also sets {@link #selectedPart} to the specified {@code part}.
	 * @param part The part object to bring into the editor pane
	 */
	private void selectPartForEditing(@NotNull CallNumberPart part) {
		this.creatingNewPart = false;
		this.selectedPart = part;
		labelEditCallNumberPart.setText(String.format("Edit %s", part.getClass().getSimpleName()));
		log.info("Selected part: {}", part);
		
		// selectedPartDao = dbManager.getDao(part.getClass());
		
		// If the selected part has a parent, load that info.
		if (part.hasParent()) {
			this.parentPart = ((HasParentPart<?>) part).getParent();
			log.info("Selected part '{}' has parent: {}", part, parentPart);
			
			parentNameNumberField.setText(parentPart.formatString());
			parentTypeField.setText(parentPart.getClass().getSimpleName());
		} else {
			log.info("Selected part '{}' does not have a parent", part);
			this.parentPart = null;
			parentNameNumberField.setText("(n/a)");
			parentTypeField.setText("(n/a)");
		}
		
		selectedPartNameField.setText(part.getName());
		selectedPartNumberField.setText(part.getNumber());
	}
	
	
	/**
	 *
	 * @param parentPart The parent object of the part-to-be.
	 * @param childPartClass The class of the child part, i.e. the one being created in the edit pane
	 */
	private void prepareEditPaneForNewPart(@Nullable CallNumberPart parentPart, @NotNull Class<? extends CallNumberPart> childPartClass) {
		creatingNewPart = true;
		labelEditCallNumberPart.setText(String.format("Creating new %s", childPartClass.getSimpleName()));
		log.info("Creating new child part of type '{}' with parent '{}'", childPartClass.getSimpleName(), parentPart);
		
		if (parentPart == null) {
			log.info("New part type '{}' does not have a parent", childPartClass.getSimpleName());
			this.parentPart = null;
			parentNameNumberField.setText("(n/a)");
			parentTypeField.setText("(n/a)");
		} else {
			log.info("New part type '{}' has parent: {}", childPartClass.getSimpleName(), parentPart.formatString());
			this.parentPart = parentPart;
			
			parentNameNumberField.setText(parentPart.formatString());
			parentTypeField.setText(parentPart.getClass().getSimpleName());
		}
		
		selectedPartNameField.clear();
		selectedPartNumberField.clear();
		
		this.newPartClass = childPartClass;
	}
	
	
	/**
	 * Save the text in the associated fields to either a new part or an existing one, depending on what is happening.
	 * @return {@code true} if successfully saved, {@code false} otherwise
	 */
	private boolean savePartFromEditing() {
		if (selectedPartNameField.getText().isBlank() || selectedPartNumberField.getText().isBlank()) {
			log.debug(
					"User attempted to save call number part without setting both its name and number. They can't do that!");
			labelSaveStatus.setTextFill(Color.RED);
			labelSaveStatus.setText("Both the name and number must be set.");
			return false;
		}
		
		// If we're creating a new part, we have some extra stuff to do, since we have to: create a new part using
		// reflection, check if it has a parent (and so some casting), then do some more casting to save it to its
		// respective DAO.
		if (creatingNewPart) {
			log.debug("Creating new part of type '{}'", newPartClass.getSimpleName());
			String newPartName = selectedPartNameField.getText();
			String newPartNumber = selectedPartNumberField.getText();
			
			try {
				Constructor<?> constructor = newPartClass.getConstructor();
				CallNumberPart newPart = (CallNumberPart) constructor.newInstance();
				
				newPart.setName(newPartName);
				newPart.setNumber(newPartNumber);
				
				if (newPart.hasParent()) {
					// This should be fine. The compiler just doesn't know it.
					@SuppressWarnings("unchecked")
					HasParentPart<CallNumberPart> childPart = (HasParentPart<CallNumberPart>) newPart;
					childPart.setParent(parentPart);
				}
				
				log.info("Prepared new part: {}", newPart);
				
				log.debug("Attempting to cast the DAO to be able to use it (create).");
				@SuppressWarnings("unchecked")
				Dao<CallNumberPart, Long> typedDao = (Dao<CallNumberPart, Long>) dbManager.getDao(newPartClass);
				
				try {
					typedDao.create(newPart);
					log.info("Created new part in database: {}", newPart);
					labelSaveStatus.setTextFill(Color.GREEN);
					labelSaveStatus.setText("Saved successfully!");
					lastSavedPart = newPart;
					return true;
					
				} catch (SQLException e) {
					log.error("Failed to create call number part: {}", newPart, e);
					labelSaveStatus.setTextFill(Color.RED);
					labelSaveStatus.setText("Failed to save new part.");
					return false;
				}
				
				
			// Lots of things can go wrong with this, mostly due to the trickiness of reflection, so we have to
			// handle all the possible exceptions.
			} catch (InvocationTargetException e) {
				log.error("Part constructor for '{}' threw an error", newPartClass.getSimpleName(), e);
				labelSaveStatus.setTextFill(Color.RED);
				labelSaveStatus.setText("An error occurred. Check logs.");
				return false;
			} catch (NoSuchMethodException e) {
				log.error("Unable to get or find the constructor for this part type '{}'", newPartClass.getSimpleName(), e);
				labelSaveStatus.setTextFill(Color.RED);
				labelSaveStatus.setText("An error occurred. Check logs.");
				return false;
			} catch (InstantiationException e) {
				log.error("Somehow, the constructor found for '{}' is abstract, and we can't instantiate abstract classes", newPartClass.getSimpleName(), e);
				labelSaveStatus.setTextFill(Color.RED);
				labelSaveStatus.setText("An error occurred. Check logs.");
				return false;
			} catch (IllegalAccessException e) {
				log.error("Can't access the constructor for '{}' due to member access restrictions", newPartClass.getSimpleName(), e);
				labelSaveStatus.setTextFill(Color.RED);
				labelSaveStatus.setText("An error occurred. Check logs.");
				return false;
			}
			
		// If we're just editing an existing part, that's not too bad.
		} else {
			log.debug("Editing existing part: {}", this.selectedPart);
			this.selectedPart.setNumber(selectedPartNumberField.getText());
			this.selectedPart.setName(selectedPartNameField.getText());
			
			log.debug("Attempting to cast the DAO to be able to use it (edit).");
			@SuppressWarnings("unchecked")
			Dao<CallNumberPart, Long> typedDao = (Dao<CallNumberPart, Long>) dbManager.getDao(this.selectedPart.getClass());
			
			try {
				typedDao.update(this.selectedPart);
				log.info("Updated existing part in database: {}", this.selectedPart);
				labelSaveStatus.setTextFill(Color.GREEN);
				labelSaveStatus.setText("Saved successfully!");
				lastSavedPart = this.selectedPart;
				return true;
				
			} catch (SQLException e) {
				log.error("Failed to update call number part: {}", this.selectedPart, e);
				labelSaveStatus.setTextFill(Color.RED);
				labelSaveStatus.setText("Failed to save new part.");
				return false;
			}
			
		}
	}
	
	
	private void saveAndReloadPartList() {
		if (!savePartFromEditing()) {
			// Don't reload anything if it didn't successfully save.
			scheduleSaveStatusClear();
			return;
		}
		
		String lastSavedPartClassName = lastSavedPart.getClass().getSimpleName();
		log.debug("Last saved part class: {}", lastSavedPartClassName);
		
		// Reload the list of parts that just had a part saved or created
		switch (lastSavedPartClassName) {
			case "Subject" -> {
				log.debug("Decided to reload subjects");
				loadSubjects();
				listSubject.getSelectionModel().select((Subject) lastSavedPart);
				loadDomains((Subject) lastSavedPart);
			}
			case "Domain" -> {
				Subject subject = ((Domain) lastSavedPart).getSubject();
				log.debug("Decided to reload domains with parent subject_id {}", subject.getId());
				loadDomains(subject);
				listDomain.getSelectionModel().select((Domain) lastSavedPart);
			}
			case "Root" -> {
				log.debug("Decided to reload roots");
				loadRoots();
				listRoot.getSelectionModel().select((Root) lastSavedPart);
				loadAspects((Root) lastSavedPart);
				loadTopics(null);
			}
			case "Aspect" -> {
				Root root = ((Aspect) lastSavedPart).getRoot();
				log.debug("Decided to reload aspects with parent root_id {}", root.getId());
				loadAspects(root);
				listAspect.getSelectionModel().select((Aspect) lastSavedPart);
				loadTopics((Aspect) lastSavedPart);
			}
			case "Topic" -> {
				Aspect aspect = ((Topic) lastSavedPart).getAspect();
				log.debug("Decided to reload topics with parent aspect_id {}", aspect.getId());
				loadTopics(aspect);
				listTopic.getSelectionModel().select((Topic) lastSavedPart);
			}
			case "AuthorPublisher" -> {
				log.debug("Decided to reload authors/publishers");
				loadAuthorPublishers();
				listAuthorPublisher.getSelectionModel().select((AuthorPublisher) lastSavedPart);
			}
			default -> { }
		}
		
		clearEditPane();
		
		// Clear labelSaveStatus after a few seconds
		scheduleSaveStatusClear();
	}
	
	
	/**
	 * Safely and cautiously delete the part.
	 * <ul>
	 *     <li>
	 *          If the part has a child model (is the parent of another model), and there
	 * 	        is at least one child part which references it, a warning will be displayed informing the user as such.
	 * 	        The user can opt to continue anyway, if so desired. If they choose to, the child parts will be deleted
	 * 	        before deleting the parent part.
	 *     </li>
	 *     <li>If the part is not a parent, it is simply deleted.</li>
	 *     <li>
	 *         If the part has grandchildren, an error is displayed to the user, and this function will exit
	 *         {@code false}. Removing grandchild objects could have massive unintended consequences and is thus
	 *         explicitly unsupported.
	 *     </li>
	 * </ul>
	 *
	 * @param selectedPart The part to delete.
	 * @return {@code true} if the deletion was successful, {@code false} otherwise.
	 */
	private boolean safelyDeletePart(@NotNull CallNumberPart selectedPart) {
		// This part can have children
		if (selectedPart.hasChild()) {
			log.debug("Parent {} can have children", selectedPart);
			
			var children = ((HasChildPart<?>) selectedPart).getChildCollection();
			int countChildren = children.size();
			String parentTypeName = selectedPart.getClass().getSimpleName().toLowerCase();
			var childClass = ((HasChildPart<?>) selectedPart).getChildClass();
			String childTypeName = childClass.getSimpleName().toLowerCase();

			// This part has associated children
			if (countChildren > 0) {
				log.info("Selected parent part '{}' has at least one linked child", selectedPart.formatString());
				// Multi-level relationships aren't supported yet.
				if (children.stream().anyMatch(c -> (
						c.hasChild() && ((HasChildPart<?>) c).countChildren() > 0)
				)) {
					log.warn("User tried to delete parent part '{}' that has grandchildren, which is not allowed.", selectedPart.formatString());
					// noinspection ExtractMethodRecommender
					Alert doubleRecursiveChildrenAlert = new Alert(Alert.AlertType.ERROR);
					doubleRecursiveChildrenAlert.setHeaderText(null);
					doubleRecursiveChildrenAlert.setTitle("Deletion error");
					doubleRecursiveChildrenAlert.setContentText("This part has at least one child which itself has " +
							                                            "at least one child. Deleting multi-level" +
							                                            "relationships like these are not currently" +
							                                            "supported. Please delete the grandchild parts" +
							                                            "first and try again.");
					doubleRecursiveChildrenAlert.showAndWait();
					return false;
				}
				
				// Prompt the user
				Alert warningUnsafeDelete = new Alert(
						Alert.AlertType.WARNING,
						String.format(
								"There %s still %d %s which reference%s this %s. Deleting this %s would " +
										"also delete %s %s. Are you sure you want to delete this %s?",
								(countChildren > 1 ? "are" : "is"),  // %s - is/are
								countChildren,  // %d
								childTypeName + (countChildren > 1 ? "s" : ""),  // %s - plural
								(countChildren > 1 ? "" : "s"),  // %s - plural of reference(s)
								parentTypeName,  // %s
								parentTypeName,  // %s
								(countChildren > 1 ? "those" : "that"),  // %s - plural
								childTypeName + (countChildren > 1 ? "s" : ""),  // %s - plural
								parentTypeName  // %s
								
						),
						ButtonType.OK, ButtonType.CANCEL
				);
				warningUnsafeDelete.setTitle("Warning: Unsafe Delete");
				warningUnsafeDelete.setHeaderText(String.format("Deletion of %s '%s'", StringUtils.capitalize(parentTypeName), selectedPart.formatString()));
				
				Optional<ButtonType> result = warningUnsafeDelete.showAndWait();
				
			// 	User understands the implications and chooses to delete anyway
				if (result.isPresent() && result.get() == ButtonType.OK) {
					log.info("Attempting to cascade-delete {} '{}'", selectedPart.getClass().getSimpleName(), selectedPart.formatString());
					@SuppressWarnings("unchecked")
					Dao<CallNumberPart, Long> parentDao = (Dao<CallNumberPart, Long>) dbManager.getDao(selectedPart.getClass());
					
					try {
						String formattedParentName = selectedPart.formatString();
						// First, delete children
						children.clear();
						// Then, delete the parent
						parentDao.delete(selectedPart);
						
						log.info("Cascade-delete of {} succeeded", selectedPart);
						
						Alert recursiveDeleteSuccess = new Alert(Alert.AlertType.INFORMATION);
						recursiveDeleteSuccess.setTitle(StringUtils.capitalize(parentTypeName) + " deletion success");
						recursiveDeleteSuccess.setHeaderText(null);
						recursiveDeleteSuccess.setContentText(String.format(
								"Successfully deleted %s '%s' and %d %s which referenced it.",
								parentTypeName,
								formattedParentName,
								countChildren,
								childTypeName + (countChildren > 1 ? "s" : "")
						));
						recursiveDeleteSuccess.showAndWait();
						return true;
						
					} catch (SQLException e) {
						log.error("Failed to delete the parent: {}", selectedPart, e);
						GuiTools.displayJavaExceptionAlert("Failed to delete the parent object.", e);
						return false;
					}
					
			//  User backs out
				} else {
					log.info("User backed out of cascade-delete of {}", selectedPart);
					Alert cancelUnsafeAlert = new Alert(Alert.AlertType.INFORMATION);
					cancelUnsafeAlert.setHeaderText(null);
					cancelUnsafeAlert.setTitle(StringUtils.capitalize(parentTypeName) + " deletion cancelled");
					cancelUnsafeAlert.setContentText(StringUtils.capitalize(parentTypeName) + " deletion cancelled.");
					cancelUnsafeAlert.showAndWait();
					return false;
				}
				
		//  There are no associated children
			} else {
				return simpleDeletePart(selectedPart, false);
			}
		} else {
			return simpleDeletePart(selectedPart, true);
		}
	}
	
	
	/**
	 * Naively delete the part, without checking relationships. Assumes that the {@code selectedPart} either doesn't
	 * have a child model or (if it does) no child actively relates to this part. This should not be run directly; use
	 * {@link InventoryManagerController#safelyDeletePart(CallNumberPart)} instead.
	 *
	 * @param selectedPart Must either lack a child model or lack any referenced children.
	 * @param prompt Whether the user will be prompted to delete.
	 * @return {@code true} if the deletion was successful, {@code false} otherwise.
	 */
	private boolean simpleDeletePart(@NotNull CallNumberPart selectedPart, boolean prompt) {
		assert (
				!selectedPart.hasChild()
				|| ((HasChildPart<?>) selectedPart).getChildCollection().stream().noneMatch(c -> (
						c.hasChild() && ((HasChildPart<?>) c).countChildren() > 0)
				)
		);
		
		if (prompt) {
			
			String selectedTypeName = selectedPart.getClass().getSimpleName().toLowerCase();
			
			Alert simpleDeletionAlert = new Alert(Alert.AlertType.CONFIRMATION);
			simpleDeletionAlert.setHeaderText(null);
			simpleDeletionAlert.setTitle(StringUtils.capitalize(selectedTypeName) + " deletion");
			simpleDeletionAlert.setContentText(String.format(
					"Delete %s '%s'?",
					selectedTypeName,
					selectedPart.formatString()
			));
			
			Optional<ButtonType> result = simpleDeletionAlert.showAndWait();
			
			// Confirmed
			if (result.isPresent() && result.get() == ButtonType.OK) {
				@SuppressWarnings("unchecked")
				Dao<CallNumberPart, Long> parentDao = (Dao<CallNumberPart, Long>) dbManager.getDao(
						selectedPart.getClass());
				
				log.info("Attempting simple deletion (with-prompt) of {}", selectedPart);
				
				try {
					String formattedParentName = selectedPart.formatString();
					String selToStr = selectedPart.toString();
					
					// delete the thing
					parentDao.delete(selectedPart);
					
					log.info("Successfully deleted (with-prompt) {}", selToStr);
					Alert simpleDeleteSuccess = new Alert(Alert.AlertType.INFORMATION);
					simpleDeleteSuccess.setTitle(StringUtils.capitalize(selectedTypeName) + " deletion success");
					simpleDeleteSuccess.setHeaderText(null);
					simpleDeleteSuccess.setContentText(String.format(
							"Successfully deleted %s '%s'.",
							selectedTypeName,
							formattedParentName
					));
					simpleDeleteSuccess.showAndWait();
					return true;
					
					
				} catch (SQLException e) {
					log.error("Failed to delete (with-prompt) the parent: {}", selectedPart, e);
					GuiTools.displayJavaExceptionAlert("Failed to delete the parent object.", e);
					return false;
				}
				
				//  User backs out
			} else {
				log.info("User backed out of simple delete (with-prompt) of {}", selectedPart);
				Alert cancelUnsafeAlert = new Alert(Alert.AlertType.INFORMATION);
				cancelUnsafeAlert.setHeaderText(null);
				cancelUnsafeAlert.setTitle(StringUtils.capitalize(selectedTypeName) + " deletion cancelled");
				cancelUnsafeAlert.setContentText(StringUtils.capitalize(selectedTypeName) + " deletion cancelled.");
				cancelUnsafeAlert.showAndWait();
				return false;
			}
			
		// no prompt
		} else {
			log.info("Attempting simple deletion (no-prompt) of {}", selectedPart);
			try {
				@SuppressWarnings("unchecked")
				Dao<CallNumberPart, Long> parentDao = (Dao<CallNumberPart, Long>) dbManager.getDao(selectedPart.getClass());
				parentDao.delete(selectedPart);
				String selToStr = selectedPart.toString();
				log.info("Successfully deleted (no-prompt) {}", selToStr);
				return true;
			} catch (SQLException e) {
				log.error("Failed to delete (no-prompt) the parent: {}", selectedPart, e);
				GuiTools.displayJavaExceptionAlert("Failed to delete the parent object.", e);
				return false;
			}
		}
	}
	
	
	/**
	 * Sort of clear the editor pane. The actual fields themselves stay filled with the last content.
	 * This basically exists just to clear some variables.
 	 */
	private void clearEditPane() {
		creatingNewPart = false;
		
		parentPart = null;
		labelEditCallNumberPart.setText("Edit part");
		log.info("Deselected part");
	}
	
	
	private void loadSubjects() {
		try {
			var subjects = dbManager.subjects.queryForAll();
			allSubjects.set(FXCollections.observableList(subjects));
		} catch (SQLException e) {
			log.error("Failed to get subjects", e);
			// listSubject.setItems(null);
			allSubjects.clear();
		}
	}
	
	private void loadDomains(@Nullable Subject parentSubject) {
		if (parentSubject == null) {
			allDomains.clear();
		} else {
			try {
				QueryBuilder<Domain, Long> queryBuilder = dbManager.domains.queryBuilder();
				queryBuilder.where().eq(Domain.FIELD_NAME_SUBJECT, parentSubject.getId());
				PreparedQuery<Domain> preparedQuery = queryBuilder.prepare();
				ObservableList<Domain> items = FXCollections.observableList(dbManager.domains.query(preparedQuery));
				allDomains.set(items);
				
			} catch (SQLException e) {
				log.error("Failed to query domains with parent subject id '{}'", parentSubject.getId(), e);
				// listDomain.setItems(null);
				allDomains.clear();
			}
		}
	}
	
	private void loadRoots() {
		try {
			var roots = dbManager.roots.queryForAll();
			allRoots.set(FXCollections.observableList(roots));

		} catch (SQLException e) {
			log.error("Failed to get roots", e);
			allRoots.clear();
		}
	}
	
	private void loadAspects(@Nullable Root parentRoot) {
		if (parentRoot == null) {
			allAspects.clear();
		} else {
			try {
				QueryBuilder<Aspect, Long> queryBuilder = dbManager.aspects.queryBuilder();
				queryBuilder.where().eq(Aspect.FIELD_NAME_ROOT, parentRoot.getId());
				PreparedQuery<Aspect> preparedQuery = queryBuilder.prepare();
				ObservableList<Aspect> items = FXCollections.observableList(dbManager.aspects.query(preparedQuery));
				allAspects.set(items);

			} catch (SQLException e) {
				log.error("Failed to query aspects with parent root id '{}'", parentRoot.getId(), e);
				allAspects.clear();
			}
		}
	}
	
	private void loadTopics(@Nullable Aspect parentAspect) {
		if (parentAspect == null) {
			allTopics.clear();
		} else {
			try {
				QueryBuilder<Topic, Long> queryBuilder = dbManager.topics.queryBuilder();
				queryBuilder.where().eq(Topic.FIELD_NAME_ASPECT, parentAspect.getId());
				PreparedQuery<Topic> preparedQuery = queryBuilder.prepare();
				ObservableList<Topic> items = FXCollections.observableList(dbManager.topics.query(preparedQuery));
				allTopics.set(items);

			} catch (SQLException e) {
				log.error("Failed to query topics with parent aspect id '{}'", parentAspect.getId(), e);
				allTopics.clear();
			}
		}
	}
	
	private void loadAuthorPublishers() {
		try {
			var authorPublishers = dbManager.authorPublishers.queryForAll();
			allAuthorPublishers.set(FXCollections.observableList(authorPublishers));
		} catch (SQLException e) {
			log.error("Failed to get authors/publishers", e);
			allAuthorPublishers.clear();
		}
	}
	
	
	
	// LIST CLICK HANDLERS //
	
	@FXML
	private void onMouseClickListSubject(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		Subject selectedSubject = listSubject.getSelectionModel().getSelectedItem();
		log.debug("Selected subject: {}", selectedSubject);
		if (selectedSubject == null) {
			return;
		}
		
		selectPartForEditing(selectedSubject);
		// this func also sets `selectedPart`
		
		loadDomains(selectedSubject);
	}
	
	@FXML
	private void onMouseClickListDomain(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		Domain selectedDomain = listDomain.getSelectionModel().getSelectedItem();
		log.debug("Selected domain: {}", selectedDomain);
		if (selectedDomain == null) {
			return;
		}
		
		parentPart = selectedDomain.getSubject();
		
		selectPartForEditing(selectedDomain);
		// this func also sets `selectedPart`
	}
	
	@FXML
	private void onMouseClickListRoot(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		Root selectedRoot = listRoot.getSelectionModel().getSelectedItem();
		log.debug("Selected root: {}", selectedRoot);
		if (selectedRoot == null) {
			return;
		}
		
		selectPartForEditing(selectedRoot);
		
		loadAspects(selectedRoot);
	}
	
	@FXML
	private void onMouseClickListAspect(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		Aspect selectedAspect = listAspect.getSelectionModel().getSelectedItem();
		log.debug("Selected aspect: {}", selectedAspect);
		if (selectedAspect == null) {
			return;
		}
		
		selectPartForEditing(selectedAspect);
		
		loadTopics(selectedAspect);
	}
	
	@FXML
	private void onMouseClickListTopic(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		Topic selectedTopic = listTopic.getSelectionModel().getSelectedItem();
		log.debug("Selected topic: {}", selectedTopic);
		if (selectedTopic == null) {
			return;
		}
		
		selectPartForEditing(selectedTopic);
	}
	
	@FXML
	private void onMouseClickListAuthorPublisher(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		AuthorPublisher selectedAuthorPublisher = listAuthorPublisher.getSelectionModel().getSelectedItem();
		log.debug("Selected author/publisher: {}", selectedAuthorPublisher);
		if (selectedAuthorPublisher == null) {
			return;
		}
		
		selectPartForEditing(selectedAuthorPublisher);
	}
	
	
	// BUTTON HANDLERS //
	
	// Create
	
	@FXML
	private void onClickButtonNewSubject(ActionEvent event) {
		event.consume();
		log.debug("Button clicked: new subject");
		
		prepareEditPaneForNewPart(null, Subject.class);
		
		selectedPartNameField.requestFocus();
	}

	@FXML
	private void onClickButtonNewDomain(ActionEvent event) {
		event.consume();
		log.debug("Button clicked: new domain");
		
		prepareEditPaneForNewPart(listSubject.getSelectionModel().getSelectedItem(), Domain.class);
		
		selectedPartNameField.requestFocus();
	}
	
	@FXML
	private void onClickButtonNewRoot(ActionEvent event) {
		event.consume();
		log.debug("Button clicked: new root");
		
		prepareEditPaneForNewPart(null, Root.class);
		
		selectedPartNameField.requestFocus();
	}
	
	@FXML
	private void onClickButtonNewAspect(ActionEvent event) {
		event.consume();
		log.debug("Button clicked: new aspect");
		
		prepareEditPaneForNewPart(listRoot.getSelectionModel().getSelectedItem(), Aspect.class);
		
		selectedPartNameField.requestFocus();
	}
	
	@FXML
	private void onClickButtonNewTopic(ActionEvent event) {
		event.consume();
		log.debug("Button clicked: new topic");
		
		prepareEditPaneForNewPart(listAspect.getSelectionModel().getSelectedItem(), Topic.class);
		
		selectedPartNameField.requestFocus();
	}
	
	@FXML
	private void onClickButtonNewAuthorPublisher(ActionEvent event) {
		event.consume();
		log.debug("Button clicked: new author/publisher");
		
		prepareEditPaneForNewPart(null, AuthorPublisher.class);
	}
	
	
	// Save
	
	@FXML
	private void onClickButtonSave(ActionEvent event) {
		log.debug("Button clicked: save");
		event.consume();
		
		saveAndReloadPartList();
	}
	
	@FXML
	private void onEnterKeyPressedInEditFields(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ENTER)) {
			log.debug("Key pressed in edit fields: save");
			keyEvent.consume();
			saveAndReloadPartList();
		}
	}
	
	
	// Delete
	
	@FXML
	private void onClickButtonDeleteSubject(ActionEvent event) {
		log.debug("Button clicked: delete subject");
		event.consume();
		
		@Nullable Subject selectedSubject = listSubject.getSelectionModel().getSelectedItem();
		if (selectedSubject == null) {
			return;
		}
		
		if (safelyDeletePart(selectedSubject)) {
			loadSubjects();
			loadDomains(listSubject.getSelectionModel().getSelectedItem());
		}
	}
	
	@FXML
	private void onClickButtonDeleteDomain(ActionEvent event) {
		log.debug("Button clicked: delete domain");
		event.consume();
		
		@Nullable Domain selectedDomain = listDomain.getSelectionModel().getSelectedItem();
		if (selectedDomain == null) {
			return;
		}
		
		if (safelyDeletePart(selectedDomain)) {
			loadDomains(null);
		}
	}
	
	@FXML
	private void onClickButtonDeleteRoot(ActionEvent event) {
		log.debug("Button clicked: delete root");
		event.consume();
		
		@Nullable Root selectedRoot = listRoot.getSelectionModel().getSelectedItem();
		if (selectedRoot == null) {
			return;
		}
		
		if (safelyDeletePart(selectedRoot)) {
			loadRoots();
			loadAspects(null);
		}
	}
	
	@FXML
	private void onClickButtonDeleteAspect(ActionEvent event) {
		log.debug("Button clicked: delete aspect");
		event.consume();
		
		@Nullable Aspect selectedAspect = listAspect.getSelectionModel().getSelectedItem();
		if (selectedAspect == null) {
			return;
		}
		
		if (safelyDeletePart(selectedAspect)) {
			loadAspects(listRoot.getSelectionModel().getSelectedItem());
			loadTopics(null);
		}
	}
	
	@FXML
	private void onClickButtonDeleteTopic(ActionEvent event) {
		log.debug("Button clicked: delete topic");
		event.consume();
		
		@Nullable Topic selectedTopic = listTopic.getSelectionModel().getSelectedItem();
		if (selectedTopic == null) {
			return;
		}
		
		if (safelyDeletePart(selectedTopic)) {
			loadTopics(listAspect.getSelectionModel().getSelectedItem());
		}
	}
	
	@FXML
	private void onClickButtonDeleteAuthorPublisher(ActionEvent event) {
		log.debug("Button clicked: delete author/publisher");
		event.consume();
		
		@Nullable AuthorPublisher selectedAuthorPublisher = listAuthorPublisher.getSelectionModel().getSelectedItem();
		if (selectedAuthorPublisher == null) {
			return;
		}
		
		if (safelyDeletePart(selectedAuthorPublisher)) {
			loadAuthorPublishers();
		}
	}
}


class CallNumberPartCellFactory<T extends CallNumberPart> implements Callback<ListView<T>, ListCell<T>> {
	@Override
	public ListCell<T> call(ListView<T> listView) {
		return new ListCell<>() {
			@Override
			public void updateItem(T part, boolean empty) {
				super.updateItem(part, empty);
				
				if (empty) {
					setText(null);
				} else if (part != null) {
					setText(part.formatString());
				} else {
					setText("null");
				}
			}
		};
	}
}

