package de.hpi3d.gamepgrog.trap.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import de.hpi3d.gamepgrog.trap.BackendManagerIntentService;
import de.hpi3d.gamepgrog.trap.R;


public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Move to an appropiate place, prettify a bit. Or a lot, I don't know.
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button upButton = view.findViewById(R.id.button_temporary_telegram);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInitialTelegramMessage();

            }
        });

        Button debugUploadContacts = view.findViewById(R.id.button_debug_contacts);
        debugUploadContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).prepareDataTheft();
            }
        });
        return view;
    }

    private void sendInitialTelegramMessage() {
        final String BOT_URL = BackendManagerIntentService.getBotUrl(getContext());


        try {
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + BOT_URL));
            startActivity(telegram);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
