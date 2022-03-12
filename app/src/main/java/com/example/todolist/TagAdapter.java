package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private static ArrayList<Tag> tags;
    private static TagAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Tag tag);
    }

    public void setOnItemClickListener(TagAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public TagAdapter(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public TagAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View Tagview = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item , parent , false);
        return new TagAdapter.TagViewHolder(Tagview, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag currentTag = tags.get(position);
        holder.name.setText(currentTag.getName());
        holder.icon.setImageResource(
                holder.name.getResources().getIdentifier(currentTag.getName(), "drawable" , holder.name.getContext().getPackageName()));

    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {

       public TextView name;
       public ImageView icon;

        public TagViewHolder(@NonNull View itemView, TagAdapter.OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            icon = itemView.findViewById(R.id.iv_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mListener != null && position  != RecyclerView.NO_POSITION)
                        mListener.onItemClick(tags.get(position));
                }
            });

        }
    }
}
