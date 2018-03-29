package privacyanalyzer.ui.view.about;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import privacyanalyzer.backend.service.UploadService;
import privacyanalyzer.ui.navigation.NavigationManager;

@SpringView
public class AboutView extends AboutViewDesign implements View {
	private final NavigationManager navigationManager;
	
		@Autowired
	 	private CustomerService service;

	    private Customer customer;
	    private Binder<Customer> binder = new Binder<>(Customer.class);

	    private Grid<Customer> grid = new Grid(Customer.class);
	    private TextField firstName = new TextField("First name");
	    private TextField lastName = new TextField("Last name");
	    private Button save = new Button("Save", e -> saveCustomer());
	    
	@Autowired
	public AboutView(NavigationManager navigationManager) {
		this.navigationManager = navigationManager;
		
	}
	
	@PostConstruct
    protected void init() {
        updateGrid();
        grid.setColumns("firstName", "lastName");
        grid.addSelectionListener(e -> updateForm());
        grid.addSelectionListener(event -> {
            Customer c= (Customer) event.getAllSelectedItems().toArray()[0];
            Notification.show(c.getFirstName());
        });
        binder.bindInstanceFields(this);
        VerticalLayout vl= new VerticalLayout(grid,firstName,lastName,save);
        addComponent(vl);
       

    }

    private void updateGrid() {
        List<Customer> customers = service.findAll();
        grid.setItems(customers);
        setFormVisible(false);
    }

    private void updateForm() {
        if (grid.asSingleSelect().isEmpty()) {
            setFormVisible(false);
        } else {
            customer = grid.asSingleSelect().getValue();
            binder.setBean(customer);
            setFormVisible(true);
        }
    }

    private void setFormVisible(boolean visible) {
        firstName.setVisible(visible);
        lastName.setVisible(visible);
        save.setVisible(visible);
    }

    private void saveCustomer() {
        service.update(customer);
        updateGrid();
    }
	
    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {
    }
}