package com.maycontainsoftware.salaryvsinflationuk;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HelpActivity extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_help);
	    
	    // TextView to hold the help text
	    TextView howToPlay = (TextView)findViewById(R.id.help_text);
	    
	    // Help text is rendered as HTML
	    String html = getResources().getString(R.string.help_text);
	    howToPlay.setText(Html.fromHtml(html));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_back:
			// Go back to the previous activity
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu, add to action bar
		getMenuInflater().inflate(R.menu.menu_help, menu);
		return true;
	}
}
