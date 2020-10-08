import java.nio.CharBuffer;

public class TestBufferClear {

    public static void main(String[] args) {
        CharBuffer buf = CharBuffer.allocate(48);
        buf.put(new char[] {'a', 'b', 'c', 'd', 'e'});
        System.out.println("position1=>"+buf.position());
        System.out.println("limit1=>"+buf.limit());
        //此次flip后将limit设置为buf存储的最大字节数
        buf.flip();
        System.out.println("position2=>"+buf.position());
        System.out.println("limit2=>"+buf.limit());
        System.out.println(buf.get());
        //此次clear之后将limit重新设置为capiticy初始值
        buf.clear();
        System.out.println("position3=>"+buf.position());
        System.out.println("limit3=>"+buf.limit());
        buf.put('g');
        buf.flip();
        System.out.println("position4=>"+buf.position());
        System.out.println("limit4=>"+buf.limit());
        System.out.println(buf.get());
        //System.out.println(buf.get());
    }
    }
