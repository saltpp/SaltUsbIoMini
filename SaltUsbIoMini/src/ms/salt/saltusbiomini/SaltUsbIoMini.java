package ms.salt.saltusbiomini;

import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class SaltUsbIoMini {
	private final static String TAG = "SaltUsbIoMini";

	// from <V-USB>/examples/hid-data/commandline/hiddata.c
	private final static int USBRQ_HID_GET_REPORT = 0x01;		
	private final static int USBRQ_HID_SET_REPORT = 0x09;
	private final static int USB_HID_REPORT_TYPE_FEATURE = 3;

	// USB Vendor ID, Product ID
	private final static int VUSB_VENDOR_ID  = 0x16C0;
	private final static int VUSB_PRODUCT_ID = 0x05DF;

	// USB_IO_MINI_COMMAND
	public final static byte UIMC_WRITE_DATA         = 0x00;	// write [0x00], [data], [mask], [any value]x5
	public final static byte UIMC_WRITE_DEVICE_ID    = 0x01;	// write [0x01], [id], [any value]x6
	public final static byte UIMC_SET_DIRECTION      = 0x02;	// write [0x02], [direction] (1 = output, 0 = input), [any value]x6

	public final static byte UIMC_SET_READ_MODE_D4A0 = 0x40;	// write [0x40], [any value]x7 / read --> [0x40], [0, 0, 0, 0, PB5, PB4, PB3, PB0]
	public final static byte UIMC_SET_READ_MODE_D3A1 = 0x41;	// write [0x41], [any value]x7 / read --> [0x41], [0, 0, 0, 0, 0,   PB4, PB3, PB0], [ADC0L(PB5)], [ADC0H(PB5)]
	public final static byte UIMC_SET_READ_MODE_D2A2 = 0x42;	// write [0x42], [any value]x7 / read --> [0x42], [0, 0, 0, 0, 0,   0,   PB3, PB0], [ADC0L(PB5)], [ADC0H(PB5)], [ADC2L(PB4)], [ADC2H(PB4)]
	public final static byte UIMC_SET_READ_MODE_D1A3 = 0x43;	// write [0x43], [any value]x7 / read --> [0x43], [0, 0, 0, 0, 0,   0,   0,   PB0], [ADC0L(PB5)], [ADC0H(PB5)], [ADC2L(PB4)], [ADC2H(PB4)], [ADC3L(PB3)], [ADC3H(PB3)]

	public final static byte UIMC_READ_VERSION       = -127;	// write [0x81], [any value]x7 / raed --> [0x81], [Version]
	public final static byte UIMC_READ_DEVICE_ID     = -126;	// write [0x82], [any value]x7 / read --> [0x82], [Device ID]

	private String ACTION_USB_PERMISSION = "ms.salt.saltusbiomini.USB_PERMISSION";

	private Context mContext;
	private UsbManager mUsbManager;
	private PendingIntent mPendingIntentPermission;
	private UsbDeviceConnection mConnection; 
	private UsbInterface mUsbInterface; 
	private boolean mbRequestedPermission;

	
	// listener
	public interface OnInitializedListener {
		public void onInitialized();
	}
	OnInitializedListener mOnInitializedListener;
	public void setOnInitializedListener(OnInitializedListener listener) {
		mOnInitializedListener = listener;
	}
	
	private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				for (int i = 0; i < device.getInterfaceCount(); ++i) {
					mUsbInterface = device.getInterface(i);
					if (mUsbInterface != null) {
						if (mUsbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_HID) {
							UsbEndpoint ue = mUsbInterface.getEndpoint(0);	// endpoint zero
							if (ue != null) {
								mConnection = mUsbManager.openDevice(device);
								if (mConnection != null) {
									boolean b = mConnection.claimInterface(mUsbInterface, true);
									if (b) {
										if (mOnInitializedListener != null) {
											mOnInitializedListener.onInitialized();
										}
									}
									else {
										Log.d(TAG, "claimInterface() failed");
										// mConnection.releaseInterface(mUsbInterface);
										mConnection.close();
										mConnection = null;
									}
								}
								else {
									Log.d(TAG, "openDevice() failed.");
								}
							}
						}
					}
				}
			}
		}
	};

	public SaltUsbIoMini(Context context) {
		mContext = context;
	}


	@SuppressLint("NewApi")
	public void connect() {
		if (mContext != null) {
			mbRequestedPermission = false;
			mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
			HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
			
			Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
			while(deviceIterator.hasNext()){
				UsbDevice device = deviceIterator.next();
				
				if (device.getVendorId() == VUSB_VENDOR_ID && device.getProductId() == VUSB_PRODUCT_ID) {
					// found the V-USB device
					// TODO: need to check manufacture string and product string
					mPendingIntentPermission = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);  
					IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);  
					mContext.registerReceiver(mUsbReceiver, filter);  
					mUsbManager.requestPermission(device, mPendingIntentPermission);
					mbRequestedPermission = true;
				}
			}
		}
	}
	
	@SuppressLint("NewApi")
	public void disconnect() {
		if (mConnection != null) {
			mConnection.releaseInterface(mUsbInterface);
			mConnection.close();
			mConnection = null;
		}
		if (mContext != null) {
			if (mbRequestedPermission) {
				mContext.unregisterReceiver(mUsbReceiver);
			}
		}
		mbRequestedPermission = false;
	}

	
	@SuppressLint("NewApi")
	private boolean hidGetReport(byte [] data) {
		if (mConnection != null) {
			int nTransferred = mConnection.controlTransfer(
					UsbConstants.USB_TYPE_CLASS | UsbConstants.USB_DIR_IN,		// input
					USBRQ_HID_GET_REPORT, USB_HID_REPORT_TYPE_FEATURE << 8,
					0,
					data, data.length, 0);
			return  (nTransferred > 0) ? true : false;
		}
		return false;
	}
	@SuppressLint("NewApi")
	private boolean hidSetReport(byte [] data) {
		if (mConnection != null) {
			int nTransferred = mConnection.controlTransfer(
					UsbConstants.USB_TYPE_CLASS | UsbConstants.USB_DIR_OUT,		// output
					USBRQ_HID_SET_REPORT, USB_HID_REPORT_TYPE_FEATURE << 8,
					0,
					data, data.length, 0);
			
			return  (nTransferred > 0) ? true : false;
		}
		return false;
	}
	
	private boolean read(byte [] data) {
		return hidGetReport(data);
	}
	
	private boolean write(byte cmd) {
		byte[] data = new byte[8];
		data[0] = cmd;
		return hidSetReport(data);
	}
	private boolean write(byte cmd, byte data1) {
		byte[] data = new byte[8];
		data[0] = cmd;
		data[1] = data1;
		return hidSetReport(data);
	}
	private boolean write(byte cmd, byte data1 ,byte data2) {
		byte[] data = new byte[8];
		data[0] = cmd;
		data[1] = data1;
		data[2] = data2;
		return hidSetReport(data);
	}
	
	/*
	 * @param mode UIMC_SET_READ_MODE_D4A0, D3A1, D2A2, D1A3
	 */
	public boolean setMode(byte mode) {
		return write(mode);
	}
	
	/*
	 * @param dir direction, 1 means output, 0 means input (High-Z), [0 ,0, 0, 0, PB5, PB4, PB3, PB0]
	 */
	public boolean setDirection(byte dir) {
		return write(UIMC_SET_DIRECTION, dir);
	}
	
	/*
	 * @param data 1 means high, 0 means low, [0 ,0, 0, 0, PB5, PB4, PB3, PB0]
	 * @param mask 1 means to change output, 0 means not change
	 */
	public boolean setData(byte data, byte mask) {
		return write(UIMC_WRITE_DATA, data, mask);
	}
	
	public boolean getData(byte [] data) {
		return read(data);
	}
}
