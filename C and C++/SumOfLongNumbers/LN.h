
#pragma once
#include "m_vector.h"
#include <string_view>

#include <cstdio>
#include <iostream>
class LN
{
  private:
	m_vector number;
	long long val = 0;
	unsigned long size = 0;
	bool sign = false;
	bool nan = false;

  public:
	LN(long long val = 0);
	LN(const char *ch);
	LN(std::string_view &str);
	LN(m_vector vec);
	LN(char ch, bool nan);
	~LN();
	LN(const LN &num);
	LN(LN &&num);
	LN &operator=(const LN &num);
	LN &operator=(LN &&num) noexcept;
	friend LN operator+(const LN &num1, const LN &num2);
	friend LN operator-(const LN &num1, const LN &num2);
	friend LN operator*(const LN &num1, const LN &num2);
	friend LN operator/(const LN &num1, const LN &num2);
	friend LN operator+=(const LN &num1, const LN &num2);
	friend LN operator-=(const LN &num1, const LN &num2);
	friend LN operator*=(const LN &num1, const LN &num2);
	friend LN operator/=(const LN &num1, const LN &num2);
	friend LN operator%(const LN &num1, const LN &num2);
	friend LN operator~(const LN &num1);
	friend LN operator-(const LN &num1);
	friend bool operator>(const LN &num1, const LN &num2);
	friend bool operator>=(const LN &num1, const LN &num2);
	friend bool operator==(const LN &num1, const LN &num2);
	friend bool operator!=(const LN &num1, const LN &num2);
	friend bool operator<(const LN &num1, const LN &num2);
	friend bool operator<=(const LN &num1, const LN &num2);
	friend std::ostream &operator<<(std::ostream &of, const LN &num2);
	explicit operator long long();
	friend bool abs_eq(LN a, LN b);
	bool my_abs_geq(const LN &a, const LN &b) const;
	int all_compare(const LN &a, const LN &b) const;
	LN times_scalar(const LN &num1, int scal) const;
	void toString(char *ans);
	bool getNan();
	bool getSign();
	unsigned long getSize();
	m_vector getNumber();
};
LN operator"" _ln(const char *num);
