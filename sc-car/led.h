#ifndef _LED_H_
#define _LED_H_

// 液晶驱动IO口
sbit LED_SCL = P0^3; 
sbit LED_SDA = P0^4;
sbit LED_RST = P0^5; 
sbit LED_DC  = P0^6;

// 变量
extern unsigned char led_refresh_flag;

/*-----------------------------------------------
  TODO：OLED初始化
  参数：
  编写：夏杰
  日期：2017.4.22
------------------------------------------------*/
void LED_init();

/*-----------------------------------------------
  TODO：向OLED写英文字符串
  参数：ucIdxX 起始行位置
  		ucIdxY 其实列位置
		ucDataStr 字符串头指针
  编写：夏杰
  日期：2017.4.22
------------------------------------------------*/
void led_wirte_str(unsigned char ucIdxX, unsigned char ucIdxY, unsigned char ucDataStr[]);

/*****************************************************************************
 函 数 名  : led_write_num
 功能描述  : 将一个Char型数转换成3位数进行显示
 输入参数  : uchar ucIdxX    ucIdxX的范围为0～122
             uchar ucIdxY    ucIdxY的范围为0～7
             uchar cData      cData为需要转化显示的数值 -128~127
 输出参数  : none
 返 回 值  : none
*****************************************************************************/
void led_write_num(unsigned char ucIdxX, unsigned char ucIdxY, char cData);





#endif