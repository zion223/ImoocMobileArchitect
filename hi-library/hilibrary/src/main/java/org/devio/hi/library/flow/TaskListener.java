package org.devio.hi.library.flow;

public interface TaskListener {

    void onStart(Task task);

    void onRunning(Task task);

    void onFinish(Task task);
}

