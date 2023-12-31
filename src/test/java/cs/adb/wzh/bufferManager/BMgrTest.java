package cs.adb.wzh.bufferManager;

import cs.adb.wzh.Storage.Buffer;
import cs.adb.wzh.Storage.Disk;
import cs.adb.wzh.bufferControlBlocks.BCB;
import cs.adb.wzh.utils.PageRequestReader;
import cs.adb.wzh.utils.SwapMethod;
import org.junit.jupiter.api.Test;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
class BMgrTest {
    @Test
    void bMgrTest() throws Exception {
        Buffer bf = new Buffer(8);
        Disk disk = new Disk();
        BMgr bMgr = new BMgr(bf, disk);
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
        Disk disk = new Disk();
        BMgr bMgr = new BMgr(bf, disk);
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
        Disk disk = new Disk();
        BMgr bMgr = new BMgr(bf, disk);
        BCB[] bcbTable = bMgr.getBcbTable();
        bMgr.move2Head(bMgr.getTail().getPre());
        for (BCB p = bMgr.getHead(); p.getFrameId() != -1; p = p.getNext()) {
            System.out.println(p.getFrameId());
        }
    }

    @Test
    void fixPageTest() throws Exception {
        Buffer bf = new Buffer();
        Disk disk = new Disk();
        BMgr bMgr = new BMgr(bf, disk);
        bMgr.setSwapMethod(SwapMethod.CLOCK);
//        bMgr.setSwapMethod(SwapMethod.LRU);

/*
        //这样就会有”当前磁盘已满，无法分配新页面！“的警告
        for (int i = 0; i < 65537; i++) {
            System.out.println(bMgr.fixNewPage());
        }
        bMgr.fixNewPage();
        bMgr.fixPage(10);
*/
        int fileLength = 50000;
        int[] pageIds = new int[fileLength];
        for (int i = 0; i < fileLength; i++) {
            pageIds[i] = bMgr.fixNewPage();
        }

//        PageRequestReader prr = new PageRequestReader("src/main/resources/testTxt1.txt");
        PageRequestReader prr = new PageRequestReader("src/main/resources/data-5w-50w-zipf.txt");


        for (int i = 0; i < prr.getRequestNum(); i++) {
            bMgr.setOperation(prr.getOperation(i));
            bMgr.fixPage(prr.getPageId(i));
//            bMgr.printBuffer();
        }
        bMgr.writeDirtys();
        System.out.printf("%.3f%%\n", bMgr.getHitNum() / prr.getRequestNum() * 100);
        System.out.println(bMgr.getHitNum());
        System.out.printf("读磁盘次数：%d\n", bMgr.getReadDiskNum());
        System.out.printf("写磁盘次数：%d\n", bMgr.getWriteDiskNum());
    }

    @Test
    void testNewPlugin() {
        System.out.println("wzh");//我觉得这插件挺酷的
        /*
        哈哈哈
         */
    }


}
