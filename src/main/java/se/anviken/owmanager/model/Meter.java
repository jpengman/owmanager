package se.anviken.owmanager.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the meters database table.
 * 
 */
@Entity
@Table(name="meters")
@NamedQuery(name="Meter.findAll", query="SELECT m FROM Meter m")
public class Meter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="meter_id")
	private int meterId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="meter_timestamp")
	private Date meterTimestamp;

	private double value;

	//bi-directional many-to-one association to MeterType
	@ManyToOne
	@JoinColumn(name="meter_type_id")
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

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public MeterType getMeterType() {
		return this.meterType;
	}

	public void setMeterType(MeterType meterType) {
		this.meterType = meterType;
	}

}