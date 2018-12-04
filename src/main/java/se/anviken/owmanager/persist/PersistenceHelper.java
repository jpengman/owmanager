package se.anviken.owmanager.persist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

import se.anviken.owmanager.Constants;
import se.anviken.owmanager.dto.TemperatureDTO;
import se.anviken.owmanager.model.Meter;
import se.anviken.owmanager.model.MeterType;
import se.anviken.owmanager.model.MinAvgMax;
import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.model.TemperatureReading;
import se.anviken.owmanager.utils.TimeUtil;

@Stateless
public class PersistenceHelper {
	private static final Value NULL_NUMBER_VALUE = Value.getNullValueFromValueType(ValueType.NUMBER);
	public static final int DEFAULT_NO_OF_MINUTES = 60;
	@PersistenceContext(unitName = "OWManager-persistence-unit")
	private EntityManager em;

	protected List<TemperatureReading> getDataSet(int id, int noofminutes) {
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.MINUTE, -noofminutes);
		return getDatasetFromInterval(id, from, to);
	}

	protected List<TemperatureReading> getDataSet(int id, String shortDate) throws ParseException {
		Calendar from = TimeUtil.getCalendarFromShortDate(shortDate);
		Calendar to = TimeUtil.getCalendarFromShortDate(shortDate);
		to.add(Calendar.DATE, 1);
		return getDatasetFromInterval(id, from, to);
	}

	private List<TemperatureReading> getDatasetFromInterval(int id, Calendar from, Calendar to) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -2);
		String table;
		if (from.before(now)) {
			table = "TemperaturesArchive";
		} else {
			table = "Temperature";
		}

		TypedQuery<TemperatureReading> tempQuery = em.createQuery(
				"SELECT DISTINCT t FROM " + table
						+ " t LEFT JOIN FETCH t.sensor WHERE t.sensor.sensorId = :id AND t.tempTimestamp BETWEEN :from AND :to",
				TemperatureReading.class);
		tempQuery.setParameter("from", from.getTime());
		tempQuery.setParameter("to", to.getTime());
		tempQuery.setParameter("id", id);
		System.out.println(tempQuery.getResultList().size());
		return tempQuery.getResultList();
	}

	protected List<TemperatureReading> getDataSet(int id) {
		return getDataSet(id, DEFAULT_NO_OF_MINUTES);
	}

	protected List<Meter> getMeterDataSet(int id, int noofdays) {
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.DATE, -noofdays);
		TypedQuery<Meter> tempQuery = em.createQuery(
				"SELECT DISTINCT t FROM Meter t  WHERE t.meterType.meterTypeId = :id AND t.meterTimestamp BETWEEN :from AND :to",
				Meter.class);
		tempQuery.setParameter("from", from.getTime());
		tempQuery.setParameter("to", to.getTime());
		tempQuery.setParameter("id", id);
		return tempQuery.getResultList();
	}

	protected Response getDatatableByType(String type, int noofminutes) {
		String[] typeArray = type.split("\\s*,\\s*");
		List<String> sensorTypes = Arrays.asList(typeArray);
		TypedQuery<Sensor> query = em
				.createQuery("SELECT s FROM Sensor s where s.sensorType.sensorType IN :sensorTypes", Sensor.class);
		query.setParameter("sensorTypes", sensorTypes);
		final List<Sensor> results = query.getResultList();
		return createDataTable(noofminutes, results);
	}

	private Response createDataTable(int noofminutes, final List<Sensor> sensors) {
		DataTable data = new DataTable();
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("Tid", ValueType.TEXT, "Tid"));

		List<Map<Date, Float>> temperatureLists = new ArrayList<Map<Date, Float>>();
		Set<Date> timestamps = new HashSet<Date>();

		for (Sensor sensor : sensors) {
			cd.add(new ColumnDescription(sensor.getName(), ValueType.NUMBER, sensor.getName()));
			Map<Date, Float> temperarures = new HashMap<Date, Float>();
			for (TemperatureReading temperature : getDataSet(sensor.getSensorId(), noofminutes)) {
				temperarures.put(temperature.getTempTimestamp(),
						temperature.getTemperature() + temperature.getSensor().getOffset());
				timestamps.add(temperature.getTempTimestamp());
			}
			temperatureLists.add(temperarures);
		}
		data.addColumns(cd);
		timestamps = new TreeSet<Date>(timestamps);
		for (Date timestamp : timestamps) {
			TableRow row = new TableRow();
			row.addCell(TimeUtil.getTimeOfDay(timestamp));
			for (Map<Date, Float> temperatureList : temperatureLists) {
				if (temperatureList.get(timestamp) != null) {
					row.addCell(temperatureList.get(timestamp));
				} else {
					row.addCell(NULL_NUMBER_VALUE);
				}
			}
			try {
				data.addRow(row);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			}
		}
		return Response.ok(fixJson(JsonRenderer.renderDataTable(data, true, true, true).toString()),
				MediaType.APPLICATION_JSON).build();
	}

	private Response createMeterDataTable(int noofdays, final List<MeterType> meterTypes) {
		DataTable data = new DataTable();
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("Tid", ValueType.NUMBER, "Tid"));

		List<Map<Date, Float>> meterLists = new ArrayList<Map<Date, Float>>();
		Set<Date> timestamps = new HashSet<Date>();

		for (MeterType meterType : meterTypes) {
			cd.add(new ColumnDescription(meterType.getMeterType(), ValueType.NUMBER, meterType.getMeterType()));
			Map<Date, Float> temperarures = new HashMap<Date, Float>();
			for (Meter meter : getMeterDataSet(meterType.getMeterTypeId(), noofdays)) {
				temperarures.put(meter.getMeterTimestamp(), meter.getValue());
				timestamps.add(meter.getMeterTimestamp());
			}
			meterLists.add(temperarures);
		}
		data.addColumns(cd);
		timestamps = new TreeSet<Date>(timestamps);
		for (Date timestamp : timestamps) {
			TableRow row = new TableRow();
			row.addCell(timestamp.getTime());
			for (Map<Date, Float> meterList : meterLists) {
				if (meterList.get(timestamp) != null) {
					row.addCell(meterList.get(timestamp));
				} else {
					row.addCell(NULL_NUMBER_VALUE);
				}
			}
			try {
				data.addRow(row);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			}
		}
		return Response.ok(fixJson(JsonRenderer.renderDataTable(data, true, true, true).toString()),
				MediaType.APPLICATION_JSON).build();
	}

	/**
	 * It seems thats the JsonRenderer.renderDataTable() sometimes render null
	 * values as ,, instead of "v":null, this fixes that with a replace all
	 * 
	 * @param string
	 *            JSON string
	 * @return fixed JSON string
	 */
	private String fixJson(String string) {

		return string.replaceAll(",,", ",{\"v\":null},");
	}

	protected Response getDatatable(String ids, int noofminutes) {
		String[] strings = ids.split("\\s*,\\s*");
		Integer[] intarray = new Integer[strings.length];
		int i = 0;
		for (String str : strings) {
			intarray[i] = Integer.parseInt(str.trim());
			i++;
		}

		List<Integer> sensorIDs = Arrays.asList(intarray);
		TypedQuery<Sensor> query = em.createQuery("SELECT s FROM Sensor s where s.sensorId IN :sensors", Sensor.class);
		query.setParameter("sensors", sensorIDs);
		final List<Sensor> results = query.getResultList();
		return createDataTable(noofminutes, results);
	}

	protected Response getMeterDatatable(String ids, int noofdays) {
		String[] strings = ids.split("\\s*,\\s*");
		Integer[] intarray = new Integer[strings.length];
		int i = 0;
		for (String str : strings) {
			intarray[i] = Integer.parseInt(str.trim());
			i++;
		}

		List<Integer> meterTypeIDs = Arrays.asList(intarray);
		TypedQuery<MeterType> query = em.createQuery("SELECT s FROM MeterType s where s.meterTypeId IN :meterTypes",
				MeterType.class);
		query.setParameter("meterTypes", meterTypeIDs);
		final List<MeterType> results = query.getResultList();
		return createMeterDataTable(noofdays, results);
	}

	protected TemperatureDTO getTemperature(int id) {
		TypedQuery<Sensor> findByIdQuery = em.createQuery(
				"SELECT DISTINCT s FROM Sensor s LEFT JOIN FETCH s.sensorType WHERE s.sensorId = :entityId ORDER BY s.sensorId",
				Sensor.class);
		findByIdQuery.setParameter("entityId", id);
		Sensor entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return new TemperatureDTO(entity.getLastLogged(), entity.getLastLoggedTemp() + entity.getOffset());
	}

	protected List<MinAvgMax> getMinAvgMaxByType(String type) {
		TypedQuery<MinAvgMax> minAvgMaxResult = null;
		switch (type.toLowerCase()) {
		case Constants.DAYS:
			minAvgMaxResult = em.createNamedQuery("MinAvgMax.findByDay", MinAvgMax.class);
			break;

		case Constants.WEEKS:
			minAvgMaxResult = em.createNamedQuery("MinAvgMax.findByWeek", MinAvgMax.class);
			break;

		case Constants.MONTHS:
			minAvgMaxResult = em.createNamedQuery("MinAvgMax.findByMonth", MinAvgMax.class);
			break;

		case Constants.YEARS:
			minAvgMaxResult = em.createNamedQuery("MinAvgMax.findByYear", MinAvgMax.class);
			break;
		}

		if (minAvgMaxResult != null) {
			return minAvgMaxResult.getResultList();
		} else {
			return null;
		}
	}

	protected List<Object[]> executeNativeQuery(String queryString) {
		Query query = em.createNativeQuery(queryString);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		return resultList;

	}
}
