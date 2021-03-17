package org.devio.hi.imooc.log;

public abstract class HiLogConfig {

    // 控制台打印的最大长度
    static int MAX_LEN = 512;
    static HiStackTraceFormatter HI_STACK_TRACE_FORMATTER = new HiStackTraceFormatter();
    static HiThreadFormatter HI_THREAD_FORMATTER = new HiThreadFormatter();

    public String getGlobalTag() {
        return "HiLog";
    }

    public boolean enable() {
        return true;
    }

    public JsonParse injectJsonParse() {
        return null;
    }

    public boolean includeThread() {
        return true;
    }

    public int stackTraceDepth() {
        return 5;
    }

    public HiLogPrinter[] printers() {
        return null;
    }

    public interface JsonParse {
        String toJson(Object src);
    }
}
