package org.devio.hi.imooc.log;

import android.util.Log;

import static org.devio.hi.imooc.log.HiLogConfig.MAX_LEN;

/**
 * 控制台打印
 */
public class HiConsolePrinter implements HiLogPrinter {

    @Override
    public void print(HiLogConfig config, int level, String tag, String printString) {
        int len = printString.length();
        // 判断打印最大长度
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                Log.println(level, tag, printString.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            if (index != len) {
                Log.println(level, tag, printString.substring(index, len));
            }
        } else {
            Log.println(level, tag, printString);
        }
    }
}
