import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class TestBlockingNIO2 {
	
	//客户端 负责发送数据
	@Test
	public void client() throws IOException{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		while(inChannel.read(buf) != -1){
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		System.out.println("客户端数据已经发送完毕!");
		//System.out.println("测试buf是否为空"+sChannel.read(buf));
		sChannel.shutdownOutput();

		//接收服务端的反馈
		int len = 0;
		while((len = sChannel.read(buf)) != -1){
			System.out.println("测试buf是否为空"+len);
			buf.flip();
			System.out.println(new String(buf.array(), 0, len));
			buf.clear();
		}
		
		inChannel.close();
		sChannel.close();
	}
	
	//服务端 负责接受数据
	@Test
	public void server() throws IOException{
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		
		FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		
		ssChannel.bind(new InetSocketAddress(9898));
		
		SocketChannel sChannel = ssChannel.accept();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		int len = 0;
		//这里使用的clear()方法其实并没有将buf清空，只是将position和mark重置，后面进行测试buf
		while((len = sChannel.read(buf)) != -1){
			buf.flip();
			outChannel.write(buf);
			buf.clear();
			System.out.println("在接收了客户端的数据之后，buf的字节数组长度为："+len);
		}
		//此处为什么没有跳出循环呢？
		System.out.println("客户端数据已经接受完毕!");
		//发送反馈给客户端
		buf.put("服务端接收数据成功".getBytes());
		buf.flip();
		sChannel.write(buf);
		
		sChannel.close();
		outChannel.close();
		ssChannel.close();
	}

}
