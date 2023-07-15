#include "return_codes.h"

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//функция считающая определитель
float get_matrix_determinant(int N, float **mat, int line, int column)
{
	if (N < 2)	  //невозможный случай если происходит то что то явно пошло не так.
	{
		printf("ERROR: unexpected case, minor size < 2");
		return ERROR_UNKNOWN;
	}
	if (N == 2)
	{
		//считаем минор если его размер 2 на 2
		return (mat[0][0] * mat[1][1]) - (mat[0][1] * mat[1][0]);
	}
	float sum = 0;
	for (int i = 0; i < N; i++)
	{
		float **ans = (float **)malloc((N - 1) * (N - 1) * sizeof(float *));
		if (ans == NULL)
		{
			printf("ERROR: Coudnt create array, memory error");
			return ERROR_OUTOFMEMORY;
		}
		for (int j = 0; j < N; j++)
		{
			ans[j] = (float *)malloc((N - 1) * sizeof(float));
			if (ans[j] == NULL)
			{
				printf("ERROR: Coudnt create array, memory error");
				return ERROR_OUTOFMEMORY;
			}
		}
		int idx = 0, jdx = 0;
		//находим текухий минор
		for (int j = 1; j < N; j++)
		{
			jdx = 0;
			for (int k = 0; k < N; k++)
			{
				if (k != i)
				{
					ans[idx][jdx] = mat[j][k];
					jdx++;
				}
			}
			idx++;
		}

		float k = 1;
		if (line + column % 2 != 0)
		{
			k = -1;
		}
		//прибавляем к текущему определителю определитель минора
		float x = get_matrix_determinant(N - 1, ans, line + 1, column + 1) * k * mat[line][column];
		sum += x;
		free(ans);
		// printf("MINOR: %f %f %i %i\n", x, k, line, column);
		//		for (int j = 0; j < N - 1; j++)
		//		{
		//			for (int k = 0; k < N - 1; k++)
		//			{
		//				printf("%f ", ans[j][k]);
		//			}
		//			printf("\n");
		//		}
		column++;
	}
	return sum;
}

int main(int argc, char **args)
{
	if (argc != 3)
	{
		//неприавльное число параметров
		printf("Invalid number of parametrs");
		return ERROR_INVALID_PARAMETER;
	}
	int n;
	FILE *fin;
	FILE *fout;
	fin = fopen(args[1], "r");
	fout = fopen(args[2], "w");
	if (fin == NULL)
	{
		//не нашли файл входа
		printf("Input file not found");
		return ERROR_FILE_NOT_FOUND;
	}
	if (fout == NULL)
	{
		//не смогли создать файл выхода
		printf("No memory for file");
		return ERROR_NOT_ENOUGH_MEMORY;
	}
	int N = 0;
	fscanf(fin, "%i", &N);
	//создаем матрицу коеффицентов
	float **mat = (float **)malloc(N * sizeof(float *));
	if (mat == NULL)
	{
		printf("ERROR: Coudnt create array, memory error");
		fclose(fin);
		fclose(fout);
		return ERROR_OUTOFMEMORY;
	}
	//создаем матрицу свободных членов
	float **ans = (float **)malloc(1 * N * sizeof(float *));
	if (ans == NULL)
	{
		printf("ERROR: Coudnt create array, memory error");
		fclose(fin);
		fclose(fout);
		free(mat);
		return ERROR_OUTOFMEMORY;
	}
	for (int i = 0; i < N; i++)
	{
		mat[i] = (float *)malloc(N * sizeof(float));
		if (mat[i] == NULL)
		{
			fclose(fin);
			fclose(fout);
			printf("ERROR: Coudnt create array, memory error");
			return ERROR_OUTOFMEMORY;
		}
		ans[i] = (float *)malloc(1 * sizeof(float));
		if (ans[i] == NULL)
		{
			fclose(fin);
			fclose(fout);
			printf("ERROR: Coudnt create array, memory error");
			return ERROR_OUTOFMEMORY;
		}
	}

	//читаем переменные
	for (int i = 0; i < N; i++)
	{
		for (int j = 0; j < N; j++)
		{
			int count = fscanf(fin, "%f", &mat[i][j]);
			if (count == 0)
			{
				printf("ERROR: expected float got string or char.");
				free(mat);
				free(ans);
				fclose(fin);
				fclose(fout);
				return ERROR_INVALID_DATA;
			}
			if (count == -1)
			{
				printf("ERROR: invalid matrix size");
				free(mat);
				fclose(fin);
				fclose(fout);
				return ERROR_INVALID_PARAMETER;
			}
			// printf("%i \n", count);
		}
		fscanf(fin, "%f", &ans[i][0]);
	}
	fclose(fin);
	//	    for (int i = 0; i < N; i++) {
	//	        for (int j = 0; j < N; j++) {
	//	            printf("%f ", mat[i][j]);
	//	        }
	//	        printf("\n");
	//	    }
	float delta = get_matrix_determinant(N, mat, 0, 0);
	// printf("%f ", delta);
	if (delta == 0)
	{	 //    если определитель равен нулю либо нет решений либо их
		for (int i = 0; i < N; i++)
		{
			float **arr = (float **)malloc(N * N * sizeof(float *));
			if (arr == NULL)
			{
				printf("ERROR: Coudnt create array, memory error");
				free(mat);
				free(ans);
				fclose(fout);
				return ERROR_OUTOFMEMORY;
			}
			for (int j = 0; j < N; j++)
			{
				arr[j] = (float *)malloc(N * sizeof(float));
				if (arr[j] == NULL)
				{
					printf("ERROR: Coudnt create array, memory error");
					free(mat);
					free(ans);
					free(arr);
					fclose(fout);
					return ERROR_OUTOFMEMORY;
				}
				for (int k = 0; k < N; k++)
				{
					arr[j][k] = mat[j][k];
				}
			}
			for (int j = 0; j < N; j++)
			{
				arr[j][i] = ans[j][0];
			}
			float deltaXk = get_matrix_determinant(N, arr, 0, 0);
			if (deltaXk != 0)
			{
				fprintf(fout, "no solution");
				free(mat);
				free(ans);
				fclose(fout);
				return 0;
			}
		}
		//    бесконечное множество
		fprintf(fout, "many solutions");
		free(mat);
		free(ans);
		fclose(fout);
		return 0;
	}

	for (int i = 0; i < N; i++)
	{
		float **arr = (float **)malloc(N * N * sizeof(float *));
		if (arr == NULL)
		{
			printf("ERROR: Coudnt create array, memory error");
			free(mat);
			free(ans);
			fclose(fout);
			return ERROR_OUTOFMEMORY;
		}
		for (int j = 0; j < N; j++)
		{
			arr[j] = (float *)malloc(N * sizeof(float));
			if (arr[j] == NULL)
			{
				printf("ERROR: Coudnt create array, memory error");
				free(mat);
				free(ans);
				fclose(fout);
				return ERROR_OUTOFMEMORY;
			}
			for (int k = 0; k < N; k++)
			{
				arr[j][k] = mat[j][k];
			}
		}
		for (int j = 0; j < N; j++)
		{
			arr[j][i] = ans[j][0];
		}
		float deltaXk = get_matrix_determinant(N, arr, 0, 0);
		// printf("%f %f", deltaXk, delta);
		//выводим ответы в файл
		int count = fprintf(fout, "%g\n", deltaXk / delta);
		if (count == 0)
		{
			printf("Error unexpected, didnt write anything in file, check input file or mem");
			free(mat);
			free(ans);
			fclose(fout);
			return ERROR_UNKNOWN;
		}
		free(arr);
	}
	free(mat);
	free(ans);
	fclose(fout);
	return 0;
}