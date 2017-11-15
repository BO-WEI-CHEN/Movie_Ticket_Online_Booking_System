import java.io.*;
import org.json.*;

public class smallRoom {

	private JSONArray jsonArray;
	private int size;
	private Boolean[] occupied;
	private String[] row;
	private int[] seatNum;

	public smallRoom(String path) {

		String fname = "small_room";
		BufferedReader br = null;
		String allString = "";

		try {
			FileInputStream FIS = new FileInputStream(path + "/" + fname + ".json");
			InputStreamReader ISR = new InputStreamReader(FIS, "UTF-8");
			br = new BufferedReader(ISR);
			String tempString = null;
			while ((tempString = br.readLine()) != null) {
				allString += tempString;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		jsonArray = new JSONArray(allString);
		size = jsonArray.length();

		occupied = new Boolean[size];
		row = new String[size];
		seatNum = new int[size];
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			occupied[i] = jsonObject.getBoolean("occupied");
			row[i] = jsonObject.getString("row");
			seatNum[i] = jsonObject.getInt("seatNum");
		}
	}

	public int index(String row, int seatNum) {
		return (int) (row.charAt(0) - 65) * 16 + seatNum - 1;
	}

	public Boolean isOccupied(int index) {
		return occupied[index];
	}

	public void setOccupied(int index) {
		occupied[index] = !occupied[index];
	}

	public String getRow(int index) {
		return row[index];
	}

	public int getSeatNum(int index) {
		return seatNum[index];
	}

	public int NumAvailable() {
		int n = 0;
		for (int i = 0; i < size; i++)
			if (occupied[i] == false)
				n++;
		return n;
	}
}
