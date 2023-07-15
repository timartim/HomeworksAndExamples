#include "return_codes.h"
#include "zlib.h"
#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#if defined ISAL
	#error "lib not supported"
#elif defined LIBDEFLATE
	#error "lib not supported"
#elif defined ZLIB
	#include "zlib.h"
#endif
bool checkEndOfPNG(unsigned char *buf, unsigned char *endOfPng, int size)
{
	for (int i = 0; i < size; i++)
	{
		if (buf[i] !=			return false;
			endOfPng[i])
		{
		}
	}
	return true;
}
unsigned char cyclicshiftLeft(unsigned char *buf, int size)
{
	int ans = buf[0];
	for (int i = 1; i < size; i++)
	{
		buf[i - 1] = buf[i];
	}
	return ans;
}
unsigned char pathPredictor(unsigned char ab, unsigned char bb, unsigned char cb)
{
	int a = ab;
	int b = bb;
	int c = cb;
	int p = ((a + b) - c);
	int pa = abs(p - a);
	int pb = abs(p - b);
	int pc = abs(p - c);
	int pr = 0;
	if ((pa <= pb) && (pa <= pc))
	{
		pr = a;
	}
	else if (pb <= pc)
	{
		pr = b;
	}
	else
	{
		pr = c;
	}
	return pr % 256;
}
unsigned char getPrevLeft(unsigned char *dat, int idx, int start)
{
	if (idx - 3 >= start)
	{
		return dat[idx - 3];
	}
	else
	{
		return 0;
	}
}
unsigned char getPrevUp(unsigned char *dat, int idx, int width)
{
	if (idx - width >= 0)
	{
		return dat[idx - width];
	}
	else
	{
		return 0;
	}
}
unsigned char getPrevLU(unsigned char *dat, int idx, int width, int prevStart)
{
	int up = idx - width;
	int left = up - 3;
	if (left >= prevStart)
	{
		return dat[left];
	}
	else
	{
		return 0;
	}
	return 0;
}
int executeColorFilt(unsigned char *dat, int start, int end, int filtr, int width)
{
	if (filtr == 0)
	{
		return 0;
	}
	else if (filtr == 1)
	{
		for (int i = start; i < end; i++)
		{
			unsigned char prevr = getPrevLeft(dat, i, start);
			int a = prevr;
			int b = dat[i];
			int res = a + b;
			dat[i] = res % 256;
		}
		return 0;
	}
	else if (filtr == 2)
	{
		for (int i = start; i < end; i++)
		{
			unsigned char prevr = getPrevUp(dat, i, width);
			int a = prevr;
			int b = dat[i];
			int res = a + b;
			dat[i] = res % 256;
		}
		return 0;
	}
	else if (filtr == 3)
	{
		for (int i = start; i < end; i++)
		{
			unsigned char upr = getPrevUp(dat, i, width);
			unsigned char prevr = getPrevLeft(dat, i, start);
			double a = upr;
			double b = prevr;
			int res = floor((a + b) / 2);
			dat[i] = (dat[i] + res) % 256;
		}
		return 0;
	}
	else if (filtr == 4)
	{
		for (int i = start; i < end; i++)
		{
			unsigned char upr = getPrevUp(dat, i, width);
			unsigned char prevr = getPrevLeft(dat, i, start);
			unsigned char LUr = getPrevLU(dat, i, width, start - width);
			int a = dat[i];
			int b = pathPredictor(prevr, upr, LUr);
			int res = (a + b) % 256;
			dat[i] = res;
		}
		return 0;
	}
	else
	{
		return 1;
	}
}
int main(int num, char **argc)
{
	if (num != 3)
	{
		fprintf(stderr, "ERROR: invalid number of parametr");
		return ERROR_INVALID_PARAMETER;
	}
	FILE *fin = fopen(argc[1], "rb");
	if (fin == NULL)
	{
		fprintf(stderr, "ERROR: couldn't open input file");
		return ERROR_FILE_NOT_FOUND;
	}

	unsigned char head[8];
	unsigned char checkPNG[] = { 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
	unsigned char endOfPng[] = { 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4e, 0x44, 0xae, 0x42, 0x60, 0x82 };
	unsigned char IHDR[25];
	//елементы отвечающие за png
	int res = fread(head, 1, 8, fin);
	if (res < 8)
	{
		fprintf(stderr, "ERRPOR, COULD NOT READ FROM FILE");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	for (int i = 0; i < 8; i++)
	{
		if (head[i] != checkPNG[i])
		{
			printf("ERROR: input is not png");
			fclose(fin);
			free(head);

			return ERROR_INVALID_DATA;
		}
	}
	res = fread(IHDR, 1, 25, fin);
	if (res < 25)
	{
		fprintf(stderr, "ERRPOR, COULD NOT READ FROM FILE");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	long long width, height;
	if ((IHDR[4] != 'I') && (IHDR[5] != 'H') && (IHDR[6] != 'D') && (IHDR[7] != 'R'))
	{
		fprintf(stderr, "ERROR: invalid ihdr chunk");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	width = IHDR[8] * 16 * 16 * 16 * 16 * 16 * 16 * 16 * 16 + IHDR[9] * 16 * 16 * 16 * 16 + IHDR[10] * 16 * 16 + IHDR[11];
	if (width == 0)
	{
		fprintf(stderr, "ERROR: invalid WIDTH of PNG");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	height = IHDR[12] * 16 * 16 * 16 * 16 * 16 * 16 * 16 * 16 + IHDR[13] * 16 * 16 * 16 * 16 + IHDR[14] * 16 * 16 + IHDR[15];
	if (height == 0)
	{
		fprintf(stderr, "ERROR: invalid height of png");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	unsigned char bitDeph, colorType;
	bitDeph = IHDR[16];
	if ((bitDeph != 1) && (bitDeph != 2) && (bitDeph != 4) && (bitDeph != 8) && (bitDeph != 16))
	{
		fprintf(stderr, "ERROR: invalid bitDeph");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	colorType = IHDR[17];
	if ((colorType != 0) && (colorType != 2) && (colorType != 3) && (colorType != 4) && (colorType != 6))
	{
		fprintf(stderr, "ERROR: invalid color type;");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}

	if ((colorType == 2) || (colorType == 4) || (colorType == 6))
	{
		if ((bitDeph != 8) && (bitDeph != 16))
		{
			fprintf(stderr, "ERROR: invalid bitdeph for colortype");
			fclose(fin);
			return ERROR_INVALID_DATA;
		}
	}
	if (colorType == 3)
	{
		if ((bitDeph != 1) && (bitDeph != 2) && (bitDeph != 4) && (bitDeph != 8))
		{
			fprintf(stderr, "ERROR: invalid bitdeph for colortype");
			fclose(fin);
			return ERROR_INVALID_DATA;
		}
	}
	unsigned char compress_meth = IHDR[18];
	if (compress_meth != 0)
	{
		fprintf(stderr, "ERROR: invalid compress method");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	unsigned char filt_meth = IHDR[19];
	if (filt_meth != 0)
	{
		fprintf(stderr, "ERROR: invalid filter method");
		fclose(fin);
		return ERROR_INVALID_DATA;
	}
	unsigned char int_math = IHDR[20];
	unsigned char red = IHDR[11], green = IHDR[12], blue = IHDR[13];
	int size = 1, idx = 0, bufSize = 12, currSize = 0;
	unsigned char *IDATA = (unsigned char *)malloc(size * sizeof(unsigned char *));
	if (IDATA == NULL)
	{
		fprintf(stderr, "ERROR: could not create array");
		fclose(fin);
		return ERROR_MEMORY;
	}
	const int realloc_size[] = { 2,	   4,	 8,		16,	   32,	  64,	  128,	  256,	  512,	   1024,
								 2048, 4096, 16192, 32384, 64768, 129536, 259072, 518144, 1036288, 2072576 };
	unsigned char buffer[12];
	unsigned char checkIDAT[4] = { 0x49, 0x44, 0x41, 0x54 };
	unsigned char bufIDAT[4];
	fread(bufIDAT, 1, 4, fin);
	bufIDAT[0] = buffer[0];
	bufIDAT[1] = buffer[1];
	bufIDAT[2] = buffer[2];
	bufIDAT[3] = buffer[3];
	int j = 0;
	bool startedIdat = false;
	while (true)
	{
		if (checkEndOfPNG(bufIDAT, checkIDAT, 4))
		{
			break;
		}
		if (checkEndOfPNG(buffer, endOfPng, bufSize))
		{
			break;
		}
		unsigned char dat = cyclicshiftLeft(buffer, bufSize);
		unsigned char next[1];
		res = fread(next, 1, 1, fin);
		if (res < 1)
		{
			fprintf(stderr, "ERRPOR, COULD NOT READ FROM FILE");
			fclose(fin);
			free(IDATA);
			return ERROR_INVALID_DATA;
		}
		unsigned char var = cyclicshiftLeft(bufIDAT, 4);
		bufIDAT[3] = next[0];
		j++;
	}
	res = fread(buffer, 1, bufSize, fin);
	if (res < bufSize)
	{
		fprintf(stderr, "ERRPOR, COULD NOT READ FROM FILE");
		fclose(fin);
		free(IDATA);
		return ERROR_INVALID_DATA;
	}
	while (true)
	{
		if (checkEndOfPNG(buffer, endOfPng, bufSize))
		{
			break;
		}
		unsigned char dat = cyclicshiftLeft(buffer, bufSize);

		if (currSize < size)
		{
			IDATA[currSize] = dat;
			currSize++;
		}
		else
		{
			IDATA = realloc(IDATA, realloc_size[++idx]);
			size = realloc_size[idx];
			IDATA[currSize] = dat;
			currSize++;
		}

		unsigned char next[1];
		res = fread(next, 1, 1, fin);
		if (res < 1)
		{
			fprintf(stderr, "ERRPOR, COULD NOT READ FROM FILE");
			fclose(fin);
			free(IDATA);
			return ERROR_INVALID_DATA;
		}
		buffer[bufSize - 1] = next[0];
		j++;
	}
	fclose(fin);
	int IDATSIZE = currSize;
	unsigned char *actualIDAT = (unsigned char *)malloc(IDATSIZE * sizeof(unsigned char *));
	if (actualIDAT == NULL)
	{
		fprintf(stderr, "ERRPOR, COULD NOT READ FROM FILE");
		free(IDATA);
		return ERROR_MEMORY;
	}
	for (int i = 0; i < IDATSIZE; i++)
	{
		actualIDAT[i] = IDATA[i];
	}
	free(IDATA);
	currSize -= 5;
	uLongf dat = ((colorType + 1) * width * height + height);
	unsigned char *unompressPng = malloc(sizeof(unsigned char) * (dat * 3));
	if (unompressPng == NULL)
	{
		fprintf(stderr, "ERROR: couldnt create array");
		free(actualIDAT);
		return ERROR_MEMORY;
	}

	int er = uncompress(unompressPng, &dat, actualIDAT, IDATSIZE);
	if (er != Z_OK)
	{
		free(actualIDAT);
		free(unompressPng);
		fprintf(stderr, "ERROR, incorrenct uncopress execution");
		return ERROR_UNKNOWN;
	}
	for (int i = 0; i < dat; i++)
	{
		if (i % (3 * width + 1) == 0)
		{
			int a = executeColorFilt(unompressPng, i + 1, i + (3 * width + 1), unompressPng[i], width * 3 + 1);
			if (a == 1)
			{
				fprintf(stderr, "ERROR, INVALID COLOUR FILTER");
				free(actualIDAT);
				free(unompressPng);
				return ERROR_INVALID_DATA;
			}
		}
	}
	FILE *fout = fopen(argc[2], "wb");
	if (fout == NULL)
	{
		fprintf(stderr, "ERROR: couldnt create array");
		free(unompressPng);
		free(actualIDAT);
		return ERROR_MEMORY;
	}
	if (colorType == 0)
	{
		res = fprintf(fout, "P5\n%i %i\n255\n", width, height);
		if (res == 0)
		{
			fprintf(stderr, "ERROR: couldnt write to file");
			free(unompressPng);
			free(actualIDAT);
			fclose(fout);
			return ERROR_INVALID_DATA;
		}
	}
	else
	{
		res = fprintf(fout, "P6\n%i %i\n255\n", width, height);
		if (res == 0)
		{
			fprintf(stderr, "ERROR: couldnt write to file");
			free(unompressPng);
			free(actualIDAT);
			fclose(fout);
			return ERROR_INVALID_DATA;
		}
	}

	for (int i = 0; i < dat; i++)
	{
		if (i % (1 + 3 * width) != 0)
		{
			res = fwrite(&unompressPng[i], 1, 1, fout);
			if (res < 1)
			{
				fprintf(stderr, "ERROR, could not write to file");
				free(actualIDAT);
				free(unompressPng);
				fclose(fout);
				return ERROR_INVALID_DATA;
			}
		}
	}
	fclose(fout);
	free(actualIDAT);
	free(unompressPng);
	return 0;
}
