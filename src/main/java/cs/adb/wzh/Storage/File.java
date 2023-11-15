package cs.adb.wzh.Storage;

import cs.adb.wzh.StorageForm.Page;

/**
 * @author Wang Zihui
 * @date 2023/11/15
 **/
public class File {
    private final int fileSize;
    private final Page[] records;

    public int getFileSize() {
        return fileSize;
    }

    public Page getFileRecord(int recordId) {
        return records[recordId];
    }


    public File(String filename) {
        final int DEF_FILE_SIZE = 50000;//50000个页面的文件
        this.fileSize = DEF_FILE_SIZE;
        this.records = new Page[DEF_FILE_SIZE];
        //假设可以读取这个文件，实际上只是初始化文件空间
        for (int recordId = 0; recordId < this.fileSize; recordId++) {
            this.records[recordId] = new Page();
        }
    }


}
