package privacyanalyzer.ui.view.apkdetails;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import privacyanalyzer.backend.ApkRepository;
import privacyanalyzer.backend.PermissionRepository;
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.User;
import privacyanalyzer.backend.service.AnalyzeService;
import privacyanalyzer.backend.service.ApkService;
import privacyanalyzer.backend.service.PermissionCallsService;
import privacyanalyzer.backend.service.PermissionService;
import privacyanalyzer.backend.service.TrackerService;
import privacyanalyzer.ui.navigation.NavigationManager;
import privacyanalyzer.ui.view.analyze.AnalyzeView;
import privacyanalyzer.ui.view.apklist.ApkListView;

@SpringView(name="apk")
public class ApkDetailsView extends ApkDetailsViewDesign implements View{

	
	
	private final NavigationManager navigationManager;
	private final ApkService apkService;
	private final PermissionService permissionService;
	private final AnalyzeService analyzeService;
	private final TrackerService trackerService;
	private final PermissionCallsService permissionCallsService;

	@Autowired
	public ApkDetailsView(NavigationManager navigationManager, ApkService apkService,
			PermissionService permissionService, AnalyzeService analyzeService, TrackerService trackerService,
			PermissionCallsService permissionCallsService) {
		this.navigationManager = navigationManager;
		this.apkService = apkService;
		this.permissionService = permissionService;
		this.analyzeService = analyzeService;
		this.trackerService = trackerService;
		this.permissionCallsService = permissionCallsService;
	}
	
	@PostConstruct
	public void init() {
		this.backButton.addClickListener(e->goBack());
		this.analyzeApk.addClickListener(e->goToAnalyzeView());
	}
	
	private void goBack() {
		// TODO Auto-generated method stub
		navigationManager.navigateTo(ApkListView.class);
	}

	private void goToAnalyzeView() {
		this.navigationManager.navigateTo(AnalyzeView.class);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		String apkId = event.getParameters();
		if ("".equals(apkId)) {
			enterView(null);
		} else {
			try {
			Long id=Long.parseLong(apkId);
			if (Long.toString(id).equalsIgnoreCase(apkId)) {
			enterView(id);}
			else enterView(null);
		
			}catch(NumberFormatException e) {
				enterView(null);
			}
		}
	}

	private void enterView(Long id) {
		ApkModel apkmodel;
		if (id==null) {
			showNotFound();
			return;
		}
		apkmodel=this.apkService.getRepository().findById(id);
		if (apkmodel==null) {
			showNotFound();
			return;
		}
		refreshView(apkmodel);
		
		
	}
	
	private void refreshView(ApkModel apkmodel) {
		this.appNameLabel.setValue(apkmodel.getAppName());
		this.dateLabel.setValue(apkmodel.getDateString()+" "+apkmodel.getTimeString());
		User u =apkmodel.getUser();
		if (u==null) this.userLabel.setValue("Guest User");
		else this.userLabel.setValue(u.getName());
		this.packageName.setValue("Package Name: "+apkmodel.getPackageName());
		this.packageVersionCode.setValue("Package Version Code: "+apkmodel.getPackageVersionCode());
		this.packageVersionName.setValue("Package Version Name: "+apkmodel.getPackageVersionName());
		this.minSDK.setValue("Min SDK: "+apkmodel.getMinSDK());
		this.targetSDK.setValue("Target SDK: "+apkmodel.getTargetSDK());
		this.sha256.setValue(apkmodel.getSha256());
		
		setStatus(apkmodel);
		
		permissionService.setGridbyPermissions(
				permissionService.getApkPermissionAssociationRepository()
						.findAllPermissionsByApkModelAndPermissionType(apkmodel, "Declared"),
				this.declaredPermissionsGrid);

		permissionService.setGridbyPermissions(
				permissionService.getApkPermissionAssociationRepository()
						.findAllPermissionsByApkModelAndPermissionType(apkmodel, "NotRequiredButUsed"),
				this.notDeclaredButUsedPermissionsGrid);

		permissionService.setGridbyPermissions(
				permissionService.getApkPermissionAssociationRepository()
						.findAllPermissionsByApkModelAndPermissionType(apkmodel, "RequiredAndUsed"),
				this.declaredAndUsedPermissionsGrid1);

		permissionService.setGridbyPermissions(
				permissionService.getApkPermissionAssociationRepository()
						.findAllPermissionsByApkModelAndPermissionType(apkmodel, "RequiredButNotUsed"),
				this.declaredAndNotUsedPermissionsGrid11);
		
		permissionService.setGridbyPermissions(
				permissionService.getApkPermissionAssociationRepository()
						.findAllPermissionsByApkModelAndPermissionType(apkmodel, "LibraryPermission"),
				this.libraryPermissions);
		
		permissionCallsService.setGrid(apkmodel, this.callsGrid);
		
		trackerService.setGrid(apkmodel, this.trackersGrid);
		
	}
	
	private void showNotFound() {
		removeAllComponents();
		HorizontalLayout hl=new HorizontalLayout();
		Label notFound=new Label("Apk not found");
		hl.setWidth("100%");
		
		hl.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		hl.addComponent(notFound);
		addComponent(hl);
	}
	
	private void setStatus(ApkModel apkmodel) {
		boolean mal=apkmodel.isMalware();
		String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
		FileResource resource;
		if (mal) {
			resource = new FileResource(new File(basepath +
	                "/VAADIN/images/malware.png"));
			
		}else {
			resource = new FileResource(new File(basepath +
	                "/VAADIN/images/clean.png"));
			
		}
		this.image.setSource(resource);
		image.setWidth(100, Unit.PIXELS);
		image.setHeight(100, Unit.PIXELS);
	    image.setVisible(true);
	}
	
	
	
	
}
