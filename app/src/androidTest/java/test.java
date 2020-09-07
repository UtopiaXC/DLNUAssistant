import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";
    private static Map<String, String> VPNCookies = null;
    private static String Address;
    private static Map<String, String> EDUCookies = null;
    private static Document document = null;

    public String getHTML(String address) {
        URL url;
        int responsecode;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String line;
        try {
            url = new URL(address);
            //打开URL
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
            //获取服务器响应代码
            responsecode = urlConnection.getResponseCode();
            if (responsecode == 200) {
                //得到输入流，即获得了网页的内容
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } else {
                return "error";
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "error";
        }
    }

    private static boolean getVPNCookie(String username, String password) {
        try {
            Connection.Response response = Jsoup.connect("http://210.30.0.110/do-login")
                    .ignoreContentType(true)
                    .userAgent(userAgent)
                    .data("auth_type", "local",
                            "username", username,
                            "password", password)
                    .method(Connection.Method.POST)
                    .execute();
            VPNCookies = response.cookies();
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("cookies error");
            return false;
        }
    }

    private static boolean getAddress() {
        try {
            Connection.Response response = Jsoup.connect("http://210.30.0.110/")
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = response.parse();
            Elements elements = doc.getElementsByClass("vpn-content-block-panel__collect_ed");
            for (Element element : elements) {
                if (element.toString().contains("http://zhjw.dlnu.edu.cn")) {
                    String url = element.toString().replace(" ", "");
                    url = url.replace("\n", "");
                    url = url.replace("<divclass=\"vpn-content-block-panel__collect_ed\"data-resource=\"12\"data-url=\"http://zhjw.dlnu.edu.cn\"data-redirect=\"", "");
                    url = url.replace("\"data-name=\"综合教务\"data-type=\"vpn\"data-logo=\"\"data-detail=\"zhjw.dlnu.edu.cn\"><iclass=\"layui-iconlayui-icon-rate\"></i></div>", "");
                    Address = "http://210.30.0.110" + url;
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
        return false;
    }

    //登录教务并获取cookie的方法
    private static boolean getEDUCookies(String username, String password) {
        try {
            Connection.Response response = Jsoup.connect(Address + "/loginAction.do")
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .data("zjh", username, "mm", password)
                    .method(Connection.Method.POST)
                    .execute();
            EDUCookies = response.cookies();
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("cookies error");
            return false;
        }
    }

    //获取教务指定网页Doc的方法,document调用结束后请赋空
    private static boolean getDocument(String address) {
        try {
            Connection.Response response = Jsoup.connect(address)
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .cookies(EDUCookies)
                    .method(Connection.Method.POST)
                    .execute();

            document = response.parse();
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    //测试账号密码及教务地址的方法
    public boolean testURP(String VPNName, String VPNPass, String username, String password) {
        try {
            if (!getVPNCookie(VPNName, VPNPass))
                return false;
            if (!getAddress())
                return false;
            if (!getEDUCookies(username, password))
                return false;
            if (!getDocument(Address + "/xkAction.do?actionType=17"))
                return false;
            String line = document.toString();

            String reg = "setTimeout";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                document = null;
                return false;
            }
            document = null;
            return true;

        } catch (Exception e) {
            System.out.println(e.toString());
            return false;

        }
    }


    public static boolean setNetwork(String VPNName, String VPNPass, String username, String password) {
        try {
            String NetAddress=null;
            if (!getVPNCookie(VPNName, VPNPass))
                return false;
            Connection.Response response = Jsoup.connect("http://210.30.0.110/")
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = response.parse();
            Elements elements = doc.getElementsByClass("vpn-content-block-panel__collect_ed");
            for (Element element : elements) {
                if (element.toString().contains("http://www.dlnu.edu.cn")) {
                    String url = element.toString().replace(" ", "");
                    url = url.replace("\n", "");
                    url = url.replace("<divclass=\"vpn-content-block-panel__collect_ed\"data-resource=\"18\"data-url=\"http://www.dlnu.edu.cn\"data-redirect=\"", "");
                    url = url.replace("/\"data-name=\"主页\"data-type=\"vpn\"data-logo=\"\"data-detail=\"www.dlnu.edu.cn\"><iclass=\"layui-iconlayui-icon-rate\"></i></div>", "");
                    NetAddress = "http://210.30.0.110" + url+"/hhh/index.htm";
                    break;
                }
            }
            response = Jsoup.connect(NetAddress)
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();
            doc = response.parse();
            elements = doc.getElementsByTag("span");
            for (Element element : elements) {
                if (element.toString().contains("网络与信息技术中心")) {
                    NetAddress = element.toString().replace("<span><a href=\"", "");
                    NetAddress=NetAddress.replace("\" target=\"_blank\">网络与信息技术中心</a></span>","");
                    break;
                }
            }

            response = Jsoup.connect(NetAddress)
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();
            doc = response.parse();
            elements = doc.getElementsByClass("txt-elise wow animated fadeInUp");
            for (Element element : elements) {
                if (element.toString().contains("上网自助服务")) {
                    NetAddress=element.toString().split("\"")[3];
                    break;
                }
            }


            response = Jsoup.connect(NetAddress+"dwr/interface/drcom.js")
                    .cookies(VPNCookies)
                    .ignoreContentType(true)
                    .userAgent(userAgent)
                    .method(Connection.Method.GET)
                    .execute();
            Map<String, String> NetCookies = response.cookies();


            response = Jsoup.connect(NetAddress+"nav_login")
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .cookies(NetCookies)
                    .method(Connection.Method.GET)
                    .execute();
            doc = response.parse();


            String pattern = "checkcode=\"\\d{4}\"";

            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(doc.toString());
            String checkcode="";
            if (m.find()) {
                checkcode = m.group().replace("checkcode=\"","");
                checkcode=checkcode.replace("\"","");
            }



            response = Jsoup.connect(NetAddress+"LoginAction.action")
                    .cookies(VPNCookies)
                    .cookies(NetCookies)
                    .ignoreContentType(true)
                    .userAgent(userAgent)
                    .data("account", username,
                            "password", MD5.md5(password),
                            "code","",
                            "checkcode",checkcode,
                            "Submit", "%E7%99%BB+%E5%BD%95")
                    .method(Connection.Method.POST)
                    .execute();
            doc = response.parse();
            System.out.println(doc);


            response = Jsoup.connect(NetAddress+"nav_getUserInfo")
                    .cookies(VPNCookies)
                    .ignoreContentType(true)
                    .userAgent(userAgent)
                    .method(Connection.Method.POST)
                    .execute();







            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        setNetwork("2018081510","10255376","2018081510","19991025");
    }

}
