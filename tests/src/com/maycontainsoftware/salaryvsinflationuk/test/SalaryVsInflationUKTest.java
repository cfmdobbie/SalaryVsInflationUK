package com.maycontainsoftware.salaryvsinflationuk.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.maycontainsoftware.salaryvsinflationuk.Constants;
import com.maycontainsoftware.salaryvsinflationuk.SalaryListActivity;

public class SalaryVsInflationUKTest extends ActivityInstrumentationTestCase2<SalaryListActivity> {

	private SalaryListActivity mActivity;
	private Spinner mSpinner;
	private SpinnerAdapter mYearData;

	public SalaryVsInflationUKTest() {
		super(SalaryListActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		mSpinner = (Spinner) mActivity.findViewById(com.maycontainsoftware.salaryvsinflationuk.R.id.yearSpinner);
		mYearData = mSpinner.getAdapter();
	}

	public void testPreConditions() {
		// assertTrue(mSpinner.getOnItemSelectedListener() != null);
		assertTrue(mYearData != null);
		assertEquals(mYearData.getCount(), Constants.END_YEAR - Constants.START_YEAR + 1);
		assertEquals(mYearData.getCount(), Constants.CPI_BY_YEAR.size());
	}

	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 5;

	public void testYearDpadSelection() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSpinner.requestFocus();
				mSpinner.setSelection(INITIAL_POSITION);
			}
		});

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		for (int i = 1; i <= TEST_POSITION; i++) {
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		}

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

		int pos = mSpinner.getSelectedItemPosition();

		assertEquals(pos, TEST_POSITION);
	}

	public void testFooterViewExistsOnSalaryList() {
		final ListView salaryList = (ListView) mActivity
				.findViewById(com.maycontainsoftware.salaryvsinflationuk.R.id.salaryList);
		assertEquals(1, salaryList.getFooterViewsCount());
	}

	public void testResultsButtonVisible() {
		final Button resultsButton = (Button) mActivity
				.findViewById(com.maycontainsoftware.salaryvsinflationuk.R.id.resultsButton);
		assertEquals(View.VISIBLE, resultsButton.getVisibility());
	}

	// Future tests to be written:

	// TODO: Delete All action bar item is no-op when list empty
	// FUTURE: Or should Delete All be disabled when list is empty?
	// TODO: Delete All results in empty list when list was not empty

	// TODO: Help action bar item launches HelpActivity

	// TODO: With empty list, entering a salary and pressing Add/Replace results in list with one entry
	// TODO: Add/Replace is a no-op when Salary box empty
	// FUTURE: Or should it be disabled when salary box is empty?
	// TODO: Add/Replace updates a value when the year already exists in list
	// TODO: Spinner moves to next item if available on Add/Replace

	// TODO: Back button exits application (if virtual keyboard not present!)

	// TODO: Next button does not exist when list is empty
	// TODO: Next button launches ResultsActivity when list is not empty
}
