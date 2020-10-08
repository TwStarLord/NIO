package my;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author 孟享广
 * @create 2020-07-31 10:09 上午
 */
public class TestChannel {

    /**
     * 通道之间的数据传输（也是使用的直接缓冲区的方式）
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        //StandardOpenOption.CREATE 和 StandardOpenOption.CREATE_NEW 在文件已存在的情况下，前者为覆盖，后者会报错
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //通道直接传递，对于大文件的话需要考虑到效率问题
        inChannel.transferTo(0,inChannel.size(),outChannel);
        //这句与上句效果相同
        //outChannel.transferFrom(inChannel,0,inChannel.size());

        inChannel.close();
        outChannel.close();
    }

    /**
     * 该方法为直接缓冲区读写模式
     * 即直接在物理内存中读写，省去了从JVM内存copy到物理内存的步骤
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        //StandardOpenOption.CREATE 和 StandardOpenOption.CREATE_NEW 在文件已存在的情况下，前者为覆盖，后者会报错
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //该映射文件
        //注意：只支持ByteBuffer
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //直接对直接缓冲区进行读写操作
        byte[] buf = new byte[inMappedBuf.limit()];
        inMappedBuf.get(buf);
        outMappedBuf.put(buf);

        //关闭通道
        inChannel.close();
        outChannel.close();

    }

    @Test
    public void test1(){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fisChannel = null;
        FileChannel fosChannel = null;
        try {
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");

            fisChannel = fis.getChannel();
            fosChannel = fos.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (fisChannel.read(buffer) != -1){
                buffer.flip();
                fosChannel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fisChannel != null) {
                try {
                    fisChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fosChannel != null) {
                try {
                    fosChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
