package com.xzit.sc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothService {
	
	// Debugging
	private static final String TAG = "BluetoothService";
	private static final boolean D = true;
	
	// �˳����UUID
	private static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	// ��Ա����
	private final BluetoothAdapter bluetoothAdapter;
	private final Handler bluetoothHandler;
	private int bluetoothState;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
    private AcceptThread acceptThread;
	
    // ������ǰ����״̬�ĳ���(Constants that indicate the current connection state)
	// we're doing nothing
    public static final int STATE_NONE = 0;       
    // ������������� now listening for incoming connections
    public static final int STATE_LISTEN = 1;     
    // ����һ���������� now initiating an outgoing connection
    public static final int STATE_CONNECTING = 2; 
    // ���ӵ�һ��Զ���豸 now connected to a remote device
    public static final int STATE_CONNECTED = 3;  
    // �Ͽ�����
    public static final int STATE_CLOSE = 4; 
	
    /**
     * @Description: TODO ���캯��
     * @param @param context 
     * @param @param handler
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
	public BluetoothService(Context context, Handler handler) {
		// ��ȡ����������
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// �趨��ʼ״̬
		bluetoothState = STATE_NONE;
		// ��ȡ����������
		bluetoothHandler = handler;
	}
	
	
	/**
	 * @Title: getBluetoothState
	 * @Description: TODO ��ȡ�����Ự���ӵ�״̬
	 * @param @return   
	 * @return int 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
    public synchronized int getBluetoothState() {
		return bluetoothState;
	}

    /**
     * @Title: setBluetoothState
     * @Description: TODO ���������Ự���ӵ�״̬(set state of the chat connect)
     * @param @param bluetoothState ��������״̬��һ������
     * @return void 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
	public synchronized void setBluetoothState(int bluetoothState) {
		this.bluetoothState = bluetoothState;
	}
    
	
	
    
    /**
     * @Title: start
     * @Description: TODO Start the bluetooth control service. 
     * 						Specifically start AcceptThread to begin a session in listening (server) mode. 
     * 						Called by the Activity onResume()
     * @param    
     * @return void 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
    public synchronized void start() {
    	// Debugging
    	if (D) Log.e(TAG, "start");
    	
    	// ȡ���κ���ͼ�����Ự���߳�(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// ȡ���߳�
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// ȡ����ǰ�Ѿ����ӻỰ���߳�(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// ȡ���߳�
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// ���������豸״̬
    	setBluetoothState(STATE_LISTEN);
    	
    	// ����һ��������������˿ںŵ��߳�(Start the thread to listen a BluetoothServerSocket)
    	if(acceptThread == null){
    		// �����߳�
    		acceptThread = new AcceptThread(true);
    		// ��ʼ����
    		acceptThread.start();
    	}
    	
    }
	
    /**
     * @Title: connect
     * @Description: TODO �������Ӹ����豸���߳�(Start the thread to connect the given device)
     * @param @param device �������豸
     * @param @param secure   
     * @return void 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		// Debugging
		if(D) Log.e(TAG, "+++ connect " + device);
		
		if(getBluetoothState() == STATE_CONNECTING){
			// ȡ���κ���ͼ�����Ự���߳�(Cancel any thread attempting to make a connection)
	    	if (connectThread != null) {
	    		// ȡ���߳�
				connectThread.cancel();
				connectThread = null;
			}
		}
    	
    	// ȡ����ǰ�Ѿ����ӻỰ���߳�(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// ȡ���߳�
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// �������Ӹ����豸���߳�(Start the thread to connect the given device)
    	connectThread = new ConnectThread(device, secure);
    	connectThread.start();
    	setBluetoothState(STATE_CONNECTING);
    	
	}
	
	/**
	 * @Title: connected
	 * @Description: TODO �����������������ӵ��߳�(Start the thread to manage a bluetooth connect)
	 * @param @param socket �������ӵĶ˿ں�(The bluetooth socket on which the connect was made)
	 * @param @param device �����ӵ��豸(The bluetooth device that has been connected)
	 * @param @param socketType   
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
		// Debugging
    	if (D) Log.e(TAG, " + + + connected + + +");
    	
    	// ȡ���κ���ͼ�����Ự���߳�(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// ȡ���߳�
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// ȡ����ǰ�Ѿ����ӻỰ���߳�(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// ȡ���߳�
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// ȡ��accept�̣߳���ΪֻҪ����һ���豸(Cancel the accept thread because we only want to connect to one device)
    	if(acceptThread == null){
    		acceptThread.cancel();
    		acceptThread = null;
    	}
    	
    	// ���������������ӡ�ִ�����ݴ�����߳�(Start the thread to manage the connect and perform transmissions)
    	connectedThread = new ConnectedThread(socket, socketType);
    	connectedThread.start();
    	
    	// �������淢���������豸������
    	Message message = bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_DEVICE_NAME);
    	Bundle bundle = new Bundle();
    	bundle.putString(SmartCarActivity.DEVICE_NAME, device.getName());
    	message.setData(bundle);
    	bluetoothHandler.sendMessage(message);
    	
    	// ������������״̬
    	setBluetoothState(STATE_CONNECTED);
	}
	
	/**
	 * @Title: close
	 * @Description: TODO �ر���������
	 * @param    
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public synchronized void close(){
		// Debugging
		if(D) Log.e(TAG, " + + + close + + +");
		
		// ȡ���κ���ͼ�����Ự���߳�(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// ȡ���߳�
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// ȡ����ǰ�Ѿ����ӻỰ���߳�(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// ȡ���߳�
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// ȡ��accept�߳�(Cancel the accept thread)
    	if(acceptThread == null){
    		acceptThread.cancel();
    		acceptThread = null;
    	}
    	
    	// ��������״̬
    	setBluetoothState(STATE_CLOSE);
	}
	
	/**
	 * @Title: stop
	 * @Description: TODO ��ֹ��������
	 * @param    
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public synchronized void stop() {
		// Debugging
		if(D) Log.e(TAG, " + + + close + + +");
		
		// ȡ���κ���ͼ�����Ự���߳�(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// ȡ���߳�
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// ȡ����ǰ�Ѿ����ӻỰ���߳�(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// ȡ���߳�
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// ȡ��accept�߳�(Cancel the accept thread)
    	if(acceptThread == null){
    		acceptThread.cancel();
    		acceptThread = null;
    	}
    	
    	// ��������״̬
    	setBluetoothState(STATE_NONE);
	}
	
	/**
	 * @Title: write
	 * @Description: TODO �� �������̵߳������ д������(Write to connected outstream)
	 * @param @param out ��д����ַ���   
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public void write(byte[] out) {
		// ������ʱ�߳� (create temporary thread)
		ConnectedThread tempThread = null;
		// ��ʱͬ���� �������豸�߳� (synchronize a copy of connectedThread)
		synchronized (this){
			if(bluetoothState != STATE_CONNECTED)
				return;
			// ͬ��
			tempThread = connectedThread;
		}
		// ���߳�д������(Perform the write unsynchronized)
		tempThread.write(out);
	}
	
	/**
	 * @Title: connectionFailed
	 * @Description: TODO ֪ͨ��������������ʧ��(Notify the UI Activity that the attempt to connect failed)
	 * @param    
	 * @return void 
	 * @throws
	 * @author: �Ľ� 1272570701@qq.com
	 * @date: 2017-4-27
	 */
    private void connectionFailed() {
    	// �������淢������ʧ����ʾ(Send a failure message to the Activity)
    	Message message = bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_TOAST);
    	Bundle bundle = new Bundle();
    	bundle.putString(SmartCarActivity.TOAST, "���������豸");
    	message.setData(bundle);
    	bluetoothHandler.sendMessage(message);
    	
    	// �����������������¼���(Start the Service to restart the listening mode)
    	BluetoothService.this.start();
    }
    
    /**
     * @Title: connectionLost
     * @Description: TODO
     * @param    
     * @return void 
     * @throws
     * @author: �Ľ� 1272570701@qq.com
     * @date: 2017-4-27
     */
    private void connectionLost() {
    	// �������淢���豸���Ӷ�ʧ��ʾ(Send a failure message to the Activity)
    	Message message = bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_TOAST);
    	Bundle bundle = new Bundle();
    	bundle.putString(SmartCarActivity.TOAST, "�豸���Ӷ�ʧ");
    	message.setData(bundle);
    	bluetoothHandler.sendMessage(message);
    	
    	// �����������������¼���(Start the Service to restart the listening mode)
    	BluetoothService.this.start();
    }
	
	
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
    	
    	private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;
        private String socketType;
        
        /**
         * @Description: TODO ���캯��
         * @param @param device Ҫ���ӵ��豸
         * @param @param secure
         * @throws
         * @author: �Ľ� 1272570701@qq.com
         * @date: 2017-4-27
         */
    	public ConnectThread(BluetoothDevice device, boolean secure) {
    		bluetoothDevice = device;
    		socketType = secure ? "Secure" : "Insecure";
    		BluetoothSocket tempSocket = null;
    		try {
    			if (secure) {
    				tempSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
				}
			} catch (IOException e) {
				Log.e(TAG, " + + + Socket Type:" + socketType + " create failed + + +");
			}
			bluetoothSocket = tempSocket;
    	}
    	
    	/**
    	 * @Title: run
    	 * @Description: TODO ִ����������
    	 * @param  
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void run() {
    		Log.i(TAG, " + + + Begin ConnectThread, socketType:" + socketType);
    		setName("ConnectThread " + socketType);
    		
    		// ȡ������������������ǰ�����̵߳�Ч��(Always cancel discovery because it will slow down a connection)
    		bluetoothAdapter.cancelDiscovery();
    		
    		try {
    			// �������˿ڽ�����������(This is a blocking call and will only return on a successful connection or an exception)
				bluetoothSocket.connect();
			} catch (IOException e) {
				Log.e(TAG, " + + + Connection fail ", e);
				try {
					// �ر������˿�
					bluetoothSocket.close();
				} catch (IOException e1) {
					Log.e(TAG, " + + + " , e1);
				}
				connectionFailed();
				return;
			}
			
			// ������ɣ����������߳�
			synchronized (BluetoothService.this) {
				connectThread = null;
			}
			// �����������߳�
			connected(bluetoothSocket, bluetoothDevice, socketType);
    		
    	}
    	
    	/**
    	 * @Title: cancel
    	 * @Description: TODO �ر���������
    	 * @param    
    	 * @return void 
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void cancel() {
    		try {
				// �ر������˿�
				bluetoothSocket.close();
			} catch (IOException e1) {
				Log.e(TAG, " + + + " , e1);
			}
    	}
        
    }
    
    private class ConnectedThread extends Thread {
    	
    	private final BluetoothSocket bluetoothSocket;
    	private final OutputStream outputStream;
    	private final InputStream inputStream;
    	
    	/**
    	 * @Description: TODO ���캯��
    	 * @param @param socket
    	 * @param @param socketType
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public ConnectedThread(BluetoothSocket socket, String socketType) {
    		// Debugging
    		Log.d(TAG, "create ConnectedThread: " + socketType);
    		// ��ʼ����Ա����
    		bluetoothSocket = socket;
    		OutputStream tempOutputStream = null;
    		InputStream tempInputStream = null;
    		
    		// ��ȡ�����˿����������
    		try {
				tempOutputStream = bluetoothSocket.getOutputStream();
				tempInputStream = bluetoothSocket.getInputStream();
			} catch (IOException e) {
				Log.e(TAG, " + + + temp socket streams not created + + + ");
			}
			outputStream = tempOutputStream;
			inputStream = tempInputStream;
    		
    	}
    	
    	/**
    	 * @Title: run
    	 * @Description: TODO
    	 * @param  
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void run() {
    		// Debugging
    		Log.e(TAG, " + + + begin ConnectedThread + + + ");
    		byte[] buffer = new byte[1024];
    		int bytes;
    		
    		// �豸������֮�󣬱��ֶ��������ļ���(Keep listening to the inputstream)
    		while(true) {
    			try {
    				// ��������������(Read from inputStream)
					bytes = inputStream.read(buffer);
					// �ѽ��յ������ݷ��͵�������
					bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, " + + + Disconnected ", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothService.this.start();
                    break;
				}
    		}
    	}
    	
    	/**
    	 * @Title: write
    	 * @Description: TODO �������д������
    	 * @param @param buffer   
    	 * @return void 
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void write(byte[] buffer) {
    		try {
				outputStream.write(buffer);
				// �����͵�������ʾ��������(Share the sent message back to the UI Activity)
				// bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
    	}
    	
    	/**
    	 * @Title: cancel
    	 * @Description: TODO �ر���������
    	 * @param    
    	 * @return void 
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void cancel() {
    		try {
				// �ر������˿�
				bluetoothSocket.close();
			} catch (IOException e1) {
				Log.e(TAG, " + + + " , e1);
			}
    	}
    }
    
    private class AcceptThread extends Thread {
    	
    	private final BluetoothServerSocket bluetoothServerSocket;
    	private String socketType;
    	
    	/**
    	 * @Description: TODO ���캯��
    	 * @param @param secure
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public AcceptThread(boolean secure){
    		socketType = secure ? "Secure" : "Insecure";
    		BluetoothServerSocket tempServerSocket = null;
    		
    		try {
    			// �����µļ�������˿�(Create a new listening server socket)
    			if(secure){
    				tempServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(socketType, BLUETOOTH_UUID);
    			}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + socketType + "listen() failed", e);
			}
			bluetoothServerSocket = tempServerSocket;
    	}
    	
    	/**
    	 * @Title: run
    	 * @Description: TODO ����������������
    	 * @param  
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void run() {
    		// Debugging
    		if(D) Log.d(TAG, "Socket Type: " + socketType + "BEGIN mAcceptThread" + this);
    		setName("AcceptThread" + socketType);
    		
    		BluetoothSocket socket = null;
    		// δ����ʱ�������񴰿�(Listen to the server socket if we're not connected)
    		while(bluetoothState != STATE_CONNECTED) {
    			// ������������
    			try {
    				// This is a blocking call and will only return on a successful connection or an exception
					socket = bluetoothServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "Socket Type: " + socketType + "accept() failed", e);
                    break;
				}
				
				// �������󱻽���֮��(If a connection was accepted)
				if (socket != null) {
					synchronized (BluetoothService.this) {
						
						//
						switch (bluetoothState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// ״̬���������������߳�(Situation normal. Start the connected thread.)
							connected(socket, socket.getRemoteDevice(), socketType);
							break;
									
						case STATE_NONE:
                        case STATE_CLOSE:
                        case STATE_CONNECTED:
                        	try {
                        		// �Ȳ���׼��״̬Ҳ����������״̬����ֹ�˿�(Either not ready or already connected. Terminate new socket.)
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
						
					}
				}
    		}
    		if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + socketType);
    	}
    	
    	/**
    	 * @Title: cancel
    	 * @Description: TODO ȡ������
    	 * @param    
    	 * @return void 
    	 * @throws
    	 * @author: �Ľ� 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void cancel() {
    		// Debugging
    		if (D) Log.d(TAG, "Socket Type" + socketType + "cancel " + this);
    		
    		// 
    		try {
				bluetoothServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Socket Type" + socketType + "close() of server failed", e);
			}
    		
    	}
    	
    }

}
