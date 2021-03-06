package com.maycontainsoftware.salaryvsinflationuk;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;

public class Constants {

	// The CPI data we have runs from 1989 to 2014
	public static final int START_YEAR = 1989;
	public static final int END_YEAR = 2014;

	public static final DecimalFormat SALARY_FORMAT = new DecimalFormat("� #,##0");
	public static final DecimalFormat INFLATION_FORMAT = new DecimalFormat("0.0 '%'");

	// CPI data mapped by year from START_YEAR to END_YEAR inclusive
	@SuppressLint("UseSparseArrays")
	public static final HashMap<Integer, Float> CPI_BY_YEAR = new HashMap<Integer, Float>(END_YEAR - START_YEAR + 1);
	static {
		CPI_BY_YEAR.put(1989, 5.2f);
		CPI_BY_YEAR.put(1990, 7f);
		CPI_BY_YEAR.put(1991, 7.5f);
		CPI_BY_YEAR.put(1992, 4.3f);
		CPI_BY_YEAR.put(1993, 2.5f);
		CPI_BY_YEAR.put(1994, 2f);
		CPI_BY_YEAR.put(1995, 2.6f);
		CPI_BY_YEAR.put(1996, 2.5f);
		CPI_BY_YEAR.put(1997, 1.8f);
		CPI_BY_YEAR.put(1998, 1.6f);
		CPI_BY_YEAR.put(1999, 1.3f);
		CPI_BY_YEAR.put(2000, 0.8f);
		CPI_BY_YEAR.put(2001, 1.2f);
		CPI_BY_YEAR.put(2002, 1.3f);
		CPI_BY_YEAR.put(2003, 1.4f);
		CPI_BY_YEAR.put(2004, 1.3f);
		CPI_BY_YEAR.put(2005, 2.1f);
		CPI_BY_YEAR.put(2006, 2.3f);
		CPI_BY_YEAR.put(2007, 2.3f);
		CPI_BY_YEAR.put(2008, 3.6f);
		CPI_BY_YEAR.put(2009, 2.2f);
		CPI_BY_YEAR.put(2010, 3.3f);
		CPI_BY_YEAR.put(2011, 4.5f);
		CPI_BY_YEAR.put(2012, 2.8f);
		CPI_BY_YEAR.put(2013, 2.6f);
		CPI_BY_YEAR.put(2014, 1.5f);
	}
}
