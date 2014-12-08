package drone_megaddon.communication;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;

import googlemaps.intro.MapActivity;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import drone_megaddon.communication.rx.RxCommandFactory;
import drone_megaddon.communication.rx.RxCommand;
import drone_megaddon.communication.tx.TxCommand;
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
    
    // Handle transmissions from device to drone.
    private final int TRANSMIT_PERIOD = 1000; //ms
    private LinkedBlockingDeque<TxCommand> pendingTransmissions;
    private Handler transmissionHandler;
    
    /**
     * @param mapActivity - MapActivity requesting the creation of 
     * a CommunicationServer; used to access MapServe
     */
    public CommunicationServer(MapActivity mapActivity)
    {
        this.mapActivity = mapActivity;
        this.serialManager = new Physicaloid(mapActivity);
        this.pendingTransmissions = new LinkedBlockingDeque<TxCommand>();
        this.transmissionHandler = new Handler();
    }

    /**
     * Open a USB serial connection and display a toast message to indicate
     * success or failure.
     */
    public void openUSBSerial()
    {
    	// Make sure that a USB-Serial connection is not already open and that
    	// we can open a connection successfully.
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
        transmissionHandler.post(new Transmitter());
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
    
    public void queueTxCommand(TxCommand txCommand)
    {
    	pendingTransmissions.add(txCommand);
    }
    
    /**
     * Continuously read incoming information from the Firefly (via USB-Serial connection). 
     */
    private class SerialCommunicationReader implements Runnable
    {
    	private final char END_OF_MESSAGE = '\n';
    	
        private Handler readerHandler = new Handler();
        private StringBuilder receiveBuffer = new StringBuilder();
        private RxCommandFactory rxCommandFactory = new RxCommandFactory();
        private MapService mapService = mapActivity.getMapService();
        
        /**
         * Add the received byte to an internal character buffer if it doesn't
         * mark the end-of-the-message. Otherwise, use the RxCommandFactory to
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
        			RxCommand command = rxCommandFactory.create(receiveBuffer.toString());
        			if (command != null) {
        				mapService.queueRxCommand(command);
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
    
    private class Transmitter implements Runnable
    {
        private void write(String message)
        {
        	if (message == null || message.isEmpty()) {
        		return;
        	}
        	
        	byte[] messageBytes = message.getBytes();
        	int numBytes = messageBytes.length;
        	
        	messageBytes = Arrays.copyOf(messageBytes, numBytes + 1);
        	messageBytes[numBytes] = -1;
        	serialManager.write(messageBytes, numBytes + 1);
        }
               
    	@Override
    	public void run()
    	{
    		// Keep polling queue to check if any new transmission commands have arrived.
    		if (pendingTransmissions.isEmpty()) {
    			StringBuffer abcd = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ Now You know the abcs");
    			write(abcd.toString());
    			transmissionHandler.postDelayed(this, TRANSMIT_PERIOD);
    			return;
    		}

    		// If something has arrived, pop from queue, and write it to drone.
    		TxCommand command = pendingTransmissions.remove();
    		serialManager.write(command.getMessageBytes());
    		transmissionHandler.postDelayed(this, TRANSMIT_PERIOD);
    	}
    }
}
