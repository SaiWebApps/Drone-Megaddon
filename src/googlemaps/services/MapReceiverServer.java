package googlemaps.services;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class MapReceiverServer extends Thread
{
	private final int PORT_NUMBER = 8080;
	private final String LOG_TAG = "MapReceiverServer";

	private MapService mapService;
	private ServerSocket serverSocket;

	public MapReceiverServer(MapService mapService)
	{
		try {
			this.mapService = mapService;
			this.serverSocket = new ServerSocket(PORT_NUMBER);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Unable to initialize ServerSocket at port " + PORT_NUMBER);
		}
	}

	public void close()
	{
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Unable to close ServerSocket");
		}
	}

	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
		{
			try {
				final Socket connection = serverSocket.accept();
				ReceiverThread receiver = new ReceiverThread(connection, mapService);
				receiver.start();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
		}
	}
}
