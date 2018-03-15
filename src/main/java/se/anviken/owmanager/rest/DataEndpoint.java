package se.anviken.owmanager.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;

import se.anviken.owmanager.model.Temperature;
import se.anviken.owmanager.persist.PersistenceHelper;
import se.anviken.owmanager.utils.DataUtil;

/**
 * 
 */
@Stateless
@Path("/data")
public class DataEndpoint extends PersistenceHelper {
	private static final String YEARS = "years";
	private static final String MONTHS = "months";
	private static final String WEEKS = "weeks";
	private static final String DAYS = "days";
	private static final int DEFAULT_NO_OF_MINUTES = 60;

	@GET
	@Path("/")
	@Produces("application/json")
	public String getAPIDoc() {
		String returnString = "getdatatable API: /getdatatable/{ids}/{noofminutes}\n"
				+ "ids = Sensor ID:s comma separated\n" + "noofminutes = Size of dataset in minutes (Default:"
				+ DataEndpoint.DEFAULT_NO_OF_MINUTES + ")\n\n"

				+ "getdatatable API: /getdatatablebytype/{type}/{noofminutes}\n" + "type = Sensor type\n"
				+ "noofminutes = Size of dataset in minutes (Default:" + DataEndpoint.DEFAULT_NO_OF_MINUTES + ")\n\n"

				+ "getminavgmax API: getminavgmax/{type}/{id}\n"
				+ "type = Type of graph, allowed values:days,weeks,months,years\n" + "id = Sensor ID\n\n"

				+ "getpeaks API: /getpeaks/{id:[0-9][0-9]*}/{noofminutes}/{range}/{minpeakvalue}\n" + "id = Sensor ID\n"
				+ "noofminutes = Size of dataset in minutes (Default:" + DataEndpoint.DEFAULT_NO_OF_MINUTES + ")\n"
				+ "range = The range of the peak (Default:" + DataUtil.DEFAULT_PEAK_RANGE + ")\n"
				+ "minpeakvalue = Minimun value of peak (Default:" + DataUtil.DEFAULT_MIN_PEAK_VALUE + ")\n\n"

				+ "getnoofpeaks API: /getnoofpeaks/{id}/{noofminutes}/{range}/{minpeakvalue}/{trimrange}\n"
				+ "id = Sensor ID\n" + "noofminutes = Size of dataset in minutes (Default:"
				+ DataEndpoint.DEFAULT_NO_OF_MINUTES + ")\n" + "range = The range of the peak (Default:"
				+ DataUtil.DEFAULT_PEAK_RANGE + ")\n" + "minpeakvalue = Minimun value of peak (Default:"
				+ DataUtil.DEFAULT_MIN_PEAK_VALUE + ")\n"
				+ "trimrange = Values outside the trimrange are excluded(Default:" + DataUtil.DEFAULT_TRIM_RANGE
				+ ")\n\n"

				+ "temperature API: /temperature/{id}\n" + "id = Sensor ID\n\n";

		return returnString;
	}

	private List<Temperature> getDataSet(int id) {
		return getDataSet(id, DEFAULT_NO_OF_MINUTES);
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

	/// getdatatablebytype/{type}/{noofminutes}
	@GET
	@Path("/getdatatablebytype/{type}")
	@Produces("application/json")
	public Response getDataTableByType(@PathParam("type") String type) {
		return getDatatableByType(type, DataEndpoint.DEFAULT_NO_OF_MINUTES);
	}

	@GET
	@Path("/getdatatablebytype/{type}/{noofminutes:[0-9][0-9]*}")
	@Produces("application/json")
	public Response getDataTableByTypeAndTime(@PathParam("type") String type,
			@PathParam("noofminutes") int noofminutes) {
		return getDatatableByType(type, noofminutes);
	}

	@GET
	@Path("/getminavgmax/{type}/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response getMinAvgMaxDataTableById(@PathParam("id") int id, @PathParam("type") String type) {

		if (type.equalsIgnoreCase(DAYS) || type.equalsIgnoreCase(WEEKS) || type.equalsIgnoreCase(MONTHS)
				|| type.equalsIgnoreCase(YEARS)) {
			String queryString = getMinAvgMaxQueryString(type, id);

			List<Object[]> resultList = executeNativeQuery(queryString);

			DataTable data = new DataTable();
			ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
			cd.add(new ColumnDescription("Tid", ValueType.TEXT, "Tid"));
			cd.add(new ColumnDescription("Min", ValueType.NUMBER, "Min"));
			cd.add(new ColumnDescription("Avg", ValueType.NUMBER, "Avg"));
			cd.add(new ColumnDescription("Max", ValueType.NUMBER, "Max"));
			data.addColumns(cd);
			for (Object[] record : resultList) {
				TableRow row = new TableRow();
				row.addCell(formatTimeSting(record, type));
				row.addCell(Double.parseDouble(record[0].toString()));
				row.addCell(Double.parseDouble(record[1].toString()));
				row.addCell(Double.parseDouble(record[2].toString()));
				try {
					data.addRow(row);
				} catch (TypeMismatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return Response
					.ok(JsonRenderer.renderDataTable(data, true, false, true).toString(), MediaType.APPLICATION_JSON)
					.build();
		} else {
			return Response.ok("Use types DAYS,WEEKS,MONTH or YEAR").build();
		}
	}

	private String formatTimeSting(Object[] record, String type) {
		if (type.equalsIgnoreCase(DAYS)) {
			return record[3] + "-" + record[4] + "-" + record[5];
		} else if (type.equalsIgnoreCase(YEARS)) {
			return record[3].toString();
		} else {
			return record[3] + "-" + record[4];
		}
	}

	private String getMinAvgMaxQueryString(String type, int id) {
		String baseSelect = "SELECT ROUND(MIN(ta.temperature),1),ROUND(AVG(ta.temperature),1),ROUND(MAX(ta.temperature),1),";
		String typeSelect = "YEAR(ta.temp_timestamp)";
		if (type.equalsIgnoreCase(DAYS)) {
			typeSelect += ",MONTH(ta.temp_timestamp),DAY(ta.temp_timestamp)";
		} else if (type.equalsIgnoreCase(WEEKS)) {
			typeSelect += ",WEEK(ta.temp_timestamp)";
		} else if (type.equalsIgnoreCase(MONTHS)) {
			typeSelect += ",MONTH(ta.temp_timestamp)";
		}
		String fromString = "FROM temperatures_archive ta " + "WHERE ta.sensor_id = " + id + " ";
		String groupBy = "GROUP BY " + typeSelect;
		return baseSelect + typeSelect + fromString + groupBy;
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

	@GET
	@Path("/getnoofpeaks/{id:[0-9][0-9]*}/{noofminutes:[0-9][0-9]*}/{range:[0-9][0-9]*}/{minpeakvalue:[0-9][0-9]*}/{trimrange:[0-9][0-9]*}")
	@Produces("application/json")
	public int getNoOfPeaksByIdTimeRangeAndMinValue(@PathParam("id") int id, @PathParam("noofminutes") int noofminutes,
			@PathParam("range") int range, @PathParam("minpeakvalue") int minpeakvalue,
			@PathParam("trimrange") int trimrange) {
		return DataUtil.FindPeaksInDataset(getDataSet(id, noofminutes), range, minpeakvalue, trimrange).size();
	}

	@GET
	@Path("/temperature/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response getTemperatureById(@PathParam("id") int id) {
		return Response.ok(getTemperature(id)).build();

	}

}