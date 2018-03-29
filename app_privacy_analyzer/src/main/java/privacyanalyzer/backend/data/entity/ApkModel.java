package privacyanalyzer.backend.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity(name = "ApkInfo")
public class ApkModel extends AbstractEntity{
	
	
	@NotNull
	private String packageName;
	
	@NotNull
	private String packageVersionName;
	
	@NotNull
	private String packageVersionCode;
	
	@NotNull
	private String minSDK;
	
	@NotNull
	private String targetSDK;
	
	@NotNull
	@Column(unique = true)
	private String sha256;
	
	@NotNull
	private String isDebuggable;
	
	@NotNull
	private String isAdbBackupEnabled;
	
	@NotNull
	private String appName;
	
	@ManyToOne
	private User user;
	
	/**
	 * 
	 */
	public ApkModel() {
	}
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getPackageVersionName() {
		return packageVersionName;
	}
	public void setPackageVersionName(String packageVersionName) {
		this.packageVersionName = packageVersionName;
	}
	public String getPackageVersionCode() {
		return packageVersionCode;
	}
	public void setPackageVersionCode(String packageVersionCode) {
		this.packageVersionCode = packageVersionCode;
	}
	public String getMinSDK() {
		return minSDK;
	}
	public void setMinSDK(String minSDK) {
		this.minSDK = minSDK;
	}
	public String getTargetSDK() {
		return targetSDK;
	}
	public void setTargetSDK(String targetSDK) {
		this.targetSDK = targetSDK;
	}
	public String getSha256() {
		return sha256;
	}
	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}
	public String getIsDebuggable() {
		return isDebuggable;
	}
	public void setIsDebuggable(String isDebuggable) {
		this.isDebuggable = isDebuggable;
	}
	public String getIsAdbBackupEnabled() {
		return isAdbBackupEnabled;
	}
	public void setIsAdbBackupEnabled(String isAdbBackupEnabled) {
		this.isAdbBackupEnabled = isAdbBackupEnabled;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User addedBy) {
		this.user = addedBy;
	}

	

}
