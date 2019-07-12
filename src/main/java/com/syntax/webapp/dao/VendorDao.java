package com.syntax.webapp.dao;

import java.util.List;

import com.syntax.webapp.model.Vendor;

public interface VendorDao {

	Vendor findById(int id);
	
	Vendor findByVendorCode(String code);
	
	void save(Vendor vendor);
	
	void deleteByVendorCode(String code);
	
	List<Vendor> findAllVendors();
	
}
