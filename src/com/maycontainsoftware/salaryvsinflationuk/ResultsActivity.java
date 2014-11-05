package com.maycontainsoftware.salaryvsinflationuk;

import static com.maycontainsoftware.salaryvsinflationuk.Constants.END_YEAR;
import static com.maycontainsoftware.salaryvsinflationuk.Constants.START_YEAR;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsActivity extends ActionBarActivity {

	// private static final String TAG = ResultsActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the UI
		setContentView(R.layout.activity_results);

		// Get reference to SharedPreferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		// Get a reference to the ListView
		ListView listView = (ListView) findViewById(R.id.listView);

		// Add the header row
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = inflater.inflate(R.layout.results_header, null);
		listView.addHeaderView(header);

		// Set up the adapter
		SalaryResultsAdapter salaryResultsAdapter = new SalaryResultsAdapter(this);
		listView.setAdapter(salaryResultsAdapter);

		// Data structure that holds the generated results - owned by adapter
		SparseArray<SalaryResult> salaryResults = salaryResultsAdapter.getData();
		salaryResults.clear();

		// Control fields to help generate result data
		Integer initialYear = null;
		Long lastSalary = null;

		for (int year = START_YEAR; year <= END_YEAR; year++) {
			// Get salary, if available
			Long salary = null;
			if (preferences.contains(String.valueOf(year))) {
				salary = preferences.getLong(String.valueOf(year), 0);
			}
			final float cpi = Constants.CPI_BY_YEAR.get(year);

			if (initialYear == null && salary == null) {
				// Still not seen any salaries, ignore
			} else if (initialYear == null) {
				// This is the first salary we've seen
				initialYear = year;
				lastSalary = salary;

				salaryResults.append(year, new SalaryResult(salary, null, null, null));

			} else if (salary == null) {
				// We've already seen a salary, but none for this year

				// Calculate salary + inflation
				long salaryPlusInflation = (long) (lastSalary * ((cpi + 100) / 100.0f));
				lastSalary = salaryPlusInflation;

				salaryResults.append(year, new SalaryResult(null, cpi, salaryPlusInflation, null));

			} else {
				// We've already seen a salary, and have one for this year as well

				// Calculate salary + inflation
				long salaryPlusInflation = (long) (lastSalary * ((cpi + 100) / 100.0f));
				long difference = salary - salaryPlusInflation;
				lastSalary = salary;

				salaryResults.append(year, new SalaryResult(salary, cpi, salaryPlusInflation, difference));
			}
		}

		salaryResultsAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_back:
			// Go back to the previous activity
			finish();
			return true;
		case R.id.action_help:
			// Show help activity
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu, add to action bar
		getMenuInflater().inflate(R.menu.menu_results, menu);
		return true;
	}

	private static class SalaryResult {
		Long salary;
		Float cpi;
		Long salaryPlusInflation;
		Long difference;

		public SalaryResult(Long salary, Float cpi, Long salaryPlusInflation, Long difference) {
			this.salary = salary;
			this.cpi = cpi;
			this.salaryPlusInflation = salaryPlusInflation;
			this.difference = difference;
		}
	}

	private static class SalaryResultsAdapter extends BaseAdapter {

		private static final int INITIAL_SALARY = 0;
		private static final int NO_SALARY_SUBMITTED = 1;
		private static final int SALARY_SUBMITTED = 2;

		SparseArray<SalaryResult> data = new SparseArray<SalaryResult>(END_YEAR - START_YEAR + 1);
		private LayoutInflater inflater;
		private Resources resources;

		public SalaryResultsAdapter(Context context) {
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			resources = context.getResources();
		}

		SparseArray<SalaryResult> getData() {
			return data;
		}

		// Determine the three different types of row we're displaying
		// With these defined, Android can pass a valid convertView to the
		// adapter's getView method
		@Override
		public int getItemViewType(int position) {
			final SalaryResult result = data.valueAt(position);
			final Long salaryValue = result.salary;
			if (position == 0) {
				return INITIAL_SALARY;
			} else if (salaryValue == null) {
				return NO_SALARY_SUBMITTED;
			} else {
				return SALARY_SUBMITTED;
			}
		}

		@Override
		public int getViewTypeCount() {
			// Three different view types: initial salary, no salary submitted,
			// salary submitted
			return 3;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			switch (getItemViewType(position)) {
			case INITIAL_SALARY:
				if (row == null) {
					row = inflater.inflate(R.layout.results_initial, null);
				}
				populateInitialSalaryView(row, position);
				break;
			case NO_SALARY_SUBMITTED:
				if (row == null) {
					row = inflater.inflate(R.layout.results_no_salary, null);
				}
				populateNoSalarySubmittedView(row, position);
				break;
			case SALARY_SUBMITTED:
				if (row == null) {
					row = inflater.inflate(R.layout.results_with_salary, null);
				}
				populateSalarySubmittedView(row, position);
				break;
			}

			return row;
		}

		private void populateInitialSalaryView(View row, int position) {
			// Required data
			final int yearValue = data.keyAt(position);
			final SalaryResult result = data.valueAt(position);

			// Year
			TextView year = (TextView) row.findViewById(R.id.year);
			year.setText(String.valueOf(yearValue));

			// Salary
			TextView salary = (TextView) row.findViewById(R.id.actual_salary);
			salary.setText(Constants.SALARY_FORMAT.format(result.salary));
		}

		private void populateNoSalarySubmittedView(View row, int position) {
			// Required data
			final int yearValue = data.keyAt(position);
			final SalaryResult result = data.valueAt(position);

			// Year
			TextView tvYear = (TextView) row.findViewById(R.id.year);
			tvYear.setText(String.valueOf(yearValue));

			// Inflation
			TextView tvInflation = (TextView) row.findViewById(R.id.inflation);
			tvInflation.setText(Constants.INFLATION_FORMAT.format(result.cpi));

			// Salary Plus Inflation
			TextView tvSalaryPlus = (TextView) row.findViewById(R.id.salary_plus_inflation);
			tvSalaryPlus.setText(Constants.SALARY_FORMAT.format(result.salaryPlusInflation));
		}

		private void populateSalarySubmittedView(View row, int position) {
			// Required data
			final int yearValue = data.keyAt(position);
			final SalaryResult result = data.valueAt(position);

			// Year
			TextView tvYear = (TextView) row.findViewById(R.id.year);
			tvYear.setText(String.valueOf(yearValue));

			// Inflation
			TextView tvInflation = (TextView) row.findViewById(R.id.inflation);
			tvInflation.setText(Constants.INFLATION_FORMAT.format(result.cpi));

			// Salary Plus Inflation
			TextView tvSalaryPlus = (TextView) row.findViewById(R.id.salary_plus_inflation);
			tvSalaryPlus.setText(Constants.SALARY_FORMAT.format(result.salaryPlusInflation));

			// Actual Salary
			TextView tvActualSalary = (TextView) row.findViewById(R.id.actual_salary);
			tvActualSalary.setText(Constants.SALARY_FORMAT.format(result.salary));

			// Raise/Cut
			TextView tvRaiseCut = (TextView) row.findViewById(R.id.raise_cut);
			String formattedDifference = Constants.SALARY_FORMAT.format(result.difference);
			if (result.difference == 0L) {
				String message = resources.getString(R.string.equal_message, formattedDifference);
				tvRaiseCut.setText(message);
				tvRaiseCut.setTextColor(resources.getColor(R.color.blue));
			} else {
				String message = resources.getString(result.difference < 0L ? R.string.cut_message
						: R.string.raise_message, formattedDifference);
				tvRaiseCut.setText(message);
				tvRaiseCut.setTextColor(resources.getColor(result.difference < 0L ? R.color.red : R.color.green));
			}
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

		// Disable touch-effect on rows
		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	}
}
