#ifndef _TIMER_H_
#define _TIMER_H_

// ��ʱ��
#define TIMER_FULL_T 65536

// ��ʱ��0��ʱ����:100us 
// (12M:timer_time = 100; 11.0582M:timer_time = 92;)
#define TIMER0_T 90

// ��ʱ��1��ʱ����:200us 
// (12M:timer_time = 200; 11.0582M:timer_time = 184;)
#define TIMER1_T 180


/*-----------------------------------------------
  TODO����ʱ����ʼ��
  ������
  ��д���Ľ�
  ���ڣ�2017.4.10
------------------------------------------------*/
void timer_init();








#endif





