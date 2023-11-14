package cs.adb.wzh.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Wang Zihui
 * @date 2023/11/13
 **/
class PageRecordReaderTest {

    @Test
    void pageRecordReaderTest() throws IOException {
        pageRecordReader prr = new pageRecordReader("src/main/resources/testTxt1.txt");
        for (int i = 0; i < prr.getRecordNum(); i++) {
            System.out.printf("%d, %d\n", prr.getPageId(i), prr.getOperation(i));
        }
    }
}