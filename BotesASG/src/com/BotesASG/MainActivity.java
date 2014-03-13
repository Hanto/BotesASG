package com.BotesASG;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	private WebView webView;
	private String cuentaUsuario = "";
	private String deviceIMEI = "";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		//Buscamos la cuenta del usuario:
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
		for (Account account : accounts) 
		{
		    if (emailPattern.matcher(account.name).matches()) 
		    {	cuentaUsuario = account.name;   }
		}
		
		//Buscamos el IMEI que identifica de forma unica a este dispositivo:
		//TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		//deviceIMEI = telephonyManager.getDeviceId();
		deviceIMEI = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		
		
		//Creamos el WebView:
		webView = (WebView) findViewById(R.id.webview);
		
		webView.loadUrl("http://www.cartelesloteriadigital.com/tablet/jorge/tablet.php?User="+cuentaUsuario+"&IMEI="+deviceIMEI);
		
		//Hacemos que se refresque cada 25, minuto, como el tiempo se pasa en milisegundos, hay que multiplicar los minutos por 60 y 1000
		new Timer().schedule(new TimerTask()
			{
				@Override
				public void run() 
				{
					webView.reload();
				}
			}, 25*60*1000, 25*60*1000);
		
		//Interceptamos el mensaje de error para que no salga la direccion de la pagina web que estamos accediendo
		webView.setWebViewClient(new WebViewClient() 
		{
		    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) 
		    {
		        webView.loadUrl("User="+cuentaUsuario+":IMEI="+deviceIMEI);
		    }
		});
		
		//Definimos los Settings:
		WebSettings settings = webView.getSettings();
		
		settings.setJavaScriptEnabled(true);		//Activar JavasSpript
		settings.setUseWideViewPort(true);			//PantallaCompleta
		settings.setLoadWithOverviewMode(true);
		
		//en el Manifest para forzar full screen sin barras de ningun tipo hay que poner lo siguiente:
		//android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">
				
		//Mientras este esta aplicacion funcionando no apagar el dispositivo ni disminuir el brillo
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
