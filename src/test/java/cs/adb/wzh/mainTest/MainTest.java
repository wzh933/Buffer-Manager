package cs.adb.wzh.mainTest;

import cs.adb.wzh.utils.PageRequestReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class MainTest {
    @Test
    void mainTest() throws IOException {
        PageRequestReader prr = new PageRequestReader("src/main/resources/testTxt1.txt");

    }
}
