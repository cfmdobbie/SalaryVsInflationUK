package com.maycontainsoftware.salaryvsinflationuk.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
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
}