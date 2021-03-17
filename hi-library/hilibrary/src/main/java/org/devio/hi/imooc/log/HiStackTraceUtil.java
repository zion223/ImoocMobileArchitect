package org.devio.hi.imooc.log;

public class HiStackTraceUtil {

    public static StackTraceElement[] getCroppedRealStackTrace(StackTraceElement[] stackTrace, String ignorePackage, int maxDepth) {
        return cropStackTrace(getRealStackTrace(stackTrace, ignorePackage), maxDepth);
    }


    // 去除忽略的包名
    public static StackTraceElement[] getRealStackTrace(StackTraceElement[] callStack, String ignorePackage) {
        int ignoreDepth = 0;
        int allDepth = callStack.length;
        String className;
        for (int i = allDepth - 1; i >= 0; i--) {
            className = callStack[i].getClassName();
            if (ignorePackage != null && className.startsWith(ignorePackage)) {
                ignoreDepth = i + 1;
                break;
            }
        }
        int realDepth = allDepth - ignoreDepth;
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(callStack, ignoreDepth, realStack, 0, realDepth);

        return realStack;
    }

    private static StackTraceElement[] cropStackTrace(StackTraceElement[] callStack, int maxDepth) {
        int realDepth = callStack.length;
        if (maxDepth > 0) {
            realDepth = Math.min(realDepth, maxDepth);
        }
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(callStack, 0, realStack, 0, realDepth);
        return realStack;
    }


}
