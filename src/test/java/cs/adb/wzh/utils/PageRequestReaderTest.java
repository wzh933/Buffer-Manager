package cs.adb.wzh.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Wang Zihui
 * @date 2023/11/13
 **/
class PageRequestReaderTest {

    @Test
    void pageRecordReaderTest() throws IOException {
        PageRequestReader prr = new PageRequestReader("src/main/resources/testTxt1.txt");
        for (int i = 0; i < prr.getRequestNum(); i++) {
            System.out.printf("%d, %d\n", prr.getPageId(i), prr.getOperation(i));
        }
    }
}