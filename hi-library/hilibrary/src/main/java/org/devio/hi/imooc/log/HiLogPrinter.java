package org.devio.hi.imooc.log;

public interface HiLogPrinter {
    void print(HiLogConfig config, int level, String tag, String printString);
}
