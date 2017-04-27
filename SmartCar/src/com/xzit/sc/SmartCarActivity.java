package com.xzit.sc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
	
    
    
	@Override
	public void onClick(View v) {
		
		// 判断按钮点击事件
		switch (v.getId()) {
		case R.id.device_scan:
			
			break;
			
			// 开启重力感应
		case R.id.gravity_open:
			Toast.makeText(this, "开启重力感应", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.gravity_close:
			Toast.makeText(this, "关闭重力感应", Toast.LENGTH_SHORT).show();
			
			// 退出软件
		case R.id.exit_button:
			bluetoothService.close();
			finish();
			break;
		}
		
	}
	
	
	
	
	
	public void directionControl() {
		
	}
}