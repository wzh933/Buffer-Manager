package cs.adb.wzh.bufferManager;

import cs.adb.wzh.Storage.Disk;
import cs.adb.wzh.bucket.Bucket;
import cs.adb.wzh.Storage.Buffer;

import cs.adb.wzh.bufferControlBlocks.BCB;
import cs.adb.wzh.dataStorageManager.DSMgr;
import cs.adb.wzh.utils.pageRecordReader;

import java.io.IOException;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class BMgr {
    private final DSMgr dsMgr;
    private final int[] f2p;
    private final Bucket[] p2f;
    private BCB head;
    private BCB tail;
    private final BCB[] bcbTable;
    private final int bufSize;
    private final int freePageNum;
    //维护一个空闲页链表的头指针
    private final BCB freePageHead;
//    private BCB freePageTail;

    //对页面的读写操作(operation, pageId)
//    private final pageRecordReader pageRecords;


    public BMgr(Buffer bf, Disk disk) throws IOException {
        this.bufSize = bf.getBufSize();
        this.freePageNum = bufSize;

        this.dsMgr = new DSMgr(bf, disk);

//        this.pageRecords = new pageRecordReader(pageRequestsFilePath);

        this.bcbTable = new BCB[bufSize];
        this.f2p = new int[bufSize];
        this.p2f = new Bucket[bufSize];

        this.head = new BCB(-1);//增加一个frameId为-1的无效节点并在之后作为环形链表的尾结点
        this.freePageHead = this.head;//开始时空闲链表头指针指向缓存区头指针
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
        bcb.getPre().setNext(bcb.getNext());
        bcb.getNext().setPre(bcb.getPre());
        bcb.setPre(this.tail);
        bcb.setNext(this.head);
        this.head.setPre(bcb);
        this.tail.setNext(bcb);
        this.head = bcb;
    }


    /**
     * 文件和访问管理器将使用记录的record_id中的page_id调用这个页面
     * 该函数查看页面是否已经在缓冲区中
     * 如果是，则返回相应的frame_id
     * 如果页面尚未驻留在缓冲区中，则它会选择牺牲页面（如果需要），并加载所请求的页面。
     *
     * @param pageId:
     * @param protection:
     * @return int
     */
    public int fixPage(int pageId, int protection) throws Exception {
        if (this.p2f[this.hash(pageId)] == null) {//如果pageId对应的hash桶是空的则新建桶
            this.p2f[this.hash(pageId)] = new Bucket();
        }
        Bucket hashBucket = this.p2f[this.hash(pageId)];//找到pageId可能存放的hash桶
        BCB targetBCB = hashBucket.searchPage(pageId);//寻找hash桶中的页面
        if (targetBCB != null) {
            /*
            如果该页面存放在缓存区中
            那么将该页面置于循环链表的首部
             */
            this.move2Head(targetBCB);
            return targetBCB.getFrameId();
        }

        /*
        如果该页面不在缓存区中
        1、得到需要将要被置换(牺牲)的尾部页面victimBCB
        2、将victimBCB从其原来的pageId对应的hash桶中删除
        3、修改victimBCB原来的pageId为当前pageId
        4、将victimBCB放入当前pageId对应的hash桶中
        5、将victimBCB移到首部
         */
        BCB victimBCB = this.bcbTable[this.selectVictim()];
        this.p2f[this.hash(victimBCB.getPageId())].removeBCB(victimBCB);
        victimBCB.setPageId(pageId);
        hashBucket.appendBCB(victimBCB);
        this.move2Head(victimBCB);
        return victimBCB.getFrameId();
    }


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
     * NumFreeFrames函数查看缓冲区，并返回可用的缓冲区页数
     *
     * @return int
     */
    public int numFreeFrames() {
        return freePageNum;
    }

    public int selectVictim() {
        //LRU策略选择尾部结点作为victimBCB
        BCB victimBCB = this.tail.getPre();
        if (victimBCB.getDirty() == 1) {
            this.writeDirtys();
        }
        return victimBCB.getFrameId();
    }

    public int hash(int pageId) {
        return pageId % bufSize;
    }

    void removeBCB(int pageId) {

    }

    void removeLRUEle() {
        //将LRU链表里的队尾元素移至队首
        head.setPre(tail);
        tail.setNext(head);
        head = tail;
        tail = tail.getPre();
        head.setPre(null);
        tail.setNext(null);
    }

    void writeDirtys() {

    }


}
