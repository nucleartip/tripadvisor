package com.example.tripplanner;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class TravelActivity extends FragmentActivity 
                  implements DatePickerDialog.OnDateSetListener{

	private Button search;
	private AutoCompleteTextView startLocation;
	private AutoCompleteTextView endLocation;
	private Context context;
	private TextView selectedDate;
	private DatePickerDialogFrgment dateFragment;
    private AutoCompleteAdapter startAdapter;
    private AutoCompleteAdapter endAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Setting up content view
		setContentView(R.layout.activity_travel);

		// Fetching up form objects
		this.context = this;
		search = (Button)findViewById(R.id.search);
		selectedDate = (TextView)findViewById(R.id.selected_date);
		startLocation = (AutoCompleteTextView)findViewById(R.id.autocomplete_start_location);
		endLocation = (AutoCompleteTextView)findViewById(R.id.autocomplete_end_location);
		startAdapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line); 
		endAdapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
	    // Setting up autocompleteview
	    startLocation.setAdapter(startAdapter);
        endLocation.setAdapter(endAdapter);
		// Setting up date picker

		selectedDate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				
				if(dateFragment == null){
					dateFragment = new DatePickerDialogFrgment();
					dateFragment.setDateListener(TravelActivity.this);
					dateFragment.show(getSupportFragmentManager(), "datePicker");				
				}else{
					dateFragment.dismiss();
					dateFragment = new DatePickerDialogFrgment();
					dateFragment.setDateListener(TravelActivity.this);
					dateFragment.show(getSupportFragmentManager(), "datePicker");					
				}
			}

		});
		// Setting up listeners
	
		
		search.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Search is not implemented yet !!", Toast.LENGTH_SHORT).show();	
			}

		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel, menu);
		return true;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		month = month + 1;
		selectedDate.setText(month+"-"+day+"-"+year);
	}
	
	
}
