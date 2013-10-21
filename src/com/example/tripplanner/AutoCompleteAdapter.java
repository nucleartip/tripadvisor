package com.example.tripplanner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable{

	private ArrayList<LocationDetail> matches;
	private ArrayList<String> sortedMatches;
	private Context context;
	private Location currentLocation;
	public AutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
		this.context = context;
		LocationManager manager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
		try{
			// Lets focus only on passive provide, as user wont wait till we get the fix for 
			// user location through slow provider such as Network,Gps
			if(manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
				currentLocation = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public int getCount() {
		return sortedMatches.size();
	}

	@Override
	public String getItem(int index) {
		return sortedMatches.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					matches = getMatches(constraint.toString());
					sortedMatches = getSortedMatches();
					// Sort it based on user current distance


					// Assign the data to the FilterResults
					filterResults.values = sortedMatches;
					filterResults.count = sortedMatches.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				}
				else {
					notifyDataSetInvalidated();
				}
			}};
			return filter;
	}
	private ArrayList<LocationDetail> getMatches(String input) {
		ArrayList<LocationDetail> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder result = new StringBuilder();
		try {
			String uri = "http://pre.dev.goeuro.de:12345/api/v1/suggest/position/en/name/" + input;            
			URL url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				result.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return resultList;
		} catch (IOException e) {
			e.printStackTrace();
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject obj = new JSONObject(result.toString());
			JSONArray array = obj.getJSONArray("results");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<LocationDetail>();
			for (int i = 0; i < array.length(); i++) {
				LocationDetail detail = new LocationDetail();
				JSONObject data = array.getJSONObject(i);
				detail.name = data.getString("name");
				if(currentLocation != null){
					JSONObject locationData = data.getJSONObject("geo_position");
					double latitude = locationData.getDouble("latitude");
					double longitude = locationData.getDouble("longitude");
					Location destination = new Location("destinatio");
					destination.setLatitude(latitude);
					destination.setLongitude(longitude);
					detail.distance = currentLocation.distanceTo(destination);
				}else{
					// Consider all equal, as location quick fix was not available
					detail.distance = 0;
				}
				resultList.add(detail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;
	}

	private ArrayList<String> getSortedMatches(){
		ArrayList<String> sortedMatches = new ArrayList<String>(matches.size());
		Collections.sort(matches);
		for(LocationDetail detail : matches){
			sortedMatches.add(detail.name);
		}
		return sortedMatches;
	}

	private class LocationDetail implements Comparable<LocationDetail>{
		String name;
		float distance;
		@Override
		public int compareTo(LocationDetail obj) {
			// TODO Auto-generated method stub
			if(this.distance > obj.distance)
				return 1;
			if(this.distance == obj.distance)
				return 0;
			else
				return -1;

		}


	}
}
