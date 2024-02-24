import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Bota5ky
 * @since 2024-02-24 20:44
 */
public class ByteBufferTest {
    @Test
    void should_read_data_from_file_when_use_byte_buffer_given_file_channel() {
        // 获得FileChannel的两种方式
        // 1. 输入输出流 2. RandomAccessFile
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
}
