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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the temperatures_archive database table.
 * 
 */
@Entity
@Table(name = "temperatures_archive")
@NamedQuery(name = "TemperaturesArchive.findAll", query = "SELECT t FROM TemperaturesArchive t")
@XmlRootElement
public class TemperaturesArchive implements Serializable, TemperatureReading {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "temperature_id")
	private int temperatureId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "temp_timestamp")
	private Date tempTimestamp;

	private float temperature;

	// uni-directional many-to-one association to Sensor
	@ManyToOne
	@JoinColumn(name = "sensor_id")
	private Sensor sensor;

	public TemperaturesArchive() {
	}

	@Override
	public int getTemperatureId() {
		return this.temperatureId;
	}

	@Override
	public void setTemperatureId(int temperatureId) {
		this.temperatureId = temperatureId;
	}

	@Override
	public Date getTempTimestamp() {
		return this.tempTimestamp;
	}

	@Override
	public void setTempTimestamp(Date tempTimestamp) {
		this.tempTimestamp = tempTimestamp;
	}

	@Override
	public float getTemperature() {
		return this.temperature;
	}

	@Override
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	@Override
	public Sensor getSensor() {
		return this.sensor;
	}

	@Override
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

}