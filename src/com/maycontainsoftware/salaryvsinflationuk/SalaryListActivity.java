package com.maycontainsoftware.salaryvsinflationuk;

import static com.maycontainsoftware.salaryvsinflationuk.Constants.END_YEAR;
import static com.maycontainsoftware.salaryvsinflationuk.Constants.START_YEAR;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SalaryListActivity extends ActionBarActivity {

	//private static final String TAG = SalaryListActivity.class.getName();
	private SalaryAdapter salaryAdapter;
	private SharedPreferences preferences;
	private ListView salaryList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salary_list);

		// Get references to UI components
		Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
		salaryList = (ListView) findViewById(R.id.salaryList);
		
		// Set up list of years for Spinner component
		List<Integer> years = new ArrayList<Integer>();
		for (int i = START_YEAR; i <= END_YEAR; i++) {
			years.add(i);
		}

		// Set up spinner
		ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, years);
		yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearSpinner.setAdapter(yearAdapter);
		
		// Layout Inflater
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Add header to ListView (must be done before setAdapter)
		View header = inflater.inflate(R.layout.salary_list_header, null);
		salaryList.addHeaderView(header, null, false);

		// Add footer to ListView (must be done before setAdapter)
		View footer = inflater.inflate(R.layout.salary_list_footer, null);
		salaryList.addFooterView(footer);

		// Add empty view to ListView
		View emptyView = findViewById(R.id.emptyMessage);
		salaryList.setEmptyView(emptyView);

		// Add the SalaryAdapter to ListView
		salaryAdapter = new SalaryAdapter(this);
		salaryList.setAdapter(salaryAdapter);
		
		// Get reference to SharedPreferences, for saving state
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Save data to SharedPreferences
		Editor editor = preferences.edit();
		editor.clear();
		SparseArray<Long> data = salaryAdapter.getData();
		for (int i = 0; i < data.size(); i++) {
			int key = data.keyAt(i);
			long value = data.valueAt(i);
			editor.putLong(String.valueOf(key), value);
		}
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Load data from SharedPreferences
		SparseArray<Long> data = salaryAdapter.getData();
		data.clear();
		for (int year = START_YEAR; year <= END_YEAR; year++) {
			if (preferences.contains(String.valueOf(year))) {
				data.put(year, preferences.getLong(String.valueOf(year), 0));
			}
		}
		salaryAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_help:
			// Show help activity
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_delete_all:
			// Show confirmation box before deleting all salary entries
			// But only if there are items to delete!
			if(salaryAdapter.getData().size() != 0) {
				new AlertDialog.Builder(this)
				.setMessage(R.string.confirm_delete_message)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						salaryAdapter.getData().clear();
						salaryAdapter.notifyDataSetChanged();
					}
				}).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu, add to action bar
		getMenuInflater().inflate(R.menu.menu_salary_list, menu);
		return true;
	}

	public void onClickAddReplace(View view) {

		// Get salary
		EditText salaryInput = (EditText) findViewById(R.id.salaryInput);
		String salaryText = salaryInput.getText().toString();

		// If a salary was entered, process it
		if (salaryText.length() != 0) {
			
			// Determine salary
			long salaryValue;
			try {
				salaryValue = Long.valueOf(salaryText);
			} catch(NumberFormatException e) {
				Toast.makeText(getApplicationContext(), R.string.salary_too_large, Toast.LENGTH_SHORT).show();
				return;
			}
			// TODO: This accepts a value that might overflow once salary+inflation is calculated.  Good way to resolve?

			// Get selected year
			Spinner year = (Spinner) findViewById(R.id.yearSpinner);
			int selectedYear = (Integer) year.getSelectedItem();

			// Clear salary box
			salaryInput.setText("");

			// Update spinner to select the next year, for convenience
			int nextSelection = year.getSelectedItemPosition() + 1;
			if (nextSelection < year.getCount()) {
				year.setSelection(nextSelection);
			}

			// Add the new entry to the list
			salaryAdapter.getData().put(selectedYear, salaryValue);
			salaryAdapter.notifyDataSetChanged();
			
			// Scroll the ListView to show the new value
			int position = salaryAdapter.getData().indexOfKey(selectedYear);
			salaryList.setSelection(position); // Jumps to position
			//salaryList.smoothScrollToPosition(position); // Doesn't take row height into account
		}
	}

	public void onClickResults(View view) {
		Intent intent = new Intent(this, ResultsActivity.class);
		startActivity(intent);
	}

	private static class SalaryAdapter extends BaseAdapter {

		SparseArray<Long> data = new SparseArray<Long>();
		private LayoutInflater inflater;

		public SalaryAdapter(Context context) {
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		SparseArray<Long> getData() {
			return data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View row = convertView;
			if(row == null) {
				row = inflater.inflate(R.layout.salary_list_item, null);
			}

			TextView year = (TextView) row.findViewById(R.id.year);
			TextView salary = (TextView) row.findViewById(R.id.salary);

			final int yearValue = data.keyAt(position);
			final long salaryValue = data.valueAt(position);

			year.setText(String.valueOf(yearValue));
			salary.setText(Constants.SALARY_FORMAT.format(salaryValue));

			ImageButton deleteButton = (ImageButton) row.findViewById(R.id.delete);
			deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					data.remove(yearValue);
					SalaryAdapter.this.notifyDataSetChanged();
				}
			});

			return row;
		}

		@Override
		public long getItemId(int position) {
			return data.keyAt(position);
		}

		@Override
		public Object getItem(int position) {
			return data.valueAt(position);
		}

		@Override
		public int getCount() {
			return data.size();
		}
	}
}
