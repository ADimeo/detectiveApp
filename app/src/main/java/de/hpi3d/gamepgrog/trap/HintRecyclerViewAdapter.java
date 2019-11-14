package de.hpi3d.gamepgrog.trap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hpi3d.gamepgrog.trap.datatypes.Hint;


public class HintRecyclerViewAdapter extends RecyclerView.Adapter<HintRecyclerViewAdapter.ViewHolder> {

    private final List<Hint> hints;



    public HintRecyclerViewAdapter(List<Hint> items) {
        hints = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_hint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.hint = hints.get(position);
        holder.hintView.setText(hints.get(position).getHintText());
    }

    @Override
    public int getItemCount() {
        return hints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView hintView;
        public Hint hint;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            hintView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + hintView.getText() + "'";
        }
    }
}
