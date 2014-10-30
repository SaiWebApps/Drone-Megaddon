package googlemaps.services;

import googlemaps.intro.MapActivity;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import android.os.Handler;

public class CommunicationServer
{	
	// Serial communication configuration settings
	private final int BAUD_RATE = 115200;
	private final int DATA_BITS = UartConfig.DATA_BITS8;
	private final int STOP_BITS = UartConfig.STOP_BITS1;
	private final int PARITY = UartConfig.PARITY_NONE;
	private final boolean DTR_ON = false;
	private final boolean RTS_ON = false;
	private final UartConfig serialConfig = new UartConfig(BAUD_RATE,
			DATA_BITS, STOP_BITS, PARITY, DTR_ON, RTS_ON);

	// Activity using this CommunicationServer
	private MapActivity mapActivity;
	// Object used to create and manage USB serial communication
	private Physicaloid serialManager;

	public CommunicationServer(MapActivity mapActivity)
	{
		this.mapActivity = mapActivity;
		this.serialManager = new Physicaloid(mapActivity);
	}

	/**
	 * Open a USB serial connection and display a toast message to indicate
	 * success or failure.
	 */
	public void openUSBSerial()
	{
		String toastMessage = "Already connected to device";
		
		// Open a connection if one hasn't already been opened.
		// If we successfully open the connection, then release a Thread to
		// read incoming messages.
		if (!serialManager.isOpened()) {
			if (serialManager.open()) {
				serialManager.setConfig(serialConfig);
				new Thread(new SerialCommunicationReader()).start();
				toastMessage = "Connected to device.";
			}
			else {
				toastMessage = "Unable to connect to device.";
			}
		}
		mapActivity.showToast(toastMessage);
	}

	/**
	 * Close the USB serial connection.
	 */
	public void closeUSBSerial()
	{
		if (!serialManager.close()) {
			mapActivity.showToast("Unable to close serial connection.");
		}
	}

	private class SerialCommunicationReader implements Runnable
	{
		private final int BUFFER_SIZE = 4096;
		
		private Handler readerHandler = new Handler();
		private StringBuilder mText = new StringBuilder();
		
		@Override
		public void run()
		{
			int numBytesRead;
			byte[] rbuf = new byte[BUFFER_SIZE];

			while(true) {
				numBytesRead = serialManager.read(rbuf);
				rbuf[numBytesRead] = 0;
				if (numBytesRead == 0) {
					continue;
				}

				for (int i = 0; i < numBytesRead;  i++) {
					mText.append((char) rbuf[i]);
				}
				readerHandler.post(new Runnable() {
					public void run() {
						mapActivity.setTitle(mText);
						mText.setLength(0);
					}
				});	
			}
		}
	}
}
