package privacyanalyzer.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import privacyanalyzer.backend.data.ApplicationPermissionsModel;
import privacyanalyzer.backend.data.LibraryModel;
import privacyanalyzer.backend.data.PermissionMethodCallModel;
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.functionalities.APKAnalyzer;
import privacyanalyzer.functionalities.MalwarePrediction;
import privacyanalyzer.ui.util.Paths;
import privacyanalyzer.ui.view.analyze.AnalyzeView;
import weka.classifiers.Classifier;

@Service
public class UploadService implements Receiver, SucceededListener {

	private final ApkService apkService;
	private final PermissionService permissionService;
	private final AnalyzeService analyzeService;
	
	public File file;

	private File directory;
	private AnalyzeView aView;

	@Autowired
	public UploadService(ApkService apkService,PermissionService permissionService,AnalyzeService analyzeService) {
		this.apkService = apkService;
		this.permissionService=permissionService;
		this.analyzeService=analyzeService;
	}

	public void setView(AnalyzeView v) {
		this.aView = v;
	}

	// Implement both receiver that saves upload in a file and
	// listener for successful upload
	public OutputStream receiveUpload(String filename, String mimeType) {
		directory = Paths.theDir;
		// Create and return a file output stream
		aView.getProgressBar().setVisible(true);
		// Create upload stream
		FileOutputStream fos = null; // Stream to write to
		try {
			directory.mkdir();
			// Open the file for writing.
			file = new File(directory.getAbsolutePath() + "\\" + System.currentTimeMillis() + ".apk");
			// file.createNewFile();

			fos = new FileOutputStream(file);
		} catch (final java.io.FileNotFoundException e) {
			new Notification("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return fos; // Return the output stream to write to
	}

	public void uploadSucceeded(SucceededEvent event) {
		// Show the uploaded file in the image viewer
		System.out.println("Analyzing " + file.getAbsolutePath());
		//APKAnalyzer apkanalyzer = new APKAnalyzer();
		ApkModel apkmodel;
		ApkModel loaded;
		boolean apkexists;
		try {
			apkmodel = analyzeService.getApkInformation(file.getAbsolutePath());
			loaded = apkService.getRepository().findBySha256(apkmodel.getSha256());
			if (loaded ==null) apkexists=false;
			else {
				apkexists=true;
				apkmodel=loaded;}
			
			if (apkexists) {
				System.out.println("APK already exists");
				
				//loaded=apkService.getRepository().checkIfExists(apkmodel).get(0);
			} else {
				apkService.save(apkmodel);
				System.out.println("Adding APK information to DB");
			}
			// System.out.println(apkmodel.toString());
			aView.getProgressBar().setVisible(false);
			aView.setInfo(apkmodel);
			ApplicationPermissionsModel apm = analyzeService.getAPKPermissions(file.getAbsolutePath());
			

			if (!apkexists) {
			permissionService.saveApkPermissions(apm.getDeclared(), apkmodel, "Declared");
			permissionService.saveApkPermissions(apm.getNotRequiredButUsed(), apkmodel, "NotRequiredButUsed");
			permissionService.saveApkPermissions(apm.getRequiredAndUsed(), apkmodel, "RequiredAndUsed");
			permissionService.saveApkPermissions(apm.getRequiredButNotUsed(), apkmodel, "RequiredButNotUsed");
			
			
			
			permissionService.setGrid(apm.getDeclared(), aView.getDeclaredPermissionsGrid());
			permissionService.setGrid(apm.getNotRequiredButUsed(), aView.getNotDeclaredButUsedPermissionsGrid());
			permissionService.setGrid(apm.getRequiredAndUsed(), aView.getDeclaredAndUsedPermissionsGrid());
			permissionService.setGrid(apm.getRequiredButNotUsed(), aView.getDeclaredAndNotUsedPermissionsGrid());
			}else {
				System.out.println(permissionService.getApkPermissionAssociationRepository().findAllPermissionsByApkModelAndPermissionType(apkmodel, "Declared"));
			permissionService
			.setGridbyPermissions
			(permissionService.
					getApkPermissionAssociationRepository().
					findAllPermissionsByApkModelAndPermissionType(apkmodel, "Declared"), aView.getDeclaredPermissionsGrid());
			
			
			permissionService
			.setGridbyPermissions
			(permissionService.
					getApkPermissionAssociationRepository().
					findAllPermissionsByApkModelAndPermissionType(apkmodel, "NotRequiredButUsed"), aView.getNotDeclaredButUsedPermissionsGrid());
			
			permissionService
			.setGridbyPermissions
			(permissionService.
					getApkPermissionAssociationRepository().
					findAllPermissionsByApkModelAndPermissionType(apkmodel, "RequiredAndUsed"), aView.getDeclaredAndUsedPermissionsGrid());
			
			permissionService
			.setGridbyPermissions
			(permissionService.
					getApkPermissionAssociationRepository().
					findAllPermissionsByApkModelAndPermissionType(apkmodel, "RequiredButNotUsed"), aView.getDeclaredAndNotUsedPermissionsGrid());
			
			}
			
			
			System.out.println(analyzeService.predict(apm.getDeclared()));
			Classifier cls = (Classifier) weka.core.SerializationHelper.read(Paths.wekaModelPath);
			MalwarePrediction malpred = new MalwarePrediction(cls, apm.getDeclared());
			if (malpred.predict() == 1) {
				apkmodel.setMalware(true);
				if (!apkexists) apkService.save(apkmodel);
				aView.getMalwareLabel().setValue("APK might be a MALWARE");

			} else {
				apkmodel.setMalware(false);
				if (!apkexists) apkService.save(apkmodel);
				aView.getMalwareLabel().setValue("No malicous activity detected");
			}
			aView.getMalwareLabel().setVisible(true);

			
			
			// System.out.println(apm.getDeclared().toString());
			LibraryModel[] libModels = analyzeService.getLibrariesPermissions(file.getAbsolutePath());
			//for (int i = 0; i < libModels.length; i++) {
				//System.out.println(libModels[i].getLibrary());
			//}
			
			aView.trackerService.setGrid(libModels, aView.getTrackersGrid());
			ArrayList<String> usedpermissionsList= new ArrayList<String>();
			usedpermissionsList.addAll(apm.getRequiredAndUsed());
			usedpermissionsList.addAll(apm.getNotRequiredButUsed());
			ArrayList<PermissionMethodCallModel> calllist=analyzeService.getCalls(file.getAbsolutePath(), usedpermissionsList);
			
			aView.trackerService.setGrid(calllist, aView.getCallsGrid());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file.delete();

	}
	

	
}
