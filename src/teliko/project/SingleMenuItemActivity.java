package teliko.project;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SingleMenuItemActivity extends ListActivity {
	JSONArray array;
	ArrayList<HashMap<String, String>> articleList = new ArrayList<HashMap<String, String>>();
	// Ετικέτες JSON
	private static final String TAG_TITLE = "title";
	private static final String TAG_MOBILEURL = "mobileurl";
	private static final String TAG_DISTANCE = "distance";
	private static final String TAG_TYPE = "type";
	private static final String TAG_JSONLIST = "list";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_list_item_view);

		// Παίρνουμε τα δεδομένα από το κύριο πρόγραμμα, το TelikoActivity
		Intent in = getIntent();
		String jsonArray = in.getStringExtra(TAG_JSONLIST);
		String type = in.getStringExtra(TAG_TYPE);
		try {
			// Μετατροπή του αντικειμένου (String) array σε JSONArray
			array = new JSONArray(jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			// Ανάγνωση της δομής JSON
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject c = array.getJSONObject(i);
				// Αποθήκευση κάθε ετικέτας ενός στοιχείου σε ανάλογη μεταβλητή
				String JSONtype = c.getString(TAG_TYPE);
				String title = c.getString(TAG_TITLE);
				String mobileurl = c.getString(TAG_MOBILEURL);
				String distance = c.getString(TAG_DISTANCE);
				// Δημιουργία νέας λίστας στοιχείων JSON μετά από RESTFUL-GET
				// αίτημα
				HashMap<String, String> map = new HashMap<String, String>();
				// Έλεγχος για να διαλέξει τα άρθρα, ανάλογα με τον τύπο του που
				// πήραμε ως δεδομένο από το κύριο πρόγραμμα
				if (JSONtype.equals(type)) {
					// Σύνδεση κάθε ετικέτας με αντίστοιχη της στην νέα λίστα
					map.put(TAG_TITLE, title);
					map.put(TAG_DISTANCE, distance);
					map.put(TAG_MOBILEURL, mobileurl);
					// Εισαγωγή της HashList σε λίστα αφηρημένων στοιχείων
					articleList.add(map);
				}
			}
		} catch (JSONException e) {
			// Έλεγχος αν έγινε ομαλά η μετάφραση
			e.printStackTrace();
		}
		// Ενημέρωση της λίστας με τα στοιχεία από το αντικείμενο ArticleList
		ListAdapter adapter = new SimpleAdapter(this, articleList,
				R.layout.single_item_of_single_list_item_view, new String[] {
						TAG_TITLE, TAG_DISTANCE, TAG_MOBILEURL }, new int[] {
						R.id.title, R.id.distance, R.id.mobileurl });

		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String url = ((TextView) view.findViewById(R.id.mobileurl))
						.getText().toString();
				// Εκτέλεση φυλλομετρητή με όρισμα τον σύνδεσμο που έχει η
				// μεταβλητή url
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
	}
}
