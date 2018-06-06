package semproject.nevent.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aayush on 3/9/2017.
 */

public class CheckinvitationsRequest extends StringRequest {
    final String STRING_TAG= "RecyclerRequest";
    private static final String REGISTER_REQUEST_URL = "http://avsadh96.000webhostapp.com/Invitestatus.php";
    private Map<String, String> params;//maps key to value dont have fixed size any number of values can be stored.

    public CheckinvitationsRequest(String username, String status, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data

        params = new HashMap<>();
        params.put("username",username);
        params.put("status",status);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
