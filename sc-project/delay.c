#include "delay.h"
#include <intrins.h>

//delay for 2*t us
void DelayUs2x(unsigned char t)
{
	while(--t);
}

//delay for t mS
void DelayMs(unsigned char t)
{
	while(t--)
	{
		DelayUs2x(245);
		DelayUs2x(245);
	}
}