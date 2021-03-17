package org.devio.hi.imooc.log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HiLogManager {

    private HiLogConfig config;

    private static HiLogManager instance;
    // 打印设备集合
    private List<HiLogPrinter> printers = new ArrayList<>();
    //私有构造方法
    private HiLogManager(HiLogConfig config, HiLogPrinter[] printers){
        this.config = config;
        this.printers.addAll(Arrays.asList(printers));
    }

    public static HiLogManager getInstance(){
        return instance;
    }

    public static void init(@NonNull HiLogConfig config, HiLogPrinter... printers){
        instance = new HiLogManager(config, printers);
    }

    public HiLogConfig getConfig() {
        return config;
    }

    public List<HiLogPrinter> getPrinters() {
        return printers;
    }
    public void addPrinter(HiLogPrinter printer){
        printers.add(printer);
    }

    public void setPrinters(List<HiLogPrinter> printers) {
        this.printers = printers;
    }

    public void removePrinter(){
        if(printers != null){
            printers.clear();
        }
    }
}
