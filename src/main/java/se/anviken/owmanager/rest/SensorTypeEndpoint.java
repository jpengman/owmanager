package se.anviken.owmanager.rest;

import java.util.Date;
import java.util.List;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import se.anviken.owmanager.dto.TemperatureDTO;
import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.model.SensorType;

/**
 * 
 */
@Stateless
@Path("/sensortypes")
public class SensorTypeEndpoint {
	@PersistenceContext(unitName = "OWManager-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json")
	public Response create(SensorType entity) {
		em.persist(entity);
		return Response
				.created(
						UriBuilder.fromResource(SensorTypeEndpoint.class)
								.path(String.valueOf(entity.getSensorTypeId()))
								.build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") int id) {
		SensorType entity = em.find(SensorType.class, id);
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
		TypedQuery<SensorType> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT s FROM SensorType s WHERE s.sensorTypeId = :entityId ORDER BY s.sensorTypeId",
						SensorType.class);
		findByIdQuery.setParameter("entityId", id);
		SensorType entity;
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
	@Path("/average/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findAverageById(@PathParam("id") int id) {
		TypedQuery<Sensor> findtempQuery = em
				.createQuery(
						"SELECT s FROM Sensor s LEFT JOIN FETCH s.sensorType WHERE s.sensorType.sensorTypeId = :entityId ORDER BY s.sensorId",
						Sensor.class);
		findtempQuery.setParameter("entityId", id);
		float temperature = 0;
		Date lastLogged=null; 
		try {
			List<Sensor> entityList = findtempQuery.getResultList();
			for (Sensor sensor:entityList){
				temperature = temperature+sensor.getLastLoggedTemp()+sensor.getOffset();
				if(lastLogged ==null || sensor.getLastLogged().after(lastLogged)){
					lastLogged = sensor.getLastLogged();
				}
			}
			temperature = temperature/entityList.size();
		} catch (NoResultException nre) {
			return Response.status(Status.NOT_FOUND).build();
		}  
		return Response.ok(new TemperatureDTO(lastLogged,temperature)).build();
	}

	@GET
	@Produces("application/json")
	public List<SensorType> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<SensorType> findAllQuery = em.createQuery(
				"SELECT DISTINCT s FROM SensorType s ORDER BY s.sensorTypeId",
				SensorType.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<SensorType> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") int id, SensorType entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id != entity.getSensorTypeId()) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(SensorType.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			entity = em.merge(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
}
