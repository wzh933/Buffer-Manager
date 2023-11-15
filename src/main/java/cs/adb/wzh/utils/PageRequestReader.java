package cs.adb.wzh.utils;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Wang Zihui
 * @date 2023/11/13
 **/
public class PageRequestReader {
    private final ArrayList<Integer> operations = new ArrayList<>();//0读1写
    private final ArrayList<Integer> pageIds = new ArrayList<>();//页号列表

    public PageRequestReader(String filePath) throws IOException {
        File fin = new File(filePath);
        FileInputStream fis = new FileInputStream(fin);

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        while ((line = br.readLine()) != null) {
            String[] str = line.split(",");
            operations.add(Integer.valueOf(str[0]));
            pageIds.add(Integer.valueOf(str[1]));
        }

        br.close();
        fis.close();

    }

    public int getOperation(int requestId) {
        return operations.get(requestId);
    }

    public int getPageId(int requestId) {
        return pageIds.get(requestId);
    }

    public int getRequestNum() {
        return pageIds.size();
    }
}
