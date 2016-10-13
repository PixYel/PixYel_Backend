package pixyel_backend.database;

public class Test {

	public static void main(String[] args) {
		try {
			DatabaseInitializer.init();
		} catch (Exception e) {
			System.out.println(e);
		}
		String testnum = "+015783978333";
		
		System.out.println();
	}

}
