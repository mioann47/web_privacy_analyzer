package privacyanalyzer.ui.view.logout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewLeaveAction;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import privacyanalyzer.backend.data.Role;
import privacyanalyzer.backend.service.UploadService;
import privacyanalyzer.ui.MainView;
import privacyanalyzer.ui.navigation.NavigationManager;
import privacyanalyzer.ui.view.about.Customer;
import privacyanalyzer.ui.view.analyze.AnalyzeView;

@SpringView
@Secured(Role.ADMIN)

public class LogoutView extends LogoutViewDesign implements View{
	
	private final NavigationManager navigationManager;
	
	@Autowired
	public LogoutView(NavigationManager navigationManager) {
		this.navigationManager = navigationManager;
		
	}
	
	@PostConstruct
	public void init() {
		navigationManager.navigateToDefaultViewForce();
		MainView.outnow();
		
	}
	
}
