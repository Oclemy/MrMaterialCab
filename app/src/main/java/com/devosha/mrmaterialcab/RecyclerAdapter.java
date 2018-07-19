package com.devosha.mrmaterialcab;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * RecyclerView Adapter class. Will implement OnClickListener and OnLongClickListener from View class.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MainViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    /*
    Interface to hold our Callback method signatures
     */
    interface Callback {
        void onItemClicked(int index, boolean longClick);
        void onIconClicked(int index);
    }

    /*
    Instance Fields
     */
    private ArrayList<String> itemsList;
    private ArrayList<Integer> selectedList;
    private RecyclerAdapter.Callback callback;

    /*
    Adapter constructor will receive Callback instance
     */
    RecyclerAdapter(RecyclerAdapter.Callback callback) {
        this.callback = callback;
        itemsList = new ArrayList<>();
        selectedList = new ArrayList<>();
    }

    /*
    Method to receive items to populate our RecyclerView
     */
    void add(String item) {
        itemsList.add(item);
        notifyItemInserted(itemsList.size() - 1);
    }

    /*
    Toggle recyclerview item's selection state.
     */
    void toggleSelected(int index) {
        final boolean newState = !selectedList.contains(index);
        if (newState) {
            selectedList.add(index);
        } else {
            selectedList.remove((Integer) index);
        }
        notifyItemChanged(index);
    }

    /*
    Restore Adapter state
     */
    @SuppressWarnings("unchecked")
    void restoreState(Bundle in) {
        itemsList = (ArrayList<String>) in.getSerializable("[main_adapter_items]");
        selectedList = (ArrayList<Integer>) in.getSerializable("[main_adapter_selected]");
        notifyDataSetChanged();
    }

    /*
    Save adapter state
     */
    void saveState(Bundle out) {
        out.putSerializable("[main_adapter_items]", itemsList);
        out.putSerializable("[main_adapter_selected]", selectedList);
    }

    /*
    clear selected items.
     */
    void clearSelected() {
        selectedList.clear();
        notifyDataSetChanged();
    }

    /*
    Inflate listitem_main.xml and return a ViewHolder instance
     */
    @Override
    public RecyclerAdapter.MainViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.listitem_main, viewGroup, false);
        return new RecyclerAdapter.MainViewHolder(view);
    }

    /*
    - Set Activated status
    - Bind data to views
    - Set click listeners
     */
    @Override
    public void onBindViewHolder(RecyclerAdapter.MainViewHolder mainViewHolder, int i) {
        mainViewHolder.view.setActivated(selectedList.contains(i));
        mainViewHolder.view.setTag("item:" + i);
        mainViewHolder.view.setOnClickListener(this);
        mainViewHolder.view.setOnLongClickListener(this);

        mainViewHolder.icon.setTag("icon:" + i);
        mainViewHolder.icon.setOnClickListener(this);

        mainViewHolder.title.setText(itemsList.get(i));
    }

    /*
    Get adapter items total count
     */
    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    /*
    get selected items count
     */
    int getSelectedCount() {
        return selectedList.size();
    }

    /*
    Get a single item
     */
    String getItem(int index) {
        return itemsList.get(index);
    }

    /*
    when a recyclerview item is clicked
     */
    @Override
    public void onClick(View v) {
        String[] tag = ((String) v.getTag()).split(":");
        int index = Integer.parseInt(tag[1]);
        if (callback != null) {
            if (tag[0].equals("icon")) {
                callback.onIconClicked(index);
            } else {
                callback.onItemClicked(index, false);
            }
        }
    }

    /*
    recyclerview item long clicked
     */
    @Override
    public boolean onLongClick(View v) {
        String[] tag = ((String) v.getTag()).split(":");
        int index = Integer.parseInt(tag[1]);
        if (callback != null) {
            callback.onItemClicked(index, true);
        }
        return false;
    }

    /*
    Our ViewHolder class. Will hold a single recyclerview item widgets.
     */
    static class MainViewHolder extends RecyclerView.ViewHolder {

        final View view;
        final TextView title;
        final View icon;

        MainViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
