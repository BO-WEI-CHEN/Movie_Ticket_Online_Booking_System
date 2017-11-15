import java.io.*;
import org.json.*;

public class userData {

	private JSONArray jsonArray;
	int size;

	public userData(String path) {

		String fname = "user";
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
	}

	public String getName(int index) {
		JSONObject jsonObject = jsonArray.getJSONObject(index);
		return jsonObject.getString("name");
	}

	public int getAge(int index) {
		JSONObject jsonObject = jsonArray.getJSONObject(index);
		return jsonObject.getInt("age");
	}

	public int getSize() {
		return size;
	}
}