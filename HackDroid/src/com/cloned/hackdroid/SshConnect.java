package com.cloned.hackdroid;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SshConnect extends Activity {
	/** Called when the activity is first created. */
	String resultText;
	EditText command;
	TextView result;
	TextView IPtext;
	Button connectBtn;
	Button disconnectBtn;
	Button commandBtn;
	Session session;
	ByteArrayOutputStream baos;
	ByteArrayInputStream bais;
	Channel channel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terminal);
		bais = new ByteArrayInputStream(new byte[1000]);
		IPtext = (TextView) findViewById(R.id.IPtext);
		command = (EditText) findViewById(R.id.editText1);
		result = (TextView) findViewById(R.id.terminal);
		connectBtn = (Button) findViewById(R.id.button1);
		disconnectBtn = (Button) findViewById(R.id.button2);
		commandBtn = (Button) findViewById(R.id.button3);

		// Sets textview to empty
		result.setText("");
		result.setMovementMethod(new ScrollingMovementMethod());
		// fixes issue with NetworkOnMainThreadException
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		IPtext.setText("Android IP: " + getIpAddr());

		// ** Buttons her**//
		commandBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		disconnectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					JSch jsch = new JSch();
					Session session = jsch.getSession("cloned",
							"192.168.87.101", 22);
					session.setPassword("xxx");

					// Avoid asking for key confirmation
					Properties prop = new Properties();
					prop.put("StrictHostKeyChecking", "no");
					session.setConfig(prop);
					session.disconnect();
					result.setText("Disconnected");

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}
		});

		connectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					JSch jsch = new JSch();
					Session session = jsch.getSession("cloned",
							"192.168.87.101", 22);
					session.setPassword("x");
					
					// Avoid asking for key confirmation
					Properties prop = new Properties();
					prop.put("StrictHostKeyChecking", "no");
					session.setConfig(prop);

					session.connect();

					ChannelExec channel = (ChannelExec) session
							.openChannel("exec");
					channel.setCommand("ls");
					channel.connect();

					// Controls the input stream, making it look more readable.
					InputStream outputstream_from_the_channel = channel
							.getInputStream();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(outputstream_from_the_channel));
					String line;
					int index = 0;

					while ((line = br.readLine()) != null) {
						
						result.append(line + "\n");
					}
					
					// closes the session to the computer after input have been received.
					int exitStatus = channel.getExitStatus();
	                if (channel != null) channel.disconnect();
	                if (session != null) session.disconnect();
					
					
//					channel.disconnect();
//	
//					session.disconnect();
//					

					if (exitStatus < 0) {
						System.out.println("Done, but exit status not set!");
					} else if (exitStatus > 0) {
						System.out.println("Done, but with error!");
					} else {
						System.out.println("Done!");
					}
					

				} catch (Exception e) {
					System.out.println(e.getMessage());
					result.append(e.getMessage() + "\n");
				}

			}
		});
	}

	public String getIpAddr() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format("%d.%d.%d.%d", (ip & 0xff),
				(ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

		return ipString;
	}

	private static void setUpHostKey(Session session) {
		// Note: There are two options to connect
		// 1: Set StrictHostKeyChecking to no
		// Create a Properties Object
		// Set StrictHostKeyChecking to no
		// session.setConfig(config);
		// 2: Use the KnownHosts File
		// Manually ssh into the appropriate machines via unix
		// Go into the .sshknown_hosts file and grab the entries for the hosts
		// Add the entries to a known_hosts file
		// jsch.setKnownHosts(khfile);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
	}

}