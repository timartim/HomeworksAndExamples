#include "m_vector.h"

#include <cmath>
#include <cstdio>
void m_vector::push(char val)
{
	if (size >= actual_size)
	{
		char* arrv = new char[lround(actual_size * 2) + 1]();
		for (std::size_t i = 0; i < size; ++i)
		{
			arrv[i] = arr[i];
		}
		delete[] arr;
		arr = arrv;
		actual_size *= 2;
	}
	arr[size - 1] = val;
	size++;
}
char m_vector::get(std::size_t idx) const
{
	return arr[idx];
}
unsigned long m_vector::getSize()
{
	return size - 1;
}
void m_vector::create(unsigned long capacity)
{
	char* arrv = new char[capacity + 1]();
	for (size_t i = 0; i < capacity; i++)
	{
		arrv[i] = '0';
	}
	delete[] arr;
	arr = arrv;
	size = capacity + 1;
	actual_size = capacity + 1;
}
void m_vector::put(std::size_t pos, char val)
{
	this->arr[pos] = val;
}
void m_vector::add(m_vector vec)
{
	for (int i = 0; i < vec.getSize(); i++)
	{
		this->push(vec.get(i));
	}
}
m_vector::m_vector()
{
	size = 1;
	actual_size = 2;
	arr = new char[2];
	arr[0] = '0';
	arr[1] = '0';
}
m_vector::m_vector(int size)
{
	size = size;
	actual_size = size;
	arr = new char[size];
	for (int i = 0; i < size; i++)
	{
		arr[i] = '0';
	}
}
m_vector& m_vector::operator=(const m_vector& num)
{
	size = num.size;
	actual_size = num.actual_size;
	delete[] arr;
	arr = new char[num.actual_size];
	for (int i = 0; i < num.size; i++)
	{
		arr[i] = num.arr[i];
	}
	return *this;
}

m_vector::m_vector(m_vector&& num) noexcept
{
	size = num.size;
	actual_size = num.actual_size;
	arr = num.arr;
	num.arr = nullptr;
}
m_vector::m_vector(const m_vector& num)
{
	size = num.size;
	actual_size = num.actual_size;
	arr = new char[num.actual_size];
	for (int i = 0; i < num.size - 1; i++)
	{
		arr[i] = num.arr[i];
	}
}
m_vector::~m_vector()
{
	delete[] arr;
	arr = nullptr;
}
m_vector& m_vector::operator=(m_vector&& num) noexcept
{
	delete[] arr;
	arr = num.arr;
	size = num.size;
	actual_size = num.actual_size;
	num.arr = nullptr;
	return *this;
}
