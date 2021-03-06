package pl.zhr.hak.wykrywaczchorob.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import pl.zhr.hak.wykrywaczchorob.Disease;
import pl.zhr.hak.wykrywaczchorob.R;

import static pl.zhr.hak.wykrywaczchorob.Disease.getDiseases;
import static pl.zhr.hak.wykrywaczchorob.activities.LoginActivity.sharedPreferencesName;

public class AddPatientActivity extends AppCompatActivity {

    @BindViews({R.id.editTextAddName, R.id.editTextAddSurname, R.id.editTextAge})
    List<EditText> editTexts;
    @BindView(R.id.textViewAddDisease)
    TextView textViewAddDisease;
    @BindView(R.id.spinnerSex)
    Spinner spinnerSex;
    @BindString(R.string.male)
    String male;
    @BindString(R.string.female)
    String female;

    private SharedPreferences sharedPreferences;
    private int diseaseID;
    private String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences(sharedPreferencesName, 0);
        sex = "";

        Bundle extras = getIntent().getExtras();
        diseaseID = Objects.requireNonNull(extras).getInt("diseaseID", 0);
        List<Disease> diseaseList = getDiseases(this);

        textViewAddDisease.setText(getString(R.string.disease_introduce, diseaseList.get(diseaseID - 1).getDiseaseName()));

        String[] spinnerSexes = new String[]{"-", female, male};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerSexes);
        spinnerSex.setAdapter(spinnerAdapter);
    }

    // reaction to interaction in the spinner
    @OnItemSelected(R.id.spinnerSex)
    public void onItemSelected(int position) {
        switch (position) {
            case 0:
                sex = "";
                break;
            case 1:
                sex = "female";
                break;
            case 2:
                sex = "male";
                break;
            default:
                break;
        }
    }

    // no interaction in spinner
    @OnItemSelected(value = R.id.spinnerSex, callback = OnItemSelected.Callback.NOTHING_SELECTED)
    public void onNothingSelected() {
        // do nothing
    }

    //cancel adding patient
    @OnClick(R.id.buttonAddCancel)
    public void buttonAddCancel() {
        finish();
    }

    // confirm adding patient - check if adding will be correct
    @OnClick(R.id.buttonAddConfirm)
    public void buttonAddConfirm() {
        String name = editTexts.get(0).getText().toString();
        String surname = editTexts.get(1).getText().toString();
        // if you have not filled in all the patient's data, do not go further
        if (name.isEmpty() || surname.isEmpty() || editTexts.get(2).getText().toString().isEmpty() || sex.equals("")) {
            Toast.makeText(AddPatientActivity.this, R.string.alldata, Toast.LENGTH_SHORT).show();
        } else {
            // checking whether a number has been entered in the age field
            boolean digitsOnly = TextUtils.isDigitsOnly(editTexts.get(2).getText());

            if (digitsOnly) {
                int age = Integer.parseInt(editTexts.get(2).getText().toString());
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Intent patientsActivity = new Intent(AddPatientActivity.this, PatientsActivity.class);

                // manually assigning IDs to patients
                int patientID = sharedPreferences.getInt("id", 1);
                patientsActivity.putExtra("id", patientID);
                sharedPreferences.edit().putInt("id", patientID + 1).apply();

                // flag to signal the need to add a patient - there is a need here
                patientsActivity.putExtra("flag", true);
                patientsActivity.putExtra("name", name);
                patientsActivity.putExtra("surname", surname);
                patientsActivity.putExtra("sex", sex);
                patientsActivity.putExtra("age", age);
                patientsActivity.putExtra("diseaseID", diseaseID);
                patientsActivity.putExtra("date", date);
                startActivity(patientsActivity);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.wrong_age), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
