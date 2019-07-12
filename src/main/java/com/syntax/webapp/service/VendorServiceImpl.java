package com.syntax.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.syntax.webapp.dao.VendorDao;
import com.syntax.webapp.model.Vendor;


@Service("vendorService")
@Transactional
public class VendorServiceImpl implements VendorService{

	@Autowired
	private VendorDao dao;
	
	public Vendor findById(int id) {
		return dao.findById(id);
	}

	public Vendor findByCode(String code) {
		Vendor o = dao.findByVendorCode(code);
		return o;
	}

	public void saveVendor(Vendor o) {
		dao.save(o);
	}

	public void updateVendor(Vendor o) {
		Vendor entity = dao.findById(o.getId());
		if(entity!=null){
			entity.setVendorCode(o.getVendorCode());
			entity.setVendorName(o.getVendorName());
		}
	}
	
	public void deleteVendorByCode(String code) {
		dao.deleteByVendorCode(code);
	}

	public List<Vendor> findAllVendors() {
		return dao.findAllVendors();
	}
	
}
