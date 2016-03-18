package se.anviken.owmanager.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the temperatures_archive database table.
 * 
 */
@Entity
@Table(name="temperatures_archive")
@NamedQuery(name="TemperaturesArchive.findAll", query="SELECT t FROM TemperaturesArchive t")
public class TemperaturesArchive implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="temperature_id")
	private int temperatureId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="temp_timestamp")
	private Date tempTimestamp;

	private float temperature;

	//uni-directional many-to-one association to Sensor
	@ManyToOne
	@JoinColumn(name="sensor_id")
	private Sensor sensor;

	public TemperaturesArchive() {
	}

	public int getTemperatureId() {
		return this.temperatureId;
	}

	public void setTemperatureId(int temperatureId) {
		this.temperatureId = temperatureId;
	}

	public Date getTempTimestamp() {
		return this.tempTimestamp;
	}

	public void setTempTimestamp(Date tempTimestamp) {
		this.tempTimestamp = tempTimestamp;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public Sensor getSensor() {
		return this.sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

}