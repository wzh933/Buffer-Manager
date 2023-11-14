package cs.adb.wzh.bufferManager;

import cs.adb.wzh.Storage.Buffer;
import cs.adb.wzh.bufferControlBlocks.BCB;
import org.junit.jupiter.api.Test;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
class BMgrTest {
    @Test
    void bMgrTest() throws Exception {
        Buffer bf = new Buffer(8);
        BMgr bMgr = new BMgr(bf, "src/main/resources/testTxt1.txt");
        for (BCB p = bMgr.getHead(); p.getFrameId() != -1; p = p.getNext()) {
            System.out.println(p.getFrameId());
        }
        System.out.println();
        for (BCB p = bMgr.getTail().getPre(); p.getFrameId() != -1; p = p.getPre()) {
            System.out.println(p.getFrameId());
        }
//        System.out.println(bMgr.getTail().getFrameId());
    }


    @Test
    void bcbTableTest() throws Exception {
        Buffer bf = new Buffer(8);
        BMgr bMgr = new BMgr(bf, "src/main/resources/testTxt1.txt");
        BCB[] bcbTable = bMgr.getBcbTable();
        for (BCB bcb : bcbTable) {
            System.out.println(bcb.getFrameId());
        }
        System.out.println();
        for (BCB bcb : bcbTable) {
            System.out.println(bcb.getNext().getFrameId());
        }
    }

    @Test
    void bcbMove2HeadTest() throws Exception {
        Buffer bf = new Buffer(8);
        BMgr bMgr = new BMgr(bf, "src/main/resources/testTxt1.txt");
        BCB[] bcbTable = bMgr.getBcbTable();
        bMgr.move2Head(bMgr.getTail().getPre());
        for (BCB p = bMgr.getHead(); p.getFrameId() != -1; p = p.getNext()) {
            System.out.println(p.getFrameId());
        }
    }
}
