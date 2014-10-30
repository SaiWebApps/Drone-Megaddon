package googlemaps.services;

import java.util.Arrays;
import java.util.List;

public class GPSParser {
	private int index = 0;
	
	public class Coordinates {
		public double UTC;
		public double latitude;
		public double longitude;
		public int quality;
		public int numSats;
		public float hPrecise;
		public double altitudeGps;
		
		public Coordinates() {
			this.UTC = -1;
			this.latitude = -1;
			this.longitude = -1;
			this.quality = -1;
			this.numSats = -1;
			this.hPrecise = -1;
			this.altitudeGps = -1;
		}
	}
	
	public Coordinates gpsParseLine (String line) {
		char NorS;
		char EorW;
		
		List<String> lineList = Arrays.asList(line.split(","));
		if (!lineList.get(0).equals("$GPGGA") || lineList.size() != 15) {
			System.out.print("lineList size is: "); 
			System.out.println(lineList.size());
//			for (int i = 0; i < lineList.size(); ++i) {
//				System.out.print(lineList.get(i) + " ");
//			}
			return null;
		}
		
		Coordinates coord = new Coordinates();
		coord.UTC = Double.parseDouble(lineList.get(1));
		System.out.println(coord.UTC);
		coord.latitude = nmeaToDecimal(Double.parseDouble(lineList.get(2)));

		NorS = lineList.get(3).charAt(0);
		coord.longitude = nmeaToDecimal(Double.parseDouble(lineList.get(4)));
		EorW = lineList.get(5).charAt(0);
		coord.quality = Integer.parseInt(lineList.get(6));
		coord.numSats = Integer.parseInt(lineList.get(7));
		coord.hPrecise = Float.parseFloat(lineList.get(8));
		coord.altitudeGps = Double.parseDouble(lineList.get(9));
		
		index += 1;
		
		if (NorS == 'S') 
			coord.latitude *= -1;
		if (EorW == 'W')
			coord.longitude *= -1;
		
		return coord;
	}
	
	private double nmeaToDecimal (double coordinate) {
		double end = coordinate % 100.0; 
		return (coordinate - end / 100.0) + (end / 60.0);
	}
	
	private boolean validNMEAChecksum (String packet) {
		final int startChars = 1;
		final int endChars = 5;
		long computedChecksum = 0;
		return true;
	}
	
	public int getIndex() {
		return index;
	}
}
