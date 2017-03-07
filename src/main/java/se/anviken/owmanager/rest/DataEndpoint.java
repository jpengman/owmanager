package se.anviken.owmanager.rest;

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
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;

import se.anviken.owmanager.dto.TemperatureDTO;
import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.model.Temperature;
import se.anviken.owmanager.utils.DataUtil;
import se.anviken.owmanager.utils.TimeUtil;

/**
 * 
 */
@Stateless
@Path("/data")
public class DataEndpoint {
	private static final int DEFAULT_NO_OF_MINUTES = 60;
	@PersistenceContext(unitName = "OWManager-persistence-unit")
	private EntityManager em;

	@GET
	@Path("/")
	@Produces("application/json")
	public String getAPIDoc() {
		String returnString = "getdatatable API: /getdatatable/{ids}/{noofminutes:[0-9][0-9]*}\n"
				+ "ids = Sensor ID:s comma separated\n" + "noofminutes = Size of dataset in minutes (Default:"
				+ DataEndpoint.DEFAULT_NO_OF_MINUTES + ")\n\n"

				+ "getpeaks API: /getpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}/{range:[0-9][0-9]*}/{minpeakvalue:[0-9][0-9]*}\n"
				+ "id = Sensor ID\n" + "noofminutes = Size of dataset in minutes (Default:"
				+ DataEndpoint.DEFAULT_NO_OF_MINUTES + ")\n" + "range = The range of the peak (Default:"
				+ DataUtil.DEFAULT_PEAK_RANGE + ")\n" + "minpeakvalue = Minimun value of peak (Default:"
				+ DataUtil.DEFAULT_MIN_PEAK_VALUE + ")\n\n"

				+ "getnoofpeaks API: /getnoofpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}/{range:[0-9][0-9]*}/{minpeakvalue:[0-9][0-9]*}\n"
				+ "id = Sensor ID\n" + "noofminutes = Size of dataset in minutes (Default:"
				+ DataEndpoint.DEFAULT_NO_OF_MINUTES + ")\n" + "range = The range of the peak (Default:"
				+ DataUtil.DEFAULT_PEAK_RANGE + ")\n" + "minpeakvalue = Minimun value of peak (Default:"
				+ DataUtil.DEFAULT_MIN_PEAK_VALUE + ")\n\n"

				+ "temperature API: /temperature/{id:[0-9][0-9]*}\n" + "id = Sensor ID\n\n";

		return returnString;
	}

	private List<Temperature> getDataSet(int id) {
		return getDataSet(id, DEFAULT_NO_OF_MINUTES);
	}

	private List<Temperature> getDataSet(int id, int noofminutes) {
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

	private Response getDatatable(String ids, int noofminutes) {
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
		cd.add(new ColumnDescription("Tid1", ValueType.TEXT, "Tid2"));
		for (Sensor sensor : results) {
			cd.add(new ColumnDescription(sensor.getName(), ValueType.NUMBER, sensor.getDescription()));
		}
		data.addColumns(cd);
		List<Map<Date, Float>> temperatureLists = new ArrayList<Map<Date, Float>>();
		Set<Date> timestamps = new HashSet<Date>();

		for (int id : sensorIDs) {
			Map<Date, Float> temperarures = new HashMap<Date, Float>();
			for (Temperature temperature : getDataSet(id, noofminutes)) {
				temperarures.put(temperature.getTempTimestamp(), temperature.getTemperature());
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

	@GET
	@Path("/getdatatable/{ids}")
	@Produces("application/json")
	public Response getDataTableById(@PathParam("ids") String ids) {
		return getDatatable(ids, DataEndpoint.DEFAULT_NO_OF_MINUTES);
	}

	@GET
	@Path("/getdatatable/{ids}/{noofminutes:[0-9][0-9]*}")
	@Produces("application/json")
	public Response getDataTableByIdAndTime(@PathParam("ids") String ids, @PathParam("noofminutes") int noofminutes) {
		return getDatatable(ids, noofminutes);
	}

	@GET
	@Path("/getpeaks/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public List<Temperature> getPeaksById(@PathParam("id") int id) {
		return DataUtil.FindPeaksInDataset(getDataSet(id));
	}

	@GET
	@Path("/getpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}")
	@Produces("application/json")
	public List<Temperature> getPeaksByIdAndTime(@PathParam("id") int id, @PathParam("noofminutes") int noofminutes) {
		return DataUtil.FindPeaksInDataset(getDataSet(id, noofminutes));
	}

	@GET
	@Path("/getpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}/{range:[0-9][0-9]*}")
	@Produces("application/json")
	public List<Temperature> getPeaksByIdTimeAndRange(@PathParam("id") int id,
			@PathParam("noofminutes") int noofminutes, @PathParam("range") int range) {
		return DataUtil.FindPeaksInDataset(getDataSet(id, noofminutes), range);
	}

	@GET
	@Path("/getpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}/{range:[0-9][0-9]*}/{minpeakvalue:[0-9][0-9]*}")
	@Produces("application/json")
	public List<Temperature> getPeaksByIdTimeRangeAndMinValue(@PathParam("id") int id,
			@PathParam("noofminutes") int noofminutes, @PathParam("range") int range,
			@PathParam("minpeakvalue") int minpeakvalue) {
		return DataUtil.FindPeaksInDataset(getDataSet(id, noofminutes), range, minpeakvalue);
	}

	@GET
	@Path("/getnoofpeaks/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public int getNoOfPeaksById(@PathParam("id") int id) {
		return DataUtil.FindPeaksInDataset(getDataSet(id)).size();
	}

	@GET
	@Path("/getnoofpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}")
	@Produces("application/json")
	public int getNoOfPeaksByIdAndTime(@PathParam("id") int id, @PathParam("noofminutes") int noofminutes) {
		return DataUtil.FindPeaksInDataset(getDataSet(id, noofminutes)).size();
	}

	@GET
	@Path("/getnoofpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}/{range:[0-9][0-9]*}")
	@Produces("application/json")
	public int getNoOfPeaksByIdTimeAndRange(@PathParam("id") int id, @PathParam("noofminutes") int noofminutes,
			@PathParam("range") int range) {
		return DataUtil.FindPeaksInDataset(getDataSet(id, noofminutes), range).size();
	}

	@GET
	@Path("/getnoofpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}/{range:[0-9][0-9]*}/{minpeakvalue:[0-9][0-9]*}")
	@Produces("application/json")
	public int getNoOfPeaksByIdTimeRangeAndMinValue(@PathParam("id") int id, @PathParam("noofminutes") int noofminutes,
			@PathParam("range") int range, @PathParam("minpeakvalue") int minpeakvalue) {
		return DataUtil.FindPeaksInDataset(getDataSet(id, noofminutes), range, minpeakvalue).size();
	}

	private Response getTemperature(int id) {
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
			return Response.status(Status.NOT_FOUND).build();
		}
		float temperature = entity.getLastLoggedTemp() + entity.getOffset();
		return Response.ok(new TemperatureDTO(entity.getLastLogged(), temperature)).build();
	}

	@GET
	@Path("/temperature/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response getTemperatureById(@PathParam("id") int id) {
		return getTemperature(id);
	}
}