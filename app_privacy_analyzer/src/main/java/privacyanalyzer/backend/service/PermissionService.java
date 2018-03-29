package privacyanalyzer.backend.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Grid;

import privacyanalyzer.backend.data.Permission;

@Service
public class PermissionService implements Serializable{

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Permission> getPermissions(List<String> permNames) {
		List<Permission> retList = new ArrayList<Permission>();

		for (String p : permNames) {
			try {
				retList.add((Permission) jdbcTemplate.query(
						"SELECT p.id, p.permissionName, p.permissionDesc,p.protectionLevel,p.permissionValue,pl.description FROM permissions p,protectionlevels pl WHERE p.protectionLevel=pl.name and p.permissionValue='"
								+ p + "'",
						(rs, rowNum) -> new Permission(rs.getLong("id"), rs.getString("permissionName"),
								rs.getString("permissionDesc"), rs.getString("protectionLevel"),
								rs.getString("permissionValue"), rs.getString("description")))
						.toArray()[0]);
			} catch (ArrayIndexOutOfBoundsException e) {
				retList.add(new Permission(p));
			}
		}

		return retList;
	}

	public void setGrid(List<String> permNames, Grid<Permission> grid) {
		List<Permission> myList = new ArrayList<Permission>();
		myList = getPermissions(permNames);
		grid.removeAllColumns();
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addColumn(Permission::getPermissionName).setCaption("Permission Name")
				.setDescriptionGenerator(Permission::getPermissionDesc);
		grid.addColumn(Permission::getPermissionValue).setCaption("Permission Value")
				.setDescriptionGenerator(Permission::getPermissionDesc);
		grid.addColumn(Permission::getProtectionLevel).setCaption("Protection Level")
				.setDescriptionGenerator(Permission::getLevelDesc);
		grid.setItems(myList);
		grid.setWidth("100%");
		
		if (permNames.size() == 0) {
			grid.setHeightByRows(1);
		}else if (permNames.size() >= 10) {
			grid.setHeightByRows(10);
		} else {
			grid.setHeightByRows(permNames.size());
		}
		grid.setVisible(true);
	}

}
