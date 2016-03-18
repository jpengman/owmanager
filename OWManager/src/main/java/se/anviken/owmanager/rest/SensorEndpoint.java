package se.anviken.owmanager.rest;

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
import se.anviken.owmanager.model.Sensor;

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
				UriBuilder.fromResource(SensorEndpoint.class)
						.path(String.valueOf(entity.getSensorId())).build())
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
		TypedQuery<Sensor> findByIdQuery = em
				.createQuery(
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
	@Produces("application/json")
	public List<Sensor> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<Sensor> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT s FROM Sensor s LEFT JOIN FETCH s.sensorType ORDER BY s.sensorId",
						Sensor.class);
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
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
}
