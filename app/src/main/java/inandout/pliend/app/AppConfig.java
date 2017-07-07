package inandout.pliend.app;

/**
 * Created by DK on 2016-10-11.
 */
public class AppConfig {
	// Server user login url
	public static String ip = "nyamnyam.dothome.co.kr";

	public static String URL_LOGIN = "http://" + ip + "/yamyam_api/login.php";

	// Server user register url
	public static String URL_REGISTER = "http://" + ip + "/yamyam_api/register.php";

	// Server data input url
	public static String URL_ADD_PLANT = "http://" + ip + "/yamyam_api/addplant.php";

	// Server data input url
	public static String URL_LOAD_PLANT = "http://" + ip + "/yamyam_api/loadplant.php";

	public static String URL_PW_CHANGE = "http://" + ip + "/yamyam_api/password-change.php";
}