package googlemaps.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.util.Log;

public class ReceiverThread extends Thread
{
	private final String LOG_TAG = "ReceiverThread";
	
	private Socket incomingConnection;
	private MapService mapService;
	
	public ReceiverThread(Socket incomingConnection, MapService mapService)
	{
		this.incomingConnection = incomingConnection;
		this.mapService = mapService;
	}

	public void close(BufferedReader reader)
	{
		if (reader == null) {
			return;
		}
		
		try {
			reader.close();
		} catch(IOException e) {
			Log.e(LOG_TAG, "Unable to close connection and release resources.");
		}
	}
	
	public void run()
	{
		BufferedReader reader = null;
		String line = null;
		
		try {
			reader = new BufferedReader(
					new InputStreamReader(incomingConnection.getInputStream()));
			
			while ((line = reader.readLine()) != null) {
				mapService.saveReceivedInfo(line);
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error reading information from drone.");
		} finally {
			// Always release resources once this connection expires.
			close(reader);
		}
	}
}