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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kunle.fragments.randomidgenerator.databinding.ActivityMainBinding;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setLayoutText();
        setOnClickListeners();
    }

    private void setLayoutText() {
        SpannableString first_part = new SpannableString("ID Characteristics");
        SpannableString second_part = new SpannableString("(must select at least 1)");
        second_part.setSpan(new AbsoluteSizeSpan(18, true),
                0, first_part.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        second_part.setSpan(new AbsoluteSizeSpan(15, true),
                0, second_part.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        CharSequence finalText = TextUtils.concat(first_part, " ", second_part);
        bind.checkBoxLabel.setText(finalText);

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
        bind.emailInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailOptions();
            }
        });

        bind.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendEmail(createIDs());
                createIDs();
            }
        });

        bind.numbersCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anyCheckBoxChecked = bind.numbersCheckBox.isChecked() ||
                        bind.lowercaseCheckBox.isChecked() ||
                        bind.uppercaseCheckBox.isChecked();
                bind.createButton.setEnabled(anyCheckBoxChecked);
            }
        });

        bind.lowercaseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anyCheckBoxChecked = bind.numbersCheckBox.isChecked() ||
                        bind.lowercaseCheckBox.isChecked() ||
                        bind.uppercaseCheckBox.isChecked();
                bind.createButton.setEnabled(anyCheckBoxChecked);
            }
        });

        bind.uppercaseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anyCheckBoxChecked = bind.numbersCheckBox.isChecked() ||
                        bind.lowercaseCheckBox.isChecked() ||
                        bind.uppercaseCheckBox.isChecked();
                bind.createButton.setEnabled(anyCheckBoxChecked);
            }
        });
    }

    private void showEmailOptions() {
        LayoutInflater inflater = getLayoutInflater();
        View emailView = inflater.inflate(R.layout.email_info, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        int dialog_button_color = ContextCompat.getColor(this, R.color.dialog_button_color);

        TextInputEditText toEmail = emailView.findViewById(R.id.toEmailAddress);
        TextInputEditText subject = emailView.findViewById(R.id.subject);
        TextInputEditText note = emailView.findViewById(R.id.note);

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

    private File createIDs() {
        File externalFilesDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        textFile = new File(externalFilesDirectory, filename);
        textFile.deleteOnExit();

        boolean numbers = bind.numbersCheckBox.isChecked();
        boolean lowercase = bind.lowercaseCheckBox.isChecked();
        boolean uppercase = bind.uppercaseCheckBox.isChecked();

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
        } else {
            Log.d("LogicTest", "Error, I've missed a scenario somehow!");
        }

        char_list_count = selected_id_array.length;

        final int numCharactersPerID = Integer.parseInt(String.valueOf(bind.characterNum.getText()).trim());
        final int numOfIDs = Integer.parseInt(String.valueOf(bind.idsNum.getText()).trim());
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
        progressBar.setMax(numOfIDs);

        builder.setView(view);
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();

        Log.d("GetMaxTest", "getMax: " + progressBar.getMax());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int y = 0;
                    FileWriter myWriter = new FileWriter(textFile);
                    for (int i = 0; i < numOfIDs; i++) {
                        StringBuilder respid = new StringBuilder(numCharactersPerID);
                        count = i;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if ((count % (numOfIDs / 100) == 0)) {
                                    progressBar.setProgress(count);
                                }
                            }
                        });


                        for (int x = 0; x < numCharactersPerID; x++) {
                            respid.append(selected_id_array[new Random().nextInt(char_list_count - 1)]);
                        }

                        respid.append(System.lineSeparator());
                        myWriter.write(String.valueOf(respid));
                        y = i;
                    }
                    myWriter.flush();
                    myWriter.close();
                    Log.d("CompletionTest", "id#: " + y);
                    executor.shutdown();

                } catch (IOException e) {
                    Log.d("Exception", "Error writing file");
                    e.printStackTrace();
                }
            }
        });

        try {
            while (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
            }

            Log.d("ThreadSleepTest", "Finished!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        alert.dismiss();
        return textFile;
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
            Toast.makeText(this, "Request failed try again: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}