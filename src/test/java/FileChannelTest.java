import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Bota5ky
 * @since 2024-02-25 17:09
 */
class FileChannelTest {
    @Test
    void transfer_to() {
        try (FileInputStream fileInputStream = new FileInputStream("src/test/resources/data.txt");
             FileOutputStream fileOutputStream = new FileOutputStream("src/test/resources/to.txt")) {
            FileChannel from = fileInputStream.getChannel();
            FileChannel to = fileOutputStream.getChannel();
            // 效率高，底层会利用操作系统的零拷贝进行优化
            // 一次最多传输2G的数据
            long size = from.size();
            for (long left = size; left > 0; ) {
                left -= from.transferTo((size -left), left, to);
            }
        } catch (IOException ignored) {
        }
    }
}
