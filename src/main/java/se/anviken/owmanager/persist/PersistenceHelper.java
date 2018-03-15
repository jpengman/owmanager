package se.anviken.owmanager.persist;

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
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;

import se.anviken.owmanager.Constants;
import se.anviken.owmanager.dto.TemperatureDTO;
import se.anviken.owmanager.model.MinAvgMax;
import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.model.Temperature;
import se.anviken.owmanager.utils.TimeUtil;

@Stateless
public class PersistenceHelper {
	@PersistenceContext(unitName = "OWManager-persistence-unit")
	private EntityManager em;

	protected List<Temperature> getDataSet(int id, int noofminutes) {
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.MINUTE, -noofminutes);
		TypedQuery<Temperature> tempQuery = em.createQuery(
				"SELECT DISTINCT t FROM Temperature t LEFT JOIN FETCH t.sensor WHERE t.sensor.sensorId = :id AND t.tempTimestamp BETWEEN :from AND :to",
				Temperature.class);
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
		DataTable data = new DataTable();
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("Tid", ValueType.TEXT, "Tid"));

		List<Map<Date, Float>> temperatureLists = new ArrayList<Map<Date, Float>>();
		Set<Date> timestamps = new HashSet<Date>();

		for (Sensor sensor : results) {
			cd.add(new ColumnDescription(sensor.getName(), ValueType.NUMBER, sensor.getName()));
			Map<Date, Float> temperarures = new HashMap<Date, Float>();
			for (Temperature temperature : getDataSet(sensor.getSensorId(), noofminutes)) {
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
				row.addCell(temperatureList.get(timestamp));
			}
			try {
				data.addRow(row);
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Response.ok(JsonRenderer.renderDataTable(data, true, false, true).toString(), MediaType.APPLICATION_JSON)
				.build();
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
		DataTable data = new DataTable();
		ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
		cd.add(new ColumnDescription("Tid", ValueType.TEXT, "Tid"));
		for (Sensor sensor : results) {
			cd.add(new ColumnDescription(sensor.getName(), ValueType.NUMBER, sensor.getName()));
		}
		data.addColumns(cd);
		List<Map<Date, Float>> temperatureLists = new ArrayList<Map<Date, Float>>();
		Set<Date> timestamps = new HashSet<Date>();

		for (int id : sensorIDs) {
			Map<Date, Float> temperarures = new HashMap<Date, Float>();
			for (Temperature temperature : getDataSet(id, noofminutes)) {
				temperarures.put(temperature.getTempTimestamp(),
						temperature.getTemperature() + temperature.getSensor().getOffset());
				timestamps.add(temperature.getTempTimestamp());
			}
			temperatureLists.add(temperarures);
		}
		timestamps = new TreeSet<Date>(timestamps);
		for (Date timestamp : timestamps) {
			TableRow row = new TableRow();
			row.addCell(TimeUtil.getTimeOfDay(timestamp));
			for (Map<Date, Float> temperatureList : temperatureLists) {
				row.addCell(temperatureList.get(timestamp));
			}
			try {
				data.addRow(row);
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Response.ok(JsonRenderer.renderDataTable(data, true, false, true).toString(), MediaType.APPLICATION_JSON)
				.build();
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
			entity = null;
		}
		if (entity == null) {
			return null;
		}
		float temperature = entity.getLastLoggedTemp() + entity.getOffset();
		return new TemperatureDTO(entity.getLastLogged(), temperature);
	}

	protected List<MinAvgMax> getMinAvgMaxByType(String type) {
		TypedQuery<MinAvgMax> minAvgMaxResult = null;
		System.out.println(type);
		switch (type.toLowerCase()) {
		case Constants.DAYS:
			minAvgMaxResult = em.createNamedQuery("MinAvgMax.findByDay", MinAvgMax.class);
			System.out.println(type);
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
