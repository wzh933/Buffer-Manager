package cs.adb.wzh.Storage;

import cs.adb.wzh.StorageForm.Frame;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class Buffer {
    private final int bufSize;
    private Frame[] buf;

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
    }

}
