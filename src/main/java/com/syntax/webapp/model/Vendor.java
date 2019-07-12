package com.syntax.webapp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="VENDOR")
public class Vendor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id 
	@SequenceGenerator(name = "HIBERNATE_SEQUENCE")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;
	
	@NotEmpty
	@Column(name="VENDOR_NAME", length=65, nullable=false)
	private String vendorName;
	
	@NotEmpty
	@Column(name="VENDOR_CODE", length=6, unique=true, nullable = false)
	private String vendorCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((vendorCode == null) ? 0 : vendorCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vendor))
			return false;
		Vendor other = (Vendor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (vendorCode == null) {
			if (other.vendorCode != null)
				return false;
		} else if (!vendorCode.equals(other.vendorCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vendor [id=" + id + ", vendor code=" + vendorCode + "]";
	}
	
}
