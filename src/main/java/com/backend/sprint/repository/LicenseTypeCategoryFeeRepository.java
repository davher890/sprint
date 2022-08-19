package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.FamilyDao;
import com.backend.sprint.model.dao.LicenseTypeCategoryFeeDao;

@Repository
public interface LicenseTypeCategoryFeeRepository
		extends PagingAndSortingRepository<LicenseTypeCategoryFeeDao, Long>, JpaSpecificationExecutor<FamilyDao> {

	@Query(value = "SELECT * FROM license_type_category_fee WHERE license_type = ?1 AND category = ?2", nativeQuery = true)
	public LicenseTypeCategoryFeeDao findByLicenseTypeAndCategory(String licenseType, String category);

}
