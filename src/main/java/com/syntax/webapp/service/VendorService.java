package com.syntax.webapp.service;

import java.util.List;

import com.syntax.webapp.model.Vendor;


public interface VendorService {
	
	Vendor findById(int id);
	
	Vendor findByCode(String code);
	
	void saveVendor(Vendor o);
	
	void updateVendor(Vendor o);
	
	void deleteVendorByCode(String code);

	List<Vendor> findAllVendors(); 
	

}