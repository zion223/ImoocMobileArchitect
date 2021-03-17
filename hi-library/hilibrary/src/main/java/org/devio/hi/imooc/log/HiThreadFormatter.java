package org.devio.hi.imooc.log;

public class HiThreadFormatter implements HiLogFormatter<Thread>{

    @Override
    public String format(Thread thread) {

        return "Thread- " + thread.getName() + "id- " + thread.getId();
    }
}
