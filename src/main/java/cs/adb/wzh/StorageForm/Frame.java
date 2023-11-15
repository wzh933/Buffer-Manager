package cs.adb.wzh.StorageForm;

/**
 * @author Wang Zihui
 * @date 2023/11/12
 **/
public class Frame {
    private final int FRAME_SIZE = 4096;
    private char[] field;

    public int getFrameSize() {
        return FRAME_SIZE;
    }

    public void setField(char[] field) {
        this.field = field;
    }

    public char[] getField() {
        return field;
    }

    public Frame() {
        this.field = new char[this.FRAME_SIZE];
    }
}

