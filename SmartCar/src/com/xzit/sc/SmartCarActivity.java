package com.xzit.sc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SmartCarActivity extends Activity implements OnClickListener {
	
	// Debugging
	private static final String TAG = "SmartCarActivity";
	private static final String TAG_LINE = " + + + + ";
	private static final boolean D = true;
	
	// ������������������Ϣ������ (Message types sent from BTService Handler)
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String mscan = null;
    
    // ��ͼ�������(Intent request codes)
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int CLOSE_CONNECT  = 3;
    
    // ��ť����
    private TextView deviceScanText;
    private TextView deviceSwitchText;
    
    // ����������
    private SensorManager sensorManager;
    private Sensor sensor;
	private SensorEventListener sensorEventListener;
	
	// ����������
	private BluetoothAdapter bluetoothAdapter;
	// �����������
	private BluetoothService bluetoothService;
	// �����������豸������
	private String deviceConnectedName;
	
	// ��ťң������Э��
	String FORWARD = "1";
	String BACK = "2";
	String STOP = "0";
	String LEFT = "3";
	String RIGHT = "4";
	
	// ������Ӧң������Э��
	TextView xViewA = null;
	TextView yViewA = null;
	TextView zViewA = null;	
	private float X = 0;
	private float Y = 0;
	private float Z = 0;
	
	
	
    /**
     * @Title: onCreate
     * @Description: TODO �������ʱ��ʼ��
     * @param @param savedInstanceState 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-26
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// ������־���
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, TAG_LINE + "ON CREATE" + TAG_LINE);
        
        // ���ò���
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
		// ��ť����
		findViewById(R.id.dir1).setVisibility(View.INVISIBLE);
		findViewById(R.id.dir3).setVisibility(View.INVISIBLE);
		findViewById(R.id.dir7).setVisibility(View.INVISIBLE);
		findViewById(R.id.dir9).setVisibility(View.INVISIBLE);
        
        // ������ʾ
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        
        // ��ȡ��ť�������
        // ����ɨ�谴ť
        deviceScanText = (TextView) findViewById(R.id.device_scan);
        // �������ذ�ť(δ������ʱ��ʾ�򿪣�����������ʾ�ر�)
        deviceSwitchText = (TextView) findViewById(R.id.device_switch);
        
        // Ϊ��ť���� ���������
        // ɨ�谴ť
        View deviceScanButton = this.findViewById(R.id.device_scan);
        deviceScanButton.setOnClickListener(this);
        // �Ͽ����Ӱ�ť
        View deviceSwitchButton = this.findViewById(R.id.device_switch);
        deviceSwitchButton.setOnClickListener(this);
        // ����������Ӧ��ť
        View gravityOpenButton = this.findViewById(R.id.gravity_open);
        gravityOpenButton.setOnClickListener(this);
        // �ر�������Ӧ��ť
        View gravityCloseButton = this.findViewById(R.id.gravity_close);
        gravityCloseButton.setOnClickListener(this);
        // �˳����
        View exitButton = this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
        
        
        // ��ȡ�������������(SensorManager)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // ��ȡ���ٶȴ���������
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        
        // ��ȡ��������������
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // �豸��֧������(Device do not support bluetooth)
        if(bluetoothAdapter == null){
        	Toast.makeText(this, "�豸��֧������", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }
        //��������
        deviceSwitchText.setOnClickListener(new OnClickListener() {
        	// ����¼�
			public void onClick(View v) {
				// �����豸���ڴ�״̬ʱ
				if(bluetoothAdapter.isEnabled()){
					// �ر������豸
					bluetoothAdapter.disable();
					// �޸İ�ť����
					deviceSwitchText.setText("��");
				} else {
					// �������豸
					bluetoothAdapter.enable();
					// �޸İ�ť����
					deviceSwitchText.setText("�ر�");
				}
			}
		});
        
    }

	
	@Override
	protected void onStart() {
		super.onStart();
		
		// �����ʼ������־���
		if(D) Log.e(TAG, TAG_LINE + "on start" + TAG_LINE);
		
		// ������������
		if(!bluetoothAdapter.isEnabled()){
			// ������������������ ��ͼ
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			deviceSwitchText.setText("�ر�");
			// ����С��
			if (bluetoothService == null) {
				directionControl();
			}
		}
	}
	
    
    /**
     * @Title: onClick
     * @Description: TODO �����水ť����¼�
     * @param @param v 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
	@Override
	public void onClick(View v) {
		
		// �жϰ�ť����¼�
		switch (v.getId()) {
		
		// ɨ���豸
		case R.id.device_scan:
			if (bluetoothService != null && bluetoothService.getState() != BluetoothService.STATE_CONNECTED){
    			Intent serverIntent = new Intent(this, Scan.class);
    			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    		} else {    			
    			deviceScanText.setText("����");
    			bluetoothService.close();
    		}
			break;
			
		// ����������Ӧ
		case R.id.gravity_open:
			Toast.makeText(this, "����������Ӧ", Toast.LENGTH_SHORT).show();
			Gravity();
			sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
			break;
		
		// �ر�������Ӧ
		case R.id.gravity_close:
			Toast.makeText(this, "�ر�������Ӧ", Toast.LENGTH_SHORT).show();
			sensorManager.unregisterListener(sensorEventListener);
			break;
			
		// �˳����
		case R.id.exit_button:
			bluetoothService.close();
			finish();
			break;
		}
		
	}
	
	/**
	 * @Title: sendCommand
	 * @Description: TODO ͨ��������������
	 * @param @param command �����͵�����  
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	private void sendCommand(String command) {
		// ������������״̬�������� 
		if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
			return ;
		}
		// У������
		if(command != null && command.length() > 0){
			// ��ȡ�����ַ���
			byte[] commandBytes = command.getBytes();
			// ��������
			bluetoothService.write(commandBytes);
		}
	}
	
	/**
	 * @Title: addCommandToButton
	 * @Description: TODO ����ť��Ӵ����¼�
	 * @param @param buttonId ��ťID
	 * @param @param command ���������  
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	private void addCommandToButton(int buttonId, final String command) {
		// ͨ��Id��ȡ��ť
		Button button = (Button)findViewById(buttonId);
		// ����ť��Ӵ����¼�
		button.setOnTouchListener(new Button.OnTouchListener() 
        {
         	@Override
             public boolean onTouch(View v, MotionEvent event) {
                 int action = event.getAction();
                 switch(action)
                 {
                 case MotionEvent.ACTION_DOWN:
                	 sendCommand(command);
                     break;
                 case MotionEvent.ACTION_UP:
                	 sendCommand(STOP);
                     break;
                 }
                 return false;
             }
         });
	}
	
	/**
	 * @Title: directionControl
	 * @Description: TODO ���ܳ��������
	 * @param    
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public void directionControl() {
		// Debugging
		if(D) Log.e(TAG, " + + + direction control + + + ");
		
		// ͣ����ť��ӵ���¼�
		Button dirStopBtn = (Button) findViewById(R.id.dir5);
		dirStopBtn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				sendCommand(STOP);
			}
		});
		
		// ������ť����¼�
		addCommandToButton(R.id.dir2, FORWARD);
		addCommandToButton(R.id.dir4, LEFT);
		addCommandToButton(R.id.dir6, RIGHT); 
		addCommandToButton(R.id.dir8, BACK);
		
		// ��ʼ�����������࣬������������(Initialize the BTService to perform bluetooth connections)
		bluetoothService = new BluetoothService(this, bluetoothHandler);
	}
	
	/**
	 * @Title: Gravity
	 * @Description: TODO ��ȡ������������ֵ�������ݴ�������ֵ���Ƴ��ķ���
	 * @param    
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-28
	 */
	private void Gravity(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		sensorEventListener = new SensorEventListener() {
			// ��ȡ��������ֵ
			@Override
			public void onSensorChanged(SensorEvent event) {
				// ȡֵ
				X = event.values[SensorManager.DATA_X];
    			Y = event.values[SensorManager.DATA_Y];
    			Z = event.values[SensorManager.DATA_Z];	
    			// ������Ӧ����
    			gravityControl();
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	/**
	 * @Title: gravityControl
	 * @Description: TODO ���������ǵ�ֵ�������ܳ�����
	 * @param    
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-28
	 */
	public void gravityControl(){
		// �ֻ����泯��
		if(Z >= 0) {
			// y�������
			if(Y <= -2.0 && Y > -9.0) {
				sendCommand(LEFT);
			} else if(Y > 2.0 && Y < 9.0) {
				sendCommand(RIGHT);
			} else if(X < -2.0 && X > -9.0) {
				sendCommand(FORWARD);
			} else if(X > 2.0 && X < 9.0) {
				sendCommand(BACK);
			} else {
				sendCommand(STOP);
			}
		}
	}
	
	
	private final Handler bluetoothHandler = new Handler(){
		@Override
    	public void handleMessage (Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
            	if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
            	switch (msg.arg1) {
            	case BluetoothService.STATE_CONNECTED:
            		Toast.makeText(getApplicationContext(), R.string.title_connecting, Toast.LENGTH_SHORT).show();
                	deviceScanText.setText("�Ͽ�");
                    break;
                case BluetoothService.STATE_CONNECTING:
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                	deviceScanText.setText("����");
                    break;
                case BluetoothService.STATE_CLOSE:
                	Toast.makeText(getApplicationContext(), R.string.title_disconnected, Toast.LENGTH_SHORT).show();
                	deviceScanText.setText("����");
                	break;
                }
                break;
                
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                deviceConnectedName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + deviceConnectedName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
                
            }
        }
	};
	
	/**
	 * @Title: onResume
	 * @Description: TODO �ص�������ʱ����������
	 * @param  
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	@Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
	/**
	 * @Title: onPause
	 * @Description: TODO �뿪������ʱȡ������������
	 * @param  
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
    @Override
    public synchronized void onPause() {
    	super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
        sensorManager.unregisterListener(sensorEventListener);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --"); 
    }

    @Override
    public void onDestroy() {
        super.onDestroy();          
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	// Inflate the menu; this adds items to the action bar if it is present.
       	getMenuInflater().inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      //  Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.discoverable:
        	// Ensure this device is discoverable by others
        	ensureDiscoverable();
            return true;
        }
        return false;
        
    }
    
    /**
     * @Title: ensureDiscoverable
     * @Description: TODO ȷ���豸�ܱ�����
     * @param    
     * @return void 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
    private void ensureDiscoverable() 
    {
    	// Debugging
        if(D) Log.d(TAG, "ensure discoverable");
        // 
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) 
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
	
    /**
     * @Title: onActivityResult
     * @Description: TODO
     * @param @param requestCode
     * @param @param resultCode
     * @param @param data 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// Debugging
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        // 
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {          
                connectDevice(data, true);
            }
            break;
        case CLOSE_CONNECT:
            connectDevice(data, false);
            
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            	directionControl();
            } 
            else {
            	// User did not enable Bluetooth or an error occurred
                Log.d(TAG, "Bluetooth not enabled");
                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    /**
     * @Title: connectDevice
     * @Description: TODO �����豸
     * @param @param data
     * @param @param secure   
     * @return void 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
    private void connectDevice(Intent data, boolean secure) {     
    	// Get the device MAC address ��ȡ�豸��MAC��ַ
        String address = data.getExtras().getString(Scan.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object  BluetoothDevice����
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
               
        // Attempt to connect to the device �������ӵ��豸
        bluetoothService.connect(device, secure);
        
    }
	
	
	

}