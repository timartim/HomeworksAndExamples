
#include <cstdio>

#include <fstream>
#include <iostream>
#include <vector>
#include "phonebook.h"
#include "quicksort.h"
#include "return_codes.h"
using std::ifstream;
using std::ofstream;
int main(int argc, char **argv)
{
	if (argc != 3)
	{
		fprintf(stderr, "wrong number of arguments");
		return ERROR_INVALID_PARAMETER;
	}
	ifstream fin;
	fin.open(argv[1]);
	if (!fin)
	{
		fprintf(stderr, "Error while opening input file");
		fin.close();
		return ERROR_FILE_NOT_FOUND;
	}
	std::string data_type, sort_type;
	fin >> data_type >> sort_type;
	bool increasing_order;
	if (sort_type == "descending")
	{
		increasing_order = false;
	}
	else if (sort_type == "ascending")
	{
		increasing_order = true;
	}
	else
	{
		fprintf(stderr, "ERROR: wrong sort type");
		fin.close();
		return ERROR_INVALID_DATA;
	}
	int n;
	fin >> n;
	ofstream fout;
	if (data_type == "int")
	{
		int *arr = (int *)(malloc(n * sizeof(int)));
		if (arr == nullptr)
		{
			fprintf(stderr, "Error, couldnt create array");
			fout.close();
			fin.close();
			return ERROR_MEMORY;
		}
		for (int i = 0; i < n; i++)
		{
			fin >> arr[i];
		}
		if (increasing_order)
		{
			quicksort< int, true >(arr, n, n - 1, 0);
		}
		else
		{
			quicksort< int, false >(arr, n, n - 1, 0);
		}
		fout.open(argv[2]);
		if (!fout)
		{
			fprintf(stderr, "Error while opening input file");

			return ERROR_FILE_NOT_FOUND;
		}
		for (int i = 0; i < n; i++)
		{
			fout << arr[i] << "\n";
		}
		free(arr);
	}
	else if (data_type == "float")
	{
		float *arr = (float *)(malloc(n * sizeof(float)));
		if (arr == NULL)
		{
			fprintf(stderr, "Error, couldnt create array");
			fout.close();
			fin.close();
			return ERROR_MEMORY;
		}
		for (int i = 0; i < n; i++)
		{
			fin >> arr[i];
		}
		if (increasing_order)
		{
			quicksort< float, true >(arr, n, n - 1, 0);
		}
		else
		{
			quicksort< float, false >(arr, n, n - 1, 0);
		}
		fout.open(argv[2]);
		if (!fout)
		{
			fprintf(stderr, "Error while opening input file");
			fout.close();
			return ERROR_FILE_NOT_FOUND;
		}
		for (int i = 0; i < n; i++)
		{
			fout << arr[i] << "\n";
		}
		free(arr);
	}
	else if (data_type == "phonebook")
	{
		auto *arr = new(std::nothrow)phonebook[n]();
		if (arr == nullptr)
		{
			fprintf(stderr, "Error, couldnt create array");
			fout.close();
			fin.close();
			return ERROR_MEMORY;
		}
		fout.open(argv[2]);
		if (!fout)
		{
			fprintf(stderr, "Error while opening input file");
			fout.close();
			return ERROR_FILE_NOT_FOUND;
		}
		for (int i = 0; i < n; i++)
		{
			std::string name, surname, lastname;
			long long number;
			fin >> name >> surname >> lastname >> number;
			arr[i].names[0] = name;
			arr[i].names[1] = surname;
			arr[i].names[2] = lastname;
			arr[i].number = number;
		}
		if (increasing_order)
		{
			quicksort< phonebook, true >(arr, n, n - 1, 0);
		}
		else
		{
			quicksort< phonebook, false >(arr, n, n - 1, 0);
		}

		for (int i = 0; i < n; i++)
		{
			fout << arr[i].names[0] << " " << arr[i].names[1] << " " << arr[i].names[2] << " " << arr[i].number << "\n";
		}
		free(arr);
	}
	else
	{
		fprintf(stderr, "ERROR: invalid data type.");
		fin.close();
		fout.close();
		return ERROR_INVALID_DATA;
	}
	fin.close();
	fout.close();
	return 0;
}