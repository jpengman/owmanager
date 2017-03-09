package se.anviken.owmanager.rest;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.utils.ColorUtil;

@Stateless
@Path("/svg")
public class SVGEndpoint {
	private static final String DEFS_END_TAG = "</defs>\n";
	private static final String DEFS_START_TAG = "<defs>\n";
	@PersistenceContext(unitName = "OWManager-persistence-unit")
	private EntityManager em;

	@GET
	@Path("/heating")
	@Produces("text/html")
	public String test() {
		return getSVG();
	}

	private String getSVG() {
		String svgStartTag = "<svg width='400' height='400'>\n";
		String[] ackSVG = getTankSVG(200, 3, 150, 300, "HEATING_ACC");
		String[] hpSVG = getTankSVG(3, 3, 150, 200, "HEATING_HP");
		String pipe = "<polygon points='200,20 153,20 153,30 173,30 173,150 153,150 153,160 173,160 173,280 200,280 200,270 183,270  183,30 200,30' fill='url(#HEATING_ACC)' style='stroke:black;stroke-width:2' />\n";
		String defsTag = DEFS_START_TAG + ackSVG[0] + hpSVG[0] + DEFS_END_TAG;
		String svgStopTag = "Sorry, your browser does not support inline SVG.\n</svg>";
		String svgString = svgStartTag + defsTag + ackSVG[1] + hpSVG[1] + pipe + svgStopTag;
		return svgString;
	}

	private String[] getTankSVG(int x, int y, int width, int height, String type) {
		TypedQuery<Sensor> findAllQuery = em
				.createQuery("SELECT DISTINCT s FROM Sensor s LEFT JOIN FETCH s.sensorType st WHERE st.sensorType ='"
						+ type + "' ORDER BY s.name", Sensor.class);
		String stopTags = "";
		int i = 0;
		int percentStep = 90 / (findAllQuery.getResultList().size() - 1);
		for (Sensor sensor : findAllQuery.getResultList()) {
			int percent = 5 + i * percentStep;
			stopTags += "<stop offset='" + percent + "%' style='stop-color:rgb("
					+ ColorUtil.shaderBYR(sensor.getLastLoggedTemp(), 25, 45, 75) + ");stop-opacity:1' />\n";
			i++;
		}
		String linearGradientTag = "<linearGradient id='" + type + "' x1='0%' y1='0%' x2='0%' y2='100%'>\n" + stopTags
				+ "</linearGradient>\n";

		String rectTag = "<rect x='" + x + "' y='" + y + "'  ry='20' width='" + width + "' height='" + height
				+ "' fill='url(#" + type + ")' style='stroke-width:2;stroke:black'/>\n";
		return new String[] { linearGradientTag, rectTag };
	}

}
