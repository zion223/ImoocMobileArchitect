package org.devio.hi.imooc.log;

import android.app.Activity;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.devio.hi.library.R;

import java.util.ArrayList;
import java.util.List;

public class HiViewPrinter implements HiLogPrinter {

    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private HiViewPrinterProvider hiViewPrinterProvider;

    public HiViewPrinter(Activity activity){
        FrameLayout rootView = activity.findViewById(android.R.id.content);
        recyclerView = new RecyclerView(activity);
        adapter = new LogAdapter(LayoutInflater.from(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        hiViewPrinterProvider = new HiViewPrinterProvider(rootView, recyclerView);
    }



    public HiViewPrinterProvider getHiViewPrinterProvider() {
        return hiViewPrinterProvider;
    }

    @Override
    public void print(HiLogConfig config, int level, String tag, String printString) {
        adapter.addItem(new HiLogMo(System.currentTimeMillis(), level, tag, printString));
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }


    public static class LogAdapter extends RecyclerView.Adapter<LogViewHolder>{

        private LayoutInflater inflater;

        private List<HiLogMo> logs = new ArrayList<>();

        public LogAdapter(LayoutInflater inflater){
            this.inflater = inflater;
        }

        void addItem(HiLogMo logItem) {
            logs.add(logItem);
            notifyItemInserted(logs.size() - 1);
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = inflater.inflate(R.layout.hilog_item, parent, false);
            return new LogViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            HiLogMo hiLogMo = logs.get(position);
            holder.tag.setTextColor(Color.WHITE);
            holder.logInfo.setTextColor(Color.WHITE);
            holder.tag.setText(hiLogMo.getFlattened());
            holder.logInfo.setText(hiLogMo.log);
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }
    }

    private static class LogViewHolder extends RecyclerView.ViewHolder {
        private TextView tag;
        private TextView logInfo;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tag =  itemView.findViewById(R.id.tag);
            logInfo =  itemView.findViewById(R.id.message);
        }
    }
}
