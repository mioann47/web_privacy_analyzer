package privacyanalyzer.ui.view.loginRegister;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.navigator.View;

import privacyanalyzer.backend.data.Role;
import privacyanalyzer.ui.MainView;
import privacyanalyzer.ui.navigation.NavigationManager;

@Secured(Role.GUEST)
public class LoginRegisterView extends LoginRegisterDesign implements View{
private final NavigationManager navigationManager;
	
	@Autowired
	public LoginRegisterView(NavigationManager navigationManager) {
		this.navigationManager = navigationManager;
		
	}
	
	@PostConstruct
	public void init() {
		navigationManager.navigateToDefaultViewForce();
		MainView.outnow();
		
	}
}
