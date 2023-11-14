package cs.adb.wzh.dataStorageManager;

import cs.adb.wzh.Storage.Disk;

import java.util.Arrays;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class DSMgr {
    private final int maxPageNum;
    private int pageNum = 0;//开始时被固定的页面数位0
    private final int[] pages;

    public DSMgr(Disk disk) {
        this.maxPageNum = disk.getDiskSize();
        /*
        pages是一个page与其useBit的索引表
        pages[pageId] = 0表示该页可用，1表示该页被固定
        开辟新内存空间之后所有值默认为0
         */
        this.pages = new int[this.maxPageNum];
    }


    public void setUse(int pageId, int useBit) {
        /*
        useBit:
        0:该页可用
        1:该页被固定
         */
        this.pages[pageId] = useBit;
    }

    public int getUse(int pageId) {
        return this.pages[pageId];
    }

    public void incNumPages() {
        pageNum += 1;
    }

    public int getNumPages() {
        return pageNum;
    }

    public int getMaxPageNum() {
        return maxPageNum;
    }
}
