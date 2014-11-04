package googlemaps.services;

import googlemaps.intro.MapActivity;
import googlemaps.services.GPSParser.Coordinates;

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

	// Mode definitions
	private final int ASCII = 1;
	private final int SLIP = 0;

	// Slipstream byte definitions
	private final int MAX_SLIP_BUF = 1024;
	private final byte ESC = (byte) 0xDB;
	private final byte START = (byte) 0xC1;
	private final byte END = (byte) 0xC0;
	private final byte ESC_END = (byte) 0xDC;
	private final byte ESC_ESC = (byte) 0xDD;


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
	private int slipTxAck(byte[] newbuf, byte size) {
		int seqNum = (newbuf[1] << 8) + newbuf[1];
		byte checksum = 0;
		byte[] sendByte = new byte[1];

		if (size > 128) {
			return 0;
		}
		sendByte[0] = END; 
		while (serialManager.write(sendByte, 1) < 0);
		sendByte[0] = END; 
		while (serialManager.write(sendByte, 1) < 0);
		sendByte[0] = START; 
		while (serialManager.write(sendByte, 1) < 0);
		sendByte[0] = size; 
		while (serialManager.write(sendByte, 1) < 0);
		
		// Send data in newbuf
		for (int i = 0; i < size; ++i) {
			switch (newbuf[i]) {
			case END:
				sendByte[0] = ESC; 
				while (serialManager.write(sendByte, 1) < 0);
				checksum += END;
				sendByte[0] = ESC_END;
				while (serialManager.write(sendByte, 1) < 0);
				break;
			case ESC:
				sendByte[0] = ESC;
				while (serialManager.write(sendByte, 1) < 0);
				checksum += ESC;
				sendByte[0] = ESC_END;
				while (serialManager.write(sendByte, 1) < 0);
				break;
			default:
				sendByte[0] = newbuf[i];
				while (serialManager.write(sendByte, 1) < 0);
				checksum += sendByte[0];
			}
		}
		// Send checksum and END
		checksum &= 0x7F; // checksum < 128, prevent from becoming ctrl msg
		sendByte[0] = checksum;
		while (serialManager.write(sendByte, 1) < 0);
		sendByte[0] = END;
		while (serialManager.write(sendByte, 1) < 0);
		
		return 0;
	}

	private class SerialCommunicationReader implements Runnable
	{
		private final int BUFFER_SIZE = 4096;

		private Handler readerHandler = new Handler();
		private StringBuilder mText = new StringBuilder();
		private boolean slipRdytoRead = false;

		@Override
		public void run()
		{
			int numBytesRead;
			int serialIdx = 0;
			byte[] rbuf = new byte[BUFFER_SIZE];
			byte[] cbyte = new byte[1]; // new char byte

			while (true) {
				numBytesRead = serialManager.read(cbyte, 1);
				if (numBytesRead <= 0) {
					return;
				}

				if (cbyte[0] == START) {
					slipRx();							
				}

				rbuf[serialIdx++] = cbyte[0];
				if (cbyte[0] == '\n') {
					rbuf[serialIdx++] = '\0';
					serialIdx = 0;
					// Transfer bytes to text buffer, so that we
					// can process the message as a String.
					for (int i = 0; i < numBytesRead;  i++) {
						mText.append((char) rbuf[i]);
					}
					readerHandler.post(new Runnable() {
						public void run() {
							String received = mText.toString();						
							mapActivity.notifyMapService(received);
							mText.setLength(0);
						}
					});
				}
				//				rbuf[numBytesRead] = 0;
				//				if (numBytesRead == 0) {
				//					continue;
				//				}
				try {
					Thread.currentThread().sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private boolean slipChecksum(byte[] slipBuf, int received) {
			byte checksum = 0;
			for (int i = 1; i < received - 1; ++i) {
				checksum += slipBuf[i];
			}
			checksum &= 0x7F;
			return checksum == slipBuf[received - 1];
		}

		private void slipRx() {
			int mode = SLIP;
			int numBytesRead;
			int received = 0;
			int res = 0;
			byte[] sbyte = new byte[1]; // new slip byte
			byte[] slipBuf = new byte[MAX_SLIP_BUF];

			if (slipRdytoRead != false) {
				return;
			}

			while(true) {
				numBytesRead = serialManager.read(sbyte, 1);
				if (numBytesRead > 0) {
					switch (sbyte[0]) {
					case END: 
						if (received != 0) {
							byte size;
							size = slipBuf[0];
							if ((received - 2) != size) {
								break;
							}
							if (!slipChecksum(slipBuf, received)) {
								break;
							}
							// valid buffer received; now process it!
							//							server_tx
						}
						break;
					case ESC:
						do {
							res = serialManager.read(sbyte, 1);
						} while (res < 0); // assuming read returns -1 on error?

						switch (sbyte[0]) {
						case ESC_END:
							sbyte[0] = END;
							break;
						case ESC_ESC:
							sbyte[0] = ESC;
							break;
						}
					default: 
						if (received < MAX_SLIP_BUF) {
							slipBuf[received++] = sbyte[0];
						}
					}
				}
				slipRdytoRead = true;
			}
		}
	}
}
