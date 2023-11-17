package cs.adb.wzh.dataStorageManager;

import cs.adb.wzh.Storage.Buffer;
import cs.adb.wzh.Storage.Disk;
import cs.adb.wzh.Storage.File;
import cs.adb.wzh.StorageForm.Frame;
import cs.adb.wzh.StorageForm.Page;
import cs.adb.wzh.bufferManager.BMgr;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class DSMgr {
    private final int maxPageNum;
    private int pageNum = 0;//开始时被固定的页面数位0
    private final int[] pages;
    private int curRecordId;
    private File curFile;
    private final Buffer bf;
    private final Disk disk;
    private int readDiskNum = 0;
    private int writeDiskNum = 0;

    public DSMgr(Buffer bf, Disk disk) {
        this.bf = bf;
        this.disk = disk;
        this.maxPageNum = disk.getDiskSize();
        /*
        pages是一个page与其useBit的索引表
        pages[pageId] = 0表示该页可用，1表示该页被固定
        开辟新内存空间之后所有值默认为0
         */
        this.pages = new int[this.maxPageNum];
    }

    public void openFile(String fileName) {
        this.curFile = new File(fileName);
    }

    public void closeFile() {
        this.curFile = null;
    }

    public Page getPage(int pageId) {
        return this.disk.getDisk()[pageId];
    }

    public void readPage(int pageId) {
        this.readDiskNum++;
    }


    public void writePage(int frameId, int pageId) {
        this.writeDiskNum++;
    }

    /**
     * 将文件指针从位置pos移动offset个偏移量
     *
     * @param offset:偏移量
     * @param pos:文件位置
     */
    public void seek(int offset, int pos) throws Exception {
        if (pos + offset >= curFile.getFileSize() || pos + offset < 0) {
            throw new Exception("文件访问越界！");
        }
        this.curRecordId = pos + offset;
    }

    public File getFile() {
        return curFile;
    }

    public void incNumPages() {
        pageNum += 1;
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

    public int getNumPages() {
        return pageNum;
    }

    public int getMaxPageNum() {
        return maxPageNum;
    }

    public int getReadDiskNum() {
        return readDiskNum;
    }

    public int getWriteDiskNum() {
        return writeDiskNum;
    }
}
