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
	
	// 蓝牙服务处理器发送消息的种类 (Message types sent from BTService Handler)
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String mscan = null;
    
    // 意图请求代码(Intent request codes)
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int CLOSE_CONNECT  = 3;
    
    // 按钮标题
    private TextView deviceScanText;
    private TextView deviceSwitchText;
    
    // 传感器对象
    private SensorManager sensorManager;
    private Sensor sensor;
	private SensorEventListener sensorEventListener;
	
	// 蓝牙适配器
	private BluetoothAdapter bluetoothAdapter;
	// 蓝牙服务对象
	private BluetoothService bluetoothService;
	// 已连接蓝牙设备的名称
	private String deviceConnectedName;
	
	// 按钮遥控命令协议
	String FORWARD = "1";
	String BACK = "2";
	String STOP = "0";
	String LEFT = "3";
	String RIGHT = "4";
	
	// 重力感应遥控命令协议
	TextView xViewA = null;
	TextView yViewA = null;
	TextView zViewA = null;	
	private float X = 0;
	private float Y = 0;
	private float Z = 0;
	
	
	
    /**
     * @Title: onCreate
     * @Description: TODO 软件启动时初始化
     * @param @param savedInstanceState 
     * @throws
     * @author: 夏杰 1272570701@qq.com
     * @date: 2017-4-26
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// 启动日志输出
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, TAG_LINE + "ON CREATE" + TAG_LINE);
        
        // 设置布局
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
		// 按钮布局
		findViewById(R.id.dir1).setVisibility(View.INVISIBLE);
		findViewById(R.id.dir3).setVisibility(View.INVISIBLE);
		findViewById(R.id.dir7).setVisibility(View.INVISIBLE);
		findViewById(R.id.dir9).setVisibility(View.INVISIBLE);
        
        // 横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        
        // 获取按钮标题对象
        // 蓝牙扫描按钮
        deviceScanText = (TextView) findViewById(R.id.device_scan);
        // 蓝牙开关按钮(未打开蓝牙时显示打开，打开蓝牙后显示关闭)
        deviceSwitchText = (TextView) findViewById(R.id.device_switch);
        
        // 为按钮设置 点击监听器
        // 扫描按钮
        View deviceScanButton = this.findViewById(R.id.device_scan);
        deviceScanButton.setOnClickListener(this);
        // 断开连接按钮
        View deviceSwitchButton = this.findViewById(R.id.device_switch);
        deviceSwitchButton.setOnClickListener(this);
        // 开启重力感应按钮
        View gravityOpenButton = this.findViewById(R.id.gravity_open);
        gravityOpenButton.setOnClickListener(this);
        // 关闭重力感应按钮
        View gravityCloseButton = this.findViewById(R.id.gravity_close);
        gravityCloseButton.setOnClickListener(this);
        // 退出软件
        View exitButton = this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
        
        
        // 获取传感器管理对象(SensorManager)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 获取加速度传感器对象
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        
        // 获取蓝牙适配器对象
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 设备不支持蓝牙(Device do not support bluetooth)
        if(bluetoothAdapter == null){
        	Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }
        //蓝牙开关
        deviceSwitchText.setOnClickListener(new OnClickListener() {
        	// 点击事件
			public void onClick(View v) {
				// 蓝牙设备处于打开状态时
				if(bluetoothAdapter.isEnabled()){
					// 关闭蓝牙设备
					bluetoothAdapter.disable();
					// 修改按钮标题
					deviceSwitchText.setText("打开");
				} else {
					// 打开蓝牙设备
					bluetoothAdapter.enable();
					// 修改按钮标题
					deviceSwitchText.setText("关闭");
				}
			}
		});
        
    }

	
	@Override
	protected void onStart() {
		super.onStart();
		
		// 软件开始运行日志输出
		if(D) Log.e(TAG, TAG_LINE + "on start" + TAG_LINE);
		
		// 请求启用蓝牙
		if(!bluetoothAdapter.isEnabled()){
			// 蓝牙适配器开启蓝牙 意图
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			deviceSwitchText.setText("关闭");
			// 控制小车
			if (bluetoothService == null) {
				directionControl();
			}
		}
	}
	
    
    /**
     * @Title: onClick
     * @Description: TODO 主界面按钮点击事件
     * @param @param v 
     * @throws
     * @author: 夏杰 1272570701@qq.com
     * @date: 2017-4-27
     */
	@Override
	public void onClick(View v) {
		
		// 判断按钮点击事件
		switch (v.getId()) {
		
		// 扫描设备
		case R.id.device_scan:
			if (bluetoothService != null && bluetoothService.getState() != BluetoothService.STATE_CONNECTED){
    			Intent serverIntent = new Intent(this, Scan.class);
    			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    		} else {    			
    			deviceScanText.setText("连接");
    			bluetoothService.close();
    		}
			break;
			
		// 开启重力感应
		case R.id.gravity_open:
			Toast.makeText(this, "开启重力感应", Toast.LENGTH_SHORT).show();
			Gravity();
			sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
			break;
		
		// 关闭重力感应
		case R.id.gravity_close:
			Toast.makeText(this, "关闭重力感应", Toast.LENGTH_SHORT).show();
			sensorManager.unregisterListener(sensorEventListener);
			break;
			
		// 退出软件
		case R.id.exit_button:
			bluetoothService.close();
			finish();
			break;
		}
		
	}
	
	/**
	 * @Title: sendCommand
	 * @Description: TODO 通过蓝牙发送命令
	 * @param @param command 待发送的命令  
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	private void sendCommand(String command) {
		// 蓝牙处于连接状态则发送数据 
		if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
			return ;
		}
		// 校验命令
		if(command != null && command.length() > 0){
			// 获取命令字符流
			byte[] commandBytes = command.getBytes();
			// 发送数据
			bluetoothService.write(commandBytes);
		}
	}
	
	/**
	 * @Title: addCommandToButton
	 * @Description: TODO 给按钮添加触摸事件
	 * @param @param buttonId 按钮ID
	 * @param @param command 待添加命令  
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	private void addCommandToButton(int buttonId, final String command) {
		// 通过Id获取按钮
		Button button = (Button)findViewById(buttonId);
		// 给按钮添加触摸事件
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
	 * @Description: TODO 智能车方向控制
	 * @param    
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public void directionControl() {
		// Debugging
		if(D) Log.e(TAG, " + + + direction control + + + ");
		
		// 停车按钮添加点击事件
		Button dirStopBtn = (Button) findViewById(R.id.dir5);
		dirStopBtn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				sendCommand(STOP);
			}
		});
		
		// 给方向按钮添加事件
		addCommandToButton(R.id.dir2, FORWARD);
		addCommandToButton(R.id.dir4, LEFT);
		addCommandToButton(R.id.dir6, RIGHT); 
		addCommandToButton(R.id.dir8, BACK);
		
		// 初始化蓝牙服务类，进行蓝牙连接(Initialize the BTService to perform bluetooth connections)
		bluetoothService = new BluetoothService(this, bluetoothHandler);
	}
	
	/**
	 * @Title: Gravity
	 * @Description: TODO 读取重力传感器的值，并根据传感器的值控制车的方向
	 * @param    
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-28
	 */
	private void Gravity(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		sensorEventListener = new SensorEventListener() {
			// 读取传感器的值
			@Override
			public void onSensorChanged(SensorEvent event) {
				// 取值
				X = event.values[SensorManager.DATA_X];
    			Y = event.values[SensorManager.DATA_Y];
    			Z = event.values[SensorManager.DATA_Z];	
    			// 重力感应控制
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
	 * @Description: TODO 根据陀螺仪的值控制智能车方向
	 * @param    
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-28
	 */
	public void gravityControl(){
		// 手机正面朝上
		if(Z >= 0) {
			// y方向倾角
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
                	deviceScanText.setText("断开");
                    break;
                case BluetoothService.STATE_CONNECTING:
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                	deviceScanText.setText("连接");
                    break;
                case BluetoothService.STATE_CLOSE:
                	Toast.makeText(getApplicationContext(), R.string.title_disconnected, Toast.LENGTH_SHORT).show();
                	deviceScanText.setText("连接");
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
	 * @Description: TODO 回到主界面时监听传感器
	 * @param  
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	@Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
	/**
	 * @Title: onPause
	 * @Description: TODO 离开主界面时取消监听传感器
	 * @param  
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
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
     * @Description: TODO 确保设备能被搜索
     * @param    
     * @return void 
     * @throws
     * @author: 夏杰 1272570701@qq.com
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
     * @author: 夏杰 1272570701@qq.com
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
     * @Description: TODO 连接设备
     * @param @param data
     * @param @param secure   
     * @return void 
     * @throws
     * @author: 夏杰 1272570701@qq.com
     * @date: 2017-4-27
     */
    private void connectDevice(Intent data, boolean secure) {     
    	// Get the device MAC address 获取设备的MAC地址
        String address = data.getExtras().getString(Scan.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object  BluetoothDevice对象
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
               
        // Attempt to connect to the device 尝试连接到设备
        bluetoothService.connect(device, secure);
        
    }
	
	
	

}