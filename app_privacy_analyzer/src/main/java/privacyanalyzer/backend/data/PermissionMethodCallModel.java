package privacyanalyzer.backend.data;

import java.io.Serializable;

import javax.persistence.Entity;


public class PermissionMethodCallModel implements Serializable{
	private String permissionName;
	private String callerFunction;
	private String permissionFunction;

	
	
	/**
	 * 
	 */
	public PermissionMethodCallModel() {

	}



	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getCallerFunction() {
		return callerFunction;
	}

	public void setCallerFunction(String callerFunction) {
		this.callerFunction = callerFunction;
	}

	public String getPermissionFunction() {
		return permissionFunction;
	}

	public void setPermissionFunction(String permissionFunction) {
		this.permissionFunction = permissionFunction;
	}

	public void print() {
		System.out.println(permissionName);
		System.out.println(callerFunction);
		System.out.println(permissionFunction);
	}

	@Override
	public boolean equals(Object o) {
		PermissionMethodCallModel x = (PermissionMethodCallModel) o;
		if (this.permissionName.equals(x.permissionName) && this.callerFunction.equals(x.callerFunction)
				&& this.permissionFunction.equals(x.permissionFunction))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return permissionName + "\n" + callerFunction + "\n" + permissionFunction + "\n\n";

	}
}
