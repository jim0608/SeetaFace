package com.mysafe.lib_base.base;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter {
    private List<T> list_data;
    protected Context context;

    protected void SetContainerList(List<T> list) {
        this.list_data = list;
        notifyDataSetChanged();
    }

    protected void SetContainerList_Range(List<T> list) {
        this.list_data = list;
        notifyItemRangeChanged(0, getItemCount());
    }


    public List<T> GetDataList() {
        return this.list_data == null ? new ArrayList<T>() : list_data;
    }

    abstract void AbOnBindViewHolder(RecyclerView.ViewHolder holder, int position);

    abstract RecyclerView.ViewHolder AbOnCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public int getItemCount() {
        return list_data == null ? 0 : list_data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AbOnBindViewHolder(holder, position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return AbOnCreateViewHolder(parent, viewType);
    }
}
