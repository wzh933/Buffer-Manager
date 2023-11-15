package cs.adb.wzh.Storage;


import cs.adb.wzh.StorageForm.Page;

/**
 * @author Wang Zihui
 * @date 2023/11/14
 **/
public class Disk {
    private final int diskSize;
    private final Page[] disk;

    public int getDiskSize() {
        return diskSize;
    }

    public Page[] getDisk() {
        return disk;
    }


    public Disk() {
        final int DEF_BUF_SIZE = 65536;//256MB的磁盘
        this.diskSize = DEF_BUF_SIZE;
        this.disk = new Page[DEF_BUF_SIZE];
        //初始化磁盘空间
        for (int pageId = 0; pageId < this.diskSize; pageId++) {
            this.disk[pageId] = new Page();
        }
    }


}
