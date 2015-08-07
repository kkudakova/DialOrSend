package com.example.kskhom.dialorsend;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EnterActivity extends ActionBarActivity {
    private Button btnSend;
    private EditText etext;
    private static final String ENTERED_TEXT = "ENTTEXT";
    private static final int DIAL = 0;
    private static final int MAIL = 1;
    private static final int UNKNOWN = 2;
    private String s = "";
    private int status = UNKNOWN;

    private static final Pattern mail_pattern = Pattern.compile
            ("[a-zA-Z]{1}[a-zA-Z\\d\\u002E\\u005F]+@([a-zA-Z]+\\u002E){1,2}((net)|(com)|(org)|(ru))");
    private static final Pattern phone_pattern = Pattern.compile
            ("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        btnSend = (Button) this.findViewById(R.id.send_button);
        etext = (EditText) this.findViewById(R.id.enter_text);
        if (savedInstanceState != null)
            if (savedInstanceState.getString(ENTERED_TEXT) != null) {
                etext.setText(savedInstanceState.getString(ENTERED_TEXT));
            }

        etext.addTextChangedListener(new TextWatcher() {
                                         @Override
                                         public void afterTextChanged(Editable e) {
                                             if (e.length() > 0) {
                                                 s = e.toString();
                                                 switch (parse(s)) {
                                                     case DIAL:
                                                         status = DIAL;
                                                         btnSend.setText(R.string.make_call);
                                                         break;
                                                     case MAIL:
                                                         status = MAIL;
                                                         btnSend.setText(R.string.send_mail);
                                                         break;
                                                     case UNKNOWN:
                                                         status = UNKNOWN;
                                                         btnSend.setText(R.string.unknown_status);
                                                 }

                                             }
                                         }

                                         @Override
                                         public void onTextChanged(CharSequence s, int start, int before, int count) {

                                             //Your query to fetch Data
                                         }

                                         @Override
                                         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                         }

                                         private int parse(String s) {
                                             Matcher matcher = mail_pattern.matcher(s);
                                             if (matcher.matches()) {
                                                 return MAIL;
                                             }
                                             matcher = phone_pattern.matcher(s);
                                             if (matcher.matches()) {
                                                 return DIAL;
                                             }
                                             return UNKNOWN;
                                         }
                                     }

        );

        btnSend.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           switch (status) {
                                               case DIAL:
                                                   try {
                                                       Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                       callIntent.setData(Uri.parse("tel:"+s));
                                                       startActivity(callIntent);
                                                   } catch (ActivityNotFoundException activityException) {
                                                       Log.e("DialOrSend", "Call failed", activityException);
                                                   }
                                                   break;
                                               case MAIL:
                                                   try {
                                                       Intent mailIntent = new Intent(Intent.ACTION_SEND);
                                                       mailIntent.setData(Uri.parse("mailto:"));
                                                       mailIntent.setType("message/rfc822");
                                                       mailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{s});
                                                       startActivity(mailIntent);
                                                   } catch (ActivityNotFoundException activityException) {
                                                       Log.e("DialOrSend", "Mail failed", activityException);
                                                   }
                                                   break;
                                               case UNKNOWN:
                                                   Toast toast = Toast.makeText(getApplicationContext(),
                                                           R.string.unknown_status,
                                                           Toast.LENGTH_SHORT);
                                                   toast.setGravity(Gravity.CENTER, 0, 0);
                                                   toast.show();
                                           }
                                       }
                                   }

        );
    }
}
