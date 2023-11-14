package cs.adb.wzh.mainTest;

import cs.adb.wzh.utils.pageRecordReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class MainTest {
    @Test
    void mainTest() throws IOException {
        pageRecordReader prr = new pageRecordReader("src/main/resources/testTxt1.txt");

    }
}
