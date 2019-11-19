package de.hpi3d.gamepgrog.trap.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;


public class ClueRecyclerViewAdapter extends RecyclerView.Adapter<ClueRecyclerViewAdapter.ViewHolder> {

    private final List<Clue> clues;



    public ClueRecyclerViewAdapter(List<Clue> items) {
        clues = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_hint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.clue = clues.get(position);
        holder.clueView.setText(clues.get(position).getHintText());
    }

    @Override
    public int getItemCount() {
        return clues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView clueView;
        public Clue clue;

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
