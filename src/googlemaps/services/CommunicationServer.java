package googlemaps.services;

import java.io.IOException;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

public class CommunicationServer
{
	private final int NUM_BYTES_PER_PERIOD = 4096;
	private final int PERIOD = 100;
	private final String LOG_TAG = "MapReceiverServer";
	private final Handler serialCommunicationHandler = new Handler();

	private UsbSerialManager communicationManager;

	public CommunicationServer() {}

	public void open(Activity activity)
	{
		communicationManager = new UsbSerialManager(activity);

		// If manager is unable to open USB-Serial connection, then nullify the
		// communication manager so that it won't be used accidentally.
		if (!communicationManager.open()) {
			communicationManager = null;
			return;
		}
	}

	public void close()
	{
		if (communicationManager != null) {
			communicationManager.close();
		}
	}

	public void run(MapService mapService)
	{
		// Exit immediately if communicationManager is null since this means that
		// we weren't able to open a serial connection.
		if (communicationManager == null) {
			return;
		}

		serialCommunicationHandler.post(new Runnable() {

			@Override
			public void run()
			{
				try {
					String line = communicationManager.readLine(NUM_BYTES_PER_PERIOD);
					Log.e(LOG_TAG, line);
				} catch (IOException e) {
					Log.e(LOG_TAG, "Failed to read incoming information from serial port.");
				}
				serialCommunicationHandler.postDelayed(this, PERIOD);
			}
		});

	}
}
