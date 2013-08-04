package hu.atyin.indprobafeladat.network;

import hu.atyin.indprobafeladat.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkManager implements HttpRequestValues {
	
	public static String JSessionId = null;
	
	private HttpClient httpClient = null;
	private HttpResponse response = null;
	private JSONObject json = null;
	private InputStream is = null;
	private HttpPost post = null;
	private HttpGet get = null;
	private String resValue = null;
	
	public NetworkManager() {

	}
	
	/**
	 * JSON adatok lekérése bejelentkezéskor
	 * 
	 * @param context
	 * @param loginId
	 * @param password
	 * @return
	 */
	public String getJSONLoginDatas(Context context, final String loginId, final String password) {
		if(NetworkManager.isNetworkConnected(context)) {
			httpClient = new DefaultHttpClient();
			json = new JSONObject();
		
			try {
				post = new HttpPost(POST_LOGIN_URL);
				json.put("loginId", loginId);
				json.put("password", password);
				
				Log.d("JSON",json.toString());
				
				StringEntity se = new StringEntity(json.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = httpClient.execute(post);
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					is = response.getEntity().getContent();
					StringBuilder sb = new StringBuilder();
					int ch;
					while((ch = is.read()) != -1) {
						sb.append((char)ch);
					}
					
					resValue = sb.toString();
					logHeaders(response);
					getSessionId(response);
					Log.d("SessionID", JSessionId);
					
					return resValue;	
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					return "Error: Server error!";
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
					return "Error: Access denied!";
				}
				else {
					return null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}	
		}
		
		return "Error: Network error!";
	}
	
	/**
	 * Kijelentkezés
	 * 
	 * @param context
	 * @return
	 */
	public String logout(Context context) {
		if(NetworkManager.isNetworkConnected(context)) {
			httpClient = new DefaultHttpClient();
			
			try {
				get = new HttpGet(GET_LOGOUT_URL);
				get.setHeader("Cookie", "JSESSIONID=" + JSessionId);
				Log.d("SessionID before Logout", JSessionId);
				response = httpClient.execute(get);
				JSessionId = null;
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					logHeaders(response);
					return "OK";
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					return "Error: Server error!";
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
					return "Error: Access denied!";
				}
				else {
					return null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "Error: Network error!";
	}
	
	/**
	 * JSON adatok lekérése számla listához
	 * 
	 * @param context
	 * @return
	 */
	public String getJSONAccounts(Context context) {
		if(NetworkManager.isNetworkConnected(context)) {
			httpClient = new DefaultHttpClient();
		
				try {
					get = new HttpGet(GET_ACCOUNTS_URL);
					get.setHeader("Cookie", "JSESSIONID=" + JSessionId);
					Log.d("SessionID before GetAccounts", JSessionId);
					response = httpClient.execute(get);
					
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						is = response.getEntity().getContent();
						StringBuilder sb = new StringBuilder();
						int ch;
						while((ch = is.read()) != -1) {
							sb.append((char)ch);
						}
						
						resValue = sb.toString();
						logHeaders(response);
						Log.d("SessionID after GetAccounts", JSessionId);
						Log.d("resValue at Accounts", resValue);
						
						return resValue;
					}
					else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
						return "Error: Server error!";
					}
					else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
						return "Error: Access denied!";
					}
					else {
						return null;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if(is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		}
		
		return "Error: Network error!";
	}
	
	/**
	 * JSON adatok lekérése átutaláshoz
	 * 
	 * @param context
	 * @param sourceAccount
	 * @param targetAccount
	 * @param amount
	 * @param description
	 * @return
	 */
	public String doTransfer(Context context, String sourceAccount, String targetAccount, int amount, String description) {
		if(NetworkManager.isNetworkConnected(context)) {
			httpClient = new DefaultHttpClient();
			json = new JSONObject();
			
			try {
				post = new HttpPost(POST_TRANSFER_URL);
				post.setHeader("Cookie", "JSESSIONID=" + JSessionId);
				Log.d("SessionID before Transfer", JSessionId);
				
				json.put("sourceAccount", sourceAccount);
				json.put("targetAccount", targetAccount);
				json.put("amount", amount);
				json.put("description", description);
				
				Log.d("JSON",json.toString());
				
				StringEntity se = new StringEntity(json.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = httpClient.execute(post);
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					is = response.getEntity().getContent();
					StringBuilder sb = new StringBuilder();
					int ch;
					while((ch = is.read()) != -1) {
						sb.append((char)ch);
					}
					
					resValue = sb.toString();
					logHeaders(response);
					Log.d("SessionID", JSessionId);
					Log.d("resValue at Transfer", resValue);
					
					return resValue;
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					return "Error: Server error!";
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
					return "Error: Access denied!";
				}
				else {
					return null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return "Error: Network error!";
	}
	
	/**
	 * JSON adatok lekérése számlatörténet-hez
	 * 
	 * @param context
	 * @param dateFrom
	 * @param dateTo
	 * @param accountNr
	 * @return
	 */
	public String getTransactions(Context context, String dateFrom, String dateTo, String accountNr) {
		if(NetworkManager.isNetworkConnected(context)) {
			httpClient = new DefaultHttpClient();
			
			try {
				String dFrom = URLEncoder.encode(dateFrom, "UTF-8");
				String dTo = URLEncoder.encode(dateTo, "UTF-8");
				String accNr = URLEncoder.encode(accountNr, "UTF-8");
				
				String url = GET_TRANSACTIONS_URL + "?from=" + dFrom + "&to=" + dTo + "&account=" + accNr;
				get = new HttpGet(url);
				get.setHeader("Cookie", "JSESSIONID=" + JSessionId);
				Log.d("SessionID before getTransactions", JSessionId);
				Log.d("GET URL", url);
				
				response = httpClient.execute(get);
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					is = response.getEntity().getContent();
					StringBuilder sb = new StringBuilder();
					int ch;
					while((ch = is.read()) != -1) {
						sb.append((char)ch);
					}
					
					resValue = sb.toString();
					logHeaders(response);
					Log.d("SessionID", JSessionId);
					Log.d("resValue at getTransactions", resValue);
					
					return resValue;
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					return "Error: Server error!";
				}
				else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
					return "Error: Access denied!";
				}
				else {
					return null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return "Error: Network error!";
	}
	
	/**
	 * Response header log-olása
	 * 
	 * @param resp
	 */
	public void logHeaders(HttpResponse resp) {
		Header [] headers = response.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			Log.d("HEADER name ["+i+"]", headers[i].getName());
			Log.d("HEADER value ["+i+"]", headers[i].getValue());
		}
	}
	
	/**
	 * JSESSIONID lekérése
	 * 
	 * @param resp
	 */
	public void getSessionId(HttpResponse resp) {
		Header header = resp.getFirstHeader("Set-Cookie");
		String cookieValue = header.getValue();
		Log.d("HEADER", cookieValue);
		
		if(cookieValue.contains("JSESSIONID")) {
			int index = cookieValue.indexOf("JSESSIONID=");
			int endIndex = cookieValue.indexOf(";", index);
			String sessionId = cookieValue.substring(index + "JSESSIONID=".length(), endIndex);
			if(sessionId != null) {
				JSessionId = sessionId;
			}
		}
	}
	
	/**
	 * Hibakód lekérése
	 * 
	 * @param json
	 * @return
	 */
	public static int getErrorCode(JSONObject json) {
		int errorCode = -1;
		try {
			errorCode = json.getInt("code");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return errorCode;
	}
	
	/**
	 * Hibaüzenet lekérése
	 * 
	 * @param json
	 * @param context
	 * @return
	 */
	public static String getErrorMessage(JSONObject json, Context context) {
		int errorCode = -1;
		errorCode = getErrorCode(json);

		switch (errorCode) {
		case 0:
			return context.getString(R.string.error_code_zero);			
		
		case 1:
			return context.getString(R.string.error_code_one);
			
		case 2:
			return context.getString(R.string.error_code_two);
			
		case 3:
			return context.getString(R.string.error_code_three);
			
		case 4:
			return context.getString(R.string.error_code_four);
			
		case 5:
			return context.getString(R.string.error_code_five);
			
		case 6:
			return context.getString(R.string.error_code_six);
			
		case 7:
			return context.getString(R.string.error_code_seven);
		
		case 8:
			return context.getString(R.string.error_code_eight);
			
		default:
			return null;
		}
	}
	
	/**
	 * Megvizsgálja, hogy van-e internet hozzáférés
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Hálózati hiba dialógusablak
	 * 
	 * @param context
	 * @param message
	 */
	public static void showNetworkErrorAlert(Context context, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(R.string.alert_network_error_title);
		if(message.equals("Error: Network error!")) {
			alert.setMessage(R.string.no_internet_message);
		}
		else if(message.equals("Error: Server error!")) {
			alert.setMessage(R.string.server_error_message);
		}
		else if(message.equals("Error: Access denied!")) {
			alert.setMessage(R.string.access_denied_message);
		}
		else {
			alert.setMessage(R.string.unknown_error_message);
		}
		
		alert.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		alert.show();
	}
}
