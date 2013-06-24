package ms.salt.testsaltusbiomini;

import ms.salt.saltusbiomini.SaltUsbIoMini;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;


public class TestSaltUsbIoMiniActivity extends Activity {
	private final static String TAG = "TestSaltUsbIoMiniActivity";

	private CheckBox mcbDirPB5;
	private CheckBox mcbDirPB4;
	private CheckBox mcbDirPB3;
	private CheckBox mcbDirPB0;
	
	private ToggleButton mtbOutputPB5;
	private ToggleButton mtbOutputPB4;
	private ToggleButton mtbOutputPB3;
	private ToggleButton mtbOutputPB0;
	
	private CheckBox mcbInputPB5;
	private CheckBox mcbInputPB4;
	private CheckBox mcbInputPB3;
	private CheckBox mcbInputPB0;
	
	private SeekBar msbInputPB5;
	private SeekBar msbInputPB4;
	private SeekBar msbInputPB3;
	
	private SaltUsbIoMini mSaltUsbIoMini;

	private Thread mThread;
	private boolean mbAbort;

	
	private void initSaltUsbIoMini(final byte mode) {
		
		uninitSaltUsbIoMini();	// uninitialize if it is already used
		
		mSaltUsbIoMini = new SaltUsbIoMini(this);
		SaltUsbIoMini.OnInitializedListener onInitializedListener = new SaltUsbIoMini.OnInitializedListener() {
			@Override
			public void onInitialized() {
				Toast.makeText(TestSaltUsbIoMiniActivity.this, "Initialized SaltUsbIoMini", Toast.LENGTH_LONG).show();
				mSaltUsbIoMini.setMode(mode);
			}
		};
		
		mSaltUsbIoMini.setOnInitializedListener(onInitializedListener);	// onInitializedListener will be called back after calling SaltUsbIoMini.connect()
		mSaltUsbIoMini.connect();
	}
	private void uninitSaltUsbIoMini() {
		if (mSaltUsbIoMini != null) {
			mSaltUsbIoMini.disconnect();
			mSaltUsbIoMini = null;
		}
	}
	
	private void initViews(final byte mode) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
				mcbInputPB5.setEnabled(false);
				mcbInputPB4.setEnabled(false);
				mcbInputPB3.setEnabled(false);
				mcbInputPB0.setEnabled(false);

				msbInputPB5.setEnabled(false);
				msbInputPB4.setEnabled(false);
				msbInputPB3.setEnabled(false);

				switch (mode) {
				case SaltUsbIoMini.UIMC_SET_READ_MODE_D4A0:
					mcbDirPB5.setVisibility(CheckBox.VISIBLE);
					mcbDirPB4.setVisibility(CheckBox.VISIBLE);
					mcbDirPB3.setVisibility(CheckBox.VISIBLE);
					mcbDirPB0.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB5.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB4.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB3.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB0.setVisibility(CheckBox.VISIBLE);
					mcbInputPB5.setVisibility(CheckBox.VISIBLE);
					mcbInputPB4.setVisibility(CheckBox.VISIBLE);
					mcbInputPB3.setVisibility(CheckBox.VISIBLE);
					mcbInputPB0.setVisibility(CheckBox.VISIBLE);
					msbInputPB5.setVisibility(SeekBar.INVISIBLE);
					msbInputPB4.setVisibility(SeekBar.INVISIBLE);
					msbInputPB3.setVisibility(SeekBar.INVISIBLE);
					break;
				case SaltUsbIoMini.UIMC_SET_READ_MODE_D3A1:
					mcbDirPB5.setVisibility(CheckBox.INVISIBLE);
					mcbDirPB4.setVisibility(CheckBox.VISIBLE);
					mcbDirPB3.setVisibility(CheckBox.VISIBLE);
					mcbDirPB0.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB5.setVisibility(CheckBox.INVISIBLE);
					mtbOutputPB4.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB3.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB0.setVisibility(CheckBox.VISIBLE);
					mcbInputPB5.setVisibility(CheckBox.INVISIBLE);
					mcbInputPB4.setVisibility(CheckBox.VISIBLE);
					mcbInputPB3.setVisibility(CheckBox.VISIBLE);
					mcbInputPB0.setVisibility(CheckBox.VISIBLE);
					msbInputPB5.setVisibility(SeekBar.VISIBLE);
					msbInputPB4.setVisibility(SeekBar.INVISIBLE);
					msbInputPB3.setVisibility(SeekBar.INVISIBLE);
					msbInputPB5.setMax(1024);	// 10bits
					break;
				case SaltUsbIoMini.UIMC_SET_READ_MODE_D2A2:
					mcbDirPB5.setVisibility(CheckBox.INVISIBLE);
					mcbDirPB4.setVisibility(CheckBox.INVISIBLE);
					mcbDirPB3.setVisibility(CheckBox.VISIBLE);
					mcbDirPB0.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB5.setVisibility(CheckBox.INVISIBLE);
					mtbOutputPB4.setVisibility(CheckBox.INVISIBLE);
					mtbOutputPB3.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB0.setVisibility(CheckBox.VISIBLE);
					mcbInputPB5.setVisibility(CheckBox.INVISIBLE);
					mcbInputPB4.setVisibility(CheckBox.INVISIBLE);
					mcbInputPB3.setVisibility(CheckBox.VISIBLE);
					mcbInputPB0.setVisibility(CheckBox.VISIBLE);
					msbInputPB5.setVisibility(SeekBar.VISIBLE);
					msbInputPB4.setVisibility(SeekBar.VISIBLE);
					msbInputPB3.setVisibility(SeekBar.INVISIBLE);
					msbInputPB5.setMax(1024);	// 10bits
					msbInputPB4.setMax(1024);	// 10bits
					break;
				case SaltUsbIoMini.UIMC_SET_READ_MODE_D1A3:
					mcbDirPB5.setVisibility(CheckBox.INVISIBLE);
					mcbDirPB4.setVisibility(CheckBox.INVISIBLE);
					mcbDirPB3.setVisibility(CheckBox.INVISIBLE);
					mcbDirPB0.setVisibility(CheckBox.VISIBLE);
					mtbOutputPB5.setVisibility(CheckBox.INVISIBLE);
					mtbOutputPB4.setVisibility(CheckBox.INVISIBLE);
					mtbOutputPB3.setVisibility(CheckBox.INVISIBLE);
					mtbOutputPB0.setVisibility(CheckBox.VISIBLE);
					mcbInputPB5.setVisibility(CheckBox.INVISIBLE);
					mcbInputPB4.setVisibility(CheckBox.INVISIBLE);
					mcbInputPB3.setVisibility(CheckBox.INVISIBLE);
					mcbInputPB0.setVisibility(CheckBox.VISIBLE);
					msbInputPB5.setVisibility(SeekBar.VISIBLE);
					msbInputPB4.setVisibility(SeekBar.VISIBLE);
					msbInputPB3.setVisibility(SeekBar.VISIBLE);
					msbInputPB5.setMax(1024);	// 10bits
					msbInputPB4.setMax(1024);	// 10bits
					msbInputPB3.setMax(1024);	// 10bits
					break;
				}
			}
		});
	}
	private void setDigitalInputView(final byte data) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Log.d(TAG, "D: data = " + data);
				mcbInputPB5.setChecked((data & 0x08) == 0x08);
				mcbInputPB4.setChecked((data & 0x04) == 0x04);
				mcbInputPB3.setChecked((data & 0x02) == 0x02);
				mcbInputPB0.setChecked((data & 0x01) == 0x01);
			}
		});
	}
	private void setAnalogInputView(final int nPB5) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Log.d(TAG, "A: PB5 = " + nPB5);
				msbInputPB5.setProgress(nPB5);
			}
		});
	}
	private void setAnalogInputView(final int nPB5, final int nPB4) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Log.d(TAG, "A: PB5 = " + nPB5 + ", PB4 = " + nPB4);
				msbInputPB5.setProgress(nPB5);
				msbInputPB4.setProgress(nPB4);
			}
		});
	}
	private void setAnalogInputView(final int nPB5, final int nPB4, final int nPB3) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Log.d(TAG, "A: PB5 = " + nPB5 + ", PB4 = " + nPB4 + ", PB3 = " + nPB3);
				msbInputPB5.setProgress(nPB5);
				msbInputPB4.setProgress(nPB4);
				msbInputPB3.setProgress(nPB3);
			}
		});
	}

	private void getViews() {
		mcbDirPB5 = (CheckBox) findViewById(R.id.checkBoxDirPB5);
		mcbDirPB4 = (CheckBox) findViewById(R.id.checkBoxDirPB4);
		mcbDirPB3 = (CheckBox) findViewById(R.id.checkBoxDirPB3);
		mcbDirPB0 = (CheckBox) findViewById(R.id.checkBoxDirPB0);
		
		mtbOutputPB5 = (ToggleButton) findViewById(R.id.toggleButtonOutputPB5);
		mtbOutputPB4 = (ToggleButton) findViewById(R.id.toggleButtonOutputPB4);
		mtbOutputPB3 = (ToggleButton) findViewById(R.id.toggleButtonOutputPB3);
		mtbOutputPB0 = (ToggleButton) findViewById(R.id.toggleButtonOutputPB0);
		
		mcbInputPB5 = (CheckBox) findViewById(R.id.checkBoxInputPB5);
		mcbInputPB4 = (CheckBox) findViewById(R.id.checkBoxInputPB4);
		mcbInputPB3 = (CheckBox) findViewById(R.id.checkBoxInputPB3);
		mcbInputPB0 = (CheckBox) findViewById(R.id.checkBoxInputPB0);

		msbInputPB5 = (SeekBar) findViewById(R.id.seekBarInputPB5);
		msbInputPB4 = (SeekBar) findViewById(R.id.seekBarInputPB4);
		msbInputPB3 = (SeekBar) findViewById(R.id.seekBarInputPB3);
	}
	
	private void setViewHandler() {
		
		// initialize button
		Button btnInitialize = (Button) findViewById(R.id.buttonInitialize);
		btnInitialize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int ids[] = {
					R.id.radioButtonD4A0,
					R.id.radioButtonD3A1,
					R.id.radioButtonD2A2,
					R.id.radioButtonD1A3,
				};
				
				int i;
				for (i = 0; i < ids.length; ++i) {
					RadioButton rb = (RadioButton) findViewById(ids[i]);
					if (rb.isChecked()) {
						break;
					}
				}
				
				byte mode = (byte) (SaltUsbIoMini.UIMC_SET_READ_MODE_D4A0 + i);
	
				// initialize USB-IO-Mini
				initSaltUsbIoMini(mode);
				
				// initialize view
				initViews(mode);
				
				// start thread to show input from USB-IO-Mini
				abortThread();
				mThread = new Thread(new Runnable() {
					@Override
					public void run() {
						while (!mbAbort) {
							// get data
							byte [] data = new byte [8];
							mSaltUsbIoMini.getData(data);
							// Log.d(TAG, String.format("data = %02X %02X %02X %02X %02X %02X %02X %02X", data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]));
							
							// show data
							setDigitalInputView(data[1]);
							switch (data[0]) {
							case SaltUsbIoMini.UIMC_SET_READ_MODE_D3A1:
								setAnalogInputView((data[3] << 8) + (((int) data[2]) & 0xff));	// if data[2] is -1(0xff), ((int) data[2]) returns -1(0xffff), so mask 0x00ff to use only low byte
								break;
							case SaltUsbIoMini.UIMC_SET_READ_MODE_D2A2:
								setAnalogInputView((data[3] << 8) + (((int) data[2]) & 0xff), (data[5] << 8) + (((int) data[4]) & 0xff));
								break;
							case SaltUsbIoMini.UIMC_SET_READ_MODE_D1A3:
								setAnalogInputView((data[3] << 8) + (((int) data[2]) & 0xff), (data[5] << 8) + (((int) data[4]) & 0xff), (data[7] << 8) + (((int) data[6]) & 0xff));
								break;
							}
							
							// wait
							try {
								Thread.sleep(10);	// TODO:
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				mbAbort = false;
				mThread.start();
			}
		});

		// direction button
		OnCheckedChangeListener occlDirection = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int dir = ((mcbDirPB5.isChecked() ? 1 : 0) << 3)
						| ((mcbDirPB4.isChecked() ? 1 : 0) << 2)
						| ((mcbDirPB3.isChecked() ? 1 : 0) << 1)
						| ((mcbDirPB0.isChecked() ? 1 : 0) << 0);
				mSaltUsbIoMini.setDirection((byte) dir);
			}
		};
		mcbDirPB5.setOnCheckedChangeListener(occlDirection);
		mcbDirPB4.setOnCheckedChangeListener(occlDirection);
		mcbDirPB3.setOnCheckedChangeListener(occlDirection);
		mcbDirPB0.setOnCheckedChangeListener(occlDirection);
		
		// output button
		OnCheckedChangeListener occlOutput = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				byte bit = 0;
				switch (buttonView.getId()) {
				case R.id.toggleButtonOutputPB5: bit = 3; break;
				case R.id.toggleButtonOutputPB4: bit = 2; break;
				case R.id.toggleButtonOutputPB3: bit = 1; break;
				case R.id.toggleButtonOutputPB0: bit = 0; break;
				}
				mSaltUsbIoMini.setData((byte) ((isChecked ? 1 : 0) << bit), (byte) (0x01 << bit));
			}
		};
		mtbOutputPB5.setOnCheckedChangeListener(occlOutput);
		mtbOutputPB4.setOnCheckedChangeListener(occlOutput);
		mtbOutputPB3.setOnCheckedChangeListener(occlOutput);
		mtbOutputPB0.setOnCheckedChangeListener(occlOutput);

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_salt_usb_io_mini);
		
		// initialize member variables
		getViews();
		
		// check radioButtonD4A0 to choose digital 4bit mode as default
		RadioButton rb = (RadioButton) findViewById(R.id.radioButtonD4A0);
		rb.setChecked(true);
		
		// set handler
		setViewHandler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_salt_usb_io_mini, menu);
		return true;
	}
	
	private void abortThread() {
		if (mThread != null) {
			mbAbort = true;
			//mThread.stop();
			while (mThread.isAlive())		// TODO: need limit to avoid infinite loop
				;
			mThread = null;
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		
		abortThread();
		uninitSaltUsbIoMini();
	}
}
