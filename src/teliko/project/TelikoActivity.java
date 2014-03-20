package teliko.project;

/*
 Όλη την εργασία την δομήσαμε πάνω στο πρώϊμο GPS που σας είχε βάλει να κάνετε ο καθηγητής σας.
 Στην ουσία σβήσαμε όλες τις μεθόδους που κάνανε εύρεση προορισμού και μέτρησης απόστασης και
 εισάγαμε μια νέα κλάση, την JSONbackround. Εκεί βρίσκεται και η καρδιά της μετάφρασης των δεδομένων
 της δομής JSON
 */
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TelikoActivity extends ListActivity {
	EditText Radius;
	TextView Result, userx, usery;

	// JSON Node names
	private static final String TAG_ARTICLES = "articles";
	private static final String TAG_ID = "id";
	private static final String TAG_LAT = "lat";
	private static final String TAG_LGT = "lng";
	private static final String TAG_TYPE = "type";
	private static final String TAG_TITLE = "title";
	private static final String TAG_URL = "url";
	private static final String TAG_MOBILEURL = "mobileurl";
	private static final String TAG_DISTANCE = "distance";

	// contacts JSONArray
	JSONArray articles = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);
		userx = (TextView) findViewById(R.id.UserPositionX);
		usery = (TextView) findViewById(R.id.UserPositionY);
		Radius = (EditText) findViewById(R.id.Radius);
		Result = (TextView) findViewById(R.id.Result);

	}

	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {

			if (Radius.getText().length() != 0 || userx.getText().length() != 0
					|| usery.getText().length() != 0) {
				userx.setText(" Lat. = " + loc.getLatitude());
				usery.setText(" Long. = " + loc.getLongitude());
				// Εκτελεί ασύγχρονα, μέσα στο κύριο UI την κλάσση
				// JSONBackground
				// Ως όρισμα παίρνει τις πληροφορίες τοποθεσίας από το GPS
				new JSONbackground().execute(loc);

			}
		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), ((String) "GPS Disabled"),
					Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), ((String) "GPS Enabled"),
					Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/*
	 * Θέτουμε μια κλάση μέσα στην κύρια μας κλάση, κληρονομώντας ιδιότητες
	 * ασύγχρονης εκτέλεσης της Οι παράμετροι που παίρνει η κλάση είναι:
	 * Location - Γεωγραφικά δεδομένα για την έναρξη Void - Δεν μας ενδιαφέρει
	 * κατά την διάρκεια της μετάφρασης της δομής JSON να μας εμφανίζει
	 * αποτελέσματα ArrayList<HashMap> - Η λίστα με τα ανακτημένα στοιχεία από
	 * την δομή JSON
	 */
	public class JSONbackground extends
		AsyncTask<Location, Void, ArrayList<HashMap<String, String>>> {
		// Οι διακόπτες που ελέγχουν αν μια ετικέτα τύπου έχει εισαχθεί στον πίνακα άρθρων
		Boolean sw_adm1st = true;
		Boolean sw_adm2nd = true;
		Boolean sw_adm3rd = true;
		Boolean sw_airport = true;
		Boolean sw_popcity = true;
		Boolean sw_city = true;
		Boolean sw_country = true;
		Boolean sw_edu = true;
		Boolean sw_event = true;
		Boolean sw_forest = true;
		Boolean sw_glacier = true;
		Boolean sw_isle = true;
		Boolean sw_landmark = true;
		Boolean sw_mountain = true;
		Boolean sw_pass = true;
		Boolean sw_railwaystation = true;
		Boolean sw_river = true;
		Boolean sw_satellite = true;
		Boolean sw_waterbody = true;
		Boolean sw_camera = true;

		ProgressDialog dialog; // Δήλωση του μηνύματος φόρτωσης άρθρων

		protected void onPreExecute() {
			// Αυτή η γραμμή ενεργοποιεί το μήνυμα ενημέρωσης ότι τα άρθρα
			// φορτώνονται. Αν δεν θέλουμε
			// να εμφανίζεται το μήνυμα και να μας αφήνει να αλληλεπιδράσουμε με
			// την εφαρμογή, την
			// σβύνουμε, όπως και την εντολή dialog.dismiss(); παρακάτω
			// Σε αντίθεση με τον προηγούμενο κώδικα που έστειλα, εδώ είναι απενεργοποιημένο
			//dialog = ProgressDialog.show(TelikoActivity.this, "Wikilocations",
					//"Please wait for articles to populate the list");

		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Location... params) {
			// Εκτέλεση της μεθόδου στο παρασκήνιο με όρισμα την μεταβλητή loc
			return JSONload(params[0]);
		}

		@Override
		protected void onPostExecute(
				ArrayList<HashMap<String, String>> articleList) {
			// Αφού εκτελεστεί επιτυχώς η μέθοδος doInBackround, επιστρέφει ένα
			// αντικείμενο
			// ArrayList που χρησιμοποιείται για την ενημέρωση της λίστας άρθρων
			ListAdapter adapter = new SimpleAdapter(TelikoActivity.this,
					articleList, R.layout.list_item, new String[] { TAG_TYPE },
					new int[] { R.id.type });

			setListAdapter(adapter);
			//dialog.dismiss();
			// Εμφάνιση δεδομένων στην λίστα, από την δομή JSON
			ListView lv = getListView();
			// Ενεργοποιούμε Listener για να φορτώσει την ιστοσελίδα στον
			// φυλλομετρητή όταν
			// επιλέξουμε κάποιο άρθρο από την λίστα
			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String ArticleType = ((TextView) view
							.findViewById(R.id.type)).getText().toString();
					// Starting new intent
					Intent in = new Intent(getApplicationContext(),
							SingleMenuItemActivity.class);
					in.putExtra(TAG_TYPE, ArticleType);
					in.putExtra("list", articles.toString());
					startActivity(in);
				}
			});

		}

		public ArrayList<HashMap<String, String>> JSONload(Location thisloc) {
			// Η διεύθυνση που θα αντλήσουμε τα δεδομένα JSON
			// Η γεωγραφική θέση καθορίζεται στο όρισμα μέσα, όπου lat και lng
			// είναι αντίστοιχα Χ και Υ
			// και Radius η τιμή που του θέτουμε εμείς στην εφαρμογή
			String APIurl = "http://api.wikilocation.org/articles?lat="
					+ thisloc.getLatitude() + "&lng=" + thisloc.getLongitude()
					+ "&format=json&radius=" + Radius.getText();
			// Αρχικοποίηση λίστας στοιχείων JSON μετά από RESTFUL-GET αίτημα
			// μέσα σε λίστα αφηρημένων στοιχείων
			ArrayList<HashMap<String, String>> articleList = new ArrayList<HashMap<String, String>>();

			// Αρχικοποίηση του μεταφραστή JSON
			JSONParser jParser = new JSONParser();

			// Ανάκτηση δομής JSON από την διεύθυνση που θέσαμε πρίν
			JSONObject json = jParser.getJSONFromUrl(APIurl);
			try {
				// Ανάγνωση των στοιχείων της δομής JSON
				// και έλεγχος αν μια ετικέτα κατηγορίας έχει αναγνωστεί
				articles = json.getJSONArray(TAG_ARTICLES);
				for (int i = 0; i < articles.length(); i++) {
					JSONObject c = articles.getJSONObject(i);
					String type = c.getString(TAG_TYPE);
					if (type.equals("adm1st") && sw_adm1st) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_adm1st = false;
						articleList.add(map);
					}
					if (type.equals("adm2nd") && sw_adm2nd) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_adm2nd = false;
						articleList.add(map);
					}
					if (type.equals("adm3rd") && sw_adm3rd) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_adm3rd = false;
						articleList.add(map);
					}
					if (type.equals("airport") && sw_airport) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_airport = false;
						articleList.add(map);
					}
					if (type.equals("city(pop)") && sw_popcity) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_popcity = false;
						articleList.add(map);
					}
					if (type.equals("city") && sw_city) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_city = false;
						articleList.add(map);
					}
					if (type.equals("country") && sw_country) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_country = false;
						articleList.add(map);
					}
					if (type.equals("edu") && sw_edu) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_edu = false;
						articleList.add(map);
					}
					if (type.equals("event") && sw_event) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_event = false;
						articleList.add(map);
					}
					if (type.equals("forest") && sw_forest) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_forest = false;
						articleList.add(map);
					}
					if (type.equals("glacier") && sw_glacier) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_glacier = false;
						articleList.add(map);
					}
					if (type.equals("isle") && sw_isle) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_isle = false;
						articleList.add(map);
					}
					if (type.equals("landmark") && sw_landmark) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_landmark = false;
						articleList.add(map);
					}
					if (type.equals("mountain") && sw_mountain) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_mountain = false;
						articleList.add(map);
					}
					if (type.equals("pass") && sw_pass) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_pass = false;
						articleList.add(map);
					}
					if (type.equals("railwaystation") && sw_railwaystation) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_railwaystation = false;
						articleList.add(map);
					}
					if (type.equals("river") && sw_river) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_river = false;
						articleList.add(map);
					}
					if (type.equals("satellite") && sw_satellite) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_satellite = false;
						articleList.add(map);
					}
					if (type.equals("waterbody") && sw_waterbody) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_waterbody = false;
						articleList.add(map);
					}
					if (type.equals("camera") && sw_camera) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_TYPE, type);
						sw_camera = false;
						articleList.add(map);
					}
				}

			} catch (JSONException e) {
				// Έλεγχος αν έγινε ομαλά η μετάφραση
				e.printStackTrace();
			}
			// Επιστροφή αντικειμένου ArrayList
			return articleList;

		}
	}
}