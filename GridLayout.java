package com.svail.gridprocess;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import com.svail.util.FileTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GridLayoutLines extends JFrame
{
	public static String COORDINATE;
	public static Double LNG;
	public static Double LAT;
	public static Double PRICE;
	public static String ADDRESS;
	
	 public static void main(String[] args) throws IOException
	 {
		  int[] rocos=DataGrid();
		  int rows=rocos[0];
		  int cols=rocos[1];
		  GridLayoutLines frame = new GridLayoutLines(rows, cols,2,2,2,2,2,2);
		  frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
		  frame.pack();
		  frame.setLocationRelativeTo( null );
		  frame.setVisible(true);
	 }
	public static ArrayList<DataPoint> dataPoints=new ArrayList<DataPoint>();
	public static ArrayList<Code> codes=new ArrayList<Code>();
	public static ArrayList<Price> price=new ArrayList<Price>();
	public static ArrayList<Integer> tempcodes=new ArrayList();
    public static void addDataPoint(DataPoint p){
		dataPoints.add(p);
	}
    public static void addCode(Code c){
    	codes.add(c);
    }
    public static void addPrice(Price pr,double pricedata,String address,String poi){
    	int count=1;
    	if(price.size()>0){
    		for(int num=0;num<price.size();num++){
        		if(pr.code==price.get(num).code&&count==1){
        			price.get(num).setPrice(pricedata);
        			price.get(num).setAddress(address);
        			price.get(num).setPois(poi);
        			count++;
        		}	
        	}
    		if(count==1){
        		price.add(pr);
        		count++;
        	}
    	}else{
    		price.add(pr);
    		count++;
    	}
    	
    	
    }
	 public static double getGridValue(int i){
		
		 int size=price.get(i).price_vet.size();
	     double sum=0;
	     for(int k=0;k<size;k++){
	            sum+=price.get(i).price_vet.get(k);
	      }
	      return sum/size;
	    
		 
	    }
	 public static int[] DataGrid(){
		 dataPoints.clear();
		 codes.clear(); 
		 price.clear();
		 String poi="";
		  double[] test=new double[2];
		  Vector<String> fang = FileTool.Load("D:/Test/anjuke_rentout_all925.txt","UTF-8");
		  try{
			  for(int m=0;m<fang.size();m++)
			  {
				  poi=fang.elementAt(m);
				    
				    if(poi.indexOf("<coordinate>")!=-1)
					{
				    	 
					     COORDINATE=getStrByKey(poi,"<coordinate>","</coordinate>");
					     if (COORDINATE !=null)
							{
								String[] coordinate=COORDINATE.split(";");
								LNG=Double.parseDouble(coordinate[0]);
								LAT=Double.parseDouble(coordinate[1]);
								test=Coordinate_Transformation.BLToGauss(LNG,LAT);
								//System.out.println(LNG+";"+LAT+": X="+test[0]+"Y="+test[1]);
								
							}
					     if(poi.indexOf("<PRICE>")!=-1){
					    	 PRICE=Double.parseDouble(getStrByKey(poi,"<PRICE>","</PRICE>").replace("元/月", "").replace("[面议]", "").replace("[押一付三]", "")); 
					     }else{
					    	 PRICE=0.0;
					     }
					     if(poi.indexOf("<LOCATION>")!=-1){
					    	 ADDRESS=getStrByKey(poi,"<LOCATION>","</LOCATION>").replace("元/月", "").replace("[面议]", "").replace("[押一付三]", "");
					     }else{
					    	 ADDRESS="未知";
					     }
					   
					    DataPoint p=new DataPoint();
					     p.setX(test[0]);
						 p.setY(test[1]);
						 p.setData(PRICE);
						 p.setAddress(ADDRESS);
						 p.setPoi(poi);
						 addDataPoint(p);	 
						}
				}
		 }catch(NumberFormatException e){
			  System.out.println(poi);
			  System.out.println(e.getMessage());
	     }
		 
		  int n=dataPoints.size();
		//  System.out.print("dataPoints的大小是:"+n+"\r\n");
		 // for(int k=0;k<n;k++){
			 // System.out.print("dataPoints的第"+k+"个数是:"+dataPoints.get(k).y+","+dataPoints.get(k).x+"\r\n"); 
			  
		 // }
		  DataPoint p=new DataPoint();
		  p.X_MAX=getXMax();
		  p.X_MIN=getXMin();
		  p.Y_MAX=getYMax();
		  p.Y_MIN=getYMin();
		  
		 // System.out.print("X_MAX是:"+p.X_MAX+"\r\n");
		  //System.out.print("X_MIN是:"+p.X_MIN+"\r\n");
		 // System.out.print("Y_MAX是:"+p.Y_MAX+"\r\n");
		 // System.out.print("Y_MIN是:"+p.Y_MIN+"\r\n");
		  int[] roco=new int[2];
		  int rows=(int) Math.ceil((p.X_MAX-p.X_MIN)/500);
		  roco[0]=rows;
		  int cols=(int) Math.ceil((p.Y_MAX-p.Y_MIN)/500); 
		  roco[1]=cols;
		 // System.out.print("d_x:"+rows+"\r\n"+"d_y:"+cols+"\r\n");
		  
		  //创建栅格编码
		  int mm=1;
		  for(int rr=rows;rr>=1;rr--){
			  for(int cc=1;cc<=cols;cc++){
				  Code c=new Code();
				  c.setRow(rr);
				  c.setCol(cc);
				  c.setCode(mm);
				  mm++;
				  addCode(c); 
				  //System.out.println(c.getCode(rr, cc));
			  }
		  }
		//  for(int k=0;k<codes.size();k++){
			//  System.out.print("codes的第"+k+"个数是:"+codes.get(k).row+","+codes.get(k).col+","+codes.get(k).code+"\r\n"); 
			  
		//  }
		//计算每个点所在的行列号以及编码,存储于动态数组 price
		  try{
			  int row=0;
			  int col=0;
			  int tt=0;
			 
			  for(int k=0;k<dataPoints.size();k++){
				  row=(int) Math.ceil((p.X_MAX-dataPoints.get(k).x)/500);
				  col=(int) Math.ceil((dataPoints.get(k).y-p.Y_MIN)/500);
				  if(row==0)
					  row+=1;
				  if(col==0)
					  col+=1;
				 // System.out.print("dataPoints的第"+k+"个数所在的行列号是:"+row+"行"+col+"列"+"编码是"+codes.get((col+cols*(row-1)-1)).code+"价格是"+dataPoints.get(k).data+"\r\n");
				  Price pr=new Price();
				  pr.setCode(codes.get((col+cols*(row-1)-1)).code);
				  double price=dataPoints.get(k).data;
				  pr.setPrice(price);
				  String address=dataPoints.get(k).address;
				  pr.setAddress(address);
				  String poi1=dataPoints.get(k).poi;
				  pr.setPois(poi1);
				  addPrice(pr,dataPoints.get(k).data,dataPoints.get(k).address,dataPoints.get(k).poi); 
			  }
			 }catch(ArrayIndexOutOfBoundsException e){
				System.out.println();
			    System.out.println(e.getMessage()); //抛出异常的是位于行列线上的点
			 
		  }
		  for(int ii=0;ii<price.size();ii++){
			 // System.out.println((int)price.get(ii).code+":"+price.get(ii).price_vet+price.get(ii).address_vet);
			 // System.out.println(getGridValue(ii));
		  
			 if((int)price.get(ii).code==41403){//41403
				 System.out.println(price.get(ii).pois.size()); 
				 System.out.println(price.get(ii).price_vet.size()); 
				 for(int a=0;a<price.get(ii).pois.size();a++){
					// System.out.println(price.get(ii).pois.elementAt(a)); 
					 FileTool.Dump(price.get(ii).pois.elementAt(a), "D:/Test/41403-poi.txt", "utf-8");
					 //System.out.println(price.get(ii).price_vet.elementAt(a));
				 }
				 for(int a=0;a<price.get(ii).price_vet.size();a++){
					
					 //FileTool.Dump(price.get(ii).price_vet, "D:/Test/41403-price.txt", "utf-8");
					// System.out.println(price.get(ii).price_vet.elementAt(a));
				 }
				  
				  FileTool.Dump((int)price.get(ii).code+":"+price.get(ii).price_vet, "D:/Test/41403-price.txt", "utf-8");
			  }
			  
		  }
		  System.out.println("OK!");
		return roco;
	 }
	
	 public GridLayoutLines(int rows,int cols,int hgap,int vgap,int top,int bottom,int right,int left)
	 {
		
		  //构建网格框架
		 
		  JPanel grid = new JPanel( new GridLayout(rows,cols,hgap,vgap) );
		  grid.setBackground(Color.gray);//Color.gray
		  /*
		  JLabel l=new JLabel();
		  Icon icon=new ImageIcon("D:/ZX/01.jpg"); 
		  l.setIcon(icon);
		  l.setBounds(0, 0, 2000,1000);
		  grid.add(l,new Integer(Integer.MIN_VALUE));
		  */
		  
		  
		  for (int m = 1; m <=rows*cols; m++)
		  {
			  //grid.setBackground(Color.gray);//Color.gray
			  grid.setBorder( new MatteBorder(top,left,bottom,right, Color.BLACK) );
			  JLabel label = new JLabel();
			  //label.setBackground(Color.gray);//Color.gray
			  label.setBorder(BorderFactory.createLineBorder(Color.gray));
			  label.setOpaque( true );
			  //label.setText(Integer.toString(m));
			  for(int xx=0;xx<price.size();xx++){
				 if(m==(int) price.get(xx).code){
					 label.setText(Double.toString(price.get(xx).price_vet.get(0))); //Double.toString(getGridValue(xx))
					 
					 //*给网格赋背景颜色
					 if(getGridValue(xx)>30000){ 
						 label.setBackground(Color.red);//Color.gray
						 label.setOpaque( true );
						// System.out.println("红色的为:"+price.get(xx).address_vet.get(0));
					 }
					 if(getGridValue(xx)>20000&&getGridValue(xx)<=30000){ 
						 label.setBackground(Color.ORANGE);//Color.gray
						 label.setOpaque( true );
						 //System.out.println("橙色的为:"+price.get(xx).address_vet.get(0));
					 }
					 if(getGridValue(xx)>10000&&getGridValue(xx)<=20000){ 
						 label.setBackground(Color.pink);//Color.gray
						 label.setOpaque( true );
						// System.out.println("粉色的为:"+price.get(xx).address_vet.get(0));
					 }
					 if(getGridValue(xx)>5000&&getGridValue(xx)<=10000){ 
						 label.setBackground(Color.gray);//Color.gray
						 label.setOpaque( true );
						// System.out.println("灰色的为:"+price.get(xx).address_vet.get(0));
					 }
					 if(getGridValue(xx)>2500&&getGridValue(xx)<=5000){ 
						 label.setBackground(Color.green);//Color.gray
						 label.setOpaque( true );
						// System.out.println("绿色的为:"+price.get(xx).address_vet.get(0));
					 }
					 
					 continue;
				  }
			  }
			  //label.setOpaque( true );
			  grid.add(label); 
		  }
		  System.out.println("总共有"+price.size()+"个格网填充了数据");
		  add( grid );
		 
	}
	 public static String getStrByKey(String sContent, String sStart, String sEnd) {
			String sOut ="";
			int fromIndex = 0;
			int iBegin = 0;
			int iEnd = 0;
			int iStart=sContent.indexOf("</POI>");
			if (iStart < 0) {
			  return null;
			  }
			for (int i = 0; i < iStart; i++) {
			  // 找出某位置，并找出该位置后的最近的一个匹配
			  iBegin = sContent.indexOf(sStart, fromIndex);
			  if (iBegin >= 0) 
			  {
			    iEnd = sContent.indexOf(sEnd, iBegin + sStart.length());
			    if (iEnd <= iBegin)
			    {
			      return null;
			    }
			  }
			  else 
			  {
					return sOut;
			  }
	          if (iEnd > 0&&iEnd!=iBegin + sStart.length())
	          {
			   sOut += sContent.substring(iBegin + sStart.length(), iEnd);
			  }
	          else
	        	  return null;
			  if (iEnd > 0) 
			  {
			   fromIndex = iEnd + sEnd.length();
			  }
			}
			  return sOut;
		}
	  public static Double getXMax(){
		  double X_MAX=0;
		  for(int i=0;i<dataPoints.size();i++){
				if(dataPoints.get(i).x>X_MAX)
					X_MAX=dataPoints.get(i).x;
		  }
		  return X_MAX;	                                                           
	  }
     public static Double getXMin(){
		   double X_MIN=dataPoints.get(0).x;
		   for(int i=1;i<dataPoints.size();i++){
					   if(dataPoints.get(i).x<X_MIN)
						   X_MIN=dataPoints.get(i).x;
			}
		   return X_MIN;	 
	 }
	 public static Double getYMax(){
		  double Y_MAX=0;
		  for(int i=0;i<dataPoints.size();i++){
					   if(dataPoints.get(i).y>Y_MAX)
						   Y_MAX=dataPoints.get(i).y;
					   }
        return Y_MAX;
    }
    public static Double getYMin(){
		   	 double Y_MIN=dataPoints.get(0).y;
		   	 for(int i=1;i<dataPoints.size();i++){
					   if(dataPoints.get(i).y<Y_MIN)
						   Y_MIN=dataPoints.get(i).y;
			}
		 return Y_MIN;	 
    }
	

}
