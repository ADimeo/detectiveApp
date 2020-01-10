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
import de.hpi3d.gamepgrog.trap.datatypes.ClueDao;
import de.hpi3d.gamepgrog.trap.datatypes.DaoSession;
import de.hpi3d.gamepgrog.trap.datatypes.Displayable;
import de.hpi3d.gamepgrog.trap.datatypes.TaskDao;


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
        Random r = new Random();

        Clue clue = new Clue("This is a hint! It's number is " + r.nextInt(100));
        clueDao.insert(clue);
*/
        // Remove once there's a way to get hints from server
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_displayable_list, container, false);


        // TODO take from local variable instead

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (numberOfColumns <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
            }
            recyclerView.setAdapter(new DisplayableRecyclerViewAdapter(currentDisplayable));
        }
        return view;
    }


}
