package de.hpi3d.gamepgrog.trap.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;


public class MainFragment extends Fragment {

    private static final String TAG = "MAIN_FRAGMENT";

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


        Button debugUploadContacts = view.findViewById(R.id.button_debug_steal);
        debugUploadContacts.setOnClickListener(v -> {
            debugInitialiseSteal();
        });

        Switch safetySwitch = view.findViewById(R.id.switch_safety);
        safetySwitch.setChecked(StorageManager.with(getActivity()).safetyMode.get());

        safetySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            StorageManager.with(getActivity()).safetyMode.set(isChecked);
        });

        return view;
    }

    private void debugInitialiseSteal() {
        ArrayList<Contact> contacts = DataStealer.takeContactData(getContext());
        String debugString = "";
        for (Contact contact : contacts) {

            String firstPhoneNumber;
            try {
                firstPhoneNumber = contact.getPhoneNumbers().get(0);
            } catch (IndexOutOfBoundsException e) {
                firstPhoneNumber = "";
            }

            String contactString = contact.getDisplayNamePrimary() + " ||| "
                    + contact.getBirthday() + " || "
                    + firstPhoneNumber + "|"
                    + contact.getHomeAddress() + "\n";


            debugString = debugString + contactString;
        }
        Log.d("CONTACTS", debugString);

        //  DataStealer.takeMessageData(getContext());
    }


    @Override
    public void onResume() {
        super.onResume();
        // TODO use firebase and remove API call
        //  FragmentActivity activity = getActivity();
        //  Intent testButtonStatus = new Intent(activity, StorageManager.class);
        //  testButtonStatus.putExtra(StorageManager.KEY_MANAGE_TYPE, StorageManager.MANAGE_TELEGRAM_BUTTON_STATUS);

        // activity.startService(testButtonStatus);

        boolean playerHasStartedConversation = StorageManager.with(getActivity()).conversationStarted.get();
        Log.d(TAG, "has player started conversation: " + playerHasStartedConversation);
        upButton.setEnabled(!playerHasStartedConversation);
    }

    private void sendInitialTelegramMessage() {
        String botUrl = StorageManager.with(getActivity()).botUrl.get();
        Log.d(TAG, "Sending Telegram Message with url: " + botUrl);
        try {
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + botUrl));
            startActivity(telegram);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
