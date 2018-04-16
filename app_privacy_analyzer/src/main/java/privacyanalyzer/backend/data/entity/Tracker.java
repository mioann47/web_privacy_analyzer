package privacyanalyzer.backend.data.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="trackers")
public class Tracker implements Serializable{

	@Id
	@GeneratedValue
	private Long trackersPropertiesId;
	
	private String trackersPropertiesWebsite;
	
	private String trackersPropertiesName;
	
	private String trackersPropertiesCodeSignature;

	/**
	 * 
	 */
	public Tracker() {
	
	}

	/**
	 * @param trackersPropertiesId
	 * @param trackersPropertiesWebsite
	 * @param trackersPropertiesName
	 * @param trackersPropertiesCodeSignature
	 */
	public Tracker(Long trackersPropertiesId, String trackersPropertiesWebsite, String trackersPropertiesName,
			String trackersPropertiesCodeSignature) {
		super();
		this.trackersPropertiesId = trackersPropertiesId;
		this.trackersPropertiesWebsite = trackersPropertiesWebsite;
		this.trackersPropertiesName = trackersPropertiesName;
		this.trackersPropertiesCodeSignature = trackersPropertiesCodeSignature;
	}

	public Long getTrackersPropertiesId() {
		return trackersPropertiesId;
	}

	public void setTrackersPropertiesId(Long trackersPropertiesId) {
		this.trackersPropertiesId = trackersPropertiesId;
	}

	public String getTrackersPropertiesWebsite() {
		return trackersPropertiesWebsite;
	}

	public void setTrackersPropertiesWebsite(String trackersPropertiesWebsite) {
		this.trackersPropertiesWebsite = trackersPropertiesWebsite;
	}

	public String getTrackersPropertiesName() {
		return trackersPropertiesName;
	}

	public void setTrackersPropertiesName(String trackersPropertiesName) {
		this.trackersPropertiesName = trackersPropertiesName;
	}

	public String getTrackersPropertiesCodeSignature() {
		return trackersPropertiesCodeSignature;
	}

	public void setTrackersPropertiesCodeSignature(String trackersPropertiesCodeSignature) {
		this.trackersPropertiesCodeSignature = trackersPropertiesCodeSignature;
	}
	
	
	
	
	
	
	
}
