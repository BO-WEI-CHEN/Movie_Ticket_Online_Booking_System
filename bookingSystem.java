import java.util.*;

public class bookingSystem {

	private Theater Theater;
	private userData user;
	private int[] ticketID;
	private int NumAllTicket;
	private bigRoom[][] bigRoom;
	private smallRoom[][] smallRoom;
	private int Hours;
	private int Minutes;
	private String myPath;

	public bookingSystem(String path) {
		myPath = path;
		Theater = new Theater(path);
		user = new userData(path);
		NumAllTicket = 0;

		int NumBigSession = 0;
		int NumSmallSession = 0;
		int NumBig = 0;
		int NumSmall = 0;
		int Biggest = 0;
		int Smallest = 0;
		for (int i = 0; i < Theater.NumMovie(); i++) {
			if (Theater.roomIsBig(i)) {
				if (Theater.NumSession(i) > Biggest)
					Biggest = Theater.NumSession(i);
				NumBig++;
				NumBigSession += Theater.NumSession(i);
			} else {
				if (Theater.NumSession(i) > Smallest)
					Smallest = Theater.NumSession(i);
				NumSmall++;
				NumSmallSession += Theater.NumSession(i);
			}
		}

		bigRoom = new bigRoom[NumBig][Biggest];
		smallRoom = new smallRoom[NumSmall][Smallest];

		for (int i = 0; i < NumBig; i++)
			for (int j = 0; j < Biggest; j++)
				bigRoom[i][j] = new bigRoom(path);
		for (int i = 0; i < NumSmall; i++)
			for (int j = 0; j < Smallest; j++)
				smallRoom[i][j] = new smallRoom(path);

		ticketID = new int[NumBigSession * 495 + NumSmallSession * 144];
		for (int i = 0; i < ticketID.length; i++)
			ticketID[i] = 0;

		Date date = new Date();
		Hours = date.getHours();
		Minutes = date.getMinutes();
	}

	public boolean normalBooking() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("請輸入您的ID：");
		int inputID = scanner.nextInt();

		if (inputID > user.getSize() - 1) {
			System.out.println("此ID不存在。訂票失敗。");
			return false;
		}
		System.out.println(user.getName(inputID) + " 您好！");
		System.out.println("請問要看什麼電影呢？請輸入編號。");
		for (int i = 0; i < Theater.NumMovie(); i++)
			System.out.println(i + 1 + ":" + Theater.getMovieName(i));
		int MOVIE = scanner.nextInt();
		MOVIE--;

		if (user.getAge(inputID) < Theater.getMovieLegalAge(MOVIE)) {
			System.out.println("您未到合法年齡，無法觀看此電影。訂票失敗");
			return false;
		}

		System.out.println("請問要看哪個場次呢？請輸入編號。");
		String[] tmp = Theater.getSessionString(MOVIE);
		for (int i = 0; i < tmp.length; i++)
			System.out.println(i + 1 + ": " + tmp[i]);
		int SESSION = scanner.nextInt();
		SESSION--;

		String o0 = Theater.getMovieID(MOVIE);
		String o1 = Theater.getMovieName(MOVIE);
		String[] o2_tmp = Theater.getSessionString(MOVIE);
		String o2 = o2_tmp[SESSION];
		int avail;
		if (Theater.roomIsBig(MOVIE))
			avail = bigRoom[whichNum(MOVIE)][SESSION].NumAvailable();
		else
			avail = smallRoom[whichNum(MOVIE)][SESSION].NumAvailable();

		System.out.println("請問要買幾張票呢？請輸入張數。");
		System.out.println("系統預設為 1 張，目前此廳仍有 " + avail + " 個座位");
		int NumTICKET = 1;
		NumTICKET = scanner.nextInt();

		if (NumTICKET > avail) {
			System.out.println("訂票失敗。" + o1 + "於 " + o2 + " 座位數量不夠。");
			return false;
		}

		// produce the ticket and output
		System.out.println("您的電影票ID:");
		for (int i = 0; i < NumTICKET; i++) {
			ticketID[NumAllTicket] = ticketProduceNormal(inputID, MOVIE, SESSION);
			System.out.println(ticketID[NumAllTicket]);
			NumAllTicket++;
		}

		System.out.println("電影ID: " + o0);
		System.out.println(o1 + "於 " + o2 + " 目前仍有 " + (avail - NumTICKET) + " 個座位");

		return true;
	}

	public boolean refund() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("請輸入您的電影票ID：");
		int inputID = scanner.nextInt();

		int pos = ID_pos(inputID);
		if (pos == -1) {
			System.out.println("此電影票ID不存在。");
			return false;
		}

		// ID rule: userID*10^6+Movie*10^4+Session*10^3+SeatIndex
		int userID = inputID / 1000000;
		int MOVIE = (inputID % 1000000) / 10000;
		int SESSION = (inputID % 10000) / 1000;
		int SeatIndex = inputID % 1000;

		int[] Sessions = Theater.getSessionInt(MOVIE);
		int bookingSession = Sessions[SESSION];
		if (Hours * 60 + Minutes + 20 > bookingSession) {
			System.out.println("退票需於開場時間前20分鐘前。");
			return false;
		}

		NumAllTicket--;
		ticketID[pos] = ticketID[NumAllTicket];
		ticketID[NumAllTicket] = 0;
		if (Theater.roomIsBig(MOVIE))
			bigRoom[whichNum(MOVIE)][SESSION].setOccupied(SeatIndex);
		else
			smallRoom[whichNum(MOVIE)][SESSION].setOccupied(SeatIndex);
		System.out.println("退票成功，全額退款。");

		return true;
	}

	public boolean conditionalBooking() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("請輸入您的ID：");
		int inputID = scanner.nextInt();

		if (inputID > user.getSize() - 1) {
			System.out.println("此ID不存在。訂票失敗。");
			return false;
		}
		System.out.println(user.getName(inputID) + " 您好！");
		System.out.println("請問要看什麼電影呢？請輸入編號。");
		for (int i = 0; i < Theater.NumMovie(); i++)
			System.out.println(i + 1 + ":" + Theater.getMovieName(i));
		int MOVIE = scanner.nextInt();
		MOVIE--;

		if (user.getAge(inputID) < Theater.getMovieLegalAge(MOVIE)) {
			System.out.println("您未到合法年齡，無法觀看此電影。訂票失敗");
			return false;
		}

		System.out.println("請問要看哪個場次呢？請輸入編號。");
		String[] tmp = Theater.getSessionString(MOVIE);
		for (int i = 0; i < tmp.length; i++)
			System.out.println(i + 1 + ": " + tmp[i]);
		int SESSION = scanner.nextInt();
		SESSION--;

		String o0 = Theater.getMovieID(MOVIE);
		String o1 = Theater.getMovieName(MOVIE);
		String[] o2_tmp = Theater.getSessionString(MOVIE);
		String o2 = o2_tmp[SESSION];
		int avail;
		if (Theater.roomIsBig(MOVIE))
			avail = bigRoom[whichNum(MOVIE)][SESSION].NumAvailable();
		else
			avail = smallRoom[whichNum(MOVIE)][SESSION].NumAvailable();

		int func = 2;
		if (Theater.roomIsBig(MOVIE)) {
			System.out.println("請問要使用哪種模式的指定劃位？請輸入1或2。");
			System.out.println("1: 指定區域");
			System.out.println("2: 指定排數");
			func = scanner.nextInt();
		} // 大廳不能指定區域

		int NumTICKET = 1;
		if (func == 1) {
			System.out.println("請問要指定哪一區呢？請輸入編號。");
			System.out.println("1:    red, 此區仍有 " + bigRoom[whichNum(MOVIE)][SESSION].NumRed() + " 個座位");
			System.out.println("2: yellow, 此區仍有 " + bigRoom[whichNum(MOVIE)][SESSION].NumYellow() + " 個座位");
			System.out.println("3:   blue, 此區仍有 " + bigRoom[whichNum(MOVIE)][SESSION].NumBlue() + " 個座位");
			System.out.println("4:   gray, 此區仍有 " + bigRoom[whichNum(MOVIE)][SESSION].NumGray() + " 個座位");
			func = 4;
			func = scanner.nextInt(); // 區域選擇

			if (func == 1)
				avail = bigRoom[whichNum(MOVIE)][SESSION].NumRed();
			else if (func == 2)
				avail = bigRoom[whichNum(MOVIE)][SESSION].NumYellow();
			else if (func == 3)
				avail = bigRoom[whichNum(MOVIE)][SESSION].NumBlue();
			else
				avail = bigRoom[whichNum(MOVIE)][SESSION].NumGray();

			System.out.println("請問要買幾張票呢？請輸入張數。(系統預設為 1 張)");
			NumTICKET = scanner.nextInt();

			if (NumTICKET > avail) {
				System.out.println("訂票失敗。" + o1 + "於 " + o2 + " 選擇區域座位數量不夠。");
				return false;
			}
			if (NumTICKET > 1) {
				int b = 1;
				System.out.println("請問要連續座位嗎？(系統預設為「是」)");
				System.out.println("1: 是 || 2: 否");
				b = scanner.nextInt(); // 是否連續座位
				if (b == 1) {
					System.out.println("您的電影票ID:");
					int[] tmp2 = new int[NumTICKET];
					tmp2 = ticketProduceSpecific_big2(inputID, MOVIE, SESSION, func, NumTICKET);
					if (tmp2[0] == -1) {
						System.out.println("訂票失敗。" + o1 + "於 " + o2 + " 選擇區域座位數量不夠。");
						return false;
					}
					for (int i = 0; i < NumTICKET; i++) {
						ticketID[NumAllTicket] = tmp2[i];
						System.out.println(ticketID[NumAllTicket]);
						NumAllTicket++;
					}
				} else {
					System.out.println("您的電影票ID:");
					for (int i = 0; i < NumTICKET; i++) {
						ticketID[NumAllTicket] = ticketProduceSpecific_big(inputID, MOVIE, SESSION, func);
						System.out.println(ticketID[NumAllTicket]);
						NumAllTicket++;
					}
				}
			} else if (NumTICKET == 1) {
				System.out.println("您的電影票ID:");
				ticketID[NumAllTicket] = ticketProduceSpecific_big(inputID, MOVIE, SESSION, func);
				System.out.println(ticketID[NumAllTicket]);
				NumAllTicket++;
			}
			System.out.println("電影ID: " + o0);
			System.out.println(o1 + "於 " + o2 + " 此區域目前仍有 " + (avail - NumTICKET) + " 個座位");
		} else if (func == 2) {
			if (Theater.roomIsBig(MOVIE)) {
				System.out.println("請問要指定哪一排呢？請輸入A~M。");
				String ROW = scanner.next();
				System.out.println("下列位置有空位可以選，請選擇有列出的編號。");
				for (int i = 0; i < 495; i++) {
					if (bigRoom[whichNum(MOVIE)][SESSION].getRow(i).equals(ROW) && //
							bigRoom[whichNum(MOVIE)][SESSION].isOccupied(i) != null && //
							bigRoom[whichNum(MOVIE)][SESSION].isOccupied(i) == false)
						System.out.print(bigRoom[whichNum(MOVIE)][SESSION].getSeatNum(i) + "、");
				}
				System.out.println("");
				int j = scanner.nextInt();
				int index = bigRoom[whichNum(MOVIE)][SESSION].index(ROW, j);
				if (bigRoom[whichNum(MOVIE)][SESSION].isOccupied(index) == null || //
						bigRoom[whichNum(MOVIE)][SESSION].isOccupied(index)) {
					System.out.println("請不要亂按沒列出來的數字。訂票失敗。");
				} else {
					System.out.println("您的電影票ID:");
					bigRoom[whichNum(MOVIE)][SESSION].setOccupied(index);
					ticketID[NumAllTicket] = inputID * 1000000 + MOVIE * 10000 + SESSION * 1000 + index;
					System.out.println(ticketID[NumAllTicket]);
					NumAllTicket++;
				}
			} else {
				System.out.println("請問要指定哪一排呢？請輸入A~I。");
				String ROW = scanner.next();
				System.out.println("下列位置有空位可以選，請選擇有列出的編號。");
				for (int i = 0; i < 144; i++) {
					if (smallRoom[whichNum(MOVIE)][SESSION].getRow(i).equals(ROW) && //
							smallRoom[whichNum(MOVIE)][SESSION].isOccupied(i) != null && //
							smallRoom[whichNum(MOVIE)][SESSION].isOccupied(i) == false)
						System.out.print(smallRoom[whichNum(MOVIE)][SESSION].getSeatNum(i) + "、");
				}
				System.out.println("");
				int j = scanner.nextInt();
				int index = smallRoom[whichNum(MOVIE)][SESSION].index(ROW, j);
				if (smallRoom[whichNum(MOVIE)][SESSION].isOccupied(index) == null || //
						smallRoom[whichNum(MOVIE)][SESSION].isOccupied(index)) {
					System.out.println("請不要亂按沒列出來的數字。訂票失敗。");
				} else {
					System.out.println("您的電影票ID:");
					smallRoom[whichNum(MOVIE)][SESSION].setOccupied(index);
					ticketID[NumAllTicket] = inputID * 1000000 + MOVIE * 10000 + SESSION * 1000 + index;
					System.out.println(ticketID[NumAllTicket]);
					NumAllTicket++;
				}
			}
		}
		return true;
	}

	public boolean searching() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("=============================");
		System.out.println("下列查詢功能，請依您的需求輸入1~5，");
		System.out.println("1: 以評分找電影");
		System.out.println("2: 限定播映時間/限定片長長度的電影列表");
		System.out.println("3: 已購買之電影票資訊");
		System.out.println("4: 電影資訊");
		System.out.println("5: 限定區域/排數座位尚有票的電影列表");
		System.out.println("=============================");
		int action = scanner.nextInt();

		switch (action) {
		case 1:
			byScore();
			break;
		case 2:
			byTime();
			break;
		case 3:
			FindTicket();
			break;
		case 4:
			aboutMovie();
			break;
		case 5:
			byRegion();
			break;
		default:
			System.out.println("輸入錯誤。查詢結束。");
			return false;
		}
		return true;
	}

	public int ID_pos(int ID) {
		for (int i = 0; i < ticketID.length; i++)
			if (ticketID[i] == ID)
				return i;
		return -1;
	}

	public int ticketProduceNormal(int UserID, int MOVIE, int Session) {
		int i = 0;
		if (Theater.roomIsBig(MOVIE)) {
			for (i = 0; i < 495; i++)
				if (bigRoom[whichNum(MOVIE)][Session].isOccupied(i) != null && //
						bigRoom[whichNum(MOVIE)][Session].isOccupied(i) == false) {
					bigRoom[whichNum(MOVIE)][Session].setOccupied(i);
					break;
				}
		} else {
			for (i = 0; i < 144; i++)
				if (smallRoom[whichNum(MOVIE)][Session].isOccupied(i) == false) {
					smallRoom[whichNum(MOVIE)][Session].setOccupied(i);
					break;
				}
		}
		return UserID * 1000000 + MOVIE * 10000 + Session * 1000 + i;
	}

	public int[] ticketProduceSpecific_big2(int UserID, int MOVIE, int Session, int t, int Num) {
		int i = 0, n = 0;
		int[] arr = new int[Num];
		int[] ans = new int[Num];
		String s;
		if (t == 1)
			s = "red";
		else if (t == 2)
			s = "yellow";
		else if (t == 3)
			s = "blue";
		else
			s = "gray";
		int k = 0;
		int cnt = 0;
		while (n < Num && cnt < 500) {
			cnt++;
			for (i = k; i < 495; i++) {
				if (bigRoom[whichNum(MOVIE)][Session].REGION(i).equals(s) && //
						bigRoom[whichNum(MOVIE)][Session].isOccupied(i) != null && //
						bigRoom[whichNum(MOVIE)][Session].isOccupied(i) == false) {
					arr[n] = i;
					if (n > 0 && (arr[n] - arr[n - 1]) != 1) {
						for (int j = 0; j < n; j++)
							bigRoom[whichNum(MOVIE)][Session].setOccupied(arr[j]);
						n = 0;
					} else {
						n++;
						bigRoom[whichNum(MOVIE)][Session].setOccupied(i);
					}
					k = i;
					break;
				}
			}
		}
		if (n < Num) {
			for (int j = 0; j < Num; j++)
				ans[j] = -1;
			return ans;
		}
		for (int j = 0; j < Num; j++)
			ans[j] = UserID * 1000000 + MOVIE * 10000 + Session * 1000 + arr[j];
		return ans;
	}

	public int ticketProduceSpecific_big(int UserID, int MOVIE, int Session, int t) {
		int i = 0;
		String s;
		if (t == 1)
			s = "red";
		else if (t == 2)
			s = "yellow";
		else if (t == 3)
			s = "blue";
		else
			s = "gray";
		for (i = 0; i < 495; i++) {
			if (bigRoom[whichNum(MOVIE)][Session].REGION(i).equals(s) && //
					bigRoom[whichNum(MOVIE)][Session].isOccupied(i) != null && //
					bigRoom[whichNum(MOVIE)][Session].isOccupied(i) == false) {
				bigRoom[whichNum(MOVIE)][Session].setOccupied(i);
				break;
			}
		}
		return UserID * 1000000 + MOVIE * 10000 + Session * 1000 + i;
	}

	public int whichNum(int MOVIE) {
		int n = 0;
		for (int i = 0; i < Theater.NumMovie(); i++)
			if (Theater.roomIsBig(MOVIE) == Theater.roomIsBig(i)) {
				if (MOVIE == i)
					return n;
				n++;
			}
		return -1;
	}

	public void FindTicket() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("請輸入您的電影票ID：");
		int inputID = scanner.nextInt();
		int pos = ID_pos(inputID);
		if (pos == -1) {
			System.out.println("此電影票ID不存在。查詢結束。");
			return;
		}

		int index = inputID % 1000;
		int Session = (inputID / 1000) % 10;
		int Movie = (inputID / 10000) % 100;

		String[] SessionStr = Theater.getSessionString(Movie);
		System.out.println("電影名稱: " + Theater.getMovieName(Movie));
		System.out.println("播映時間: " + SessionStr[Session]);
		System.out.println("廳位: " + Theater.getHall(Movie));
		int t;
		String r;
		if (Theater.roomIsBig(Movie)) {
			t = bigRoom[0][0].getSeatNum(index);
			r = bigRoom[0][0].getRow(index);
		} else {
			t = smallRoom[0][0].getSeatNum(index);
			r = smallRoom[0][0].getRow(index);
		}
		if (t < 10)
			System.out.println("座位: " + r + "_0" + t);
		else
			System.out.println("座位: " + r + "_" + t);
	}

	public void byScore() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("請輸入分數：(滿分10分)");
		int score = scanner.nextInt();
		for (int i = 0; i < Theater.NumMovie(); i++)
			if (Theater.getMovieScore(i) >= score)
				System.out.println(Theater.getMovieScore(i) + "分，" + Theater.getMovieName(i));
	}

	public void aboutMovie() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("請輸入欲查詢的電影ID:");
		String inputID = scanner.next();
		boolean flag = false;
		int i;
		for (i = 0; i < Theater.NumMovie(); i++)
			if (Theater.getMovieID(i).equals(inputID)) {
				flag = true;
				break;
			}
		if (flag) {
			System.out.println("電影名稱: " + Theater.getMovieName(i));
			System.out.println("分級: " + Theater.getMovieC(i));
			System.out.println("播映時間: " + Theater.getSession(i));
			System.out.println("廳位: " + Theater.getHall(i));
		} else
			System.out.println("輸入錯誤。查詢結束。");
	}

	public void byTime() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("請輸入欲購買的電影票張數:");
		int NumTICKET = scanner.nextInt();

		System.out.println("請輸入最早播映時間:(請以如右之格式 輸入15:20)");
		String t1 = scanner.next();
		System.out.println("請輸入最晚播映時間:(請以如右之格式 輸入15:20)");
		String t2 = scanner.next();
		String[] t = t1.split(":");
		int time1 = Integer.parseInt(t[0]) * 60 + Integer.parseInt(t[1]);
		t = t2.split(":");
		int time2 = Integer.parseInt(t[0]) * 60 + Integer.parseInt(t[1]);

		System.out.println("請輸入最大片長: (單位:分鐘)");
		int period = scanner.nextInt();

		boolean flag = true;
		for (int M = 0; M < Theater.NumMovie(); M++) {
			int[] arr = new int[20];
			for (int i = 0; i < 20; i++)
				arr[i] = -1;
			int k = 0;
			int[] ss;
			ss = Theater.getSessionInt(M);
			for (int S = 0; S < Theater.NumSession(M); S++) {
				if (ss[S] > time1 && ss[S] < time2 && Theater.getMoviePeriod(M) < period) {
					if (Theater.roomIsBig(M)) {
						int n = 0;
						for (int i = 0; i < 495; i++)
							if (bigRoom[whichNum(M)][S].isOccupied(i) != null && //
									bigRoom[whichNum(M)][S].isOccupied(i) == false)
								n++;
						if (n > NumTICKET) {
							arr[k] = S;
							k++;
						}
					} else {
						int n = 0;
						for (int i = 0; i < 144; i++)
							if (smallRoom[whichNum(M)][S].isOccupied(i) != null && //
									smallRoom[whichNum(M)][S].isOccupied(i) == false)
								n++;
						if (n > NumTICKET) {
							arr[k] = S;
							k++;
						}
					}
				}
			}
			if (arr[0] != -1) {
				flag = false;
				System.out.println("電影ID: " + Theater.getMovieID(M));
				System.out.println("電影名稱: " + Theater.getMovieName(M));
				System.out.print("場次: ");
				for (int i = 0; i < 20; i++) {
					if (arr[i] == -1)
						break;
					String[] tt = Theater.getSessionString(M);
					System.out.print(tt[arr[i]] + " 、 ");
				}
				System.out.println(" ");
			}
		}
		if (flag)
			System.out.println("恕已無電影可滿足您的需求。");
	}

	public void byRegion() {
		boolean flag = true;
		Scanner scanner = new Scanner(System.in);
		System.out.println("請輸入欲購買的電影票張數:");
		int NumTICKET = scanner.nextInt();

		System.out.println("請問要使用哪種模式的指定劃位？請輸入1或2。");
		System.out.println("1: 指定區域");
		System.out.println("2: 指定排數");
		int func = scanner.nextInt();

		if (func != 1) {
			System.out.println("請輸入欲指定的排號:請輸入A~M。");
			String ROW = scanner.next();

			for (int M = 0; M < Theater.NumMovie(); M++) {
				int[] arr = new int[20];
				for (int i = 0; i < 20; i++)
					arr[i] = -1;
				int k = 0;
				for (int S = 0; S < Theater.NumSession(M); S++)
					if (Theater.roomIsBig(M)) {
						int n = 0;
						for (int i = 0; i < 495; i++)
							if (bigRoom[whichNum(M)][S].getRow(i).equals(ROW) && //
									bigRoom[whichNum(M)][S].isOccupied(i) != null && //
									bigRoom[whichNum(M)][S].isOccupied(i) == false)
								n++;
						if (n >= NumTICKET) {
							arr[k] = S;
							k++;
						}
					} else {
						int n = 0;
						for (int i = 0; i < 144; i++)
							if (smallRoom[whichNum(M)][S].getRow(i).equals(ROW) && //
									smallRoom[whichNum(M)][S].isOccupied(i) != null && //
									smallRoom[whichNum(M)][S].isOccupied(i) == false)
								n++;
						if (n >= NumTICKET) {
							arr[k] = S;
							k++;
						}
					}
				if (arr[0] != -1) {
					flag = false;
					System.out.println("電影ID: " + Theater.getMovieID(M));
					System.out.println("電影名稱: " + Theater.getMovieName(M));
					System.out.print("場次: ");
					for (int i = 0; i < 20; i++) {
						if (arr[i] == -1)
							break;
						String[] tt = Theater.getSessionString(M);
						System.out.print(tt[arr[i]] + " 、 ");
					}
					System.out.println(" ");
				}
			}
		} else {
			System.out.println("請問要指定哪一區呢？請輸入編號。");
			System.out.println("1: red");
			System.out.println("2: yellow");
			System.out.println("3: blue");
			System.out.println("4: gray");
			func = 4;
			func = scanner.nextInt(); // 區域選擇

			String s;
			if (func == 1)
				s = "red";
			else if (func == 2)
				s = "yellow";
			else if (func == 3)
				s = "blue";
			else
				s = "gray";

			for (int M = 0; M < Theater.NumMovie(); M++) {
				int[] arr = new int[20];
				for (int i = 0; i < 20; i++)
					arr[i] = -1;
				int k = 0;
				for (int S = 0; S < Theater.NumSession(M); S++)
					if (Theater.roomIsBig(M)) {
						int n = 0;
						for (int i = 0; i < 495; i++)
							if (bigRoom[whichNum(M)][S].REGION(i).equals(s) && //
									bigRoom[whichNum(M)][S].isOccupied(i) != null && //
									bigRoom[whichNum(M)][S].isOccupied(i) == false)
								n++;
						if (n >= NumTICKET) {
							arr[k] = S;
							k++;
						}
					}
				if (arr[0] != -1) {
					flag = false;
					System.out.println("電影ID: " + Theater.getMovieID(M));
					System.out.println("電影名稱: " + Theater.getMovieName(M));
					System.out.print("場次: ");
					for (int i = 0; i < 20; i++) {
						if (arr[i] == -1)
							break;
						String[] tt = Theater.getSessionString(M);
						System.out.print(tt[arr[i]] + " 、 ");
					}
					System.out.println(" ");
				}
			}
		}
		if (flag)
			System.out.println("恕已無電影可滿足您的需求。");
	}
}
