package privacyanalyzer.ui.view.analyze;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;

import privacyanalyzer.backend.data.PermissionMethodCallModel;
import privacyanalyzer.backend.data.Role;
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.Permission;
import privacyanalyzer.backend.data.entity.Tracker;
import privacyanalyzer.backend.service.PermissionService;
import privacyanalyzer.backend.service.TrackerService;
import privacyanalyzer.backend.service.UploadService;
import privacyanalyzer.ui.navigation.NavigationManager;
import privacyanalyzer.ui.view.about.AboutView;

@SpringView
public class AnalyzeView extends AnalyzeViewDesign implements View{
	
	@Autowired
	public PermissionService permissionService;
	
	@Autowired
	public TrackerService trackerService;
	
	private final NavigationManager navigationManager;
	private final UploadService uploadService;
	private ApkModel apkModel;
	private Binder<ApkModel> binder = new Binder<>(ApkModel.class);
	
	
	@Autowired
	public AnalyzeView(NavigationManager navigationManager,UploadService uploadService) {
		this.navigationManager = navigationManager;
		this.uploadService=uploadService;
		uploadService.setView(this);
	}
	
	@PostConstruct
	public void init() {
		getMalwareLabel().setVisible(false);
		declaredPermissionsGrid.setVisible(false);
		notDeclaredButUsedPermissionsGrid.setVisible(false);
		declaredAndNotUsedPermissionsGrid11.setVisible(false);
		declaredAndUsedPermissionsGrid1.setVisible(false);
		trackersGrid.setVisible(false);
		callsGrid.setVisible(false);
		progressBar.setVisible(false);
		showApkInformation(false);
		setWidth("100%");
		upload.setReceiver(uploadService);
		upload.addSucceededListener(uploadService);
		binder.bind(nameField, ApkModel::getAppName, ApkModel::setAppName);
		binder.bind(packageNameField, ApkModel::getPackageName, ApkModel::setPackageName);
		binder.bind(packageVersionNameField, ApkModel::getPackageVersionName, ApkModel::setPackageVersionName);
		binder.bind(packageVersionCodeField, ApkModel::getPackageVersionCode,ApkModel::setPackageVersionCode);
		binder.bind(minSDKField, ApkModel::getMinSDK, ApkModel::setMinSDK);
		binder.bind(minSDKField1, ApkModel::getTargetSDK, ApkModel::setTargetSDK);
		binder.bind(sha256Field, ApkModel::getSha256, ApkModel::setSha256);
		
		
		
	}
	public void setInfo(ApkModel model) {
		this.apkModel=model;
		binder.readBean(model);
		showApkInformation(true);
	}
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}
	
	public Grid<Permission> getDeclaredPermissionsGrid() {
		return declaredPermissionsGrid;
	}
	public Grid<Permission> getNotDeclaredButUsedPermissionsGrid() {
		return notDeclaredButUsedPermissionsGrid;
	}
	
	public Grid<Permission> getDeclaredAndUsedPermissionsGrid() {
		return declaredAndUsedPermissionsGrid1;
	}
	
	public Grid<Permission> getDeclaredAndNotUsedPermissionsGrid() {
		return declaredAndNotUsedPermissionsGrid11;
	}
	
	public Grid<Tracker> getTrackersGrid(){
		return trackersGrid;
	}
	
	public Grid<PermissionMethodCallModel> getCallsGrid(){
		return callsGrid;
	}
	
	public Label getMalwareLabel() {
		return malwareStatus;
	}
	
	public void showApkInformation(boolean value) {
		
		nameLabel.setVisible(value);
		nameField.setVisible(value);
		packageName.setVisible(value);
		packageNameField.setVisible(value);
		packageVersionName.setVisible(value);
		packageVersionNameField.setVisible(value);
		packageVersionCode.setVisible(value);
		packageVersionCodeField.setVisible(value);
		minSDK.setVisible(value);
		minSDKField.setVisible(value);
		targetSDK.setVisible(value);
		minSDKField1.setVisible(value);
		sha256.setVisible(value);
		sha256Field.setVisible(value);
		
		
	}
}
