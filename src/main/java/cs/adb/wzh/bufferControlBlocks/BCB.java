package cs.adb.wzh.bufferControlBlocks;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class BCB {
    private int pageId;
    private final int frameId;
    private int latch;
    private int count;
    private int dirty = 0;
    private BCB next;
    private BCB pre;

    /**
     * BCB块的id对应其在缓存区中的frameId
     *
     * @param frameId:缓存区页号
     */
    public BCB(int frameId) {
        this.frameId = frameId;
    }


    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public void setNext(BCB next) {
        this.next = next;
    }

    public void setPre(BCB pre) {
        this.pre = pre;
    }

    public void setDirty(int dirty) {
        this.dirty = dirty;
    }


    public BCB getNext() {
        return next;
    }


    public BCB getPre() {
        return pre;
    }

    public int getPageId() {
        return pageId;
    }

    public int getFrameId() {
        return frameId;
    }

    public int getDirty() {
        return dirty;
    }


}
