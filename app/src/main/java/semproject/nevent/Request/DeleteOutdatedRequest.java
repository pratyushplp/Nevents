package semproject.nevent.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 6/14/2017.
 */

public class DeleteOutdatedRequest extends StringRequest {
    final String STRING_TAG= "DeleteOutRequest";
    private static final String REGISTER_REQUEST_URL = "http://avsadh96.000webhostapp.com/DeleteOut.php";
    private Map<String, String> params; //maps key to value dont have fixed size any number of values can be stored.

    public DeleteOutdatedRequest(Integer id,Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);// post request or get request any one can be used to transfer data
        Log.e(STRING_TAG,String.valueOf(id));
        params = new HashMap<>();
        params.put("id",String.valueOf(id));
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}