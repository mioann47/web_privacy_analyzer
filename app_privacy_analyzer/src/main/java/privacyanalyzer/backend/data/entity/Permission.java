package privacyanalyzer.backend.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;


public class Permission implements Serializable{

	 @Id
	 private Long id;
	 
	 @Column(name="permissionName",unique = true)
	 private String permissionName;
	 
	 @Column(name="permissionDesc")
	 private String  permissionDesc;
	 
	 @Column(name="protectionLevel")
	 private String  protectionLevel;
	 
	 @Column(name="permissionValue",unique = true)
	 private String  permissionValue;
	 
	 private String  levelDesc;
	 
	 /*@ManyToMany
	 @JoinTable(name = "apk_permission",joinColumns = @JoinColumn(name="permission_id"),inverseJoinColumns = @JoinColumn(name="apk_id"))
	 private List<ApkModel> apks;*/
	 
	/**
	 * @param id
	 * @param permissionName
	 * @param permissionDesc
	 * @param protectionLevel
	 * @param permissionValue
	 */
	public Permission(Long id, String permissionName, String permissionDesc, String protectionLevel,
			String permissionValue,String levelDesc) {
		super();
		this.id = id;
		this.permissionName = permissionName;
		this.permissionDesc = permissionDesc;
		this.protectionLevel = protectionLevel;
		this.permissionValue = permissionValue;
		this.levelDesc=levelDesc;
	}
	
	public Permission(String permissionValue) {
		super();
		this.id = (long) -1;
		
		this.permissionName =  permissionValue.split("\\.")[permissionValue.split("\\.").length-1];
		this.permissionDesc =  "Unknown permission";
		this.protectionLevel = "No information available";
		this.permissionValue = permissionValue;
		this.levelDesc = "";
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getPermissionDesc() {
		return permissionDesc;
	}
	public void setPermissionDesc(String permissionDesc) {
		this.permissionDesc = permissionDesc;
	}
	public String getProtectionLevel() {
		return protectionLevel;
	}
	public void setProtectionLevel(String protectionLevel) {
		this.protectionLevel = protectionLevel;
	}
	public String getPermissionValue() {
		return permissionValue;
	}
	public void setPermissionValue(String permissionValue) {
		this.permissionValue = permissionValue;
	}

	public String getLevelDesc() {
		return levelDesc;
	}

	public void setLevelDesc(String levelDesc) {
		this.levelDesc = levelDesc;
	}
	
	
	
	
	
}
