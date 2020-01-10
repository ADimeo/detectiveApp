package de.hpi3d.gamepgrog.trap.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.datatypes.Displayable;


public class ClueRecyclerViewAdapter extends RecyclerView.Adapter<ClueRecyclerViewAdapter.ViewHolder> {

    private final List<Displayable> displayable;


    public ClueRecyclerViewAdapter(List<Displayable> items) {
        displayable = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_displayable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.clue = displayable.get(position);
        holder.clueView.setText(displayable.get(position).getDisplayString());
    }

    @Override
    public int getItemCount() {
        return displayable.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView clueView;
        public Displayable clue;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            clueView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + clueView.getText() + "'";
        }
    }
}
