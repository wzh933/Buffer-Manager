package cs.adb.wzh.bufferManager;

import cs.adb.wzh.Storage.Disk;
import cs.adb.wzh.bucket.Bucket;
import cs.adb.wzh.Storage.Buffer;

import cs.adb.wzh.bufferControlBlocks.BCB;
import cs.adb.wzh.dataStorageManager.DSMgr;
import cs.adb.wzh.utils.SwapMethod;

import java.io.IOException;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class BMgr {
    private final DSMgr dsMgr;
    private final Bucket[] p2f;
    /*
    有了BCB表之后就不需要f2p索引表了
    其中bcbTable[frameId] = BCB(frameId)
     */
    private final BCB[] bcbTable;
    private BCB head;
    private BCB tail;
    private final int bufSize;
    private int freePageNum;
    private final Buffer bf;
    private final Disk disk;

    private double hitNum = 0;
    private int operation;//0-读 1-写
//    private BCB freePageTail;

    //对页面的读写操作(operation, pageId)
//    private final pageRecordReader pageRecords;

    private BCB clockSentinel;//维护一个时钟哨兵
    private boolean useLRU = false;
    private boolean useCLOCK = false;
    private SwapMethod swapMethod;

    public BMgr(Buffer bf, Disk disk) throws IOException {
        this.bf = bf;
        this.disk = disk;
        this.bufSize = bf.getBufSize();
        this.freePageNum = bufSize;

        this.dsMgr = new DSMgr(bf, disk);
        this.dsMgr.openFile("data.dbf");

//        this.pageRecords = new pageRecordReader(pageRequestsFilePath);

        this.bcbTable = new BCB[bufSize];
        this.p2f = new Bucket[bufSize];

        this.head = new BCB(-1);//增加一个frameId为-1的无效节点并在之后作为环形链表的尾结点
        BCB p = this.head, q;
        //初始化帧缓存区BCB的双向循环链表
        for (int i = 0; i < this.bufSize; i++) {
            q = new BCB(i);
            p.setNext(q);//设置后继节点
            q.setPre(p);//设置前驱节点
            p = q;
            if (i == this.bufSize - 1) {
                this.tail = q;//设置缓存区尾指针
            }
            this.bcbTable[i] = p;//bcbTable中: bcbId = bcb.getFrameId()
        }
        //循环链表
        this.head.setPre(tail);
        this.tail.setNext(head);
        //让无效结点作为尾部结点
        this.head = this.head.getNext();
        this.tail = this.tail.getNext();
        //开始时时钟哨兵指向循环双向链表中最后一个结点
        this.clockSentinel = this.tail.getPre();
    }

    public BCB getHead() {
        return head;
    }

    public BCB getTail() {
        return tail;
    }

    public BCB[] getBcbTable() {
        return bcbTable;
    }


    public void move2Head(BCB bcb) {
        if (bcb == this.head || bcb == this.tail) {
            /*
            本就是首尾部结点则不做任何操作
            因为首部结点不用动
            尾部结点无效也不需要任何操作
             */
            return;
        }
        //这样写代码舒服多了
        bcb.getPre().setNext(bcb.getNext());
        bcb.getNext().setPre(bcb.getPre());
        bcb.setPre(this.tail);
        bcb.setNext(this.head);
        this.head.setPre(bcb);
        this.tail.setNext(bcb);
        this.head = bcb;
    }


/*
    /**
     * 文件和访问管理器将使用记录的record_id中的page_id调用这个页面
     * 该函数查看页面是否已经在缓冲区中
     * 如果是，则返回相应的frame_id
     * 如果页面尚未驻留在缓冲区中，则它会选择牺牲页面（如果需要），并加载所请求的页面。
     *
     * @param pageId:需要被固定于缓存区的页号
     * @return frameId:页面pageId被固定于缓存区的帧号
     * /
    public int fixPage(int pageId) throws Exception {
        int frameId = this.fixPageLRU(pageId);//可以切换不同的策略进行页面的置换
        this.bcbTable[frameId].setDirty(operation);//如果是写操作则将脏位设置为1(0-读 1-写)

        return frameId;
/*
        Page page = this.dsMgr.readPage(pageId);
        if (this.p2f[this.hash(pageId)] == null) {//如果pageId对应的hash桶是空的则新建桶
            this.p2f[this.hash(pageId)] = new Bucket();
        }
        Bucket hashBucket = this.p2f[this.hash(pageId)];//找到pageId可能存放的hash桶
        BCB targetBCB = hashBucket.searchPage(pageId);//寻找hash桶中的页面
        if (targetBCB != null) {
            /*
            如果该页面存放在缓存区中
            那么命中次数加一
            同时将该页面置于循环链表的首部
             * /
            this.hitNum++;
            this.move2Head(targetBCB);
            return targetBCB.getFrameId();
        }

        /*
        如果该页面不在缓存区中，则从磁盘读取该页并且执行如下的内存操作：
        a)该缓存区未满，则将该页面存放于缓存区的尾部并将其移动至首部
        b)该缓存区已满，则执行淘汰规则：
            1、得到需要将要被置换(牺牲)的尾部页面victimBCB和其帧号frameId
            2、将victimBCB从其原来的pageId对应的hash桶中删除
            3、修改victimBCB原来的pageId为当前pageId
            4、将victimBCB放入当前pageId对应的hash桶中
            5、将victimBCB移到首部
            6、如果该帧脏位为1，则将帧frameId中的内容写入页面pageId中
         * /
        this.dsMgr.readPage(pageId);
        int frameId;
        if (this.freePageNum > 0) {
            frameId = this.bufSize - this.freePageNum;
            BCB freeBCB = this.bcbTable[frameId];
            freeBCB.setPageId(pageId);
            this.move2Head(freeBCB);
            this.freePageNum--;
            this.p2f[this.hash(freeBCB.getPageId())].appendBCB(freeBCB);
        } else {
            frameId = this.selectVictim();
            if (this.bcbTable[frameId].getDirty() == 1) {
                this.dsMgr.writePage(frameId, this.bcbTable[frameId].getPageId());
            }
            BCB victimBCB = this.bcbTable[frameId];
            victimBCB.setPageId(pageId);
//            System.out.printf("frameId: %d, pageId: %d\n", victimBCB.getFrameId(), victimBCB.getPageId());
            hashBucket.appendBCB(victimBCB);
//            this.move2Head(victimBCB);

//            this.bf.getBuf()[frameId].setField(page.getField());
        }
        return frameId;
* /

    }
*/


    /**
     * 当插入、索引拆分或创建对象时需要一个新页面时，使用这个函数
     * 此函数将找到一个空页面，文件和访问管理器可以使用它来存储一些数据
     *
     * @return 该page被分配到缓存区中的frameId
     */
    public int fixNewPage() throws Exception {
        /*
        先在被固定的页面中搜索可用位为0的页面
        如果找到被固定页面中的可用页面pageId
        那么该页面pageId将被重用
        同时置该页面的使用位为1
         */
        for (int pageId = 0; pageId < dsMgr.getNumPages(); pageId++) {
            if (dsMgr.getUse(pageId) == 0) {
                dsMgr.setUse(pageId, 1);
                return pageId;
            }
        }
        /*
        否则被固定的页面计数器+=1
        并且从非固定页面中重新分配页面
        并且置该页面的使用位为1
        其中被分配的页面allocPageId为pageNum(pageId从0开始)
         */
        int allocPageId = dsMgr.getNumPages();
        if (allocPageId >= dsMgr.getMaxPageNum()) {
            throw new Exception("当前磁盘已满，无法分配新页面！");
        }
        dsMgr.setUse(allocPageId, 1);
        dsMgr.incNumPages();
        return allocPageId;
    }

    /**
     * 文件和访问管理器将使用记录的record_id中的page_id调用这个页面
     * 该函数查看页面是否已经在缓冲区中
     * 如果是，则返回相应的frame_id
     * 如果页面尚未驻留在缓冲区中，则它会选择牺牲页面（如果需要），并加载所请求的页面。
     *
     * @param pageId:需要被固定于缓存区的页号
     * @return frameId:页面pageId被固定于缓存区的帧号
     */
    public int fixPage(int pageId) throws Exception {
        if (this.p2f[this.hash(pageId)] == null) {//如果pageId对应的hash桶是空的则新建桶
            this.p2f[this.hash(pageId)] = new Bucket();
        }
        Bucket hashBucket = this.p2f[this.hash(pageId)];//找到pageId可能存放的hash桶
        BCB targetBCB = hashBucket.searchPage(pageId);//寻找hash桶中的页面
        if (targetBCB != null) {
            /*
            如果该页面存放在缓存区中
            那么命中次数加一
            如果采用LRU策略则将该页面置于循环链表的首部
             */
            this.hitNum++;
            targetBCB.setDirty(this.operation);//如果是写操作则将脏位设置为1(0-读 1-写)
            if (this.swapMethod == SwapMethod.LRU) {//如果使用LRU置换算法则将命中页面移动至首部
                this.move2Head(targetBCB);
            }
            targetBCB.setReferenced(1);
            return targetBCB.getFrameId();
        }

        /*
        如果该页面不在缓存区中，则从磁盘读取该页并且执行如下的内存操作：
        a)该缓存区未满，则将该页面存放于缓存区的尾部并将其移动至首部
        b)该缓存区已满，则执行淘汰规则：
            1、得到需要将要被置换(牺牲)的尾部页面victimBCB和其帧号frameId
            2、将victimBCB从其原来的pageId对应的hash桶中删除
            3、修改victimBCB原来的pageId为当前pageId
            4、将victimBCB放入当前pageId对应的hash桶中
            5、将victimBCB移到首部
            6、如果该帧脏位为1，则将帧frameId中的内容写入页面pageId中
         */
        if (this.operation == 0) {
            this.dsMgr.readPage(pageId);
        }
        int frameId;
        if (this.freePageNum > 0) {
            frameId = this.bufSize - this.freePageNum;
            BCB freeBCB = this.bcbTable[frameId];
            freeBCB.setPageId(pageId);
            this.move2Head(freeBCB);
            this.freePageNum--;
            this.p2f[this.hash(freeBCB.getPageId())].appendBCB(freeBCB);
        } else {
            frameId = this.selectVictim();
            if (this.bcbTable[frameId].getDirty() == 1) {
                this.dsMgr.writePage(frameId, this.bcbTable[frameId].getPageId());
            }
            BCB victimBCB = this.bcbTable[frameId];
            victimBCB.setPageId(pageId);
//            System.out.printf("frameId: %d, pageId: %d\n", victimBCB.getFrameId(), victimBCB.getPageId());
            hashBucket.appendBCB(victimBCB);
//            this.move2Head(victimBCB);

//            this.bf.getBuf()[frameId].setField(page.getField());
        }
        this.bcbTable[frameId].setDirty(operation);//如果是写操作则将脏位设置为1(0-读 1-写)
        return frameId;
    }

    /**
     * NumFreeFrames函数查看缓冲区，并返回可用的缓冲区页数
     *
     * @return int
     */
    public int numFreeFrames() {
        return freePageNum;
    }

    /**
     * @return 被淘汰的页面存放的帧号frameId
     */
    private int selectVictim() throws Exception {
        int victimFrame;
        switch (swapMethod) {
            case LRU -> victimFrame = removeLRUEle();
            case CLOCK -> victimFrame = removeCLOCKEle();
            default -> victimFrame = this.tail.getPre().getFrameId();
        }
        return victimFrame;
    }

    private int hash(int pageId) {
        return pageId % bufSize;
    }

    private void removeBCB(int pageId) throws Exception {
        Bucket hashBucket = this.p2f[this.hash(pageId)];
        if (hashBucket == null) {
            throw new Exception("哈希桶不存在，代码出错啦！");
        }
        if (this.p2f[this.hash(pageId)].searchPage(pageId) == null) {
            throw new Exception("找不到要删除的页，代码出错啦！");
        }

        for (Bucket curBucket = hashBucket; curBucket != null; curBucket = curBucket.getNext()) {
            for (int i = 0; i < curBucket.getBcbNum(); i++) {
                if (curBucket.getBcbList().get(i).getPageId() == pageId) {
                    curBucket.getBcbList().remove(i);
//                    System.out.println(curBucket);
                    for (Bucket curBucket1 = curBucket; curBucket1 != null; curBucket1 = curBucket1.getNext()) {
                        if (curBucket1.getNext() != null) {
                            //将下个桶中的首元素加入当前桶
                            curBucket1.getBcbList().add(curBucket1.getNext().getBcbList().get(0));
                            //删除下个桶的首元素
                            curBucket1.getNext().getBcbList().remove(0);
                            //如果下个桶空则删除桶
                            if (curBucket1.getNext().getBcbNum() == 0) {
                                curBucket1.setNext(null);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private int removeLRUEle() throws Exception {
        //LRU策略选择尾部结点作为victimBCB
        BCB victimBCB = this.tail.getPre();
        //从hash表中删除BCB并在之后建立新的索引
        this.removeBCB(victimBCB.getPageId());
        //将被淘汰结点放至首部并在之后重新设置其页号
        this.move2Head(victimBCB);
        return victimBCB.getFrameId();
    }

    private int removeCLOCKEle() {
        BCB curBCB = this.clockSentinel;
        for (; curBCB.getReferenced() != 0; curBCB = curBCB.getNext()) {
            if (curBCB.getFrameId() == -1) {//遇到无效节点则不进行任何操作
                continue;
            }
            curBCB.setReferenced(0);
        }
        this.clockSentinel = curBCB;
        this.clockSentinel.setReferenced(1);
        return this.clockSentinel.getFrameId();
    }


    public void writeDirtys() {
        /*
        这键盘突然好了
        我真是蚌埠住了
         */
        for (int frameId = 0; frameId < this.bufSize; frameId++) {
            if (this.bcbTable[frameId].getDirty() == 1) {
                this.dsMgr.writePage(frameId, this.bcbTable[frameId].getPageId());
            }
        }

    }

    public void printBuffer() {
        for (BCB p = this.head; p.getFrameId() != -1; p = p.getNext()) {
            System.out.printf("%d, ", p.getPageId());
        }
        System.out.println();
    }

    public void setUseLRU(boolean useLRU) {
        this.useLRU = useLRU;
    }

    public void setUseCLOCK(boolean useCLOCK) {
        this.useCLOCK = useCLOCK;
    }

    public void setSwapMethod(SwapMethod swapMethod) {
        this.swapMethod = swapMethod;
    }

    public double getHitNum() {
        return hitNum;
    }

    public void setOperation(int operation) {//0-读 1-写
        this.operation = operation;
    }

    public int getReadDiskNum() {
        return this.dsMgr.getReadDiskNum();
    }

    public int getWriteDiskNum() {
        return this.dsMgr.getWriteDiskNum();
    }


}
