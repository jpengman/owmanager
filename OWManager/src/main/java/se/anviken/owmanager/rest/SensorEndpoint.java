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
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;

import se.anviken.owmanager.dto.TemperatureDTO;
import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.model.Temperature;
import se.anviken.owmanager.utils.TimeUtil;

/**
 * 
 */
@Stateless
@Path("/sensors")
public class SensorEndpoint {
	@PersistenceContext(unitName = "OWManager-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json")
	public Response create(Sensor entity) {
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(SensorEndpoint.class).path(String.valueOf(entity.getSensorId())).build())
				.build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") int id) {
		Sensor entity = em.find(Sensor.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") int id) {
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
		return Response.ok(entity).build();
	}

	@GET
	@Path("/temperature/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response getTemperatureById(@PathParam("id") int id) {
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
	@Path("/getdatatable/{ids}/{noofhours:[0-9][0-9]*}")
	@Produces("application/json")
	public Response getDataTableById(@PathParam("ids") String ids, @PathParam("noofhours") int noofhours) {
		String[] strings = ids.split("\\s*,\\s*");
		Integer[] intarray = new Integer[strings.length];
		int i = 0;
		for (String str : strings) {
			intarray[i] = Integer.parseInt(str.trim());
			i++;
		}
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.HOUR, -noofhours);

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
		TypedQuery<Temperature> tempQuery = em.createQuery(
				"SELECT DISTINCT t FROM Temperature t LEFT JOIN FETCH t.sensor WHERE t.sensor.sensorId = :id AND t.tempTimestamp BETWEEN :from AND :to",
				Temperature.class);
		tempQuery.setParameter("from", from.getTime());
		tempQuery.setParameter("to", to.getTime());
		for (int id : sensorIDs) {
			tempQuery.setParameter("id", id);
			Map<Date, Float> temperarures = new HashMap<Date, Float>();
			for (Temperature temperature : tempQuery.getResultList()) {
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

		return Response.ok(JsonRenderer.renderDataTable(data, true, false, true), MediaType.APPLICATION_JSON).build();

	}

	@GET
	@Produces("application/json")
	public List<Sensor> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
		TypedQuery<Sensor> findAllQuery = em.createQuery(
				"SELECT DISTINCT s FROM Sensor s LEFT JOIN FETCH s.sensorType ORDER BY s.sensorId", Sensor.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Sensor> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") int id, Sensor entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id != entity.getSensorId()) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(Sensor.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			entity = em.merge(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
}
