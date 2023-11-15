package cs.adb.wzh.Storage;

import cs.adb.wzh.StorageForm.Frame;
import cs.adb.wzh.StorageForm.Page;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class Buffer {
    private final int bufSize;
    private final Frame[] buf;

    public int getBufSize() {
        return bufSize;
    }

    public Frame[] getBuf() {
        return buf;
    }


    public Buffer() {
        final int DEF_BUF_SIZE = 1024;
        this.bufSize = DEF_BUF_SIZE;
        this.buf = new Frame[DEF_BUF_SIZE];
        //初始化帧缓存区
        for (int i = 0; i < this.bufSize; i++) {
            this.buf[i] = new Frame();
        }
    }

    /**
     * @param bufSize:用户自定义的缓存区大小
     */
    public Buffer(int bufSize) throws Exception {
        if (bufSize <= 0) {
            throw new Exception("缓存区大小不可以为非正数！");
        }
        this.bufSize = bufSize;
        this.buf = new Frame[bufSize];
        //初始化帧缓存区
        for (int i = 0; i < this.bufSize; i++) {
            this.buf[i] = new Frame();
        }
    }

    public Frame readFrame(int frameId) {
        return buf[frameId];
    }

    public void writeFrame(Page page, int frameId) {
        //先不进行任何操作
    }

}
