package semproject.nevent.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 6/13/2017.
 */

public class TimeRequest extends StringRequest {
    final String STRING_TAG= "TimeRequest";
    private static final String REGISTER_REQUEST_URL = "http://api.geonames.org/timezoneJSON?lat=27.7167&lng=85.3167&username=nevent";
    private Map<String, String> params;//maps key to value dont have fixed size any number of values can be stored.

    public TimeRequest(Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data
        Log.e(STRING_TAG,"processing");

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}