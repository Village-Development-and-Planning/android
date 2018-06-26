package org.ptracking.vdp.views.fragments.options.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Option;

import java.util.List;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListVH> {
    private List<Option> optionList;

    public ListAdapter(List<Option> optionList) {
        this.optionList = optionList;
    }

    @Override
    public ListAdapter.ListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListVH(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ListAdapter.ListVH holder, int position) {
        holder.textView.setText(optionList.get(position).getTextString());
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    static class ListVH extends RecyclerView.ViewHolder {
        TextView textView;

        ListVH(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
