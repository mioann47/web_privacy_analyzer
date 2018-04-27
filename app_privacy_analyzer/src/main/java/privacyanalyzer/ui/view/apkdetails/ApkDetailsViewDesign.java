package privacyanalyzer.ui.view.apkdetails;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
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
public class ApkDetailsViewDesign extends VerticalLayout {
	protected Button backButton;
	protected Button analyzeApk;
	protected Label appNameLabel;
	protected Label dateLabel;
	protected Label userLabel;
	protected Image image;
	protected Label statusLabel;
	protected Label sha256;
	protected Label packageName;
	protected Label packageVersionName;
	protected Label packageVersionCode;
	protected Label minSDK;
	protected Label targetSDK;
	protected Grid declaredPermissionsGrid;
	protected Grid notDeclaredButUsedPermissionsGrid;
	protected Grid declaredAndUsedPermissionsGrid1;
	protected Grid declaredAndNotUsedPermissionsGrid11;
	protected Grid libraryPermissions;
	protected Grid callsGrid;
	protected Grid trackersGrid;

	public ApkDetailsViewDesign() {
		Design.read(this);
	}
}
