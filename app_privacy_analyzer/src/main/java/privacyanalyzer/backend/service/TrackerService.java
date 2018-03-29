package privacyanalyzer.backend.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import privacyanalyzer.backend.data.Tracker;

@Service
public class TrackerService implements Serializable{

	private List trackerList;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	private void getAllTrackers() {
		
		trackerList=jdbcTemplate.queryForList("select * from trackers"); 
		
	}
	
	
	
	public List getTrackerList() {
		return trackerList;
	}

	public void setTrackerList(ArrayList<Tracker> trackerList) {
		this.trackerList = trackerList;
	}
	
	
	
	
}
