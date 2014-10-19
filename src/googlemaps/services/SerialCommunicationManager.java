package googlemaps.services;

import java.io.IOException;
import java.util.List;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class SerialCommunicationManager 
{
	private final String LOG_TAG = getClass().getName();
	private final int BAUD_RATE = 115200;

	private UsbManager usbManager;
	private UsbSerialPort serialPort;

	public SerialCommunicationManager(Activity activity)
	{
		this.usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE); 
	}

	private UsbSerialDriver getFirstDriver()
	{	
		List<UsbSerialDriver> availableDrivers =
				UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
		if (availableDrivers == null || availableDrivers.isEmpty()) {
			return null;
		}
		return availableDrivers.get(0);
	}
	
	private UsbSerialPort getFirstSerialPort(UsbSerialDriver driver)
	{
		UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
		if (connection == null) {
			return null;
		}
		
		List<UsbSerialPort> allPorts = driver.getPorts();
		if (allPorts == null || allPorts.isEmpty()) {
			return null;
		}
		return allPorts.get(0);
	}
	
	private boolean setupSerialPort()
	{
		try {
			serialPort.setParameters(BAUD_RATE,
					UsbSerialPort.DATABITS_8, 0, UsbSerialPort.PARITY_NONE);
			return true;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Unable to properly initialize serial port");
			return false;
		}
	}
	
	public boolean open()
	{
		UsbSerialDriver driver = getFirstDriver();		
		if (driver == null) {
			return false;
		}
		
		serialPort = getFirstSerialPort(driver);
		if (serialPort == null) {
			return false;
		}
		return setupSerialPort();
	}

	public void close()
	{
		try {
			serialPort.close();
		} catch (IOException e) {
			Log.e(LOG_TAG, "Unable to close serial port.");
		}
	}

	public String readLine(int targetNumBytes) throws IOException
	{
		byte[] readBuffer = new byte[targetNumBytes];
		serialPort.read(readBuffer, 0);
		return new String(readBuffer);
	}
}