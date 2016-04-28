package se.anviken.owmanager.owfs;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class OWSensor {
	private String address;
	private String alias;
	private String crc8;
	private String errata;
	private String family;
	private Double fasttemp;
	private String id;
	private String locator;
	private boolean power;
	private String r_address;
	private String r_id;
	private String r_locator;
	private Double temperature;
	private Double temperature10;
	private Double temperature11;
	private Double temperature12;
	private Double temperature9;
	private Double temphigh;
	private Double templow;
	private String type;
	public OWSensor() {
		super();
	}
	public OWSensor(String adress) throws JSONException, IOException {
		super();
		JSONObject json = OWFSUtil.readJsonForSensor(adress);
		System.out.println(json.toString());
		this.setAddress(json.getString("address"));
		this.setAlias(json.getString("alias"));
		this.setCrc8(json.getString("crc8"));
		//this.setErrata(json.getString("errata"));
		this.setFamily(json.getString("family"));
		this.setFasttemp(json.getDouble("fasttemp"));
		this.setId(json.getString("id"));
		this.setLocator(json.getString("locator"));
		//TODO fill all fields...
	}
	public String getAddress() {
		return address;
	}
	public String getAlias() {
		return alias;
	}
	public String getCrc8() {
		return crc8;
	}
	public String getErrata() {
		return errata;
	}
	public String getFamily() {
		return family;
	}
	public Double getFasttemp() {
		return fasttemp;
	}
	public String getId() {
		return id;
	}
	public String getLocator() {
		return locator;
	}
	public String getR_address() {
		return r_address;
	}
	public String getR_id() {
		return r_id;
	}
	public String getR_locator() {
		return r_locator;
	}
	public Double getTemperature() {
		return temperature;
	}
	public Double getTemperature10() {
		return temperature10;
	}
	public Double getTemperature11() {
		return temperature11;
	}
	public Double getTemperature12() {
		return temperature12;
	}
	public Double getTemperature9() {
		return temperature9;
	}
	public Double getTemphigh() {
		return temphigh;
	}
	public Double getTemplow() {
		return templow;
	}
	public String getType() {
		return type;
	}
	public boolean isPower() {
		return power;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public void setCrc8(String crc8) {
		this.crc8 = crc8;
	}
	public void setErrata(String errata) {
		this.errata = errata;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public void setFasttemp(double fasttemp) {
		this.fasttemp = fasttemp;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setLocator(String locator) {
		this.locator = locator;
	}
	public void setPower(boolean power) {
		this.power = power;
	}
	public void setR_address(String r_address) {
		this.r_address = r_address;
	}
	public void setR_id(String r_id) {
		this.r_id = r_id;
	}
	public void setR_locator(String r_locator) {
		this.r_locator = r_locator;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public void setTemperature10(double temperature10) {
		this.temperature10 = temperature10;
	}
	public void setTemperature11(double temperature11) {
		this.temperature11 = temperature11;
	}
	public void setTemperature12(double temperature12) {
		this.temperature12 = temperature12;
	}
	public void setTemperature9(double temperature9) {
		this.temperature9 = temperature9;
	}
	public void setTemphigh(double temphigh) {
		this.temphigh = temphigh;
	}
	public void setTemplow(double templow) {
		this.templow = templow;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "OWSensor [address=" + address + ", alias=" + alias + ", crc8=" + crc8 + ", errata=" + errata
				+ ", family=" + family + ", fasttemp=" + fasttemp + ", id=" + id + ", locator=" + locator + ", power="
				+ power + ", r_address=" + r_address + ", r_id=" + r_id + ", r_locator=" + r_locator + ", temperature="
				+ temperature + ", temperature10=" + temperature10 + ", temperature11=" + temperature11
				+ ", temperature12=" + temperature12 + ", temperature9=" + temperature9 + ", temphigh=" + temphigh
				+ ", templow=" + templow + ", type=" + type + "]";
	}

	
}
