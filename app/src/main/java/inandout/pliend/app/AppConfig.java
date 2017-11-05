package inandout.pliend.app;

/**
 * Created by DK on 2016-10-11.
 */
public class AppConfig {
	// Server user login url
	public static String ip = "nyamnyam.dothome.co.kr";

	public static String URL_LOGIN = "http://13.124.153.76:3000/login"; // "http://" + ip + "/yamyam_api/login.php";

	// Server user register url
	public static String URL_REGISTER = "http://13.124.153.76:3000/register"; // "http://" + ip + "/yamyam_api/register.php";

	// Server data input url
	public static String URL_ADD_PLANT = "http://" + ip + "/yamyam_api/addplant.php";

	public static String URL_REG_PLANT = "http://13.124.153.76:3000/regplant";
	public static String URL_MOD_PLANT = "http://13.124.153.76:3000/modplant";

	// Server data input url
	public static String URL_LOAD_PLANT = "http://13.124.153.76:3000/loadplant"; // "http://" + ip + "/yamyam_api/loadplant.php";

	// Server data input url
	public static String URL_LOAD_QUEST = "http://13.124.153.76:3000/loadquest"; // "http://" + ip + "/yamyam_api/load_quest.php";

	public static String URL_PW_CHANGE = "http://" + ip + "/yamyam_api/password-change.php";

	public static String URL_REFRESH = "http://13.124.153.76:3000/refresh";

	public static String URL_REG_TOKEN = "http://13.124.153.76:3000/regtoken"; // "http://" + ip + "/yamyam_api/register_token.php";

	public static String URL_REG_EMAIL = "http://13.124.153.76:3000/regemail"; // "http://" + ip + "/yamyam_api/register_email.php";

	public static String URL_REG_QUEST = "http://13.124.153.76:3000/regquest"; // "http://" + ip + "/yamyam_api/register_quest.php";
}