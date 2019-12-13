package de.hpi3d.gamepgrog.trap.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.api.BackendManagerIntentService;


public class MainFragment extends Fragment {

    private Button upButton;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        upButton = view.findViewById(R.id.button_temporary_telegram);
        upButton.setOnClickListener((View v) -> {
            sendInitialTelegramMessage();
        });


        Button debugUploadContacts = view.findViewById(R.id.button_debug_contacts);
        debugUploadContacts.setOnClickListener((View v) -> {
            ((MainActivity) getActivity()).prepareDataTheft();
        });

        Switch safetySwitch = view.findViewById(R.id.switch_safety);
        safetySwitch.setChecked(BackendManagerIntentService.isInSafetyMode(getContext()));

        safetySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BackendManagerIntentService.setSafetyMode(isChecked, getContext());
        });

        return view;


    }


    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        Intent testButtonStatus = new Intent(activity, BackendManagerIntentService.class);
        testButtonStatus.putExtra(BackendManagerIntentService.KEY_MANAGE_TYPE, BackendManagerIntentService.MANAGE_TELEGRAM_BUTTON_STATUS);

        activity.startService(testButtonStatus);


        boolean playerHasStartedConversation = BackendManagerIntentService.getHasPlayerStartedConversation(getContext());
        upButton.setEnabled(!playerHasStartedConversation);
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
