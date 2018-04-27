package privacyanalyzer.ui.view.apklist;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.renderers.ImageRenderer;

import privacyanalyzer.backend.ApkRepository;
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.Order;
import privacyanalyzer.ui.navigation.NavigationManager;
import privacyanalyzer.ui.view.analyze.AnalyzeView;
import privacyanalyzer.ui.view.apkdetails.ApkDetailsView;
import privacyanalyzer.ui.view.orderedit.OrderEditView;




@SpringView
public class ApkListView extends ApkListDesign implements View{

	private final NavigationManager navigationManager;
	private final ApkRepository apkRepository;

	@Autowired
	public ApkListView(NavigationManager navigationManager, ApkRepository apkRepository) {
		super();
		this.navigationManager = navigationManager;
		this.apkRepository = apkRepository;
	}



	@PostConstruct
	public void init() {
		
		list.setItems(apkRepository.findAll());
		list.addSelectionListener(e->selectedApk(e.getFirstSelectedItem().get()));
		
		this.searchField.addValueChangeListener(this::onNameFilterTextChange);

		this.analyzeApk.addClickListener(e->goToAnalyzeView());
		
		ImageRenderer<ApkModel> renderer= (ImageRenderer<ApkModel>) list.getColumn("imageColumn").getRenderer();
		renderer.addClickListener(e->selectedApk(e.getItem()));
	}
	
	private void goToAnalyzeView() {
		this.navigationManager.navigateTo(AnalyzeView.class);
	}



	public void selectedApk(ApkModel apkmodel) {
		navigationManager.navigateTo(ApkDetailsView.class, apkmodel.getId());
	}
	
	private void onNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		 ListDataProvider<ApkModel> dataProvider=(ListDataProvider<ApkModel>) list.getDataProvider();
		 dataProvider.setFilter(ApkModel::getAppName, s -> caseInsensitiveContains(s, event.getValue()));
	}
    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }
}
