package com.isoft.baidu.location;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class BDService extends Service{

	private LocationClient mLocationClient = null;
	public String ServerIP = "115.28.228.149";
	public int ServerPort = 8001;
	DatagramSocket socket;
	InetAddress inet;
	private String datasString="";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		ServerIP = intent.getStringExtra("ServerIP");
		ServerPort = Integer.parseInt(intent.getStringExtra("ServerPort"));
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		acquireWakeLock();
		// setup socket connect
		try {
			
			socket = new DatagramSocket();
			inet = InetAddress.getByName(ServerIP);
			// PostData("Testing.....");
			} catch (Exception e) {
			// TODO Auto-generated catch block
				Log.i("baidu","1------------------------------");
				e.printStackTrace();
			}
		mLocationClient = new LocationClient(getApplicationContext());
		InitLocation();//初始化定位
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				Log.i("baidu","---------------------");
				datasString = "";
				datasString = "9929A80800106C0A|"+location.getLongitude()+"|"+location.getLatitude()+"|"+location.getDirection()+"|"+
						location.getSpeed()+"|"+location.getTime();
				Log.i("baidu",datasString+"---------------------");
				try {
					String dataStr = bin2hex(datasString);
					final DatagramPacket dp = new DatagramPacket(dataStr.getBytes(),
							dataStr.getBytes().length, inet, ServerPort);

					new Thread(new Runnable() {

						public void run() {
							// TODO Auto-generated method stub
							try {
								socket.send(dp);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mLocationClient.start();
	}
	
	/**
	 * 字符串转换成十六进制值
	 * 
	 * @param bin
	 *            String 我们看到的要转换成十六进制的字符串
	 * @return
	 */
	public static String bin2hex(String bin) {
		char[] digital = "0123456789ABCDEF".toCharArray();
		StringBuffer sb = new StringBuffer("");
		byte[] bs = bin.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(digital[bit]);
			bit = bs[i] & 0x0f;
			sb.append(digital[bit]);
		}
		return sb.toString();
	}
	
	/*
	 * 初始化
	 */
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(8000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(false);
		mLocationClient.setLocOption(option);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		socket.close();
		releaseWakeLock();
	}
	WakeLock mWakeLock;

	// 申请设备电源锁
	private void acquireWakeLock() {
		String TAG = "My Tag";
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, TAG);
			if (null != mWakeLock) {
				mWakeLock.acquire();
			}
		}
	}

	// 释放设备电源锁
	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
}
