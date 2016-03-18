package se.anviken.owmanager.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the sensors database table.
 * 
 */
@Entity
@Table(name="sensors")
@NamedQuery(name="Sensor.findAll", query="SELECT s FROM Sensor s")
public class Sensor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="sensor_id")
	private int sensorId;

	private String address;

	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_logged")
	private Date lastLogged;

	@Column(name="last_logged_temp")
	private float lastLoggedTemp;

	private String name;

	private int offset;

	@Column(name="reset_interval")
	private int resetInterval;

	//uni-directional many-to-one association to SensorType
	@ManyToOne
	@JoinColumn(name="sensor_type_id")
	private SensorType sensorType;

	public Sensor() {
	}

	public int getSensorId() {
		return this.sensorId;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastLogged() {
		return this.lastLogged;
	}

	public void setLastLogged(Date lastLogged) {
		this.lastLogged = lastLogged;
	}

	public float getLastLoggedTemp() {
		return this.lastLoggedTemp;
	}

	public void setLastLoggedTemp(float lastLoggedTemp) {
		this.lastLoggedTemp = lastLoggedTemp;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getResetInterval() {
		return this.resetInterval;
	}

	public void setResetInterval(int resetInterval) {
		this.resetInterval = resetInterval;
	}

	public SensorType getSensorType() {
		return this.sensorType;
	}

	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}

}