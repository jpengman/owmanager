package se.anviken.owmanager.dto;

import java.util.Calendar;
import java.util.Date;

public class TemperatureDTO {

	private Calendar logged;
	private float Temperature;
	public TemperatureDTO(Date logged, float temperature) {
		super();
		this.setLogged(logged);
		setTemperature(temperature);
	}
	public Calendar getLogged() {
		return logged;
	}
	public void setLogged(Date logged) {
		this.logged = Calendar.getInstance();
		this.logged.setTime(logged);
	}
	public void setLogged(Calendar logged) {
		this.logged = logged;
	}
	public float getTemperature() {
		return Temperature;
	}
	public void setTemperature(float temperature) {
		Temperature = temperature;
	}
	
}
