//
// Created by Артемий on 22.04.2022.
//

#ifndef LAB3_PHONEBOOK_H
#define LAB3_PHONEBOOK_H
struct phonebook
{
	std::string names[3];
	long long number;
};
int compare_string(std::string s1, std::string s2)
{
	for (int i = 0; i < std::min(s1.length(), s2.length()); i++)
	{
		if (s1[i] < s2[i])
		{
			return -1;
		}
		else if (s1[i] > s2[i])
		{
			return 1;
		}
		else
		{
			if (s1[i] == s2[i])
			{
				continue;
			}
		}
	}
	if (s1.length() > s2.length())
	{
		return 1;
	}
	else if (s2.length() > s1.length())
	{
		return -1;
	}
	return 0;
}
int compare_phonebook(const phonebook& p1, const phonebook& p2, int idx)
{
	for (int i = 0; i < 3; i++)
	{
		int res = compare_string(p1.names[i], p2.names[i]);
		if ((res == 1) || (res == -1))
		{
			return res;
		}
	}
	if (p1.number > p2.number)
	{
		return 1;
	}
	if (p1.number < p2.number)
	{
		return 1;
	}
	return 0;
}
bool operator>(const phonebook& p1, const phonebook& p2)
{
	if (compare_phonebook(p1, p2, 0) == 1)
	{
		return true;
	}
	return false;
}
bool operator<(const phonebook& p1, const phonebook& p2)
{
	if (compare_phonebook(p1, p2, 0) == -1)
	{
		return true;
	}
	return false;
}
bool operator==(const phonebook& p1, const phonebook& p2)
{
	if (compare_phonebook(p1, p2, 0) == 0)
	{
		return true;
	}
	return false;
}
bool operator>=(const phonebook& p1, const phonebook& p2)
{
	if ((compare_phonebook(p1, p2, 0) == 0) || (compare_phonebook(p1, p2, 0) == 1))
	{
		return true;
	}
	return false;
}
bool operator<=(const phonebook& p1, const phonebook& p2)
{
	if ((compare_phonebook(p1, p2, 0) == 0) || (compare_phonebook(p1, p2, 0) == -1))
	{
		return true;
	}
	return false;
}
#endif	  // LAB3_PHONEBOOK_H
