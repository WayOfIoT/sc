#ifndef _LED_H_
#define _LED_H_

// Һ������IO��
sbit LED_SCL = P0^3; 
sbit LED_SDA = P0^4;
sbit LED_RST = P0^5; 
sbit LED_DC  = P0^6;

// ����
extern unsigned char led_refresh_flag;

/*-----------------------------------------------
  TODO��OLED��ʼ��
  ������
  ��д���Ľ�
  ���ڣ�2017.4.22
------------------------------------------------*/
void LED_init();

/*-----------------------------------------------
  TODO����OLEDдӢ���ַ���
  ������ucIdxX ��ʼ��λ��
  		ucIdxY ��ʵ��λ��
		ucDataStr �ַ���ͷָ��
  ��д���Ľ�
  ���ڣ�2017.4.22
------------------------------------------------*/
void led_wirte_str(unsigned char ucIdxX, unsigned char ucIdxY, unsigned char ucDataStr[]);

/*****************************************************************************
 �� �� ��  : led_write_num
 ��������  : ��һ��Char����ת����3λ��������ʾ
 �������  : uchar ucIdxX    ucIdxX�ķ�ΧΪ0��122
             uchar ucIdxY    ucIdxY�ķ�ΧΪ0��7
             uchar cData      cDataΪ��Ҫת����ʾ����ֵ -128~127
 �������  : none
 �� �� ֵ  : none
*****************************************************************************/
void led_write_num(unsigned char ucIdxX, unsigned char ucIdxY, char cData);





#endif