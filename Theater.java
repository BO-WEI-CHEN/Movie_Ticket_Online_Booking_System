import java.io.*;
import org.json.*;

public class Theater {

	private JSONArray jsonArray;
	private int NumMovie;

	// constructor get JSONContext
	public Theater(String path) {

		String fname = "movie_info";
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
		NumMovie = jsonArray.length();
	}

	public int NumMovie() {
		return NumMovie;
	}

	public int NumSession(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		String movieSessions = jsonObject.getString("time");
		String[] tmp = movieSessions.split("、");
		return tmp.length;
	}

	public int[] getSessionInt(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		String movieSessions = jsonObject.getString("time");
		String[] tmp1 = movieSessions.split(" ");
		String[] tmp = tmp1[1].split("、");
		int[] time;
		time = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			String[] j = tmp[i].split("：");
			time[i] = Integer.parseInt(j[0]) * 60 + Integer.parseInt(j[1]);
		}
		return time;
	}

	public String[] getSessionString(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		String movieSessions = jsonObject.getString("time");
		String[] tmp1 = movieSessions.split(" ");
		return tmp1[1].split("、");
	}

	public String getSession(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		return jsonObject.getString("time");
	}

	public String getHall(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		return jsonObject.getString("hall");
	}

	public boolean roomIsBig(int movie) {
		String hall = getHall(movie);
		if (hall.equals(" 武當 ") || hall.equals(" 少林 ") || hall.equals(" 華山 "))
			return true;
		else
			return false;
	}

	public int roomSize(int movie) {
		String hall = getHall(movie);
		if (hall.equals(" 武當 ") || hall.equals(" 少林 ") || hall.equals(" 華山 "))
			return 495;
		else
			return 144;
	}

	public String getMovieID(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		return jsonObject.getString("id");
	}

	public String getMovieName(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		return jsonObject.getString("movie");
	}

	public String getMovieURL(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		return jsonObject.getString("url");
	}

	public String getMovieC(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		return jsonObject.getString("classification");
	}

	public int getMovieLegalAge(int movie) {
		if (getMovieC(movie).equals("限制"))
			return 18;
		else if (getMovieC(movie).equals("輔導"))
			return 15;
		else if (getMovieC(movie).equals("保護"))
			return 6;
		else
			return 0;
	}

	public String getMovieD(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		return jsonObject.getString("descri");
	}

	public int getMoviePeriod(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		String str = jsonObject.getString("infor");
		String[] str1 = str.split("分");
		String[] str2 = str1[0].split("：");
		return Integer.parseInt(str2[1]);
	}

	public double getMovieScore(int movie) {
		JSONObject jsonObject = jsonArray.getJSONObject(movie);
		String[] tmp = jsonObject.getString("score").split("/");
		return Double.parseDouble(tmp[0]);
	}
}
