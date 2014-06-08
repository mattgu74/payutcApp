package utc.assos.payutcapp.login;

import java.util.concurrent.ExecutionException;

import utc.assos.payutcapp.communication.CasConnexion;
import utc.assos.payutcapp.communication.RequestTicket;
import utc.assos.payutcapp.R;
import utc.assos.payutcapp.prefs.PrefsManager;
import android.app.Activity;
import android.os.Bundle;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		String login = PrefsManager.getLogin(this);
		String password = PrefsManager.getLogin(this);
		
		if(login != null && password != null){
			String tbt = ConnexionCas(login, password);
			if (tbt!= null && tbt.startsWith("TGT")){
				String ticket = getTicket(tbt, getResources().getString(R.string.service));
				if (ticket!=null && ticket.startsWith("ST")){
					//TODO registration
					
				}else{
					failAuthentification();
				}
				
			}else{
				failAuthentification();
			}
		}
	
	}
	
	public void failAuthentification(){
		PrefsManager.setLogin(this, null);
		PrefsManager.setPassword(this, null);
	}
	
	public void registerInfo(String login, String password){
		PrefsManager.setLogin(this, login);
		PrefsManager.setPassword(this, password);
	}
	
	public String ConnexionCas(String user, String pass){
		try {
			CasConnexion casConnexion = new CasConnexion(this);
			return casConnexion.execute(user, pass).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getTicket(String tbt, String service){
		try {
			RequestTicket rt = new RequestTicket(this);
			return rt.execute(tbt, service).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
