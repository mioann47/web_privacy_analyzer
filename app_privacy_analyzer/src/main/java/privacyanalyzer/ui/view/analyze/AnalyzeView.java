package privacyanalyzer.ui.view.analyze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import privacyanalyzer.backend.data.Role;
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.Permission;
import privacyanalyzer.backend.data.entity.PermissionMethodCallModel;
import privacyanalyzer.backend.data.entity.Tracker;
import privacyanalyzer.backend.service.AnalyzeService;
import privacyanalyzer.backend.service.ApkService;
import privacyanalyzer.backend.service.PermissionService;
import privacyanalyzer.backend.service.TrackerService;

import privacyanalyzer.ui.navigation.NavigationManager;
import privacyanalyzer.ui.util.Hash;
import privacyanalyzer.ui.util.Paths;
import privacyanalyzer.ui.view.about.AboutView;
import privacyanalyzer.ui.view.apkdetails.ApkDetailsView;

@SpringView
public class AnalyzeView extends AnalyzeViewDesign implements View{
	

	

	
	private final NavigationManager navigationManager;
	private final ApkService apkService;
	private final AnalyzeService analyzeService;
	private final Uploader uploader;

	
	@Autowired
	public AnalyzeView(NavigationManager navigationManager, ApkService apkService,AnalyzeService analyzeService) {
		this.navigationManager = navigationManager;
		this.apkService=apkService;
		this.analyzeService=analyzeService;
		uploader=new Uploader();
		
	}
	
	@PostConstruct
	public void init() {
		progressBar.setVisible(false);
		setWidth("100%");
		upload.setReceiver(uploader);
		upload.addSucceededListener(uploader);

	}
	
	
	public void selectedApk(ApkModel apkmodel) {
		navigationManager.navigateTo(ApkDetailsView.class, apkmodel.getId());
	}
	
	class Uploader implements Receiver, SucceededListener {

		public File file;

		private File directory;
		


		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			System.out.println("Uploading file: "+filename);
			if (!mimeType.equalsIgnoreCase("application/vnd.android.package-archive")) {
				new Notification("Wrong file type. Please upload an apk.", Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
				return null;
			}
			directory = Paths.theDir;
			// Create and return a file output stream
			progressBar.setVisible(true);
			// Create upload stream
			FileOutputStream fos = null; // Stream to write to
			try {
				directory.mkdir();
				// Open the file for writing.
				file = new File(directory.getAbsolutePath() + "\\" + System.currentTimeMillis() + ".apk");
				// file.createNewFile();

				fos = new FileOutputStream(file);
			} catch (final java.io.FileNotFoundException e) {
				new Notification("Could not open file", e.getMessage(), Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return fos; // Return the output stream to write to
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			// TODO Auto-generated method stub
			System.out.println("Analyzing "+file.getAbsolutePath());
			String sha256=Hash.SHA256.getHash(file);
			ApkModel apkmodel=apkService.getRepository().findBySha256(sha256);
			if (apkmodel==null) {
				System.out.println("APK not in DB, adding...");
				analyzeService.analyzeAndSaveAPK(file);
			}
			new Notification("Apk uploaded and analyzed!",  Notification.Type.ASSISTIVE_NOTIFICATION)
			.show(Page.getCurrent());
			apkmodel=apkService.getRepository().findBySha256(sha256);
			selectedApk(apkmodel);
			
			file.delete();
		}
		
	}
}
