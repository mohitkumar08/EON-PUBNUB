package bit.pubnubeon;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.yayandroid.locationmanager.LocationBaseActivity;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.LogType;
import com.yayandroid.locationmanager.constants.ProviderType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class MainActivity extends LocationBaseActivity {

    protected Pubnub pubnub;
    String roomName="pubnub_map1";
    Timer timer;
    TimerTask timerTask;
    @Override
    public LocationConfiguration getLocationConfiguration() {
        //Log.e("location", "call");
        return new LocationConfiguration()
                .keepTracking(true)
                .askForGooglePlayServices(true)
                .setMinAccuracy(200.0f)
                .setWaitPeriod(ProviderType.GOOGLE_PLAY_SERVICES, 5 * 1000)
                .setWaitPeriod(ProviderType.GPS, 10 * 1000)
                .setWaitPeriod(ProviderType.NETWORK, 5 * 1000)
                .setGPSMessage("Would you mind to turn GPS on?")
                .setRationalMessage("Gimme the permission!");
    }

    @Override
    public void onLocationChanged(Location location) {
       /* setText(location);*/
        //   Log.e("location", String.valueOf(location.getLatitude()));
        JSONObject object=new JSONObject();
        try {

            JSONArray array=new JSONArray();
            array.put(location.getLatitude());
            array.put(location.getLongitude());
            object.put("latlng",array);
           publish(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationFailed(int failType) {
        //Log.e("location", String.valueOf(failType));

        switch (failType) {
            case FailType.PERMISSION_DENIED: {
                Log.e("loca","Couldn't get location, because user didn't give permission!");

            }
            case FailType.GP_SERVICES_NOT_AVAILABLE:
            case FailType.GP_SERVICES_CONNECTION_FAIL: {
                Log.e("loca", "Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                Log.e("loca", "Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.TIMEOUT: {
                Log.e("loca", "Couldn't get location, and timeout!");
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //LocationManager.setLogType(LogType.GENERAL);
        LocationManager.setLogType(LogType.IMPORTANT);
        getLocation();
        initPubNub();
        startTimer();
    }
    public void initPubNub() {

        Log.e("initPubNub ", "called");

        pubnub = new Pubnub("pub-c-99a67f10-32c0-45b2-ab4e-bdc901359dfe","sub-c-68dce294-f732-11e5-b552-02ee2ddab7fe", false);
        pubnub.setHeartbeat(140, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.e("log", "heartbeat received");
            }

            @Override
            public void errorCallback(String channel, Object message) {
                Log.e("Constants.LOGT", "error receiving heartbeat");
                pubnub.disconnectAndResubscribe();
            }
        });

        pubnub.setHeartbeatInterval(120);
        this.pubnub.setHeartbeatInterval(30);
        //pubnub.setNonSubscribeTimeout(60);
        pubnub.setResumeOnReconnect(true);
        //  pubnub.setMaxRetries(500);
        // pubnub.setSubscribeTimeout(20000);
        pubnub.setUUID("12334556"); //You can set a custom UUID or let the SDK generate one for you

        subscribe();
    }



    private void subscribe() {
        try {
            Callback subscribeCallback = new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.e("message ", "" + message);

MainActivity.this.runOnUiThread(new Runnable() {
    @Override
    public void run() {

    }
});
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    super.errorCallback(channel, error);
                }
            };
            this.pubnub.subscribe(this.roomName, subscribeCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     */
    @Override
    protected void onRestart() {
        try {
            super.onRestart();
            if (this.pubnub == null) {
                initPubNub();
            } else {
                subscribe();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void publish(JSONObject data) {
        try {


            PnGcmMessage gcmMessage = new PnGcmMessage();
            gcmMessage.setData(data);

            this.pubnub.publish(this.roomName, gcmMessage, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.e("successCallback", message.toString());
                    super.successCallback(channel, message);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    //BELogUtils.debug(constant.TAG, "Error: " + error.toString());
                    Log.e("errorCallback", error.toString());
                    super.errorCallback(channel, error);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 500, 1000); //

    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                JSONObject object=new JSONObject();

                try {

                    object.put("lan","abc");
                    object.put("lot","cac");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //publish(object);
            }

        };

    }

}

