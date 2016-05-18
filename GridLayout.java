package com.svail.gridprocess;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.svail.util.FileTool;
import com.svail.util.Tool;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GridLayoutLines extends JFrame {
	public static String longitude;
	public static String latitude;
	public static String region;
	public static String title;
	public static String time;
	public static String price;
	public static String area;
	public static double unit_price;
	public static String location;
	public static String community ;
	public static String address ;
	public static String property;
	public static String pay_way;
	public static String house_type;
	public static String rent_type;
	public static String fitment ;
	public static String direction ;
	public static String floor ;
	public static String totalarea;
	public static String developer;
	public static String property_fee;
	public static String households;
	public static String built_year;
	public static String volume_rate;
	public static String green_rate;
	public static String park;
	public static String heat_supply;
	public static String traffic ;
	public static String url ;
	public static String property_company;
	public static String down_payment;
	public static String month_payment;
	public static String folder="D:\\Crawldata_BeiJing\\5i5j\\rentout\\";
	
	public static String COORDINATE;
	public static Double LNG;
	public static Double LAT;
	public static Double PRICE;
	public static String ADDRESS;

	public static Double X_MAX = 2.0542041271351546E7;// 2.0542041271351546E7
	public static Double X_MIN = 2.036373920422157E7;
	public static Double Y_MAX = 4547353.496401368;
	public static Double Y_MIN = 4368434.982578722;
	public static int rows;
	public static int cols;

	public static void main(String[] args) throws IOException {

		//setCode();
		//addCode("D:/Crawldata_BeiJing/anjuke/rentout/0326/anjuke_rentout1231_result.txt-Json.txt");
		//codeStatistic("D:/Crawldata_BeiJing/anjuke/rentout/0326/anjuke_rentout1231_result-Json-1000.txt");

		/*
		 * DataGrid(
		 * "D:\\Crawldata_BeiJing\\fang\\rentout\\0326\\fang_rentout0108\\fang_rentout0108_result.txt"
		 * ,5000); GridLayoutLines frame = new GridLayoutLines(rows, cols, 2, 2,
		 * 2, 2, 2, 2); frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		 * frame.pack(); frame.setLocationRelativeTo(null);
		 * frame.setVisible(true);
		 */
		
		//房地产数据处理第一步：
		//toJson("D:/Crawldata_BeiJing/5i5j/resold/source/woaiwojia_resold1231_result.txt");
		
		//第二步：给每条poi添加code编码
		 setCode();
		// addCode("D:/Crawldata_BeiJing/5i5j/resold/source/woaiwojia_resold1231_result.txt-Json.txt");
		 
		//第三步：统计每个网格编码所含有房源的数目
		// codeStatistic("D:/Crawldata_BeiJing/5i5j/resold/source/woaiwojia_resold1231_result-Json-1000.txt");
		 
		//第四步：选取特定的code进行价格时序分析
		// getCode("D:/Crawldata_BeiJing/5i5j/resold/source/woaiwojia_resold1231_result-Json-1000.txt",15463);
		 
		//第五步：以某一网格为例，获取每个时间段网格的房屋均价，存于unitprice文件中
		 getTimeSeriesPrice();
		
	}
	public static String[] DATE={"1125","1201","1207","1214","1222","1231","0108","0119","0127","0219","0227","0309","0314"};
	public static void getTimeSeriesPrice(){
		String[] url={
                 "woaiwojia_resold1125_result-Json-1000-15463.txt",
				 "woaiwojia_resold1201_result-Json-1000-15463.txt",
				 "woaiwojia_resold1207_result-Json-1000-15463.txt",
				 "woaiwojia_resold1214_result-Json-1000-15463.txt",
				 "woaiwojia_resold1222_result-Json-1000-15463.txt",
				 "woaiwojia_resold1231_result-Json-1000-15463.txt",
				 "woaiwojia_resold0108_result-Json-1000-15463.txt",
				 "woaiwojia_resold0119_result-Json-1000-15463.txt",
				 "woaiwojia_resold0127_result-Json-1000-15463.txt",
				 "woaiwojia_resold0219_result-Json-1000-15463.txt",
				 "woaiwojia_resold0227_result-Json-1000-15463.txt",
				 "woaiwojia_resold0309_result-Json-1000-15463.txt",
				 "woaiwojia_resold0314_result-Json-1000-15463.txt"
				 };
			for(int i=0;i<url.length;i++){
				getPrice("D:/Crawldata_BeiJing/5i5j/resold/source/"+url[i]);
				
				//getCode("D:/Crawldata_BeiJing/5i5j/rentout/1000/15446/"+url[i]);
				System.out.println("完成第"+i+"个文件的处理");
			}
	}
	/**
	 * 获取同一网格里的房源的平均价格
	 * @param file
	 */
	public static void getPrice(String file){
		Vector<String> pois = FileTool.Load(file, "utf-8");
		String poi="";
		 ArrayList UnitPrice=new ArrayList();
		 double total=0;
		 double average=0;
		try{
			for(int i=0;i<pois.size();i++){
				poi=pois.elementAt(i);
				JSONObject jsonObject = JSONObject.fromObject(poi);
				
				//Object price = jsonObject.get("price");				
				//Object area = jsonObject.get("area");
				//Object house_type=jsonObject.get("house_type");
				Object unit_price=jsonObject.get("unit_price");
				
				UnitPrice.add(unit_price);
				
				/*
				String total1=price.toString()+","+area.toString()+","+house_type.toString()+","+unit_price.toString();
                FileTool.Dump(total1, file.replace(".txt", "") + "-unitprice.txt", "utf-8");
                */
                
              }
			
			int pricecounts=0;
			for(int k=0;k<UnitPrice.size();k++){
				double unitprice=Double.parseDouble(UnitPrice.get(k).toString());
				if(unitprice!=0){
					total+=unitprice;
					pricecounts++;
				}
				
			}			
			average=total/pricecounts;
			System.out.println(average);
			FileTool.Dump(String.valueOf(average), "D:/Crawldata_BeiJing/5i5j/resold/source/unitprice.txt", "utf-8");

		}catch(net.sf.json.JSONException e){
			FileTool.Dump(poi, file.replace(".txt", "") + "-exception.txt", "utf-8");
		}
		
	}
	/**
	 * 将某一个code的房源全部挑出来进行分析研究
	 * @param file
	 */
	public static void getCode(String file,int index){
		
		Vector<String> pois = FileTool.Load(file, "utf-8");
		String poi="";
		try{
			for(int i=0;i<pois.size();i++){
				poi=pois.elementAt(i);
				JSONObject jsonObject = JSONObject.fromObject(poi);
				Object code = jsonObject.get("code");
				String codestr = code.toString();
				int codeint = Integer.parseInt(codestr);
				if(codeint==index){
					FileTool.Dump(poi, file.replace(".txt", "") + "-"+index+".txt", "utf-8");
				}
			}

		}catch(net.sf.json.JSONException e){
			FileTool.Dump(poi, file.replace(".txt", "") + "-exception.txt", "utf-8");
		}
		
		
	}
	/**
	 * 
	 * @param folder
	 */
	public static void toJson(String folder){

		Vector<String> pois=FileTool.Load(folder, "utf-8");
		boolean fang=false;
		String poi="";
	try{
		int n=pois.size();
		for(int i=0;i<pois.size();i++){
			poi=pois.elementAt(i);
			JSONObject jsonObjArr = new JSONObject();

			//title ：标题
			if(poi.indexOf("TITLE")!=-1){
				title=Tool.getStrByKey(poi, "<TITLE>", "</TITLE>", "</TITLE>");
				jsonObjArr.put("title",title);
			}else{
				title="null";
				jsonObjArr.put("title",title);
			}
			
			//longitude :经度 latitude ：纬度
			if(poi.indexOf("Coordinate")!=-1){
				String[] coor=Tool.getStrByKey(poi, "<Coordinate>", "</Coordinate>", "</Coordinate>").split(";");
				longitude=coor[0];
				latitude=coor[1];
				jsonObjArr.put("longitude",longitude);
				jsonObjArr.put("latitude",latitude);
			}else if(poi.indexOf("Coor")!=-1){
				String[] coor=Tool.getStrByKey(poi, "<Coor>", "</Coor>", "</Coor>").split(";");
				longitude=coor[0];
				latitude=coor[1];
				jsonObjArr.put("longitude",longitude);
				jsonObjArr.put("latitude",latitude);
			}else{
				longitude="null";
				latitude="null";
				jsonObjArr.put("longitude",longitude);
				jsonObjArr.put("latitude",latitude);
			}
			
			//region：坐标所定位的行政区划层级
			if(poi.indexOf("Reg")!=-1){
				region=Tool.getStrByKey(poi, "<Reg>", "</Reg>", "</Reg>");
				jsonObjArr.put("region",region);
			}else{
				region="regionnull";
				jsonObjArr.put("region",region);
			}
			
			//time ：时间
			if(poi.indexOf("TIME")!=-1){
				time=Tool.getStrByKey(poi, "<TIME>", "</TIME>", "</TIME>");
				jsonObjArr.put("time",time);
			}else{
				time="null";
				jsonObjArr.put("time",time);
			}
			
			//price ：总价
			if(poi.indexOf("PRICE")!=-1){
				price=Tool.getStrByKey(poi, "<PRICE>", "</PRICE>", "</PRICE>").replace("元/月","").replace("未知", "");
				jsonObjArr.put("price",price);
			}else{
				price="null";
				jsonObjArr.put("price",price);
			}
			
			//down_payment：最低首付
			if(poi.indexOf("DOWN_PAYMENT")!=-1){
				down_payment=Tool.getStrByKey(poi, "<DOWN_PAYMENT>", "</DOWN_PAYMENT>", "</DOWN_PAYMENT>").replace("元/月","").replace("未知", "");
				jsonObjArr.put("down_payment",down_payment);
			}else{
				down_payment="null";
				jsonObjArr.put("down_payment",down_payment);
			}
			
			//month_payment:月供
			if(poi.indexOf("MONTH_PAYMENT")!=-1){
				month_payment=Tool.getStrByKey(poi, "<MONTH_PAYMENT>", "</MONTH_PAYMENT>", "</MONTH_PAYMENT>").replace("元/月","").replace("未知", "");
				jsonObjArr.put("month_payment",month_payment);
			}else{
				month_payment="null";
				jsonObjArr.put("month_payment",month_payment);
			}
			
			
			//area ：面积
			if(fang){
				url=Tool.getStrByKey(poi, "<URL>", "</URL>", "</URL>");
				area=getArea(url).replace("平米", "").replace("m²", "").replace("平方米", "");
				jsonObjArr.put("area",area);
				
			}else{
				if(poi.indexOf("AREA")!=-1&&poi.indexOf("HOUSE_AREA")==-1){
					area=Tool.getStrByKey(poi, "<AREA>", "</AREA>", "</AREA>").replace("平米", "").replace("m²", "").replace("平方米", "");
					if(area.equals("0")){
						area="null";
					}
					jsonObjArr.put("area",area);
				}else if(poi.indexOf("HOUSE_AREA")!=-1){
					area=Tool.getStrByKey(poi, "<HOUSE_AREA>", "</HOUSE_AREA>", "</HOUSE_AREA>").replace("平米", "").replace("m²", "").replace("平方米", "");
					jsonObjArr.put("area",area);
				}else{
					area="null";
					jsonObjArr.put("area",area);
				}
			}	
			
			
			
			//unit_price ：每平方米单价
			if(!(price.equals("null"))&&!(area.equals("null"))){
				if(price.indexOf("万元")!=-1||price.indexOf("万")!=-1){
					unit_price=Double.parseDouble(price.replace("万元", "").replace("万", ""))/Double.parseDouble(area)*1000;
					jsonObjArr.put("unit_price",unit_price);
				}else{
					unit_price=Double.parseDouble(price)/Double.parseDouble(area);
					jsonObjArr.put("unit_price",unit_price);
				}
				
			}else if(poi.indexOf("UNIT_PRICE")!=-1){
				unit_price=Double.parseDouble(Tool.getStrByKey(poi, "<UNIT_PRICE>", "</UNIT_PRICE>", "</UNIT_PRICE>").replace("元", ""));
				jsonObjArr.put("unit_price",unit_price);
			}else{
				unit_price=0;
				jsonObjArr.put("unit_price",unit_price);
			}
			
			//location：所在区域
			if(poi.indexOf("LOCATION")!=-1){
				location=Tool.getStrByKey(poi, "<LOCATION>", "</LOCATION>", "</LOCATION>");
				jsonObjArr.put("location",location);
			}else if(poi.indexOf("REGION")!=-1){
				location=Tool.getStrByKey(poi, "<REGION>", "</REGION>", "</REGION>");
				jsonObjArr.put("location",location);
			}else{
				location="null";
				jsonObjArr.put("location",location);
			}
			
			//cmmunity ：所在小区
			if(poi.indexOf("COMMUNITY")!=-1){
				community=Tool.getStrByKey(poi, "<COMMUNITY>", "</COMMUNITY>", "<COMMUNITY>");
				jsonObjArr.put("community",community);
			}else{
				community="null";
				jsonObjArr.put("community",community);
			}
			
			//address ：地址
			if(poi.indexOf("ADDRESS")!=-1){
				address=Tool.getStrByKey(poi, "<ADDRESS>", "</ADDRESS>", "</ADDRESS>").replace("(地图)", "");
				jsonObjArr.put("address",address);
			}else{
				address="null";
				jsonObjArr.put("address",address);
			}
			
			//property：房屋性质（住宅之类的）
			if(poi.indexOf("PROPERTY_TYPE")!=-1){
				property=Tool.getStrByKey(poi, "<PROPERTY_TYPE>", "</PROPERTY_TYPE>", "</PROPERTY_TYPE>");
				jsonObjArr.put("property",property);
			}else if(poi.indexOf("TYPE")!=-1){
				property=Tool.getStrByKey(poi, "<TYPE>", "</TYPE>", "</TYPE>");
				jsonObjArr.put("property",property);
			}else{
				property="null";
				jsonObjArr.put("property",property);
			}
			
			//pay_way ： 付款方式
			if(poi.indexOf("DEPOSIT")!=-1){
				pay_way=Tool.getStrByKey(poi, "<DEPOSIT>", "</DEPOSIT>", "</DEPOSIT>");
				jsonObjArr.put("pay_way",pay_way);
			}else{
				pay_way="null";
				jsonObjArr.put("pay_way",pay_way);
			}
			
			//house_type：户型
			if(poi.indexOf("HOUSE_TYPE")!=-1){
				house_type=Tool.getStrByKey(poi, "<HOUSE_TYPE>", "</HOUSE_TYPE>", "</HOUSE_TYPE>");
				jsonObjArr.put("house_type",house_type);
			}else{
				house_type="null";
				jsonObjArr.put("house_type",house_type);
			}
			
			//rent_type： 求租方式
			if(poi.indexOf("PARTMENT")!=-1){
				rent_type=Tool.getStrByKey(poi, "<PARTMENT>", "</PARTMENT>", "</PARTMENT>");
				jsonObjArr.put("rent_type",rent_type);
			}else if(poi.indexOf("RENT_TYPE")!=-1){
				rent_type=Tool.getStrByKey(poi, "<RENT_TYPE>", "</RENT_TYPE>", "</RENT_TYPE>");
				jsonObjArr.put("rent_type",rent_type);
			}else{
				rent_type="null";
				jsonObjArr.put("rent_type",rent_type);
			}
			
			//fitment ：装修	
			if(poi.indexOf("DECORATION")!=-1){
				fitment=Tool.getStrByKey(poi, "<DECORATION>", "</DECORATION>", "</DECORATION>");
				jsonObjArr.put("fitment",fitment);
			}else if(poi.indexOf("FITMENT")!=-1){
				fitment=Tool.getStrByKey(poi, "<FITMENT>", "</FITMENT>", "</FITMENT>");
				jsonObjArr.put("fitment",fitment);
			}else{
				fitment="null";
				jsonObjArr.put("fitment",fitment);
			}
			
			
			//direction ：朝向
			if(poi.indexOf("DIRECTION")!=-1){
				direction=Tool.getStrByKey(poi, "<DIRECTION>", "</DIRECTION>", "</DIRECTION>");
				jsonObjArr.put("direction",direction);
			}else if(poi.indexOf("ORIENTATION")!=-1){
				direction=Tool.getStrByKey(poi, "<ORIENTATION>", "</ORIENTATION>", "</ORIENTATION>");
				jsonObjArr.put("direction",direction);
			}else{
				direction="null";
				jsonObjArr.put("direction",direction);
			}

		
			
			//floor ：楼层
			if(poi.indexOf("FLOOR")!=-1){
				floor=Tool.getStrByKey(poi, "<FLOOR>", "</FLOOR>", "</FLOOR>");
				jsonObjArr.put("floor",floor);
			}else{
				floor="null";
				jsonObjArr.put("floor",floor);
			}
			
			//totalarea:总面积
			if(poi.indexOf("TOTAL_AREA")!=-1){
				totalarea=Tool.getStrByKey(poi, "<TOTAL_AREA>", "</TOTAL_AREA>", "</TOTAL_AREA>");
				jsonObjArr.put("totalarea",totalarea);
			}else{
				totalarea="null";
				jsonObjArr.put("totalarea",totalarea);
			}
			
			//developer：开发商
			if(poi.indexOf("DEVELOPER")!=-1){
				developer=Tool.getStrByKey(poi, "<DEVELOPER>", "</DEVELOPER>", "</DEVELOPER>");
				jsonObjArr.put("developer",developer);
			}else{
				developer="null";
				jsonObjArr.put("developer",developer);
			}
			
			//property_company:物业公司
			if(poi.indexOf("PROPERTY")!=-1){
				property_company=Tool.getStrByKey(poi, "<PROPERTY>", "</PROPERTY>", "</PROPERTY>");
				jsonObjArr.put("property_company",property_company);
			}else{
				property_company="null";
				jsonObjArr.put("property_company",property_company);
			}
			
			//property_fee:物业费
			if(poi.indexOf("PROPERTY_FEE")!=-1){
				property_fee=Tool.getStrByKey(poi, "<PROPERTY_FEE>", "</PROPERTY_FEE>", "</PROPERTY_FEE>");
				jsonObjArr.put("property_fee",property_fee);
			}else{
				title="null";
				jsonObjArr.put("property_fee",property_fee);
			}
			
			//households：总户数
			if(poi.indexOf("HOUSEHOLDS")!=-1){
				households=Tool.getStrByKey(poi, "<HOUSEHOLDS>", "</HOUSEHOLDS>", "</HOUSEHOLDS>");
				jsonObjArr.put("households",households);
			}else{
				households="null";
				jsonObjArr.put("households",households);
			}
			
			//built_year:建筑年代
			if(poi.indexOf("BUILT_YEAR")!=-1){
				built_year=Tool.getStrByKey(poi, "<BUILT_YEAR>", "</BUILT_YEAR>", "</BUILT_YEAR>");
				jsonObjArr.put("built_year",built_year);
			}else if(poi.indexOf("BUILT_Date")!=-1){
				built_year=Tool.getStrByKey(poi, "<BUILT_Date>", "</BUILT_Date>", "</BUILT_Date>");
				jsonObjArr.put("built_year",built_year);
			}else{
				built_year="null";
				jsonObjArr.put("built_year",built_year);
			}
				
			//volume_rate:容积率
			if(poi.indexOf("VOLUME_RATE")!=-1){
				volume_rate=Tool.getStrByKey(poi, "<VOLUME_RATE>", "</VOLUME_RATE>", "</VOLUME_RATE>");
				jsonObjArr.put("volume_rate",volume_rate);
			}else{
				volume_rate="null";
				jsonObjArr.put("volume_rate",volume_rate);
			}
			
			//green_rate:绿化率
			if(poi.indexOf("GREEN_RATE")!=-1){
				green_rate=Tool.getStrByKey(poi, "<GREEN_RATE>", "</GREEN_RATE>", "</GREEN_RATE>");
				jsonObjArr.put("green_rate",green_rate);
			}else{
				green_rate="null";
				jsonObjArr.put("green_rate",green_rate);
			}
			
			//park：停车位
			if(poi.indexOf("PARK")!=-1){
				park=Tool.getStrByKey(poi, "<PARK>", "</PARK>", "</PARK>");
				jsonObjArr.put("park",park);
			}else{
				park="null";
				jsonObjArr.put("park",park);
			}
			
			//heat_supply:供暖方式
			if(poi.indexOf("HEAT_SUPPLY")!=-1){
				heat_supply=Tool.getStrByKey(poi, "<HEAT_SUPPLY>", "</HEAT_SUPPLY>", "</HEAT_SUPPLY>");
				jsonObjArr.put("heat_supply",heat_supply);
			}else{
				heat_supply="null";
				jsonObjArr.put("heat_supply",heat_supply);
			}
			
			//url ：链接		
			if(poi.indexOf("URL")!=-1){
				url=Tool.getStrByKey(poi, "<URL>", "</URL>", "</URL>");
				jsonObjArr.put("url",url);
			}else{
				url="null";
				jsonObjArr.put("url",url);
			}
			
			/*
			if(poi.indexOf("")!=-1){
				=Tool.getStrByKey(poi, "", "", "");
				jsonObjArr.put("",);
			}else{
				title="null";
				jsonObjArr.put("",);
			}
			
			if(poi.indexOf("")!=-1){
				=Tool.getStrByKey(poi, "", "", "");
				jsonObjArr.put("",);
			}else{
				title="null";
				jsonObjArr.put("",);
			}
			*/
						
		System.out.println(i+":"+jsonObjArr.toString());	
		FileTool.Dump(jsonObjArr.toString(), folder + "-Json.txt", "utf-8");
		}
		
	}catch(JSONException e){
		e.getStackTrace();
	}catch(NumberFormatException e){
		System.out.println(e.getMessage());
		System.out.println(poi);
	}
}
	public static String getArea(String url){

		String area="";
		int monitor=0;
		try {
			String content = Tool.fetchURL(url);
			//String  content = HTMLTool.fetchURL(url, "gbk", "get");
			Parser parser = new Parser();
			if (content == null) {
				FileTool.Dump(url, folder + "-Null.txt", "utf-8");
			} else {
                parser.setInputHTML(content);
				parser.setEncoding("utf-8");
				HasParentFilter parentFilter1 = new HasParentFilter(new AndFilter(new TagNameFilter("ul"),new HasAttributeFilter("class", "Huxing floatl")));
			    HasParentFilter parentFilter2 = new HasParentFilter(new AndFilter(new TagNameFilter("li"),parentFilter1));
			   // HasParentFilter parentFilter3 = new HasParentFilter(new AndFilter(new TagNameFilter("li"),parentFilter2));
				NodeFilter filter = new AndFilter(new TagNameFilter("p"),new AndFilter(new HasAttributeFilter("class", "info"),parentFilter2));//new HasAttributeFilter("class", "info")
				NodeList nodes = parser.extractAllNodesThatMatch(filter);
				if (nodes.size() != 0) {
					for (int n = 0; n < nodes.size(); n++) {
						TagNode no = (TagNode) nodes.elementAt(n);
						String str=no.toPlainTextString().replace(" ", "").replace("\r\n", "").replace("\t", "").replace("\n", "").trim();
						if(str.indexOf("平方米")!=-1){
							area=str;
							System.out.println(str);
							monitor=1;
						}
						
					}
				}
				parser.reset();
				if(monitor==0){
					parentFilter1 = new HasParentFilter(new AndFilter(new TagNameFilter("ul"),new HasAttributeFilter("class", "house-info")));
					parentFilter2 = new HasParentFilter(new AndFilter(new TagNameFilter("li"),parentFilter1));
					filter = new AndFilter(new TagNameFilter("span"),new AndFilter(new HasAttributeFilter("title", "建筑面积"),parentFilter2));
					nodes = parser.extractAllNodesThatMatch(filter);
					if (nodes.size() != 0) {
						for (int n = 0; n < nodes.size(); n++) {
							TagNode no = (TagNode) nodes.elementAt(n);
							String str=no.toPlainTextString().replace(" ", "").replace("\r\n", "").replace("\t", "").replace("\n", "").trim();
							if(str.indexOf("m²")!=-1){
								area=str;
								System.out.println(str);
								monitor=1;
							}
						}
					}
				}
				
				if(monitor==0){
					area="null";
				}
				
			
			}
			
			
		}catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
			FileTool.Dump(url, folder + "NullLink.txt", "utf-8");
		}catch(FailingHttpStatusCodeException e){
			System.out.println(e.getMessage());
		}
		return area;
	}

	/**
	 * 统计每个网格编码所含有房源的数目
	 * 
	 * @param file
	 */
	public static void codeStatistic(String file) {
		Vector<String> pois = FileTool.Load(file, "utf-8");
		Map<Integer, Integer> Codes = new HashMap<Integer, Integer>();
		String poi = "";

		for (int k = 0; k < codes.size(); k++) {
			int Code = codes.get(k).code;
			Codes.put(Code, 0);

		}
		int i=0;
		try {

			for (i=0; i < pois.size(); i++) {
				poi = pois.elementAt(i);
				JSONObject jsonObject = JSONObject.fromObject(poi);
				Object code = jsonObject.get("code");
				String codestr = code.toString();
				int codeint = Integer.parseInt(codestr);

				int value = Codes.get(codeint);
				value++;
				Codes.put(codeint, value);

			}
		} catch (net.sf.json.JSONException e) {
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            System.out.println(i+":"+poi);
            FileTool.Dump(poi, file.replace(".txt", "")+"-exception.txt", "utf-8");
		}
       
		int count = 0;
		String[] codes=new String[Codes.size()];
		int[] counts=new int[Codes.size()];
		
		for (Map.Entry<Integer, Integer> entry : Codes.entrySet()) {
			
			if(entry.getValue()!=0){
				//System.out.println(entry.getKey() + " : " + entry.getValue());	
				counts[count]=entry.getValue();
				codes[count]=entry.getKey() + " : " + entry.getValue();
				count++;
				
			}	
		}
		
		Tool.InsertSortArray_Descending(codes.length, counts,codes);
		for (int m = 0; m < codes.length; m++) {
			if(codes[m]!=null){
				System.out.println(codes[m]);
				FileTool.Dump(codes[m], file.replace(".txt", "")+"-codecount.txt", "utf-8");
			}
			
		}

	}

	/**
	 * 根据每条记录的经纬度计算其所在的网格编码
	 * 
	 * @param file
	 */
	public static void addCode(String file) {
		Vector<String> pois = FileTool.Load(file, "utf-8");
		String poi = "";
		for (int i = 0; i < pois.size(); i++) {
			poi = pois.elementAt(i);
			JSONObject jsonObject = JSONObject.fromObject(poi);

			int code = setPoiCode(poi, 1000);
			jsonObject.put("code", code);

			String str = jsonObject.toString().replace("\\", "").replace("\"\"", "\"");
			System.out.println(str);
			FileTool.Dump(str, file.replace(".txt", "") + "-1000.txt", "utf-8");
		}
	}

	/**
	 * 给每个网格设置编码，从1开始编码
	 */
	public static void setCode() {
		/*
		 * 将北京的东北角和西南角的坐标转换成平面坐标 BLToGauss(117.500126,41.059244)
		 * BLToGauss(115.417284,39.438283) BLToGauss: 2.0542041271351546E7
		 * 4547353.496401368 BLToGauss: 2.036373920422157E7 4368434.982578722
		 * 两点之间的距离是:252593.47127613405 178302.06712997705 178918.51382264588
		 * 
		 */

		rows = (int) Math.ceil((X_MAX - X_MIN) / 1000);
		cols = (int) Math.ceil((Y_MAX - Y_MIN) / 1000);

		// System.out.print("d_x:"+rows+"\r\n"+"d_y:"+cols+"\r\n");

		// 创建栅格编码
		int mm = 1;
		for (int rr = 1; rr <= rows; rr++) {
			for (int cc = 1; cc <= cols; cc++) {
				Code c = new Code();
				c.setRow(rr);
				c.setCol(cc);
				c.setCode(mm);
				mm++;
				addCode(c);
				// System.out.println(c.getCode(rr, cc));
			}
		}
		// int k=200;
		// for(int k=0;k<codes.size();k++){
		// System.out.print("codes的第"+k+"个数是:"+codes.get(k).row+","+codes.get(k).col+","+codes.get(k).code+"\r\n");

		// }

	}

	/**
	 * 根据所写入的每条poi得知它所在的编码
	 * 
	 * @param file
	 *            需要处理的文件
	 * @param resolution
	 *            设置的网格分辨率
	 */
	public static int setPoiCode(String poi, int resolution) {

		int code;
		JSONObject jsonObject = JSONObject.fromObject(poi);
		String latitude = (String) jsonObject.get("latitude");
		String longitude = (String) jsonObject.get("longitude");

		LNG = Double.parseDouble(longitude);
		LAT = Double.parseDouble(latitude);
		double[] Coordinate = new double[2];
		Coordinate = Coordinate_Transformation.BLToGauss(LNG, LAT);

		double X = Coordinate[0];
		double Y = Coordinate[1];

		int row = (int) Math.ceil((X - X_MIN) / resolution); // (X_MAX - X) /
																// resolution

		int col = (int) Math.ceil((Y - Y_MIN) / resolution);
		int index = (col + cols * (row - 1)); // index=(row-1)*cols+col
												// 依据行列数算出某行某列对应的编码

		code = codes.get(index + 1).code; // 由于codes中的第0个数的编码为1，故所有的index需要加1

		// 以中央子午线的投影为纵坐标轴x，规定x轴向北为正；以赤道的投影为横坐标轴y，规定y轴向东为正。
		return code;
	}

	public static ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
	public static ArrayList<Code> codes = new ArrayList<Code>();
	public static ArrayList<Price> prices = new ArrayList<Price>();
	public static ArrayList<Integer> tempcodes = new ArrayList();

	public static void addDataPoint(DataPoint p) {
		dataPoints.add(p);
	}

	public static void addCode(Code c) {
		codes.add(c);
	}

	public static void addPrice(Price pr, double pricedata, String address, String poi) {
		int count = 1;
		if (prices.size() > 0) {
			for (int num = 0; num < prices.size(); num++) {
				if (pr.code == prices.get(num).code && count == 1) {
					prices.get(num).setPrice(pricedata);
					prices.get(num).setAddress(address);
					prices.get(num).setPois(poi);
					count++;
				}
			}
			if (count == 1) {
				prices.add(pr);
				count++;
			}
		} else {
			prices.add(pr);
			count++;
		}

	}

	public static double getGridValue(int i) {

		int size = prices.get(i).price_vet.size();
		double sum = 0;
		for (int k = 0; k < size; k++) {
			sum += prices.get(i).price_vet.get(k);
		}
		return sum / size;

	}

	/**
	 * 将每一条记录放入动态数组dataPoints中
	 */
	// setDataPoint("D:/Test/41403-poi.txt");
	public static void setDataPoint(String folder) {
		String poi = "";
		double[] Coordinate = new double[2];
		Vector<String> fang = FileTool.Load(folder, "UTF-8");
		try {
			for (int m = 0; m < fang.size(); m++) {
				poi = fang.elementAt(m);

				if (poi.indexOf("<Coordinate>") != -1) {

					COORDINATE = Tool.getStrByKey(poi, "<Coordinate>", "</Coordinate>", "</Coordinate>");
					if (COORDINATE != null) {
						String[] coordinate = COORDINATE.split(";");
						LNG = Double.parseDouble(coordinate[0]);
						LAT = Double.parseDouble(coordinate[1]);
						Coordinate = Coordinate_Transformation.BLToGauss(LNG, LAT);
						// System.out.println(LNG+";"+LAT+":
						// X="+test[0]+"Y="+test[1]);

					}
					if (poi.indexOf("<PRICE>") != -1) {
						PRICE = Double.parseDouble(Tool.getStrByKey(poi, "<PRICE>", "</PRICE>", "</PRICE>")
								.replace("元/月", "").replace("[面议]", "").replace("[押一付三]", ""));
					} else {
						PRICE = 0.0;
					}
					if (poi.indexOf("<ADDRESS>") != -1) {
						ADDRESS = Tool.getStrByKey(poi, "<ADDRESS>", "</ADDRESS>", "</ADDRESS>");
					} else {
						ADDRESS = "未知";
					}

					DataPoint p = new DataPoint();
					p.setX(Coordinate[0]);
					p.setY(Coordinate[1]);
					p.setData(PRICE);
					p.setAddress(ADDRESS);
					p.setPoi(poi);
					addDataPoint(p);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println(poi);
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 计算出dataPoints中每条记录所在的网格,并将价格存于网格
	 * 
	 * @param datafolder
	 *            待处理的数据
	 * @param resolution
	 *            网格分辨率
	 */
	// DataGrid("D:/Test/41403-poi.txt",2000);
	public static void DataGrid(String datafolder, int resolution) {

		setDataPoint(datafolder);

		// System.out.print("dataPoints的大小是:"+n+"\r\n");
		// for(int k=0;k<n;k++){
		// System.out.print("dataPoints的第"+k+"个数是:"+dataPoints.get(k).y+","+dataPoints.get(k).x+"\r\n");

		// }

		// 计算每个点所在的行列号以及编码,存储于动态数组 price
		try {
			int row = 0;
			int col = 0;
			int tt = 0;

			int size = dataPoints.size();
			for (int k = 0; k < size; k++) {
				row = (int) Math.ceil((X_MAX - dataPoints.get(k).x) / resolution);
				col = (int) Math.ceil((dataPoints.get(k).y - Y_MIN) / resolution);
				if (row == 0)
					row += 1;
				if (col == 0)
					col += 1;

				int index = (col + cols * (row - 1) - 1);
				int code = codes.get(index).code;
				double price = dataPoints.get(k).data;
				String address = dataPoints.get(k).address;

				System.out.print("dataPoints的第" + k + "个数所在的行列号是:" + row + "行" + col + "列" + "编码是" + code + "价格是"
						+ price + "\r\n");
				Price pr = new Price();
				pr.setCode(code);
				pr.setPrice(price);
				pr.setAddress(address);
				String poi1 = dataPoints.get(k).poi;
				pr.setPois(poi1);
				addPrice(pr, dataPoints.get(k).data, dataPoints.get(k).address, dataPoints.get(k).poi);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println();
			System.out.println(e.getMessage()); // 抛出异常的是位于行列线上的点

		}

		/**
		 * 此处是为了找出某个网格的所有记录
		 */
		for (int ii = 0; ii < prices.size(); ii++) {
			// System.out.println((int)price.get(ii).code+":"+price.get(ii).price_vet+price.get(ii).address_vet);
			// System.out.println(getGridValue(ii));

			if ((int) prices.get(ii).code == 3946) {
				System.out.println(prices.get(ii).pois.size());
				System.out.println(prices.get(ii).price_vet.size());
				for (int a = 0; a < prices.get(ii).pois.size(); a++) {
					// System.out.println(price.get(ii).pois.elementAt(a));
					FileTool.Dump(prices.get(ii).pois.elementAt(a), "D:/Test/3946-poi.txt", "utf-8");
					// System.out.println(price.get(ii).price_vet.elementAt(a));
				}
				for (int a = 0; a < prices.get(ii).price_vet.size(); a++) {

					// FileTool.Dump(price.get(ii).price_vet,
					// "D:/Test/41403-price.txt", "utf-8");
					// System.out.println(price.get(ii).price_vet.elementAt(a));
				}

				FileTool.Dump((int) prices.get(ii).code + ":" + prices.get(ii).price_vet, "D:/Test/3946-price.txt",
						"utf-8");
			}

		}
		System.out.println("OK!");
	}

	/**
	 * 将网格可视化出来
	 * 
	 * @param rows
	 * @param cols
	 * @param hgap
	 * @param vgap
	 * @param top
	 * @param bottom
	 * @param right
	 * @param left
	 */
	public GridLayoutLines(int rows, int cols, int hgap, int vgap, int top, int bottom, int right, int left) {

		// 构建网格框架

		JPanel grid = new JPanel(new GridLayout(rows, cols, hgap, vgap));
		grid.setBackground(Color.gray);// Color.gray
		/*
		 * JLabel l=new JLabel(); Icon icon=new ImageIcon("D:/ZX/01.jpg");
		 * l.setIcon(icon); l.setBounds(0, 0, 2000,1000); grid.add(l,new
		 * Integer(Integer.MIN_VALUE));
		 */

		for (int m = 1; m <= rows * cols; m++) {
			// grid.setBackground(Color.gray);//Color.gray
			grid.setBorder(new MatteBorder(top, left, bottom, right, Color.BLACK));
			JLabel label = new JLabel();
			// label.setBackground(Color.gray);//Color.gray
			label.setBorder(BorderFactory.createLineBorder(Color.gray));
			label.setOpaque(true);
			// label.setText(Integer.toString(m));
			for (int xx = 0; xx < prices.size(); xx++) {
				if (m == (int) prices.get(xx).code) {
					label.setText(Double.toString(prices.get(xx).price_vet.get(0))); // Double.toString(getGridValue(xx))

					// *给网格赋背景颜色
					if (getGridValue(xx) > 30000) {
						label.setBackground(Color.red);// Color.gray
						label.setOpaque(true);
						// System.out.println("红色的为:"+price.get(xx).address_vet.get(0));
					}
					if (getGridValue(xx) > 20000 && getGridValue(xx) <= 30000) {
						label.setBackground(Color.ORANGE);// Color.gray
						label.setOpaque(true);
						// System.out.println("橙色的为:"+price.get(xx).address_vet.get(0));
					}
					if (getGridValue(xx) > 10000 && getGridValue(xx) <= 20000) {
						label.setBackground(Color.pink);// Color.gray
						label.setOpaque(true);
						// System.out.println("粉色的为:"+price.get(xx).address_vet.get(0));
					}
					if (getGridValue(xx) > 5000 && getGridValue(xx) <= 10000) {
						label.setBackground(Color.gray);// Color.gray
						label.setOpaque(true);
						// System.out.println("灰色的为:"+price.get(xx).address_vet.get(0));
					}
					if (getGridValue(xx) > 2500 && getGridValue(xx) <= 5000) {
						label.setBackground(Color.green);// Color.gray
						label.setOpaque(true);
						// System.out.println("绿色的为:"+price.get(xx).address_vet.get(0));
					}

					continue;
				}
			}
			// label.setOpaque( true );
			grid.add(label);
		}
		System.out.println("总共有" + prices.size() + "个格网填充了数据");
		add(grid);

	}

	public static Double getXMax() {
		double X_MAX = 0;
		for (int i = 0; i < dataPoints.size(); i++) {
			if (dataPoints.get(i).x > X_MAX)
				X_MAX = dataPoints.get(i).x;
		}
		return X_MAX;
	}

	public static Double getXMin() {
		double X_MIN = dataPoints.get(0).x;
		for (int i = 1; i < dataPoints.size(); i++) {
			if (dataPoints.get(i).x < X_MIN)
				X_MIN = dataPoints.get(i).x;
		}
		return X_MIN;
	}

	public static Double getYMax() {
		double Y_MAX = 0;
		for (int i = 0; i < dataPoints.size(); i++) {
			if (dataPoints.get(i).y > Y_MAX)
				Y_MAX = dataPoints.get(i).y;
		}
		return Y_MAX;
	}

	public static Double getYMin() {
		double Y_MIN = dataPoints.get(0).y;
		for (int i = 1; i < dataPoints.size(); i++) {
			if (dataPoints.get(i).y < Y_MIN)
				Y_MIN = dataPoints.get(i).y;
		}
		return Y_MIN;
	}

}
