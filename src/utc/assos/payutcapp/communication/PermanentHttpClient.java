package utc.assos.payutcapp.communication;

import java.security.KeyStore;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class PermanentHttpClient {

	static private PermanentHttpClient instance;
	
	private HttpClient httpClient;
	private Header[] headers;
	
	private PermanentHttpClient() {
		httpClient = null;
		// TODO Auto-generated constructor stub
	}
	
	static public PermanentHttpClient getInstance(){
		if (instance == null)
			instance = new PermanentHttpClient();
		
		return instance;
	}
	
	public HttpClient getNewHttpClient() {
		if (httpClient == null){
			try {
				KeyStore trustStore = KeyStore.getInstance(KeyStore
						.getDefaultType());
				trustStore.load(null, null);
	
				SSLSocketFactory sf = new AdditionalKeyStoresSSLSocketFactory(
						trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	
				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));
	
				ClientConnectionManager ccm = new ThreadSafeClientConnManager(
						params, registry);
	
				return new DefaultHttpClient(ccm, params);
			} catch (Exception e) {
				return new DefaultHttpClient();
			}
		}else{
			return httpClient;
		}
	}

	public void setCookie(Header[] headers) {
		// TODO Auto-generated method stub
		this.headers = headers;
	}
	
	public Header[] getCookie() {
		// TODO Auto-generated method stub
		return this.headers;
	}
}
