package drone_megaddon.communication;

import googlemaps.intro.MapActivity;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import drone_megaddon.ui_services.MapService;

import android.os.Handler;

/**
 * Handle USB-Serial communications between the Android app
 * and the Firefly node. 
 */
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

    /**
     * @param mapActivity - MapActivity requesting the creation of 
     * a CommunicationServer; used to access MapServe
     */
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
        if (serialManager.isOpened()) {
        	mapActivity.showToast("Already connected to device.");
        	return;
        }
        if (!serialManager.open()) {
        	mapActivity.showToast("Unable to connect to device.");
        	return;
        }
        
        serialManager.setConfig(serialConfig);
        new Thread(new SerialCommunicationReader()).start();        
        mapActivity.showToast("Connected to device successfully.");
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

    /**
     * Write the given message onto the USB serial connection.
     */
    public void write(String message)
    {
    	byte[] messageBytes = message.getBytes();
    	serialManager.write(messageBytes, messageBytes.length);
    }
    
    /**
     * Continuously read incoming information from the Firefly (via USB-Serial connection). 
     */
    private class SerialCommunicationReader implements Runnable
    {
    	private final char END_OF_MESSAGE = 255;
    	
        private Handler readerHandler = new Handler();
        private StringBuilder receiveBuffer = new StringBuilder();
        private CommandFactory commandFactory = new CommandFactory();
        private MapService mapService = mapActivity.getMapService();
        
        /**
         * Add the received byte to an internal character buffer if it doesn't
         * mark the end-of-the-message. Otherwise, use the CommandFactory to
         * construct a command using the message, and queue the command for service
         * in MapService.
         * @param receivedByte - byte received from Firefly (via USB-Serial connection)
         */
        private void handleReceivedByte(byte receivedByte)
        {
        	char receivedChar = (char) receivedByte;
        	
        	if (receivedChar != END_OF_MESSAGE) {
        		receiveBuffer.append(receivedChar);
        		return;
        	}
        	readerHandler.post(new Runnable() {
        		
        		@Override
        		public void run() 
        		{	
        			Command command = commandFactory.create(receiveBuffer.toString());
        			if (command != null) {
        				mapService.queueCommand(command);
        			}
        			receiveBuffer.setLength(0); // Clear buffer.
        		}
        	});
        }
        
        @Override
        public void run()
        {
            int numBytesRead;
            // rbuf[0] = received byte, rbuf[1] = null byte (0x00)
            byte[] rbuf = new byte[2];

            while(true) {
                // Read incoming byte.
                numBytesRead = serialManager.read(rbuf, 1);
                rbuf[numBytesRead] = 0;
                
                // Skip this iteration and try again if we read nothing.
                if (numBytesRead == 0) {
                    continue;
                }
                handleReceivedByte(rbuf[0]);
            }
        }
    }
}
