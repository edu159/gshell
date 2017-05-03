package com.example.eduardo.gshell;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ryo on 14/04/17.
 */

public class RecyclerViewAdapter
        extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    private List<Host> items;
    private SparseBooleanArray selectedItems;

    RecyclerViewAdapter(List<Host> hostData){
        if (hostData == null){
            throw new IllegalArgumentException("hostData must not be null");
        }
        items = hostData;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_host_row_twoline,viewGroup,false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {

        Host host = items.get(position);
        viewHolder.alias.setText(String.valueOf(host._alias));
        viewHolder.address.setText(String.valueOf(host._address));

        viewHolder.itemView.setActivated(selectedItems.get(position,false));
    }


    @Override
    public int getItemCount(){
        return items.size();
    }
    public Host getHostItem(int position) { return items.get(position);}

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder{
        TextView alias;
        TextView address;

        public ListItemViewHolder(View itemView){
            super(itemView);
            alias = (TextView) itemView.findViewById(R.id.txt_alias);
            address = (TextView) itemView.findViewById(R.id.txt_address);

        }
    }
}
