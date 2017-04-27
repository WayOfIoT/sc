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
	
    
    
	@Override
	public void onClick(View v) {
		
		// �жϰ�ť����¼�
		switch (v.getId()) {
		case R.id.device_scan:
			
			break;
			
			// ����������Ӧ
		case R.id.gravity_open:
			Toast.makeText(this, "����������Ӧ", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.gravity_close:
			Toast.makeText(this, "�ر�������Ӧ", Toast.LENGTH_SHORT).show();
			
			// �˳����
		case R.id.exit_button:
			bluetoothService.close();
			finish();
			break;
		}
		
	}
	
	
	
	
	
	public void directionControl() {
		
	}
}