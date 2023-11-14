package cs.adb.wzh.bucket;

import cs.adb.wzh.bufferControlBlocks.BCB;

import java.util.ArrayList;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class Bucket {
    private final int bucketSize = 4;
    private final ArrayList<BCB> bcbList;
    private Bucket next;

    public Bucket() {
        bcbList = new ArrayList<>();
    }

    public boolean isFull() {
        return bcbList.size() == bucketSize;
    }

    /**
     * @param bcb:将要加入桶的BCB块
     */
    public void appendBCB(BCB bcb) {
        Bucket curBucket = this;//从当前第一个桶开始遍历
        while (curBucket.isFull()) {//当前桶非空时退出循环
            if (curBucket.getNext() == null) {//当前桶刚满，还没创建溢出块时
                curBucket.setNext(new Bucket());//创建溢出块
            }
            curBucket = curBucket.getNext();//遍历下个桶
        }
        curBucket.getBcbList().add(bcb);//将BCB控制块放入当前桶中
    }

    public void removeBCB(BCB bcb) throws Exception {
        if (this.searchPage(bcb.getPageId()) == null) {
            throw new Exception("找不到要删除的页，代码出错啦！");
        }

        for (Bucket curBucket = this; curBucket != null; curBucket = curBucket.getNext()) {
            for (int i = 0; i < curBucket.getBcbNum(); i++) {
                if (curBucket.getBcbList().get(i) == bcb) {
                    curBucket.getBcbList().remove(i);
                    for (Bucket nextBucket = curBucket; nextBucket.getNext() != null; nextBucket = nextBucket.getNext()) {
                        //将下个桶中的首元素加入当前桶
                        nextBucket.getBcbList().add(nextBucket.getNext().getBcbList().get(0));
                        //删除下个桶的首元素
                        nextBucket.getNext().getBcbList().remove(0);
                        //如果下个桶空则删除桶
                        if (nextBucket.getNext().getBcbNum() == 0) {
                            nextBucket.setNext(null);
                        }
                    }
                    break;
                }
            }
        }
    }


    /**
     * @param pageId:要查找的pageId
     * @return pageId所在的BCB块
     */
    public BCB searchPage(int pageId) {
        for (BCB bcb : this.bcbList) {
            if (bcb.getPageId() == pageId) {
                return bcb;
            }
        }
        if (this.next != null) {//溢出块非空则继续搜索溢出块
            return this.next.searchPage(pageId);
        } else {//否则返回空
            return null;
        }
    }

    public int getBucketSize() {
        return bucketSize;
    }

    public int getBcbNum() {
        return bcbList.size();
    }


    public ArrayList<BCB> getBcbList() {
        return bcbList;
    }

    public Bucket getNext() {
        return next;
    }

    public void setNext(Bucket next) {
        this.next = next;
    }

    public void printBucket() {
        for (Bucket curBucket = this; curBucket != null; curBucket = curBucket.getNext()) {
            for (BCB bcb : curBucket.getBcbList()) {
                System.out.println(bcb.getPageId());
            }
            System.out.println();
        }
    }
}
