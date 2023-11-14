package cs.adb.wzh.Storage;


import cs.adb.wzh.StorageForm.Page;

/**
 * @author Wang Zihui
 * @date 2023/11/14
 **/
public class Disk {
    private final int diskSize;
    private Page[] disk;

    public int getDiskSize() {
        return diskSize;
    }

    public Page[] getDisk() {
        return disk;
    }


    public Disk() {
        final int DEF_BUF_SIZE = 50000;
        this.diskSize = DEF_BUF_SIZE;
        this.disk = new Page[DEF_BUF_SIZE];
    }


}
