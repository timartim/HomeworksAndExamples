#include "LN.h"

#include "m_vector.h"

#include <cstdio>
#include <cstring>
bool LN::my_abs_geq(const LN &a, const LN &b) const
{
	if ((a.nan) || (b.nan))
	{
		return false;
	}
	if (a.size > b.size)
	{
		return true;
	}
	if (a.size < b.size)
	{
		return false;
	}
	for (std::size_t i = b.size; i >= 1; i--)
	{
		if (a.number.get(i - 1) > b.number.get(i - 1))
		{
			return true;
		}
		else if (a.number.get(i - 1) < b.number.get(i - 1))
		{
			return false;
		}
	}
	return true;
}
int LN::all_compare(const LN &a, const LN &b) const
{
	if (a.nan || b.nan)
	{
		return -1;
	}
	if (a.sign == b.sign)
	{
		if (!a.sign)
		{
			if (a.size > b.size)
			{
				return 1;
			}
			else if (b.size > a.size)
			{
				return -1;
			}
			else
			{
				for (std::size_t i = a.size; i >= 1; i--)
				{
					if (a.number.get(i - 1) > b.number.get(i - 1))
					{
						return 1;
					}
					else if (a.number.get(i - 1) < b.number.get(i - 1))
					{
						return -1;
					}
				}
				return 0;
			}
		}
		else
		{
			if (a.size > b.size)
			{
				return -1;
			}
			else if (b.size > a.size)
			{
				return 1;
			}
			else
			{
				for (std::size_t i = a.size; i >= 1; i--)
				{
					if (a.number.get(i - 1) > b.number.get(i - 1))
					{
						return -1;
					}
					else if (a.number.get(i - 1) < b.number.get(i - 1))
					{
						return 1;
					}
				}
				return 0;
			}
		}
	}
	else
	{
		if (a.sign)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}

bool abs_eq(LN a, LN b)
{
	LN num1 = a;
	LN num2 = b;
	num1.sign = true;
	num2.sign = true;
	return num1 == num2;
}
bool operator==(const LN &num1, const LN &num2)
{
	if (num1.nan || num2.nan)
	{
		return false;
	}
	if (num1.all_compare(num1, num2) == 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}
bool operator!=(const LN &num1, const LN &num2)
{
	if (num1.nan || num2.nan)
	{
		return true;
	}
	if (num1.all_compare(num1, num2) != 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}
bool operator>(const LN &num1, const LN &num2)
{
	if (num1.nan || num2.nan)
	{
		return false;
	}
	if (num1.all_compare(num1, num2) == 1)
	{
		return true;
	}
	else
	{
		return false;
	}
}
bool operator>=(const LN &num1, const LN &num2)
{
	if (num1.nan || num2.nan)
	{
		return false;
	}
	int res = num1.all_compare(num1, num2);
	if ((res == 0) || (res == 1))
	{
		return true;
	}
	else
	{
		return false;
	}
}
bool operator<(const LN &num1, const LN &num2)
{
	if (num1.nan || num2.nan)
	{
		return false;
	}
	if (num1.all_compare(num1, num2) == -1)
	{
		return true;
	}
	else
	{
		return false;
	}
}
bool operator<=(const LN &num1, const LN &num2)
{
	if (num1.nan || num2.nan)
	{
		return false;
	}
	int res = num1.all_compare(num1, num2);
	if ((res == 0) || (res == -1))
	{
		return true;
	}
	else
	{
		return false;
	}
}

LN operator+(const LN &num1, const LN &num2)
{
	if ((num1.nan) || (num2.nan))
	{
		LN a = LN("0");
		a.nan = true;
		return a;
	}
	m_vector ans;
	if (num1.sign != num2.sign)
	{
		if (num1.my_abs_geq(num1, num2))
		{
			std::size_t itr1 = 0;
			std::size_t itr2 = 0;
			bool next = false;
			while ((itr1 != num1.size) && (itr2 != num2.size))
			{
				int cur_res = ((num1.number.get(itr1) - '0') - (num2.number.get(itr2) - '0') - next);
				if (cur_res >= 0)
				{
					char val = cur_res + '0';
					ans.push(val);
					next = false;
				}
				else
				{
					char val = (10 + cur_res) + '0';
					ans.push(val);
					next = true;
				}
				itr1++;
				itr2++;
			}
			while (itr1 != num1.size)
			{
				int cur_res = ((num1.number.get(itr1) - '0') - next);
				if (cur_res >= 0)
				{
					char val = cur_res + '0';
					ans.push(val);
					next = false;
				}
				else
				{
					char val = (10 + cur_res) + '0';
					ans.push(val);
					next = true;
				}
				itr1++;
			}
		}
		else
		{
			std::size_t itr1 = 0;
			std::size_t itr2 = 0;
			bool next = false;
			while ((itr1 != num1.size) && (itr2 != num2.size))
			{
				int cur_res = ((num2.number.get(itr2) - '0') - (num1.number.get(itr1) - '0') - next);
				if (cur_res >= 0)
				{
					char val = cur_res + '0';
					ans.push(val);
					next = false;
				}
				else
				{
					char val = (10 + cur_res) + '0';
					ans.push(val);
					next = true;
				}
				itr1++;
				itr2++;
			}
			while (itr2 != num2.size)
			{
				int cur_res = ((num2.number.get(itr2) - '0') - next);
				if (cur_res >= 0)
				{
					char val = cur_res + '0';
					ans.push(val);
					next = false;
				}
				else
				{
					char val = (10 + cur_res) + '0';
					ans.push(val);
					next = true;
				}
				itr2++;
			}
		}
		LN result(ans);
		if (ans != LN("0"))
		{
			if (num1.my_abs_geq(num1, num2))
			{
				result.sign = num1.sign;
			}
			else
			{
				result.sign = num2.sign;
			}
		}
		else
		{
			result.sign = false;
		}

		return result;
	}
	else
	{
		unsigned long len1 = std::max(num1.size, num2.size);
		unsigned long len2 = std::min(num1.size, num2.size);
		unsigned long itr1 = 0;
		unsigned long itr2 = 0;
		bool next = false;
		while ((itr1 != len1) && (itr2 != len2))
		{
			int cur_res = ((num1.number.get(itr1) - '0') + (num2.number.get(itr2) - '0') + next) % 10;
			char val = cur_res + '0';
			ans.push(val);

			next = ((num1.number.get(itr1) - '0') + (num2.number.get(itr2) - '0') + next) / 10;
			itr1++;
			itr2++;
		}
		if (num1.size > num2.size)
		{
			while (itr1 != num1.size)
			{
				int cur_res = ((num1.number.get(itr1) - '0') + next) % 10;
				char val = cur_res + '0';
				ans.push(val);

				next = ((num1.number.get(itr1) - '0') + next) / 10;
				itr1++;
			}
			if (next)
			{
				ans.push('1');
			}
		}
		else
		{
			while (itr2 != num2.size)
			{
				int cur_res = ((num2.number.get(itr2) - '0') + next) % 10;
				char val = cur_res + '0';
				ans.push(val);
				next = ((num2.number.get(itr2) - '0') + next) / 10;
				itr2++;
			}
			if (next)
			{
				ans.push('1');
			}
		}
		LN result(ans);
		result.sign = num2.sign;
		return result;
	}
}

LN operator-(const LN &num1, const LN &num2)
{
	if ((num1.nan) || (num2.nan))
	{
		LN a("0");
		a.nan = true;
		return a;
	}
	LN copy_num2 = LN(num2.number);
	copy_num2.sign = !num2.sign;
	copy_num2.size = num2.size;
	LN ans = num1 + copy_num2;
	return ans;
}
LN operator-(const LN &num)
{
	if (num.nan)
	{
		LN a("0");
		a.nan = true;
		return a;
	}

	LN result = num;
	if (num == LN("0"))
	{
		return result;
	}
	else
	{
		result.sign = !num.sign;
	}

	return result;
}
LN LN::times_scalar(const LN &num1, int scal) const
{
	m_vector ans;
	int carry = 0;
	for (size_t j = 0; j < num1.size; j++)
	{
		int cur_res = ((num1.number.get(j) - '0') * scal + carry) % 10;
		char val = cur_res + '0';
		ans.push(val);
		carry = ((num1.number.get(j) - '0') * scal + carry) / 10;
	}
	if (carry != 0)
	{
		char val = carry + '0';
		ans.push(val);
	}
	LN current_ans(ans);
	return current_ans;
}
LN operator*(const LN &num1, const LN &num2)
{
	if ((num1.nan) || (num2.nan))
	{
		LN a("0");
		a.nan = true;
		return a;
	}
	int zeros = 0;

	LN result("0");
	if ((num1 == result) || (num2 == result))
	{
		return result;
	}
	for (std::size_t i = 0; i < num2.size; ++i)
	{
		m_vector ans;
		LN cur = num1.times_scalar(num1, num2.number.get(i) - '0');
		for (std::size_t j = 0; j < zeros; j++)
		{
			ans.push('0');
		}
		for (std::size_t j = 0; j < cur.size; j++)
		{
			ans.push(cur.number.get(j));
		}

		result = result + ans;
		zeros++;
	}
	result.sign = (num1.sign != num2.sign);
	return result;
}

LN operator/(const LN &num1, const LN &num2)
{
	if ((num1.nan) || (num2.nan))
	{
		LN a("0");
		a.nan = true;
		return a;
	}
	LN result("0");
	if (num2 == LN("0"))
	{
		result.nan = true;
		return result;
	}
	if (num1 == LN("0"))
	{
		return result;
	}
	if (num1.my_abs_geq(num2, num1) && (!abs_eq(num1, num2)))
	{
		return result;
	}
	else
	{
		m_vector ans;
		ans.create(num1.size - num2.size + 1);
		result.number = ans;
		result.size = ans.getSize();
		result.sign = false;
		for (std::size_t i = ans.getSize(); i >= 1; i--)
		{
			int curnum = 0;
			while (num1.my_abs_geq(num1, num2 * result))
			{
				curnum++;
				result.number.arr[i - 1] = curnum + '0';
			}
			curnum--;
			result.number.arr[i - 1] = curnum + '0';
		}
		LN a(result.number);
		a.sign = (num1.sign != num2.sign);
		return a;
	}
}
LN operator%(const LN &num1, const LN &num2)
{
	if ((num1.nan) || (num2.nan))
	{
		LN a("0");
		a.nan = true;
		return a;
	}
	LN num3 = num1 / num2;
	LN result = num1 - (num3 * num2);
	if (result == LN("0"))
	{
		result.sign = false;
	}
	return result;
}

LN operator~(const LN &num)
{
	if (num.nan)
	{
		LN a("0");
		a.nan = true;
		return a;
	}
	LN result("0");
	if (num < LN("0"))
	{
		result.nan = true;
		return result;
	}
	m_vector ans;
	ans.create(num.size / 2 + 1);
	result.number = ans;
	result.size = ans.getSize();
	result.sign = false;
	for (std::size_t i = ans.getSize(); i >= 1; i--)
	{
		int curnum = 0;
		while (result * result <= num)
		{
			curnum++;
			result.number.arr[i - 1] = curnum + '0';
		}
		curnum--;
		result.number.put(i - 1, curnum + '0');
	}
	LN a(result.number);
	return a;
}
LN operator+=(const LN &num1, const LN &num2)
{
	LN c = num1 + num2;
	return c;
}

LN operator-=(const LN &num1, const LN &num2)
{
	LN c = num1 - num2;
	return c;
}
LN operator*=(const LN &num1, const LN &num2)
{
	LN c = num1 * num2;
	return c;
}
LN operator/=(const LN &num1, const LN &num2)
{
	LN c = num1 / num2;
	return c;
}
LN operator"" _ln(const char *num)
{
	LN result(num);
	return result;
}
void LN::toString(char *ans)
{
	std::size_t itr1 = 0;
	for (size_t i = size; i >= 1; i--)
	{
		if (itr1 < size)
		{
			ans[itr1] = number.get(i - 1);
			itr1++;
		}
		else
		{
			break;
		}
	}
}
LN::LN(long long int val)
{
	number = m_vector();
	if (val == 0)
	{
		number.push('0');
	}
	else
	{
		if (val > 0)
		{
			long long x = val;
			int dig = 0;
			while (x > 0)
			{
				dig = x % 10;
				number.push(dig + '0');
				x = x / 10;
			}
		}
		else
		{
			long long x = val * -1;
			int dig = 0;
			while (x > 0)
			{
				dig = x % 10;
				number.push(dig + '0');
				x = x / 10;
			}
			sign = true;
		}
	}
	size = number.getSize();
}
LN::LN(m_vector vec)
{
	size = 0;
	number = m_vector();
	for (std::size_t itr = vec.getSize(); itr >= 1; itr--)
	{
		std::size_t i = itr - 1;
		if (vec.get(i) == '0')
		{
			continue;
		}
		else
		{
			for (std::size_t j = 0; j <= i; j++)
			{
				size++;
				number.push(vec.get(j));
			}
			break;
		}
	}
	if (size == 0)
	{
		number.push('0');
		size++;
	}
}
LN::LN(const char *ch)
{
	m_vector acc;
	bool meetSign = false;
	unsigned long itr = 0;
	while ((ch[itr] == '0') || (ch[itr] == '-'))
	{
		if (ch[itr] == '-')
		{
			meetSign = true;
		}
		itr++;
	}
	if (itr == std::strlen(ch))
	{
		number.push('0');
		size = 1;
		sign = false;
	}
	else
	{
		for (size_t i = itr; i < std::strlen(ch); i++)
		{
			if (ch[i] != '-')
			{
				acc.push(ch[i]);
			}
			else
			{
				meetSign = true;
			}
		}
		for (std::size_t i = acc.getSize(); i >= 1; i--)
		{
			number.push(acc.get(i - 1));
		}
		sign = meetSign;
		size = number.getSize();
	}
}
LN::LN(std::string_view &str)
{
	m_vector acc;
	bool meetSign = false;
	unsigned long itr = 0;
	while ((str[itr] == '0') || (str[itr] == '-'))
	{
		if (str[itr] == '-')
		{
			meetSign = true;
		}
		itr++;
	}
	if (itr == str.size())
	{
		number.push('0');
		size = 1;
		sign = false;
	}
	else
	{
		for (std::size_t i = itr; i < str.size(); i++)
		{
			if (str[i] != '-')
			{
				acc.push(str[i]);
			}
			else
			{
				meetSign = true;
			}
		}
		for (std::size_t i = acc.getSize(); i >= 1; i--)
		{
			number.push(acc.get(i - 1));
		}
		sign = meetSign;
		size = number.getSize();
	}
}

LN::LN(LN &&other)	  // перемещение
{
	number = other.number;
	sign = other.sign;
	nan = other.nan;
	size = other.size;
	other.number.arr = nullptr;
}
LN::LN(const LN &other)
{
	number = other.number;
	sign = other.sign;
	nan = other.nan;
	size = other.size;
}
LN &LN::operator=(const LN &other)
{
	if (&other != this)
	{
		number = other.number;
		sign = other.sign;
		nan = other.nan;
		size = other.size;
	}
	return *this;
}

LN::operator long long()
{
	LN a(9223372036854775807);
	LN b(this->number);	   // counstructor creates an abs LN, does not takes sign of value
	if (this->sign)
	{
		if (b >= a + LN("1"))
		{
			throw std::overflow_error("to big number to cast to long long");
		}
		else
		{
			long long ans = 0;
			int k = 1;
			for (size_t i = 0; i < this->size; i++)
			{
				ans += (number.get(i) - '0') * k;
				k *= 10;
			}
			return ans;
		}
	}
	else
	{
		if (b >= a)
		{
			throw std::overflow_error("to big number to cast to long long");
		}
		else
		{
			long long ans = 0;
			int k = 1;
			for (size_t i = 0; i < this->size; i++)
			{
				ans += (number.get(i) - '0') * k;
				k *= 10;
			}
			return ans;
		}
	}
}
LN &LN::operator=(LN &&num) noexcept
{
	if(this == &num){
		return *this;
	}
	number = num.number;
	sign = num.sign;
	nan = num.nan;
	size = num.size;
	num.number.arr = nullptr;
	return *this;
}
bool LN::getNan()
{
	return nan;
}
bool LN::getSign()
{
	return sign;
}
LN::LN(char ch, bool n)
{
	number = m_vector();
	sign = false;
	nan = n;
	number.push('0');
}
m_vector LN::getNumber()
{
	return number;
}
unsigned long LN::getSize()
{
	return size;
}
std::ostream &operator<<(std::ostream &of, const LN &num2)
{
	for (std::size_t i = num2.size; i >= 1; i--)
	{
		of << num2.number.get(i - 1);
	}
	return of;
}

LN::~LN() = default;
