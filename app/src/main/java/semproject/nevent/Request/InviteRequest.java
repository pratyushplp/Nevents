package semproject.nevent.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 8/3/2017.
 */

public class InviteRequest extends StringRequest {
    final String STRING_TAG= "InviteRequest";
    private static final String REGISTER_REQUEST_URL = "http://avsadh96.000webhostapp.com/Extract.php";
    private Map<String, String> params;//maps key to value dont have fixed size any number of values can be stored.

    public InviteRequest(String username,String invitedname, String eventID, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data
        //Log.e(STRING_TAG,username);
        params = new HashMap<>();
        params.put("username",username);
        params.put("eventId",eventID);
        params.put("check","invitefriend");
        params.put("invitedname",invitedname);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}