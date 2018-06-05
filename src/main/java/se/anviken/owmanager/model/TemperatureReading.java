package se.anviken.owmanager.model;

import java.util.Date;

public interface TemperatureReading {
	public int getTemperatureId();

	public void setTemperatureId(int temperatureId);

	public Date getTempTimestamp();

	public void setTempTimestamp(Date tempTimestamp);

	public float getTemperature();

	public void setTemperature(float temperature);

	public Sensor getSensor();

	public void setSensor(Sensor sensor);
}
