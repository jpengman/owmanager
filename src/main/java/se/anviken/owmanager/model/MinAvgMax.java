package se.anviken.owmanager.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the meter_types database table.
 * 
 */
@Entity
@Table(name = "min_avg_max")
@NamedQueries({ @NamedQuery(name = "MinAvgMax.findAll", query = "SELECT m FROM MinAvgMax m"),
		@NamedQuery(name = "MinAvgMax.findByDay", query = "SELECT m FROM MinAvgMax m WHERE m.type= 'DAYS'"),
		@NamedQuery(name = "MinAvgMax.findByWeek", query = "SELECT m FROM MinAvgMax m WHERE m.type= 'WEEKS'"),
		@NamedQuery(name = "MinAvgMax.findByMonth", query = "SELECT m FROM MinAvgMax m WHERE m.type= 'MONTHS'"),
		@NamedQuery(name = "MinAvgMax.findByYear", query = "SELECT m FROM MinAvgMax m WHERE m.type= 'YEARS'") })
public class MinAvgMax implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private float min;
	private float avg;
	private float max;
	private Integer day;
	private Integer week;
	private Integer month;
	private Integer year;
	private String type;

	public float getAvg() {
		return avg;
	}

	public Integer getDay() {
		return day;
	}

	public int getId() {
		return id;
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	public Integer getMonth() {
		return month;
	}

	public String getType() {
		return type;
	}

	public Integer getWeek() {
		return week;
	}

	public Integer getYear() {
		return year;
	}

	public void setAvg(float avg) {
		this.avg = avg;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public void setYear(int year) {
		this.year = year;
	}

}