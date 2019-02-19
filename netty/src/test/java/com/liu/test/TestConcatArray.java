/** 
 * Project Name:netty 
 * File Name:TestConcatArray.java 
 * Package Name:com.liu.test 
 * Date:2019年2月19日上午11:28:21 
 * Copyright (c) 2019, chenzhou1025@126.com All Rights Reserved. 
 * 
*/

package com.liu.test;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

/**
 * ClassName:TestConcatArray <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2019年2月19日 上午11:28:21 <br/>
 * 
 * @author liu-guofei
 * @version
 * @since JDK 1.8
 * @see
 */
public class TestConcatArray<T> {
	public static void main(String[] args) {
		/**
		 * 在apache-commons中，有一个ArrayUtils.addAll(Object[], Object[])方法 合并数组
		 */
		int a[] = new int[] { 1, 2, 3, 4, 5, 6 };

		int b[] = new int[] { 33, 44, 55, 66, 77, 88 };

		int[] c = ArrayUtils.addAll(a, b);

		String string = Arrays.toString(c);
		System.out.println(string);

	}

	static String[] concat(String[] a, String[] b) {
		String[] c = new String[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	static <T> T[] concat2(T[] array1, T[] array2) {
		final Class<?> type1 = array1.getClass().getComponentType();

		@SuppressWarnings("unchecked")
		final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	@SuppressWarnings("unused")
	private static <T> T[] concat1(T[] a, T[] b) {
		final int alen = a.length;
		final int blen = b.length;
		if (alen == 0) {
			return b;
		}
		if (blen == 0) {
			return a;
		}
		@SuppressWarnings("unchecked")
		final T[] result = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), alen + blen);
		System.arraycopy(a, 0, result, 0, alen);
		System.arraycopy(b, 0, result, alen, blen);
		return result;
	}
}
