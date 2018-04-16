package privacyanalyzer.backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import privacyanalyzer.backend.data.entity.ApkModel;
import privacyanalyzer.backend.data.entity.ApkPermissionAssociation;
import privacyanalyzer.backend.data.entity.Permission;

public interface ApkPermissionAssociationRepository extends JpaRepository<ApkPermissionAssociation, Long >{

	
	@Query("select apkAss.permission"
			+ " from ApkPermissionAssociation apkAss"
			+ " where apkAss.permissionType = :#{[1]} and apkAss.apk=("
																	+ " select xapk"
																	+ " from ApkInfo xapk"
																	+ " where xapk.sha256 = :#{[0]})")
	List<Permission> findAllPermissionsByApkAndPermissionType(String sha256,String permissionType);
	//na epistrefi ta permissions analoga me to apk kai to permissionType(Declared,etc..)
	
	
	@Query("select apkAss.permission"
			+ " from ApkPermissionAssociation apkAss"
			+ " where apkAss.permissionType = :#{[1]} and apkAss.apk= :#{[0]}")
	List<Permission> findAllPermissionsByApkModelAndPermissionType(ApkModel apkmodel,String permissionType);
	//na epistrefi ta permissions analoga me to apk kai to permissionType(Declared,etc..)
	
	
	List<ApkPermissionAssociation> findByApk(ApkModel apk);
}
