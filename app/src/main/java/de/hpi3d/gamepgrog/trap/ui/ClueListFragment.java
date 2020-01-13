package de.hpi3d.gamepgrog.trap.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hpi3d.gamepgrog.trap.CustomApplication;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.ClueDao;
import de.hpi3d.gamepgrog.trap.tasks.DaoSession;


public class ClueListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    public static final String KEY__HINT_LIST = "hint_key_list";
    private ArrayList<Clue> currentClues;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClueListFragment() {
        currentClues = new ArrayList<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO Read arguments we are given
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


        DaoSession daoSession = ((CustomApplication) getActivity().getApplication()).getDaoSession();
        ClueDao clueDao = daoSession.getClueDao();
 /*  
        Random r = new Random();

        Clue clue = new Clue("This is a hint! It's number is " + r.nextInt(100));
        clueDao.insert(clue);
*/
        // Remove once there's a way to get hints from server


        ArrayList<Clue> clueList = new ArrayList<>(clueDao.queryBuilder().list());
        this.setClue(clueList);
    }


    public void setClue(ArrayList<Clue> newClues) {

        this.currentClues = newClues;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hint_list, container, false);


        // TODO take from local variable instead

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ClueRecyclerViewAdapter(currentClues));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
