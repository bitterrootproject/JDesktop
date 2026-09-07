package org.bitterrootproject.jdesktop.gui;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableHeaderRow;
import lombok.extern.slf4j.Slf4j;
import org.bitterrootproject.jdesktop.DatabaseManager;
import org.bitterrootproject.jdesktop.models.CallNumber;
import org.bitterrootproject.jdesktop.models.CallNumberPart;

import java.net.URL;
import java.sql.SQLException;

import java.util.ResourceBundle;


/**
 * JavaFX controller class for the call number table browser.
 */
@Slf4j
public class CallNumberTableController implements Initializable {
	/// The resolved resource URL to the FXML file for this class.
	public static final URL RESOURCE = CallNumberTableController.class.getResource("CallNumberTableView.fxml");
	
	@FXML
	private TableView<CallNumber> callNumberTableView;
	
	@FXML
	private TableColumn<CallNumber, String> columnSubject;
	
	@FXML
	private TableColumn<CallNumber, String> columnDomain;
	
	@FXML
	private TableColumn<CallNumber, String> columnRoot;
	
	@FXML
	private TableColumn<CallNumber, String> columnAspect;
	
	@FXML
	private TableColumn<CallNumber, String> columnTopic;
	
	@FXML
	private TableColumn<CallNumber, String> columnAuthorPublisher;
	
	@FXML
	private TextField filterSubject, filterDomain, filterRoot, filterAspect, filterTopic, filterAuthorPublisher;
	
	private FilteredList<CallNumber> filteredCallNumbers;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Prevent the table's columns from being re-ordered
		callNumberTableView.widthProperty().addListener((
				source,
				oldWidth,
				newWidth
        )-> {
			TableHeaderRow header = (TableHeaderRow) callNumberTableView.lookup("TableHeaderRow");
			header.reorderingProperty().addListener((
					observable,
					oldValue,
					newValue
            ) -> header.setReordering(false));
		});
		
		// Keep the filter boxes aligned with the table columns
		filterSubject.prefWidthProperty().bind(columnSubject.widthProperty().subtract(2));
		filterDomain.prefWidthProperty().bind(columnDomain.widthProperty().subtract(2));
		filterRoot.prefWidthProperty().bind(columnRoot.widthProperty().subtract(2));
		filterAspect.prefWidthProperty().bind(columnAspect.widthProperty().subtract(2));
		filterTopic.prefWidthProperty().bind(columnTopic.widthProperty().subtract(2));
		filterAuthorPublisher.prefWidthProperty().bind(columnAuthorPublisher.widthProperty().subtract(2));
		
		// Set column IDs
		columnSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
		columnDomain.setCellValueFactory(new PropertyValueFactory<>("domain"));
		columnRoot.setCellValueFactory(new PropertyValueFactory<>("root"));
		columnAspect.setCellValueFactory(new PropertyValueFactory<>("aspect"));
		columnTopic.setCellValueFactory(new PropertyValueFactory<>("topic"));
		columnAuthorPublisher.setCellValueFactory(new PropertyValueFactory<>("authorPublisher"));
		
		
		// Load the call numbers into the table.
		try {
			DatabaseManager dbManager = DatabaseManager.getInstance();
			var callNumbers = dbManager.callNumbers.queryForAll();
			
			// Apply the filter
			filteredCallNumbers = new FilteredList<>(FXCollections.observableArrayList(callNumbers), p -> true);
			// Set the items
			callNumberTableView.setItems(filteredCallNumbers);
			
			
			// Watch the filter fields for any changes
			Runnable reapplyFilter = () -> filteredCallNumbers.setPredicate( callNumber -> (
					filterContains(filterSubject.getText(), callNumber.getSubject())
					&& filterContains(filterDomain.getText(), callNumber.getDomain())
					&& filterContains(filterRoot.getText(), callNumber.getRoot())
					&& filterContains(filterAspect.getText(), callNumber.getAspect())
					&& filterContains(filterTopic.getText(), callNumber.getTopic())
					&& filterContains(filterAuthorPublisher.getText(), callNumber.getAuthorPublisher())
			));
			
			filterSubject.textProperty().addListener((o, ov, nv) -> reapplyFilter.run());
			filterDomain.textProperty().addListener((o, ov, nv) -> reapplyFilter.run());
			filterRoot.textProperty().addListener((o, ov, nv) -> reapplyFilter.run());
			filterAspect.textProperty().addListener((o, ov, nv) -> reapplyFilter.run());
			filterTopic.textProperty().addListener((o, ov, nv) -> reapplyFilter.run());
			filterAuthorPublisher.textProperty().addListener((o, ov, nv) -> reapplyFilter.run());
			
			
			
		} catch (SQLException e) {
			log.error("Failed to get call numbers.", e);
			System.exit(1);
		}
	}
	
	
	/**
	 * Check whether this call number part contains the filter text.
	 *
	 * @param filter The filter text
	 * @param callNumberPart The specific call number part to filter
	 * @return {@code true} if the call number part being checked against contains the filter text, {@code false} otherwise
	 */
	private boolean filterContains(String filter, CallNumberPart callNumberPart) {
		String filterText = filter.strip().toLowerCase();
		return filterText.isEmpty()
				|| (
					// Call number part number
					callNumberPart.getNumber().toLowerCase().contains(filterText)
					
					// Call number part name
					|| callNumberPart.getName().toLowerCase().contains(filterText)
				);
	}
}
