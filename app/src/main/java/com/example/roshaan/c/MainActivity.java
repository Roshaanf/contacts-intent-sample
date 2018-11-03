package com.example.roshaan.c;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    int REQUEST_CODE_PICK_CONTACTS = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {

            Uri uriContact = data.getData();

            Cursor c = getContentResolver().query(uriContact, null, null, null, null);

            String lookupKey = null;
            if (c.moveToFirst()) {

                Log.i("Contact ", "Contacts ID: " + c.getString(c.getColumnIndex(ContactsContract.Contacts._ID)));
                Log.i("Contact ", "Contacts Name: " + c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                lookupKey = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

                if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0)
                    getContactNumber(lookupKey);

                getContactEmail(lookupKey);

                System.out.println("Contacts get both");
                getContactPhoneAndEmail(lookupKey);


                c.close();
            }


        }
    }

    private String getContactEmail(String lookupKey) {


        String selection = ContactsContract.Data.LOOKUP_KEY + "= ? AND " +
                ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";

        String selectionArgs[] = {lookupKey};

//        ContactsContract.Data is the table which contains all types of contact data
//        in this we have made our select query to get Email type of data from data table
        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        String email = null;

        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

            Log.i("Contact ", "Contacts Email: " + email);
        }

        cursor.close();

        return email;
    }


    private String getContactNumber(String lookupKey) {


        String selection = ContactsContract.Data.LOOKUP_KEY + "= ? AND " +
                ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";

        String selectionArgs[] = {lookupKey};

//        ContactsContract.Data is the table which contains all types of contact data
//        in this we have made our select query to get Phone type of data from data table
        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        String number = null;

        if (cursor.moveToFirst()) {
            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Log.i("Contact ", "Contacts Number: " + number);
        }

        cursor.close();

        return number;
    }



    /*   in this method cursor will always have two rows one for email and other for phone
     , if contact doesnot have an email then both rows will contain phone , so we cant get the
     event when contact does not have an email
*/
    private String getContactPhoneAndEmail(String lookupKey) {


        String selection = ContactsContract.Data.LOOKUP_KEY + "= ? AND " +
                ContactsContract.Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "','" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')";

        String selectionArgs[] = {lookupKey};

//        ContactsContract.Data is the table which contains all types of contact data
//        in this we have made our select query to get Email type of data and phone type of data
//        from data table so we will get two rows each for each type
        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        String number = null, email = null;

//        cursor will contain two rows one row for email type and other row for phone number type
        if (cursor.moveToFirst()) {

            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            Log.i("Contact ", "Contacts Email: " + email);
        }

        if (cursor.moveToLast()) {
            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Log.i("Contact ", "Contacts Number: " + number);
        }

        cursor.close();

        return number;
    }
}
