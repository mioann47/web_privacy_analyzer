package privacyanalyzer.backend.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Grid;

import privacyanalyzer.backend.TrackerRepository;
import privacyanalyzer.backend.data.LibraryModel;
import privacyanalyzer.backend.data.PermissionMethodCallModel;
import privacyanalyzer.backend.data.entity.Permission;
import privacyanalyzer.backend.data.entity.Tracker;

@Service
public class TrackerService implements Serializable {

	

	
	private List<Tracker> trackerList;
	
	private final TrackerRepository trackerRepository;
	
	@Autowired
	public TrackerService(TrackerRepository trackerRepository) {
		this.trackerRepository=trackerRepository;
		getAllTrackers();
	}
	


	
	private void getAllTrackers() {
		trackerList=trackerRepository.findAll();

	}

	public List<Tracker> getTrackerList() {
		return trackerList;
	}

	public void setTrackerList(ArrayList<Tracker> trackerList) {
		this.trackerList = trackerList;
	}

	
	public Tracker exists(String name) {
		
		for(Tracker t:getTrackerList()) {
			
			if (t.getTrackersPropertiesName().equalsIgnoreCase(name)) {
				return t;
			}
			
		}
		
		
		return null;
	}
	
	public void setGrid(LibraryModel[] libModels, Grid<Tracker> grid) {
		ArrayList<Tracker> mylist=new ArrayList<Tracker>();
		
		for (int i = 0; i < libModels.length; i++) {
			/*System.out.println(libModels[i].getLibrary());*/
			
			Tracker t=exists(libModels[i].getLibrary());
			if (t!=null) {
				mylist.add(t);
			}
			
		}
		
		grid.removeAllColumns();
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addColumn(Tracker::getTrackersPropertiesName).setCaption("Name");
		grid.addColumn(Tracker::getTrackersPropertiesWebsite).setCaption("Website");
		grid.setItems(mylist);
		grid.setWidth("100%");

		if (mylist.size() == 0) {
			grid.setHeightByRows(1);
		} else if (mylist.size() >= 10) {
			grid.setHeightByRows(10);
		} else {
			grid.setHeightByRows(mylist.size());
		}
		grid.setVisible(true);
	}
	
	
	public void setGrid(List<PermissionMethodCallModel> list, Grid<PermissionMethodCallModel> grid) {

		
		grid.removeAllColumns();
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addColumn(PermissionMethodCallModel::getPermissionName).setCaption("Permission Name");
		grid.addColumn(PermissionMethodCallModel::getCallerFunction).setCaption("Caller Function (package -> function)");
		grid.addColumn(PermissionMethodCallModel::getPermissionFunction).setCaption("Permission Function (package -> function)");
		grid.setItems(list);
		grid.setWidth("100%");

		if (list.size() == 0) {
			grid.setHeightByRows(1);
		} else if (list.size() >= 10) {
			grid.setHeightByRows(10);
		} else {
			grid.setHeightByRows(list.size());
		}
		grid.setVisible(true);
	}

}
