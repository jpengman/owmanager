package se.anviken.owmanager.utils;

import java.util.ArrayList;
import java.util.List;

import se.anviken.owmanager.model.Temperature;

public class DataUtil {
	public static final int DEFAULT_MIN_PEAK_VALUE = -256;
	public static final int DEFAULT_PEAK_RANGE = 10;
	public static final int DEFAULT_TRIM_RANGE = 3;

	public static List<Temperature> FindPeaksInDataset(List<Temperature> points) {
		return FindPeaksInDataset(points, DEFAULT_PEAK_RANGE, DEFAULT_MIN_PEAK_VALUE, DEFAULT_TRIM_RANGE);
	}

	public static List<Temperature> FindPeaksInDataset(List<Temperature> points, int range) {
		return FindPeaksInDataset(points, range, DEFAULT_MIN_PEAK_VALUE, DEFAULT_TRIM_RANGE);
	}

	public static List<Temperature> FindPeaksInDataset(List<Temperature> dataset, int range, int minpeakvalue) {
		return FindPeaksInDataset(dataset, range, minpeakvalue, DEFAULT_TRIM_RANGE);
	}

	public static List<Temperature> FindPeaksInDataset(List<Temperature> dataset, int range, int minpeakvalue,
			int trim) {
		List<Temperature> result = new ArrayList<Temperature>();
		if (dataset == null) {
			return null;
		}
		int l, r;
		for (int i = trim; i + trim < dataset.size(); i++) {
			boolean isPeak = true;
			if (dataset.get(i).getTemperature() < minpeakvalue) {
				isPeak = false;
				continue;
			}
			l = Math.max(0, i - range);
			r = Math.min(dataset.size() - 1, i + range);
			for (int j = l; j <= r; j++) {
				if (i == j) {
					continue;
				}
				if (dataset.get(i).getTemperature() < dataset.get(j).getTemperature()) {
					isPeak = false;
					break;
				}
			}
			if (isPeak) {
				result.add(dataset.get(i));
				i += range;
			}
		}
		return result;
	}
}
