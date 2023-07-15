////
//// Created by Артемий on 28.04.2022.
////
//#include <iostream>
//#include <vector>
//#include <string>
//#include "phonebook.h"
// char toLowerCase(char&ch){
//	if((ch >= 'A' )&&(ch <= 'Z')){
//		return (char)((int)ch-'A' + 'a');
//	}
//	return ch;
//}
// int compare_string(string s1, string s2){
//	for(int i = 0; i < min(s1.length(), s2.length()); i++){
//		if(s1[i] < s2[i]){
//			return 1;
//		}else if(s1[i] > s2[i]){
//			return -1;
//		}else{
//			if(s1[i] == s2[i]){
//				continue ;
//			}
//		}
//	}
//	if(s1.length() > s2.length()){
//		return 1;
//	}else if(s2.length() > s1.length()){
//		return -1;
//	}
//	return 0;
//}
// int compare_phonebook(string*arr1, string*arr2, int idx){
//	if(idx >=3){
//		return 0;
//	}
//	int res = compare_string(arr1[idx], arr2[idx]);
//	if((res == 1)||(res == -1)){
//		return res;
//	}
//	return compare_phonebook(arr1, arr2, idx+1);
//}
// int sortPhonebook(phonebook*vc,int n, bool acsending){
//	if((n == 0) || (n == 1)){
//		return 0;
//	}
//	int num_of_left = 0;
//	int num_of_right = 0;
//	phonebook el = vc[n/2];
//	int numOfel = 0;
//	for(int i = 0; i < n; i++){
//		if(acsending){
//			int res = compare_phonebook(el.names, vc[i].names, 0);
//			if(res == 1){
//				num_of_left++;
//			}
//			if(res == -1){
//				num_of_right++;
//			}
//			if(res == 0){
//				if(numOfel > 0){
//					num_of_right++;
//				}
//				numOfel++;
//			}
//		}else{
//			int res = compare_phonebook(el.names, vc[i].names, 0);
//			if(res == -1){
//				num_of_left++;
//			}
//			if(res == 1){
//				num_of_right++;
//			}
//			if(res == 0){
//				if(numOfel > 0){
//					num_of_right++;
//				}
//				numOfel++;
//			}
//		}
//	}
//	numOfel = 0;
//	auto*left = (phonebook*)malloc(num_of_left*sizeof (phonebook));
//	if(left == nullptr){
//		return -1;
//	}
//	auto*right = (phonebook*)malloc(num_of_right*sizeof (phonebook));
//	if(right == nullptr){
//		return -1;
//	}
//	int leftitr = 0, rightitr = 0;
//	for(int i = 0; i < n; i++){
//		if(acsending){
//			int res = compare_phonebook(el.names, vc[i].names, 0);
//			if(res == 1){
//				left[leftitr] = vc[i];
//				leftitr++;
//			}
//			if(res == -1){
//				right[rightitr] = vc[i];
//				rightitr++;
//			}
//			if(res == 0){
//				if(numOfel > 0){
//					right[rightitr] = vc[i];
//					rightitr++;
//				}
//				numOfel++;
//			}
//		}else{
//			int res = compare_phonebook(el.names, vc[i].names, 0);
//			if(res == -1){
//				left[leftitr] = vc[i];
//				leftitr++;
//			}
//			if(res == 1){
//				right[rightitr] = vc[i];
//				rightitr++;
//			}
//			if(res == 0){
//				if(numOfel > 0){
//					right[rightitr] = vc[i];
//					rightitr++;
//				}
//				numOfel++;
//			}
//		}
//	}
//	sortPhonebook(left, num_of_left, acsending);
//	sortPhonebook(right, num_of_right, acsending);
//	auto*ans = (phonebook*)malloc(n * sizeof(phonebook));
//	int ansitr = 0;
//	for(int i = 0; i < num_of_left; i++){
//		ans[ansitr] = left[i];
//		ansitr++;
//	}
//	ans[ansitr] = el;
//	ansitr++;
//	for(int i = 0; i < num_of_right; i++){
//		right[ansitr] = right[i];
//		ansitr++;
//	}
//	for(int i = 0; i < n; i++){
//		vc[i] = ans[i];
//	}
//	return 0;
//}
