#include "LN.h"
#include "return_codes.h"
#include "stack"
#include <cstdio>
#include <fstream>
#include <stack>
using std::ifstream;
using std::ofstream;
using std::stack;
using std::string_view;
int main(int argc, char** argv)
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
		return ERROR_FILE_NOT_FOUND;
	}
	stack< LN > st;
	try
	{
		while (!fin.eof())
		{
			std::string val;
			std::string op;
			std::string num;
			std::string actual;
			fin >> val;
			if (val.empty())
			{
				break;
			}
			actual = val;
			if (actual.length() == 1)
			{
				LN a("0");
				LN b("0");
				bool v = false;
				int n = 0;
				switch (val[0])
				{
				case '-':
					a = st.top();
					st.pop();
					b = st.top() - a;
					st.pop();
					st.push(b);
					break;
				case '+':
					a = st.top();
					st.pop();
					b = a + st.top();
					st.pop();
					st.push(b);
					break;
				case '*':
					a = st.top();
					st.pop();
					b = a * st.top();
					st.pop();
					st.push(b);
					break;
				case '/':
					a = st.top();
					st.pop();
					b = st.top() / a;
					st.pop();
					st.push(b);
					break;
				case '~':
					a = st.top();
					b = ~a;
					st.pop();
					st.push(b);
					break;
				case '_':
					a = st.top();
					b = -a;
					st.pop();
					st.push(b);
					break;
				case '=':
					a = st.top();
					b = a;
					st.pop();
					st.pop();
					st.push(b);
					break;
				case '>':
					a = st.top();
					st.pop();
					v = st.top() > a;
					n = v;
					b = LN(n);
					st.pop();
					st.push(b);
					break;
				case '<':
					a = st.top();
					st.pop();
					v = st.top() < a;
					n = v;
					b = LN(n);
					st.pop();
					st.push(b);
					break;
				case '%':
					a = st.top();
					st.pop();
					b = st.top() % a;
					st.pop();
					st.push(b);
					break;
				default:
					string_view str = val;
					LN c(str);
					st.push(c);
				}
			}
			else if (actual.length() == 2)
			{
				LN a("0");
				LN b("0");
				bool v = false;
				int n = 0;
				if (actual == "+=")
				{
					a = st.top();
					st.pop();
					b = a + st.top();
					st.pop();
					st.push(b);
				}
				else if (actual == "-=")
				{
					a = st.top();
					st.pop();
					b = a - st.top();
					st.pop();
					st.push(b);
				}
				else if (actual == "/=")
				{
					a = st.top();
					st.pop();
					b = a / st.top();
					st.pop();
					st.push(b);
				}
				else if (actual == "*=")
				{
					a = st.top();
					st.pop();
					b = a * st.top();
					st.pop();
					st.push(b);
				}
				else if (actual == ">=")
				{
					a = st.top();
					st.pop();
					v = st.top() >= a;
					n = v;
					b = LN(n);
					st.pop();
					st.push(b);
				}
				else if (actual == "<=")
				{
					a = st.top();
					st.pop();
					v = st.top() <= a;
					n = v;
					b = LN(n);
					st.pop();
					st.push(b);
				}
				else if (actual == "==")
				{
					a = st.top();
					st.pop();
					v = a == st.top();
					n = v;
					b = LN(n);
					st.pop();
					st.push(b);
				}
				else if (actual == "!=")
				{
					a = st.top();
					st.pop();
					v = a != st.top();
					n = v;
					b = LN(n);
					st.pop();
					st.push(b);
				}
				else
				{
					std::string_view str = actual;
					LN c(str);
					st.push(c);
				}
			}
			else
			{
				if (actual == "NaN")
				{
					LN c('0', true);
					st.push(c);
				}
				else
				{
					std::string_view str = actual;
					LN c(str);
					st.push(c);
				}
			}
		}
	} catch (std::bad_alloc& e)
	{
		fprintf(stderr, "Could not allocate memory");
		fin.close();
		return ERROR_MEMORY;
	} catch (std::overflow_error& e)
	{
		fprintf(stderr, "ERROR: Trying to convert to long long to big number, bad idea");
		fin.close();
		return ERROR_INVALID_DATA;
	} catch(...){

	}

	fin.close();
	ofstream fout;
	fout.open(argv[2]);
	unsigned long size = st.size();
	for (std::size_t i = 0; i < size; i++)
	{
		if (st.top().getNan())
		{
			fout << "NaN"
				 << "\n";
		}
		else
		{
			if (st.top().getSign())
			{
				fout << "-";
			}

			fout << st.top() << '\n';
		}

		st.pop();
	}
	fout.close();
	return 0;
}
