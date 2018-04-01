package privacyanalyzer.backend.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Grid;

import privacyanalyzer.backend.data.LibraryModel;
import privacyanalyzer.backend.data.PermissionMethodCallModel;
import privacyanalyzer.backend.data.Tracker;
import privacyanalyzer.backend.data.entity.Permission;

@Service
public class TrackerService implements Serializable {

	private List trackerList;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	private void getAllTrackers() {

		trackerList = jdbcTemplate.query("select * from trackers",
				(rs, rowNum) -> new Tracker(rs.getLong("trackers_properties_id"),
						rs.getString("trackers_properties_website"), rs.getString("trackers_properties_name"),
						rs.getString("trackers_properties_code_signature")));

	}

	public List<Tracker> getTrackerList() {
		return trackerList;
	}

	public void setTrackerList(ArrayList<Tracker> trackerList) {
		this.trackerList = trackerList;
	}

	
	public Tracker exists(String name) {
		
		for(Tracker t:getTrackerList()) {
			
			if (t.getName().equalsIgnoreCase(name)) {
				return t;
			}
			
		}
		
		
		return null;
	}
	
	public void setGrid(LibraryModel[] libModels, Grid<Tracker> grid) {
		ArrayList<Tracker> mylist=new ArrayList<Tracker>();
		
		for (int i = 0; i < libModels.length; i++) {
			/*System.out.println(libModels[i].getLibrary());
			System.out.println(">"+libModels[i].getPackage());
			System.out.println(">"+libModels[i].getStandard_Package());
			System.out.println(">"+libModels[i].getWebsite());
			System.out.println(">"+libModels[i].getMatch_Ratio());
			System.out.println(">"+libModels[i].getType());*/
			
			Tracker t=exists(libModels[i].getLibrary());
			if (t!=null) {
				mylist.add(t);
			}
			
		}
		
		grid.removeAllColumns();
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addColumn(Tracker::getName).setCaption("Name");
		grid.addColumn(Tracker::getWebsite).setCaption("Website");
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
