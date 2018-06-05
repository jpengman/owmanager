package se.anviken.owmanager.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the meters database table.
 * 
 */
@Entity
@Table(name = "meters")
@NamedQuery(name = "Meter.findAll", query = "SELECT m FROM Meter m")
public class Meter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "meter_id")
	private int meterId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "meter_timestamp")
	private Date meterTimestamp;

	private float value;

	// bi-directional many-to-one association to MeterType
	@ManyToOne
	@JoinColumn(name = "meter_type_id")
	private MeterType meterType;

	public Meter() {
	}

	public int getMeterId() {
		return this.meterId;
	}

	public void setMeterId(int meterId) {
		this.meterId = meterId;
	}

	public Date getMeterTimestamp() {
		return this.meterTimestamp;
	}

	public void setMeterTimestamp(Date meterTimestamp) {
		this.meterTimestamp = meterTimestamp;
	}

	public float getValue() {
		return this.value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public MeterType getMeterType() {
		return this.meterType;
	}

	public void setMeterType(MeterType meterType) {
		this.meterType = meterType;
	}

}