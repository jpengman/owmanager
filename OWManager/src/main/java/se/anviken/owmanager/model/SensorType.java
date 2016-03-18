package se.anviken.owmanager.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the sensor_types database table.
 * 
 */
@Entity
@Table(name = "sensor_types")
@NamedQuery(name = "SensorType.findAll", query = "SELECT s FROM SensorType s")
@XmlRootElement
public class SensorType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "sensor_type_id")
	private int sensorTypeId;

	private String description;

	@Column(name = "sensor_type")
	private String sensorType;

	public SensorType() {
	}

	public int getSensorTypeId() {
		return this.sensorTypeId;
	}

	public void setSensorTypeId(int sensorTypeId) {
		this.sensorTypeId = sensorTypeId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSensorType() {
		return this.sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

}