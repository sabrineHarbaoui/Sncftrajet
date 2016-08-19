package com.ov.exercice.sncf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * Consigne:
 * Enregistrez vous sur https://data.sncf.com/api (http://www.navitia.io/register/) pour récupérer une clé d'API.
 * Ce code va servir à récupérer les horaires de trains au départ de montparnasse.
 * Puis ensuite les afficher sous forme Heure : destination
 * Vous utiliserez votre clé dans l'url d'appel de l'API.
 * Il ne respecte pas les standards et doit être nettoyé puis refactoré pour
 * être réutilisable et compréhensible.
 */

public class GetTrajets {

	HttpURLConnection mConnection = null;

	public void initialiser(){
		try {
			mConnection = (HttpURLConnection) (new URL("https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:OCE:SA:87391003/departures?datetime=20160729T150423")).openConnection();
			mConnection.setRequestProperty("Authorization", "Basic " + (Base64.getUrlEncoder().encodeToString("374817c5-d83d-4eb0-a42e-2ad1811d6794:".getBytes())));
			mConnection.setRequestMethod("GET");
			mConnection.setRequestProperty("Content-length", "0");
			mConnection.setUseCaches(false);
			mConnection.setAllowUserInteraction(false);	
			if(mConnection == null) System.out.println("Échec a établir une connexion!!!!!");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int connecter(){
		initialiser();
		int lResult = 0;
		if(mConnection == null) return 0;
		try {
			mConnection.connect();
			lResult = mConnection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lResult;
	}

	public void afficher(){
		try {
			afficherHoraires(mConnection.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void afficherHoraires(InputStream iInputStream){
		if(iInputStream == null) return;
		BufferedReader lReader = new BufferedReader(new InputStreamReader(iInputStream));
		StringBuilder lBuilder = new StringBuilder();
		String lLine;
		try {
			while ((lLine = lReader.readLine()) != null) {
				lBuilder.append(lLine+"\n");
			}
			lReader.close();
			String lJson = lBuilder.toString();
			JSONObject jSONObject = (JSONObject)(new JSONParser()).parse(lJson);
			JSONArray lDepartures = (JSONArray) (jSONObject.get("departures"));
			System.out.println("Prochains départs de Montparnasse :");
			int lSize = lDepartures.size()-1;
			for (int lIndex = 0 ; lIndex < lSize ; ++lIndex) {
				JSONObject lJdate = (JSONObject)((JSONObject)lDepartures.get(lIndex)).get("stop_date_time");
				JSONObject lJroute = ((JSONObject)((JSONObject)lDepartures.get(lIndex)).get("route"));
				JSONObject lJline = (JSONObject)lJroute.get("line");
				String lVar = lJdate.get("departure_date_time")+" : "+lJline.get("name");
				System.out.println(lVar);
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getResult(){
		int lResult = connecter();
		switch (lResult) {
		case 200:
		case 201:
			afficher();
		}	
	}

	public static void main(String[] args) {
		GetTrajets lTrajets = new GetTrajets();
		lTrajets.getResult();
	}
}
