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
	
	// 此程序的UUID
	private static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	// 成员变量
	private final BluetoothAdapter bluetoothAdapter;
	private final Handler bluetoothHandler;
	private int bluetoothState;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
    private AcceptThread acceptThread;
	
    // 表明当前连接状态的常量(Constants that indicate the current connection state)
	// we're doing nothing
    public static final int STATE_NONE = 0;       
    // 监听传入的连接 now listening for incoming connections
    public static final int STATE_LISTEN = 1;     
    // 启动一个外向连接 now initiating an outgoing connection
    public static final int STATE_CONNECTING = 2; 
    // 连接到一个远程设备 now connected to a remote device
    public static final int STATE_CONNECTED = 3;  
    // 断开连接
    public static final int STATE_CLOSE = 4; 
	
    /**
     * @Description: TODO 构造函数
     * @param @param context 
     * @param @param handler
     * @throws
     * @author: 夏杰 1272570701@qq.com
     * @date: 2017-4-27
     */
	public BluetoothService(Context context, Handler handler) {
		// 获取蓝牙适配器
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// 设定初始状态
		bluetoothState = STATE_NONE;
		// 获取蓝牙处理器
		bluetoothHandler = handler;
	}
	
	
	/**
	 * @Title: getBluetoothState
	 * @Description: TODO 获取蓝牙会话连接的状态
	 * @param @return   
	 * @return int 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
    public synchronized int getBluetoothState() {
		return bluetoothState;
	}

    /**
     * @Title: setBluetoothState
     * @Description: TODO 设置蓝牙会话连接的状态(set state of the chat connect)
     * @param @param bluetoothState 定义连接状态的一个整数
     * @return void 
     * @throws
     * @author: 夏杰 1272570701@qq.com
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
     * @author: 夏杰 1272570701@qq.com
     * @date: 2017-4-27
     */
    public synchronized void start() {
    	// Debugging
    	if (D) Log.e(TAG, "start");
    	
    	// 取消任何试图建立会话的线程(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// 取消线程
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// 取消当前已经连接会话的线程(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// 取消线程
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// 设置蓝牙设备状态
    	setBluetoothState(STATE_LISTEN);
    	
    	// 开启一个监听蓝牙服务端口号的线程(Start the thread to listen a BluetoothServerSocket)
    	if(acceptThread == null){
    		// 开启线程
    		acceptThread = new AcceptThread(true);
    		// 开始运行
    		acceptThread.start();
    	}
    	
    }
	
    /**
     * @Title: connect
     * @Description: TODO 开启连接给定设备的线程(Start the thread to connect the given device)
     * @param @param device 给定的设备
     * @param @param secure   
     * @return void 
     * @throws
     * @author: 夏杰 1272570701@qq.com
     * @date: 2017-4-27
     */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		// Debugging
		if(D) Log.e(TAG, "+++ connect " + device);
		
		if(getBluetoothState() == STATE_CONNECTING){
			// 取消任何试图建立会话的线程(Cancel any thread attempting to make a connection)
	    	if (connectThread != null) {
	    		// 取消线程
				connectThread.cancel();
				connectThread = null;
			}
		}
    	
    	// 取消当前已经连接会话的线程(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// 取消线程
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// 开启连接给定设备的线程(Start the thread to connect the given device)
    	connectThread = new ConnectThread(device, secure);
    	connectThread.start();
    	setBluetoothState(STATE_CONNECTING);
    	
	}
	
	/**
	 * @Title: connected
	 * @Description: TODO 开启管理来蓝牙连接的线程(Start the thread to manage a bluetooth connect)
	 * @param @param socket 简历连接的端口号(The bluetooth socket on which the connect was made)
	 * @param @param device 已连接的设备(The bluetooth device that has been connected)
	 * @param @param socketType   
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
		// Debugging
    	if (D) Log.e(TAG, " + + + connected + + +");
    	
    	// 取消任何试图建立会话的线程(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// 取消线程
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// 取消当前已经连接会话的线程(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// 取消线程
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// 取消accept线程，因为只要连接一个设备(Cancel the accept thread because we only want to connect to one device)
    	if(acceptThread == null){
    		acceptThread.cancel();
    		acceptThread = null;
    	}
    	
    	// 开启管理蓝牙连接、执行数据传输的线程(Start the thread to manage the connect and perform transmissions)
    	connectedThread = new ConnectedThread(socket, socketType);
    	connectedThread.start();
    	
    	// 向主界面发送已连接设备的名称
    	Message message = bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_DEVICE_NAME);
    	Bundle bundle = new Bundle();
    	bundle.putString(SmartCarActivity.DEVICE_NAME, device.getName());
    	message.setData(bundle);
    	bluetoothHandler.sendMessage(message);
    	
    	// 设置蓝牙连接状态
    	setBluetoothState(STATE_CONNECTED);
	}
	
	/**
	 * @Title: close
	 * @Description: TODO 关闭蓝牙服务
	 * @param    
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public synchronized void close(){
		// Debugging
		if(D) Log.e(TAG, " + + + close + + +");
		
		// 取消任何试图建立会话的线程(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// 取消线程
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// 取消当前已经连接会话的线程(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// 取消线程
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// 取消accept线程(Cancel the accept thread)
    	if(acceptThread == null){
    		acceptThread.cancel();
    		acceptThread = null;
    	}
    	
    	// 设置蓝牙状态
    	setBluetoothState(STATE_CLOSE);
	}
	
	/**
	 * @Title: stop
	 * @Description: TODO 终止蓝牙服务
	 * @param    
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public synchronized void stop() {
		// Debugging
		if(D) Log.e(TAG, " + + + close + + +");
		
		// 取消任何试图建立会话的线程(Cancel any thread attempting to make a connection)
    	if (connectThread != null) {
    		// 取消线程
			connectThread.cancel();
			connectThread = null;
		}
    	
    	// 取消当前已经连接会话的线程(Cancel any thread currently running a connection)
    	if (connectedThread != null) {
			// 取消线程
    		connectedThread.cancel();
    		connectedThread = null;
		}
    	
    	// 取消accept线程(Cancel the accept thread)
    	if(acceptThread == null){
    		acceptThread.cancel();
    		acceptThread = null;
    	}
    	
    	// 设置蓝牙状态
    	setBluetoothState(STATE_NONE);
	}
	
	/**
	 * @Title: write
	 * @Description: TODO 向 已连接线程的输出流 写入数据(Write to connected outstream)
	 * @param @param out 待写入的字符流   
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
	public void write(byte[] out) {
		// 创建临时线程 (create temporary thread)
		ConnectedThread tempThread = null;
		// 临时同步到 已连接设备线程 (synchronize a copy of connectedThread)
		synchronized (this){
			if(bluetoothState != STATE_CONNECTED)
				return;
			// 同步
			tempThread = connectedThread;
		}
		// 向线程写入数据(Perform the write unsynchronized)
		tempThread.write(out);
	}
	
	/**
	 * @Title: connectionFailed
	 * @Description: TODO 通知主界面蓝牙连接失败(Notify the UI Activity that the attempt to connect failed)
	 * @param    
	 * @return void 
	 * @throws
	 * @author: 夏杰 1272570701@qq.com
	 * @date: 2017-4-27
	 */
    private void connectionFailed() {
    	// 向主界面发送连接失败提示(Send a failure message to the Activity)
    	Message message = bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_TOAST);
    	Bundle bundle = new Bundle();
    	bundle.putString(SmartCarActivity.TOAST, "不能连接设备");
    	message.setData(bundle);
    	bluetoothHandler.sendMessage(message);
    	
    	// 启动蓝牙服务来重新监听(Start the Service to restart the listening mode)
    	BluetoothService.this.start();
    }
    
    /**
     * @Title: connectionLost
     * @Description: TODO
     * @param    
     * @return void 
     * @throws
     * @author: 夏杰 1272570701@qq.com
     * @date: 2017-4-27
     */
    private void connectionLost() {
    	// 向主界面发送设备连接丢失提示(Send a failure message to the Activity)
    	Message message = bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_TOAST);
    	Bundle bundle = new Bundle();
    	bundle.putString(SmartCarActivity.TOAST, "设备连接丢失");
    	message.setData(bundle);
    	bluetoothHandler.sendMessage(message);
    	
    	// 启动蓝牙服务来重新监听(Start the Service to restart the listening mode)
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
         * @Description: TODO 构造函数
         * @param @param device 要连接的设备
         * @param @param secure
         * @throws
         * @author: 夏杰 1272570701@qq.com
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
    	 * @Description: TODO 执行蓝牙连接
    	 * @param  
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void run() {
    		Log.i(TAG, " + + + Begin ConnectThread, socketType:" + socketType);
    		setName("ConnectThread " + socketType);
    		
    		// 取消蓝牙搜索，提升当前连接线程的效率(Always cancel discovery because it will slow down a connection)
    		bluetoothAdapter.cancelDiscovery();
    		
    		try {
    			// 在蓝牙端口建立蓝牙连接(This is a blocking call and will only return on a successful connection or an exception)
				bluetoothSocket.connect();
			} catch (IOException e) {
				Log.e(TAG, " + + + Connection fail ", e);
				try {
					// 关闭蓝牙端口
					bluetoothSocket.close();
				} catch (IOException e1) {
					Log.e(TAG, " + + + " , e1);
				}
				connectionFailed();
				return;
			}
			
			// 连接完成，重置连接线程
			synchronized (BluetoothService.this) {
				connectThread = null;
			}
			// 开启已连接线程
			connected(bluetoothSocket, bluetoothDevice, socketType);
    		
    	}
    	
    	/**
    	 * @Title: cancel
    	 * @Description: TODO 关闭蓝牙连接
    	 * @param    
    	 * @return void 
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void cancel() {
    		try {
				// 关闭蓝牙端口
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
    	 * @Description: TODO 构造函数
    	 * @param @param socket
    	 * @param @param socketType
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public ConnectedThread(BluetoothSocket socket, String socketType) {
    		// Debugging
    		Log.d(TAG, "create ConnectedThread: " + socketType);
    		// 初始化成员变量
    		bluetoothSocket = socket;
    		OutputStream tempOutputStream = null;
    		InputStream tempInputStream = null;
    		
    		// 获取蓝牙端口输入输出流
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
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void run() {
    		// Debugging
    		Log.e(TAG, " + + + begin ConnectedThread + + + ");
    		byte[] buffer = new byte[1024];
    		int bytes;
    		
    		// 设备已连接之后，保持对输入流的监听(Keep listening to the inputstream)
    		while(true) {
    			try {
    				// 从输入流读数据(Read from inputStream)
					bytes = inputStream.read(buffer);
					// 把接收到的数据发送到主界面
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
    	 * @Description: TODO 向输出流写入数据
    	 * @param @param buffer   
    	 * @return void 
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void write(byte[] buffer) {
    		try {
				outputStream.write(buffer);
				// 将发送的数据显示到主界面(Share the sent message back to the UI Activity)
				// bluetoothHandler.obtainMessage(SmartCarActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
    	}
    	
    	/**
    	 * @Title: cancel
    	 * @Description: TODO 关闭蓝牙连接
    	 * @param    
    	 * @return void 
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void cancel() {
    		try {
				// 关闭蓝牙端口
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
    	 * @Description: TODO 构造函数
    	 * @param @param secure
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public AcceptThread(boolean secure){
    		socketType = secure ? "Secure" : "Insecure";
    		BluetoothServerSocket tempServerSocket = null;
    		
    		try {
    			// 创建新的监听服务端口(Create a new listening server socket)
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
    	 * @Description: TODO 接受蓝牙连接请求
    	 * @param  
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
    	 * @date: 2017-4-27
    	 */
    	public void run() {
    		// Debugging
    		if(D) Log.d(TAG, "Socket Type: " + socketType + "BEGIN mAcceptThread" + this);
    		setName("AcceptThread" + socketType);
    		
    		BluetoothSocket socket = null;
    		// 未连接时监听服务窗口(Listen to the server socket if we're not connected)
    		while(bluetoothState != STATE_CONNECTED) {
    			// 接收连接请求
    			try {
    				// This is a blocking call and will only return on a successful connection or an exception
					socket = bluetoothServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "Socket Type: " + socketType + "accept() failed", e);
                    break;
				}
				
				// 连接请求被接受之后(If a connection was accepted)
				if (socket != null) {
					synchronized (BluetoothService.this) {
						
						//
						switch (bluetoothState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// 状态正常，开启连接线程(Situation normal. Start the connected thread.)
							connected(socket, socket.getRemoteDevice(), socketType);
							break;
									
						case STATE_NONE:
                        case STATE_CLOSE:
                        case STATE_CONNECTED:
                        	try {
                        		// 既不是准备状态也不是已连接状态，终止端口(Either not ready or already connected. Terminate new socket.)
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
    	 * @Description: TODO 取消连接
    	 * @param    
    	 * @return void 
    	 * @throws
    	 * @author: 夏杰 1272570701@qq.com
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
