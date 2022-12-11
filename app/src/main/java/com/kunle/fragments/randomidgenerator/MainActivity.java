package com.kunle.fragments.randomidgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kunle.fragments.randomidgenerator.databinding.ActivityMainBinding;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final String[] full_character_list = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final String[] number_character_list = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final String[] lowercase_character_list = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m"};
    private final String[] uppercase_character_list = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M"};
    private ActivityMainBinding bind;
    private int char_list_count;
    String[] selected_id_array;
    private String toEmailAddress;
    private String emailSubject;
    private String emailNote;
    private boolean anyCheckBoxChecked;
    private String filename;
    private File textFile;
    private int count = 0;
    private HashSet<String> uniqueIDChecker = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setLayoutText();
        setOnClickListeners();
    }

    private void setLayoutText() {
        //create a very specifically formatted text for the Main UI page
        SpannableString first_part = new SpannableString("ID Characteristics");
        SpannableString second_part = new SpannableString("(must select at least 1)");
        second_part.setSpan(new AbsoluteSizeSpan(18, true),
                0, first_part.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        second_part.setSpan(new AbsoluteSizeSpan(15, true),
                0, second_part.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        CharSequence finalText = TextUtils.concat(first_part, " ", second_part);
        bind.checkBoxLabel.setText(finalText);

        //auto-create email file name to avoid user error (name based on unique data and time)
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd--HHmmss", Locale.US);
            Date now = new Date();
            filename = "GeneratedIDs-" + formatter.format(now) + ".txt";
        } catch (Exception e) {
            e.printStackTrace();
        }

        bind.fileName.setText(filename);
    }

    private void setOnClickListeners() {
        bind.idsNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //on-change listener to format numbers while user is typing them in
            @Override
            public void afterTextChanged(Editable editable) {
                bind.idsNum.removeTextChangedListener(this);
                if (!editable.toString().equals("")) {
                    try {
                        String originalString = editable.toString();
                        int temp_num_storage;

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }
                        temp_num_storage = Integer.parseInt(originalString);

                        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        df.applyPattern("#,###,###");
                        bind.idsNum.setText(df.format(temp_num_storage));
                        bind.idsNum.setSelection(bind.idsNum.getText().length());
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Number is too large!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                bind.idsNum.addTextChangedListener(this);
            }
        });

        bind.characterNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //on-change listener to format numbers while user is typing them in
            @Override
            public void afterTextChanged(Editable editable) {
                bind.characterNum.removeTextChangedListener(this);
                if (!editable.toString().equals("")) {
                    try {
                        String originalString = editable.toString();
                        int temp_num_storage;

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }
                        temp_num_storage = Integer.parseInt(originalString);

                        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        df.applyPattern("#,###,###");
                        bind.characterNum.setText(df.format(temp_num_storage));
                        bind.characterNum.setSelection(bind.characterNum.getText().length());
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Number is too large!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                bind.characterNum.addTextChangedListener(this);
            }
        });


        bind.emailInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailOptions();
            }
        });

        bind.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //does a few validation checks before beginning to create IDs
                String idsNum_text = String.valueOf(bind.idsNum.getText()).trim();
                String characterNum_text = String.valueOf(bind.characterNum.getText()).trim();

                if (idsNum_text.contains(",")) {
                    idsNum_text = idsNum_text.replaceAll(",", "");
                }

                if (characterNum_text.contains(",")) {
                    characterNum_text = characterNum_text.replaceAll(",", "");
                }

                int numCharactersPerID;
                int numOfIDs;
                try {
                    numCharactersPerID = Integer.parseInt(characterNum_text);
                    numOfIDs = Integer.parseInt(idsNum_text);

                    if ((numOfIDs > 0) && (numCharactersPerID > 0)) {
                        createIDs();
                    } else {
                        Toast.makeText(MainActivity.this, "Both # of IDs and # of characters per ID need to be greater than 0",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Please enter all required numeric values before creating IDs",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //next 3 on-click listeners meant to enable "Create IDs" button ONLY if
        //one of 3 checkboxes is selected. User must choose 1 to actually create IDs
        bind.numbersCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anyCheckBoxChecked = bind.numbersCheckBox.isChecked()
                        || bind.lowercaseCheckBox.isChecked()
                        || bind.uppercaseCheckBox.isChecked();
                bind.createButton.setEnabled(anyCheckBoxChecked);
            }
        });

        bind.lowercaseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anyCheckBoxChecked = bind.numbersCheckBox.isChecked()
                        || bind.lowercaseCheckBox.isChecked()
                        || bind.uppercaseCheckBox.isChecked();
                bind.createButton.setEnabled(anyCheckBoxChecked);
            }
        });

        bind.uppercaseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anyCheckBoxChecked = bind.numbersCheckBox.isChecked()
                        || bind.lowercaseCheckBox.isChecked()
                        || bind.uppercaseCheckBox.isChecked();
                bind.createButton.setEnabled(anyCheckBoxChecked);
            }
        });
    }

    private void showEmailOptions() {
        //creates and inflates email options.
        //These values will populate email "To:","Subject", and email body when email is created
        LayoutInflater inflater = getLayoutInflater();
        View emailView = inflater.inflate(R.layout.email_info, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        int dialog_button_color = ContextCompat.getColor(this, R.color.dialog_button_color);

        TextInputEditText toEmail = emailView.findViewById(R.id.toEmailAddress);
        TextInputEditText subject = emailView.findViewById(R.id.subject);
        TextInputEditText note = emailView.findViewById(R.id.note);

        //repopulates email information if already entered and saved previously
        toEmail.setText((toEmailAddress != null ? toEmailAddress : null));
        subject.setText((emailSubject != null ? emailSubject : null));
        note.setText((emailNote != null ? emailNote : null));

        builder.setView(emailView);
        builder.setTitle("Enter Email Information")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toEmailAddress = String.valueOf(toEmail.getText()).trim();
                        emailSubject = String.valueOf(subject.getText()).trim();
                        emailNote = String.valueOf(note.getText()).trim();
                        Toast.makeText(MainActivity.this, "Email information received and saved",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(dialog_button_color);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(dialog_button_color);
    }

    private void createIDs() {
        File externalFilesDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        textFile = new File(externalFilesDirectory, filename);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        textFile.deleteOnExit();

        boolean numbers = bind.numbersCheckBox.isChecked();
        boolean lowercase = bind.lowercaseCheckBox.isChecked();
        boolean uppercase = bind.uppercaseCheckBox.isChecked();

        //logic to see which character lists program should use to create IDs
        if (numbers && !lowercase && !uppercase) {
            selected_id_array = number_character_list;
        } else if (!numbers && lowercase && !uppercase) {
            selected_id_array = lowercase_character_list;
        } else if (!numbers && !lowercase && uppercase) {
            selected_id_array = uppercase_character_list;
        } else if (numbers && lowercase && !uppercase) {
            selected_id_array = ArrayUtils.addAll(number_character_list, lowercase_character_list);
        } else if (!numbers && lowercase && uppercase) {
            selected_id_array = ArrayUtils.addAll(lowercase_character_list, uppercase_character_list);
        } else if (numbers && !lowercase && uppercase) {
            selected_id_array = ArrayUtils.addAll(number_character_list, uppercase_character_list);
        } else if (numbers && lowercase && uppercase) {
            selected_id_array = full_character_list;
        }

        char_list_count = selected_id_array.length;

        String idsNum_text = String.valueOf(bind.idsNum.getText()).trim();
        String characterNum_text = String.valueOf(bind.characterNum.getText()).trim();

        if (idsNum_text.contains(",")) {
            idsNum_text = idsNum_text.replaceAll(",", "");
        }

        if (characterNum_text.contains(",")) {
            characterNum_text = characterNum_text.replaceAll(",", "");
        }

        final int numCharactersPerID = Integer.parseInt(characterNum_text);
        final int numOfIDs = Integer.parseInt(idsNum_text);

        //check to see if even possible for the selected character array
        //to create as many unique IDs as user need
        //Math.pow() calculation is calculating maximum # of possible unique permutations
        if (numOfIDs <= Math.pow(char_list_count, numCharactersPerID)) {
            try {
                File myObj = new File(filename);
                myObj.createNewFile();
            } catch (IOException e) {
                Log.d("Exception", "Error creating file");
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.progress_bar, null);

            ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            TextView percent_progress_value = view.findViewById(R.id.percent_progress);
            progressBar.setMax(numOfIDs);

            builder.setView(view);
            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.show();

            //runs labor-intensive ID creation on background thread,
            //with updates to UI progress bar on main thread
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileWriter myWriter = new FileWriter(textFile);
                        while (count < numOfIDs) {
                            StringBuilder respid = new StringBuilder(numCharactersPerID);
                            for (int x = 0; x < numCharactersPerID; x++) {
                                respid.append(selected_id_array[new Random().nextInt(char_list_count)]);
                            }
                            //checks if ID is already in hashset (i.e. is it unique)
                            if (uniqueIDChecker.add(String.valueOf(respid))) {
                                respid.append(System.lineSeparator());
                                myWriter.write(String.valueOf(respid));
                                count++;

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(count);
                                        double percent_progress_text = ((double) count / (double) numOfIDs) * 100;
                                        String progress = (int) percent_progress_text + "%";
                                        percent_progress_value.setText(progress);
                                    }
                                });
                            }
                        }
                        myWriter.flush();
                        myWriter.close();
                    } catch (IOException e) {
                        Log.d("Exception", "Error writing file");
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            alert.dismiss();
                            sendEmail(textFile);
                        }
                    });
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DecimalFormat df = new DecimalFormat("#,###");
            builder.setMessage("Can't create " + df.format(numOfIDs)
                            + " unique IDs with parameters chosen. " +
                            "Please either change ID characteristics or increase # of characters per ID")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    private void sendEmail(File file) {
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        file.deleteOnExit();

        try {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmailAddress});
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailNote);
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
        } catch (Throwable e) {
            Toast.makeText(this, "Request failed try again: " + e, Toast.LENGTH_LONG).show();
        }
    }
}