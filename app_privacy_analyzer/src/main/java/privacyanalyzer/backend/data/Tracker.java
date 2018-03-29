package privacyanalyzer.backend.data;

import java.io.Serializable;

public class Tracker implements Serializable {

	private int id;
	private String website;
	private String name;
	private String code_signature;

	/**
	 * 
	 */
	public Tracker() {
		super();
	}

	/**
	 * @param id
	 * @param website
	 * @param name
	 * @param code_signature
	 */
	public Tracker(int id, String website, String name, String code_signature) {
		super();
		this.id = id;
		this.website = website;
		this.name = name;
		this.code_signature = code_signature;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode_signature() {
		return code_signature;
	}

	public void setCode_signature(String code_signature) {
		this.code_signature = code_signature;
	}

}