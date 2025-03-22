package com.michaelcaldow.unitConverterApp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Initialising my Spinners, TextViews, and EditTexts
    Spinner categorySpinner;
    Spinner sourceUnitSpinner;
    Spinner destinationUnitSpinner;
    EditText sourceUnitValueEditText;
    TextView resultTextView;
    Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Locating my Spinners, TextViews, and EditTexts from activity_main.xml file
        categorySpinner = findViewById(R.id.spinner_categories);
        sourceUnitSpinner = findViewById(R.id.spinner_source_unit);
        destinationUnitSpinner = findViewById(R.id.spinner_destination_unit);
        sourceUnitValueEditText = findViewById(R.id.editTextNumberDecimal_source_unit);
        resultTextView = findViewById(R.id.textView_result);
        convertButton = findViewById(R.id.button_convert);

        // Populating the category spinner with the conversion types (Length, Weight, Temperature)
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        categorySpinner.setAdapter(categoryAdapter);

        // Setting up a listener for whatever was selected as the conversion category
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                updateUnitSpinners(selectedCategory);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Setting up a convert button listener
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Setting up an updateUnitSpinners method so that the source unit and destination unit spinners have their content altered based on what conversion category is selected.
    private void updateUnitSpinners(String category) {
        ArrayAdapter<CharSequence> unitAdapter = null;

        switch (category) {
            case "Length":
                unitAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.length_units_array,
                        android.R.layout.simple_spinner_dropdown_item
                );
                break;
            case "Weight":
                unitAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.weight_units_array,
                        android.R.layout.simple_spinner_dropdown_item
                );
                break;
            case "Temperature":
                unitAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.temperature_units_array,
                        android.R.layout.simple_spinner_dropdown_item
                );
                break;
        }

        if (unitAdapter != null) {
            sourceUnitSpinner.setAdapter(unitAdapter);
            destinationUnitSpinner.setAdapter(unitAdapter);
        }
    }

    // Creating a performConversion method to be performed since the button will run this method when clicked
    private void performConversion() {
        String category = categorySpinner.getSelectedItem().toString();
        String sourceUnit = sourceUnitSpinner.getSelectedItem().toString();
        String destinationUnit = destinationUnitSpinner.getSelectedItem().toString();
        String sourceValueStr = sourceUnitValueEditText.getText().toString();

        // Creating a toast to notify user to populate  the Source unit value
        if (sourceValueStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value to convert.", Toast.LENGTH_SHORT).show();
            return;
        }

        double sourceValue = Double.parseDouble(sourceValueStr);
        double result = 0;

        if (category.equals("Length")) {
            result = convertLength(sourceValue, sourceUnit, destinationUnit);
        } else if (category.equals("Weight")) {
            result = convertWeight(sourceValue, sourceUnit, destinationUnit);
        } else if (category.equals("Temperature")) {
            result = convertTemperature(sourceValue, sourceUnit, destinationUnit);
        }

        resultTextView.setText(String.valueOf(result));
    }

    // Creating convertLength method so the performConversion method can be executed when a length conversion is required
    private double convertLength(double value, String source, String destination) {

        // Converting source value to meters first, so that later the method can convert to the destination unit easily
        double meters = value;

        switch (source) {
            case "Centimeter":
                meters = value / 100.0;
                break;
            case "Inch":
                meters = value * 0.0254;
                break;
            case "Foot":
                meters = value * 0.3048;
                break;
            case "Yard":
                meters = value * 0.9144;
                break;
            case "Kilometer":
                meters = value * 1000.0;
                break;
            case "Mile":
                meters = value * 1609.34;
                break;
        }

        // Converting to destination unit from meters
        switch (destination) {
            case "Centimeter":
                return meters * 100.0;
            case "Inch":
                return meters / 0.0254;
            case "Foot":
                return meters / 0.3048;
            case "Yard":
                return meters / 0.9144;
            case "Kilometer":
                return meters / 1000.0;
            case "Mile":
                return meters / 1609.34;
            default: // Meter
                return meters;
        }

    }

    private double convertWeight(double value, String source, String destination) {

        // Converting source value to kilograms first, so that the method can convert to the destination unit later more easily
        double kilograms = value;

        switch (source) {
            case "Gram":
                kilograms = value / 1000.0;
                break;
            case "Pound":
                kilograms = value * 0.453592;
                break;
            case "Ounce":
                kilograms = value * 0.0283495;
                break;
            case "Ton":
                kilograms = value * 907.185;
                break;
        }

        // Converting from kilograms to destination unit
        switch (destination) {
            case "Gram":
                return kilograms * 1000.0;
            case "Pound":
                return kilograms / 0.453592;
            case "Ounce":
                return kilograms / 0.0283495;
            case "Ton":
                return kilograms / 907.185;
            default: // Kilogram
                return kilograms;
        }
    }

    private double convertTemperature(double value, String source, String destination) {

        // Storing source unit value into "result" variable, and then using if, else if statements as there are only three destination units available
        double result = value;

        if (source.equals("Celcius")) {
            if (destination.equals("Fahrenheit")) {
                result = (value * 9.0 / 5.0) + 32.0;
            } else if (destination.equals("Kelvin")) {
                result = value + 273.15;
            }
        } else if (source.equals("Fahrenheit")) {
            if (destination.equals("Celcius")) {
                result = (value - 32.0) * 5.0 / 9.0;
            } else if (destination.equals("Kelvin")) {
                result = (value + 459.67) * 5.0 / 9.0;
            }
        } else if (source.equals("Kelvin")) {
            if (destination.equals("Celcius")) {
                result = value - 273.15;
            } else if (destination.equals("Fahrenheit")) {
                result = (value * 9.0 / 5.0) - 459.67;
            }
        }
        return result;
    }
}