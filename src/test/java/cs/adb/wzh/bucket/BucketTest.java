package cs.adb.wzh.bucket;

import cs.adb.wzh.bufferControlBlocks.BCB;
import org.junit.jupiter.api.Test;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
class BucketTest {
    @Test
    void bucketTest() throws Exception {
        final int bcbSize = 220;
        final int bucketSize = 4;
        BCB[] bcbs = new BCB[bcbSize];
        Bucket[] buckets = new Bucket[bucketSize];
        for (int i = 0; i < bucketSize; i++) {
            buckets[i] = new Bucket();
        }
        for (int i = 0; i < bcbSize; i++) {
            bcbs[i] = new BCB(i * 10);
            bcbs[i].setPageId(i);
            buckets[i % bucketSize].appendBCB(bcbs[i]);
        }

//        BCB resBCB = buckets[0].searchPage(19);
//        System.out.println(resBCB);
        buckets[3].printBucket();
        buckets[3].removeBCB(buckets[3].searchPage(7));
        buckets[3].printBucket();
    }
}
