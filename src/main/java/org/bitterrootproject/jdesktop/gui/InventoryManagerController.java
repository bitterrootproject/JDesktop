package org.bitterrootproject.jdesktop.gui;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.util.Callback;
import javafx.scene.paint.Color;

import javafx.animation.PauseTransition;
import javafx.util.Duration;


import lombok.extern.slf4j.Slf4j;

import org.bitterrootproject.jdesktop.DatabaseManager;
import org.bitterrootproject.jdesktop.models.*;

import javafx.scene.input.MouseEvent;

import org.jetbrains.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * The JavaFX controller for the call number part inventory manager.
 */
@Slf4j
public class InventoryManagerController implements Initializable {
	public static final URL RESOURCE = InventoryManagerController.class.getResource("InventoryManagerView.fxml");
	
	private DatabaseManager dbManager;
	
	// UPPER HALF: select the part //
	
	// Filter fields
	@FXML
	private TextField filterSubject;
	
	@FXML
	private TextField filterDomain;
	
	@FXML
	private TextField filterRoot;
	
	@FXML
	private TextField filterAspect;
	
	@FXML
	private TextField filterTopic;
	
	@FXML
	private TextField filterAuthorPublisher;
	
	
	// List views
	@FXML
	private ListView<Subject> listSubject;
	
	@FXML
	private ListView<Domain> listDomain;
	
	@FXML
	private ListView<Root> listRoot;
	
	@FXML
	private ListView<Aspect> listAspect;
	
	@FXML
	private ListView<Topic> listTopic;
	
	@FXML
	private ListView<AuthorPublisher> listAuthorPublisher;
	
	
	// New buttons
	@FXML
	private Button buttonNewSubject;
	
	@FXML
	private Button buttonNewDomain;
	
	@FXML
	private Button buttonNewRoot;
	
	@FXML
	private Button buttonNewAspect;
	
	@FXML
	private Button buttonNewTopic;
	
	@FXML
	private Button buttonNewAuthorPublisher;
	
	
	
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
	
	@FXML
	private Button buttonSave;
	
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

		loadSubjects();
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
		
		// If we're creating a new part, we have some extra stuff to do, since we have to create a new part using
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
			listSubject.setItems(FXCollections.observableArrayList(subjects));
		} catch (SQLException e) {
			log.error("Failed to get subjects", e);
			listSubject.setItems(null);
		}
	}
	
	private void loadDomains(@NotNull Subject parentSubject) {
		try {
			QueryBuilder<Domain, Long> queryBuilder = dbManager.domains.queryBuilder();
			queryBuilder.where().eq(Domain.FIELD_NAME_SUBJECT, parentSubject.getId());
			PreparedQuery<Domain> preparedQuery = queryBuilder.prepare();
			ObservableList<Domain> items = FXCollections.observableArrayList(dbManager.domains.query(preparedQuery));
			listDomain.setItems(items);
			
		} catch (SQLException e) {
			log.error("Failed to query domains with parent subject id '{}'", parentSubject, e);
			listDomain.setItems(null);
		}
	}
	
	@FXML
	private void onMouseClickListSubject(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		Subject selectedSubject = listSubject.getSelectionModel().getSelectedItem();
		log.debug("Selected subject: {}", selectedSubject);
		
		selectPartForEditing(selectedSubject);
		// this func also sets `selectedPart`
		
		loadDomains(selectedSubject);
	}
	
	@FXML
	private void onMouseClickListDomain(MouseEvent mouseEvent) {
		mouseEvent.consume();
		
		Domain selectedDomain = listDomain.getSelectionModel().getSelectedItem();
		log.debug("Selected domain: {}", selectedDomain);
		
		Subject subject = selectedDomain.getSubject();
		parentPart = subject;
		
		selectPartForEditing(selectedDomain);
		// this func also sets `selectedPart`
		
		loadDomains(subject);
	}
	
	
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
		
		prepareEditPaneForNewPart(this.selectedPart, Domain.class);
		
		selectedPartNameField.requestFocus();
	}

	@FXML
	private void onClickButtonSave(ActionEvent event) {
		log.debug("Button clicked: save");
		event.consume();
		
		if (!savePartFromEditing()) {
			// Don't reload anything if it didn't successfully save.
			scheduleSaveStatusClear();
			return;
		}
		
		String lastSavedPartClassName = lastSavedPart.getClass().getSimpleName();
		log.debug("Last saved part class: {}", lastSavedPartClassName);
		
		switch (lastSavedPartClassName) {
			case "Subject": {
				log.debug("Decided to reload subjects");
				loadSubjects();
				break;
			}
			case "Domain": {
				Subject subject = ((Domain) lastSavedPart).getSubject();
				log.debug("Decided to reload domains with parent subject_id {}", subject.getId());
				loadDomains(subject);
				break;
			}
			default: break;
		}
		
		clearEditPane();
		
		// Clear labelSaveStatus after a few seconds
		scheduleSaveStatusClear();
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
