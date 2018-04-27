package privacyanalyzer.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import privacyanalyzer.backend.data.ApplicationPermissionsModel;
import privacyanalyzer.backend.data.LibraryModel;
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.PermissionMethodCallModel;
import privacyanalyzer.functionalities.APKAnalyzer;
import privacyanalyzer.functionalities.MalwarePrediction;
import privacyanalyzer.ui.util.Paths;
import weka.classifiers.Classifier;

@Service
public class AnalyzeService extends APKAnalyzer implements Serializable{

	private final ApkService apkService;
	private final PermissionService permissionService;
	private final TrackerService trackerService;
	private final PermissionCallsService permissionCallsService;

	@Autowired
	public AnalyzeService(ApkService apkService,PermissionService permissionService,TrackerService trackerService,PermissionCallsService permissionCallsService) {
		this.apkService = apkService;
		this.permissionService=permissionService;
		this.trackerService = trackerService;
		this.permissionCallsService = permissionCallsService;
	}

	public boolean predict(ArrayList<String> permissionList) {
		try {
		Classifier cls = (Classifier) weka.core.SerializationHelper.read(Paths.wekaModelPath);
		MalwarePrediction malpred = new MalwarePrediction(cls, permissionList);
		
		return malpred.predict() == 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean analyzeAndSaveAPK(File file) {
		
		try {
			ApkModel  apkmodel= getApkInformation(file.getAbsolutePath());
			apkService.save(apkmodel);
			
			ApplicationPermissionsModel apm = getAPKPermissions(file.getAbsolutePath());
			permissionService.saveApkPermissions(apm.getDeclared(), apkmodel, "Declared");
			permissionService.saveApkPermissions(apm.getNotRequiredButUsed(), apkmodel, "NotRequiredButUsed");
			permissionService.saveApkPermissions(apm.getRequiredAndUsed(), apkmodel, "RequiredAndUsed");
			permissionService.saveApkPermissions(apm.getRequiredButNotUsed(), apkmodel, "RequiredButNotUsed");
			
			Set<String> hsmalware = new HashSet<>();
			hsmalware.addAll(apm.getDeclared());
			hsmalware.addAll(apm.getNotRequiredButUsed());
			ArrayList<String> malwarePermissionToCheck=new ArrayList<String>();
			malwarePermissionToCheck.addAll(hsmalware);
			apkmodel.setMalware(predict(malwarePermissionToCheck));
			apkService.save(apkmodel);
			
			LibraryModel[] libModels = getLibrariesPermissions(file.getAbsolutePath());
			
		    ArrayList<String> libsPermissions = new ArrayList<String>();
		    
		    for (int i=0;i<libModels.length;i++) {
		    	libsPermissions.addAll(libModels[i].getPermission());
		    }

		    Set<String> hs = new HashSet<>();
		    hs.addAll(libsPermissions);
		    libsPermissions.clear();
		    libsPermissions.addAll(hs);
		    permissionService.saveApkPermissions(libsPermissions, apkmodel, "LibraryPermission");
			trackerService.saveTrackers(libModels, apkmodel);
			
			ArrayList<String> usedpermissionsList = new ArrayList<String>();
			usedpermissionsList.addAll(apm.getRequiredAndUsed());
			usedpermissionsList.addAll(apm.getNotRequiredButUsed());

			
				ArrayList<PermissionMethodCallModel> calllist = getCalls(file.getAbsolutePath(),
						usedpermissionsList);
				permissionCallsService.saveAll(calllist, apkmodel);
			
			//save calls
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
}
