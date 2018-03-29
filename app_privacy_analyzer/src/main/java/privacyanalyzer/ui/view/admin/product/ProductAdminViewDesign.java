package privacyanalyzer.ui.view.admin.product;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class ProductAdminViewDesign extends VerticalLayout {
	protected TextField search;
	protected Button add;
	protected CssLayout listParent;
	protected Grid<privacyanalyzer.backend.data.entity.Product> list;
	protected VerticalLayout form;
	protected TextField name;
	protected TextField price;
	protected Button update;
	protected Button cancel;
	protected Button delete;

	public ProductAdminViewDesign() {
		Design.read(this);
	}
}
