package privacyanalyzer.backend.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Grid;

import privacyanalyzer.backend.PermissionCallsRepository;
import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.PermissionMethodCallModel;

@Service
public class PermissionCallsService extends MyCrudService<PermissionMethodCallModel> {

	private final PermissionCallsRepository permissionCallsRepository;

	@Autowired
	public PermissionCallsService(PermissionCallsRepository permissionCallsRepository) {

		this.permissionCallsRepository = permissionCallsRepository;
	}

	public void saveAll(List<PermissionMethodCallModel> list,ApkModel apkmodel) {
		
		for (PermissionMethodCallModel pmc:list) {
			pmc.setApk(apkmodel);
			String caller=pmc.getCallerFunction();
			String permissioncall=pmc.getPermissionFunction();
			pmc.setCallerFunction(caller.startsWith("L") ? caller.substring(1) : caller);
			pmc.setPermissionFunction(permissioncall.startsWith("L") ? permissioncall.substring(1) : permissioncall);
			permissionCallsRepository.save(pmc);
		}
	}

	@Override
	protected CrudRepository<PermissionMethodCallModel, Long> getRepository() {
		// TODO Auto-generated method stub
		return this.permissionCallsRepository;
	}

	@Override
	public long countAnyMatching(Optional<String> filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<PermissionMethodCallModel> findAnyMatching(Optional<String> filter, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGrid(ApkModel apkmodel, Grid<PermissionMethodCallModel> grid) {
		List<PermissionMethodCallModel> list=this.permissionCallsRepository.findByApk(apkmodel);
		grid.removeAllColumns();
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addColumn(PermissionMethodCallModel::getName).setCaption("Permission Name");
		grid.addColumn(PermissionMethodCallModel::getCallerFunction)
				.setCaption("Caller Function (package -> function)");
		grid.addColumn(PermissionMethodCallModel::getPermissionFunction)
				.setCaption("Permission Function (package -> function)");
		grid.setItems(list);
		grid.setWidth("100%");

		if (list.size() == 0) {
			grid.setHeightByRows(1);
		} else if (list.size() >= 10) {
			grid.setHeightByRows(10);
		} else {
			grid.setHeightByRows(list.size());
		}
		grid.setVisible(true);
	}

}
