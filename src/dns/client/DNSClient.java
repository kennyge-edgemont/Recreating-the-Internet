package dns.client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

import util.Util;

public class DNSClient {
	
	public static void main(String[] args) throws IOException {
		//convert int to two bytes
		Scanner sc = new Scanner(System.in);
		DNSClient client = new DNSClient(InetAddress.getByName("localhost"));
		
		while(true) {
			int port = sc.nextInt();
			
			byte a = (byte) port;
			byte b = (byte) (port >>> 8);
			
			System.out.println(a);
			System.out.println(b);
			
			int x = (b & 0xff << 8) | (a & 0xff);
			
			System.out.println(x);
		}
	}
	
	public DatagramSocket socket;
	public InetAddress serverIP;
	
	public DNSClient(InetAddress serverIP) throws SocketException {
		this.serverIP = serverIP;
		
		socket = new DatagramSocket();
	}
	
	public void registerDomain(InetAddress ip, int port, String name) throws IOException {
		if(name.length() > 255) {
			throw new RuntimeException("Domain name too long");
		}
		
		byte operation = 0;
		byte[] address = ip.getAddress();
		byte[] portBytes = Util.intToTwoBytes(port);
		byte[] nameBytes = name.getBytes();
		
		byte[] total = new byte[1 + 4 + 2 + nameBytes.length];
		
		total[0] = operation;
		
		for(int i = 1; i <= 4; i++) {
			total[i] = address[i - 1];
		}
		
		total[5] = portBytes[0];
		total[6] = portBytes[1];
		
		for(int i = 0; i < nameBytes.length; i++) {
			total[i + 7] = nameBytes[i];
		}
		
		this.sendPacket(total);
	}
	
	private void sendPacket(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, 0, data.length);
		packet.setAddress(serverIP);
		packet.setPort(5000);
		
		socket.send(packet);
	}
	
}
