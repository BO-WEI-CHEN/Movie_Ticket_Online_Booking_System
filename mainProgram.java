import java.util.*;

public class mainProgram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "D:/OneDrive/json/";
		// Theater t = new Theater(path);
		Scanner scanner = new Scanner(System.in);
		bookingSystem b = new bookingSystem(path);
		int action = 0;

		Date date = new Date();
		int H = date.getHours();
		int M = date.getMinutes();
		if (M < 10)
			System.out.println("現在時間: " + H + ":0" + M);
		else
			System.out.println("現在時間: " + H + ":" + M);

		for (;;) {
			System.out.println("=============================");
			System.out.println("歡迎使用此系統，請依您的需求輸入1~4，");
			System.out.println("1: 一般訂票");
			System.out.println("2: 退票");
			System.out.println("3: 條件式訂票");
			System.out.println("4: 查詢");
			System.out.println("5: 結束此系統");
			System.out.println("=============================");
			action = scanner.nextInt();

			if (action == 5)
				break;

			switch (action) {
			case 1:
				b.normalBooking();
				break;
			case 2:
				b.refund();
				break;
			case 3:
				b.conditionalBooking();
				break;
			 case 4:
			 b.searching();
			 break;
			}
		}
	}

}
