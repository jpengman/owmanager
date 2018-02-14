package se.anviken.owmanager.rest;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.anviken.owmanager.model.Sensor;
import se.anviken.owmanager.svg.LinearGradient;
import se.anviken.owmanager.svg.Polygon;
import se.anviken.owmanager.svg.Rectangle;
import se.anviken.owmanager.svg.SVG;
import se.anviken.owmanager.svg.Stop;
import se.anviken.owmanager.svg.Text;
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
	public String heating() {
		int rulerYpos = 245;
		SVG svg = new SVG(600, 400);
		Rectangle accRect = new Rectangle(200, 3, 20, 20, 150, 300);
		Rectangle hpRect = new Rectangle(3, 3, 20, 20, 150, 200);
		Rectangle ruler = new Rectangle(3, rulerYpos, 0, 0, 150, 15);
		Polygon pipes = new Polygon(
				"200,20 153,20 153,30 173,30 173,150 153,150 153,160 173,160 173,280 200,280 200,270 183,270 183,30 200,30");

		Text rulerText1 = new Text("25", 6, rulerYpos + 10, 10, "Verdana");
		rulerText1.setFill("white");
		Text rulerText2 = new Text("45", 73, rulerYpos + 10, 10, "Verdana");
		Text rulerText3 = new Text("75", 135, rulerYpos + 10, 10, "Verdana");

		LinearGradient accLG = new LinearGradient("accLG", "0%", "0%", "0%", "100%");
		LinearGradient hpLG = new LinearGradient("hpLG", "0%", "0%", "0%", "100%");
		LinearGradient rulerLG = new LinearGradient("rulerLG", "0%", "0%", "100%", "0%");

		accLG = addStops(accLG, "HEATING_ACC");
		hpLG = addStops(hpLG, "HEATING_HP");
		rulerLG.addStop(new Stop("0%", "blue"));
		rulerLG.addStop(new Stop("47%", "yellow"));
		rulerLG.addStop(new Stop("53%", "yellow"));
		rulerLG.addStop(new Stop("100%", "red"));

		accRect.setGradient(accLG);
		accRect.setStroke("black");
		accRect.setStrokeWidth("2");

		ruler.setGradient(rulerLG);

		hpRect.setGradient(hpLG);
		hpRect.setStroke("black");
		hpRect.setStrokeWidth("2");

		pipes.setGradient(hpLG);
		pipes.setStroke("black");
		pipes.setStrokeWidth("2");

		svg.addShape(accRect);
		svg.addShape(hpRect);
		svg.addShape(pipes);
		svg.addShape(ruler);
		svg.addShape(rulerText1);
		svg.addShape(rulerText2);
		svg.addShape(rulerText3);
		svg.addLinearGradient(accLG);
		svg.addLinearGradient(hpLG);
		svg.addLinearGradient(rulerLG);
		return svg.toString();
	}

	private LinearGradient addStops(LinearGradient lg, String type) {
		TypedQuery<Sensor> findAllQuery = em
				.createQuery("SELECT DISTINCT s FROM Sensor s LEFT JOIN FETCH s.sensorType st WHERE st.sensorType ='"
						+ type + "' ORDER BY s.name", Sensor.class);
		int i = 0;
		int percentStep = 90 / (findAllQuery.getResultList().size() - 1);
		for (Sensor sensor : findAllQuery.getResultList()) {
			int percent = 5 + i * percentStep;
			lg.addStop(new Stop(percent, "rgb(" + ColorUtil.shaderBYR(sensor.getLastLoggedTemp(), 25, 45, 75) + ")"));
			i++;
		}
		return lg;
	}

}
