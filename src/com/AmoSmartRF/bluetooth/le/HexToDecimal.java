package com.AmoSmartRF.bluetooth.le;

import org.apache.http.HttpException;

public class HexToDecimal {

	public HexToDecimal(String str) throws Exception {
		try {
			for (int i = 0; i < str.length(); i++) {// 判断是否满足16进制数
				if (!((str.charAt(i) <= '9' && str.charAt(i) >= '0')
						|| (str.charAt(i) <= 'F' && str.charAt(i) >= 'A') || (str
						.charAt(i) <= 'f' && str.charAt(i) >= 'a'))) {
					throw new Exception();// 若不满足，抛出异常
				}
			}
			System.out.println("Hex to Decimal is : " + test16To10(str));//显示结果

		} catch (HttpException e) {
			// e.printStackTrace();
			// System.out.println("Wrong number!");

		}

	}

	// 若满足16进制数，则进行16——>10转换
	public long test16To10(String str) {

		char str1[] = new char[100];// 用于存储16进制数

		long number = 0;
		long result = 0;
		for (int i = 0; i < str.length(); i++) {
			str1[i] = str.charAt(i);// 把string类型转换成char型

		}
		// 进行计算
		int k = 0;
		for (int j = str.length() - 1; j >= 0; j--) {
			if (str1[j] <= '9')
				number = (long) ((Integer.parseInt(String.valueOf(str1[j]))) * Math// 小于字符'10'的进行转化成整型，用于计算
						.pow(16, k));
			if (str1[j] == 'A' || str1[j] == 'a')
				number = (long) (10 * Math.pow(16, k));
			if (str1[j] == 'B' || str1[j] == 'b')
				number = (long) (11 * Math.pow(16, k));
			if (str1[j] == 'C' || str1[j] == 'c')
				number = (long) (12 * Math.pow(16, k));
			if (str1[j] == 'D' || str1[j] == 'd')
				number = (long) (13 * Math.pow(16, k));
			if (str1[j] == 'E' || str1[j] == 'e')
				number = (long) (14 * Math.pow(16, k));
			if (str1[j] == 'F' || str1[j] == 'f')
				number = (long) (15 * Math.pow(16, k));
			k++;

			result = result + number;
		}

		return result;
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
