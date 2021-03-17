package org.devio.hi.imooc.log;

public interface HiLogFormatter<T> {
    String format(T data);
}
