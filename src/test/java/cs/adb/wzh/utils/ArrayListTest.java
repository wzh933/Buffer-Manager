package cs.adb.wzh.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * @author Wang Zihui
 * @date 2023/11/13.
 **/
public class ArrayListTest {
    @Test
    void removeTest() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            arrayList.add(i);
        }
        arrayList.remove(0);
        for (Integer a : arrayList) {
            System.out.println(a);
        }
    }
}
