import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * 测试 filp 和 rewind 的区别，对于position 和 limit 值的改变
 * 参考文章 https://zhuanlan.zhihu.com/p/58157432
 *
 * 结论：
 * rewind 与 flip 都会把 position 设置为 0，且都会丢弃 mark 值（其实是设置为 -1），但不同点在于：
 * 1.flip 会改变 limit 的值，一般会设置为当前的读写位置；
 * 2.rewind 不会改变 limit 的值，一般会设置为 capacity 的值；
 *
 * 使用场景
 *
 * 一定要保证 limit 值设置为正确的前提下，才能使用 rewind，否则可能出现多余的数据，
 * 见上例，而 flip 是将当前的位置 position 赋值给 limit，所以适用于读写当前位置之前的数据。
 */
public class TestFlipAndRewind {
    /**
     *在 ByteBuffer 的使用中，为了尽可能地复用 Buffer，
     * 我们不可避免地要重新设置 ByteBuffer 的 position 与 limit，
     * 它们之间的差值就是 remaining() 方法的结果，
     * 也是 Channel 真正写入或读取的数据（超过 limit 的数据会自动丢弃）：
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(10);

        //  存入7个字节的数据
        buff.put("ABCDEFG".getBytes());

        //  声明通道
        WritableByteChannel outChannel = Channels.newChannel(System.out);

        //  设置有效的读取位置
        buff.position(6).limit(7);

        //  利用通道输出结果
        outChannel.write(buff);

        //  关闭通道
        outChannel.close();
    }

    /**
     *当为 buff.flip() 时，输出 H。
     *
     * 当为 buff.rewind() 时，输出 HBCDEFG +3个空格。
     *
     * 按照上面的说明，执行 flip()后，limit 的值为1，position 的值为0，所以只会输出 “H”。
     *
     * 而执行 rewind() 后，limit 的值为 10，position 的值为 0，所以不仅会输出 “HBCDEFG” 7个字符，还会额外多输出 3 个空白字符，共计 10 个字节。
     *
     * 使用场景
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        //  limit的默认值与capacity相等
        ByteBuffer buff = ByteBuffer.allocate(10);
        WritableByteChannel outChannel = Channels.newChannel(System.out);
        //  存入数据
        buff.put("ABCDEFG".getBytes());
        //  改变位置
        buff.position(0);
        buff.put("H".getBytes());
        //  翻转或倒回
        //buff.flip();
          buff.rewind();
        System.out.println("position=>"+buff.position());
        System.out.println("limit=>"+buff.limit());

        //  输出结果
        outChannel.write(buff);
        outChannel.close();
    }
}
