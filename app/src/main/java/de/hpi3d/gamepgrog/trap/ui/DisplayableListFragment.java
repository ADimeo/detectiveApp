package de.hpi3d.gamepgrog.trap.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hpi3d.gamepgrog.trap.CustomApplication;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.datatypes.ClueDao;
import de.hpi3d.gamepgrog.trap.datatypes.Displayable;
import de.hpi3d.gamepgrog.trap.tasks.DaoSession;
import de.hpi3d.gamepgrog.trap.tasks.TaskDao;

public class DisplayableListFragment extends Fragment {

    public static final String KEY_WHAT_TO_DISPLAY = "key_display_type";
    public static final String DISPLAY_CLUES = "display_clues";
    public static final String DISPLAY_TASKS = "display_tasks";

    private String whatToDisplay = "";

    private final int numberOfColumns = 1;

    private ArrayList<Displayable> currentDisplayable;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DisplayableListFragment() {
        currentDisplayable = new ArrayList<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            whatToDisplay = getArguments().getString(KEY_WHAT_TO_DISPLAY);
        }

        DaoSession daoSession = ((CustomApplication) getActivity().getApplication()).getDaoSession();
        if (DISPLAY_CLUES.equals(whatToDisplay)) {
            ClueDao clueDao = daoSession.getClueDao();
            this.currentDisplayable = new ArrayList<>(clueDao.queryBuilder().list());
        } else if (DISPLAY_TASKS.equals(whatToDisplay)) {
            TaskDao taskDao = daoSession.getTaskDao();
            this.currentDisplayable = new ArrayList<>(taskDao.queryBuilder().list());
        } else {
            throw new IllegalArgumentException("Can only display clues or tasks");
        }

     /*
        // Uncomment for fast local test data

        Clue displayable = new Clue("PLACEHOLDER CLUE GENERATED IN DisplayableListFragment " + r.nextInt(100));
        daoSession.getClueDao().insert(displayable);
        Task task = new Task("PLACEHOLDER TASK GENERATED IN DisplayableListFragment " + r.nextInt(100));
        daoSession.getTaskDao().insert(task);

        Clue[] clues = (daoSession.getClueDao().queryBuilder().list()).toArray(new Clue[0]);
        Log.d("Temp", Arrays.toString(clues));
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_displayable_list, container, false);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (numberOfColumns <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
            }

            recyclerView.setAdapter(new DisplayableRecyclerViewAdapter(currentDisplayable, getActivity()));
        }

        Log.d("ON_CREATE_VIEW", Arrays.toString(currentDisplayable.toArray(new Displayable[0])));
        return view;
    }


}
