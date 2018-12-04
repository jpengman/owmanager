package se.anviken.owmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the meter_types database table.
 * 
 */
@Entity
@Table(name = "meter_types")
@NamedQuery(name = "MeterType.findAll", query = "SELECT m FROM MeterType m")
public class MeterType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "meter_type_id")
	private int meterTypeId;

	private String description;

	@Column(name = "meter_type")
	private String meterType;

	@Column(name = "name")
	private String name;

	public MeterType() {
	}

	public int getMeterTypeId() {
		return this.meterTypeId;
	}

	public void setMeterTypeId(int meterTypeId) {
		this.meterTypeId = meterTypeId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMeterType() {
		return this.meterType;
	}

	public void setMeterType(String meterType) {
		this.meterType = meterType;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}