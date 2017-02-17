package se.anviken.owmanager.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;

import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.owfs.OWFSUtil;

@Stateless
@Path("/notassigned")
public class NotAssignedEndpoint {
	@PersistenceContext(unitName = "OWManager-persistence-unit")
	private EntityManager em;
	
	@GET
	@Path("/")
	@Produces("application/json")
	public Response findById() {
		List<String> notAssignedList = new ArrayList<String>() ;
		try {
			List<String> allConnectedSensors = OWFSUtil.getAllSensorAdresses();
	
			TypedQuery<Sensor> findAllQuery = em
					.createQuery(
							"SELECT DISTINCT s FROM Sensor s LEFT JOIN FETCH s.sensorType ORDER BY s.sensorId",
							Sensor.class);
			List<Sensor> assignedSensors = findAllQuery.getResultList();
			Iterator<String> allConnectedIterator = allConnectedSensors.iterator();
			
			while(allConnectedIterator.hasNext()){
				String currentAdress = allConnectedIterator.next();
				boolean matched = false;
				Iterator<Sensor> assignedIterator = assignedSensors.iterator();
				while(assignedIterator.hasNext()){
					if(assignedIterator.next().getAddress().equals(currentAdress))
					matched=true;
				}
				if(!matched)
					notAssignedList.add(currentAdress);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(notAssignedList).build();
	}
}
