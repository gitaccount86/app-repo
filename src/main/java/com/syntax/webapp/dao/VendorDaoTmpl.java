package com.syntax.webapp.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.syntax.webapp.model.User;
import com.syntax.webapp.model.Vendor;

@Repository("VendorDao")
public class VendorDaoTmpl extends AbstractDao<Integer, Vendor>implements VendorDao {

	static final Logger logger = LoggerFactory.getLogger(VendorDaoTmpl.class);
	
	public Vendor findById(int id){
		Vendor o = getByKey(id);
		return o;
	}

	public Vendor findByVendorCode(String code) {
		logger.info("Vendor code : {}", code);
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("vendorCode", code));
		Vendor o = (Vendor)crit.uniqueResult();
		return o;
	}

	public void save(Vendor vendor) {
		persist(vendor);

	}

	public void deleteByVendorCode(String code) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("vendorCode", code));
		Vendor o = (Vendor)crit.uniqueResult();
		delete(o);
	}

	public List<Vendor> findAllVendors() {
		Criteria criteria = createEntityCriteria().addOrder(Order.asc("vendorCode"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<Vendor> o = (List<Vendor>) criteria.list();
		return o;
	}

}
