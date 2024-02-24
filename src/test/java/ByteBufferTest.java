import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static io.bota5ky.ByteBufferUtil.debugAll;

/**
 * @author Bota5ky
 * @since 2024-02-24 20:44
 */
public class ByteBufferTest {
    @Test
    void should_read_limit_data_from_file_when_use_byte_buffer_given_file_channel() {
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
    void should_read_all_data_from_file_when_use_byte_buffer_given_file_channel() {
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
    void should_flip_compact_when_use_byte_buffer_given_file_channel() {
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
}
