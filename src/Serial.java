import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;


public class Serial implements SerialPortEventListener {
	
	SerialPort serialPort;

	private static final String PORT_NAMES[] = {"/dev/tty.usbserial-A9007UX1", // Mac OS X
										        "/dev/ttyUSB0", // Linux
										        "COM3", // Windows
	};
	
	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 115200;
	private SpacePanel panel;
	
	public Serial(String ncom, SpacePanel sp){	
		panel = sp;
		if(Integer.parseInt(ncom)>=3 && Integer.parseInt(ncom)<=9)
			PORT_NAMES[2] = "COM" + ncom;	
		initialize();
		Thread t = new Thread() {
			public void run() {
				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
			}
		};
		t.start();
		System.out.println("Serial Comms Started");
	}
	
	public void initialize() {
	    CommPortIdentifier portId = null;
	    Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();	
	    
	    while (portEnum.hasMoreElements()) {
	        CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
	        for (String portName : PORT_NAMES) {
	            if (currPortId.getName().equals(portName)) {
	                portId = currPortId;
	                break;
	            }
	        }
	    }
	    if (portId == null) {
	    	
	        System.out.println("Could not find COM port.");
	        return;
	    }
	
	    try {
	    	
	        serialPort = (SerialPort) portId.open(this.getClass().getName(),
	                TIME_OUT);
	        serialPort.setSerialPortParams(DATA_RATE,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE);
	
	        // open the streams
	        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
	        output = serialPort.getOutputStream();
	
	        serialPort.addEventListener(this);
	        serialPort.notifyOnDataAvailable(true);
	        
	    } catch (Exception e) {
	    	
	        System.err.println(e.toString());
	    }
	}
	
	
	public synchronized void close() {
		
	    if (serialPort != null) {
	        serialPort.removeEventListener();
	        serialPort.close();
	    }
	}
	
	public synchronized void send(int b){
		
		try{
			
			output.write(b);
			
		}catch (Exception e) {
			
			System.err.println(e.toString());
		}
	}
	
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		
	    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
	    	
	        try {
	        	
	            String inputLine = null;
	            
	            if (input.ready()) {
	            	
	                inputLine = input.readLine();
	                panel.read(inputLine);
	            }
	
	        } catch (Exception e) {
	        	
	            System.err.println(e.toString());
	        }
	    }	    
	}
}