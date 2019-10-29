package de.hpi3d.gamepgrog.trap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {



    public MainFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Move to an appropiate place, prettify a bit. Or a lot, I don't know.
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button upButton = (Button) view.findViewById(R.id.button_temporary_telegram);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInitialTelegramMessage();

            }
        });
        return view;
    }

    private void sendInitialTelegramMessage(){
        // TODO: Read User ID, fix temporary data

        String playerToken = BackendManagerIntentService.getPlayerId(getContext());


        final String TELEGRAM_BASE_URL = "https://telegram.me/";
        final String BOT_URL = "/MicroTransactionBot"; // TODO: Add name of our bot
        final String USER_TOKEN_URL = "?start=" + playerToken;


        try {
            Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse(TELEGRAM_BASE_URL + BOT_URL + USER_TOKEN_URL));
            startActivity(telegram);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
