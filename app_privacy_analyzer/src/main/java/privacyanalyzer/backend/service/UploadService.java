package privacyanalyzer.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
import privacyanalyzer.functionalities.APKAnalyzer;
import privacyanalyzer.functionalities.MalwarePrediction;
import privacyanalyzer.ui.util.Paths;
import privacyanalyzer.ui.view.analyze.AnalyzeView;
import weka.classifiers.Classifier;

@Service
public class UploadService implements Receiver, SucceededListener {

	private final ApkService apkService;

	public File file;

	private File directory;
	private AnalyzeView aView;

	@Autowired
	public UploadService(ApkService apkService) {
		this.apkService = apkService;
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
		APKAnalyzer apkanalyzer = new APKAnalyzer();
		ApkModel apkmodel;
		try {
			apkmodel = apkanalyzer.getApkInformation(file.getAbsolutePath());

			if (apkService.getRepository().checkIfExists(apkmodel).size() > 0) {
				System.out.println("APK already exists");
			} else {
				apkService.save(apkmodel);
				System.out.println("Adding APK information to DB");
			}
			// System.out.println(apkmodel.toString());
			aView.getProgressBar().setVisible(false);
			aView.setInfo(apkmodel);
			ApplicationPermissionsModel apm = apkanalyzer.getAPKPermissions(file.getAbsolutePath());
			aView.permissionService.setGrid(apm.getDeclared(), aView.getDeclaredPermissionsGrid());
			aView.permissionService.setGrid(apm.getNotRequiredButUsed(), aView.getNotDeclaredButUsedPermissionsGrid());
			aView.permissionService.setGrid(apm.getRequiredAndUsed(), aView.getDeclaredAndUsedPermissionsGrid());
			aView.permissionService.setGrid(apm.getRequiredButNotUsed(), aView.getDeclaredAndNotUsedPermissionsGrid());

			LibraryModel[] libModels = apkanalyzer.getLibrariesPermissions(file.getAbsolutePath());
			//for (int i = 0; i < libModels.length; i++) {
				//System.out.println(libModels[i].getLibrary());
			//}

			Classifier cls = (Classifier) weka.core.SerializationHelper.read(Paths.wekaModelPath);
			MalwarePrediction malpred = new MalwarePrediction(cls, apm.getDeclared());
			if (malpred.predict() == 1) {

				aView.getMalwareLabel().setValue("APK might be a MALWARE");

			} else {

				aView.getMalwareLabel().setValue("No malicous activity detected");
			}
			aView.getMalwareLabel().setVisible(true);

			// System.out.println(apm.getDeclared().toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file.delete();

	}
}
