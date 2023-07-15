
#pragma once

#include <cstdio>
class m_vector
{
  public:
	unsigned long size;
	unsigned long actual_size;
	char *arr;
	m_vector();
	m_vector(int size);
	void push(char val);
	char get(size_t idx) const;
	unsigned long getSize();
	void create(unsigned long size);
	void add(m_vector vec);
	m_vector(const m_vector &num);
	m_vector(m_vector &&num) noexcept;
	~m_vector();
	void put(size_t pos, char val);
	m_vector &operator=(const m_vector &num);
	m_vector &operator=(m_vector &&num) noexcept;
};