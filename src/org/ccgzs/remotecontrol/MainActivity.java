package org.ccgzs.remotecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button upbtn = null;
	private Button downbtn = null;
	private Button leftbtn = null;
	private Button rightbtn = null;
	private Button connbtn = null;
	private Button disbtn = null;
	
	private Socket sock = null;
	private InputStream in = null;
	private OutputStream out = null;
	
	private byte[] bytes = null;
	private byte[] up_bytes = new byte[] {
			(byte)4, (byte)0, (byte)0, (byte)38,
			(byte)4, (byte)2, (byte)0, (byte)2,
			(byte)4, (byte)0, (byte)2, (byte)38,
	};
	private byte[] down_bytes = new byte[] {
			(byte)4, (byte)0, (byte)0, (byte)40,
			(byte)4, (byte)2, (byte)0, (byte)2,
			(byte)4, (byte)0, (byte)2, (byte)40,
	};
	private byte[] left_bytes = new byte[] {
			(byte)4, (byte)0, (byte)0, (byte)37,
			(byte)4, (byte)2, (byte)0, (byte)2,
			(byte)4, (byte)0, (byte)2, (byte)37,
	};
	private byte[] right_bytes = new byte[] {
			(byte)4, (byte)0, (byte)0, (byte)39,
			(byte)4, (byte)2, (byte)0, (byte)2,
			(byte)4, (byte)0, (byte)2, (byte)39,
	};

	private Handler conn_success_handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			upbtn.setEnabled(true);
	        downbtn.setEnabled(true);
	        leftbtn.setEnabled(true);
	        rightbtn.setEnabled(true);
	        disbtn.setEnabled(true);
		};
	};
	
	private Handler send_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	private Handler conn_fail_handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			connbtn.setEnabled(true);
			if(msg.what == 1) {
				Toast.makeText(getApplicationContext(), "查找服务器失败", Toast.LENGTH_SHORT).show();
			} else if(msg.what == 2) {
				Toast.makeText(getApplicationContext(), "连接到服务器失败", Toast.LENGTH_SHORT).show();
			}
		};
	};
	
    private OnClickListener clicker = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			try {
				if(view.getId() == R.id.upbtn) {
					bytes = up_bytes;
				} else if(view.getId() == R.id.downbtn) {
					bytes = down_bytes;
				} else if(view.getId() == R.id.leftbtn) {
					bytes = left_bytes;
				} else if(view.getId() == R.id.rightbtn) {
					bytes = right_bytes;
				}
				out.write(bytes);
				Message msg = send_handler.obtainMessage(0);
				msg.sendToTarget();
			} catch (IOException e) {
				Message msg = send_handler.obtainMessage(1);
				msg.sendToTarget();
				e.printStackTrace();
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        upbtn = (Button) findViewById(R.id.upbtn);
        downbtn = (Button) findViewById(R.id.downbtn);
        leftbtn = (Button) findViewById(R.id.leftbtn);
        rightbtn = (Button) findViewById(R.id.rightbtn);
        connbtn = (Button) findViewById(R.id.connbtn);
        disbtn = (Button) findViewById(R.id.disbtn);
        
        upbtn.setEnabled(false);
        downbtn.setEnabled(false);
        leftbtn.setEnabled(false);
        rightbtn.setEnabled(false);
        disbtn.setEnabled(false);
        
        upbtn.setOnClickListener(clicker);
        downbtn.setOnClickListener(clicker);
        leftbtn.setOnClickListener(clicker);
        rightbtn.setOnClickListener(clicker);
        
        connbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				(new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							sock = new Socket("192.168.2.103", 8888);
							Message msg_succ = conn_success_handler.obtainMessage(1);
							msg_succ.sendToTarget();
							in = sock.getInputStream();
							out = sock.getOutputStream();
						} catch (UnknownHostException e) {
							e.printStackTrace();
							Message msg_fail = conn_fail_handler.obtainMessage(1);
							msg_fail.sendToTarget();
						} catch (IOException e) {
							e.printStackTrace();
							Message msg_fail = conn_fail_handler.obtainMessage(2);
							msg_fail.sendToTarget();
						}
					}
				})).start();
				connbtn.setEnabled(false);
			}
		});
    }
}
