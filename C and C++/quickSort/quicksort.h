#include <ctime>
#include <iostream>

#include <ctime>
#include <stdlib.h>
#ifndef LAB3_QUICKSORT_H
	#define LAB3_QUICKSORT_H

template< typename T, bool descending >
void quicksort(T *arr, int n, int r, int l)
{
	if (l >= r)
	{
		return;
	}
	int i = l;
	int j = r;
	int val = (l + r) / 2 + 1;
	T el = arr[val];
	if (descending)
	{
		while (i <= j)
		{
			while (arr[i] < el)
			{	 //если меньше, то элементы подходят
				i++;
			}
			while (arr[j] > el)
			{	 //	если больше, то элементы подходят
				j--;
			}
			if (i <= j)
			{	 //не подходят, меняем местами
				T acc = arr[i];
				arr[i] = arr[j];
				arr[j] = acc;
				i++;
				j--;
			}
		}
	}
	else
	{
		while (i <= j)
		{
			while (arr[i] > el)
			{
				i++;
			}
			while (arr[j] < el)
			{
				j--;
			}
			if (i <= j)
			{
				T acc = arr[i];
				arr[i] = arr[j];
				arr[j] = acc;
				i++;
				j--;
			}
		}
	}
	if (l < j)
	{
		quicksort< T, descending >(arr, n, j, l);
	}
	if (i < r)
	{
		quicksort< T, descending >(arr, n, r, i);
	}
}
#endif	  // LAB3_QUICKSORT_H
		  //	if(l < r){
//		//srand(time(NULL));
//		T el = arr[r];
//		int idx = l - 1;
//		if(descending){
//			for(int i = l; i < r; i++){
//				if(arr[i] < el){
//					idx++;
//					T acc = arr[idx];
//					arr[idx] = arr[i];
//					arr[i] = acc;
//				}
//			}
//			T acc = arr[idx + 1];
//			arr[idx + 1] = arr[r];
//			arr[r] = acc;
//		}else{
//			for(int i = l; i < r; i++){
//				if(arr[i] >= el){
//					idx++;
//					T acc = arr[idx];
//					arr[idx] = arr[i];
//					arr[i] = acc;
//				}
//			}
//			T acc = arr[idx + 1];
//			arr[idx + 1] = arr[r];
//			arr[r] = acc;
//		}
//		quicksort<T, descending>(arr, n, idx, l);
//		quicksort<T, descending>(arr, n, r, idx + 2);
//	}