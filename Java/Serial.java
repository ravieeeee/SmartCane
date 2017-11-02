package graphTest;

import gnu.io.CommPort;


import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Serial {
	public static void main(String[] args) {
		try {
			// Window : COM3, Raspberry : /dev/ttyACM0
			String SerialPortID = "COM3";
			System.setProperty("gnu.io.rxtx.SerialPorts", SerialPortID);
			(new Serial()).connect(SerialPortID); // 입력한 포트로 연결
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Serial() {
		super();
	}

	void connect(String portName) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			// 클래스 이름을 식별자로 사용하여 포트 오픈
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				// 포트 설정(통신속도 설정. 기본 9600으로 사용)
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				// InputStream 버퍼 생성 후 오픈
				InputStream in = serialPort.getInputStream();

				// 읽기 쓰레드 작동
				(new Thread(new SerialReader(in))).start();

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
		
	}

	// 데이터 수신
	public static class SerialReader implements Runnable {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void run() {
			communicate();
		}

		// 소켓 통신
		public void communicate() {
			int count = 0;

			byte[] buffer = new byte[1024];

			// 임시적 String 배열
			String[] bufferSet = new String[100];

			// 최종적으로 전달할 double 배열
			double[] distances = new double[100];
			int len = -1;

			// String 배열 초기화
			for (int i = 0; i < 100; i++) {
				bufferSet[i] = "";
			}

			// Java - Swift
			// 자동 close
			try (ServerSocket server = new ServerSocket()) {
				// 서버 초기화
				InetSocketAddress ipep = new InetSocketAddress(9999);
				server.bind(ipep);

				System.out.println("Initialize complate");

				// LISTEN 대기
				Socket client = server.accept();
				System.out.println("Connection");
				
				OutputStream sender = null;
				InputStream reciever = null;

				try {
					// 값이 들어오는 동안 계속 반복
					while ((len = this.in.read(buffer)) > -1) {
						String str = new String(buffer, 0, len);

						// 구분자 처리
						if (new String(buffer, 0, len).contains("a")) {
							int i = new String(buffer, 0, len).indexOf("a");
							if (i != 0) {
								bufferSet[count] += new String(buffer, 0, i);
							}
							if (bufferSet[count].contains(System.getProperty("line.separator"))) {
								bufferSet[count] = bufferSet[count].replaceAll(System.getProperty("line.separator"),
										"");
							}
							count++;
						} else {
							bufferSet[count] += new String(buffer, 0, len);
						}
						
						// 카운트가 꽉찼을 때, 초기화 작업
						if (count == 100) {
							count = 0;
							for (int i = 0; i < 100; i++) {
								distances[i] = Double.parseDouble(bufferSet[i]);
							}
							
							// send, reciever 스트림 받아오기
							// 자동 close
							try {
								if (sender == null) {
									sender = client.getOutputStream();
								}
								if (reciever == null) {
									reciever = client.getInputStream();
								}
								
								// 클라이언트로 hello world 메시지 보내기
								// 11byte 데이터
								for (int i = 0; i < 100; i++) {
									String message = Double.toString(distances[i]);
									byte[] data = message.getBytes();
									sender.write(data, 0, data.length);
									System.out.println(message);
									// 클라이언트로부터 메시지 받기
									// 2byte 데이터
									data = new byte[2];
									reciever.read(data, 0, data.length);

									// 수신 메시지 출력
									message = new String(data);
									String out = String.format("recieve - %s", message);
									System.out.println(out);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							// 확인용 콘솔창 출력
							for (int i = 0; i < 100; i++) {
								System.out.println("distances[" + i + "] = " + distances[i]);
							}

							// String 배열 초기화
							for (int i = 0; i < 100; i++) {
								bufferSet[i] = "";
							}
						}
						
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}