package utc.assos.payutcapp.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class LoginApp extends AsyncTask<String, Integer, String> {

	@Override
	protected String doInBackground(String... params) {
		
		HttpClient httpclient = PermanentHttpClient.getInstance().getNewHttpClient();

		// On cr�� notre ent�te
		HttpPost httppost = new HttpPost(params[0] + "loginapp");
		httppost.setHeaders(PermanentHttpClient.getInstance().getCookie());
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("key", params[1]
				.toString()));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response;
			response = httpclient.execute(httppost);
			PermanentHttpClient.getInstance().setCookie(response.getHeaders("Cookie"));
			// get the tbt
			String rep = EntityUtils.toString(response.getEntity());
//			Log.d("rep", rep);
			return rep;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
