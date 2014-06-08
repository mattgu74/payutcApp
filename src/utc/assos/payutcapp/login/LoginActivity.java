package utc.assos.payutcapp.login;


import java.util.concurrent.ExecutionException;

import utc.assos.payutcapp.communication.CasConnexion;
import utc.assos.payutcapp.communication.LoginCas;
import utc.assos.payutcapp.communication.NotificationAddDevice;
import utc.assos.payutcapp.communication.RequestTicket;
import utc.assos.payutcapp.GcmManager;
import utc.assos.payutcapp.MainActivity;
import utc.assos.payutcapp.R;
import utc.assos.payutcapp.prefs.PrefsManager;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	GcmManager gcm_manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		String login = PrefsManager.getLogin(this);
		String password = PrefsManager.getLogin(this);
		
		final EditText edit_login = (EditText) findViewById(R.id.edit_login);
		final EditText edit_pass = (EditText) findViewById(R.id.edit_password);
		
		Button connect = (Button) findViewById(R.id.connect);
		
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connexion(edit_login.getText().toString(), edit_pass.getText().toString());
			}
		});

	}
	
	public void connexion(String login, String password){
		if(login != null && password != null){
			String tbt = ConnexionCas(login, password);
			if (tbt!= null && tbt.startsWith("TGT")){
				String ticket = getTicket(tbt, getResources().getString(R.string.service));
				if (ticket!=null && ticket.startsWith("ST")){
					loginCas(ticket, getResources().getString(R.string.service));
					String regid = registration();
					if (regid != null){
						//TODO notification
						notifications(regid);
						
						PrefsManager.setLogin(this, login);
						PrefsManager.setPassword(this, password);
						
						Intent intent = new Intent(this, MainActivity.class);
						startActivity(intent);
						this.finish();
					}else{
						failAuthentification();
					}
					
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
		Toast.makeText(this, "Erreur d'authentification", Toast.LENGTH_SHORT).show();
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
	
	public String loginCas(String ticket, String service){
		try {
			LoginCas lc = new LoginCas();
			return lc.execute(this.getResources().getString(R.string.url)+"MYACCOUNT/",ticket, service).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String registration(){
		
		String regid;
		gcm_manager = new GcmManager(this);
		
		if (gcm_manager.checkPlayServices()) {
            regid = gcm_manager.getRegistrationId(this);
            if (regid.isEmpty()) {
            	gcm_manager.registerInBackground();
            } else {
            	return regid;
            }
        } else {
            return null;
        }
		return regid;
	}
	
	public String notifications(String token){
		try {
			NotificationAddDevice no = new NotificationAddDevice();
			return no.execute(this.getResources().getString(R.string.url), token).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    gcm_manager.checkPlayServices();
	}
}
