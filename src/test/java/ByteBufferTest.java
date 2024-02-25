import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static io.bota5ky.ByteBufferUtil.debugAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bota5ky
 * @since 2024-02-24 20:44
 */
public class ByteBufferTest {
    @Test
    void read_limit_data() {
        // 获得FileChannel的两种方式
        // 1.输入输出流 2.RandomAccessFile
        try (FileInputStream fileInputStream = new FileInputStream("src/test/resources/data.txt")) {
            FileChannel channel = fileInputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);
            channel.read(buffer);
            // 切换至读模式
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                System.out.print((char) b);
            }
        } catch (IOException ignored) {
        }
    }

    @Test
    void read_all_data() {
        try (FileInputStream fileInputStream = new FileInputStream("src/test/resources/data.txt")) {
            FileChannel channel = fileInputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (channel.read(buffer) != -1) {
                // 切换至读模式
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    System.out.print((char) b);
                }
                // 清空buffer
                buffer.clear();
            }
        } catch (IOException ignored) {
        }
    }

    @Test
    void test_flip_and_compact() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61); // a
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64, 0x65}); // b c d
        debugAll(buffer);
        // position仍在写入位置，读到00
        // get()好像会增加limit
        System.out.println(Integer.toHexString(buffer.get()));
        buffer.flip();
        System.out.println(Integer.toHexString(buffer.get()));
        debugAll(buffer);
        // 注释第一次打印，可以明显看到数据前移，并保留了原来的数据
        buffer.compact();
        debugAll(buffer);
    }

    @Test
    void show_allocate_and_allocate_direct_difference() {
        assertEquals("class java.nio.HeapByteBuffer", ByteBuffer.allocate(10).getClass().toString());
        assertEquals("class java.nio.DirectByteBuffer", ByteBuffer.allocateDirect(10).getClass().toString());
        // class java.nio.HeapByteBuffer - java堆内存，读写效率低，受到GC的影响
        // class java.nio.DirectByteBuffer - 直接内存操作系统控制，读写效率高（少一次拷贝），不会受GC影响，但分配效率低，容易造成内存泄露
    }

    @Test
    void test_rewind_mark_and_reset() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        buffer.get(new byte[4]);
        debugAll(buffer);
        buffer.rewind();
        buffer.get(new byte[4]);
        debugAll(buffer);
        buffer.rewind();
        // mark 记录一个position位置
        // reset 将position重置到mark位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark(); // 加标记，索引2的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset(); // 将position重置到索引2
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());

        // get(i)不改变position
        System.out.println((char) buffer.get(3));
        debugAll(buffer);
    }

    @Test
    void convert_string_to_byte_buffer_and_vice_versa() {
        // 1.字符串转为ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1); // 不切换模式

        // 2.指定字符集转换
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2); // 自动切换到读模式

        // 3.wrap
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3); // 和方法2效果一样

        // 返回类型为CharBuffer需要toString()转换
        System.out.println(StandardCharsets.UTF_8.decode(buffer2));
        // 在写模式decode会无输出，因为position没有改变，需要flip()
        System.out.println(StandardCharsets.UTF_8.decode(buffer1));
    }

    @Test
    void scattering_read() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("src/test/resources/data.txt", "r")) {
            FileChannel channel = randomAccessFile.getChannel();
            ByteBuffer b1 = ByteBuffer.allocate(5);
            ByteBuffer b2 = ByteBuffer.allocate(5);
            ByteBuffer b3 = ByteBuffer.allocate(3);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        } catch (IOException ignored) {
        }
    }

    @Test
    void gathering_write() {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("!");

        try (RandomAccessFile randomAccessFile = new RandomAccessFile("src/test/resources/write.txt", "rw")) {
            FileChannel channel = randomAccessFile.getChannel();
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException ignored) {
        }
    }
}
