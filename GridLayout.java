package com.svail.gridprocess;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import com.svail.util.FileTool;
import com.svail.util.Tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GridLayoutLines extends JFrame {
	public static String COORDINATE;
	public static Double LNG;
	public static Double LAT;
	public static Double PRICE;
	public static String ADDRESS;

	public static void main(String[] args) throws IOException {
		
		setCode();
		DataGrid("D:/Test/41403-poi.txt",20000);
		GridLayoutLines frame = new GridLayoutLines(rows, cols, 2, 2, 2, 2, 2, 2);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
	public static ArrayList<Code> codes = new ArrayList<Code>();
	public static ArrayList<Price> price = new ArrayList<Price>();
	public static ArrayList<Integer> tempcodes = new ArrayList();

	public static void addDataPoint(DataPoint p) {
		dataPoints.add(p);
	}

	public static void addCode(Code c) {
		codes.add(c);
	}

	public static void addPrice(Price pr, double pricedata, String address, String poi) {
		int count = 1;
		if (price.size() > 0) {
			for (int num = 0; num < price.size(); num++) {
				if (pr.code == price.get(num).code && count == 1) {
					price.get(num).setPrice(pricedata);
					price.get(num).setAddress(address);
					price.get(num).setPois(poi);
					count++;
				}
			}
			if (count == 1) {
				price.add(pr);
				count++;
			}
		} else {
			price.add(pr);
			count++;
		}

	}

	public static double getGridValue(int i) {

		int size = price.get(i).price_vet.size();
		double sum = 0;
		for (int k = 0; k < size; k++) {
			sum += price.get(i).price_vet.get(k);
		}
		return sum / size;

	}
	public static Double X_MAX=2.0542041271351546E7;
	public static Double X_MIN = 2.036373920422157E7;
	public static Double Y_MAX = 4547353.496401368;
	public static Double Y_MIN = 4368434.982578722;
	public static int rows;
	public static int cols;
	/**
	 * 给每个网格设置编码
	 */
    public static void setCode(){
    	/* 将北京的东北角和西南角的坐标转换成平面坐标
		 * BLToGauss(117.500126,41.059244)
		 * BLToGauss(115.417284,39.438283)
		 * BLToGauss: 2.0542041271351546E7 4547353.496401368 
		 * BLToGauss: 2.036373920422157E7 4368434.982578722
		 * 两点之间的距离是:252593.47127613405
		 * 178302.06712997705 178918.51382264588
		 * 
		 */

		rows = (int) Math.ceil((X_MAX - X_MIN) / 1000);
		cols = (int) Math.ceil((Y_MAX - Y_MIN) / 1000);
		
		// System.out.print("d_x:"+rows+"\r\n"+"d_y:"+cols+"\r\n");

		// 创建栅格编码
		int mm = 1;
		for (int rr = rows; rr >= 1; rr--) {
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
		// for(int k=0;k<codes.size();k++){
		// System.out.print("codes的第"+k+"个数是:"+codes.get(k).row+","+codes.get(k).col+","+codes.get(k).code+"\r\n");

		// }
    	
    }
    /**
     * 将每一条记录放入动态数组dataPoints中
     */
    //setDataPoint("D:/Test/41403-poi.txt");
    public static void setDataPoint(String folder){
    	String poi = "";
		double[] Coordinate = new double[2];
		Vector<String> fang = FileTool.Load(folder, "UTF-8");
		try {
			for (int m = 0; m < fang.size(); m++) {
				poi = fang.elementAt(m);

				if (poi.indexOf("<coordinate>") != -1) {

					COORDINATE = Tool.getStrByKey(poi, "<coordinate>", "</coordinate>","</coordinate>");
					if (COORDINATE != null) {
						String[] coordinate = COORDINATE.split(";");
						LNG = Double.parseDouble(coordinate[0]);
						LAT = Double.parseDouble(coordinate[1]);
						Coordinate = Coordinate_Transformation.BLToGauss(LNG, LAT);
						// System.out.println(LNG+";"+LAT+":
						// X="+test[0]+"Y="+test[1]);

					}
					if (poi.indexOf("<PRICE>") != -1) {
						PRICE = Double.parseDouble(Tool.getStrByKey(poi, "<PRICE>", "</PRICE>", "</PRICE>").replace("元/月", "")
								.replace("[面议]", "").replace("[押一付三]", ""));
					} else {
						PRICE = 0.0;
					}
					if (poi.indexOf("<LOCATION>") != -1) {
						ADDRESS = Tool.getStrByKey(poi, "<LOCATION>", "</LOCATION>", "</LOCATION>").replace("元/月", "").replace("[面议]", "")
								.replace("[押一付三]", "");
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
     * @param datafolder  待处理的数据
     * @param resolution  网格分辨率
     */
    //DataGrid("D:/Test/41403-poi.txt",2000);
	public static void DataGrid(String datafolder,int resolution) {
		
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
				
				int index=(col+cols*(row-1)-1);
				int code=codes.get(index).code;
				double price=dataPoints.get(k).data;
				String address = dataPoints.get(k).address;
				
				System.out.print("dataPoints的第"+k+"个数所在的行列号是:"+row+"行"+col+"列"+"编码是"+code+"价格是"+price+"\r\n");
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
		for (int ii = 0; ii < price.size(); ii++) {
			// System.out.println((int)price.get(ii).code+":"+price.get(ii).price_vet+price.get(ii).address_vet);
			// System.out.println(getGridValue(ii));

			if ((int) price.get(ii).code == 41403) {
				System.out.println(price.get(ii).pois.size());
				System.out.println(price.get(ii).price_vet.size());
				for (int a = 0; a < price.get(ii).pois.size(); a++) {
					// System.out.println(price.get(ii).pois.elementAt(a));
					FileTool.Dump(price.get(ii).pois.elementAt(a), "D:/Test/41403-poi.txt", "utf-8");
					// System.out.println(price.get(ii).price_vet.elementAt(a));
				}
				for (int a = 0; a < price.get(ii).price_vet.size(); a++) {

					// FileTool.Dump(price.get(ii).price_vet,
					// "D:/Test/41403-price.txt", "utf-8");
					// System.out.println(price.get(ii).price_vet.elementAt(a));
				}

				FileTool.Dump((int) price.get(ii).code + ":" + price.get(ii).price_vet, "D:/Test/41403-price.txt",
						"utf-8");
			}

		}
		System.out.println("OK!");
	}

	/**
	 * 将网格可视化出来
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
			for (int xx = 0; xx < price.size(); xx++) {
				if (m == (int) price.get(xx).code) {
					label.setText(Double.toString(price.get(xx).price_vet.get(0))); // Double.toString(getGridValue(xx))

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
		System.out.println("总共有" + price.size() + "个格网填充了数据");
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
