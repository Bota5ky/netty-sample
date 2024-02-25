import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static io.bota5ky.ByteBufferUtil.debugAll;

/**
 * @author Bota5ky
 * @since 2024-02-25 10:19
 */
class StickyPackageTest {
    /*
        网络上有多条数据发送给服务端，数据之间使用\n进行分隔
        但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
        Hello,world\n
        I'm zhang san\n
        How are you?\n
        变成了下面的两个 byteBuffer
        Hello,world\nI'm zhang san\nHo
        w are you?\n
        现在要求你编写程序，将错乱的数据恢复成原始的按\n分的数据

        黏包：提高发送效率导致
        半包：服务器缓冲区大小限制导致的
     */
    @Test
    void solve_sticky_package() {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhang san\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                //从source读，target写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
