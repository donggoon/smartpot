package inandout.pliend.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by DK on 2016-10-13.
 */
public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();

	private RequestQueue mRequestQueue;

	private static AppController mInstance;

	private boolean isPlantRegister = false;
	private boolean isBluetooth = false;
	private String plantName;
	private String plantBirth;
	private String plantType;
	private String plantLevel;
	private String machineName;
	private String userEmail;
	private boolean isPlantTabSelected = false;
	private boolean isMachineTabSelected = false;
	private boolean isQuestTabSelected = false;
	private boolean isAnalyzeTabSelected = false;
	private int currentHumi;
	private int currentLight;
	private int currentTemp;
	private int statusType = 1;

	public boolean getIsPlantRegister() { return isPlantRegister; }
	public void setIsPlantRegister(boolean isPlantRegister) { this.isPlantRegister = isPlantRegister; }

	public boolean getIsBluetooth() { return isBluetooth; }
	public void setIsBluetooth(boolean isBluetooth) { this.isBluetooth = isBluetooth; }

	public String getPlantName() { return plantName; }
	public void setPlantName(String plantName) { this.plantName = plantName; }

	public String getPlantBirth() { return plantBirth; }
	public void setPlantBirth(String plantBirth) { this.plantBirth = plantBirth; }

	public String getPlantType() { return plantType; }
	public void setPlantType(String plantType) { this.plantType = plantType; }

	public String getPlantLevel() { return plantLevel; }
	public void setPlantLevel(String plantLevel) { this.plantLevel = plantLevel; }

	public String getMachineName() { return machineName; }
	public void setMachineName(String machineName) { this.machineName = machineName; }

	public String getUserEmail() { return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

	public boolean getIsPlantTabSelected() { return isPlantTabSelected; }
	public void setIsPlantTabSelected(boolean isPlantTabSelected) { this.isPlantTabSelected = isPlantTabSelected; }

	public boolean getIsQuestTabSelected() { return isQuestTabSelected; }
	public void setIsQuestTabSelected(boolean isQuestTabSelected) { this.isQuestTabSelected = isQuestTabSelected; }

	public int getStatusType() { return statusType; }
	public void setStatusType(int statusType) { this.statusType = statusType; }

	public int getCurrentHumi() { return currentHumi; }
	public void setCurrentHumi(int currentHumi) { this.currentHumi = currentHumi; }

	public int getCurrentLight() { return currentLight; }
	public void setCurrentLight(int currentLight) { this.currentLight = currentLight; }

	public int getCurrentTemp() { return currentTemp; }
	public void setCurrentTemp(int currentTemp) { this.currentHumi = currentTemp; }

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}