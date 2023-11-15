package cs.adb.wzh.StorageForm;

/**
 * @author Wang Zihui
 * @date 2023/11/14
 **/
public class Page {
    private final int PAGE_SIZE = 4096;
    private char[] field;

    public int getFrameSize() {
        return PAGE_SIZE;
    }

    public void setField(char[] field) {
        this.field = field;
    }

    public char[] getField() {
        return field;
    }

    public Page() {
        this.field = new char[this.PAGE_SIZE];
    }
}
