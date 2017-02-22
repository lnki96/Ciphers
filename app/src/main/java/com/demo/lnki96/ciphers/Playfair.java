package com.demo.lnki96.ciphers;
import java.util.Scanner;


public class Playfair {
	public static void main(String args[]) throws Exception {
		System.out.println("［A 加密］［J 解密］,Please Choose One");
		Scanner c = new Scanner(System.in);// 创建Scanner对象
		String s1 = c.nextLine();// 获取本行的字符串
		if (s1.equalsIgnoreCase("A")) {// 判断变量s1与A是否相等，忽略大小
			System.out.println("请输入明文：");
			Scanner sc = new Scanner(System.in);
			String s = sc.nextLine();
			System.out.println("请输入密钥：");
			Scanner sc1 = new Scanner(System.in);
			String key = sc1.nextLine();
			Encrypt(s, key);// 调用Encryption方法
		} else if (s1.equalsIgnoreCase("J")) {
			System.out.println("请输入密文：");
			Scanner sc = new Scanner(System.in);
			String s = sc.nextLine();
			System.out.println("请输入密钥：");
			Scanner sc1 = new Scanner(System.in);
			String key = sc1.nextLine();
			Decrypt(s, key);// 调用Encryption方法
		}
	}
	private static int length(Scanner sc1) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 生成Playfair密文  <br>
	 *
	 * @param str
	 *            明文

	 * @param2 key
	 *            密钥key
	 *
	 * @return 返回密文
	 */
	public static String Encrypt(String str, String key) {// 加密
		String ee=str;
		String string = "";
		String a ="abcdefghiklmnopqrstuvwxyz";
		String[] aa = a.split("");
		String[] bb = key.split("");

		int n = key.length();
		//System.out.println(n);

		int flag = 0;
		for (int i = 1;i < 26; i++){       //构造矩阵
			flag = 0;
			for (int l = 1, j = 0;l<n ;l++)
			{

				if (bb[l].equals(aa[i]))
				{
					flag = 1;

				}
			}
			if( flag != 1 ){
				key += aa[i];
			}

		}

		//System.out.println(key);
		bb = key.split("");
		String[][] e = {{"", "", "", "", ""},{ "", "", "", "", ""},{ "", "", "", "", ""},{ "", "", "", "", "", ""},{ "", "", "", "", ""}};
		int hang;
		flag =0;
		for(hang=0;hang<25;hang++,flag++)
		{

			e[hang/5][flag]=bb[hang+1];

			//System.out.println(e[hang/5][flag]);
			if(flag==4)
			{
				flag=-1;
			}
		}

		String[] cc = str.split("");

		int m = str.length();

		str="";            //将明文中两个相邻相同字母中间加q
		String s = "q";
		for(int i=1;i<m;i = i + 2){
			if(cc[i].equals(cc[i+1]))
			{
				str += cc[i];
				str += s;
				str += cc[i+1];
			}
			else
			{
				str += cc[i];
				str += cc[i+1];
			}
		}





		m = str.length();                 //判断明文长度是否为偶数
		if(m%2!=0)
			str += s;


		m = str.length();  //将明文中j替换为i
		cc = str.split("");
		s = "j";
		for(int i=1;i<=m;i++)
			if(cc[i]==s)
				cc[i]="i";

		//开始加密
		int p = 0, q = 0, x = 0, y = 0;
		for(int k = 1; k <=m - 1; k = k + 2){

			for(int i = 0; i < 5; i++)
				for(int j = 0; j < 5; j++)
					if(cc[k].equals(e[i][j])){
						p = i;
						q = j;
						//System.out.println(e[i][j]);
					}
			for(int i=0;i<5;i++)
				for(int j=0;j<5;j++)
					if(cc[k+1].equals(e[i][j]))
					{
						x=i;
						y=j;
						//System.out.println(e[i][j]);
					}

			if(p==x)
			{

				string += e[p][((q+1)==5)?0:(q+1)];

				string += e[x][((y+1)==5)?0:(y+1)];

			}
			else if(q==y)
			{
				string += e[((p+1)==5)?0:(p+1)][q];

				string += e[((x+1)==5)?0:(x+1)][y];

			}
			else
			{
				string += e[p][y];

				string += e[x][q];

			}
		}

		System.out.println(ee + " 加密后为：" + string);
		return string;
	}


	/**
	 * 解密Playfair密文  <br>
	 *
	 * @param str
	 *            密文

	 * @param2 key
	 *            密钥key
	 *
	 * @return 返回明文
	 */
	public static String Decrypt(String str, String key) {// 解密

		String ee = str;
		String string = "";
		String a ="abcdefghiklmnopqrstuvwxyz";
		String[] aa = a.split("");
		String[] bb = key.split("");

		int n = key.length();
		//System.out.println(n);
		int flag = 0;
		for (int i = 1;i <= 25; i++){       //构造矩阵
			flag = 0;
			for (int l = 1, j = 0;l<=n ;l++)
			{

				if (bb[l].equals(aa[i]))
				{
					flag = 1;

				}
			}
			if( flag != 1 ){
				key += aa[i];
			}

		}
		bb = key.split("");
		String[][] e = {{"", "", "", "", ""},{ "", "", "", "", ""},{ "", "", "", "", ""},{ "", "", "", "", "", ""},{ "", "", "", "", ""}};
		int hang;
		flag = 0;
		for(hang=0;hang<25;hang++,flag++)
		{

			e[hang/5][flag]=bb[hang+1];

			//System.out.println(e[hang/5][flag]);
			if(flag==4)
			{
				flag=-1;
			}
		}
		int m = str.length();
		String[] cc = str.split("");

		int p = 0, q = 0, x = 0, y = 0;//  最后一步到倒数第二步    密文到加工完成的明文
		for(int k = 1; k <= m - 1; k = k + 2){

			for(int i = 0; i < 5; i++)
				for(int j = 0; j < 5; j++)
					if(cc[k].equals(e[i][j])){
						p = i;
						q = j;
						//System.out.println(e[i][j]);
					}
			for(int i=0;i<5;i++)
				for(int j=0;j<5;j++)
					if(cc[k+1].equals(e[i][j]))
					{
						x=i;
						y=j;
						//System.out.println(e[i][j]);
					}

			if(p==x)
			{

				string += e[p][((q-1)== -1)?4:(q-1)];

				string += e[x][((y-1)== -1)?4:(y-1)];

			}
			else if(q==y)
			{
				string += e[((p-1)== -1)?4:(p-1)][q];

				string += e[((x-1)== -1)?4:(x-1)][y];

			}
			else
			{
				string += e[p][y];

				string += e[x][q];

			}
		}

		str = string;
		m = str.length();  //将明文中i替换为j
		cc = str.split("");
		String s = "i";

		for(int i=1;i<=m;i++)
			if(cc[i].equals(s))
				cc[i]="j";

		string="";            //将明文中两个相邻相同字母中间的q去掉
		s = "q";
		for(int i=1;i<=m-2;i++)
			if(cc[i+1].equals(s) && cc[i].equals(cc[i+2]))
			{
				cc[i+1]="";
			}
		for(int i = 1; i <= m; i++)
		{
			if(cc[i].equals(""))
			{
				string += cc[i+1];
				i = i + 1;
			}
			else
				string += cc[i];
		}
		System.out.println(ee + " 加密后为：" + string);
		return string;
	}


}//最后一个分号
    		
    	     
    
    	
    		
    	
    
    
    
    	
    
    
    
   