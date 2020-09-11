package com.utopiaxc.dlnuassistant.fuctions;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utopiaxc.dlnuassistant.sqlite.SQLHelperExamInfo;
import com.utopiaxc.dlnuassistant.sqlite.SQLHelperGradesList;
import com.utopiaxc.dlnuassistant.sqlite.SQLHelperTimeTable;

import org.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class FunctionsPublicBasic {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";
    private static Map<String, String> VPNCookies = null;
    private static String Address;
    private static Map<String, String> EDUCookies = null;
    private static Document document = null;
    private static String NetAddress = null;

    //获取指定网站的dom文档的方法
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

    //获取网页VPN Cookie的方法
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
            return false;
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("cookies error");
            return true;
        }
    }

    //获取综合教务地址的方法
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
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return true;
        }
        return true;
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
            return false;
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("cookies error");
            return true;
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
            return false;
        } catch (Exception e) {
            System.out.println(e.toString());
            return true;
        }
    }

    //测试账号密码及教务地址的方法
    public boolean testURP(String VPNName, String VPNPass, String username, String password) {
        try {
            if (getVPNCookie(VPNName, VPNPass))
                return false;
            if (getAddress())
                return false;
            if (getEDUCookies(username, password))
                return false;
            if (getDocument(Address + "/xkAction.do?actionType=17"))
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

    //处理课程表的方法
    public boolean setClassTableSQL(Context context, String VPNName, String VPNPass, String username, String password) {
        try {
            //判断Cookie是否正确获取
            if (getVPNCookie(VPNName, VPNPass))
                return false;
            if (getAddress())
                return false;
            if (getEDUCookies(username, password))
                return false;
            if (getDocument(Address + "/xkAction.do?actionType=17"))
                return false;

            //寻找全部带有odd标签的课程
            Elements elements = document.getElementsByClass("odd");
            String[] messages_last = new String[30];

            //数据库打开
            SQLHelperTimeTable sql = new SQLHelperTimeTable(context, "URP_timetable", null, 2);
            SQLiteDatabase sqliteDatabase = sql.getWritableDatabase();
            sqliteDatabase.execSQL("delete from classes");


            //遍历每一课程
            for (Element element : elements) {
                Elements elements_class = element.getElementsByTag("td");
                String[] messages = new String[30];
                int flag = 0;

                //去掉无用信息
                for (Element element_toSave : elements_class) {
                    messages[flag] = element_toSave.toString();
                    messages[flag] = messages[flag].replace(" ", "");
                    messages[flag] = messages[flag].replace("<td>&nbsp", "");
                    messages[flag] = messages[flag].replace("</td>", "");
                    messages[flag] = messages[flag].replace("<tdrowspan=\"1\">&nbsp", "");
                    messages[flag] = messages[flag].replace("<tdrowspan=\"2\">&nbsp", "");
                    messages[flag] = messages[flag].replace("<tdrowspan=\"3\">&nbsp", "");
                    messages[flag] = messages[flag].replace("<tdrowspan=\"4\">&nbsp", "");
                    flag++;
                }

                //判断是否得多行课程
                if (flag > 16) {
                    System.arraycopy(messages, 0, messages_last, 0, 11);
                }
                if (flag < 8) {
                    System.arraycopy(messages, 0, messages, 11, 7);
                    System.arraycopy(messages_last, 0, messages, 0, 11);
                }

                for (int i = 11; i < 18; i++) {
                    if (messages[i].equals(";"))
                        messages[i] = "^";
                }

                //处理上课周数信息
                messages[11] = messages[11].replace("周上", "");
                messages[11] = messages[11].replace(";", "");

                if (messages[11].contains("双周")) {
                    messages[11] = messages[11].replace("双周", "");
                    String[] divide = messages[11].split("-");
                    for (String divide_check : divide) {
                        divide_check.replace("-", "");
                    }
                    messages[11] = "";
                    for (int i = Integer.parseInt(divide[0]); i <= Integer.parseInt(divide[1]); i++) {
                        if (i % 2 == 0) {
                            messages[11] += i + ",";
                        }
                    }
                    messages[11] = messages[11].substring(0, messages[11].length() - 1);
                }

                if (messages[11].contains("单周")) {
                    messages[11] = messages[11].replace("单周", "");
                    String[] divide = messages[11].split("-");
                    for (String divide_check : divide) {
                        divide_check.replace("-", "");
                    }
                    messages[11] = "";
                    for (int i = Integer.parseInt(divide[0]); i <= Integer.parseInt(divide[1]); i++) {
                        if (i % 2 == 1) {
                            messages[11] += i + ",";
                        }
                    }
                    messages[11] = messages[11].substring(0, messages[11].length() - 1);
                }

                if (messages[11].contains("-")) {
                    String[] time = messages[11].split(",");
                    messages[11] = "";
                    for (String check : time) {
                        check = check.replace(",", "");
                        if (check.contains("-")) {
                            String[] result = check.split("-");
                            result[0] = result[0].replace(",", "");
                            result[1] = result[1].replace(",", "");


                            for (int i = Integer.parseInt(result[0]); i < Integer.parseInt(result[1]) + 1; i++)
                                messages[11] += i + ",";
                        } else
                            messages[11] += check + ",";
                    }
                    messages[11] = messages[11].substring(0, messages[11].length() - 1);
                }

                //数据库提交表
                ContentValues values = new ContentValues();
                values.put("ClassId", messages[1].replace(";", ""));
                values.put("ClassName", messages[2].replace(";", ""));
                values.put("Credit", messages[4].replace(";", ""));
                values.put("ClassAttribute", messages[5].replace(";", ""));
                values.put("ExamAttribute", messages[6].replace(";", ""));
                values.put("Teacher", messages[7].replace(";", ""));
                values.put("Way", messages[9].replace(";", ""));
                values.put("Week", messages[11]);
                values.put("Data", messages[12].replace(";", ""));
                values.put("Time", messages[13].replace(";", ""));
                values.put("Count", messages[14].replace(";", ""));
                values.put("School", messages[15].replace(";", ""));
                values.put("Building", messages[16].replace(";", ""));
                values.put("Room", messages[17].replace(";", ""));

                //插入数据表
                sqliteDatabase.insert("classes", null, values);


            }

            //SQLit测试
            /*
            Cursor cursor = sqliteDatabase.query("classes", new String[]{"ClassName","Credit"}, null, null, null, null, null);
            while(cursor.moveToNext())
                System.out.println(cursor.getString(cursor.getColumnIndex("ClassName"))
                +cursor.getString(cursor.getColumnIndex("Credit")));
            cursor.close();
            */

            SharedPreferences sharedPreferences = context.getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("ClassIsGot", true);
            editor.commit();

            sqliteDatabase.close();
            document = null;
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            document = null;
            return false;
        }
    }

    //处理成绩表的方法
    public boolean setGrades(Context context, String VPNName, String VPNPass, String username, String password) {
        //判断Cookie是否正确获取
        if (getVPNCookie(VPNName, VPNPass))
            return false;
        if (getAddress())
            return false;
        if (getEDUCookies(username, password))
            return false;
        if (getDocument(Address + "/gradeLnAllAction.do?type=ln&oper=sxinfo&lnsxdm=001"))
            return false;

        try {
            SQLHelperGradesList sqlHelperGradesList = new SQLHelperGradesList(context, "URP_Grade", null, 2);
            SQLiteDatabase sqLiteDatabase = sqlHelperGradesList.getWritableDatabase();
            sqLiteDatabase.execSQL("delete from grades");


            Elements elements_all_course = document.getElementsByClass("odd");
            for (Element element_single_course : elements_all_course) {
                Elements elements_single_course = element_single_course.getElementsByTag("td");
                String[] messages_single_sign = new String[20];
                int flag = 0;
                for (Element element_single_sign : elements_single_course) {
                    messages_single_sign[flag] = element_single_sign.toString();
                    flag++;
                }

                for (int i = 0; i < flag; i++) {
                    messages_single_sign[i] = messages_single_sign[i].replace(" ", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("<tdalign=\"center\">", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("</td>", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("&nbsp;", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("<palign=\"center\">", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("</p>", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("<td>", "");
                }
                ContentValues values = new ContentValues();
                values.put("ClassId", messages_single_sign[0]);
                values.put("ClassName", messages_single_sign[2]);
                values.put("Credit", messages_single_sign[4]);
                values.put("ClassAttribute", messages_single_sign[5]);
                values.put("Grade", messages_single_sign[6]);

                sqLiteDatabase.insert("grades", null, values);
            }

            //SQL测试
            /*
            Cursor cursor = sqLiteDatabase.query("grades", new String[]{"ClassName","Credit"}, null, null, null, null, null);
            while(cursor.moveToNext())
                System.out.println(cursor.getString(cursor.getColumnIndex("ClassName"))
                        +cursor.getString(cursor.getColumnIndex("Credit")));
            cursor.close();
            */

            SharedPreferences sharedPreferences = context.getSharedPreferences("Grades", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("GradeIsGot", true);
            editor.commit();
            sqLiteDatabase.close();

            return true;

        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }


    }

    //处理考试信息表的方法
    public boolean setExamInfo(Context context, String VPNName, String VPNPass, String username, String password) {
        if (getVPNCookie(VPNName, VPNPass))
            return false;
        if (getAddress())
            return false;
        if (getEDUCookies(username, password))
            return false;
        if (getDocument(Address + "/ksApCxAction.do?oper=getKsapXx"))
            return false;
        try {
            SQLHelperExamInfo sqlHelperExamInfo = new SQLHelperExamInfo(context, "URP_Exam", null, 2);
            SQLiteDatabase sqLiteDatabase = sqlHelperExamInfo.getWritableDatabase();
            sqLiteDatabase.execSQL("delete from exams");


            Elements elements_all_course = document.getElementsByClass("odd");
            for (Element element_single_course : elements_all_course) {
                Elements elements_single_course = element_single_course.getElementsByTag("td");
                String[] messages_single_sign = new String[20];
                int flag = 0;
                for (Element element_single_sign : elements_single_course) {
                    messages_single_sign[flag] = element_single_sign.toString();
                    flag++;
                }


                for (int i = 0; i < flag; i++) {
                    messages_single_sign[i] = messages_single_sign[i].replace(" ", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("<tdalign=\"center\">", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("</td>", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("&nbsp;", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("<palign=\"center\">", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("</p>", "");
                    messages_single_sign[i] = messages_single_sign[i].replace("<td>", "");
                }


                ContentValues values = new ContentValues();
                values.put("ExamName", messages_single_sign[4]);
                values.put("ExamSchool", messages_single_sign[1]);
                values.put("ExamBuilding", messages_single_sign[2]);
                values.put("ExamRoom", messages_single_sign[3]);
                values.put("ExamData", messages_single_sign[5]);
                values.put("ExamTime", messages_single_sign[6]);

                sqLiteDatabase.insert("exams", null, values);

            }

            //SQL测试
            /*
            Cursor cursor = sqLiteDatabase.query("grades", new String[]{"ClassName","Credit"}, null, null, null, null, null);
            while(cursor.moveToNext())
                System.out.println(cursor.getString(cursor.getColumnIndex("ClassName"))
                        +cursor.getString(cursor.getColumnIndex("Credit")));
            cursor.close();
            */

            SharedPreferences sharedPreferences = context.getSharedPreferences("ExamInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("ExamInfoIsGot", true);
            editor.commit();
            sqLiteDatabase.close();

            return true;

        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }


    }

    //登录网络信息中心的方法
    public boolean loginNetwork(String VPNName, String VPNPass, String username, String password) {
        try {
            if (getVPNCookie(VPNName, VPNPass))
                return false;
            Connection.Response response = Jsoup.connect("http://210.30.0.110/")
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = response.parse();
            //System.out.println(response.parse().toString());
            Elements elements = doc.getElementsByClass("vpn-content-block-panel__collect_ed");
            for (Element element : elements) {
                if (element.toString().contains("http://www.dlnu.edu.cn")) {
                    String url = element.toString().replace(" ", "");
                    url = url.replace("\n", "");
                    url = url.replace("<divclass=\"vpn-content-block-panel__collect_ed\"data-resource=\"18\"data-url=\"http://www.dlnu.edu.cn\"data-redirect=\"", "");
                    url = url.replace("/\"data-name=\"主页\"data-type=\"vpn\"data-logo=\"\"data-detail=\"www.dlnu.edu.cn\"><iclass=\"layui-iconlayui-icon-rate\"></i></div>", "");
                    NetAddress = "http://210.30.0.110" + url + "/hhh/index.htm";
                    break;
                }
            }
            response = Jsoup.connect(NetAddress)
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();
            doc = response.parse();
            //System.out.println(response.parse().toString());
            elements = doc.getElementsByTag("span");
            for (Element element : elements) {
                if (element.toString().contains("网络与信息技术中心")) {
                    NetAddress = element.toString().replace("<span><a href=\"", "");
                    NetAddress = NetAddress.replace("\" target=\"_blank\">网络与信息技术中心</a></span>", "");
                    break;
                }
            }

            response = Jsoup.connect(NetAddress)
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();
            doc = response.parse();

            //System.out.println(response.parse().toString());


            elements = doc.getElementsByClass("txt-elise wow animated fadeInUp");
            for (Element element : elements) {
                if (element.toString().contains("上网自助服务")) {
                    NetAddress = element.toString().split("\"")[3];
                    break;
                }
            }


            response = Jsoup.connect(NetAddress + "nav_login")
                    .ignoreContentType(true)
                    .userAgent(userAgent)
                    .cookies(VPNCookies)
                    .method(Connection.Method.GET)
                    .execute();
            doc = response.parse();
            String pattern = "checkcode=\"\\d{4}\"";

            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(doc.toString());
            String checkcode = "";
            if (m.find()) {
                checkcode = m.group().replace("checkcode=\"", "");
                checkcode = checkcode.replace("\"", "");
            }

            doGet(NetAddress + "RandomCodeAction.action");

            response = Jsoup.connect(NetAddress + "LoginAction.action")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .cookies(VPNCookies)
                    .userAgent(userAgent)
                    .data("account", username)
                    .data("password", md5(password))
                    .data("code", "")
                    .data("checkcode", checkcode)
                    .data("Submit", "登 录")
                    .method(Connection.Method.POST)
                    .execute();

            if (!response.parse().toString().contains("套餐"))
                return false;
            else
                return true;

            //System.out.println(response.parse().toString());


//            response = Jsoup.connect(NetAddress + "nav_getUserInfo")
//                    .cookies(VPNCookies)
//                    .ignoreContentType(true)
//                    .userAgent(userAgent)
//                    .method(Connection.Method.GET)
//                    .execute();
//
//            System.out.println(response.parse().toString());
//            response = Jsoup.connect(NetAddress + "nav_offLine")
//                    .cookies(VPNCookies)
//                    .ignoreContentType(true)
//                    .userAgent(userAgent)
//                    .method(Connection.Method.GET)
//                    .execute();
//            doc = response.parse();
//            elements = doc.getElementsByTag("td");
//            String code = null;
//            for (Element element : elements) {
//                if (element.toString().contains("display:none"))
//                    code = element.toString().replace("<td style=\"display:none;\">", "").replace("</td>", "");
//            }


//            doGet(NetAddress+"tooffline?fldsessionid="+code);
//            Thread.sleep(1000);
//            doGet(NetAddress+"tooffline?fldsessionid="+code);
//            Thread.sleep(1000);
//            doGet(NetAddress+"tooffline?fldsessionid="+code);
//            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Login Network Error");
            return false;
        }
    }

    //填写校园网信息方法
    public boolean getNetworkMessages(String VPNName, String VPNPass, String username, String password, HashMap messages) {
        if (!loginNetwork(VPNName, VPNPass, username, password)) {
            return false;
        }
        Document messagesDoc = doGet(NetAddress + "refreshaccount");
        JSONObject jsonObject = JSON.parseObject(messagesDoc.body().toString().replace("<body>", "").replace("</body>", ""));

        messages.put("account", "账户：" + jsonObject.getJSONObject("note").get("welcome"));
        messages.put("balance", "余额：￥" + jsonObject.getJSONObject("note").get("leftmoeny"));
        messages.put("statue","停复机状态："+jsonObject.getJSONObject("note").get("status"));
        if (Integer.parseInt(jsonObject.getJSONObject("note").get("onlinestate").toString()) == 1) {
            messages.put("online", "状态：在线");
        } else
            messages.put("online", "状态：离线");
        String set = jsonObject.getJSONObject("note").get("service").toString();
        if (set.contains("30G"))
            set = set.replace("30G", "300G");
        else if (set.contains("20G"))
            set = set.replace("20G", "200G");
        else if (set.contains("10G"))
            set = set.replace("10G", "100G");
        messages.put("set", "套餐：\n" + set);
        messages.put("overdate", "当月" + jsonObject.getJSONObject("note").get("overdate").toString().replace("为", "：").replace("；", ""));

        messagesDoc = doGet(NetAddress + "nav_getUserInfo");
        Elements elements = messagesDoc.getElementsByTag("tr");
        for (Element element : elements) {
            if (element.toString().contains("本月时长")) {
                String usedTime = element.toString().replace("\n", "")
                        .replace(" ", "")
                        .replace("<trclass=\"tr2\"><tdclass=\"t_l\">本月时长（分钟）</td><!--0004_PersonList.jsp=本月时长（分钟）--><tdclass=\"t_r1\">&nbsp;", "")
                        .replace("</td><tdclass=\"t_r2\">&nbsp;</td></tr>", "");
                DecimalFormat df = new DecimalFormat("#.00");
                String unit = "分钟";
                if (Integer.parseInt(usedTime) >= 60) {
                    double usage = Integer.parseInt(usedTime) / 60.0;
                    unit = "小时";
                    usedTime = df.format(usage);
                }
                messages.put("usedtime", "当月使用时长：" + usedTime + unit);
            }
            if (element.toString().contains("本月流量")) {
                double usedBand = Double.parseDouble(element.toString().replace("\n", "")
                        .replace(" ", "")
                        .replace("<tr><tdclass=\"t_l\">本月流量（MB）</td><!--0005_PersonList.jsp=本月流量（MB）--><tdclass=\"t_r1\">&nbsp;", "")
                        .replace("</td><tdclass=\"t_r2\">&nbsp;</td></tr>", ""));
                String unit = "MB";
                DecimalFormat df = new DecimalFormat("#.00");
                String usedBandString = df.format(usedBand);
                if (usedBand > 1024) {
                    double usage = usedBand / 1024.0;
                    unit = " GB";
                    usedBandString = df.format(usage);
                }
                messages.put("usedband", "当月已用流量：" + usedBandString + unit);
            }
        }


        return true;

    }

    public boolean logoutNetwork(String VPNName, String VPNPass, String username, String password) {
        if (!loginNetwork(VPNName, VPNPass, username, password)) {
            return false;
        }
        int onlineCode = 0;
        Document document = doGet(NetAddress + "nav_offLine");
        Elements elements=document.getElementsByTag("td");
        for (Element element:elements){
            try{
                onlineCode=Integer.parseInt(element.text());
            }catch (Exception e){

            }
        }
        System.out.println(onlineCode);
        for (int i = 0; i < 5; i++) {
            doGet(NetAddress + "tooffline?fldsessionid=" + onlineCode);
            System.out.println("Do Logout With "+NetAddress+"tooffline?fldsessionid=" + onlineCode);
        }
        return true;
    }

    public String getSetCheck(String VPNName, String VPNPass, String username, String password){
        if (!loginNetwork(VPNName, VPNPass, username, password)) {
            return "ERROR";
        }
        Document document=doGet(NetAddress+"nav_servicedefaultbook");
        Elements elements=document.getElementsByTag("div");
        if (document.toString().contains("[本科10元10G]"))
            return "本科10元100G";
        else if (document.toString().contains("[本科20元20G]"))
            return "本科20元200G";
        else if (document.toString().contains("[本科30元30G]"))
            return "本科30元300G";
        else if (document.toString().contains("[]"))
            return "未预约下月套餐，默认与本月相同";
        else
            return "DONTHAVE";
    }

    public boolean bookSet(String VPNName, String VPNPass, String username, String password,int setNum){
        if (!loginNetwork(VPNName, VPNPass, username, password)) {
            return false;
        }

        try {
            Connection.Response response = Jsoup.connect(NetAddress+"selfservicebookAction")
                    .ignoreContentType(true)
                    .data("serid", String.valueOf(setNum))
                    .cookies(VPNCookies)
                    .userAgent(userAgent)
                    .method(Connection.Method.GET)
                    .execute();
            Document document=response.parse();
            if (document.toString().contains("成功/正常")||document.toString().contains("您已经预约该套餐"))
                return true;
            else
                return false;
        } catch (IOException e) {
            System.out.println("Change set net error");
            return false;
        }
    }

    public boolean stopNetwork(String VPNName, String VPNPass, String username, String password){
        if (!loginNetwork(VPNName, VPNPass, username, password)) {
            return false;
        }
        Document document=doGet(NetAddress+"nav_selfstopNow");
        if (document.toString().contains("操作状态：成功"))
            return true;
        else
            return false;
    }

    public boolean reopenNetwork(String VPNName, String VPNPass, String username, String password){
        if (!loginNetwork(VPNName, VPNPass, username, password)) {
            return false;
        }
        Document document=doGet(NetAddress+"nav_SelfReopenNow");
        if (document.toString().contains("操作状态：成功"))
            return true;
        else
            return false;
    }

    //nav_selfstopNow

    //get爬虫方法
    private Document doGet(String address) {
        try {
            Connection.Response response = Jsoup.connect(address)
                    .ignoreContentType(true)
                    .cookies(VPNCookies)
                    .userAgent(userAgent)
                    .method(Connection.Method.GET)
                    .execute();
            return response.parse();
        } catch (Exception e) {
            System.out.println("Get Error With "+address);
            return null;
        }
    }

    //MD5信息摘要算法
    public static String md5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return "ERROR";
        }
    }

}
