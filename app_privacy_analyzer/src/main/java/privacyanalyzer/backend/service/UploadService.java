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
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.PermissionMethodCallModel;
import privacyanalyzer.functionalities.APKAnalyzer;
import privacyanalyzer.functionalities.MalwarePrediction;
import privacyanalyzer.ui.util.Hash;
import privacyanalyzer.ui.util.Paths;
import privacyanalyzer.ui.view.analyze.AnalyzeView;
import weka.classifiers.Classifier;

@Service
public class UploadService implements Receiver, SucceededListener {

	private final ApkService apkService;
	private final PermissionService permissionService;
	private final AnalyzeService analyzeService;
	private final TrackerService trackerService;
	private final PermissionCallsService permissionCallsService;

	public File file;

	private File directory;
	private AnalyzeView aView;

	@Autowired
	public UploadService(ApkService apkService, PermissionService permissionService, AnalyzeService analyzeService,
			TrackerService trackerService, PermissionCallsService permissionCallsService) {
		this.apkService = apkService;
		this.permissionService = permissionService;
		this.analyzeService = analyzeService;
		this.trackerService = trackerService;
		this.permissionCallsService = permissionCallsService;
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
		// APKAnalyzer apkanalyzer = new APKAnalyzer();
		String sha256;
		ApkModel apkmodel;
		ApkModel loaded;
		boolean apkexists;
		try {
			sha256 = Hash.SHA256.getHash(file);

			loaded = apkService.getRepository().findBySha256(sha256);
			if (loaded == null) {

				apkexists = false;
				apkmodel = analyzeService.getApkInformation(file.getAbsolutePath());
			} else {
				apkexists = true;
				apkmodel = loaded;
			}

			if (apkexists) {
				System.out.println("APK already exists");

				// loaded=apkService.getRepository().checkIfExists(apkmodel).get(0);
			} else {
				apkService.save(apkmodel);
				System.out.println("Adding APK information to DB");
			}
			// System.out.println(apkmodel.toString());
			aView.getProgressBar().setVisible(false);
			aView.setInfo(apkmodel);

			ApplicationPermissionsModel apm = null;
			if (!apkexists) {
				apm = analyzeService.getAPKPermissions(file.getAbsolutePath());
				permissionService.saveApkPermissions(apm.getDeclared(), apkmodel, "Declared");
				permissionService.saveApkPermissions(apm.getNotRequiredButUsed(), apkmodel, "NotRequiredButUsed");
				permissionService.saveApkPermissions(apm.getRequiredAndUsed(), apkmodel, "RequiredAndUsed");
				permissionService.saveApkPermissions(apm.getRequiredButNotUsed(), apkmodel, "RequiredButNotUsed");

			} 
				//System.out.println(permissionService.getApkPermissionAssociationRepository().findAllPermissionsByApkModelAndPermissionType(apkmodel, "Declared"));
				permissionService.setGridbyPermissions(
						permissionService.getApkPermissionAssociationRepository()
								.findAllPermissionsByApkModelAndPermissionType(apkmodel, "Declared"),
						aView.getDeclaredPermissionsGrid());

				permissionService.setGridbyPermissions(
						permissionService.getApkPermissionAssociationRepository()
								.findAllPermissionsByApkModelAndPermissionType(apkmodel, "NotRequiredButUsed"),
						aView.getNotDeclaredButUsedPermissionsGrid());

				permissionService.setGridbyPermissions(
						permissionService.getApkPermissionAssociationRepository()
								.findAllPermissionsByApkModelAndPermissionType(apkmodel, "RequiredAndUsed"),
						aView.getDeclaredAndUsedPermissionsGrid());

				permissionService.setGridbyPermissions(
						permissionService.getApkPermissionAssociationRepository()
								.findAllPermissionsByApkModelAndPermissionType(apkmodel, "RequiredButNotUsed"),
						aView.getDeclaredAndNotUsedPermissionsGrid());

			

			// System.out.println(analyzeService.predict(apm.getDeclared()));
			if (!apkexists) {
				Classifier cls = (Classifier) weka.core.SerializationHelper.read(Paths.wekaModelPath);
				MalwarePrediction malpred = new MalwarePrediction(cls, apm.getDeclared());
				if (malpred.predict() == 1) {
					apkmodel.setMalware(true);

					apkService.save(apkmodel);

				} else {
					apkmodel.setMalware(false);

					apkService.save(apkmodel);

				}
			}

			if (apkmodel.isMalware()) {
				aView.getMalwareLabel().setValue("APK might be a MALWARE");
			} else {
				aView.getMalwareLabel().setValue("No malicous activity detected");
			}

			aView.getMalwareLabel().setVisible(true);

			// System.out.println(apm.getDeclared().toString());

			// for (int i = 0; i < libModels.length; i++) {
			// System.out.println(libModels[i].getLibrary());
			// }
			if (!apkexists) {
				LibraryModel[] libModels = analyzeService.getLibrariesPermissions(file.getAbsolutePath());
				trackerService.saveTrackers(libModels, apkmodel);
			}

			trackerService.setGrid(apkmodel, aView.getTrackersGrid());

			if (!apkexists) {
			ArrayList<String> usedpermissionsList = new ArrayList<String>();
			usedpermissionsList.addAll(apm.getRequiredAndUsed());
			usedpermissionsList.addAll(apm.getNotRequiredButUsed());

			
				ArrayList<PermissionMethodCallModel> calllist = analyzeService.getCalls(file.getAbsolutePath(),
						usedpermissionsList);
				permissionCallsService.saveAll(calllist, apkmodel);
			}

			permissionCallsService.setGrid(apkmodel, aView.getCallsGrid());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file.delete();

	}

}
